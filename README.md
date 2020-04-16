# Tiny IOC
利用尽量少的依赖（目前依赖dom4j，cglib，aspectjweaver）实现一个~~支持AOP的IOC容器~~模仿Spring的框架

设计思路参考[手写Spring---IOC容器（1）](https://juejin.im/post/5cb1c9c4e51d456e770bdc9c)
、[手写Spring---DI依赖注入（2）](https://juejin.im/post/5cb778016fb9a068aa4b971b)、
[手写Spring---AOP面向切面编程（3）](https://juejin.im/post/5cc01088f265da035c6bc7f8)和[手写Spring---AOP面向切面编程（4）](https://juejin.im/post/5cc83ae5e51d456e7d18a000)
在此基础上扩展支持基于xml的bean定义和基于注解的bean定义，实现bean的自动化扫描。~~后续将支持AOP织入~~(已支持)。更多功能包括：模块化与自动配置、数据库支持、MVC支持（计划中）、web容器支持（计划中）

### IOC容器功能

**注意：XML方式容器已停止开发**

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

### AOP支持

| 计划的功能 | 实现 |
| -------- | -------- |
| AOP所需代理类生成 | 30%* |
| 基于类和方法的切点定义 | 100% |
| 基于正则式的切点定义 | 30%(未完整测试) |
| 切面扫描 | 100% |
| 切面自动注册 | 100% |
| 自动配置AOP开闭| 100% |
*经测试，~~AOP代理类不支持DI，回滚进度~~，不支持@Wire注入，请使用构造器注入

### 数据库支持
| 计划的功能 | 实现 |
| --------- | -- |
| 自动配置数据库功能开闭| [x] |
| 数据库连接池 | 部分实现 |
| 结果集映射到POJO | 基本可用 |
| 基于接口的Dao层实现 | [x] |
| SQL语句占位符 | 部分可用 |
| 事务支持（封装JDBC） | 部分可用 |

## 注解方式设计约定
* 默认情况下，标注`@Bean`或者`@Config`的类才能被扫描。`@Bean`作用在类上必须提供无参构造函数，若必须带参数，则可以在标记了`@Config`的类中将相应的
工厂方法标记为`@Bean`来定义bean，此时若`@Bean`注解没有提供beanName则默认使用该方法名，此种bean定义不支持通过class获取实例
* 使用`@InitMethod`和`@DestroyMethod`标记构造和解构函数
* 使用`@Wire`注入依赖，你可以在接口前使用`@Wire`注解，容器将尝试使用标记了`@Bean`的对应实现类或者**兼容的工厂方法**去完成依赖注入
* 使用`@Value`注入值，包裹在"#{}"中的定义将会从指定的property文件读取，同时可在“#{}”后添加“:default"来指定默认值（将default替换成需要的默认值），否则按照字面值解释
* 可以通过向PropertiesApplicationContext（注解方式下默认的IOC容器）添加`PackageScanner.Filter`来自定义扫描过程。
* 目前框架的自动配置器会去主动扫描的自动配置类定义在core模块的`com.aagu.ioc.context.ConfigurationCenter.java`，需要修改代码才能添加额外的自动配置。

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

## 数据库设计约定
要想使用数据库，需要在配置文件中声明`enable-data=true`,并提供数据库Url，Driver，User（可选），Password（可选）

数据库支持的设计思路参考Mybatis，Dao层接口用`@Repository`标识，提供`@Select`，`@Insert`，`@Update`，`@Delete`注解，每种注解需要声明sql语句，暂不支持占位。配合`@Wire`注解自动注入实现类。当然也可以获取`sessionFactory`实例，手动执行sql，返回结果为`ResultSet`。

数据库支持利用`@Transactional`声明事务，目前仅支持将一个方法中的数据库操作纳入一个事务管理，捕获异常自动回滚，否则提交。不支持嵌套事务、事务传播、隔离
级别定义。当`@Transactional`作用于方法时，该方法将纳入事务管理，当`@Transactional`作用于类时，该类声明的全部公开方法都将纳入事务管理。

编程式事务可以通过获取`transactionManager`这个bean手动控制

## Web服务器
该项目内置的Web服务器处于非常初级的阶段，并未在开发计划中（其实就是我自己玩玩）

## 如何使用
程序的入口定义在TinyIocApplication中，实际使用时我们需要新建一个它的子类并实现`run`方法，待容器初始化完成后将从此处开始运行。对于Web应用，可以通过继承TinyWebApplication类快速开始，避免手动编码开启web服务器。
同时我们的子类必须加入`@Application`注解，我们可以在该注解上填写`basePackage`指定扫描的包（默认为我们的子类所在的包）。要想使用xml方式的容器，需要在`@Application`注解上填写`xmlLocation`注明xml文件所在路径。
另外`@Application`还可通过`property`指定property文件，从文件读取值的定义。

目前仅支持注解方式的AOP使用，标记`@Aspect`的类将被识别为切面定义。
目前支持前置、后置、环绕和异常四种切点定义，分别使用`@Before`、`@After`、`@Around`和`@AfterThrow`标记，并为expression参数传入AspectJ形式的匹配语法。
方法名没有限定，前置、后置和异常切点入参为`JointPoint`类，环绕切点入参为`ProceedJointPoint`。通过`@Order`注解添加切面顺序，数字越小优先级越高，默认为0

！！！注意：AOP默认关闭，需要在property中加入`enable-aop = true`手动开启