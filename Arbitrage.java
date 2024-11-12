import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DirectedEdge {
    private final int from;
    private final int to;
    private final double weight;

    public DirectedEdge(int from, int to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public int from() { return from; }
    public int to() { return to; }
    public double weight() { return weight; }
}

class EdgeWeightedDigraph {
    private final int V;
    private final List<List<DirectedEdge>> adj;

    public EdgeWeightedDigraph(int V) {
        this.V = V;
        adj = new ArrayList<>(V);
        for (int v = 0; v < V; v++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(DirectedEdge e) {
        adj.get(e.from()).add(e);
    }

    public int V() { return V; }
    public Iterable<DirectedEdge> adj(int v) { return adj.get(v); }
}

class BellmanFordSP {
    private final double[] distTo;
    private final DirectedEdge[] edgeTo;
    private boolean hasNegativeCycle;
    private List<DirectedEdge> cycle;

    public BellmanFordSP(EdgeWeightedDigraph G, int s) {
        distTo = new double[G.V()];
        edgeTo = new DirectedEdge[G.V()];

        for (int v = 0; v < G.V(); v++) {
            distTo[v] = Double.POSITIVE_INFINITY;
        }
        distTo[s] = 0.0;

        for (int pass = 0; pass < G.V(); pass++) {
            for (int v = 0; v < G.V(); v++) {
                for (DirectedEdge e : G.adj(v)) {
                    relax(e);
                }
            }
        }

        findNegativeCycle(G);
    }

    private void relax(DirectedEdge e) {
        int v = e.from(), w = e.to();
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
        }
    }

    private void findNegativeCycle(EdgeWeightedDigraph G) {
        for (int v = 0; v < G.V(); v++) {
            for (DirectedEdge e : G.adj(v)) {
                if (distTo[e.to()] > distTo[e.from()] + e.weight()) {
                    hasNegativeCycle = true;
                    cycle = new ArrayList<>();
                    DirectedEdge x = e;
                    do {
                        cycle.add(x);
                        x = edgeTo[x.from()];
                    } while (x != null && !cycle.contains(x));  // Detect the cycle
                    return;
                }
            }
        }
    }

    public boolean hasNegativeCycle() { return hasNegativeCycle; }
    public Iterable<DirectedEdge> negativeCycle() { return cycle; }
}

public class Arbitrage {
    public static void main(String[] args) {
        String fileName = "D:\\Arbitrage.csv"; // Update the path if necessary

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.out.println("File is empty or missing data.");
                return;
            }

            String[] header = headerLine.split(",");
            int V = header.length - 1;  // Number of currencies
            String[] name = new String[V];
            System.arraycopy(header, 1, name, 0, V);  // Currency names are in header

            EdgeWeightedDigraph G = new EdgeWeightedDigraph(V);

            // Reading the CSV data and creating edges
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                for (int v = 0; v < V; v++) {
                    String rateStr = tokens[v + 1];
                    if (rateStr.equals("N/A")) {
                        continue;
                    }
                    try {
                        double rate = Double.parseDouble(rateStr);
                        for (int w = 0; w < V; w++) {
                            if (v != w) {
                                DirectedEdge e = new DirectedEdge(v, w, -Math.log(rate));  // Use negative log of the exchange rate
                                G.addEdge(e);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid rate format in line: " + line);
                    }
                }
            }

            BellmanFordSP spt = new BellmanFordSP(G, 0);
            if (spt.hasNegativeCycle()) {
                System.out.println("Arbitrage opportunity detected:");

                // Find the negative cycle and calculate the profit
                double stake = 1000.0;
                List<DirectedEdge> cycle = (List<DirectedEdge>) spt.negativeCycle();

                // Step 1: Print the cycle of conversions
                DirectedEdge firstEdge = cycle.get(0);
                System.out.printf("%10.5f %s ", stake, name[firstEdge.from()]);
                stake *= Math.exp(-firstEdge.weight());  // Apply the exchange rate
                System.out.printf("= %10.5f %s\n", stake, name[firstEdge.to()]);

                // Step 2: Continue printing the cycle
                for (int i = 1; i < cycle.size(); i++) {
                    DirectedEdge e = cycle.get(i);
                    System.out.printf("%10.5f %s ", stake, name[e.from()]);
                    stake *= Math.exp(-e.weight());
                    System.out.printf("= %10.5f %s\n", stake, name[e.to()]);
                }

                // Step 3: Close the loop and show the final return to USD (if starting from USD)
                DirectedEdge lastEdge = cycle.get(cycle.size() - 1);
                stake *= Math.exp(-lastEdge.weight());  // Convert back to the starting currency (USD)
                System.out.printf("%10.5f %s = %10.5f %s\n", stake, name[lastEdge.to()], stake, name[firstEdge.from()]);
            } else {
                System.out.println("No arbitrage opportunity");
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
