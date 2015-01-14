/**
 * This is an object that creates a thread for brightness. 
 * It does the calculation for brightness in this thread
 * 
 * @author Eric Yang & Judy Zhu
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 *
 */
public class BrightnessThread extends Thread{

	float rotateAngle;
	boolean axis;
	int start;
	int skip;
	
	/**
	 * Initialize the thread
	 * @param start : start is where the it start counting
	 * @param skip : number of threads
	 */
	public BrightnessThread(int start,int skip) {
		this.start = start;
		this.skip = skip;
	}

	/**
	 * execute the thread, calculate the brightness for all PCubes
	 */
	public void run() {
		
		for (int a = start; a < MyMain.allPCubes.length; a = a + skip) {
			MyMain.allPCubes[a].analyzeBrightness();
		}

	}
}
