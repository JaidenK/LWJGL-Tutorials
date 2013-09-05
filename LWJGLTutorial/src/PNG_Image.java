import java.io.IOException;
import java.io.FileInputStream;

import de.matthiasmann.twl.utils.PNGDecoder;


public class PNG_Image extends PNGDecoder {
	
	private float glVers = LWJGLTutorial.glVersionF;
	private float[] corners = new float[12];	//3 points * 4 corners of the image. Should go counter-clockwise.
	private float scale;
	private int scrnWidth = LWJGLTutorial.WIDTH;
	private int scrnHeight = LWJGLTutorial.HEIGHT;

	public PNG_Image(float[] corners, String filePath, float scale) throws IOException {
		super(new FileInputStream(filePath));
		this.scale = scale;
		if(corners.length == 12) {
			this.corners = corners;
		}
		else if(corners.length < 12 && corners.length >= 3) {
			//you should pick the "bottom-left" vertex and use the PNG's width and height values to fill in the blanks
		}
	}
	
	public void draw() {	//check the PNGDecoder tutorial on LWJGL's wiki for a short tutorial, I'll figure out 3.2 rendering if you don't
		if(glVers <= 3.1) {
			
		}
		else if(glVers >= 3.2) {
			
		}
	}
	
	public void translate(float x, float y) {	//should mimic OpenGL_Object's method of rendering
		
	}
	
	public void translate(int xPixels, int yPixels) {
		float scaledX = (float) xPixels/scrnWidth;
		float scaledY = (float) yPixels/scrnHeight;
		
		translate(scaledX, scaledY);
	}
	
	/*
	public void rotate(float z) {	//should mimic OpenGL_Object's rotate method (I'll get on it, don't worry)
		
	}
	*/
}