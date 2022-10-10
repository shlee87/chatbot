import json
import os

import DatabaseConnect as dbc
import GenerateGraph as gg
import networkx as nx
import boto3 as bt
from ast import literal_eval as makeTuple
from math import exp
# import matplotlib.pyplot as plt

createNewGraph = False

# find the clustering coefficient for a particular node 
# def localClusteringCoefficient(graph, v):

# 	# find the neighbors of v (the node being examined)
# 	N = findNeighbors(v)

def normalize(large, selection):

	return selection / large

def logistic(x):

	return (1 / (1 + exp(-10*(x - 0.4))))
	# return exp(x)

def calculateNeighborPercents(selectedNode, graph, features, event):

	# Thought Process: 
	# Step 1: Find the neighbors of the desired skill 
	# Step 2: Use the strongest weight in the graph to determine how to scale weights 
	# Step 2.1: Maybe use strongest weight among the neighbors? 
	# Step 3: Use the scaled weight as input to a logarithmic function
	# Award partial points back based on the output of the logarithmic function to those who possess 
	# these neighboring skills 

	allNeighbors = nx.neighbors(graph, selectedNode)

	# Find only the desired neighbors
	neighbors = [skill for skill in allNeighbors if skill in event["requiredSkills"]]

	weights = nx.get_edge_attributes(graph, "weight")
	normalx = []
	normaly = []
	names = []
	for x in neighbors:
		print(x)
		try:
			# print(weights[selectedNode, x])
			tmp = normalize(features[1], weights[selectedNode, x])
			normalx.append(tmp)
		except:
			# print(weights[x, selectedNode])
			tmp = normalize(features[1], weights[x, selectedNode])
			normalx.append(tmp)

		
		normaly.append(logistic(tmp))
		names.append(x)

	return { "neighboringSkills": names, "neighboringPercent": normaly }

########
# MAIN #
########

def lambda_handler(event, context):

	# if a new graph is required
	if createNewGraph:
		# queries 
		limit = 100;
		pairs = dbc.mysqlconnect(f"SELECT DISTINCT employee_id, workers_skill FROM nolaridc.skills WHERE employee_id IN (SELECT employee_id FROM (SELECT DISTINCT employee_id FROM nolaridc.employees LIMIT {limit}) x ) AND workers_skill IS NOT NULL AND NOT workers_skill = '' ORDER BY employee_id")
		EIDs = dbc.mysqlconnect(f"SELECT DISTINCT employee_id FROM nolaridc.employees LIMIT {limit}")

		sgraph = nx.Graph()

		gg.generate(sgraph, EIDs, pairs)

		gg.saveGraph(sgraph)


	# load a saved graph
	else: 


		# try to connect to s3 for sgraph
		s3 = bt.client('s3')
		bucket = os.environ['BUCKET']
		folder = os.environ['FOLDER'] + "/"
		graphKey = os.environ['GRAPHKEY']
		featuresKey = os.environ['FEATURESKEY']
		
		try:
			
			data = s3.get_object(Bucket=bucket, Key=(folder + graphKey))
			txt = s3.get_object(Bucket=bucket, Key=(folder + featuresKey))
			data_body = data['Body'].read().decode('utf-8')
			txt_body = txt['Body'].read().decode('utf-8')
		
			
		except Exception as e:
			print(e)
			raise e
			
		

		sgraph = nx.node_link_graph(json.loads(data_body))
		raw_features = txt_body.split('\r\n')
	
		features = []
		for i in raw_features:
			# if i != "":
			tmp = makeTuple(i)
			features.append(tmp)

		# print(features)

		requestedSkillDict = {}

		for selectedNode in event["requiredSkills"]:
			
			details = calculateNeighborPercents(selectedNode, sgraph, features, event)
			requestedSkillDict[ f"{selectedNode}" ] = details 
			
		
		
		return {
			
			"requestedSkillDict": requestedSkillDict

		}

