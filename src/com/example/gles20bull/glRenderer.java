package com.example.gles20bull;

import java.io.FileNotFoundException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

import com.example.gles20bull.shapes.Mini;

public class glRenderer implements Renderer {
	Context context;
	
	static final String TAG = "MyGLRenderer";
	
	private Mini mini;
	
	public float xAngle = 30;
	public float yAngle = 60;
	public float scale = 1;
	private float[] mModelMat = new float[16];
		
	private final float[] mMVMat = new float[16];
	private final float[] mMVPMat = new float[16];
	private final float[] mProjMat = new float[16];
	private final float[] mViewMat = new float[16];
	
	private float mAngle;
	
	public glRenderer(Context context) {
		this.context = context;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.5f);
		
		Matrix.setLookAtM(mViewMat, 0, 0, 0, 500, 0f, 0f, 0f, 0f, 1.0f, 0f);
		
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearDepthf(1.0f);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		
		try {
			mini = new Mini(context);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDrawFrame(GL10 unused) {		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);		
		
		Matrix.setIdentityM(mModelMat, 0);			
		Matrix.rotateM(mModelMat, 0, -xAngle, 0.0f, 1.0f, 0.0f);
		Matrix.rotateM(mModelMat, 0, -yAngle, 1.0f, 0.0f, 0.0f);
		Matrix.multiplyMM(mMVMat, 0, mViewMat, 0, mModelMat, 0);
		Matrix.multiplyMM(mMVPMat, 0, mProjMat, 0, mMVMat, 0);
		Matrix.scaleM(mMVPMat, 0, scale, scale, scale);
		
		mini.draw(mMVMat, mMVPMat);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		float ratio = (float) width / height;
		Matrix.frustumM(mProjMat, 0, -ratio, ratio, -1, 1, 1, 1000);
	}
	
	public static int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		return shader;
	}
	
	public static void checkGlError(String glOperation) {
		int error;
		while((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError" + error);
		}
	}
	
	public float getAngle() {
		return mAngle;
	}
	
	public void setAngle(float angle) {
		mAngle  = angle;
	}
}
