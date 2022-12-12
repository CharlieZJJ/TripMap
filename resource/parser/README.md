所有的数据都准备好了

首先开启neo4j数据库，用户名neo4j，密码123456

接下来运行run.py即可

完成后可以查看[这里](http://localhost:7474/browser/)来检验

接下来打开sql文件夹，其中包含

- post.sql
- user.sql
- update.sql
- trip_map_structure.sql
- trip_map.sql

如果想要从0开始，可以按照顺序运行trip_map_structure.sql post.sql user.sql update.sql（注意一定要按照顺序）

如果想直接导入，可以直接运行trip_map.sql