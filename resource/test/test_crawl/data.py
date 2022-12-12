from ShowapiRequest import ShowapiRequest
import json
r = ShowapiRequest("http://route.showapi.com/2893-5","1211954","617d85fd64e7437a812bd1e8e07c9f01" )
r.addBodyPara("keyword","上海")
r.addBodyPara("page","1")
r.addBodyPara("sort","general")
res = r.post()
js = json.loads(res.text)

dump = json.dumps(js, ensure_ascii=False,sort_keys=True, indent=2, separators=(',', ': '))

print(dump)

with open('list.json', 'w', encoding='utf-8') as f:
  f.write(dump)


r = ShowapiRequest("http://route.showapi.com/2893-2","1211954","617d85fd64e7437a812bd1e8e07c9f01" )
r.addBodyPara("note_id","62d11f28000000002103efeb")
res = r.post()
js = json.loads(res.text)

dump = json.dumps(js, ensure_ascii=False,sort_keys=True, indent=2, separators=(',', ': '))

print(dump)

with open('data.json', 'w', encoding='utf-8') as f:
  f.write(dump)
