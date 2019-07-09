
/**
 * A Tuple of elements with custom overridden equals() which works element-wise
 */
public class Tuple {
	String[] elements;

	public Tuple(String[] s) {
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
			for (int i = 0; i < elements.length; i++) {
				if (!this.elements[i].equals(t.elements[i]))
					return false;
			}
			return true;
		}
		// if o is not a tuple
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

	public String toOutputString() {
		String res = "(";
		for (int j = 0; j < elements.length; j++) {
			if (j + 1 != elements.length)
				res += elements[j] + "|";
			else
				res += elements[j];
		}
		res += ")";
		return res;
	}
}
