import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
		// Collect and parse all files
		ArrayList<Formula> formulas = new ArrayList<Formula>();
		File folder = new File("instances");
		File[] listOfFiles = folder.listFiles();
		for (File f: listOfFiles) {
		  if (f.isFile()) {
		    formulas.add(new Formula(f.getAbsolutePath()));
		  }
		}
		
		// Solve each File
		for(Formula form : formulas) {
			form.printFormula();
			System.out.println("SearchTree: " + form.searchTree(form.k_par, new ArrayList<String>()));
			System.out.println("-------");
			System.out.println("Reduction: ");
			System.out.println("~~~~~~~~~~~~~~~~");
			form.saveToFile();
		}
	}

}
