import couchdbkit
import csv
couch = couchdbkit.Server("https://user:pass@id-bluemix.cloudant.com")
db = couch.get_or_create_db('faces-real')

with open('LDAP100k.csv','U') as csvfile:
  reader = csv.DictReader(csvfile)
  records = []
  i = 0
  for record in reader:
    records.append(record)
    if i != 0 and i%100 == 0:
      print('writing records through: ' + str(i))
      db.bulk_save(records)
      records = []
    i += 1
  '''save the leftovers'''
  db.bulk_save(records)
