import java.util.HashSet;
import java.util.Iterator;

/**
 * Contains one Relation with its name, arity and set of elements.
 */
public class Relation {
	String name;
	int arity;
	HashSet<Tuple> elements;

	/**
	 * Constructs a Relation consisting of an identifier, its arity (number of elements per Tuple) and a set of Tuples contained in this Relation.
	 */
	public Relation(String s, int i, HashSet<Tuple> hs) {
		name = s;
		arity = i;
		elements = hs;
	}

	/**
	 * Returns a String which represents this Relation for debugging.
	 */
	public String toOutputString() {
		String res = "";
		res += name + arity + " = ";
		if (elements.isEmpty()) {
			res += "emptyset";
		} else {
			res += "{";
			Iterator<Tuple> it = elements.iterator();
			while (it.hasNext()) {
				res += it.next().toOutputString(true);
				if (it.hasNext())
					res += ",";
			}
			res += "}";
		}
		return res;
	}

	/**
	 * Returns the number of Tuples this Relation holds.
	 */
	public int size() {
		if (elements != null)
			return elements.size();
		else
			return -1;
	}

}
