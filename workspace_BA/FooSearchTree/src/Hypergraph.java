import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Hypergraph {
	int[] nodes;
	// A hyperedge is a Array of nodes (int)
	ArrayList<Tuple> edges = new ArrayList<Tuple>();
	// HashMap of nodes to global hyperedges
	HashMap<Integer, ArrayList<Tuple>> node_to_edges = new HashMap<Integer, ArrayList<Tuple>>();
	
	// TODO Change Integer to int

	public Hypergraph(int[] nodes, ArrayList<Tuple> edges, HashMap<Integer, ArrayList<Tuple>> node_to_edges) {
		this.nodes = nodes;
		this.edges = edges;
		this.node_to_edges = node_to_edges;
	}

	public String toOutputString() {
		String res = "Hypergraph\n" + "nodes: (";
		for (int i = 0; i < nodes.length; i++) {
			res += "v" + nodes[i];
			if (i < nodes.length - 1)
				res += ",";
			else
				res += ")\n";
		}
		res += "edges:\n";
		// Loop through all mappings from nodes to edges
		for (Entry<Integer, ArrayList<Tuple>> e : node_to_edges.entrySet()) {
			res += " v" + e.getKey() + " -> ";
			// Loop through all edges the entry e is contained in
			for (int i = 0; i < e.getValue().size(); i++) {
				res += "{";
				// Loop through all nodes that are in an edge
				for (int j = 0; j < e.getValue().get(i).elements.length; j++) {
					res += Integer.toString(e.getValue().get(i).elements[j]);
					if (j < e.getValue().get(i).elements.length - 1)
						res += ",";
				}
				res += "}; ";
			}
			res += "\n";
		}
		return res;
	}
}
