import java.util.ArrayList;

/**
 * A Tuple of elements with custom overridden equals() which works element-wise
 */
public class Tuple {
	ArrayList<String> elements;

	public Tuple(ArrayList<String> s) {
		elements = s;
	}

	/**
	 * Compares two tuples, returns true if the contains the same elements in the
	 * same order.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof Tuple) {
			// cast o to Tuple
			Tuple t = (Tuple) obj;
			// All elements shall match
			if(this.elements.size() == t.elements.size()) {
				for (int i = 0; i < elements.size(); i++) {
					if (!this.elements.get(i).equals(t.elements.get(i)))
						return false;
				}
				return true;				
			}
		}
		// if o is not a tuple or not of same arity
		return false;
	}

	/**
	 * HashCode override to make HashSet.contains() use overridden equals().
	 */
	public int hashCode() {
		String content = "";
		for (String s : elements) {
			content += s;
		}
		return content.hashCode();
	}

	public void printThis() {
		String tmp = "(";
		for (int j = 0; j < elements.size(); j++) {
			if (j + 1 != elements.size())
				tmp += elements.get(j) + ",";
			else
				tmp += elements.get(j);
		}
		tmp += ")";
		System.out.print(tmp);
	}
}
