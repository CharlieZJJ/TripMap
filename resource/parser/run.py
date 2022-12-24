from py2neo import *

graph = Graph('http://110.40.174.234:7474', name='neo4j', password='123456')

post_cypher = []
with open('cql/post.txt', 'r', encoding='utf-8') as f:
  line = f.readline()
  while line:
    line = line.strip('\n')
    while not line.endswith(';'):
      tmp = f.readline()
      if tmp:
        tmp = tmp.strip('\n')
        line += tmp
      else:
        break
    if line.endswith(';'):
      post_cypher.append(line)
    line = f.readline()

for ty in post_cypher:
  graph.run(ty)


for line in open('cql/user.txt', 'r', encoding='utf-8'):
  graph.run(line)
for line in open('cql/topic.txt', 'r', encoding='utf-8'):
  graph.run(line)
for line in open('cql/place.txt', 'r', encoding='utf-8'):
  graph.run(line)
for line in open('cql/suggest.txt', 'r', encoding='utf-8'):
  graph.run(line)
for line in open('cql/store.txt', 'r', encoding='utf-8'):
  graph.run(line)
for line in open('cql/publish.txt', 'r', encoding='utf-8'):
  graph.run(line)
for line in open('cql/like.txt', 'r', encoding='utf-8'):
  graph.run(line)
for line in open('cql/follow.txt', 'r', encoding='utf-8'):
  graph.run(line)
for line in open('cql/collect.txt', 'r', encoding='utf-8'):
  graph.run(line)
for line in open('cql/belong.txt', 'r', encoding='utf-8'):
  graph.run(line)
