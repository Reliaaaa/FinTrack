import json,re,sqlite3
from pathlib import Path
root=Path(__file__).resolve().parents[1]
schema=json.loads((root/'app/schemas/id.fintrack.app.data.AppDatabase/1.json').read_text())['database']
db=sqlite3.connect(':memory:')
for entity in schema['entities']:
 db.execute(entity['createSql'].replace('${TABLE_NAME}',entity['tableName']))
 for index in entity['indices']: db.execute(index['createSql'].replace('${TABLE_NAME}',entity['tableName']))
db.execute("INSERT INTO obligations VALUES (1,'Tagihan','CICILAN',100,1,2,'2026-10')")
for sql in re.findall(r'db.execSQL\("(.*?)"\)',(root/'app/src/main/java/id/fintrack/app/data/Migrations.kt').read_text()): db.execute(sql)
assert db.execute('SELECT * FROM bill_payments').fetchall()==[(1,'2026-10',None,'LEGACY_MARKER')]
assert db.execute('SELECT remaining FROM obligations').fetchone()==(2,)
assert db.execute('SELECT COUNT(*) FROM transactions').fetchone()==(0,)
try: db.execute("INSERT INTO bill_payments VALUES(1,'2026-10',123,'CREATED')")
except sqlite3.IntegrityError: pass
else: raise AssertionError('duplicate period accepted')
db.execute("INSERT INTO bill_payments VALUES(1,'2026-11',123,'CREATED')")
try: db.execute("INSERT INTO bill_payments VALUES(2,'2026-11',123,'LINKED')")
except sqlite3.IntegrityError: pass
else: raise AssertionError('duplicate transaction accepted')
print('PASS: v1 schema migration, legacy marker, no debit, unchanged remaining, unique period and transaction')
