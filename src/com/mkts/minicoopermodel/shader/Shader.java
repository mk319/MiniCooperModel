package com.mkts.minicoopermodel.shader;

import android.opengl.GLES20;
import android.util.Log;

public class Shader {
	// OpenGL ID for the shader
	private int glID;
	
	public Shader(int type, String code){
		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    glID = GLES20.glCreateShader(type);
	    
	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(glID, code);
	    GLES20.glCompileShader(glID);
	    
		// check for compilation error
		int[] compiled = new int[1];
		GLES20.glGetShaderiv(glID, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			Log.v("ShaderCompilation", GLES20.glGetShaderInfoLog(glID));
			GLES20.glDeleteShader(glID);
			glID = 0;
		}
	}
	
	public int getID(){
		return glID;
	}
	
	// attach the shader on a OpenGL program
	public void attach(int glProgram){
		GLES20.glAttachShader(glProgram, glID);
	}
}
