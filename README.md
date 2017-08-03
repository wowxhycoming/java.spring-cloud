# java.spring-cloud

# 项目顺序

1. 根项目配置
2. cloud-boot
3. cloud-eureka-server
4. cloud-biz-instance(provider) , cloud-biz-client(consumer)
5. cloud-config-server、cloud-config-repository

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

1. 在根项目下创建 `cloud-boot` 的 `Module` 。

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

1. 在根项目下创建 `cloud-eureka-server` 的 `Module` 。

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
    

## cloud-biz-instance(provider) , cloud-biz-client(consumer)

注册中心建设完毕，就该在注册中心上注册服务 和 使用服务了。

为了区分 `eureka` 服务上注册的角色，这里使用两个项目分别承担不同角色。

1. 在根项目下创建 `cloud-biz-instance` 的子 `Module` 。

    POM 文件中增加依赖和打包工具
    ```
    <dependencies>
    
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- eureka客户端 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
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
    
2. 创建主类，固定角色

    使用如下注解
    ```
    @SpringBootApplication
    @EnableEurekaClient
    ```
    
    >开启 `@EnableEurekaClient` 服务会注册到 EurekaServer 上。
    
3. 创建一个 controller 提供服务

    这个 controller 需要实现读取 properties 中的值，然后返回给客户端。
    
    并且可以根据命令行启动参数来决定读取哪个配置文件。当然不同配置文件中的 key 相同，value 不同。
    
    用于区分多个实例提供相同服务。
    
    ```
    @RestController
    @RequestMapping("/value")
    public class BizInstanceController {
    
        @Autowired
        private Property property;
    
        public Property getProperty() {
            return property;
        }
    
        public void setProperty(Property property) {
            this.property = property;
        }
    
        /**
         * 返回 properties 中的内容，也有可能是通过命令行参数传入的参数
         * @return
         */
        @RequestMapping("/properties")
        public String getValue() {
            return property.toString();
        }
    }
    ```
    
    当前 controller 中注入了一个类型 Property， 是自己编写的用于读取配置文件的一个映射类。其实现也很简单：
    ```
    @Component
    @ConfigurationProperties(prefix="service")
    public class Property {
    
        private String name;
        private String ip;
        private int port;
    
        // getter and setter ...
    
        @Override
        public String toString() {
            return "Property{" +
                    "name='" + name + '\'' +
                    ", ip='" + ip + '\'' +
                    ", port=" + port +
                    '}';
        }
    }
    ```
    
5. 配置如何注册服务

    * application.yml 
    ```
    # 指定激活的配置文件
    spring:
      profiles:
        active: dev
    
    # 用于被 @ConfigurationProperties 读取的属性
    service:
      name: service1
      ip: 127.0.0.1
      port: 801
    ```
    
    * application-dev.yml
    ```
    spring:
      application:
        # 应用名称
        name: cloud-biz-instance
    server:
      # 服务端口
      port: 60011
    
    eureka:
      instance:
        # 代表了一个启动示例的标识 自定义，可以显示在控制台上
        instance-id: ${spring.cloud.client.ipAddress}:${server.port}
      client:
        # 服务是否注册到注册中心
        registerWithEureka: true
        # 是否获取注册中心注册服务的列表
        fetchRegistry: true
        serviceUrl:
          # 注册对应的eureka的默认域，一般添加对应注册中心地址
          defaultZone: http://eureka-server-1:60897/eureka/,http://eureka-server-2:60898/eureka/,http://eureka-server-3:60899/eureka/
    ```
    
6. 创建启动脚本

    因为需要启动多个实例进行负载均衡和容灾，将下面命令制作成三个不同的启动脚本，以用于启动实例
    ```
    java -jar ../../target/cloud-biz-instance-1.0-SNAPSHOT.jar --server.port=60011 --service.name=service1 --service.ip=10.7.13.1 --service.port=8081
    java -jar ../../target/cloud-biz-instance-1.0-SNAPSHOT.jar --server.port=60012 --service.name=service2 --service.ip=10.7.13.2 --service.port=8082
    java -jar ../../target/cloud-biz-instance-1.0-SNAPSHOT.jar --server.port=60013 --service.name=service3 --service.ip=10.7.13.3 --service.port=8083
    ```
    脚本中，重新指定了服务启动的端口 和 覆盖了应用要读取的值。
    
    > 像以前一样，可以单独访问看看服务是否可用

7. 在根项目下创建 `cloud-biz-client` 的子 `Module` 用于访问被集群化的 `cloud-biz-instance`

    POM 文件中增加依赖和打包工具
    ```
    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- eureka客户端 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>

    </dependencies>
    ```
    
8. 创建主类

    使用如下注解
    ```
    @SpringBootApplication
    @EnableEurekaClient
    ```
    
    >开启 `@EnableEurekaClient` 服务也可以从 EurekaServer 查询其他服务。
    
9. 创建 controller 来测试服务

    ```
    @RestController
    @RequestMapping("/view")
    public class BizClientController {
    
        @Autowired
        RestTemplate client;
    
        @Bean
        @LoadBalanced
        public RestTemplate restTemplate() {
            RestTemplate template = new RestTemplate();
            SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) template.getRequestFactory();
            factory.setConnectTimeout(3000);
            factory.setReadTimeout(3000);
            return template;
        }
    
        @RequestMapping("/properties")
        public String getValue() {
            String result = client.getForObject("http://CLOUD-BIZ-INSTANCE/value/properties/",String.class);
            System.out.println("return from instance : " + result);
    
            return client.getForObject("http://CLOUD-BIZ-INSTANCE/value/properties/",String.class);
        }
    }
    ```
    
10. 启动 client 服务器

    作为 client 服务，不需要启动多个实例，只需要启动一个实例多次访问检验即可。
    
    > 访问 `http://127.0.0.1:60021/view/properties` ，发现返回的信息是轮询的，这是 eureka 的默认策略。
    
