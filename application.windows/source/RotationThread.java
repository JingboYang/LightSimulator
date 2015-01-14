/**
 * This is an object that creates a thread for rotation
 * 
 * @author Eric Yang & Judy Zhu
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 *
 */
public class RotationThread extends Thread{

	float rotateAngle;
	boolean axis;
	int start;
	int skip;

	/**
	 * Initialize the thread
	 * @param start : start is where the it start counting
	 * @param skip : number of threads
	 */
	public RotationThread(int start, float rotateAngle, boolean axis, int skip) {
		this.rotateAngle = rotateAngle;
		this.axis = axis;
		this.start = start;
		this.skip = skip;
	}

	/**
	 * execute the thread, rotate the PCubes
	 */
	public void run() {
		
		for (int a = start; a < MyMain.allPCubes.length; a = a + skip) {
			MyMain.allPCubes[a].rotate(rotateAngle, axis);
		}

	}
}
