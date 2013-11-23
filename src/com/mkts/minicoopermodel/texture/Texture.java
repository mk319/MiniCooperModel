package com.mkts.minicoopermodel.texture;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//import com.mkts.minicoopermodel.polygon.Vertex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture {
	// OpenGL ID for the texture
	private int glID;
	
	// bitmap of the texture
	private Bitmap bitmap;
	
	// UV mapping of the texture
	private float[] positionArray = {};
	
	public Texture(Context context, int resource){
		// get new texture ID
		int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        glID = textures[0]; 
        
        // get bitmap
		InputStream input = context.getResources().openRawResource(resource);
		bitmap = BitmapFactory.decodeStream(input);
		
		// bind texture to OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glID);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        
        bitmap.recycle();
	}
	
	public int getID(){
		return glID;
	}
	
	// activate and bind the texture to the OpenGL context
	public void activate(){
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glID);
	}
	
	public void setPositionArray(float[] array){
		positionArray = array;
	}
	
	public void add(Vertex v){
		
	}
	
	// get a float buffer of the UV mapping, used by OpenGL
	public FloatBuffer getBuffer(){
		FloatBuffer buffer;
		ByteBuffer bb; 
		
		bb = ByteBuffer.allocateDirect(positionArray.length * Vertex.COORD_SIZE);
        bb.order(ByteOrder.nativeOrder());
        
        buffer = bb.asFloatBuffer();
        buffer.put(positionArray);
        buffer.position(0);
		
		return buffer;
	}
}
