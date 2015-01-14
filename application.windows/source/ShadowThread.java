/**
 * This is an object that creates a thread to determine shadows
 * 
 * @author Eric Yang & Judy Zhu
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 *
 */
public class ShadowThread implements Runnable{
	
	int start;
	int b;
	int skip;
	
	/**
	 * Initialize the thread
	 * @param b : lightsource index
	 * @param start : start is where the it start counting
	 * @param skip : number of threads
	 */
	public ShadowThread(int b, int start, int skip){
		this.b = b;
		this.start = start;		
		this.skip = skip;
	}
	

	/**
	 * execute the thread, determine the shaodws in the "world"
	 */
	public void run(){
		
		for (int a = 0; a < MyMain.boundPoints.length; a = a + skip) {
			// for a specific point on the outer bound

			float[] line = PFunction.lineEquation(MyMain.lightSource[b],
					MyMain.boundPoints[a]);
			// the closest one to the light source is lit up

			loop1: for (int c = 0; c < MyMain.allPCubes.length; c++) {

				if (MyMain.allPCubes[c].isLight == false) {

					if (PFunction.distancePointToLine(MyMain.allPCubes[c].center,
							MyMain.boundPoints[a],line) < MyMain.allPCubes[c].radius2) {

						MyMain.allPCubes[c].setCovered(b, false);
						break loop1;
					}

				}

			}

		}
		
	}

}
