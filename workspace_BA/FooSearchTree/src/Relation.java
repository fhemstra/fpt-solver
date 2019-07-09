import java.util.HashSet;
import java.util.Iterator;

/**
 * Contains one Relation with its name, arity and set of elements.
 */
public class Relation {
	String name;
	String arity;
	HashSet<Tuple> elements;

	public Relation(String s, String i, HashSet<Tuple> hs) {
		name = s;
		arity = i;
		elements = hs;
	}

	public void printThis() {
		System.out.print(name + arity + " = ");
		if (elements.isEmpty()) {
			System.out.print("emptyset");
		} else {
			Iterator<Tuple> it = elements.iterator();
			while (it.hasNext()) {
				it.next().printThis();
				if (it.hasNext())
					System.out.print(",");
			}
		}
		System.out.println();
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
