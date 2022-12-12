# from py2neo import *

# graph = Graph('http://127.0.0.1:7474', name='neo4j', password='123456')

# for line in open('cql/suggest.txt', 'r', encoding='utf-8'):
#   graph.run(line)
# for line in open('cql/belong.txt', 'r', encoding='utf-8'):
#   graph.run(line)

with open('sql/topic.sql', 'w', encoding='utf-8') as tf:
  for line in open('cql/topic.txt', 'r', encoding='utf-8'):
    topic_id = line[line.find('topic_id:') + 9 : line.find(',')]
    topic_name = line[line.find('topic_name:') + 11 : line.find('"}')+1]
    tf.write('INSERT INTO topic VALUES({}, {});\n'.format(topic_id, topic_name))

with open('sql/place.sql', 'w', encoding='utf-8') as tf:
  for line in open('cql/place.txt', 'r', encoding='utf-8'):
    place_id = line[line.find('place_id:') + 9 : line.find(', place_province')]
    place_province = line[line.find('place_province:') + 15 : line.find(', place_area')]
    place_area = line[line.find('place_area:') + 11 : line.find(', place_address')]
    place_address = line[line.find('place_address:') + 14 : line.find('"}') + 1]
    tf.write('INSERT INTO place VALUES({}, {}, {}, {});\n'.format(place_id, place_province, place_area, place_address))