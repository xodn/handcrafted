# Mini Spring

手搓 Spring 核心机制，用最小实现理解 Spring IoC 容器的设计。

## 已实现

- [x] BeanDefinition
- [ ] BeanFactory
- [x] Singleton Container
- [x] Dependency Injection
- [x] Bean Lifecycle
- [x] BeanPostProcessor
- [x] AOP
- [ ] Circular Dependency
- [ ] Transaction

## 核心流程

扫描
↓
BeanDefinition
↓
createBean
├─ instantiateBean
├─ populateBean
├─ initializeBean
│   ├─ Aware
│   ├─ BeanPostProcessor.before
│   ├─ 初始化方法
│   └─ BeanPostProcessor.after
↓
singletonObjects

## 学习目标

通过逐步实现 Spring 核心机制，理解：

- IoC 容器如何管理 Bean
- BeanDefinition 与 Bean 实例的区别
- Bean 的创建与生命周期
- Dependency Injection 的实现原理
- BeanPostProcessor 的作用
- AOP 代理如何介入 Bean 生命周期
- Spring 如何解决循环依赖
- 声明式事务的基本实现原理