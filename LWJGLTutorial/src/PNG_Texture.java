import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class PNG_Texture {
	
	private float[] corners;
	private int screenWidth = LWJGLTutorial.WIDTH;
	private int screenHeight = LWJGLTutorial.HEIGHT;
	private Texture texture;

	public PNG_Texture(float[] corners, String filePath) throws IOException {
		this.corners = corners;
		try{
			texture = TextureLoader.getTexture("PNG", new FileInputStream(new File(filePath)));
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void draw() {        
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
		GL11.glColor3f(1.f, 1.f, 1.f);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f((corners[0] * (float)(screenWidth/2)), (corners[1] * (float)(screenHeight/2)), corners[2]);
			GL11.glTexCoord2f(0.0f, 1.0f);
			GL11.glVertex3f((corners[3] * (float)(screenWidth/2)), (corners[4] * (float)(screenHeight/2)), corners[5]);
			GL11.glTexCoord2f(.0f, .0f);
			GL11.glVertex3f((corners[6] * (float)(screenWidth/2)), (corners[7] * (float)(screenHeight/2)), corners[8]);
			GL11.glTexCoord2f(1.0f,0.f);
			GL11.glVertex3f((corners[9] * (float)(screenWidth/2)), (corners[10] * (float)(screenHeight/2)), corners[11]);
		GL11.glEnd();
	}
}