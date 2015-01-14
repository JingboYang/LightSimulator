/**
 * This is class that is dedicated so sorting algorithms
 * 
 * @author Eric Yang & Judy Zhu
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 *
 */
public class SortFunction {
	
	/**
	 * sort PCubes based on distance from light source
	 */
	public static MyMain.PCube[] sortPCubeByDistance(
			MyMain.PCube[] array, int index) {

		int length = array.length;

		rearrangePCube(array, length, index);

		int end = length - 1;
		while (end > 0) {
			MyMain.PCube tmp = array[end];
			array[end] = array[0];
			array[0] = tmp;
			shiftPosition(array, 0, end - 1, index);
			end--;
		}

		return array;

	}
	
	/**
	 * Rearrange the array according to the binary tree relationship
	 * @param array : the array that is being sorted
	 * @param length : length of array
	 * @param index : the terms that I am working with
	 */
	public static void rearrangePCube(MyMain.PCube[] array, int length, int index) {
		int start = (length - 2) / 2;

		while (start >= 0) {
			shiftPosition(array, start, length - 1, index);
			start--;
		}
	}
	
	/**
	 * compare, then put lower values in the front
	 * @param array : the array that is being sorted
	 * @param start : start of the interval
	 * @param end : end of the interval
	 * @param index : where we currently are
	 */
	public static void shiftPosition(MyMain.PCube[] array, int start, int end,
			int index) {
		int original = start;

		while ((original * 2 + 1) <= end) {
			int branch = original * 2 + 1;

			if (branch + 1 <= end
					&& array[branch].getDistance2(index) < array[branch + 1]
							.getDistance2(index))
				branch = branch + 1;
			if (array[original].getDistance2(index) < array[branch]
					.getDistance2(index)) {
				MyMain.PCube tmp = array[original];
				array[original] = array[branch];
				array[branch] = tmp;
				original = branch;
			} else
				return;
		}
	}

	/**
	 * sort PCubes based on coordinates, Y, Z, then X
	 * @param array : the array that is being sorted
	 */
	public static MyMain.PCube[] sortPCube(MyMain.PCube[] array) {

		int length = array.length;

		array = sortPCubeByY(array);

		// sort z based on y

		int index = 0;
		int end;

		do {

			float storeY = array[index].getCenter().getY();
			end = index + 1;

			loop2: while (end < length) {
				if (array[end].getCenter().getY() != storeY) {

					array = sortPCubeByZ(array, index, end - 1);

					index = end;
					break loop2;
				}
				end++;
			}

		} while (end + 1 < length);

		float storeY2 = array[length - 1].getCenter().getY();
		loop3: for (int a = length - 1; a >= 0; a--) {
			if (array[a].getCenter().getY() != storeY2) {
				array = sortPCubeByZ(array, a + 1, length - 1);
				break loop3;
			}

		}

		// sort x based on y and z
		index = 0;

		do {

			float storeY = array[index].getCenter().getY();
			float storeZ = array[index].getCenter().getZ();
			end = index + 1;

			loop2: while (end < length) {
				if (array[end].getCenter().getY() != storeY
						|| array[end].getCenter().getZ() != storeZ) {

					array = sortPCubeByX(array, index, end - 1);

					index = end;
					break loop2;
				}
				end++;
			}

		} while (end + 1 < length);

		float storeY3 = array[length - 1].getCenter().getY();
		float storeZ3 = array[length - 1].getCenter().getZ();
		loop3: for (int a = length - 1; a >= 0; a--) {
			if (array[a].getCenter().getY() != storeY3
					|| array[a].getCenter().getZ() != storeZ3) {
				array = sortPCubeByX(array, a + 1, length - 1);
				break loop3;
			}

		}

		return array;
	}

	/**
	 * sort PCubes based on coordinates based on Y
	 * @param array : the array that is being sorted
	 */
	public static MyMain.PCube[] sortPCubeByY(MyMain.PCube[] array) {

		int length = array.length;

		rearrangePCubeY(array, length);

		int end = length - 1;
		while (end > 0) {
			MyMain.PCube tmp = array[end];
			array[end] = array[0];
			array[0] = tmp;
			shiftPositionY(array, 0, end - 1);
			end--;
		}

		return array;

	}
	
	/**
	 * Rearrange the array according to the binary tree relationship
	 * @param array : the array that is being sorted
	 * @param length : length of array
	 * @param index : the terms that I am working with
	 */
	public static void rearrangePCubeY(MyMain.PCube[] array, int length) {
		int start = (length - 2) / 2;

		while (start >= 0) {
			shiftPositionY(array, start, length - 1);
			start--;
		}
	}
	