## cloud-config-server、cloud-config-repository

在前面的过程中，会发现有很多地方都出现了重复的配置，比如：

```
serviceUrl:
  # 注册对应的eureka的默认域，一般添加对应注册中心地址
  defaultZone: http://eureka-server-1:60897/eureka/,http://eureka-server-2:60898/eureka/,http://eureka-server-3:60899/eureka/
```

这样一段配置同时出现在 `cloud-biz-client`、`cloud-biz-instance`、`cloud-eureka-server` 三个项目中。


该应用分布式配置管理，在分布式系统和微服务应用中，这一步很重要。正好，spring-cloud 提供了一个分布式配置管理的组件。

`eureka-server` 和将要说到 `config-server` 总会有一个是固化到项目里的。如果 `config-server` 注册到了 `eureka-server` 上，在项目启动时就要先知道 `eureka-server`；如果要通过 `config-server` 读取 `eureka-server` 的配置信息，在项目启动时就要先知道 `config-server` 的地址。

> 将原来 EurekaServer 的应用名 `eureka-server` 改为 `cloud-eureka-server` 。

下面假设有一份数据库的配置信息，将被分布式配置服务管理起来。

### 项目中固定信息的决定

由于配置服务器只有在项目启动的时候才会用到(或是通过配置服务器更新信息时)，所以，将配置服务器的信息固化到项目 `bootstrap.yml` 中。

### cloud-config-repository

1. 什么是 cloud-config-repository

    cloud-config-repository 是配置的仓库，是一个文件结构在 git 或是 svn 上。
    
    cloud-config-repository 的作用是为 cloud-config-server 提供配置文件的文件结构。
    
    > 注意：  
    使用配置文件的应用，要保证 `应用名` 和 `配置文件名` 名字完全匹配。  
    `应用名` 和 `配置文件名` 的命名都准守一个规则 `{applicationName}-{profile}.properties` 。

2. 应用名

    `应用名`是在项目中的配置文件里进行指定的：
    
    ```
    spring:
      application:
        # 应用名称
        name: cloud-biz-instance
    ```
    
    上面配置指定的应用名为 `cloud-biz-instance` ；那这个应用的配置文件在 cloud-config-repository 中的名字为 cloud-biz-instance-{profile}.properties 。

3. profile

    `profile` 这样指定
    
    ```
    spring:
      profiles:
        active: dev
    ```
    
    这样，项目将读取名为 `cloud-biz-instance-dev.properties` 的配置文件。
    
