# Environment

| name  | version |
|-------|---------|
| maven | 3.8.6   |
| Java  | 1.11    |

neo4j目录下是它的实体类和dao层，暂时配置到这里，有问题再更新

mysql目录下是它的实体类和mapper层，使用的是mybatis-plus

另外需要将mysql的root用户密码设置为123456
neo4j的neo4j用户设置为123456

- [x] 统一格式返回
- [x] 统一异常处理
- [x] 安全验证及登录
- [x] log处理
- [ ] 分页问题的处理

sql文件在resource/下

下载项目后在idea打开，等待idea配置完成即可运行
