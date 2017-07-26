# java.spring-cloud

# 项目顺序

1. 根项目配置
2. cloud-boot
3. cloud-eureka-server

## 根项目配置

修改 POM 文件：

    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>me.xhy</groupId>
        <artifactId>java.spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>1.5.2.RELEASE</version>
            <relativePath/>
        </parent>
    
        <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
            <java.version>1.8</java.version>
            <spring-cloud.version>Dalston.SR1</spring-cloud.version>
        </properties>
    
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-dependencies</artifactId>
                    <version>${spring-cloud.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
    
        <repositories>
            <repository>
                <id>spring-snapshots</id>
                <url>http://repo.spring.io/snapshot</url>
                <snapshots>
                    <enabled>true</enabled>
                </snapshots>
            </repository>
            <repository>
                <id>spring-milestones</id>
                <url>http://repo.spring.io/milestone</url>
            </repository>
        </repositories>
    
        <pluginRepositories>
            <pluginRepository>
                <id>spring-snapshots</id>
                <url>http://repo.spring.io/snapshot</url>
            </pluginRepository>
            <pluginRepository>
                <id>spring-milestones</id>
                <url>http://repo.spring.io/milestone</url>
            </pluginRepository>
        </pluginRepositories>
    
    </project>
    ```

## cloud-boot

1. 在跟项目下创建 `cloud-boot` 的 `Module` 。

    在 `pom` 中添加依赖 spring-boot 依赖
    
    ```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    ```

2. 创建主类，开启 web 应用

    一个简单的 `spring boot` 程序，了解一下 `spring boot` 的开箱即用。简单几个注解，开启 web 应用。
    
    `@SpringBootApplication` 包含了三个注解，分别是 `@Configuration` , `@EnableAutoConfiguration` , `@ComponentScan` .
    
    ```
    @SpringBootApplication
    @RestController
    @RequestMapping("/")
    public class BootApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(BootApplication.class, args);
        }
    
        @RequestMapping("/greet")
        public String greet() {
            return "hi there";
        }
    
    }
    ```
    
3. 创建 `application.yml` 配置文件

    指定启动端口

    ```
    server:
      port: 60001
    ```

    >启动项目，访问 `http://localhost:60001/greet` 查看结果。

4. 添加监控

    只需要添加一个依赖，即可享有监控功能。
    
    ```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    ```
    
    >访问 `http://localhost:60001/health` 查看结果。

## cloud-eureka-server

1. 在跟项目下创建 `cloud-eureka-server` 的 `Module` 。

    在 `pom` 中添加依赖 eureka-server 、 监控 和 打包工具的 依赖
    
    ```
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka-server</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
    ```
    
2. 创建主类 `EurekaServerApplication` 

    ```
    @EnableEurekaServer
    @SpringBootApplication
    public class EurekaServerApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(EurekaServerApplication.class, args);
        }
    
    }
    ```

3. 修改 host 文件

    ```
    127.0.0.1 eureka-server-1
    127.0.0.1 eureka-server-2
    127.0.0.1 eureka-server-3
    ```

4. 创建配置文件 application.yml

    ```
    spring:
      application:
        # 应用名称
        name: eureka-server
    
    server:
      # eureka 的服务端口
      port: 60897
    
    eureka:
      # 环境名称
      environment: dev
      instance:
        # 代表了一个启动示例的标识 自定义，可以显示在控制台上
        instance-id: ${spring.cloud.client.ipAddress}:${server.port}
        hostname: localhost
        prefer-ip-address: true
      client:
        # 服务是否注册到注册中心
        registerWithEureka: true
    #    # 是否获取注册中心注册服务的列表
        fetchRegistry: true
        serviceUrl:
    #      # 注册对应的eureka的默认域，一般添加对应注册中心地址
          defaultZone: http://eureka-server-1:60897/eureka/,http://eureka-server-2:60898/eureka/,http://eureka-server-3:60899/eureka/

    ```
    
    > 这里 `defaultZone` 为什么要使用域名。  
    在 eureka 集群中，所有节点的名字都一样，让 eureka 午饭分辨，导致 `DS Replicas` 错误。

5. 当前 Module 下创建 `command\windows` 文件夹

    ```
    java -jar ../../target/cloud-eureka-server-1.0-SNAPSHOT.jar --server.port=60897
    java -jar ../../target/cloud-eureka-server-1.0-SNAPSHOT.jar --server.port=60898
    java -jar ../../target/cloud-eureka-server-1.0-SNAPSHOT.jar --server.port=60899
    ```
    
    以上三条命令，创建三个 `.bat` 文件。
    
    分别启动三个 `bat` 文件，访问 `http://eureka-server-1:60897/` `http://eureka-server-2:60898/` `http://eureka-server-3:60899/` 查看效果。
    
    > 这里查看网页信息 `General Info` 部分，三个分片的状态为 `unavailable-replicas` 下，可能是因为节点的IP都相同造成的，多个节点分布在不同的机器，状态就会变为 `available-replicas` 。
    
    > 不用关心 `General Info` 下的 `unavailable-replicas` ，不耽误正常使用。
    
6. cloud-eureka-server 中一些概念

    配置文件中的几个关键概念：  
    Eureka Server
    >注册中心。它提供一个用于注册的服务和一个REST api ，来使它可以注册、注销、发现其他服务。
    
    Eureka Service
    >可以在注册中心注册的和被其他应用发现的 任何应用。 一个 Eureka Service 拥有逻辑描述符或 service id，可以使用它们找到一个或多个相同的应用实例。
    
    Eureka Instance
    >为了被发现把自己注册到 Eureka Server 的应用。
    
    Eureka Client
    >主动发现其他 Eureka Service 的应用。