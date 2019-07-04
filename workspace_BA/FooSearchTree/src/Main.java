import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
		// Parsing
		Formula vc = new Formula("vertex-cover.txt");
		Formula test = new Formula("test-thingie.txt");
		test.printFormula();

		// Testing
		System.out.println("Test assignment:");
		HashMap<String, String> assignment = new HashMap<>();
		assignment.put("x", "v1");
		assignment.put("y", "v2");
		for (String[] s : test.clauses) {
			System.out.println(test.checkClause(s, assignment));
		}
		System.out.println("SearchTree: " + test.searchTree(test.k_par));
	}

}
