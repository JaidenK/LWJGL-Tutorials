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
	
	private float glVers;
	private int screenWidth = LWJGLTutorial.WIDTH;	//for use with translation and rotation
	private int screenHeight = LWJGLTutorial.HEIGHT;
	
	private float[] vertices;
	private float[] colors;
	
	private int vaoID = LWJGLTutorial.vaoID;	//vertex array object
	private int vboID = 0;	//vertex buffer object
	private int vboiID = 0;	//vertex buffer object index
	private int vbocID = 0;	//vertex buffer object color
	private int indexCount = 0;
	private int shaderProgramID = LWJGLTutorial.shaderProgramID;
	private byte[] indices;
	
	private float adjustedWidth = (float)(screenWidth/2);
	private float adjustedHeight = (float)(screenHeight/2);
	
	FloatBuffer verticesBuffer;
	FloatBuffer colorBuffer;

	public OpenGL_Object(float[] vertices, float[] colors) {
		glVers = LWJGLTutorial.glVersionF;
		this.vertices = vertices;
		this.colors = colors;
		
		indices = new byte[(vertices.length/3)];
		for(byte i = 0; i < indices.length; i++) {
			indices[i] = i;
		}
		
		if(glVers >= 3.2) {
			
			int errorValue = GL11.glGetError();
			if(errorValue != GL11.GL_NO_ERROR) {
				System.out.println(GLU.gluErrorString(errorValue));
				System.exit(-1);
			}
			
			int projMatID = GL20.glGetUniformLocation(shaderProgramID, "LWJGLTutorial.projectionMatrix");
			
			verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
			verticesBuffer.put(vertices);
			verticesBuffer.flip();
			
			colorBuffer = BufferUtils.createFloatBuffer(colors.length);
			colorBuffer.put(colors);
			colorBuffer.flip();
			
			indexCount = (indices.length);
			ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indexCount);
			indicesBuffer.put(indices);
			indicesBuffer.flip();
		
			vaoID = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vaoID);
		
			vboID = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
			GL20.glEnableVertexAttribArray(0);
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
			
			vbocID = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbocID);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
			GL20.glEnableVertexAttribArray(1);
			GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);
			
			vboiID = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiID);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
			
			GL30.glBindVertexArray(0);
			
			GL20.glUseProgram(shaderProgramID);
			LWJGLTutorial.projectionMatrix.store(LWJGLTutorial.projMatBuffer);
			LWJGLTutorial.projMatBuffer.flip();
			GL20.glUniformMatrix4(projMatID, false, LWJGLTutorial.projMatBuffer);
			GL20.glUseProgram(0);
		}
	}
	
	public void draw() {
		if(glVers <= 3.1) {
			GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glColor3f(colors[0], colors[1], colors[2]);
				GL11.glVertex3f((vertices[0] * adjustedWidth), (vertices[1] * adjustedHeight), vertices[2]);
				
				GL11.glColor3f(colors[3], colors[4], colors[5]);
				GL11.glVertex3f((vertices[3] * adjustedWidth), (vertices[4] * adjustedHeight), vertices[5]);
				
				GL11.glColor3f(colors[6], colors[7], colors[8]);
				GL11.glVertex3f((vertices[6] * adjustedWidth), (vertices[7] * adjustedHeight), vertices[8]);
			GL11.glEnd();
		}
		else if(glVers >= 3.2) {
			
			GL20.glUseProgram(shaderProgramID);
			
			GL30.glBindVertexArray(vaoID);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiID);
			GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_BYTE, 0);
			
			GL20.glUseProgram(0);
		}
	}
	
	public void rotate(float z) {
		
	}
	
	public void translate(float x, float y) {
		//can be implemented by just adding to "vertices" and then redoing the buffer stuff
	}
	
	public void releaseMemory() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vbocID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboiID);
	}
}
