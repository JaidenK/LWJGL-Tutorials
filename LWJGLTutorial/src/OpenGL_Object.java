import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
//import org.lwjgl.opengl.Util;
import org.lwjgl.util.glu.GLU;

//this can be inherited from and specified into Triangle, Rectangle, Circle, etc. so that many basic shapes can be used
//and the render method in LWJGLTutorial can use the glBegin/OpenGL 3.x render method for each shape (e.g. GL_TRIANGLES, GL_QUADS, etc.)

public class OpenGL_Object {
	
	private int screenWidth = LWJGLTutorial.WIDTH;	//for use with translation and rotation
	private int screenHeight = LWJGLTutorial.HEIGHT;
	
	private float[] vertices;
	private float[] colors;
	
	private float adjustedWidth = (float)(screenWidth/2);
	private float adjustedHeight = (float)(screenHeight/2);
	
	FloatBuffer verticesBuffer;
	FloatBuffer colorBuffer;

	public OpenGL_Object(float[] vertices, float[] colors) {
		this.vertices = vertices;
		this.colors = colors;
	}
	
	public void draw() {
		GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glColor3f(colors[0], colors[1], colors[2]);
			GL11.glVertex3f((vertices[0] * adjustedWidth), (vertices[1] * adjustedHeight), vertices[2]);
			
			GL11.glColor3f(colors[3], colors[4], colors[5]);
			GL11.glVertex3f((vertices[3] * adjustedWidth), (vertices[4] * adjustedHeight), vertices[5]);
			
			GL11.glColor3f(colors[6], colors[7], colors[8]);
			GL11.glVertex3f((vertices[6] * adjustedWidth), (vertices[7] * adjustedHeight), vertices[8]);
		GL11.glEnd();
	}
}
