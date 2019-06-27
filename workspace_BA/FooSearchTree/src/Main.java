import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

	public static void main(String[] args) {
		parseFormula("vertex-cover.txt");
	}

	private static void parseFormula(String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = "";
			
			// Name of the formula
			line = br.readLine();
			String name = line;
			// Universe
			line = br.readLine();
			String[] universe = line.split(",");
			// Signature
			line = br.readLine();
			String[] relations = line.split(";");
			for(String s : relations) {
				// TODO extract information about each relation
				s.indexOf("{");
				s.indexOf("}");
			}
			// Relation S which will contain the solution
			line = br.readLine();
			// TODO Create S
			// Parameter k
			line = br.readLine();
			int k = Integer.parseInt(line);
			// Bound variables
			line = br.readLine();
			String[] bound_vars = line.split(",");
			// Formula
			line = br.readLine();
			// TODO extract formula
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
