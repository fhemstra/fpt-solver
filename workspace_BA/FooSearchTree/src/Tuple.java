
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
	 * same order. Ignores entries that are -1.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof Tuple) {
			// cast o to Tuple
			Tuple t = (Tuple) obj;
			// Remove -1
			int common_size = Math.max(t.elements.length, this.elements.length);
			int[] t_trimmed = trimArray(t.elements, common_size);
			int[] this_trimmed = trimArray(this.elements, common_size);
			// All elements shall match
			for (int i = 0; i < this_trimmed.length; i++) {
				// Order is not important (1,2) == (2,1)
				boolean curr_element_matches = false;
				for (int j = 0; j < t_trimmed.length; j++) {
					if (this_trimmed[i] == t_trimmed[j]) {
						curr_element_matches = true;
						break;
					}
				}
				if (!curr_element_matches)
					return false;
			}
			return true;
		}
		// if o is not a tuple
		return false;
	}

	private int[] trimArray(int[] arr, int common_size) {
		int[] res = new int[common_size];
		int it_1 = 0;
		int it_2 = 0;
		while (it_2 < arr.length) {
			if (arr[it_2] != -1) {
				res[it_1] = arr[it_2];
				it_1++;
			}
			it_2++;
		}
		return res;
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
	public String toOutputString(boolean show_minus_one_entries) {
		String res = "(";
		if (elements.length == 0) {
			res += "empty";
		} else {
			for (int j = 0; j < elements.length; j++) {
				// Don't print the -1
				if (!show_minus_one_entries && elements[j] == -1)
					continue;
				res += elements[j];
				if (j + 1 != elements.length) {
					if (show_minus_one_entries || elements[j + 1] != -1) {
						res += "|";
					}
				}
			}
		}
		res += ")";
		return res;
	}

	public boolean intersectsWith(Tuple f) {
		for (int i = 0; i < this.elements.length; i++) {
			for (int j = 0; j < f.elements.length; j++) {
				if (this.elements[i] == f.elements[j] && this.elements[i] != -1)
					return true;
			}
		}
		return false;
	}

	public boolean onlyMinusOne() {
		for (int i : elements) {
			if (i != -1)
				return false;
		}
		return true;
	}

	/**
	 * Adds an element to the tuple.
	 */
	public void addElement(int u) {
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] == -1) {
				elements[i] = u;
				return;
			}
		}
	}

	/**
	 * Checks of elements contains u.
	 */
	public boolean arrContains(int u) {
		for (int e : elements) {
			if (e == u)
				return true;
		}
		return false;
	}
}
