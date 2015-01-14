/**
 * This is an object that creates a thread to determine shadows
 * 
 * @author Eric Yang
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 *
 */
public class Vertex3D {

	protected float x;
	protected float y;
	protected float z;
	/**
	 * Set a default point, initialize the 3 coordinates
	 */
	public Vertex3D() {
		x = 0;
		y = 0;
		z = 0;
	}
	/**
	 * Create the two coordinates
	 * @param xIn the x component
	 * @param yIn the y component
	 * @param zIn the z component
	 */
	public Vertex3D(float xIn, float yIn, float zIn) {
		x = xIn;
		y = yIn;
		z = zIn;
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
	 * @param yIn the new x component 
	 */
	public void setY(float yIn) {
		y = yIn;
	}
	/**
	 * Sets the z component of the point 
	 * @param zIn the new x component 
	 */
	public void setZ(float zIn) {
		z = zIn;
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
	 * Return the value of the z for the point
	 * @return the value of the z for the point 
	 */
	public float getZ() {
		return z;
	}
	/**
	 * Convert the 3D vertices into 2D
	 * @return new vertex v 
	 */
	public Vertex2D to2D() {

		Vertex2D v = new Vertex2D();
		v.setX(x - z * (float)0.70710678118);
		v.setY(y - z * (float)0.70710678118);

		return v;

	}
	/**
	 * Rotation commands
	 * @param currentAngle the angle already exists
	 * @param rotateAngle the angle made by the motion of rotating the object
	 * @param axis decide the object is moved upwards/downwards or backwards/forwards
	 */
	public void rotate(float currentAngle, float rotateAngle, boolean axis){
		// axis = true. Rotate around Y axis, mouse move horizontally
		// axis = false. Rotate around X axis, mouse move vertically
		
		if(axis){
			
			float length = (float) Math.sqrt(x*x + z*z);
			
			x=(float) (length*Math.cos(currentAngle+rotateAngle));
			z=(float) (length*Math.sin(currentAngle+rotateAngle));
				
		}else{
			
			float length = (float) Math.sqrt(y*y + z*z);
			
			y=(float) (length*Math.cos(currentAngle+rotateAngle));
			z=(float) (length*Math.sin(currentAngle+rotateAngle));
			
		}
		
	}
	/**
	 * Output the information of this vertex
	 */
	public String toString() {
		return "X: " + x + " Y: " + y + " Z: " + z;
	}

}
