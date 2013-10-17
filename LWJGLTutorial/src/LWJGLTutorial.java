import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

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
	private List<PNG_Texture> pngs = new ArrayList<PNG_Texture>();
	public static Matrix4f projectionMatrix;
	public static FloatBuffer projMatBuffer = BufferUtils.createFloatBuffer(16);
	
	public static void main(String[] args) {
		LWJGLTutorial test = new LWJGLTutorial();
		test.start();
	}

	public void start() {
		
		try {
			Display.setDisplayMode(new DisplayMode(1,1));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		Display.destroy();
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle("LWJGL Tutorial");
			Display.create();
		} catch (LWJGLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		initGL(WIDTH, HEIGHT);
		
		float[] firstVertices = {
			0.2f, 0.4f, 0.0f,
			0.2f, 0.7f, 0.0f,
			-0.05f, 0.55f, 0.0f
		};
		
		float[] secondVertices = {
				-20.f, -.955f, -1.f,
				1.f, -1.f, 1.f,
				1.f, -.95f, -1.f
		};
		
		float[] thirdVertices = {
				-.5f, -.5f, 0.5f,
				.5f, -.5f, 0.5f,
				.5f, .5f, 0.5f,
				-.5f, .5f, 0.5f
		};
		
		float[] firstColors = {
			0.3f, 0.4f, 0.8f,	//(77, 102, 204), or 0x4D66CC
			0.0f, 0.3f, 0.5f,	//(0, 77, 128),   or 0x004D80
			0.6f, 0.2f, 0.7f	//(153, 51, 179), or 0x9933B3
		};
		
		objects.add(new OpenGL_Object(firstVertices, firstColors));
		objects.add(new OpenGL_Object(secondVertices, firstColors));
		
		try {
			pngs.add(new PNG_Texture(thirdVertices, "res/CaveDwellersPixelated.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		while(!Display.isCloseRequested()) {
			
			render(objects, pngs);
			
			Display.update();
			Display.sync(60);
		}
		Display.destroy();
	}
	
	public void initGL(int width, int height) {
		
		GL11.glClearColor(0.3f, 0.7f, 0.8f, 0.4f);
		
		if(glVersionF < 3.1) {
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(-(width/2), (width/2), -(height/2), (height/2), -1f, 1f);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}
	
	public void renderObjects(List<OpenGL_Object> objects) {
		glDisable(GL_TEXTURE_2D);
		for(int i = 0; i < objects.size(); i++) {
			objects.get(i).draw();
		}
	}
	
	public void renderPNGs(List<PNG_Texture> pngs) {
		for(int i = 0; i < pngs.size(); i++) {
			pngs.get(i).draw();
		}
	}
	
	public void render(List<OpenGL_Object> objects, List<PNG_Texture> pngs) {
		glClear(GL_COLOR_BUFFER_BIT);
		glLoadIdentity();
		if(objects.size() != 0) {
			renderObjects(objects);
		}
		if(pngs.size() != 0) {
			renderPNGs(pngs);
		}
	}
}
