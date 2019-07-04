import java.util.HashSet;
import java.util.Iterator;

public class Relation {
	String name;
	int arity;
	HashSet<Tuple> elements;

	public Relation(String s, int i, HashSet<Tuple> hs) {
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

}
