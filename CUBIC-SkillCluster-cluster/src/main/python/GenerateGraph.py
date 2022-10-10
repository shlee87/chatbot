import DatabaseConnect as dbc
import networkx as nx
from networkx.readwrite import json_graph
# import matplotlib.pyplot as plt
from bitarray import bitarray
import json

DEBUG = True

# The idea is to make an undirected, weighted graph where skills are the nodes and people are the edges between skills. 
# This graph will assist in percent matching - especially in the case that someone can up-skill 

# function that finds all permutations of n choose k in a set of bits 
# copied from here: https://diego.assencio.com/?index=50ed9dcd9009dd70c3a2f8822271e4c7
def bitmasks(n,m):
    if m < n:
        if m > 0:
            for x in bitmasks(n-1,m-1):
                yield bitarray([1]) + x
            for x in bitmasks(n-1,m):
                yield bitarray([0]) + x
        else:
            yield n * bitarray('0')
    else:
        yield n * bitarray('1')

def strongestEdge(graph):

	dic = nx.get_edge_attributes(graph, "weight")
	strongest = ("", "", 0)
	# # print(dic)
	for node1, node2 in dic:
		if dic[node1, node2] > strongest[2]:
			strongest = (node1, node2, dic[node1, node2])

	return strongest

def generate(graph, EIDs, pairs):
	for selection in createNodes(graph, EIDs, pairs):
		createEdges(graph, selection)

def createNodes(graph, EIDs, pairs):

	for person in EIDs:
		
		if DEBUG:
			print(person);

		# keep track of the current selection of pairs (for this person only)
		selection = [];

		for pair in pairs:
			# breakout of this iteration if the person changes
			if (pair[0] != person[0]):
				continue

			# step 2
			# then add all of their skills into the graph as nodes. 
			if DEBUG:
				print(pair)
			selection.append(pair[1])
			graph.add_node(pair[1])

		if DEBUG:
			print(selection)
			print("\n")

		yield selection

def createEdges(graph, selection):

		# step 3
		# Then, all of those nodes will be given edges between them. 
		# If the skills already possess an edge between them, increase the weight. 
		# If there's only one skill that a person has, then no need to make an edge
		if len(selection) < 2:
			return

		edgeMask = []
		edgeList = []

		for b in bitmasks(len(selection), 2):
			edgeMask.append(b)

		for mask in edgeMask:
			
			tempzip = zip(mask, selection)
			thisEdge = []

			for bit, skill in tempzip:
				if bit == 1:
					thisEdge.append(skill)

			edgeList.append(thisEdge)

		if DEBUG:
			print(edgeList)

		# graph.add_edges_from(edgeList, weight=1.0)
		for edge in edgeList:
			if (graph.has_edge(edge[0], edge[1])):
				# increase weight of this edge
				try:
					graph.edges[edge[0], edge[1]]["weight"] = nx.get_edge_attributes(graph, "weight")[edge[0], edge[1]] + 1
				except KeyError:
					graph.edges[edge[1], edge[0]]["weight"] = nx.get_edge_attributes(graph, "weight")[edge[1], edge[0]] + 1
				except:
					print(f"Failed to increase edge weight for {edge}")
				# pass

			else:
				# otherwise, create the edge with weight 1
				graph.add_edge(edge[0], edge[1], weight=1)



		# repeat steps 2 and 3 until done


# function that saves the graph to a gexf file 
def saveGraph(graph):
	nx.write_gexf(graph, "graph.gexf")
	with open("graph_features.txt", "w") as file:
		tmp = strongestEdge(graph)
		edge = (tmp[0], tmp[1])
		weight = tmp[2]
		file.write(str(edge) + '\n')
		file.write(str(weight))

def saveJSON(graph):
	data = json_graph.node_link_data(sgraph)
	s = json.dumps(data)
	with open("graph.json", "w") as file:
		file.write(s)

# function that loads the graph from a gexf file
def loadGraph(path):
	return nx.read_gexf(path)

########
# MAIN #
########

if __name__ == "__main__":
	# for b in bitmasks(10, 2):
	# 	print(b.to01())
	# 	mylist = []
	# 	for wahwah in b:
	# 		mylist.append(wahwah)
	# 	print(mylist)

	# step 1 
	# Figure out who people are. 
	# I would like to start with a person, 
	limit = 1000;
	pairs = dbc.mysqlconnect(f"SELECT DISTINCT employee_id, worker_skill FROM nolaridc_v2.skills WHERE employee_id IN (SELECT employee_id FROM (SELECT DISTINCT employee_id FROM nolaridc_v2.employees LIMIT {limit}) x ) AND worker_skill IS NOT NULL AND NOT worker_skill = '' ORDER BY employee_id")
	EIDs = dbc.mysqlconnect(f"SELECT DISTINCT employee_id FROM nolaridc_v2.employees LIMIT {limit}")

	if DEBUG:
		print(pairs)
		print("\n\n")
		print(EIDs)
		print("\n\n")

	sgraph = nx.Graph()
	# sgraph = loadGraph("graph.gexf")

	generate(sgraph, EIDs, pairs)

	# print(nx.get_edge_attributes(sgraph, "weight")["Leadership Planning", "People Leadership"])
	# print(nx.get_edge_attributes(sgraph, "weight")["People Leadership", "Leadership Planning"])
	# print(nx.get_edge_attributes(sgraph, "weight"))
	dic = nx.get_edge_attributes(sgraph, "weight")
	# # print(dic)
	for node1, node2 in dic:
		if dic[node1, node2] > 6:
			print(f"found one at {node1} to {node2}")
			print(dic[node1, node2])

	saveGraph(sgraph)
	saveJSON(sgraph)
	


	# save the result as an image
	# this is actually very time-consuming, so dont use it when testing
	# nx.draw_kamada_kawai(sgraph, with_labels=False, font_size=6, node_size=3, width=0.5)
	# plt.savefig("graph.png")