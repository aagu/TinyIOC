# Tiny IOC
利用尽量少的依赖（目前依赖dom4j，cglib，aspectjweaver）实现一个支持AOP的IOC容器

设计思路参考[手写Spring---IOC容器（1）](https://juejin.im/post/5cb1c9c4e51d456e770bdc9c)
、[手写Spring---DI依赖注入（2）](https://juejin.im/post/5cb778016fb9a068aa4b971b)、
[手写Spring---AOP面向切面编程（3）](https://juejin.im/post/5cc01088f265da035c6bc7f8)和[手写Spring---AOP面向切面编程（4）](https://juejin.im/post/5cc83ae5e51d456e7d18a000)
在此基础上扩展支持基于xml的bean定义和基于注解的bean定义，实现bean的自动化扫描。后续将支持AOP织入

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

| 计划中的功能 | 进度 |
| -------- | -------- |
| AOP所需代理类生成 | 100% |
| 基于类和方法的切点定义 | 100% |
| 基于正则式的切点定义 | 30%(未完整测试) |
| 切面扫描 | 100% |
| 切面自动注册 | 100% |

## 注解方式设计约定
* 标注`@Bean`或者`@Config`的类才能被扫描。`@Bean`作用在类上必须提供无参构造函数，若必须带参数，则可以在标记了`@Config`的类中将相应的
工厂方法标记为`@Bean`来定义bean，此时若`@Bean`注解没有提供beanName则默认使用该方法名，此种bean定义不支持通过class获取实例
* 使用`@InitMethod`和`@DestroyMethod`标记构造和解构函数
* 使用`@Wire`注入依赖，你可以在接口前使用`@Wire`注解，容器将尝试使用标记了`@Bean`的对应实现类去完成依赖注入
* 使用`@Value`注入值，包裹在"#{}"中的定义将会从指定的property文件读取，同时可在“#{}”后添加“:default"来指定默认值（将default替换成需要的默认值），否则按照字面值解释

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

## 如何使用
程序的入口定义在TinyIocApplication中，实际使用时我们需要新建一个它的子类并实现`run`方法，待容器初始化完成后将从此处开始运行。同时我们的子类必须加入`@Application`注解，
我们可以在该注解上填写`basePackage`指定扫描的包（默认为我们的子类所在的包）。要想使用xml方式的容器，需要在`@Application`注解上填写`xmlLocation`注明xml文件所在路径。
另外`@Application`还可通过`property`指定property文件，从文件读取值的定义。

目前仅支持注解方式的AOP使用，标记`@Aspect`的类将被识别为切面定义。
目前支持前置、后置、环绕和异常四种切点定义，分别使用`@Before`、`@After`、`@Around`和`@AfterThrow`标记，并为expression参数传入AspectJ形式的匹配语法。
方法名没有限定，前置、后置和异常切点入参为`JointPoint`类，环绕切点入参为`ProceedJointPoint`。

！！！注意：AOP默认关闭，需要在property中加入`enable-aop = true`手动开启