	/**
	 * compare, then put lower values in the front
	 * @param array : the array that is being sorted
	 * @param start : start of the interval
	 * @param end : end of the interval
	 */
	public static void shiftPositionY(MyMain.PCube[] array, int start, int end) {
		int original = start;

		while ((original * 2 + 1) <= end) {
			int branch = original * 2 + 1;

			if (branch + 1 <= end
					&& array[branch].getCenter().getY() < array[branch + 1]
							.getCenter().getY())
				branch = branch + 1;
			if (array[original].getCenter().getY() < array[branch].getCenter()
					.getY()) {
				MyMain.PCube tmp = array[original];
				array[original] = array[branch];
				array[branch] = tmp;
				original = branch;
			} else
				return;
		}
	}

	/**
	 * sort based on z
	 */
	public static MyMain.PCube[] sortPCubeByZ(MyMain.PCube[] bigArray,
			int first, int last) {

		int length = last - first + 1;

		MyMain.PCube[] array = new MyMain.PCube[length];

		System.arraycopy(bigArray, first, array, 0, length);

		rearrangePCubeZ(array, length);

		int end = length - 1;
		while (end > 0) {
			MyMain.PCube tmp = array[end];
			array[end] = array[0];
			array[0] = tmp;
			shiftPositionZ(array, 0, end - 1);
			end--;
		}

		System.arraycopy(array, 0, bigArray, first, length);

		return bigArray;

	}
	
	/**
	 * Rearrange the array according to the binary tree relationship
	 * @param array : the array that is being sorted
	 * @param length : length of array
	 */
	public static void rearrangePCubeZ(MyMain.PCube[] array, int length) {
		int start = (length - 2) / 2;

		while (start >= 0) {
			shiftPositionZ(array, start, length - 1);
			start--;
		}
	}
	
	/**
	 * compare, then put lower values in the front
	 * @param array : the array that is being sorted
	 * @param start : start of the interval
	 * @param end : end of the interval
	 * @param index : where we currently are
	 */
	public static void shiftPositionZ(MyMain.PCube[] array, int start, int end) {
		int original = start;

		while ((original * 2 + 1) <= end) {
			int branch = original * 2 + 1;

			if (branch + 1 <= end
					&& array[branch].getCenter().getZ() < array[branch + 1]
							.getCenter().getZ())
				branch = branch + 1;
			if (array[original].getCenter().getZ() < array[branch].getCenter()
					.getZ()) {
				MyMain.PCube tmp = array[original];
				array[original] = array[branch];
				array[branch] = tmp;
				original = branch;
			} else
				return;
		} 
	}

	/**
	 * sort based on x
	 */
	public static MyMain.PCube[] sortPCubeByX(MyMain.PCube[] bigArray,
			int first, int last) {

		int length = last - first + 1;

		MyMain.PCube[] array = new MyMain.PCube[length];

		System.arraycopy(bigArray, first, array, 0, length);

		rearrangePCubeZ(array, length);

		int end = length - 1;
		while (end > 0) {
			MyMain.PCube tmp = array[end];
			array[end] = array[0];
			array[0] = tmp;
			shiftPositionX(array, 0, end - 1);
			end--;
		}

		System.arraycopy(array, 0, bigArray, first, length);

		return bigArray;

	}
	
	/**
	 * Rearrange the array according to the binary tree relationship
	 * @param array : the array that is being sorted
	 * @param length : length of array
	 */
	public static void rearrangePCubeX(MyMain.PCube[] array, int length) {
		int start = (length - 2) / 2;

		while (start >= 0) {
			shiftPositionX(array, start, length - 1);
			start--;
		}
	}
	
	/**
	 * compare, then put lower values in the front
	 * @param array : the array that is being sorted
	 * @param start : start of the interval
	 * @param end : end of the interval
	 */
	public static void shiftPositionX(MyMain.PCube[] array, int start, int end) {
		int original = start;

		while ((original * 2 + 1) <= end) {
			int branch = original * 2 + 1;

			if (branch + 1 <= end
					&& array[branch].getCenter().getX() < array[branch + 1]
							.getCenter().getX())
				branch = branch + 1;
			if (array[original].getCenter().getX() < array[branch].getCenter()
					.getX()) {
				MyMain.PCube tmp = array[original];
				array[original] = array[branch];
				array[branch] = tmp;
				original = branch;
			} else
				return;
		}
	}

	

}
