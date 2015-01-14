/**
 * This is an object that creates a thread to initialize PCubes 
 * It initialize PCubes
 * 
 * @author Eric Yang & Judy Zhu
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 *
 */
public class InitializeThread implements Runnable{

	float rotateAngle;
	boolean axis;
	int start;
	int skip;

	/**
	 * Initialize the thread
	 * @param start : start is where the it start counting
	 * @param skip : number of threads
	 */
	public InitializeThread(int start,int skip) {
		this.start = start;
		this.skip = skip;
	}
	
	/**
	 * execute the thread, initialize all PCubes
	 */
	public void run() {
		
		for (int a = start; a < MyMain.allPCubes.length; a = a + skip) {
			MyMain.allPCubes[a].setColor(0);
			MyMain.allPCubes[a].initializeCovered();
		}

	}
}
