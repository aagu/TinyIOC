package com.aagu.ioc.util;

import com.aagu.ioc.bean.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class BeanUtils {
    public static Constructor<?> determineConstructor(BeanDefinition definition, Object[] args)
            throws NoSuchMethodException {
        Constructor<?> constructor = null;
        // 无参构造函数
        if (args == null) return Objects.requireNonNull(definition.getBeanClass()).getConstructor();

        // 尝试从definition获取
        constructor = definition.getConstructor();
        if (constructor != null) return constructor;

        // 根据参数类型获取
        Constructor<?>[] constructors = Objects.requireNonNull(definition.getBeanClass()).getConstructors();
        for (Constructor<?> c: constructors) {
            if (c.getParameters().length == args.length) {
                Class<?>[] pTypes = c.getParameterTypes();
                int i = 0;
                for (; i < pTypes.length; i++) {
                    if (pTypes[i].getTypeName().equals(args[i].getClass().getTypeName())) break;
                }
                if (i == pTypes.length) {
                    constructor = c;
                    break;
                }
            }
        }
        if (constructor != null) {
            definition.setConstructor(constructor);
            return constructor;
        }
        throw new NoSuchMethodException("在" +definition.getBeanClass() +"中找不到入参为" + Arrays.toString(args) + "的构造函数");
    }

    public static Method determineFactoryMethod(BeanDefinition bd, Object[] args, Class<?> type) throws Exception {
        if (type == null) {
            type = bd.getBeanClass();
        }
        String methodName = bd.getFactoryMethodName();
        if (args == null) {
            return type.getMethod(methodName, null);
        }
        Method m = null;
        // 对于原型bean,从第二次开始获取bean实例时，可直接获得第一次缓存的构造方法。
        m = bd.getFactoryMethod();
        if (m != null) {
            return m;
        }
        // 根据参数类型获取精确匹配的方法
        Class[] paramTypes = new Class[args.length];
        int j = 0;
        for (Object p : args) {
            paramTypes[j++] = p.getClass();
        }
        try {
            m = type.getMethod(methodName, paramTypes);
        } catch (Exception e) {
            // 这个异常不需要处理
        }
        if (m == null) {
            // 没有精确参数类型匹配的，则遍历匹配所有的方法
            // 判断逻辑：先判断参数数量，再依次比对形参类型与实参类型
            outer: for (Method m0 : type.getMethods()) {
                if (!m0.getName().equals(methodName)) {
                    continue;
                }
                Class<?>[] paramterTypes = m.getParameterTypes();
                if (paramterTypes.length == args.length) {
                    for (int i = 0; i < paramterTypes.length; i++) {
                        if (!paramterTypes[i].isAssignableFrom(args[i].getClass())) {
                            continue outer;
                        }
                    }
                    m = m0;
                    break outer;
                }
            }
        }
        if (m != null) {
            // 对于原型bean,可以缓存找到的方法，方便下次构造实例对象。在BeanDefinfition中获取设置所用方法的方法。
            if (bd.isPrototype()) {
                bd.setFactoryMethod(m);
            }
            return m;
        } else {
            throw new Exception("不存在对应的构造方法！" + bd);
        }
    }

}
