## 数据库

### 选择

现在市面上很多产品都能为用户推荐一些娱乐旅行的帖子，或者推荐一些地点。但是没有一个把他们结合起来的app。例如小红书可以给用户推荐帖子，但却没有地点的推荐，而大众点评能推荐地点却不能推荐帖子。因此我们决定做一个这样的app出来。首先，考虑到我们需要有帖子，有地点，有用户，而且他们之间有着非常复杂的关系，用传统的MySQL等关系数据库可能会存储很多的冗余数据，同时因为需要join的原因，效率也会受到影响（事实证明，关系的数量至少是帖子等节点的数量的十几倍），因此我们选用了Neo4j来作为我们的数据库。Neo4j是一个图数据库，能帮助用户更好的管理 关系 > 节点的这种情况，同时还支持多级递归的查询，这正是我们所需要的。同样又考虑到某些内容（比如帖子的正文或者其他的一些详细信息）没有必要保存在Neo4j数据库之中，因为他们对于关系和节点来说并不重要，我认为Neo4j应该保存的是节点之间关系，而非大段大段的字符串。因此我们同样使用了MySQL来做这个事情。为了增加推荐的效率，我们还选择了Redis作为一个缓存，用来缓存给用户的推荐，并在用户做出一些行为后进行更新。

### 访问

对于Neo4j，我们使用了Spring Data Neo4j来进行管理和访问，多是通过Cypher语句来进行查询和更新。

对于MySQL，我们使用了Mybatis-plus来访问和管理，通过它封装好的查询、更新接口进行查询和更新。

对于Redis，我们使用了Spring Data Redis来进行管理和访问，将redis的命令封装起来，从而使用他们来查询和更新Redis

### 跨库

redis之中其实保存的只有推荐的内容，并不涉及跨库访问

MySQL和Neo4j通过相同的节点（或实体）共用一个id来实现互相访问。

### 推荐

推荐算法使用的是协同过滤算法。我们主要根据用户关注的人来为用户进行推荐。首先我们会构建一个用户、地点表。每一项是用户对该地点的打分。这个打分是通过该用户的一些点赞、收藏等行为来进行的。在构建好这个表之后，我们计算出地点的得分，从而实现为用户推荐的功能。

（这一段你看看加不加）虽然实现了这一推荐算法，但是由于本身是第一次接触推荐，对于深度学习也不是很了解，没有能很完美的实现这个协同过滤算法，性能不是很理想。但是的确验证了我们一开始选择Neo4j的正确性。

### 实现

#### Neo4j

##### 节点

user

|     属性      |          注释           |
| :-----------: | :---------------------: |
|  user_avatar  |        用户头像         |
|    user_id    | 用户id，和mysql之中相同 |
| user_nickname |        用户昵称         |

place

|      属性      |          注释           |
| :------------: | :---------------------: |
| place_address  |         地点名          |
|   place_area   |       地点所在市        |
| place_province |       地点所在省        |
|    place_id    | 地点id，和MySQL之中相同 |

post

|    属性    |                 注释                  |
| :--------: | :-----------------------------------: |
| post_desc  | 帖子描述（MySQL存放的正文的前50个字） |
|  post_id   |                帖子id                 |
|  post_img  | 帖子图片（MySQL存放的图片集的第一个） |
| post_title |               帖子标题                |

post

|    属性    |  注释  |
| :--------: | :----: |
|  topic_id  | 话题id |
| topic_name | 话题名 |

##### 关系

| 关系名  | 起始节点类型 | 终止节点类型 |      注释      |
| :-----: | :----------: | :----------: | :------------: |
| PUBLISH |     user     |     post     | 用户 发表 帖子 |
|  LIKE   |     user     |     post     | 用户 点赞 帖子 |
| COLLECT |     user     |     post     | 用户 收藏 帖子 |
| FOLLOW  |     user     |     user     | 用户 关注 用户 |
|  STORE  |     user     |    place     | 用户 收藏 地点 |
| SUGGEST |     post     |    place     | 帖子 推荐 地点 |
| BELONG  |     post     |    topic     | 帖子 属于 话题 |

#### MySQL

##### user

|            字段             |   类型   |        注释         |
| :-------------------------: | :------: | :-----------------: |
|           user_id           |   int    | 用户id，和neo4j相同 |
|        user_nickname        | varchar  |      用户昵称       |
|         user_avatar         | varchar  |    用户头像链接     |
|        user_password        | varchar  |      用户密码       |
|        user_account         | varchar  |      用户账号       |
|      user_create_time       | datetime |    用户注册时间     |
|       user_fan_count        |   int    |     用户粉丝数      |
|      user_follow_count      |   int    |     用户关注数      |
|       user_post_count       |   int    |  用户发表的帖子数   |
|   user_collect_post_count   |   int    |  用户收藏的帖子数   |
| user_collect_location_count |   int    |  用户收藏的地点数   |

##### place

|      字段      |  类型   |        注释         |
| :------------: | :-----: | :-----------------: |
|    place_id    |   int   | 地点id，和neo4j相同 |
| place_province | varchar |     地点所在省      |
|   place_area   | varchar |     地点所在市      |
| place_address  | varchar |       地点名        |

##### post

|        字段        |  类型   |        注释         |
| :----------------: | :-----: | :-----------------: |
|      post_id       |   int   | 地点id，和neo4j相同 |
| post_publish_time  | varchar |     地点所在省      |
|  post_image_list   | varchar |     地点所在市      |
|     post_desc      | varchar |       地点名        |
| post_collect_count |   int   |  帖子被收藏的数量   |
|  post_like_count   |   int   |  帖子被点赞的数量   |
|     post_title     | varchar |     帖子的标题      |

##### topic

|    字段    |  类型   |        注释         |
| :--------: | :-----: | :-----------------: |
|  topic_id  |   int   | 话题id，和neo4j相同 |
| topic_name | varchar |       话题名        |

##### 

