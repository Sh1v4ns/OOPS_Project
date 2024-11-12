# OOPS_Project

Author name : Shivansh Shukla
Author Registration number : 23BDS054


This code is designed to detect arbitrage opportunities across various currency exchange rates. It reads exchange rates from a CSV file, builds a graph with these rates, and uses the Bellman-Ford algorithm to find negative weight cycles, which indicate potential arbitrage opportunities.

Here’s a step-by-step breakdown:

## 1. Classes and Methods
DirectedEdge: This represents a directed edge in a graph, storing information about the source (from), destination (to), and weight of the edge.

EdgeWeightedDigraph: This class represents a directed, weighted graph with:

V: the number of vertices (currencies).
adj: an adjacency list where each vertex has a list of outgoing edges.
addEdge(): a method to add an edge to the graph, which includes a bounds check for edge validity.
BellmanFordSP: This class performs the Bellman-Ford algorithm to detect negative weight cycles:

distTo: stores the shortest path distance to each vertex from the source.
edgeTo: stores the last edge on the shortest path to each vertex.
findNegativeCycle(): detects cycles with negative weights. If such a cycle is found, it’s stored in cycle to represent a possible arbitrage chain.
Key Concept: A negative cycle in the graph implies that it's possible to start with some money and end up with more money after a series of currency exchanges, creating an arbitrage opportunity.
## 2. Main Logic in Arbitrage Class
Reading and Initializing:

The code reads exchange rates from a CSV file.
It expects the first row to contain currency codes (names) and each subsequent row to represent the exchange rates from one currency to others.
V represents the number of currencies, derived from the header length.
Constructing the Graph:

For each exchange rate, the program adds a DirectedEdge from one currency to another, with a weight equal to the negative logarithm of the exchange rate. The negative log transformation turns a multiplication of rates into a summation, making it compatible with the Bellman-Ford algorithm.
Finding Arbitrage Opportunities:

After the graph is constructed, the program runs the Bellman-Ford algorithm starting from an arbitrary currency (index 0).
If a negative cycle is detected, the code interprets this cycle as an arbitrage opportunity.
It prints the exchange sequence in this cycle, showing how an initial stake grows as it moves through the cycle.
## 3. Output
If an arbitrage opportunity exists, the code prints the steps of the currency exchanges and the evolving value of the initial investment (starting with $1,000).
If no arbitrage opportunity is found, it simply outputs "No arbitrage opportunity."
Summary
In essence, this code detects arbitrage opportunities by converting exchange rates into a graph, applying the Bellman-Ford algorithm to detect negative cycles, and then reporting the sequence of exchanges (if any) that would lead to a profit. This process is highly relevant in financial markets where exchange rate discrepancies across multiple currencies can lead to profit if acted on quickly
