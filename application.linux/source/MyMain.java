import processing.core.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * The main portion of the Statshadowcraft project. Program should be executed
 * from this file to work correctly
 * 
 * @author Eric Yang & Judy Zhu
 * @version 0.0
 * @since SE1.6
 * @since May 2013
 * 
 */
public class MyMain extends PApplet {
	
	public static void main(String args[]) {
	    PApplet.main(new String[] { "--present", "MyMain" });
	  }

	// read and write fiel
	Scanner in;
	PrintWriter out;

	boolean printed = false;

	// pre-determined variables
	final float pi = (float) 3.1415926525;
	// for customization
	int numberOfThreads;
	int resolutionOfWorld; // smaller the better
	boolean turnOnShadow; // if turned on, cubes covered = true
							// else, cubes are not covered initially

	// window
	public static int height = 700;
	public static int width = 700;

	// mouse
	float storeMouseX;
	float storeMouseY;

	// control bar
	Bar[] myBar;
	boolean movingBar;
	Option[] myOption;
	Switch shadowSwitch;

	public int selectedLight;

	// pixels are stored in the form of PCubes
	public static PCube[] allPCubes;
	public int objectCount;
	public int[] objectStart;
	public int[] objectEnd;

	// the world
	public static Vertex3D[] boundPoints;

	// major "things" in my world
	SolidCube[] myCubes;
	Ground[] myGround;
	static LightSource[] lightSource;

	// calculate FPS
	double lastRefreshTime;
	double currentTime;
	double FPS;
	PFont FPSFont;

	// threads
	static Thread[] rotateThreads;
	static Thread[] shadowThreads;
	static Thread[] brightnessThreads;
	static Thread[] initializeThreads;

