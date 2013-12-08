package com.example.gles20bull.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.example.gles20bull.glRenderer;

import android.opengl.GLES20;

public class Triangle {

	private final FloatBuffer vertexBuffer;
	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatHandle;
	
	private final String vertexShaderCode = 
			"uniform mat4 MVPMat;" +
			"attribute vec4 vPosition;" +
			"void main() {" +
			"  gl_Position = MVPMat * vPosition;" +
			"}";
	
	private final String fragmentShaderCode = 
			"precision mediump float;" +
			"uniform vec4 vColor;" +
			"void main() {" +
			"  gl_FragColor = vColor;" +
			"}";
	
	static final int COORDS_PER_VERTEX = 3;
	static float triangleCoords[] = {
		 0.0f,  0.5f, 0.0f,
		-0.5f, -0.5f, 0.0f,
		 0.5f, -0.5f, 0.0f
	};
	
	private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
	private final int vertexStride = COORDS_PER_VERTEX * 4;
	
	float color[] = { 1.0f, 0.0f, 0.0f, 1.0f };
	
	public Triangle() {
		ByteBuffer bb = 
				ByteBuffer.allocateDirect(triangleCoords.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(triangleCoords);
		vertexBuffer.position(0);
		
		int vertexShader = glRenderer.loadShader(
				GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = glRenderer.loadShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		
		mProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(mProgram, vertexShader);
		GLES20.glAttachShader(mProgram, fragmentShader);
		GLES20.glLinkProgram(mProgram);
	}
	
	public void draw(float[] mvpMat) {
		GLES20.glUseProgram(mProgram);
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(
				mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
				vertexStride, vertexBuffer);
		
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		
		mMVPMatHandle = GLES20.glGetUniformLocation(mProgram, "MVPMat");
		glRenderer.checkGlError("glGetUniformLocation");
		
		GLES20.glUniformMatrix4fv(mMVPMatHandle, 1, false, mvpMat, 0);
		glRenderer.checkGlError("glUniformMatrix4fv");
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
		
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
