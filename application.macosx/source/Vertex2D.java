/**
 * This is an object that creates vertex that contains two coordinates
 * 
 * @author Eric Yang & Judy Zhu
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 *
 */
public class Vertex2D {

	float x;
	float y;
	
	/**
	 * Set a default point
	 */
	public Vertex2D() {
		x = 0;
		y = 0;
	}
	/**
	 * Create the two coordinates
	 * @param xIn the x component
	 * @param yIn the y component
	 */
	public Vertex2D(float xIn, float yIn) {
		x = xIn;
		y = yIn;
	}
	/**
	 * Sets the x component of the point 
	 * @param xIn the new x component 
	 */
	public void setX(float xIn) {
		x = xIn;
	}
	/**
	 * Sets the y component of the point 
	 * @param yIn the new y component 
	 */
	public void setY(float yIn) {
		y = yIn;
	}
	/**
	 * Return the value of the x for the point
	 * @return the value of the x for the point 
	 */
	public float getX() {
		return x;
	}
	/**
	 * Return the value of the y for the point
	 * @return the value of the y for the point 
	 */
	public float getY() {
		return y;
	}
	/**
	 * JUDY doesn't get what is this doing
	 * @param width of the window defined by MyMain
	 * @param height of the window defined by MyMain
	 */
	public Vertex2D getScreenPoint(int width, int height) {
		Vertex2D screen = new Vertex2D();
		screen.setX((float) (width / 2.0 + x));
		screen.setY((float) (height / 2.0 - y));
		return screen;
	}
	/**
	 * Output the information of this vertex
	 */
	public String toString() {
		return "X: " + x + " Y: " + y;
	}

}
