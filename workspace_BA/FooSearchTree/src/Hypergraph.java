import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Hypergraph {
	int[] nodes;
	// HashMap of nodes to global edges
	ArrayList<String> edges = new ArrayList<String>();
	HashMap<Integer, String[]> node_to_edges = new HashMap<Integer, String[]>();
	
	public Hypergraph(int[] nodes, ArrayList<String> edges, HashMap<Integer, String[]> node_to_edges) {
		this.nodes = nodes;
		this.edges = edges;
		this.node_to_edges = node_to_edges;
	}
	
	public String toOutputString() {
		String res = "nodes: (";
		for(int i = 0; i < nodes.length; i++) {
			if(i < nodes.length-1) res += nodes[i] + ",";
			else res += nodes[i] + ")\n";
		}
		res += "edges:\n";
		for(Entry<Integer, String[]> e : node_to_edges.entrySet()) {
			res += " " + e.getKey() + " -> ";
			for(String s : e.getValue()) {
				res += s + " ";
			}
			res += "\n";
		}
		return res;
	}
}
