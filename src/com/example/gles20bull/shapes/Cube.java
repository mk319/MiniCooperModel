package com.example.gles20bull.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;

import com.example.gles20bull.glRenderer;

public class Cube {
	Context context;
    
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private ByteBuffer  mIndexBuffer;
    private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatHandle;
    
    private final String vertexShaderCode = 
			"uniform mat4 uMVPMat;" +
			"attribute vec4 aPosition;" +
			"attribute vec4 a_color;" +
			"varying vec4 v_color;" +
			"void main() {" +
			"  v_color = a_color;" +
			"  gl_Position = uMVPMat * aPosition;" +
			"}";
	
	private final String fragmentShaderCode = 
			"precision mediump float;" +
			"varying vec4 v_color;" +
			"void main() {" +
			"  gl_FragColor = v_color;" +
			"}";
   
	private float vertices[] = {
			 -97.0f, -180.0f,   -2.0f,  // b
              97.0f, -180.0f,   -2.0f,  // b
              97.0f,  181.0f,   -2.0f,  // b
             -97.0f,  181.0f,   -2.0f,  // b
             -97.0f, -180.0f,  163.0f,  // f
              97.0f, -180.0f,  163.0f,  // f
              97.0f,  181.0f,  163.0f,  // f
             -97.0f,  181.0f,  163.0f   // f
	};
    
	private float colors[] = {
			0.0f,  1.0f,  0.0f,  1.0f,
			0.0f,  1.0f,  0.0f,  1.0f,
			1.0f,  0.5f,  0.0f,  1.0f,
			1.0f,  0.5f,  0.0f,  1.0f,
			1.0f,  0.0f,  0.0f,  1.0f,
			1.0f,  0.0f,  0.0f,  1.0f,
			0.0f,  0.0f,  1.0f,  1.0f,
			1.0f,  0.0f,  1.0f,  1.0f
	};
   
	private byte indices[] = {
			0, 4, 5, 0, 5, 1,
			1, 5, 6, 1, 6, 2,
			2, 6, 7, 2, 7, 3,
			3, 7, 4, 3, 4, 0,
			4, 7, 6, 4, 6, 5,
			3, 0, 1, 3, 1, 2
	};
                
    public Cube(Context context) {
    		this.context = context;
    		
            ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mVertexBuffer = byteBuf.asFloatBuffer();
            mVertexBuffer.put(vertices);
            mVertexBuffer.position(0);
                
            byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mColorBuffer = byteBuf.asFloatBuffer();
            mColorBuffer.put(colors);
            mColorBuffer.position(0);
                
            mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
            mIndexBuffer.put(indices);
            mIndexBuffer.position(0);
            
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
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(
				mPositionHandle, 3, GLES20.GL_FLOAT, false,
				0, mVertexBuffer);
		
		mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
		GLES20.glEnableVertexAttribArray(mColorHandle);
		GLES20.glVertexAttribPointer(
				mColorHandle, 3, GLES20.GL_FLOAT, false,
				0, mColorBuffer);
		
		mMVPMatHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMat");
		glRenderer.checkGlError("glGetUniformLocation");
		
		GLES20.glUniformMatrix4fv(mMVPMatHandle, 1, false, mvpMat, 0);
		glRenderer.checkGlError("glUniformMatrix4fv");
		
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);
		
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mColorHandle);
    }
}