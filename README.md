# java.spring-cloud

# 项目顺序

1. 根项目配置
2. cloud-boot

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