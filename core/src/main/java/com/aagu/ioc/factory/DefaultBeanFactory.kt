package com.aagu.ioc.factory

import com.aagu.ioc.bean.*
import com.aagu.ioc.exception.BeanDefinitionNotFoundException
import com.aagu.ioc.exception.BeanNotFoundException
import com.aagu.ioc.exception.DuplicateBeanDefinitionException
import com.aagu.ioc.exception.IllegalBeanDefinitionException
import com.aagu.ioc.util.StringUtils
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.Collections.synchronizedList
import java.util.concurrent.ConcurrentHashMap

abstract class DefaultBeanFactory: BeanFactory, BeanDefinitionRegistry {
    protected val beanDefinitionMap = ConcurrentHashMap<String, BeanDefinition>(256)
    private val beanMap = ConcurrentHashMap<String, Any>(256)
    private val classMap = ConcurrentHashMap<Class<*>, String>(256)
    private val buildingBeans = ThreadLocal<HashSet<String>>().apply {
        set(HashSet())
    }
    private val beanPostProcessors = synchronizedList(ArrayList<BeanPostProcessor>())
    private val factoryPostProcessor = synchronizedList(ArrayList<FactoryPostProcessor>())

    override fun <T> getBean(name: String): T {
        return doGetBean(name)
    }

    override fun <T> getBean(clazz: Class<T>): T {
        val name = classMap[clazz]
        if (StringUtils.isEmpty(name)) throw BeanNotFoundException(name)
        else {
            return getBean(name!!)
        }
    }

    override fun registerBeanPostProcessor(processor: BeanPostProcessor) {
        beanPostProcessors.add(processor)
    }

    override fun registerFactoryPostProcessor(processor: FactoryPostProcessor) {
        factoryPostProcessor.add(processor)
        processor.setBeanFactory(this)
    }

    override fun registerBeanDefinition(name: String, definition: BeanDefinition) {
        if (!definition.validate()) {
            throw IllegalBeanDefinitionException("名为 $name 的bean定义不合法 $definition")
        }
        if (this.containsBeanDefinition(name)) {
            throw DuplicateBeanDefinitionException("容器中已经存在名为 $name 的bean定义 ${this.getBeanDefinition(name)}")
        }
        beanDefinitionMap[name] = definition
        if (definition.getBeanClass() != null) {
            // 对于@Config定义的bean，只能通过beanName访问
            classMap[definition.getBeanClass()!!] = name
        }
    }

    override fun getBeanDefinition(name: String): BeanDefinition {
        return beanDefinitionMap[name] ?:
            (return beanDefinitionMap[StringUtils.lowerCaseFirstChar(name)] ?:
                throw BeanDefinitionNotFoundException("找不到名为 $name 的bean定义"))
    }

    override fun containsBeanDefinition(name: String): Boolean {
        return beanDefinitionMap.contains(name)
    }

    override fun close() {
        for (entry in beanDefinitionMap.entries) {
            val beanName = entry.key
            val beanDefinition = entry.value

            if (beanDefinition.isSingleton() && StringUtils.isNotEmpty(beanDefinition.getDestroyMethodName())) {
                val instance = beanMap[beanName] ?: continue
                try {
                    val method = instance.javaClass.getMethod(beanDefinition.getDestroyMethodName())
                    method.invoke(instance)
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: SecurityException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                } finally {
                    beanMap.remove(beanName)
                }
            }
        }
    }

    override fun getRegisteredBeanNames(): List<String> {
        return beanDefinitionMap.keys().toList()
    }

    abstract fun init()

