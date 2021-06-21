### Spring经典八股文整理

#### 1. 什么是Spring/ 列举一下Spring重要模块。

```
Spring是一个轻量级的开发框架，提供了IOC和di功能。同时也是支持对现有许多框架的组合，是一个脚手架。
Spring一般指的是Spring Framework。包含了许多模块的集合，比如: core container、Data access、AOP/aspect、test、web。
```

```
Spring框架中组合了许多的开发模块，比如核心容器、Web、DataAccess组件等。其中最重要和最根本的组件是CoreContainer.
Core Container: 包含beans、core、context、spEL等。提供IOC、依赖注入功能。
Aspects: 为AspectJ提供集成支持
AOP： 提供了面向切面编程的实现。
JDBC: 提供数据库连接服务支持
ORM: 用来支持ORM工具。
Web: 用来对Web应用程序提供支持。
Test: JUint等测试框架提供支持。
```

#### 2. Spring中的IoC

+ 什么Spring IoC容器？http://svip.iocoder.cn/Spring/IoC-intro/
+ https://www.iocoder.cn/Fight/Interview-poorly-asked-Spring-IOC-process-1/

```
Spring Ioc容器是Spring框架的核心，IoC容器负责对象的创建和他们之间的装配。并配置和管理对象的完整生命周期。
即: IoC容器负责管理对象，包括对象的创建、对象之间的装配以及管理对象的生命周期。
```

+ 什么是IoC、为什么要IoC。

```
IoC即控制反转。就是将具体对象的生命周期和对象之间关系的管理交给容器，让容器为代码编写人员去服务。
控制: 传统开发通过new等方式亲手控制依赖对象，IoC容器之后，控制权交给了IoC容器。
翻转: 传统开发是通过new等方式主动获取依赖的对象，而IoC容器中，对象的依赖是又IoC容器注入的，属于被动接受。Spring通过DI的方式实现IoC。


IoC的优势:
1. 最小化应用程序的代码量
2. 很小的代价和侵入实现松耦合
3. 可以支持bean对象的延迟加载
了解: 依赖注入的方式？ 如何解决循环依赖的问题。
```

+ IoC的基本组件和大致流程

```
1. Resource，是对资源的抽象，实现类代表了不同资源的访问策略: classPathResource、FileSystemResource。
2. ResourceLoader，是对资源加载的抽象，实现类实现了不同资源的加载: ClassPahtResourceLoader、FileSystemResourceLoader、
3. BeanFactory，Bean的容器，内部维护一个Map，通过BeanDefinition的描述实现bean的创建和管理。
4. BeanDefinition，用来描述Bean的信息。
5. BeanDefinitionReader，将配置文件的内容转化为BeanDefinition。

大致流程:
ResourceLoader加载Resource，并通过BeanDefinitionReader将配置资源加载为BeanDefinition，然后在BeanFactory的Map中进行维护。
```

+ Spring容器种类

```
Spring提供两种IoC容器，一个是BeanFactory和ApplicationContext。
BeanFactory: 低级容器，只负责加载Bean和获取Bean.
Applicationcontext: 高级容器，拓展了BeanFactory接口，提供了一些额外的功能。
Lifecycle: 管理生命周期
BeanNameAware: Aware感知
Closable: 释放资源
```

+ Spring bean是什么

```
Bean就是由Spring容器管理的对象，基于用户配置的元数创建然后通过Spring容器进行管理。
```

+ Bean在容器中的生命周期？

```
1. Bean容器将用户配置的元数据转换成Spring中的Beandefinition对象。
2. Bean容器利用java反射机制，参照BeanDefinition实例化一个Bean对象。
3. 如果存在属性值，通过Set()方法赋值。
4. 如果Bean实现了一系列Aware接口,例如BeanClassNameAware、BeanClassLoaderAware，调用对象的Set方法传入参数。
5. 如果存在和当前容器相关的BeanPostProcesser，执行postProcesserBeforeInitialization();
6. 如果配置了自定义的init-method，调用指定的方法。
7. 如果存在和当前容器相关的BeanPostProcesser，执行postProcesserAfterInitialization();
++++++++++++++
此时，Bean对象投入使用
++++++++++++++
8. 如果要销毁Bean时候，如果实现了DisposableBean，执行destroy()方法。
9. 如果配置了destroy-mothod方法，调用指定配置的方法进行销毁。
```

+ Spring 容器DI如果解决循环依赖问题

```
个人感觉解决这个问题，类似一道算法题，copy一个图。
大致原理就是，创建和获得依赖是分离的，创建之后就会放入一个缓存中，然后递归的去创建依赖，如果依赖在缓存中直接返回，如果不存在则创建，然后递归的生成依赖，知道函数返回。
```

#### 3. Spring的AOP

+ 什么是AOP/AOP的实现

```
AOP,面向切面编程，是将与具体业务无关，但是业务模块需要共同调用的逻辑和责任: (日志处理，事务、权限管理等)封装起来，降低这些逻辑与业务代码的耦合。提高扩展性和维护性。
AOP的实现是基于代理模式，或者说是动态代理。通过创建代理对象，在代理对象中执行通用的逻辑。
具体实现方式:JDK Proxy、Cglib、AspectJ。
```

+ Spring AOP 和 AspectJ AOP的区别

```
1. 代理方式不同 : Spring是基于动态代理方式，AspectJ是基于静态大力的方式
2. 支持粒度不同 : 
Spring AOP 仅支持方法级别的 PointCut 。
AspectJ AOP 提供了完全的 AOP 支持，它还支持属性级别的 PointCut 。
```

#### 4. Spring中的设计模式

+ 单例模式

```
Spring容器中的Bean默认都是单例的，同过单例工厂获得。
```

+ 工厂模式

```
Spring中的Bean工厂(BeanFactory/AppliactionContext)来创建Bean对象。
工厂模式: 需要对相关对象直接new的话，该对象是强相关的，如果需要修改依赖对象代价比较高，所以将对象的创建交给工厂来实现，用户只需要指定需求，工厂进行具体类的创建，当有变更只需要修改工厂类的实现即可。
简单工厂、工厂方法、抽象工厂。
```

+ 代理模式

```
Spring AOP功能的实现。
```

+ 模板方法

```
Spring 中 jdbcTemplate、hibernateTemplate 等以 Template 结尾的对数据库操作的类，它们就使用到了模板模式。
```

+ 适配器模式 https://blog.csdn.net/carson_ho/article/details/54910430/

```
解决调用接口不兼容的情况，比如A接口a(),但是B接口之后b(),如果想要实现调用a()也能实现b的功能，就需要一个适配器对象进行接口转换。两种方式: 通过继承的方式和通过组合的方式
```

```java
interface A {
    public void a();
}
class B {
    public void b(){};
}
class adapter implements A extends B {
    @Override
    public void a(){
        b();// super.b();
    };
}
// 或者
class adapter implements A {
    B b = new B();
    @Override
    public void a(){
        b.b();// super.b();
    };
}
```



