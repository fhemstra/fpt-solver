
/**
 * A Tuple of elements with custom overridden equals() which works element-wise
 */
public class Tuple {
	int[] elements;

	public Tuple(int[] i) {
		elements = i;
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
				if (!(this.elements[i] == t.elements[i]))
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
		for (int i : elements) {
			content += Integer.toString(i);
		}
		return content.hashCode();
	}

	/**
	 * Returns a String to output for this tuple.
	 */
	public String toOutputString() {
		String res = "(";
		for (int j = 0; j < elements.length; j++) {
			// Don't print the -1
			if (elements[j] == -1)
				continue;
			if (j + 1 != elements.length && elements[j + 1] != -1)
				res += elements[j] + "|";
			else
				res += elements[j];
		}
		res += ")";
		return res;
	}

	public boolean intersectsWith(Tuple f) {
		boolean res = false;
		for (int i = 0; i < this.elements.length; i++) {
			for (int j = 0; j < f.elements.length; j++) {
				if (this.elements[i] == f.elements[j] && this.elements[i] != -1)
					res = true;
			}
		}
		return res;
	}

	public boolean onlyMinusOne() {
		for (int i : elements) {
			if (i != -1)
				return false;
		}
		return true;
	}
}
