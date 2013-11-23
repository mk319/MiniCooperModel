package com.mkts.minicoopermodel;


import com.mkts.minicoopermodel.shader.ShaderManager;
import com.mkts.minicoopermodel.texture.TextureManager;

import android.app.Application;

// add some calls to made possible for any part of the 
// application to get the shader and texture managers
public class GLApplication extends Application {
	
	// global managers for shaders and texture
	private ShaderManager sm;
	private TextureManager tm;
	
	// get shader manager
	public ShaderManager getShaderManager(){
		return sm;
	}
	
	// get texture manager
	public TextureManager getTextureManager(){
		return tm;
	}
	
	// re-instantiate managers
	// this is used when the process stop/start,
	// because the Garbage Collector somehow
	// messes with OpenGL shaders and textures 
	public void refreshManagers(){
		sm = new ShaderManager();
		tm = new TextureManager();
	}
}
