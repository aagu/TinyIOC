# Tiny IOC
徒手（只依赖了dom4j，因为懒得处理xml文件）实现一个IOC容器

设计思路参考[手写Spring---IOC容器（1）](https://juejin.im/post/5cb1c9c4e51d456e770bdc9c)
和[手写Spring---DI依赖注入（2）](https://juejin.im/post/5cb778016fb9a068aa4b971b)，
在此基础上扩展支持基于xml的bean定义和基于注解的bean定义，实现bean的自动化扫描。

| 实现的功能 | 注解方式 | xml方式 |
| -------- | --------| -------|
| bean扫描 | [x] | [x] |
| 单例支持 | [x] | [x] |
| 通过名称获取实例 | [x] | [x] |
| 通过类名获取实例 | [x] | [x] |
| 自定义构造、解构函数| [x] | [x] |
| 值注入 | [x] | [x] |
| 依赖注入 | [x] | [x] |
| 构造函数注入 | [x] | [x] |

## 注解方式设计约定
* 标注`@Bean`或者`@Config`的类才能被扫描。`@Bean`作用在类上必须提供无参构造函数，若必须带参数，则可以在标记了`@config`的类中将相应的
工厂方法标记为`@Bean`来定义bean，此时若`@Bean`注解没有提供beanName则默认使用该方法名，此种bean定义不支持通过class获取实例
* 使用`@InitMethod`和`@DestroyMethod`标记构造和解构函数
* 使用`@Wire`注入依赖
* 使用`@Value`注入值，包裹在"#{}"中的定义将会从`app.properties`文件读取，否则按照字面值解释

## xml方式设计约定
* 所有的bean都定义在标签`<container></container>`中
* `<bean></bean>`标签中是一个bean的定义
* `name`属性声明bean的名称，必须有
* `class`属性声明bean的java类，必须有
* `scope`属性声明bean的类型，可选属性，有singleton和prototype两种值
* `<initMethod></initMethod>`标签值为java class中自定义的初始化方法，可选
* `<destroyMethod></destroyMethod>`标签值为java class中自定义的销毁方法，可选
* `<property></property>`标签表示bean的注入，`name`属性是java class中依赖的名称。`ref`属性声明被注入的bean的名称（与bean的声明的名称保持一致）。`type`表明注入值的类型，`value`表明注入值的真实值，这两者配合使用，且不能和`ref`同时使用
* `<constructor></constructor>`标签表明构造函数，每个参数单独声明在`<param></param>`中，`name`属性表示参数名称，`type`属性表示参数类型，类注入统一标记为bean，`value`属性表示参数真实值（对于类注入，就是bean的名称）