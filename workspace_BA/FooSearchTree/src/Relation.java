import java.util.HashSet;
import java.util.Iterator;

/**
 * Contains one Relation with its name, arity and set of elements.
 */
public class Relation {
	String name;
	int arity;
	HashSet<Tuple> elements;

	public Relation(String s, int i, HashSet<Tuple> hs) {
		name = s;
		arity = i;
		elements = hs;
	}

	public String toOutputString() {
		String res = "";
		res += name + arity + " = ";
		if (elements.isEmpty()) {
			res += "emptyset";
		} else {
			res += "{";
			Iterator<Tuple> it = elements.iterator();
			while (it.hasNext()) {
				res += it.next().toOutputString();
				if (it.hasNext())
					res += ",";
			}
			res += "}";
		}
		return res;
	}

	/**
	 * Returns the number of elements the Relation has.
	 */
	public int size() {
		if (elements != null)
			return elements.size();
		else
			return -1;
	}

}
