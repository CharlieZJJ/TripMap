# TripMap

## 简介

​        这是一个基于neo4j图数据库的集关注用户和收藏/点赞/发布帖子功能为一体的社交类APP；这是一个基于协同过滤算法的为用户个性化推荐在地图上的可视化打卡地点以及推荐帖子的打卡推荐类APP。在该APP中，用户不仅可以关注朋友的朋友，还可以浏览系统为其推荐的帖子，在地图上查看为其推荐的地点，点击地图上的地点还可以查看与地点相关的帖子。基于协同过滤的推荐算法，用户获得的推荐基于其关注的用户、点赞、收藏地点和帖子等行为，使用户获得较为良好的浏览体验。

## 环境

|    名称    |    版本    |
| :--------: | :--------: |
|   Server   | Centos 7.9 |
|   Neo4j    |   4.4.11   |
|   MySQL    |   8.0.31   |
|   Redis    |   3.2.12   |
|    Java    |  11.0.14   |
| SprintBoot |   2.7.5    |

## 技术栈

Spring Security

Spring Boot

Spring Data Redis

Spring Data Neo4j

Mybatis-plus

协同过滤算法

异步推荐

## 部署

在服务器配置好环境后，使用maven将项目打包通过ftp发送到服务器

## 运行

```
nohup java -jar TripMap-0.0.1-SNAPSHOT.jar &
```

## API

运行成功后可以访问`localhost:8080/api`查看系统支持的api

## 功能划分
![image](https://user-images.githubusercontent.com/100272100/209806005-c531a182-31f9-4537-a41b-a8354b5b43c3.png)


## 前端仓库

配套前端仓库点击如下链接查看：

[hot-zhy/trip_map: 2022年非关系数据库课程项目前端代码 (github.com)](https://github.com/hot-zhy/trip_map)

## 前端实现效果

1.首页
![image](https://user-images.githubusercontent.com/100272100/209810016-a9e14cc5-a2ea-4b08-8ee9-ae14cc6a798b.png)

2.地图页
![image](https://user-images.githubusercontent.com/100272100/209810124-eb5e2704-678c-414b-9efe-466a32c1ad83.png)

3.搜索页
![image](https://user-images.githubusercontent.com/100272100/209810154-7ee3be7f-dc27-4a21-8e5f-9b3053963d17.png)

4.搜索结果页
![image](https://user-images.githubusercontent.com/100272100/209810211-67623186-2eb5-4a80-bef6-7f83a6958213.png)

5.帖子详情页
![image](https://user-images.githubusercontent.com/100272100/209810240-7a8b944c-12b6-428a-9857-1f9361fb6e7b.png)

6.我的页
![image](https://user-images.githubusercontent.com/100272100/209810272-f73933a8-91b2-4f0f-8293-659b3435742f.png)

7.我的关注列表/我的粉丝列表页
![image](https://user-images.githubusercontent.com/100272100/209810299-1939eb1b-257a-4a46-b245-57cb49a054d1.png)

8.我的收藏地点页
![image](https://user-images.githubusercontent.com/100272100/209810320-2f184f86-851c-44fc-8e31-0b3bd5f96065.png)

9.我的帖子/我的收藏帖子页
![image](https://user-images.githubusercontent.com/100272100/209810407-291df243-1785-4578-99b6-60c600488a26.png)

10.发布帖子页
![image](https://user-images.githubusercontent.com/100272100/209810435-5cdb7c84-5ee7-485e-b837-eec6701e384a.png)

11.与地点相关的帖子列表页
![image](https://user-images.githubusercontent.com/100272100/209810480-5181fefb-2781-4348-af1f-8dda4060b558.png)

