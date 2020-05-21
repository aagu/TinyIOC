package com.aagu.ioc.util

import com.aagu.ioc.annotation.*
import com.aagu.ioc.bean.*
import com.aagu.ioc.exception.IllegalBeanDefinitionException
import com.aagu.ioc.exception.PropertyNotFoundException
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

object BeanUtils {
    fun registerBeanDefinition(clazz: Class<*>, registry: BeanDefinitionRegistry) {
        val atBean = clazz.getAnnotation(Bean::class.java)
        val beanName = processBeanNameByClass(atBean, clazz)
        val beanDef = WireableBeanDefinition()
        beanDef.setBeanClass(clazz)
        processScope(atBean, beanDef)
        processInitAndDestroy(clazz, beanDef)
        processProperties(clazz, beanDef)
        takeCareConstructor(clazz, beanName, beanDef)
        registry.registerBeanDefinition(beanName, beanDef)
    }

    fun isAssignableFrom(target: Class<*>, source: Class<*>): Boolean {
        if (!target.isInterface) {
            return target.isAssignableFrom(source)
        } else {
            val interfaces = source.interfaces
            for (interf in interfaces) {
                if (interf == target) return true
            }
            return target == source
        }
    }

    fun registerConfig(clazz: Class<*>, registry: BeanDefinitionRegistry) {
        // 首先把Config注册成bean
        val configName = clazz.simpleName
        val configBeanDef = GeneralBeanDefinition()
        configBeanDef.setBeanClass(clazz)
        takeCareConstructor(clazz, configName, configBeanDef)
        registry.registerBeanDefinition(configName, configBeanDef)

        val beanDeclareMethod = clazz.methods.filter { method -> method.isAnnotationPresent(Bean::class.java) }
        for (method in beanDeclareMethod) {
            val atBean = method.getAnnotation(Bean::class.java)
            val beanName = processBeanNameByMethod(atBean, method)
            val beanClazz = method.returnType
            val beanDef = GeneralBeanDefinition()
            processScope(atBean, beanDef)
            beanDef.setFactoryBeanName(configName)
            beanDef.setFactoryMethodName(method.name)
            processInitAndDestroy(beanClazz, beanDef)
            processProperties(beanClazz, beanDef)
            registry.registerBeanDefinition(beanName, beanDef)
        }
    }

    fun processBeanNameByClass(atBean: Bean, clazz: Class<*>): String {
        var beanName = atBean.beanName
        if (StringUtils.isEmpty(beanName)) {
            beanName = clazz.simpleName
        }
        return StringUtils.lowerCaseFirstChar(beanName)
    }

    fun getConstructorArgumentValues(definition: BeanDefinition): Array<*> {
        val args = definition.getConstructorArguments()
        if (args.isNullOrEmpty()) return arrayOfNulls<Any>(0)
        val processed = arrayOfNulls<Any>(args.size)
        for ((idx, arg) in args.withIndex()) {
            if (arg is Class<*>) {
                if (arg.isAnnotationPresent(Bean::class.java)) {
                    processed[idx] =
                        BeanReference(processBeanNameByClass(arg.getAnnotation(Bean::class.java), arg))
                } else {
                    processed[idx] =
                        BeanReference(StringUtils.lowerCaseFirstChar(arg.simpleName))
                }
            } else {
                processed[idx] = args[idx]
            }
        }
        return processed
    }

    /**
     * Get Java field recursively
     * @param clazz
     * @param fieldName name of the field try to get
     * @return field where field been declared
     * */
    fun getFieldRecursively(clazz: Class<*>, fieldName: String): Field? {
        var cls = clazz;
        while (cls != Object::class.java) {
            try {
                return cls.getDeclaredField(fieldName)
            } catch (ex: NoSuchFieldException) {
                cls = cls.superclass
            }
        }
        return null
    }

    private fun processScope(atBean: Bean, beanDef: AbstractBeanDefinition) {
        val scope = atBean.scope
        if (scope == BeanDefinition.SCOPE_PROTOTYPE) {
            beanDef.setScope(BeanDefinition.SCOPE_PROTOTYPE)
        } else {
            beanDef.setScope(BeanDefinition.SCOPE_SINGLETON)
        }
    }

