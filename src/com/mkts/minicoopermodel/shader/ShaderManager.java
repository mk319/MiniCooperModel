package com.mkts.minicoopermodel.shader;

package org.garrett.gldroid.shader;

import java.io.InputStream;

import org.garrett.gldroid.R;

import android.content.Context;
import android.opengl.GLES20;

// this shader manager is used to create shaders on demand
// if a already created shader is requested the manager returns it
// instead of creating a new one
public class ShaderManager {
	
	// shaders source code
	private String coloredFragmentShaderCode;
	private String coloredVertexShaderCode;
	private String texturedFragmentShaderCode; 
	private String texturedVertexShaderCode;
	
	// shaders
	private Shader coloredFragShader;
	private Shader coloredVertexShader;
	private Shader texturedFragShader;
	private Shader texturedVertexShader;
	
	// read a shader source code from the raw resources
    private String readCode(Context context, int resource){
        try {
            InputStream input = context.getResources().openRawResource(resource);

            byte[] b = new byte[input.available()];
            input.read(b);
            return new String(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // colored fragment shader
    public Shader getColFragShader(Context context){
    	if(coloredFragShader != null)
   			return coloredFragShader;
    	
    	if(coloredFragmentShaderCode == null)
    		coloredFragmentShaderCode = readCode(context, R.raw.colored_fragment_shader);
    	
    	coloredFragShader = new Shader(GLES20.GL_FRAGMENT_SHADER, coloredFragmentShaderCode);
    	return coloredFragShader;
    }
    
    // colored vertex shader
    public Shader getColVertexShader(Context context){
    	if(coloredVertexShader != null)
   			return coloredVertexShader;
    	
    	if(coloredVertexShaderCode == null)
    		coloredVertexShaderCode = readCode(context, R.raw.colored_vertex_shader);
    	
    	coloredVertexShader = new Shader(GLES20.GL_VERTEX_SHADER, coloredVertexShaderCode);
    	return coloredVertexShader;
    }
    
    // textured fragment shader
    public Shader getTexFragShader(Context context){
    	if(texturedFragShader != null)
    		return texturedFragShader;
    	
    	if(texturedFragmentShaderCode == null)
    		texturedFragmentShaderCode = readCode(context, R.raw.textured_fragment_shader);
    	
    	texturedFragShader = new Shader(GLES20.GL_FRAGMENT_SHADER, texturedFragmentShaderCode);
    	return texturedFragShader;
    }
    
    // tectured vertex shader
    public Shader getTexVertexShader(Context context){
    	if(texturedVertexShader != null)
   			return texturedVertexShader;
    	
    	if(texturedVertexShaderCode == null)
    		texturedVertexShaderCode = readCode(context, R.raw.textured_vertex_shader);
    	
    	texturedVertexShader = new Shader(GLES20.GL_VERTEX_SHADER, texturedVertexShaderCode);
    	return texturedVertexShader;
    }
}
