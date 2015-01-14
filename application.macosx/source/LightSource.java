/**
 * This is an object that extends Vertex3D to create 
 * a lightsource
 * 
 * @author Eric Yang & Judy Zhu
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 *
 */
public class LightSource extends Vertex3D{
	
	public float strength;
	
	/**
	 * initialize the lightsource
	 * @param  x,y,z : coordinates of the lightsource
	 * @param strong  : the strength of the light source. higher the stronger
	 */
	public LightSource(float x, float y, float z, float strong){
		super(x,y,z);
		strength = strong;
	}
	/**
	 * set the brightness of the lightsource
	 * @param number £º brightness of the lightsource
	 */
	public void setStrength(float number){
		strength = number;
	}

}
