import pymysql as psql
import os

def mysqlconnect(query):

	endpoint = os.environ['ENDPOINT']
	user = os.environ['USER']
	password = os.environ['PASSWORD']
	db = os.environ['DATABASE']

	# to connect to the sql db

	conn = psql.connect(
		host=endpoint,
		user=user,
		password=password,
		db=db,
		)

	cur = conn.cursor()
	cur.execute(query)
	output = cur.fetchall()
	# print(output)

	# close the connection
	conn.close()

	return output

# driver code
if __name__ == "__main__":
	output = mysqlconnect("SELECT DISTINCT employee_id, workers_skill FROM nolaridc.skills LIMIT 1000");
	print(output)