package pack;

/**
 * A Tuple of elements with custom overridden equals() which works element-wise
 */
public class Tuple {
	int[] elements;

	/**
	 * Constructs a Tuple containing the given elements.
	 */
	public Tuple(int[] i) {
		elements = i;
	}

	/**
	 * Compares two tuples, returns true if they contain the same elements in the
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

	/**
	 * Returns the result of removing "-1"-entries from the given array. The size of the resulting array is the same as given in the second parameter to enable re-adding elements. 
	 */
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
	 * Elements are equal, if they are equal ignoring -1 entries.
	 */
	public int hashCode() {
		String content = "";
		for (int i : elements) {
			if(i != -1) {
				content += Integer.toString(i);				
			}
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

	/**
	 * Retuns weather this Tuple intersects with the given Tuple. Therefore, if this returns false, the Tuples are disjoint.
	 */
	public boolean intersectsWith(Tuple f) {
		for (int i = 0; i < this.elements.length; i++) {
			for (int j = 0; j < f.elements.length; j++) {
				if (this.elements[i] == f.elements[j] && this.elements[i] != -1)
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns weather this Tuple only contains "-1"-entries.
	 */
	public boolean onlyMinusOne() {
		for (int i : elements) {
			if (i != -1)
				return false;
		}
		return true;
	}

	/**
	 * Adds the given element to this tuple.
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
	 * Checks if this Tuple contains the given element.
	 */
	public boolean arrContains(int u) {
		for (int e : elements) {
			if (e == u)
				return true;
		}
		return false;
	}

	public void removeElement(int edge_node) {
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] == edge_node) {
				elements[i] = -1;
			}
		}
	}
	
	/**
	 * Returns the number of elements in the given array that are not -1.
	 */
	public int actualSize() {
		int counter = 0;
		for (int e : this.elements) {
			if (e != -1)
				counter++;
		}
		return counter;
	}
	
	public Tuple copyThis() {
		int[] elements_copy = new int[this.elements.length];
		for(int i = 0; i < elements_copy.length; i++) {
			elements_copy[i] = this.elements[i];
		}
		Tuple tuple_copy = new Tuple(elements_copy);
		try {
			if(tuple_copy.hashCode() != this.hashCode()) {
				throw new Exception();
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return tuple_copy;
	}
}
