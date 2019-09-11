import java.util.ArrayList;

public class Sunflower {

	ArrayList<Tuple> petals = new ArrayList<Tuple>();
	ArrayList<Integer> core;

	/**
	 * Constructs a Sunflower consisting of the specified petals and the given core.
	 */
	public Sunflower(ArrayList<Tuple> petals, ArrayList<Integer> core) {
		this.petals = petals;
		this.core = core;
	}

	/**
	 * Returns a String representing this Sunflower for debugging.
	 */
	public String toOutputString() {
		String res = "Petals: ";
		for(Tuple t : petals) {
			res += t.toOutputString(true) + " ";
		}
		res += "\nCore: ";
		if(core.isEmpty()) res += "empty";
		for(int c : core) {
			res += c + " ";
		}
		return res;
	}

}
