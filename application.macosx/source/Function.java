/**
 * This class contains many useful functions for the program.
 * Note that this is a shortened version of the original Function class
 * 
 * @author Eric Yang & Judy Zhu
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 * 
 */
public class Function {
	
	/**
	 * Takes in a new term to add to the end of an array
	 * @param array : the desired array
	 * @param termToAdd : the term need to be added
	 * @return the expanded array
	 */
	public static int[] expandArray(int[] array, int termToAdd) {

		int length = array.length;
		int[] store = new int[length];

		if (length > 0) {

			System.arraycopy(array, 0, store, 0, length);

			array = new int[length + 1];

			System.arraycopy(store, 0, array, 0, length);

			array[length] = termToAdd;

		} else {
			array = new int[1];
			array[0] = termToAdd;
		}
		return array;
	}
	
	/**
	 * Initialize a boolean array by making all values the desired state
	 * @param array : the array need to be initialized
	 * @param state : true or false
	 * @return initialized array
	 */
	public static boolean[] initialize(boolean[] array, boolean state) {
		for (int a = 0; a < array.length; a++) {
			array[a] = state;
		}
		return array;
	}

}
