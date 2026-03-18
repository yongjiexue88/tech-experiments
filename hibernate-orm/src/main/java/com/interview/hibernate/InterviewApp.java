package com.interview.hibernate; // 包声明

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ 面试要点: @SpringBootApplication 做了什么？ ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ 这是一个复合注解 (Meta-annotation)，它合并了三个核心注解的功能： ║
 * ║ 1. @SpringBootConfiguration: 标记这是一个配置类，等同于 @Configuration ║
 * ║ 2. @EnableAutoConfiguration: Spring Boot 的核心！告诉 Spring 去扫描 ║
 * ║ classpath，如果发现了相关包，就自动装配。比如，发现了 hibernate-core， ║
 * ║ 就自动为你创建一个 EntityManagerFactory 和 TransactionManager。 ║
 * ║ 3. @ComponentScan: 自动扫描当前包及其子包下的所有 @Component, @Service, ║
 * ║ @Repository，将其注册到 IoC 容器中。 ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@SpringBootApplication
public class InterviewApp {

    public static void main(String[] args) {
        // 启动 Spring 上下文
        SpringApplication.run(InterviewApp.class, args);
    }
}
