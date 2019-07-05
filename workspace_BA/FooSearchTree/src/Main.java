import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
		// Parsing
		//Formula form = new Formula("vertex-cover.txt");
		//Formula form = new Formula("test-thingie.txt");
		Formula form = new Formula("doc-example.txt");
		// TODO add "reflexive" flag to relations (E)
		form.printFormula();
		System.out.println("SearchTree: " + form.searchTree(form.k_par, new ArrayList<String>()));
	}

}