    private fun processInitAndDestroy(clazz: Class<*>, beanDef: AbstractBeanDefinition) {
        val methods = clazz.methods
        for (method in methods) {
            if (method.isAnnotationPresent(InitMethod::class.java)) {
                beanDef.setInitMethodName(method.name)
            }
            if (method.isAnnotationPresent(DestroyMethod::class.java)) {
                beanDef.setDestroyMethodName(method.name)
            }
        }
    }

    private fun processProperties(clazz: Class<*>, beanDef: AbstractBeanDefinition) {
        val fields = clazz.declaredFields
        val propertyList = ArrayList<PropertyValue>()
        for (field in fields) {
            if (field.isAnnotationPresent(Wire::class.java)) {
                if (beanDef is WireableBeanDefinition) {
                    val targetClass = field.type
                    beanDef.addWireProperty(PropertyValue(field.name, BeanReference(StringUtils.lowerCaseFirstChar(targetClass.simpleName))))
                } else {
                    throw IllegalBeanDefinitionException("unable to process @Wire annotation" +
                            " with incompatible bean definition ${beanDef.javaClass}")
                }
            }
            if (field.isAnnotationPresent(Value::class.java)) {
                val anno = field.getAnnotation(Value::class.java)
                var value:String = anno.value
                if (value.isNotBlank()) {
                    if (value.startsWith("#")) {
                        try {
                            value = handlePropValue(value)
                        } catch (e: PropertyNotFoundException) {
                            continue
                        }
                    }
                    when (field.type) {
                        Int::class.java -> {
                            propertyList.add(PropertyValue(field.name, value.toInt()))
                        }
                        Long::class.java -> {
                            if (value.endsWith("L")) value = value.substring(0, value.length - 1)
                            propertyList.add(PropertyValue(field.name, value.toLong()))
                        }
                        Float::class.java -> {
                            propertyList.add(PropertyValue(field.name, value.toFloat()))
                        }
                        else -> {
                            propertyList.add(PropertyValue(field.name, value))
                        }
                    }
                }
            }
        }
        if (propertyList.isNotEmpty()) beanDef.setPropertyValues(propertyList)
    }

    private fun takeCareConstructor(
        clazz: Class<*>,
        beanName: String,
        beanDef: AbstractBeanDefinition
    ) {
        val constructors = clazz.declaredConstructors
        var constructor: Constructor<*> = constructors[0]

        for (c in constructors) {
            if (c.parameterCount == 0) {
                constructor = c
            }
        }

        if (constructor.parameterCount > 0) {
            beanDef.setConstructorArguments(constructor.parameterTypes)
        }
        constructor.isAccessible = true
        beanDef.setConstructor(constructor)
//        } else {
//            throw NoSuchMethodException("在 $beanName 的定义 ${beanDef.getBeanClass()} 中找不到入参为 $args 的构造函数")
//        }
    }

    private fun findConstructorWithArgs(constructor: Constructor<*>, args: Array<*>): Boolean {
        var found = true

        val pTypes = constructor.parameterTypes
        if (pTypes.size == args.size) {
            for (i in pTypes.indices) {
                if (pTypes[i].typeName != args[i]!!.javaClass.typeName) {
                    found = false
                    break
                }
            }
        }

        return found
    }

    @Throws(PropertyNotFoundException::class)
    private fun handlePropValue(value: String): String {
        val propGroups = StringUtils.getGroupsFromRegex(value, "\\#\\{(\\w+)\\}(:(\\w+))?")
        if (propGroups.isEmpty()) {
            throw PropertyNotFoundException("找不到prop: $value 的定义")
        }
        val propValue = propGroups[0]
        if (propValue != null) {
            return PropertyLoader.getProperty(propValue)
                ?: if (propGroups.size > 2) {
                    propGroups[2]!!
                } else throw PropertyNotFoundException("找不到prop: $propValue 的定义")
        }
        return value
    }

    private fun processBeanNameByMethod(atBean: Bean, method: Method): String {
        var beanName = atBean.beanName
        if (StringUtils.isEmpty(beanName)) {
            beanName = method.name
        }
        return StringUtils.lowerCaseFirstChar(beanName)
    }
}