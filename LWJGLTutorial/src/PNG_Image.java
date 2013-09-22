import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;


public class PNG_Image {
	
	private PNGDecoder decoder;
	ByteBuffer buffer;
	private float glVers;
	private float[] corner;
	private float scale;
	private int scrnWidth = LWJGLTutorial.WIDTH;
	private int scrnHeight = LWJGLTutorial.HEIGHT;
	private int textureID;

	public PNG_Image(float[] corners, String filePath, float scale) throws IOException {
		glVers = LWJGLTutorial.glVersionF;
		InputStream image = new FileInputStream(filePath);
		try {
			decoder = new PNGDecoder(image);
			if(decoder.hasAlpha()) {
				buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
				decoder.decode(buffer, decoder.getWidth() * 4, Format.RGBA);
			}
			else if(decoder.isRGB()) {
				buffer = ByteBuffer.allocateDirect(3 * decoder.getWidth() * decoder.getHeight());
				decoder.decode(buffer, decoder.getWidth() * 3, Format.RGB);
			} else {
				buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
				decoder.decode(buffer, decoder.getWidth() * 4, decoder.decideTextureFormat(Format.RGBA));
			}
			buffer.flip();
		} finally {
			image.close();
		}
		textureID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void draw() {
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 	//sorry for the wonky spacing here, but otherwise
						  0, 					//this extends way out and it's harder to follow
						  GL11.GL_RGB, 			
						  decoder.getWidth(), decoder.getHeight(), //anyway, what this does is upload 
						  0, 									   //the texture data
						  GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 
						  buffer);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	public void translate(float x, float y) {
		
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
	
	public void releaseGLMemory() {
		GL11.glDeleteTextures(textureID);
	}
}