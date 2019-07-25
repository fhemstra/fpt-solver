import java.util.ArrayList;

public class Sunflower {

	ArrayList<Tuple> petals = new ArrayList<Tuple>();
	ArrayList<Integer> core;

	public Sunflower(ArrayList<Tuple> petals, ArrayList<Integer> core) {
		this.petals = petals;
		this.core = core;
	}

	public String toOutputString() {
		String res = "Petals: ";
		for(Tuple t : petals) {
			res += t.toOutputString(true) + " ";
		}
		res += "\nCore: ";
		for(int c : core) {
			res += c + " ";
		}
		return res;
	}

}