	/**
	 * Loads up the window calls the initialize function
	 */
	public void setup() {

		// Needed a numerical input so can be exported
		//size(width, height);
		size(700, 700);
		
		fill(255);
		stroke(255);
		background(100, 150, 240);

		try {
			initialize();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		FPSFont = createFont("Comic Sans MS", 30, true);
		textFont(FPSFont);
		lastRefreshTime = System.currentTimeMillis();

		storeMouseX = mouseX;
		storeMouseY = mouseY;
	}

	/**
	 * Called by setup() calls readFromFile() initialize the UI and lightsources
	 * 
	 * @throws FileNotFoundException
	 */
	public void initialize() throws FileNotFoundException {

		out = createWriter("Output.txt");

		boolean[] drawFaces = new boolean[6];
		drawFaces = Function.initialize(drawFaces, true);

		allPCubes = new PCube[0];
		objectCount = 0;
		objectStart = new int[0];
		objectEnd = new int[0];

		try {
			readFromFile();
		} catch (Exception e) {
			quit(0);
		}

		// to finally create lightsource
		objectStart = Function.expandArray(objectStart, allPCubes.length);
		allPCubes = PFunction.expandPCubeArray(allPCubes, allPCubes.length
				+ lightSource.length);
		objectEnd = Function.expandArray(objectEnd, allPCubes.length);

		for (int a = 0; a < lightSource.length; a++) {
			allPCubes[objectStart[objectCount] + a] = new PCube(lightSource[a],
					2, drawFaces);
			allPCubes[objectStart[objectCount] + a].isLight = true;
		}

		objectCount++; // object count for the lightsource

		// Control Panel
		// the control bars
		myBar = new Bar[4];
		movingBar = false;

		for (int a = 0; a < 3; a++) {
			myBar[a] = new Bar(a, 1800);
		}

		myBar[3] = new Bar(3, 500);

		// options
		myOption = new Option[lightSource.length];
		selectedLight = 0;

		for (int a = 0; a < lightSource.length; a++) {
			myOption[a] = new Option(a);
		}

		shadowSwitch = new Switch(turnOnShadow);

		createBoundM1();

		rotateAllPCubes((float) 0.000001, true);

	}

	/**
	 * Reads from the file "Input.txt" to collect necessary information to run
	 * the program
	 * 
	 * @throws FileNotFoundException
	 */
	public void readFromFile() throws FileNotFoundException {

		in = new Scanner(new FileInputStream("Input.txt"));

		// light source
		waste(1);
		int numberOfLightSource = (int) Math
				.floor(Double.valueOf(in.nextLine()));
		if (numberOfLightSource > 0) {
			lightSource = new LightSource[numberOfLightSource];
			for (int a = 0; a < numberOfLightSource; a++) {
				waste(1);
				float x = Float.valueOf(in.nextLine());
				float y = Float.valueOf(in.nextLine());
				float z = Float.valueOf(in.nextLine());
				waste(1);
				float strength = Float.valueOf(in.nextLine());
				if (strength <= 900 && strength > 0) {
					lightSource[a] = new LightSource(x, y, z, strength);
				} else {
					lightSource[a] = new LightSource(x, y, z, 150);
				}
			}
		} else {
			quit(1);
		}

		out.println("Light source(s) is probably generated correctly");

		// big cube
		waste(1);
		int numberOfBigCube = (int) Math.floor(Double.valueOf(in.nextLine()));
		if (numberOfBigCube >= 0) {
			myCubes = new SolidCube[numberOfBigCube];
			for (int a = 0; a < numberOfBigCube; a++) {
				waste(1);
				float x = Float.valueOf(in.nextLine());
				float y = Float.valueOf(in.nextLine());
				float z = Float.valueOf(in.nextLine());
				waste(1);
				float size = Float.valueOf(in.nextLine());
				System.out.println(size);
				if (size > 20) {
					waste(1);
					int resolution = (int) Math.floor(Double.valueOf(in
							.nextLine()));
					if (PFunction.checkNumberPowerOf2PowerOf5(resolution)) {
						if (resolution <= size && resolution > 1) {
							myCubes[a] = new SolidCube(size, resolution,
									new Vertex3D(x, y, z));
						} else {
							myCubes[a] = new SolidCube(size, (int) size / 2,
									new Vertex3D(x, y, z));
						}
					} else {
						quit(2);
					}
				} else {
					waste(1);
					int resolution = (int) Math.floor(Double.valueOf(in
							.nextLine()));
					if (PFunction.checkNumberPowerOf2PowerOf5(resolution)) {
						if (resolution <= size && resolution > 1) {
							myCubes[a] = new SolidCube(100, resolution,
									new Vertex3D(x, y, z));
						} else {
							myCubes[a] = new SolidCube(100, (int) size / 2,
									new Vertex3D(x, y, z));
						}
					} else {
						quit(2);
					}
				}
			}
		} else {
			quit(2);
		}

		out.println("Big Cube(s) is probably generated correctly");

		// the ground
		waste(1);
		int numberOfGround = (int) Math.floor(Double.valueOf(in.nextLine()));
		if (numberOfGround >= 0) {
			myGround = new Ground[numberOfGround];
			for (int a = 0; a < numberOfGround; a++) {
				waste(1);
				int resolution = (int) Math
						.floor(Double.valueOf(in.nextLine()));
				if (PFunction.checkNumberPowerOf2PowerOf5(resolution)) {
					if (resolution <= 800 && resolution > 1) {
						waste(1);
						float position = Float.valueOf(in.nextLine());
						myGround[a] = new Ground(resolution, position);
					} else {
						waste(1);
						float position = Float.valueOf(in.nextLine());
						myGround[a] = new Ground((int) 400, position);
					}
				} else {
					quit(3);
				}
			}
		} else {
			quit(3);
		}

		out.println("Ground is probably generated correctly");

		// shadow on or off
		waste(1);
		int shadowStatus = (int) Math.floor(Double.valueOf(in.nextLine()));
		if (shadowStatus % 2 == 0) {
			turnOnShadow = false;
		} else {
			turnOnShadow = true;
		}

		out.println("Shadow is probably configured correctly");

		// number of threads
		waste(1);
		int threadNumbers = (int) Math.floor(Double.valueOf(in.nextLine()));
		if (threadNumbers > 0) {
			numberOfThreads = threadNumbers;
		} else {
			numberOfThreads = 1;
		}

		rotateThreads = new Thread[numberOfThreads];
		shadowThreads = new Thread[numberOfThreads];
		brightnessThreads = new Thread[numberOfThreads];
		initializeThreads = new Thread[numberOfThreads];

		waste(1);
		int resolutionOfTheWorld = Integer.valueOf(in.nextLine());
		if (resolutionOfTheWorld > 1) {
			if (PFunction.checkNumberPowerOf2PowerOf5(resolutionOfTheWorld)) {
				if (resolutionOfTheWorld <= 5000) {
					resolutionOfWorld = resolutionOfTheWorld;
				} else {
					resolutionOfWorld = 5000;
				}
			} else {
				quit(4);
			}
		} else {
			quit(4);
		}

	}

	/**
	 * Draw method controls how the things are shown on the screen
	 */
	public void draw() {

		background(100, 150, 240);

		ControlPanel();

		rotation();

		fillWithLightSource();

		for (int a = 0; a < 4; a++) {
			myBar[a].drawBar();
		}

		for (int a = 0; a < lightSource.length; a++) {
			myOption[a].drawOption();
		}

		shadowSwitch.drawSwitch();

		FPS();
	}

	/**
	 * Determines if the control bars/switches are under influence of the mouse
	 * and then changes the values they represent according to the coordinates
	 * of the mouse
	 */
	public void ControlPanel() {

		// select light source
		if (mousePressed) {
			for (int a = 0; a < lightSource.length; a++) {
				if (myOption[a].inControl(mouseX, mouseY)) {
					myOption[a].check();
					selectedLight = a;
					for (int b = 0; b < lightSource.length; b++) {
						if (a != b) {
							myOption[b].uncheck();
						}
					}
				}
			}
		}

		// the bars
		movingBar = false;

		if (mousePressed) {
			for (int a = 0; a < 4; a++) {
				if (myBar[a].inControl(mouseX, mouseY)) {
					myBar[a].moveBar(mouseX);

					if (a == 0) {
						lightSource[selectedLight].setX(myBar[a]
								.getPosition(mouseX));
					} else if (a == 1) {
						lightSource[selectedLight].setY(myBar[a]
								.getPosition(mouseX));
					} else if (a == 2) {
						lightSource[selectedLight].setZ(myBar[a]
								.getPosition(mouseX));
					} else {
						lightSource[selectedLight].setStrength(myBar[a]
								.getPosition(mouseX));
					}

					movingBar = true;
				}

			}
		}

		myBar[0].setPosition(lightSource[selectedLight].x);
		myBar[1].setPosition(lightSource[selectedLight].y);
		myBar[2].setPosition(lightSource[selectedLight].z);
		myBar[3].setPosition(lightSource[selectedLight].strength);

		if (mousePressed) {
			if (shadowSwitch.inControl(mouseX, mouseY)) {
				if (shadowSwitch.checked && turnOnShadow) {
					shadowSwitch.uncheck();
					turnOnShadow = false;
				} else if (shadowSwitch.checked == false
						&& turnOnShadow == false) {
					shadowSwitch.check();
					turnOnShadow = true;
				}
			}
		}

	}

	/**
	 * Calculates the angles that the cubes will be rotated
	 */
	public void rotation() {

		float mousePlaceX = mouseX;
		float mousePlaceY = mouseY;

		if (mousePressed && movingBar == false) {

			boolean axis;
			if (mousePlaceX > width - 20 || mousePlaceX < 20) {
				mousePlaceX = storeMouseX;
			}

			if (mousePlaceY > height - 20 || mousePlaceY < 20) {
				mousePlaceY = storeMouseY;
			}

			float rotateAngle;
			if (mouseButton == LEFT) {

				axis = true;

				rotateAngle = PFunction.findRotateAngleX(mousePlaceX)
						- PFunction.findRotateAngleX(storeMouseX);

			} else {
				axis = false;

				rotateAngle = PFunction.findRotateAngleY(mousePlaceY)
						- PFunction.findRotateAngleY(storeMouseY);
			}

			rotateAllPCubes(rotateAngle, axis);

		}

		for (int a = 0; a < allPCubes.length; a++) {
			allPCubes[a].refreshPCube();
		}

		storeMouseX = mousePlaceX;
		storeMouseY = mousePlaceY;

	}

	/**
	 * Calls on rotation threads to perform the actual rotation. i.e, changes in
	 * the coordinates
	 * 
	 * @param rotateAngle
	 *            : the angle which the cubes are being rotated
	 * @param axis
	 *            :true, Y-axis, false, X-axis
	 */
	public void rotateAllPCubes(float rotateAngle, boolean axis) {

		if (axis) {

			for (int z = 0; z < rotateThreads.length; z++) {
				rotateThreads[z] = new Thread(new RotationThread(z,
						rotateAngle, axis, rotateThreads.length));
			}

			for (int z = 0; z < rotateThreads.length; z++) {
				rotateThreads[z].start();
			}

			try {
				for (int z = 0; z < rotateThreads.length; z++) {
					rotateThreads[z].join();
				}
			} catch (Exception e) {
			}

		} else {

			for (int z = 0; z < rotateThreads.length; z++) {
				rotateThreads[z] = new Thread(new RotationThread(z,
						rotateAngle, axis, rotateThreads.length));
			}

			for (int z = 0; z < rotateThreads.length; z++) {
				rotateThreads[z].start();
			}
			try {
				for (int z = 0; z < rotateThreads.length; z++) {
					rotateThreads[z].join();
				}
			} catch (Exception e) {
			}

		}

	}

	/**
	 * calls other functions to first determine shadow, and then draw the cubes
	 * according to their distance to the lightsources
	 */
	public void fillWithLightSource() {

		if (turnOnShadow) {
			determineShadow();
		}

		drawAllPCubes();
	}

	/**
	 * Calls the shadow threads to perform the shadow calculations
	 */
	public void determineShadow() {

		for (int b = 0; b < lightSource.length; b++) {

			allPCubes = SortFunction.sortPCubeByDistance(allPCubes, b);

			for (int z = 0; z < shadowThreads.length; z++) {
				shadowThreads[z] = new Thread(new ShadowThread(b, z,
						rotateThreads.length));
			}

			for (int z = 0; z < shadowThreads.length; z++) {
				shadowThreads[z].start();
			}

			try {
				for (int z = 0; z < shadowThreads.length; z++) {
					shadowThreads[z].join();
				}
			} catch (Exception e) {
			}

		}
	}

	/**
	 * calls the brightness threads and initialize threads to first determine
	 * the brightness, then draw the cubes, and finally initialize the cubes for
	 * the next calculation
	 */
	public void drawAllPCubes() {

		allPCubes = SortFunction.sortPCube(allPCubes);

		// brightness
		for (int a = 0; a < brightnessThreads.length; a++) {
			brightnessThreads[a] = new Thread(new BrightnessThread(a,
					brightnessThreads.length));
		}

		for (int a = 0; a < brightnessThreads.length; a++) {
			brightnessThreads[a].start();
		}

		try {
			for (int a = 0; a < brightnessThreads.length; a++) {
				brightnessThreads[a].join();
			}
		} catch (Exception e) {
		}

		for (int a = 0; a < allPCubes.length; a++) {
			allPCubes[a].drawPCube();
		}

		// print before initialize
		if (printed == false) {
			try {
				printToFile();
			} catch (Exception e) {
			}
		}

		// initialization
		for (int a = 0; a < initializeThreads.length; a++) {
			initializeThreads[a] = new Thread(new InitializeThread(a,
					initializeThreads.length));
		}

		for (int a = 0; a < initializeThreads.length; a++) {
			initializeThreads[a].start();
		}

		try {
			for (int a = 0; a < initializeThreads.length; a++) {
				initializeThreads[a].join();
			}
		} catch (Exception e) {
		}

	}

	/**
	 * Print to file. Print number of PCubes Print status of the shadow Print
	 * average FPS Print status of all PCubes
	 * 
	 * @throws FileNotFoundException
	 */
	public void printToFile() throws FileNotFoundException {

		out.println("Total Number of PCubes: " + allPCubes.length);
		out.println("The Lightsources are at:");

		for (int a = 0; a < lightSource.length; a++) {
			out.println(lightSource[a]);
		}

		if (turnOnShadow) {
			out.println("The Shadow is turned on");
		} else {
			out.println("The Shadow is turned off");
		}

		for (int a = 0; a < allPCubes.length; a++) {
			out.println("Cube: " + allPCubes[a]);
			boolean allLitUp = true;
			for (int b = 0; b < lightSource.length; b++) {
				if (allPCubes[a].covered[b] == true) {
					out.print("is not lit up ");
					out.println("by lightsource " + b);
					allLitUp = false;
				}
			}
			if (allLitUp) {
				out.println("is lit up by all lights");
			}
		}

	}

	/**
	 * Output to Output.txt to create a crash log.
	 * 
	 * @param status
	 *            £º why the program crash
	 */
	public void quit(int status) {
		if (status == 0) {
			out.println("Invalid input. Please follow the instructions.");
			out.println("Do not enter things other than numbers when you are not supposed to.");
			out.println("Not following the format may result in crashing. Wrong crash log may be generated due to unknown error."
					.toUpperCase());
		} else if (status == 1) {
			out.println("Invalid light source");
		} else if (status == 2) {
			out.println("Invalid Big Cube");
		} else if (status == 3) {
			out.println("Invalid Ground");
		} else {
			out.println("Invalid World");
		}

		out.println("You may want to see the sample input from the User Manual");
		out.println("If you are sure that the your input is valid, please contact the authors");
		out.flush();
		out.close();
		System.exit(0);
	}

	/**
	 * Calculates FPS
	 */
	public void FPS() {

		currentTime = System.currentTimeMillis();
		FPS = (float) (Math.round(100000.0 / (currentTime - lastRefreshTime)) / 100.0);
		String FPSShow = String.format("%.2f", FPS);

		fill(100);
		textAlign(LEFT);
		text("FPS: " + FPSShow, 40, 30);
		text("Total Pixel count: " + allPCubes.length, 40, 50);

		lastRefreshTime = currentTime;

		if (printed == false) {
			out.println("Your initial FPS is: " + FPSShow);
			out.println("This FPS is much lower than your actual FPS");
			out.flush();
			out.close();
			printed = true;
		}

	}

	/**
	 * The most fundamental element to the program Each PCube is a pixel
	 * 
	 * @author Eric
	 * 
	 */
	class PCube {

		boolean isLight;

		Vertex3D center;
		float size;
		float half;
		float radius2;
		Vertex3D[] vertices;

		float gray;

		boolean[] drawFaces; // defined from outside, position of the
								// cube relative to other cubes.
								// side, corner, face, blablal
		boolean[] doDrawFaces; // defined by the angle of the cube
		boolean[] actuallyDrawing;
		Quadrilateral[] faces;

		float[] distanceToLightSource2;
		// float brightness;
		boolean[] covered;

		int[][] vertexOnFace;

		// 0 front, 1 top, 2 right, 3 left, 4 back, 5 bottom

		/**
		 * Create a PCube
		 * 
		 * @param a
		 *            : the center
		 * @param s
		 *            : size
		 * @param doDrawFaces
		 *            : which faces are pointing outside
		 */
		public PCube(Vertex3D a, float s, boolean[] doDrawFaces) {
			size = s;
			half = (float) (s / 2.0);
			center = a;

			drawFaces = doDrawFaces;

			initializePCube();

		}

		/**
		 * set the color of this PCube
		 * 
		 * @param gray
		 *            : gray scale color
		 */
		public void setColor(float gray) {
			this.gray = gray;
		}

		/**
		 * determine the status of the PCube true = covered false = not covered
		 * 
		 * @param index
		 *            : with respect to which light source
		 * @param state
		 *            : true / false
		 */
		public void setCovered(int index, boolean state) {
			covered[index] = state;
		}

		/**
		 * Return the center coordinates of the PCube
		 * 
		 * @return center coordinates
		 */
		public Vertex3D getCenter() {
			return center;
		}

		/**
		 * Return information if the light reaches certain vertex on the PCube
		 * 
		 * @return true/false
		 */
		public boolean getLightSource() {
			return isLight;
		}

		/**
		 * Return the size of the PCube
		 * 
		 * @return the value of the size
		 */
		public float getSize() {
			return size;
		}

		/**
		 * Return the half of the size
		 * 
		 * @return half of the size
		 */
		public float getHalf() {
			return half;
		}

		/**
		 * **********************************hihi I am guessing again Return the
		 * direct distance from the center of the PCube to the lightsource
		 * 
		 * @return this distance
		 */
		public float getDistance2(int index) {
			return distanceToLightSource2[index];
		}

		/**
		 * Return if certain part of the PCube is covered by another object or
		 * not
		 * 
		 * @return true/false
		 */
		public boolean getCovered(int index) {
			return covered[index];
		}

		/**
		 * Return if certain part of the PCube should have its shadow
		 * 
		 * @return true/false
		 */
		public void initializeCovered() {
			covered = Function.initialize(covered, turnOnShadow);
		}

		/**
		 * Initialize the vertices of the PCube, the light and shadow of the
		 * PCube
		 */
		public void initializePCube() {

			isLight = false;
			radius2 = half * size;

			vertices = new Vertex3D[8];
			for (int b = 0; b < 8; b++) {
				vertices[b] = new Vertex3D();
			}

			vertices[0].setX(center.getX() - half);
			vertices[0].setY(center.getY() + half);
			vertices[0].setZ(center.getZ() + half);

			vertices[1].setX(center.getX() + half);
			vertices[1].setY(center.getY() + half);
			vertices[1].setZ(center.getZ() + half);

			vertices[2].setX(center.getX() + half);
			vertices[2].setY(center.getY() + half);
			vertices[2].setZ(center.getZ() - half);

			vertices[3].setX(center.getX() - half);
			vertices[3].setY(center.getY() + half);
			vertices[3].setZ(center.getZ() - half);

			vertices[4].setX(center.getX() - half);
			vertices[4].setY(center.getY() - half);
			vertices[4].setZ(center.getZ() + half);

			vertices[5].setX(center.getX() + half);
			vertices[5].setY(center.getY() - half);
			vertices[5].setZ(center.getZ() + half);

			vertices[6].setX(center.getX() + half);
			vertices[6].setY(center.getY() - half);
			vertices[6].setZ(center.getZ() - half);

			vertices[7].setX(center.getX() - half);
			vertices[7].setY(center.getY() - half);
			vertices[7].setZ(center.getZ() - half);

			vertexOnFace = new int[6][];

			int[] bla0 = { 0, 1, 5, 4 };
			vertexOnFace[0] = bla0;

			int[] bla1 = { 0, 1, 2, 3 };
			vertexOnFace[1] = bla1;

			int[] bla2 = { 1, 2, 6, 5 };
			vertexOnFace[2] = bla2;

			int[] bla3 = { 3, 0, 4, 7 };
			vertexOnFace[3] = bla3;

			int[] bla4 = { 2, 3, 7, 6 };
			vertexOnFace[4] = bla4;

			int[] bla5 = { 4, 5, 6, 7 };
			vertexOnFace[5] = bla5;

			this.setColor(0);
			distanceToLightSource2 = new float[lightSource.length];
			covered = new boolean[lightSource.length];
			covered = Function.initialize(covered, turnOnShadow);

		}

		/**
		 * Calculate and store the information of the PCube after rotation
		 */
		public void refreshPCube() {

			// distance to lightsource
			int distanceLength = distanceToLightSource2.length;
			for (int a = 0; a < distanceLength; a++) {
				distanceToLightSource2[a] = PFunction.distance2(lightSource[a],
						center);
			}

			covered = new boolean[lightSource.length];
			covered = Function.initialize(covered, turnOnShadow);

			// analyze which faces are shown
			// top left front, top right front ...
			// bottom right back, bottom left back

			float[] distances = new float[8];

			Vertex3D viewer = new Vertex3D(center.getX() + 1500
					* (float) 0.707106,
					center.getY() + 1500 * (float) 0.707106, 1500 + size);

			for (int a = 0; a < 8; a++) {
				distances[a] = PFunction.distance2(viewer, vertices[a]);
			}

			float lowest = 999999999;
			float index = 0;

			for (int a = 0; a < 8; a++) {
				if (distances[a] <= lowest) {
					lowest = distances[a];
					index = a;
				}
			}

			Vertex2D[] vertices2D = new Vertex2D[8];
			for (int a = 0; a < 8; a++) {
				vertices2D[a] = vertices[a].to2D();
				vertices2D[a] = vertices2D[a].getScreenPoint(width, height);
			}
			faces = new Quadrilateral[6];

			faces[0] = new Quadrilateral(vertices2D[0], vertices2D[1],
					vertices2D[5], vertices2D[4]);
			faces[1] = new Quadrilateral(vertices2D[0], vertices2D[1],
					vertices2D[2], vertices2D[3]);
			faces[2] = new Quadrilateral(vertices2D[1], vertices2D[2],
					vertices2D[6], vertices2D[5]);
			faces[3] = new Quadrilateral(vertices2D[3], vertices2D[0],
					vertices2D[4], vertices2D[7]);
			faces[4] = new Quadrilateral(vertices2D[2], vertices2D[3],
					vertices2D[7], vertices2D[6]);
			faces[5] = new Quadrilateral(vertices2D[4], vertices2D[5],
					vertices2D[6], vertices2D[7]);

			doDrawFaces = new boolean[6];
			doDrawFaces = Function.initialize(doDrawFaces, false);

			if (index == 0) {

				doDrawFaces[0] = true;
				doDrawFaces[1] = true;
				doDrawFaces[3] = true;

			} else if (index == 1) {

				doDrawFaces[0] = true;
				doDrawFaces[1] = true;
				doDrawFaces[2] = true;

			} else if (index == 2) {

				doDrawFaces[1] = true;
				doDrawFaces[2] = true;
				doDrawFaces[4] = true;

			} else if (index == 3) {

				doDrawFaces[1] = true;
				doDrawFaces[3] = true;
				doDrawFaces[4] = true;

			} else if (index == 4) {

				doDrawFaces[0] = true;
				doDrawFaces[3] = true;
				doDrawFaces[5] = true;

			} else if (index == 5) {

				doDrawFaces[0] = true;
				doDrawFaces[2] = true;
				doDrawFaces[5] = true;

			} else if (index == 6) {

				doDrawFaces[2] = true;
				doDrawFaces[4] = true;
				doDrawFaces[5] = true;

			} else if (index == 7) {

				doDrawFaces[3] = true;
				doDrawFaces[4] = true;
				doDrawFaces[5] = true;

			}

			actuallyDrawing = new boolean[6];

			for (int a = 0; a < 6; a++) {
				if (doDrawFaces[a] && drawFaces[a]) {
					actuallyDrawing[a] = true;
				}
			}

		}

		/**
		 * Analyze the brightness level that should be shown
		 */
		public void analyzeBrightness() {

			for (int b = 0; b < lightSource.length; b++) {

				if (covered[b] == false) {

					gray = gray
							+ 255
							/ (distanceToLightSource2[b]
									/ lightSource[b].strength / lightSource[b].strength);
				}
			}

		}

		/**
		 * Paint the PCube with leveled brightness colors only those faces that
		 * will be seen by the viewers are drawn
		 */
		public void drawPCube() {

			if (isLight == false) {

				fill(gray);
				stroke(gray);

				for (int a = 0; a < 6; a++) {
					// if (drawFaces[a] == doDrawFaces[a] && drawFaces[a] ==
					// true) {
					if (actuallyDrawing[a]) {
						faces[a].drawQuad();
					}
				}

			} else {

				fill(255, 240, 180);
				stroke(255, 240, 180);
				Vertex2D newPoint = center.to2D().getScreenPoint(width, height);
				ellipse(newPoint.getX(), newPoint.getY(), 20, 20);

			}
		}

		/**
		 * Rotate 8 vertices of PCube
		 * 
		 * @param rotateAngle
		 *            the rotation angle
		 * @param axis
		 *            determine if the rotation is horizontal or vertical
		 */
		public void rotate(float rotateAngle, boolean axis) {

			for (int a = 0; a < 8; a++) {
				vertices[a].rotate(
						PFunction.findCurrentAngle(vertices[a], axis),
						rotateAngle, axis);

			}
			center.rotate(PFunction.findCurrentAngle(center, axis),
					rotateAngle, axis);
		}

		/**
		 * Output the information of the center and the size of the PCube
		 */
		public String toString() {
			return "Center: " + center + " size: " + size;
		}
	}

	/**
	 * This serves as a collection of Vertex3D points so the quadrilaterals can
	 * be easily graphed.
	 * 
	 * @author Eric
	 * 
	 */
	class Quadrilateral {

		Vertex2D pointA;
		Vertex2D pointB;
		Vertex2D pointC;
		Vertex2D pointD;

		/**
		 * Establish the 4 vertices of a quadrilateral
		 * 
		 * @param a
		 *            the first vertex
		 * @param b
		 *            the second vertex
		 * @param c
		 *            the third vertex
		 * @param d
		 *            the forth vertex
		 */
		public Quadrilateral(Vertex2D a, Vertex2D b, Vertex2D c, Vertex2D d) {
			pointA = a;
			pointB = b;
			pointC = c;
			pointD = d;
		}

		/**
		 * draw the "shadow" of 3D square onto 2D. it should become a strange
		 * shaped parallelogram
		 */
		public void drawQuad() {
			quad(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY(),
					pointC.getX(), pointC.getY(), pointD.getX(), pointD.getY());
		}

	}

	/**
	 * A collection of PCubes, the "Big Cube"
	 * 
	 * @author Eric
	 * 
	 */
	class SolidCube {

		Vertex3D cubeCenter;

		float bigSideLength;
		float sideCubes;
		float size;

		float startOutsideX;
		float endOutsideX;
		float startInsideX;
		float endInsideX;

		float startOutsideY;
		float endOutsideY;
		float startInsideY;
		float endInsideY;

		float startOutsideZ;
		float endOutsideZ;
		float startInsideZ;
		float endInsideZ;

		int totalCubes;
		int index;

		Vertex3D[] centerPoint;
		boolean[][] drawFaces;

		/**
		 * Initialize Big Cube with give size and cubes per side
		 * 
		 * @param cubeSize
		 *            : size of the big cube
		 * @param cubeOnTheSide
		 *            : number of PCubes on one side
		 */
		public SolidCube(float cubeSize, int cubeOnTheSide) {

			cubeCenter = new Vertex3D();

			bigSideLength = cubeSize;
			sideCubes = cubeOnTheSide;

			createBigCube();

		}

		/**
		 * @param cubeSize
		 *            : side length of the cube
		 * @param cubeOnTheSide
		 *            : number of PCubes on one side, must be a multiple of 2 or
		 *            5
		 * @param centerOfTheCube
		 *            : center of the big cube
		 */
		public SolidCube(float cubeSize, int cubeOnTheSide,
				Vertex3D centerOfTheCube) {

			cubeCenter = centerOfTheCube;

			bigSideLength = cubeSize;
			sideCubes = cubeOnTheSide;
			createBigCube();

		}

		/**
		 * Create the Cube displays on the screen
		 */
		public void createBigCube() {
			

			size = bigSideLength / sideCubes;

			startOutsideX = (float) -((bigSideLength - size) / 2.0)
					+ cubeCenter.getX();
			endOutsideX = (float) ((bigSideLength - size) / 2.0)
					+ cubeCenter.getX();
			startInsideX = startOutsideX + size;
			endInsideX = endOutsideX - size;

			startOutsideY = (float) -((bigSideLength - size) / 2.0)
					+ cubeCenter.getY();
			endOutsideY = (float) ((bigSideLength - size) / 2.0)
					+ cubeCenter.getY();
			startInsideY = startOutsideY + size;
			endInsideY = endOutsideY - size;

			startOutsideZ = (float) -((bigSideLength - size) / 2.0)
					+ cubeCenter.getZ();
			endOutsideZ = (float) ((bigSideLength - size) / 2.0)
					+ cubeCenter.getZ();
			startInsideZ = startOutsideZ + size;
			endInsideZ = endOutsideZ - size;

			totalCubes = (int) ((sideCubes - 2) * (sideCubes - 2) * 6
					+ (sideCubes - 2) * 12 + 8);
			index = 0;

			objectStart = Function.expandArray(objectStart, allPCubes.length);
			allPCubes = PFunction.expandPCubeArray(allPCubes, allPCubes.length
					+ totalCubes);
			// allPCubeCenters = PFunction.expandVertex3DArray(allPCubeCenters,
			// allPCubeCenters.length + totalCubes);
			objectEnd = Function.expandArray(objectEnd, allPCubes.length);

			centerPoint = new Vertex3D[totalCubes];
			drawFaces = new boolean[totalCubes][6];

			createFaces();
			createBian();
			createCorners();

			for (int a = 0; a < index; a++) {
				// allPCubeCenters[objectStart[objectCount] + a] =
				// centerPoint[a];
				allPCubes[objectStart[objectCount] + a] = new PCube(
						centerPoint[a], size, drawFaces[a]);
			}

			objectCount++; // objectCount is only increased in the very end

		}

		/**
		 * Create the 6 faces of the big cube
		 */
		public void createFaces() {
			float x, y, z;

			// front
			z = endOutsideZ;
			for (x = startInsideX; x <= endInsideX; x = x + size) {
				for (y = startInsideY; y <= endInsideY; y = y + size) {
					centerPoint[index] = new Vertex3D(x, y, z);

					drawFaces[index][0] = true;
					drawFaces[index][1] = false;
					drawFaces[index][2] = false;
					drawFaces[index][3] = false;
					drawFaces[index][4] = false;
					drawFaces[index][5] = false;

					index++;
				}
			}

			// back
			z = startOutsideZ;
			for (x = startInsideX; x <= endInsideX; x = x + size) {
				for (y = startInsideY; y <= endInsideY; y = y + size) {
					centerPoint[index] = new Vertex3D(x, y, z);

					drawFaces[index][0] = false;
					drawFaces[index][1] = false;
					drawFaces[index][2] = false;
					drawFaces[index][3] = false;
					drawFaces[index][4] = true;
					drawFaces[index][5] = false;

					index++;
				}
			}

			// left
			x = startOutsideX;
			for (y = startInsideY; y <= endInsideY; y = y + size) {
				for (z = startInsideZ; z <= endInsideZ; z = z + size) {
					centerPoint[index] = new Vertex3D(x, y, z);

					drawFaces[index][0] = false;
					drawFaces[index][1] = false;
					drawFaces[index][2] = false;
					drawFaces[index][3] = true;
					drawFaces[index][4] = false;
					drawFaces[index][5] = false;

					index++;
				}
			}

			// right
			x = endOutsideX;
			for (y = startInsideY; y <= endInsideY; y = y + size) {
				for (z = startInsideZ; z <= endInsideZ; z = z + size) {
					centerPoint[index] = new Vertex3D(x, y, z);

					drawFaces[index][0] = false;
					drawFaces[index][1] = false;
					drawFaces[index][2] = true;
					drawFaces[index][3] = false;
					drawFaces[index][4] = false;
					drawFaces[index][5] = false;

					index++;
				}
			}

			// top
			y = endOutsideY;
			for (x = startInsideX; x <= endInsideX; x = x + size) {
				for (z = startInsideZ; z <= endInsideZ; z = z + size) {
					centerPoint[index] = new Vertex3D(x, y, z);

					drawFaces[index][0] = false;
					drawFaces[index][1] = true;
					drawFaces[index][2] = false;
					drawFaces[index][3] = false;
					drawFaces[index][4] = false;
					drawFaces[index][5] = false;

					index++;
				}
			}

			// bottom
			y = startOutsideY;
			for (x = startInsideX; x <= endInsideX; x = x + size) {
				for (z = startInsideZ; z <= endInsideZ; z = z + size) {
					centerPoint[index] = new Vertex3D(x, y, z);

					drawFaces[index][0] = false;
					drawFaces[index][1] = false;
					drawFaces[index][2] = false;
					drawFaces[index][3] = false;
					drawFaces[index][4] = false;
					drawFaces[index][5] = true;

					index++;
				}
			}

		}

		/**
		 * Create the 8 corners of the big cube
		 */
		public void createCorners() {

			// corners
			// 7
			centerPoint[index] = new Vertex3D(startOutsideX, startOutsideY,
					startOutsideZ);

			drawFaces[index][0] = false;
			drawFaces[index][1] = false;
			drawFaces[index][2] = false;
			drawFaces[index][3] = true;
			drawFaces[index][4] = true;
			drawFaces[index][5] = true;

			index++;

			// 4
			centerPoint[index] = new Vertex3D(startOutsideX, startOutsideY,
					endOutsideZ);

			drawFaces[index][0] = true;
			drawFaces[index][1] = false;
			drawFaces[index][2] = false;
			drawFaces[index][3] = true;
			drawFaces[index][4] = false;
			drawFaces[index][5] = true;

			index++;

			// 3
			centerPoint[index] = new Vertex3D(startOutsideX, endOutsideY,
					startOutsideZ);

			drawFaces[index][0] = false;
			drawFaces[index][1] = true;
			drawFaces[index][2] = false;
			drawFaces[index][3] = true;
			drawFaces[index][4] = true;
			drawFaces[index][5] = false;

			index++;

			// 0
			centerPoint[index] = new Vertex3D(startOutsideX, endOutsideY,
					endOutsideZ);

			drawFaces[index][0] = true;
			drawFaces[index][1] = true;
			drawFaces[index][2] = false;
			drawFaces[index][3] = true;
			drawFaces[index][4] = false;
			drawFaces[index][5] = false;

			index++;

			// 6
			centerPoint[index] = new Vertex3D(endOutsideX, startOutsideY,
					startOutsideZ);

			drawFaces[index][0] = false;
			drawFaces[index][1] = false;
			drawFaces[index][2] = true;
			drawFaces[index][3] = false;
			drawFaces[index][4] = true;
			drawFaces[index][5] = true;

			index++;

			// 5
			centerPoint[index] = new Vertex3D(endOutsideX, startOutsideY,
					endOutsideZ);

			drawFaces[index][0] = true;
			drawFaces[index][1] = false;
			drawFaces[index][2] = true;
			drawFaces[index][3] = false;
			drawFaces[index][4] = false;
			drawFaces[index][5] = true;

			index++;

			// 2
			centerPoint[index] = new Vertex3D(endOutsideX, endOutsideY,
					startOutsideZ);

			drawFaces[index][0] = false;
			drawFaces[index][1] = true;
			drawFaces[index][2] = true;
			drawFaces[index][3] = false;
			drawFaces[index][4] = true;
			drawFaces[index][5] = false;

			index++;

			// 1
			centerPoint[index] = new Vertex3D(endOutsideX, endOutsideY,
					endOutsideZ);

			drawFaces[index][0] = true;
			drawFaces[index][1] = true;
			drawFaces[index][2] = true;
			drawFaces[index][3] = false;
			drawFaces[index][4] = false;
			drawFaces[index][5] = false;

			index++;
		}

		/**
		 * Create the 8 sides of the big cube
		 */
		public void createBian() {
			float x, y, z;
			// bian, 12 of them

			for (x = startInsideX; x <= endInsideX; x = x + size) {

				// 7 to 6
				y = startOutsideY;
				z = startOutsideZ;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = false;
				drawFaces[index][2] = false;
				drawFaces[index][3] = false;
				drawFaces[index][4] = true;
				drawFaces[index][5] = true;

				index++;

				// 4 to 5
				y = startOutsideY;
				z = endOutsideZ;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = true;
				drawFaces[index][1] = false;
				drawFaces[index][2] = false;
				drawFaces[index][3] = false;
				drawFaces[index][4] = false;
				drawFaces[index][5] = true;

				index++;

				// 3 to 2
				y = endOutsideY;
				z = startOutsideZ;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = true;
				drawFaces[index][2] = false;
				drawFaces[index][3] = false;
				drawFaces[index][4] = true;
				drawFaces[index][5] = false;

				index++;

				// 0 to 1
				y = endOutsideY;
				z = endOutsideZ;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = true;
				drawFaces[index][1] = true;
				drawFaces[index][2] = false;
				drawFaces[index][3] = false;
				drawFaces[index][4] = false;
				drawFaces[index][5] = false;

				index++;
			}

			for (y = startInsideY; y <= endInsideY; y = y + size) {

				// 3 to 7
				x = startOutsideX;
				z = startOutsideZ;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = false;
				drawFaces[index][2] = false;
				drawFaces[index][3] = true;
				drawFaces[index][4] = true;
				drawFaces[index][5] = false;

				index++;

				// 0 to 4
				x = startOutsideX;
				z = endOutsideZ;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = true;
				drawFaces[index][1] = false;
				drawFaces[index][2] = false;
				drawFaces[index][3] = true;
				drawFaces[index][4] = false;
				drawFaces[index][5] = false;

				index++;

				// 2 to 6
				x = endOutsideX;
				z = startOutsideZ;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = false;
				drawFaces[index][2] = true;
				drawFaces[index][3] = false;
				drawFaces[index][4] = true;
				drawFaces[index][5] = false;

				index++;

				// 1 to 5
				x = endOutsideX;
				z = endOutsideZ;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = true;
				drawFaces[index][1] = false;
				drawFaces[index][2] = true;
				drawFaces[index][3] = false;
				drawFaces[index][4] = false;
				drawFaces[index][5] = false;

				index++;
			}

			for (z = startInsideZ; z <= endInsideZ; z = z + size) {

				// 4 to 7
				x = startOutsideX;
				y = startOutsideY;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = false;
				drawFaces[index][2] = false;
				drawFaces[index][3] = true;
				drawFaces[index][4] = false;
				drawFaces[index][5] = true;

				index++;

				// 0 to 3
				x = startOutsideX;
				y = endOutsideY;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = true;
				drawFaces[index][2] = false;
				drawFaces[index][3] = true;
				drawFaces[index][4] = false;
				drawFaces[index][5] = false;

				index++;

				// 5 to 6
				x = endOutsideX;
				y = startOutsideY;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = false;
				drawFaces[index][2] = true;
				drawFaces[index][3] = false;
				drawFaces[index][4] = false;
				drawFaces[index][5] = true;

				index++;

				// 1 to 2
				x = endOutsideX;
				y = endOutsideY;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = true;
				drawFaces[index][2] = true;
				drawFaces[index][3] = false;
				drawFaces[index][4] = false;
				drawFaces[index][5] = false;

				index++;
			}
		}

	}

	/**
	 * Creates ground
	 * 
	 * @author Eric
	 * 
	 */
	class Ground {

		float bottom;

		int sideCubes;
		float size;
		float sideLength = 800;

		float startOutside;
		float endOutside;
		float startInside;
		float endInside;

		Vertex3D[] centerPoint;
		boolean[][] drawFaces;
		int index;

		/**
		 * @param cubes
		 *            : number of cubes on one side
		 * @param bottomCoordinate
		 *            : the "ground" lies below (0,0,0). The "depth" of the
		 *            ground.
		 */
		public Ground(int cubes, float bottomCoordinate) {

			bottom = bottomCoordinate;
			sideCubes = cubes;

			size = sideLength / sideCubes;

			startOutside = (float) -((sideLength - size) / 2.0);
			endOutside = (float) ((sideLength - size) / 2.0);
			startInside = startOutside + size;
			endInside = endOutside - size;

			int totalCubes = sideCubes * sideCubes * 2;
			index = 0;

			objectStart = Function.expandArray(objectStart, allPCubes.length);
			allPCubes = PFunction.expandPCubeArray(allPCubes, allPCubes.length
					+ totalCubes);
			// allPCubeCenters = PFunction.expandVertex3DArray(allPCubeCenters,
			// allPCubeCenters.length + totalCubes);
			objectEnd = Function.expandArray(objectEnd, allPCubes.length);

			centerPoint = new Vertex3D[totalCubes];
			drawFaces = new boolean[totalCubes][6];

			createGround();

			for (int a = 0; a < index; a++) {
				// allPCubeCenters[objectStart[objectCount] + a] =
				// centerPoint[a];
				allPCubes[objectStart[objectCount] + a] = new PCube(
						centerPoint[a], size, drawFaces[a]);
			}

			objectCount++; // objectCount is only increased in the very end
		}

		/**
		 * Create the rectangular 3-D ground of the world
		 */
		public void createGround() {

			float x, y, z;

			y = bottom;

			// center
			for (x = startInside; x <= endInside; x = x + size) {
				for (z = startInside; z <= endInside; z = z + size) {
					centerPoint[index] = new Vertex3D(x, y, z);

					drawFaces[index][0] = false;
					drawFaces[index][1] = true;
					drawFaces[index][2] = false;
					drawFaces[index][3] = false;
					drawFaces[index][4] = false;
					drawFaces[index][5] = false;

					index++;
				}
			}

			// Bian
			for (x = startInside; x <= endInside; x = x + size) {

				// side on the back
				z = startOutside;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = true;
				drawFaces[index][2] = false;
				drawFaces[index][3] = false;
				drawFaces[index][4] = true;
				drawFaces[index][5] = false;

				index++;

				// side on the front
				z = endOutside;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = true;
				drawFaces[index][1] = true;
				drawFaces[index][2] = false;
				drawFaces[index][3] = false;
				drawFaces[index][4] = false;
				drawFaces[index][5] = false;

				index++;

			}

			for (z = startInside; z <= endInside; z = z + size) {

				// side on the left
				x = startOutside;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = true;
				drawFaces[index][2] = false;
				drawFaces[index][3] = true;
				drawFaces[index][4] = false;
				drawFaces[index][5] = false;

				index++;

				// side on the right

				x = endOutside;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = true;
				drawFaces[index][2] = true;
				drawFaces[index][3] = false;
				drawFaces[index][4] = false;
				drawFaces[index][5] = false;

				index++;

			}

			// corners
			// back left
			centerPoint[index] = new Vertex3D(startOutside, y, startOutside);
			drawFaces[index][0] = false;
			drawFaces[index][1] = true;
			drawFaces[index][2] = false;
			drawFaces[index][3] = true;
			drawFaces[index][4] = true;
			drawFaces[index][5] = false;

			index++;

			// front left
			centerPoint[index] = new Vertex3D(startOutside, y, endOutside);
			drawFaces[index][0] = true;
			drawFaces[index][1] = true;
			drawFaces[index][2] = false;
			drawFaces[index][3] = true;
			drawFaces[index][4] = false;
			drawFaces[index][5] = false;

			index++;

			// back right
			centerPoint[index] = new Vertex3D(endOutside, y, startOutside);
			drawFaces[index][0] = false;
			drawFaces[index][1] = true;
			drawFaces[index][2] = true;
			drawFaces[index][3] = false;
			drawFaces[index][4] = true;
			drawFaces[index][5] = false;

			index++;

			// front right
			centerPoint[index] = new Vertex3D(endOutside, y, endOutside);
			drawFaces[index][0] = true;
			drawFaces[index][1] = true;
			drawFaces[index][2] = true;
			drawFaces[index][3] = false;
			drawFaces[index][4] = false;
			drawFaces[index][5] = false;

			index++;

			// ---------- layer 2 ----------
			y = bottom - size;

			// center
			for (x = startInside; x <= endInside; x = x + size) {
				for (z = startInside; z <= endInside; z = z + size) {
					centerPoint[index] = new Vertex3D(x, y, z);

					drawFaces[index][0] = false;
					drawFaces[index][1] = false;
					drawFaces[index][2] = false;
					drawFaces[index][3] = false;
					drawFaces[index][4] = false;
					drawFaces[index][5] = true;

					index++;
				}
			}

			// Bian
			for (x = startInside; x <= endInside; x = x + size) {

				// side on the back
				z = startOutside;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = false;
				drawFaces[index][2] = false;
				drawFaces[index][3] = false;
				drawFaces[index][4] = true;
				drawFaces[index][5] = true;

				index++;

				// side on the front
				z = endOutside;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = true;
				drawFaces[index][1] = false;
				drawFaces[index][2] = false;
				drawFaces[index][3] = false;
				drawFaces[index][4] = false;
				drawFaces[index][5] = true;

				index++;

			}

			for (z = startInside; z <= endInside; z = z + size) {

				// side on the left
				x = startOutside;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = false;
				drawFaces[index][2] = false;
				drawFaces[index][3] = true;
				drawFaces[index][4] = false;
				drawFaces[index][5] = true;

				index++;

				// side on the right

				x = endOutside;
				centerPoint[index] = new Vertex3D(x, y, z);

				drawFaces[index][0] = false;
				drawFaces[index][1] = false;
				drawFaces[index][2] = true;
				drawFaces[index][3] = false;
				drawFaces[index][4] = false;
				drawFaces[index][5] = true;

				index++;

			}

			// corners
			// back left
			centerPoint[index] = new Vertex3D(startOutside, y, startOutside);
			drawFaces[index][0] = false;
			drawFaces[index][1] = false;
			drawFaces[index][2] = false;
			drawFaces[index][3] = true;
			drawFaces[index][4] = true;
			drawFaces[index][5] = true;

			index++;

			// front left
			centerPoint[index] = new Vertex3D(startOutside, y, endOutside);
			drawFaces[index][0] = true;
			drawFaces[index][1] = false;
			drawFaces[index][2] = false;
			drawFaces[index][3] = true;
			drawFaces[index][4] = false;
			drawFaces[index][5] = true;

			index++;

			// back right
			centerPoint[index] = new Vertex3D(endOutside, y, startOutside);
			drawFaces[index][0] = false;
			drawFaces[index][1] = false;
			drawFaces[index][2] = true;
			drawFaces[index][3] = false;
			drawFaces[index][4] = true;
			drawFaces[index][5] = true;

			index++;

			// front right
			centerPoint[index] = new Vertex3D(endOutside, y, endOutside);
			drawFaces[index][0] = true;
			drawFaces[index][1] = false;
			drawFaces[index][2] = true;
			drawFaces[index][3] = false;
			drawFaces[index][4] = false;
			drawFaces[index][5] = true;

			index++;

		}
	}

	/**
	 * The control bars
	 * 
	 * @author Eric
	 * 
	 */
	class Bar {

		float startX;
		float startY;
		float endX;
		float endY;
		float middleX;
		float totalLength;
		float halfLength;
		float totalRange; // note that this is the length
							// in two directions
		float halfRange;

		float redM;
		float greenM;
		float blueM;
		float redB;
		float greenB;
		float blueB;

		float topX;
		float topY;
		float bottomX;
		float bottomY;
		float centerX;
		float centerY;

		boolean special;

		/**
		 * Initialize the Bar with given index and range
		 * 
		 * @param index : index, 0, 1, 2 ,3 represent X, Y, Z, and strength
		 * @param range : maximum value it can represent
		 */
		public Bar(int index, float range) {

			if (index == 3) {
				special = true;
			}

			startX = 145 * index + 20;
			startY = height - 30;
			endX = startX + 125;
			endY = startY + 8;
			totalLength = endX - startX;
			halfLength = (float) (totalLength / 2.0);
			middleX = (float) ((endX + startX) / 2.0);
			totalRange = range;
			halfRange = (float) (totalRange / 2.0);

			redM = 140;
			greenM = 80;
			blueM = 35;

			redB = 40;
			greenB = 200;
			blueB = 120;

			centerX = middleX;
			centerY = (float) ((endY + startY) / 2.0);
			topX = centerX - 5;
			topY = centerY - 8;
			bottomX = centerX + 5;
			bottomY = centerY + 8;

		}

		/**
		 * set the position of the cube according to the position of its center
		 * 
		 * @param value
		 *            : the actual value that the bar represents
		 */
		public void setPosition(float value) {

			if (special) {
				centerX = value / totalRange * totalLength + startX;
			} else {
				centerX = value / halfRange * halfLength + middleX;
			}

			topX = centerX - 5;
			topY = centerY - 8;
			bottomX = centerX + 5;
			bottomY = centerY + 8;
		}

		/**
		 * Return of the x component of the starting point
		 * 
		 * @return x the x component of the starting point
		 */
		public float getStartX() {
			return startX;
		}

		/**
		 * Return of the y component of the starting point
		 * 
		 * @return y the y component of the starting point
		 */
		public float getStartY() {
			return startY;
		}

		/**
		 * Return of the x component of the ending point
		 * 
		 * @return x the x component of the ending point
		 */
		public float getEndX() {
			return endX;
		}

		/**
		 * Return of the y component of the ending point
		 * 
		 * @return y the y component of the ending point
		 */
		public float getEndY() {
			return endY;
		}

		/**
		 * Check if the mouse in on top of this bar(s)
		 * 
		 * @param mousePlaceX
		 * @param mousePlaceY
		 * @return
		 */
		public boolean inControl(float mousePlaceX, float mousePlaceY) {
			if (special) {

				if ((mousePlaceX > startX && mousePlaceX < endX)
						&& (mousePlaceY > startY - 10 && mousePlaceY < endY + 10)) {
					return true;
				} else {
					return false;

				}
			} else {

				if ((mousePlaceX > startX + 15 && mousePlaceX < endX - 15)
						&& (mousePlaceY > startY - 10 && mousePlaceY < endY + 10)) {
					return true;
				} else {
					return false;
				}

			}
		}

		/**
		 * get the actual value that the bar represents
		 * 
		 * @param mousePlace
		 *            : x-coordinate of the mouse
		 * @return the value that the bar represents
		 */
		public float getPosition(float mousePlace) {

			if (special) {
				return (mousePlace - startX) / totalLength * totalRange;
			} else {
				return (mousePlace - middleX) / halfLength * halfRange;
			}
		}

		/**
		 * change the position of the slide so it moves according to the mouse
		 * 
		 * @param mousePlace
		 *            : x-coordinate of the mouse
		 */
		public void moveBar(float mousePlace) {
			centerX = mousePlace;
			topX = centerX - 5;
			topY = centerY - 8;
			bottomX = centerX + 5;
			bottomY = centerY + 8;

		}

		/**
		 * draw the control bar
		 */
		public void drawBar() {
			stroke(redM, greenM, blueM);
			fill(redM, greenM, blueM);
			drawRoundedRect(startX, startY, endX, endY, 4);

			stroke(redB, greenB, blueB);
			fill(redB, greenB, blueB);
			drawRoundedRect(topX, topY, bottomX, bottomY, 3);

		}
	}

	/**
	 * This is the option switch to choose which light source are being
	 * controled by the control bars
	 * 
	 * @author Eric
	 */
	class Option {

		int index;
		final float size = 15;
		final float startX = width - 120;
		final float endX = startX + size;
		float startY;
		float endY;
		boolean checked;
		PFont labelFont;

		/**
		 * Determine the position of the option bars
		 * 
		 * @param num
		 *            : index of the option bar
		 */
		public Option(int num) {
			index = num;
			startY = height - index * 30 - 30;
			endY = startY + size;

			labelFont = createFont("Impact", 15, true);

			if (index == 0) {
				checked = true;
			} else {
				checked = false;
			}
		}

		/**
		 * marked this option bar as checked
		 */
		public void check() {
			checked = true;
		}

		/**
		 * mark this option bar as unchecked
		 */
		public void uncheck() {
			checked = false;
		}

		/**
		 * check to see if mouse is on top of the option
		 * 
		 * @param mousePlaceX
		 *            : x-ccordinate of mouse
		 * @param mousePlaceY
		 *            : y-coordinate of mouse
		 * @return true, in control, false, not in control
		 */
		public boolean inControl(float mousePlaceX, float mousePlaceY) {
			if ((mousePlaceX > startX && mousePlaceX < startX + 90)
					&& (mousePlaceY > startY - 10 && mousePlaceY < endY + 10)) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * draw the option bar
		 */
		public void drawOption() {
			if (checked) {
				fill(240, 230, 170);
				stroke(240, 230, 170);
				drawRoundedRect(startX, startY, endX, endY, 6);
				fill(210, 155, 190);
				textAlign(LEFT);
				textFont(labelFont);
				text("Light Source " + index, endX + 5, endY);
			} else {
				fill(15, 100, 140);
				stroke(15, 100, 140);
				drawRoundedRect(startX, startY, endX, endY, 6);
				fill(100, 80, 60);
				textAlign(LEFT);
				textFont(labelFont);
				text("Light Source " + index, endX + 5, endY);
			}
		}

	}

	/**
	 * This is the switch for the shadow ON/OFF switch.
	 * 
	 * @author Eric
	 * 
	 */
	class Switch {

		int index;
		final float size = 15;
		final float startX = width - 120;
		final float endX = startX + size;
		final float startY = 30;
		final float endY = startY + size;
		boolean checked;
		PFont labelFont;

		/**
		 * create a switch with input state
		 * 
		 * @param state
		 *            : true or false, state of the switch
		 */
		public Switch(boolean state) {

			labelFont = createFont("Impact", 15, true);

			checked = state;
		}

		/**
		 * turn the switch on
		 */
		public void check() {
			checked = true;
		}

		/**
		 * turn the switch off
		 */
		public void uncheck() {
			checked = false;
		}

		/**
		 * Check if mouse in in control of the switch
		 * 
		 * @param mousePlaceX
		 *            :X-coordinates of the mouse
		 * @param mousePlaceY
		 *            :Y-coordinates of the mouse
		 * @return true, if in control, false, if not in control
		 */
		public boolean inControl(float mousePlaceX, float mousePlaceY) {
			if ((mousePlaceX > startX && mousePlaceX < startX + 90)
					&& (mousePlaceY > startY - 10 && mousePlaceY < endY + 10)) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Draw the switch
		 */
		public void drawSwitch() {
			if (checked) {
				fill(240, 230, 170);
				stroke(240, 230, 170);
				drawRoundedRect(startX, startY, endX, endY, 6);
				fill(210, 155, 190);
				textAlign(LEFT);
				textFont(labelFont);
				text("Shadow On", endX + 5, endY);
			} else {
				fill(15, 100, 140);
				stroke(15, 100, 140);
				drawRoundedRect(startX, startY, endX, endY, 6);
				fill(100, 80, 60);
				textAlign(LEFT);
				textFont(labelFont);
				text("Shadow Off ", endX + 5, endY);
			}
		}

	}

	/**
	 * Draw a rectangle with rounded angle for the UI
	 * 
	 * @param x1
	 *            : top left corner
	 * @param y1
	 *            : top left corner
	 * @param x2
	 *            : bottom right corner
	 * @param y2
	 *            : bottom right corner
	 * @param r
	 *            : radius of the "rounded" angle
	 */
	public void drawRoundedRect(float x1, float y1, float x2, float y2, float r) {

		if (x1 >= x2) {
			float storeX = x1;
			x1 = x2;
			x2 = storeX;

			float storeY = y1;
			y1 = y2;
			y2 = storeY;
		}

		if (y1 >= y2) {
			float storeX = x1;
			x1 = x2;
			x2 = storeX;

			float storeY = y1;
			y1 = y2;
			y2 = storeY;
		}

		float innerX1 = x1 + r;
		float innerY1 = y1 + r;
		float innerX2 = x2 - r;
		float innerY2 = y2 - r;

		rectMode(CORNERS);

		rect(innerX1, innerY1, innerX2, innerY2); // center
		rect(x1, innerY1, innerX1, innerY2); // left
		rect(innerX2, innerY1, x2, innerY2); // right
		rect(innerX1, y1, innerX2, innerY1); // top
		rect(innerX1, innerY1, innerX2, y2); // bottom

		arc(innerX1, innerY1, r * 2, r * 2, (float) Math.toRadians(180),
				(float) Math.toRadians(270));
		arc(innerX1, innerY2, r * 2, r * 2, (float) Math.toRadians(90),
				(float) Math.toRadians(180));
		arc(innerX2, innerY1, r * 2, r * 2, (float) Math.toRadians(270),
				(float) Math.toRadians(360));
		arc(innerX2, innerY2, r * 2, r * 2, (float) Math.toRadians(0),
				(float) Math.toRadians(90));
	}

	/**
	 * Generates all points on the boundary of the world
	 */
	public void createBoundM1() {

		int resolution = resolutionOfWorld;

		float bigSideLength = 1000;
		int sidePoints = (int) (bigSideLength / resolution);
		int interval = resolution;

		float startOutside;
		float endOutside;
		float startInside;
		float endInside;

		int totalPoints;
		int index = 0;

		startOutside = (float) -((bigSideLength - interval) / 2.0);
		endOutside = (float) ((bigSideLength - interval) / 2.0);
		startInside = startOutside + interval;
		endInside = endOutside - interval;

		totalPoints = (int) ((sidePoints - 2) * (sidePoints - 2) * 6
				+ (sidePoints - 2) * 12 + 8);
		index = 0;

		boundPoints = new Vertex3D[totalPoints];
		objectEnd = Function.expandArray(objectEnd, allPCubes.length);

		float x, y, z;

		// face
		// front
		z = endOutside;
		for (x = startInside; x <= endInside; x = x + interval) {
			for (y = startInside; y <= endInside; y = y + interval) {
				boundPoints[index] = new Vertex3D(x, y, z);
				index++;
			}
		}

		// back
		z = startOutside;
		for (x = startInside; x <= endInside; x = x + interval) {
			for (y = startInside; y <= endInside; y = y + interval) {
				boundPoints[index] = new Vertex3D(x, y, z);
				index++;
			}
		}

		// left
		x = startOutside;
		for (y = startInside; y <= endInside; y = y + interval) {
			for (z = startInside; z <= endInside; z = z + interval) {
				boundPoints[index] = new Vertex3D(x, y, z);
				index++;
			}
		}

		// right
		x = endOutside;
		for (y = startInside; y <= endInside; y = y + interval) {
			for (z = startInside; z <= endInside; z = z + interval) {
				boundPoints[index] = new Vertex3D(x, y, z);
				index++;
			}
		}

		// top
		y = endOutside;
		for (x = startInside; x <= endInside; x = x + interval) {
			for (z = startInside; z <= endInside; z = z + interval) {
				boundPoints[index] = new Vertex3D(x, y, z);
				index++;
			}
		}

		// bottom
		y = startOutside;
		for (x = startInside; x <= endInside; x = x + interval) {
			for (z = startInside; z <= endInside; z = z + interval) {
				boundPoints[index] = new Vertex3D(x, y, z);
				index++;
			}
		}

		// corners
		// 7
		boundPoints[index] = new Vertex3D(startOutside, startOutside,
				startOutside);
		index++;
		// 4
		boundPoints[index] = new Vertex3D(startOutside, startOutside,
				endOutside);
		index++;

		// 3
		boundPoints[index] = new Vertex3D(startOutside, endOutside,
				startOutside);
		index++;

		// 0
		boundPoints[index] = new Vertex3D(startOutside, endOutside, endOutside);
		index++;

		// 6
		boundPoints[index] = new Vertex3D(endOutside, startOutside,
				startOutside);
		index++;

		// 5
		boundPoints[index] = new Vertex3D(endOutside, startOutside, endOutside);
		index++;

		// 2
		boundPoints[index] = new Vertex3D(endOutside, endOutside, startOutside);
		index++;

		// 1
		boundPoints[index] = new Vertex3D(endOutside, endOutside, endOutside);
		index++;

		// Bian
		for (x = startInside; x <= endInside; x = x + interval) {

			// 7 to 6
			y = startOutside;
			z = startOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;

			// 4 to 5
			y = startOutside;
			z = endOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;

			// 3 to 2
			y = endOutside;
			z = startOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;

			// 0 to 1
			y = endOutside;
			z = endOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;
		}

		for (y = startInside; y <= endInside; y = y + interval) {

			// 3 to 7
			x = startOutside;
			z = startOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;

			// 0 to 4
			x = startOutside;
			z = endOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;

			// 2 to 6
			x = endOutside;
			z = startOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;

			// 1 to 5
			x = endOutside;
			z = endOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;
		}

		for (z = startInside; z <= endInside; z = z + interval) {

			// 4 to 7
			x = startOutside;
			y = startOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;

			// 0 to 3
			x = startOutside;
			y = endOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;

			// 5 to 6
			x = endOutside;
			y = startOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;

			// 1 to 2
			x = endOutside;
			y = endOutside;
			boundPoints[index] = new Vertex3D(x, y, z);
			index++;
		}

	}

	/**
	 * Skip certain number of lines in input
	 * 
	 * @param lines
	 *            : number of lines need to be skiped
	 */
	public void waste(int lines) {
		String bla;
		for (int a = 0; a < lines; a++) {
			if (in.hasNext()) {
				bla = in.nextLine();
			}
		}
	}
}