### spring-cloud 项目下的配置文件

1. application.yml

   ```
   spring:
    profiles:
      active: dev
   ```
   
   激活本地和远程的 profile 。并且更重要的是，配置服务器的信息一定要在 `bootstrap，yml` 中指定才能生效。
   
   无论在 `bootstrap，yml` 激活哪种 profile ， `application.yml` 是一定会被读取的，所以，可以将无差异的配置信息放到 `application.yml` 中。

2. config-server 上的配置文件

    优于本地普通配置文件的加载。
    
3. bootstrap.yml

    优于 config-server 和 本地普通配置文件 的加载。
    

### 现有项目的配置文件结构改造

项目中启用 `bootstrap.yml` 并激活 `profile` 为 dev 。

bootstrap.yml
```
spring:
 profiles:
   active: dev
```

### cloud-config-server、cloud-config-repository

1. 在根项目下创建 `cloud-config-server` 的 `Module` 。

    pom文件中添加 `config-server` 的依赖和打包插件：
    
    ```
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
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
    
2. 创建主类 `ConfigServerApplication`

    ```
    @SpringBootApplication
    @EnableConfigServer
    public class ConfigServerApplication {
        public static void main(String[] args) {
            SpringApplication.run(ConfigServerApplication.class, args);
        }
    }
    ```
    
3. 创建配置文件

    ```
    server:
      port: 60999 #本应用启动的 http 服务端口
    
    spring:
      cloud:
        config:
          server:
            git:
              uri: https://github.com/wowxhycoming/java.spring-cloud.git # github 仓库地址
              searchPaths: cloud-config-repository # 从仓库那个目录下读取文件
    ```
    
    > config-server 会去 https://github.com/wowxhycoming/java.spring-cloud.git 这个 git地址下的 cloud-config-repository 文件夹读取配置文件。
    
4. 创建配置文件仓库文件夹

    在根项目下创建文件夹 `cloud-config-repository`
    
5. 为每个项目创建配置文件

    * cloud-eureka-server
    创建 `cloud-config-repository/cloud-eureka-server-dev.propreties`
    ```
    eureka.server.default.zone=http://eureka-server-1:60897/eureka/,http://eureka-server-2:60898/eureka/,http://eureka-server-3:60899/eureka/
    ```
    
    * cloud-biz-instance-dev
    创建 `cloud-config-repository/cloud-biz-instance-dev.properties`
    ```
    eureka.server.default.zone=http://eureka-server-1:60897/eureka/,http://eureka-server-2:60898/eureka/,http://eureka-server-3:60899/eureka/
    ```
    
    * cloud-biz-client-dev
    创建 `cloud-config-repository/cloud-biz-client-dev.properties`
    ```
    eureka.server.default.zone=http://eureka-server-1:60897/eureka/,http://eureka-server-2:60898/eureka/,http://eureka-server-3:60899/eureka/
    ```
    
    > 启动 config-server 之前要先上传配置文件到版本仓库。
    
6. 修改项目配置文件

    * 首先在每个项目中的 `bootstrap.yml` 配置文件中写上配置服务器的地址。
    ```
    spring:
    #  application:
    #    name: cloud-boot-business
      profiles:
        active: dev
      cloud:
        config:
          uri: http://127.0.0.1:${config.port:60999} #表示如果在命令行提供了就使用命令行参数
    #      name: cloud-boot-service # 等于 spring.application.name 但优先级低
    #      profile: ${config.profile:dev} # 等于 spring.profiles.active 但优先级低
    ```
    > spring.application.name 写成对应项目应该的名字。
    
    > 固定不变 或 非常少改变的东西也可以放到这里。
    
    * 更改每个项目的 application-dev.yml 文件，修改 eureka.client.serviceUrl.defaultZone 的值
    ```
    defaultZone: ${eureka.server.default.zone}
    ```
    
### 项目启动顺序

clean 项目重新 package 。

1. 启动 cloud-config-server(确保配置文件已经上传的到版本库，config-server 才有意义)
2. 启动 cloud-eureka-server(多节点)
3. 启动 cloud-biz-instance(多节点)
4. 启动 cloud-biz-client