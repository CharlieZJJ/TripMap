from ShowapiRequest import ShowapiRequest
import json

app_id = "1211954"
app_secret = "617d85fd64e7437a812bd1e8e07c9f01"

note_url = "http://route.showapi.com/2893-2"
list_url = "http://route.showapi.com/2893-5"

areas = []

for line in open('area.txt', encoding='utf-8'):
  areas.append(line)
  
areas = [line.strip("\n") for line in areas]

areas = areas[:14]
# areas = areas[14:28]
# areas = areas[28:42]

log = open('log.txt', 'a', encoding='utf-8')

for area in areas[5:]:
  for page in range(1, 20):
    r = ShowapiRequest(list_url, app_id, app_secret)
    r.addBodyPara("keyword", area)
    r.addBodyPara("page", str(page))
    r.addBodyPara("sort","general")
    res = r.post()
    text = res.text
    js = json.loads(text)
    list_dict_dump = json.dumps(js, ensure_ascii=False, sort_keys=True, indent=2, separators=(',', ': '))
    with open('list/result_list_'+ area + str(page) +'.json', 'w', encoding='utf-8') as dump_file:
      dump_file.write(list_dict_dump)
    try:
      note_list = js['showapi_res_body']['data']['items']
    except BaseException:
      print('list/result_list_'+ area + str(page) +'.json', file=log)
      continue
    note_dict = {}
    for note in note_list:
      try:
        note_id = note['note']['id']  
      except BaseException:
        print('note/result_note_'+ area + str(page) +'.json', file=log)
        continue
      n = ShowapiRequest(note_url, app_id, app_secret)
      n.addBodyPara('note_id', note_id)
      nres = n.post()
      ntext = nres.text
      njs = json.loads(ntext)
      note_dict[area + "-" + note_id] = njs
    note_dict_dump = json.dumps(note_dict, ensure_ascii=False, sort_keys=True, indent=2, separators=(',', ': '))
    with open('note/result_note_'+ area + str(page) +'.json', 'w', encoding='utf-8') as dump_file:
      dump_file.write(note_dict_dump)