# InkField Backend

砚田小说创作平台 - 后端服务

## 技术栈

- Java 21
- Spring Boot 3
- PostgreSQL
- Flyway
- Maven

## 快速开始

### 环境要求

- JDK 21
- Maven 3.9+
- PostgreSQL 15+

### 数据库配置

创建 PostgreSQL 数据库后，在 `application.yml` 中配置连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/inkfield
    username: your_username
    password: your_password
```

### 运行

```bash
mvn spring-boot:run
```

API 地址：http://localhost:4000/api

### 构建

```bash
mvn clean package
```

## 项目结构

```
src/main/java/com/novelstudio/backend/
├── controller/    # API 控制器
├── service/       # 业务逻辑
├── model/         # 数据模型
├── persistence/   # 数据访问层
├── config/        # 配置类
└── exception/     # 异常处理
```

## 相关仓库

- [前端仓库](https://github.com/Jvxi/InkField-frontend)
- [汇总仓库](https://github.com/Jvxi/InkField)
