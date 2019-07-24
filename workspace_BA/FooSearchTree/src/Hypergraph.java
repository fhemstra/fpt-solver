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
	
	//TODO findSunflower(Hypergraph h), kernelize()

	public String toOutputString() {
		String res = "nodes: {";
		for (int i = 0; i < nodes.length; i++) {
			res += nodes[i];
			if (i < nodes.length - 1)
				res += ",";
			else
				res += "}\n";
		}
		res += "edges: {";
		for (int i = 0; i < edges.size(); i++) {
			Tuple t = edges.get(i);
			res += t.toOutputString();
			res = (i < edges.size()-1) ? res + "," : res;
		}
		res += "}\n";
		res += "mapping:\n";
		// Loop through all mappings from nodes to edges
		for (Entry<Integer, ArrayList<Tuple>> e : node_to_edges.entrySet()) {
			res += " " + e.getKey() + " -> ";
			// Loop through all edges the entry e is contained in
			for (int i = 0; i < e.getValue().size(); i++) {
				res += "(";
				// Loop through all nodes that are in an edge
				for (int j = 0; j < e.getValue().get(i).elements.length; j++) {
					res += Integer.toString(e.getValue().get(i).elements[j]);
					if (j < e.getValue().get(i).elements.length - 1)
						res += "|";
				}
				res += "),";
			}
			res += "\n"; // TODO fix this for last iteration
		}
		return res;
	}
}
