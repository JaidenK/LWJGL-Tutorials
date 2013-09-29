import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;

public class LWJGLTutorial {
	
	public static final int HEIGHT = 640;
	public static final int WIDTH = 800;
	
	public static int vaoID = 0;	//vertex array object
	public static int vertShaderID;
	public static int fragShaderID;
	public static int shaderProgramID;
	
	public static float glVersionF;
	
	private List<OpenGL_Object> objects = new ArrayList<OpenGL_Object>();
	private List<PNG_Image> pngs = new ArrayList<PNG_Image>();
	public static Matrix4f projectionMatrix;
	public static FloatBuffer projMatBuffer = BufferUtils.createFloatBuffer(16);
	
	public static void main(String[] args) {
		LWJGLTutorial test = new LWJGLTutorial();
		test.start();
	}
	
	public float degToRad(float degrees) {			//utility function, in case thinking in degrees is a bit easier
		return (float) ((degrees / 180) * Math.PI);
	}

	public void start() {
		
		try {
			Display.setDisplayMode(new DisplayMode(1,1));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		String glVersion = "1.1f";
		float glVersionf = 1.1f;
		
		try {
			glVersion = GL11.glGetString(GL11.GL_VERSION);
			String[] glVersionSplit = glVersion.split("\\.");
			glVersion = glVersionSplit[0] + "." + glVersionSplit[1];
			glVersionf = Float.parseFloat(glVersion);
			LWJGLTutorial.glVersionF = glVersionf;
			System.out.println("The GL version for this computer is " + glVersion);
		}
		catch(NumberFormatException e) {
			glVersion = "1.1f";
			glVersionf = 1.1f;
			e.printStackTrace();
		}
		
		Display.destroy();
		
		if(glVersionf <= 3.1) {
			try {
				Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
				Display.setTitle("LWJGL Tutorial");
				Display.create();
			} catch(LWJGLException ex) {
				ex.printStackTrace();
				System.exit(-1);
			}
		}
		else if(glVersionf >= 3.2){
			PixelFormat pixelFormat = new PixelFormat(8, 8, 0).withBitsPerPixel(32);
			ContextAttribs attributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true).withDebug(true);
			try {
				Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
				Display.setTitle("LWJGL Tutorial");
				Display.create(pixelFormat, attributes);
			} catch(LWJGLException ex) {
				ex.printStackTrace();
				System.exit(-1);
			}
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		
		initGL(glVersionf, WIDTH, HEIGHT);
		
		float[] v0 = { 			//first vertices
			0.2f, 0.4f, 0.0f,
			0.2f, 0.7f, 0.0f,
			-0.05f, 0.55f, 0.0f
		};
		
		float[] v1 = {
				-20.f, -.955f, -1.f,
				1.f, -1.f, 1.f,
				1.f, -.95f, -1.f
		};
		
		float[] v2 = {
				-.5f, -.5f, 0.f,
				.5f, -.5f, 0.f,
				.5f, .5f, 0.f,
				-.5f, .5f, 0.f
		};
		
		float[] c0 = {			//first colors
			0.3f, 0.4f, 0.8f,	//(77, 102, 204), or 0x4D66CC
			0.0f, 0.3f, 0.5f,	//(0, 77, 128),   or 0x004D80
			0.6f, 0.2f, 0.7f	//(153, 51, 179), or 0x9933B3
		};
		
		objects.add(new OpenGL_Object(v0, c0));
		objects.add(new OpenGL_Object(v1, c0));
		
		try {
			pngs.add(new PNG_Image(v2, "res/CaveDwellersPixelated.png", 1.f));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		while(!Display.isCloseRequested()) {
			
			GL11.glClearColor(0.3f, 0.7f, 0.8f, 0.4f);
			
			render(objects, pngs);
			
			Display.update();
			Display.sync(60);
		}
		
		if(LWJGLTutorial.glVersionF >= 3.2) {
			for(int i = 0; i < objects.size(); i++) {
				objects.get(i).releaseMemory();
			}
			releaseGLMemory();
		}
		
		Display.destroy();
	}
	
	public void initGL(float glVersionF, int width, int height) {
		
		if(glVersionF < 3.1) {
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(-(width/2), (width/2), -(height/2), (height/2), -1f, 1f);
			//GLU.gluPerspective(45.f, (float)(LWJGLTutorial.WIDTH / LWJGLTutorial.HEIGHT), 1.f, -1.f);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}
		else {
			projectionMatrix = new Matrix4f();
			float aspectRatio = (float) (width/height);
			float FOV = 80f;		//our field of view is the scale of the "back"
			float near = 1f;
			float far = -1f;
			
			float yScaled = (float) (1/(Math.tan((FOV / 2) * (Math.PI / 180)))); 	//sets up the scale of y at the "back"
			float xScaled = yScaled / aspectRatio; 					//take the back height and divides it by the ratio which is still used, giving the width
			float depth = far - near; 				//finds the depth, in this case 0.5f
			
			projectionMatrix.m00 = xScaled;						/* These variables are internally used by OpenGL and represent 		*/
			projectionMatrix.m11 = yScaled;						/* different "positions" used by the projection matrix, or the 		*/
			projectionMatrix.m22 = -((far + near) / depth);		/* sort of 3D trapezoid that represents what can be seen past  		*/
			projectionMatrix.m23 = -1;							/* the "camera". Legacy OpenGL takes care of this automatic-   		*/
			projectionMatrix.m32 = -((2 * near * far) / depth); /* ally using the functions seen above (glOrtho being the big one). */
			projectionMatrix.m33 = 0;
			
			/* ((1/Math.tan((FOV/2) * (Math.PI/180)))/aspectRatio)	 	   0			0			0
			 * 
			 * 
			 * 
			 * 0			(1/(Math.tan((FOV/2) * (Math.PI/180))))						0			0
			 * 
			 * 
			 * 
			 * 0							0						-((far+near)/depth)				-1
			 * 
			 * 
			 * 
			 * 0							0						-2((2*near*far)/depth)			 0 
			 */
			
			vertShaderID = getShader("shaders/vertShader.glsl", GL20.GL_VERTEX_SHADER);
			fragShaderID = getShader("shaders/colorShader.glsl", GL20.GL_FRAGMENT_SHADER);
			shaderProgramID = GL20.glCreateProgram();
			GL20.glAttachShader(shaderProgramID, vertShaderID);
			GL20.glAttachShader(shaderProgramID, fragShaderID);
			GL20.glBindAttribLocation(shaderProgramID, 0, "in_Position");
            GL20.glBindAttribLocation(shaderProgramID, 1, "in_Color");
			GL20.glLinkProgram(shaderProgramID);
			GL20.glValidateProgram(shaderProgramID);
		}
	}
	
	public void renderObjects(List<OpenGL_Object> objects) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		for(int i = 0; i < objects.size(); i++) {
			objects.get(i).draw();
		}
	}
	
	public void renderPNGs(List<PNG_Image> pngs) {
		for(int i = 0; i < pngs.size(); i++) {
			pngs.get(i).draw();
		}
	}
	
	public void render(List<OpenGL_Object> objects, List<PNG_Image> pngs) {
		if(objects.size() != 0) {
			renderObjects(objects);
		}
		if(pngs.size() != 0) {
			renderPNGs(pngs);
		}
	}
	
	public int getShader(String path, int shaderType) {
		StringBuilder source = new StringBuilder();
		int shadersID = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line;
			while ((line = reader.readLine()) != null) {
				source.append(line).append("\n");
			}
			reader.close();
		}
		catch(IOException e) {
			System.err.println("Couldn't read the file");
			e.printStackTrace();
			System.exit(-1);
		}
		
		shadersID = GL20.glCreateShader(shaderType);
		GL20.glShaderSource(shadersID, source);
		GL20.glCompileShader(shadersID);
		System.out.println(GL20.glGetShaderInfoLog(shadersID, 245));
		
		return shadersID;
	}
	
	public void releaseGLMemory() {
		GL20.glUseProgram(0);
		GL20.glDetachShader(shaderProgramID, vertShaderID);
		GL20.glDetachShader(shaderProgramID, fragShaderID);
		GL20.glDeleteShader(vertShaderID);
		GL20.glDeleteShader(fragShaderID);
		GL20.glDeleteProgram(shaderProgramID);
		GL30.glBindVertexArray(vaoID);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoID);
	}
}