    open fun finalizeInit() {
        for (processor in factoryPostProcessor) {
            processor.process(beanDefinitionMap)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> doGetBean(name: String): T {
        val beanUnderBuilding: HashSet<String> = buildingBeans.get()
        if (beanUnderBuilding.contains(name)) throw RuntimeException("循环依赖 $name")

        beanUnderBuilding.add(name)
        var instance = beanMap[name]
        if (instance != null) {
            beanUnderBuilding.remove(name)
            return instance as T
        }

        val definition = getBeanDefinition(name)
        val type = definition.getBeanClass()
        instance = if (type != null) {
            if (StringUtils.isEmpty(definition.getFactoryBeanName())) {
                createInstanceByConstructor(definition)
            } else {
                createInstanceByStaticFactoryMethod(definition)
            }
        } else {
            createInstanceByFactoryBean(definition)
        }

        beanUnderBuilding.remove(name)

        setPropertyDIValues(definition, instance)

        applyBeanNameAware(definition, instance, name)

        applyBeanFactoryAware(definition, instance)

        instance = applyPostProcessorBeforeInitialization(instance, name)

        doInit(definition, instance)

        instance = applyPostProcessorAfterInitialization(instance, name)

        if (definition.isSingleton()) {
            beanMap[name] = instance
        }

        return instance as T
    }

    private fun setPropertyDIValues(definition: BeanDefinition, instance: Any) {
        if (definition.getPropertyValues().isNullOrEmpty()) return
        for (pv in definition.getPropertyValues()!!) {
            val clazz = instance.javaClass
            val field = clazz.getDeclaredField(pv.name)
            field.isAccessible = true
            val origValue = pv.value
            var value: Any?
            value = when (origValue) {
                is BeanReference -> doGetBean(origValue.getBeanName())
                else -> origValue
            }
            field.set(instance, value)
        }
    }

    private fun doInit(definition: BeanDefinition, instance: Any) {
        if (StringUtils.isNotEmpty(definition.getInitMethodName())) {
            val method = instance.javaClass.getMethod(definition.getInitMethodName())
            method.invoke(instance)
        }
    }

    private fun createInstanceByFactoryBean(definition: BeanDefinition): Any {
        val factory = doGetBean<Any>(definition.getFactoryBeanName()!!)
        val args = getRealValues(definition.getConstructorArgumentValues())
        val method = determineFactoryMethod(definition, args, factory.javaClass)
        return method.invoke(factory, *args)
    }

    private fun createInstanceByStaticFactoryMethod(definition: BeanDefinition): Any {
        val clazz = definition.getBeanClass()!!
        val args = getRealValues(definition.getConstructorArgumentValues())
        val method = determineFactoryMethod(definition, args, null)
        return method.invoke(clazz, *args)
    }

    private fun createInstanceByConstructor(definition: BeanDefinition): Any {
        try {
            val args = getConstructorArgumentValues(definition)
            return if (args.isEmpty()) definition.getBeanClass()!!.newInstance()
            else determineConstructor(definition, args).newInstance(*args)
        } catch (e: InstantiationException) {
            println("创建 ${definition.getBeanClass()} 实例异常 ${e.message}")
            throw e
        }
    }

    private fun getConstructorArgumentValues(definition: BeanDefinition): Array<*> {
        return getRealValues(definition.getConstructorArgumentValues())
    }

    private fun getRealValues(defs: Array<*>?): Array<*> {
        if (defs.isNullOrEmpty()) return arrayOfNulls<Any>(0)
        val actualValues = arrayOfNulls<Any>(defs.size)
        var value: Any?
        for (i in defs.indices) {
            value = when {
                defs[i] == null -> null
                defs[i] is BeanReference -> doGetBean((defs[i] as BeanReference).getBeanName())
                else -> defs[i]
            }
            actualValues[i] = value
        }
        return actualValues
    }

    private fun determineConstructor(definition: BeanDefinition, args: Array<*>?): Constructor<*> {
        var constructor: Constructor<*>?
        // 无参构造函数
        if (args == null) return definition.getBeanClass()!!.getConstructor()

        // 尝试从definition获取
        constructor = definition.getConstructor()
        if (constructor != null) return constructor

        // 根据参数类型获取
        val paramTypes = arrayOfNulls<Class<*>>(args.size)
        var j = 0
        for (p in args) {
            paramTypes[j++] = p!!.javaClass
        }
        try {
            constructor = definition.getBeanClass()!!.getConstructor(*paramTypes)
        } catch (e: Exception) {
            // 这个异常不需要处理
        }

        if (constructor == null) {
            val constructors = definition.getBeanClass()!!.constructors
            outer@ for (constr in constructors) {
                val pTypes = constr.parameterTypes
                if (pTypes.size == args.size) {
                    for (i in pTypes.indices) {
                        if (pTypes[i].typeName == args[i]!!.javaClass.typeName) continue@outer
                    }
                    constructor = constr
                    break@outer
                }
            }
        }

        if (constructor != null) {
            definition.setConstructor(constructor)
            return constructor
        } else {
            throw NoSuchMethodException("在 ${definition.getBeanClass()} 中找不到入参为 $args 的构造函数")
        }
    }

    private fun determineFactoryMethod(definition: BeanDefinition, args: Array<*>?, type: Class<*>?): Method {
        var localType = type
        if (localType == null) {
            localType = definition.getBeanClass()
        }
        val methodName = definition.getFactoryMethodName()!!
        if (args == null) {
            return localType!!.getMethod(methodName)
        }
        var m: Method?
        // 对于原型bean,从第二次开始获取bean实例时，可直接获得第一次缓存的构造方法。
        m = definition.getFactoryMethod()
        if (m != null) {
            return m
        }
        // 根据参数类型获取精确匹配的方法
        val paramTypes = arrayOfNulls<Class<*>>(args.size)
        var j = 0
        for (p in args) {
            paramTypes[j++] = p!!.javaClass
        }
        try {
            m = localType!!.getMethod(methodName, *paramTypes)
        } catch (e: Exception) {
            // 这个异常不需要处理
        }

        if (m == null) {
            // 没有精确参数类型匹配的，则遍历匹配所有的方法
            // 判断逻辑：先判断参数数量，再依次比对形参类型与实参类型
            outer@ for (m0 in localType!!.methods) {
                if (m0.name != methodName) {
                    continue
                }
                val parameterTypes = m!!.parameterTypes
                if (parameterTypes.size == args.size) {
                    for (i in parameterTypes.indices) {
                        if (!parameterTypes[i].isAssignableFrom(args[i]!!.javaClass)) {
                            continue@outer
                        }
                    }
                    m = m0
                    break@outer
                }
            }
        }
        if (m != null) {
            // 对于原型bean,可以缓存找到的方法，方便下次构造实例对象。在BeanDefinition中获取设置所用方法的方法。
            if (definition.isPrototype()) {
                definition.setFactoryMethod(m)
            }
            return m
        } else {
            throw Exception("不存在对应的构造方法！$definition")
        }
    }

    private fun applyPostProcessorBeforeInitialization(instance: Any, beanName: String): Any {
        var bean = instance
        for (bpp in beanPostProcessors) {
            bean = bpp.postProcessBeforeInitialization(beanName, bean)
        }
        return bean
    }

    private fun applyPostProcessorAfterInitialization(instance: Any, beanName: String): Any {
        var bean = instance
        for (bpp in beanPostProcessors) {
            bean = bpp.postProcessAfterInitialization(beanName, bean)
        }
        return bean
    }

    private fun applyBeanNameAware(definition: BeanDefinition, instance: Any, beanName: String) {
        val beanClass = definition.getBeanClass()
        if (beanClass != null && BeanNameAware::class.java.isAssignableFrom(beanClass)) {
            (instance as BeanNameAware).setBeanName(beanName)
        }
    }

    private fun applyBeanFactoryAware(definition: BeanDefinition, instance: Any) {
        val beanClass = definition.getBeanClass()
        if (beanClass != null && BeanFactoryAware::class.java.isAssignableFrom(beanClass)) {
            (instance as BeanFactoryAware).setBeanFactory(this)
        }
    }
}