import processing.core.PApplet;

/**
 * This is another helping function that is mostly concerned with the
 * Starshadowcraft program.
 * 
 * @author Eric Yang & Judy Zhu
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 * 
 */
public class PFunction extends PApplet {

	public static final float pi = (float) 3.1415926535;

	/**
	 * Check if the number is an integer multiple of 2 and/or 5
	 * 
	 * @param number
	 *            : the number need to be tested
	 * @return true, is a multiple of 2 and/or 5, false, not a multiple
	 */
	public static boolean checkNumberPowerOf2PowerOf5(double number) {

		loop1: do {
			if (number / 2.0 != Math.floor(number / 2.0)) {
				break loop1;
			}
			number = number / 2.0;
		} while (number >= 2);

		loop1: do {
			if (number / 5.0 != Math.floor(number / 5.0)) {
				break loop1;
			}
			number = number / 5.0;
		} while (number >= 5);

		if (number == 1) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Distance between two 3D points
	 * 
	 * @param A
	 *            : one point
	 * @param B
	 *            : another point
	 * @return the distance
	 */
	public static float distance2(Vertex3D A, Vertex3D B) {

		double x2 = (A.x - B.x) * (A.x - B.x);
		double y2 = (A.y - B.y) * (A.y - B.y);
		double z2 = (A.z - B.z) * (A.z - B.z);

		return (float) (x2 + y2 + z2);
	}

	/**
	 * 
	 * @param array
	 * @param finalSize
	 *            is the numbe of terms. finalSize = 3 means 3 terms in the
	 *            array. Array end at term 2.
	 */
	public static MyMain.PCube[] expandPCubeArray(MyMain.PCube[] array,
			int finalSize) {

		int length = array.length;
		MyMain.PCube[] store = new MyMain.PCube[length];

		if (length > 0) {

			System.arraycopy(array, 0, store, 0, length);

			array = new MyMain.PCube[finalSize];

			System.arraycopy(store, 0, array, 0, length);

		} else {
			array = new MyMain.PCube[finalSize];
		}
		return array;
	}

	/**
	 * Find the angle between the point and the selected (X or Y) axis
	 * 
	 * @param v
	 *            : the point
	 * @param axis
	 *            : true, Y-axis, false, X-axis
	 * @return the angle
	 */
	public static float findCurrentAngle(Vertex3D v, boolean axis) {
		// axis = true. Rotate around Y axis, mouse move horizontally
		// axis = false. Rotate around X axis, mouse move vertically

		if (axis) {

			if (v.getX() > 0) {
				return atan(v.getZ() / v.getX());
			} else if (v.getX() < 0) {
				return pi + atan(v.getZ() / v.getX());
			} else {

				if (v.getZ() > 0) {
					return pi / 2;
				} else {
					return pi * 3 / 2;
				}
			}
		} else {

			if (v.getY() > 0) {
				return atan(v.getZ() / v.getY());
			} else if (v.getY() < 0) {
				return pi + atan(v.getZ() / v.getY());
			} else {

				if (v.getZ() > 0) {
					return pi / 2;
				} else {
					return pi * 3 / 2;
				}
			}
		}
	}

	/**
	 * Calculate the angle turned as mouse is dragged
	 * 
	 * @param mousePlaceX
	 *            : X-coordinate of the mouse
	 * @return the angle
	 */
	public static float findRotateAngleX(float mousePlaceX) {

		double half = MyMain.width / 2.0;

		return (float) Math.acos((mousePlaceX - half) / half);

	}

	/**
	 * Calculate the angle turned as mouse is dragged
	 * 
	 * @param mousePlaceY
	 *            : Y-coordinate of the mouse
	 * @return the angle
	 */
	public static float findRotateAngleY(float mousePlaceY) {

		double half = MyMain.height / 2.0;

		return (float) Math.acos((half - mousePlaceY) / half);

	}

	/**
	 * Determine the vector equation of a line defined by two given points
	 * 
	 * @param A
	 *            : a point
	 * @param B
	 *            : another point
	 * @return an array that stores all values in the equation
	 */
	public static float[] lineEquation(Vertex3D A, Vertex3D B) {

		float[] coefficients = new float[6];

		coefficients[0] = A.x;
		coefficients[1] = A.x - B.x;

		coefficients[2] = A.y;
		coefficients[3] = A.y - B.y;

		coefficients[4] = A.z;
		coefficients[5] = A.z - B.z;

		return coefficients;

	}

	/**
	 * Using cross product to determine the shortest distance between a point
	 * and a line
	 * 
	 * @param A
	 *            : the point
	 * @param line
	 *            : the equation for the line
	 * @return the distance squared
	 */
	public static float distancePointToLine(Vertex3D A, Vertex3D bound,
			float[] line) {

		/*if ((bound.x >= line[0]) == (A.x >= line[0])
				&& (bound.y >= line[2] && A.y >= line[2])
				&& (bound.z >= line[4] && A.z >= line[4])) {*/
		
		if(
				(bound.x >= line[0]) == (A.x >= line[0]) &&
				(bound.y >= line[2]) == (A.y >= line[2]) &&
				(bound.z >= line[4]) == (A.z >= line[4]) 
				
				){
			
		
			float[] vector = new float[3];
			vector[0] = A.x - line[0];
			vector[1] = A.y - line[2];
			vector[2] = A.z - line[4];

			float[] result = new float[3];
			result[0] = vector[1] * line[5] - vector[2] * line[3];
			result[1] = vector[2] * line[1] - vector[0] * line[5];
			result[2] = vector[0] * line[3] - vector[1] * line[1];

			float num1 = result[0] * result[0] + result[1] * result[1]
					+ result[2] * result[2];
			float num2 = line[1] * line[1] + line[3] * line[3] + line[5]
					* line[5];

			return num1 / num2;
			
		} else {
			
			return 99999;
			
		}

	}

}
