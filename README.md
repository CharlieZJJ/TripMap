# TripMap

### 简介



### 环境

|    名称    |    版本    |
| :--------: | :--------: |
|   Server   | Centos 7.9 |
|   Neo4j    |   4.4.11   |
|   MySQL    |   8.0.31   |
|   Redis    |   3.2.12   |
|    Java    |  11.0.14   |
| SprintBoot |   2.7.5    |

### 技术栈

Spring Security

Spring Boot

Spring Data Redis

Spring Data Neo4j

Mybatis-plus

协同过滤算法

异步推荐

### 部署

在服务器配置好环境后，使用maven将项目打包通过ftp发送到服务器

运行

```
nohup java -jar TripMap-0.0.1-SNAPSHOT.jar &
```

### API

运行成功后可以访问`localhost:8080/api`查看系统支持的api

