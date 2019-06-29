import java.util.HashSet;
import java.util.Iterator;

public class Relation {
	String name;
	int arity;
	HashSet<String[]> elements;

	public Relation(String s, int i, HashSet<String[]> hs) {
		name = s;
		arity = i;
		elements = hs;
	}

	public void printThis() {
		System.out.print(name + arity + " = ");
		if (elements.isEmpty()) {
			System.out.print("emptyset");
		} else {
			Iterator<String[]> it = elements.iterator();
			while (it.hasNext()) {
				String tmp = "(";
				String[] s_arr = it.next();
				for (int j = 0; j < s_arr.length; j++) {
					if (j + 1 != s_arr.length)
						tmp += s_arr[j] + ",";
					else
						tmp += s_arr[j];
				}
				if (it.hasNext())
					tmp += "),";
				else
					tmp += ")";
				System.out.print(tmp);
			}
		}
		System.out.println();
	}

}
