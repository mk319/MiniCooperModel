package com.mkts.minicoopermodel;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.mkts.minicoopermodel.polygon.ColoredCube;
import com.mkts.minicoopermodel.polygon.GLObject;
import com.mkts.minicoopermodel.polygon.TexturedCube;
import com.mkts.minicoopermodel.utils.OBJParser;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

public class GLMiniCooperRenderer implements Renderer {
	Context context;
	
	private float[] pMatrix = new float[16]; // projection
	private float[] vMatrix = new float[16]; // view
	private float[] vpMatrix = new float[16]; // view and projection
    
	// rotation values updated by the surface view 
    public volatile float rotateX = 0;
    public volatile float rotateY = 0;
    
    // global rotation
    private final float GLOBAL_ROTATION = 3;
    
    // objects
    private GLObject mesh;
	private GLObject texturedCube;
	private GLObject coloredCube;
	private final float CUBE_EDGE_SIZE = 1f;
	
	// frame rate control
	private long startTime;
	private final long DRAW_WAIT_TIME = 33; // 30 FPS
	
	// lighting
	private final float LIGHT_X = -0.5f;
	private final float LIGHT_Y = 0.7f;
	private final float LIGHT_Z = -1.5f;
		
	public GLMiniCooperRenderer(Context context){
		this.context = context;
	}
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// color used to clear each frame
		GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1);
		
		// view matrix
		// this represents the point of view (camera)
		Matrix.setLookAtM(vMatrix, 0, 0, 0, 6, 0, 0, 0, 0, 1.0f, 0);
		
		// depth buffer
		// without this the last object drawn will appear in front
		// of all others, even if it should be in the back (lesser Z coordinate)
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearDepthf(1.0f);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		
		// create cubes
		texturedCube = new TexturedCube(context, CUBE_EDGE_SIZE);
		coloredCube =  new ColoredCube(context, CUBE_EDGE_SIZE);
		
		// set light position
		coloredCube.setLightPosition(LIGHT_X, LIGHT_Y, LIGHT_Z);
		texturedCube.setLightPosition(LIGHT_X, LIGHT_Y, LIGHT_Z);
		
		// move cubes to their initial position
		texturedCube.translate(-1.5f, 0, 1.5f);
		coloredCube.translate(1.5f, 0, -1.5f);
		
		// create object from a OBJ file
		mesh = OBJParser.parse(context, R.raw.chest_mesh, R.raw.chest_texture);
		mesh.translate(0, -0.5f, 0);
		mesh.setLightPosition(LIGHT_X, LIGHT_Y, LIGHT_Z);
		
		// set start time
		startTime = System.currentTimeMillis();
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		// frame rate control
		long dt = System.currentTimeMillis() - startTime;
		// if less then DRAW_WAIT_TIME has passed, sleep
		if (dt < DRAW_WAIT_TIME){
			try {
				Thread.sleep(DRAW_WAIT_TIME - dt);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		startTime = System.currentTimeMillis();
		
		// clear frame both with the clear color and the depth buffer
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// global rotation
		texturedCube.globalRotate(0, GLOBAL_ROTATION, 0);
		coloredCube.globalRotate(0, GLOBAL_ROTATION, 0);
		
		// rotate cubes
		texturedCube.rotate(rotateY, rotateX, 0);
		coloredCube.rotate(rotateY, rotateX, 0);
		
		// draw cubes
	    texturedCube.draw(vpMatrix);
	    coloredCube.draw(vpMatrix);
		
//		mesh.rotate(rotateY, rotateX, 0);
		mesh.globalRotate(0, GLOBAL_ROTATION, 0);
		mesh.draw(vpMatrix);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int w, int h) {
		GLES20.glViewport(0, 0, w, h);
		
		// projection matrix
		// this puts all vertices in the correct place 
		// according to the screen size (avoid distortions)
		float ratio = (float) w / h;
	    Matrix.frustumM(pMatrix, 0, -ratio, ratio, -1, 1, 3, 15);
	    
	    // projection and view
	 	Matrix.multiplyMM(vpMatrix, 0, pMatrix, 0, vMatrix, 0);
	}
	
	public static void checkGLErrors(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.v("GLError", op + ": " + error);
		}
	}
}
