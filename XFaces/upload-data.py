import couchdb
import csv
couch = couchdb.Server("https://user:pass@id-bluemix.cloudant.com")
db = couch['faces100k']

with open('LDAP100k.csv','U') as csvfile:
  reader = csv.DictReader(csvfile)
  for record in reader:
    db.save(record)
