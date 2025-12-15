# 商户礼品码核销系统 (Merchant Gift Code Redemption System)

## 项目简介

这是一个基于 Spring Boot 的后端服务，为商家用户提供一套完整的礼品码核销和管理功能。系统支持礼品码的核销、核销记录查询以及多维度的数据统计分析。

## 技术栈

*   **Java 21**
*   **Spring Boot 3.2.5**
*   **Spring Data JPA (Hibernate)**
*   **Maven**
*   **H2 Database** (用于开发和测试)

## 如何运行

### 1. 环境要求

*   JDK 21 或更高版本
*   Maven 3.x

### 2. 运行项目

在项目根目录下，执行以下命令：

```bash
mvn spring-boot:run
```

应用启动后，服务将在 `http://localhost:8080` 上可用。

### 3. 数据库配置

*   当前项目配置为使用 **H2 内存数据库**，这意味着每次应用重启时，数据都会被重置。
*   项目启动时，会自动执行 `src/main/resources/data.sql` 文件来插入一条测试商户和一条测试礼品码，方便开发和测试。
*   如果需要连接到 MySQL 数据库，请修改 `pom.xml` 中的数据库驱动依赖，并在 `src/main/resources/application.properties` 中更新数据源配置。

## API 文档

详细的 API 使用说明请参见 `API.md` 文件。
