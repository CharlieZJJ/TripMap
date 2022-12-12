import json
import os
import time
import bcrypt
import random
import datetime

areas = []

for line in open('area.txt', encoding='utf-8'):
  areas.append(line)
  
areas = [line.strip("\n") for line in areas]

note_template   = 'note/result_note_{}{}.json'
list_template   = 'list/result_list_{}{}.json'
sql_template    = 'sql/{}.sql'
img_template    = 'http://sns-img-hw.xhscdn.com/{}'
key_template    =  '{}-{}'
post_template   = 'INSERT INTO post(post_collect_count, post_like_count, post_image_list, post_desc, post_title, post_publish_time) VALUES (0, 0, \'{}\', \'{}\', \'{}\', \'{}\');'
user_template   = 'INSERT INTO user(user_nickname, user_avatar, user_password, user_account, user_create_time, user_fan_count, user_follow_count, user_post_count, user_collect_post_count, user_collect_location_count) VALUES (\'{}\', \'{}\', \'{}\', \'{}\', \'{}\', 0, 0, 0, 0, 0);'

# def getNoteName(name, page):
#   return note_template.format(name, page)

# def getListName(name, page):
#   return list_template.format(name, page)

# def getSqlName(name):
#   return sql_template.format(name)

# def exist(name):
#   return os.path.exists(name)

# def getKeyName(name, id):
#   return key_template.format(name, id)

# def get_random_number_str(length):
#   num_str = ''.join(str(random.choice(range(10))) for _ in range(length))
#   return num_str

# def generate_random_str(randomlength=16):
#   random_str = ''
#   base_str = 'ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz,.'
#   length = len(base_str) - 1
#   for i in range(randomlength):
#       random_str += base_str[random.randint(0, length)]
#   tmp = bcrypt.hashpw(bytes(random_str, 'utf-8'), bcrypt.gensalt())
#   return str(tmp, 'utf-8')

# def randomtime(start = '2018-01-01 08:00:00', end = '2022-11-20 00:00:00',  frmt="%Y-%m-%d %H:%M:%S"):
#   stime = datetime.datetime.strptime(start, frmt)
#   etime = datetime.datetime.strptime(end, frmt)
#   time_datetime=random.random() * (etime - stime) + stime
#   time_str=time_datetime.strftime(frmt)
#   return time_str

# class User:
#   def __init__(self, spec):
#     self.id = spec['user']['id']
#     self.avatar = spec['user']['image']
#     self.nickname = spec['user']['nickname']
#     self.account = get_random_number_str(11)
#     self.password = generate_random_str()
#     self.create_time = randomtime()
  
#   def __str__(self):  # 其实一般可能都是这样简单用一下的
#     return 'id = ' + self.id + '\navatar = ' + self.avatar + '\nnickname = ' + self.nickname + '\naccount = ' + self.account + '\npassword = ' + self.password + '\ncreate_time = '+ self.create_time
  
#   def __hash__(self):
#     return hash(self.id)
  
#   def __eq__(self, other):
#     return self.id == other.id

#   def sql(self):
#     return user_template.format(self.nickname.replace('\'', '\'\''), self.avatar, self.password, self.account, self.create_time)


# class Post:
#   def __init__(self, note, spec):
#     self.id = note['note']['id']
#     self.publish_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(note['note']['timestamp']))
#     self.title = note['note']['title']
#     self.imgs = [img_template.format(img['trace_id']) for img in note['note']['images_list']]
#     self.desc = spec['note_list'][0]['desc']
#     # self.tags = spec['hash_tag']
#     self.head_tags = [tag['name'] for tag in spec['note_list'][0]['head_tags']]
#     self.topics = [topic['name'] for topic in spec['note_list'][0]['topics']]
  
#   def __str__(self):
#     return 'id = ' + self.id + '\npublish_time = ' + self.publish_time + '\ntitle = ' + self.title + '\nimgs = ' + ', '.join(self.imgs) + '\ndesc = ' + self.desc + '\nhead_tags = '+ ', '.join(self.head_tags) + '\ntopics = ' + ', '.join(self.topics)
  
#   def __hash__(self):
#     return hash(self.id)
  
#   def __eq__(self, other):
#     return self.id == other.id

#   def sql(self):
#     return post_template.format(', '.join(self.imgs), self.desc.replace('\'', '\'\''), self.title.replace('\'', '\'\''), self.publish_time)

# users = []
# posts = []

# for area in areas:
# # for area in areas[:1]:

#   for page in range(1, 20):
#   # for page in range(1, 2):
#     # 打开list文件
#     list_name = getListName(area, page)
#     if exist(list_name):
#       with open(list_name, 'r', encoding='utf-8') as list_file:
#         cur_list = json.load(list_file)
#       if cur_list['showapi_res_code'] != 0 or cur_list['showapi_res_body']['code'] != 0:
#         continue
#       listnotes = cur_list['showapi_res_body']['data']['items'] 
#       # 成功打开且可读取之后打开note文件
#       note_name = getNoteName(area, page)
#       # print(note_name)
#       if exist(note_name):
#           with open(note_name, 'r', encoding='utf-8') as note_file:
#             note_js = json.load(note_file)
#           for note in listnotes:
#             if 'note' in note.keys():
#               keyname = getKeyName(area, note['note']['id'])
#               if keyname in note_js.keys() and note_js[keyname]['showapi_res_code'] == 0 and len(note_js[keyname]['showapi_res_body']['data']) > 0:
#                 post = Post(note, note_js[keyname]['showapi_res_body']['data'][0])
#                 user = User(note_js[keyname]['showapi_res_body']['data'][0])
#                 posts.append(post)
#                 users.append(user)


# print("total posts: ")
# print(len(posts))
# posts = set(posts)
# print("after remove duplicated: ")
# print(len(posts))
# with open('sql/post.sql', 'a', encoding='utf-8') as sql_file:
#   for post in posts:
#     sql_file.write(post.sql())
#     sql_file.write('\n\n')

# print("total users: ")
# print(len(users))
# users = set(users)
# print("after remove duplicated: ")
# print(len(users))
# with open('sql/user.sql', 'a', encoding = 'utf-8') as user_sql:
#   for user in users:
#     user_sql.write(user.sql())
#     user_sql.write('\n')

# user: id nickname avatar
# post: id, title, desc, img
# place: id, province, area, address
# topic: id, name



# user  publish  post   发布
# user  like     post   点赞
# user  collect  post   收藏
# user  follow   user   关注
# user  store    place  收藏地点
# psot  suggest  place  推荐
# post  belong   topic  属于某个话题

# def create_
