import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

	// TODO findSunflower(Hypergraph h), kernelize()

	/**
	 * Return a String to output for this hypergraph. The node 0 is a placeholder.
	 */
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
			res = (i < edges.size() - 1) ? res + "," : res;
		}
		res += "}\n";
		res += "mapping:\n";
		// Loop through all mappings from nodes to edges
		Iterator<Entry<Integer, ArrayList<Tuple>>> it = node_to_edges.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, ArrayList<Tuple>> e = it.next();
			res += " " + e.getKey() + " -> ";
			// If a node is isolated
			if(e.getValue().size() == 0) res += "iso";
			// Loop through all edges the entry e is contained in
			for (int i = 0; i < e.getValue().size(); i++) {
				res += e.getValue().get(i).toOutputString();
				if(i < e.getValue().size() - 1) res += ",";
			}
			if(it.hasNext()) res += "\n";
		}
		return res;
	}
}
