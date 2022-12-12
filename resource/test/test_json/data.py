import json

text = ''
with open('data.json') as f:
  text = f.read()

js = json.loads(text)

dump = json.dumps(js, ensure_ascii=False,sort_keys=True, indent=2, separators=(',', ': '))

print(dump)

with open('tmp.json', 'w', encoding='utf-8') as f:
  f.write(dump)
