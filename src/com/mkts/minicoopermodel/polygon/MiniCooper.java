package com.mkts.minicoopermodel.polygon;


import java.nio.FloatBuffer;

import com.mkts.minicoopermodel.GLApplication;
import com.mkts.minicoopermodel.shader.Shader;
import com.mkts.minicoopermodel.shader.ShaderManager;
import com.mkts.minicoopermodel.texture.Texture;
import com.mkts.minicoopermodel.texture.TextureManager;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class MiniCooper {
	private Context context;

	// OpenGL ES program for this polygon
	private int glProgram;
	
	// global managers
	private ShaderManager sm;
	private TextureManager tm;

	// shaders
	private Shader fragShader;
	private Shader vertexShader;
	
	// texture
	protected Texture texture;
	protected float[] texturePosition = {};
	
	// set of vertices
	private VertexList vertexList;
	private FloatBuffer vertexBuffer;
	
	// color
	private Color color;
	
	// light position
	private float[] lightPosition = {0, 1, -1};
	
	// model matrix
	private float mMatrix[] = new float[16];	
	
	public MiniCooper(Context context){
		this.context = context;
		vertexList = new VertexList();
		
		sm = ((GLApplication)context.getApplicationContext()).getShaderManager();
		tm = ((GLApplication)context.getApplicationContext()).getTextureManager(); 
		
		// set identity on model matrix
		Matrix.setIdentityM(mMatrix, 0);
		
		// create the OpenGL program for this object
        glProgram = GLES20.glCreateProgram();
	}
	
	public MiniCooper(Context context, VertexList vList, VertexList tList, VertexList nList){
		this.context = context;
		vertexList = new VertexList();
		
		sm = ((GLApplication)context.getApplicationContext()).getShaderManager();
		tm = ((GLApplication)context.getApplicationContext()).getTextureManager(); 
		
		// set identity on model matrix
		Matrix.setIdentityM(mMatrix, 0);
		
		// create the OpenGL program for this object
        glProgram = GLES20.glCreateProgram();

        // add vertices
        for(int i=0; i<vList.size(); i++)
        	vertexList.add(vList.get(0));
        
        setTexturePosition(tList.get2DArray());
        setNormal(nList);
	}
	
	// move the polygon in space
	public void translate(float x, float y, float z){
		Matrix.translateM(mMatrix, 0, x, y, z);
	}

	// rotate the polygon in all 3 global system axis
	public void globalRotate(float x, float y, float z){
		float[] rm = new float[16];
		
		if(x != 0){
			Matrix.setRotateM(rm, 0, x, 1, 0, 0);
			Matrix.multiplyMM(mMatrix, 0, rm, 0, mMatrix, 0);
		}
		if(y != 0){
			Matrix.setRotateM(rm, 0, y, 0, 1, 0);
			Matrix.multiplyMM(mMatrix, 0, rm, 0, mMatrix, 0);
		}
		
		if(z != 0){
			Matrix.setRotateM(rm, 0, z, 0, 0, 1);
			Matrix.multiplyMM(mMatrix, 0, rm, 0, mMatrix, 0);
		}
	}
	
	// rotate the polygon in all 3 axis of the polygon itself
	public void rotate(float x, float y, float z){
		if(x != 0)
			Matrix.rotateM(mMatrix, 0, x, 1, 0, 0);
		
		if(y != 0)
			Matrix.rotateM(mMatrix, 0, y, 0, 1, 0);
		
		if(z != 0)
			Matrix.rotateM(mMatrix, 0, z, 0, 0, 1);
	}
	
	// add a vertex
	public void add(float x, float y, float z){
		add(new Vertex(x, y, z));
	}

	// add a vertex
	public void add(Vertex v){
		vertexList.add(v);
		vertexBuffer = getFloatBuffer();
	}
	
	public void setNormal(VertexList nList){
		
	}

	// compute the final matrix(MVP) and draw the 
	// polygon using the correct method (using texture or not)
	public void draw(float[] vpMatrix){
		float[] mvpMatrix = new float[16];
		
		Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, mMatrix, 0);
		
		if(texture != null)
			drawTextured(mvpMatrix);
		else if(color != null)
			drawColored(mvpMatrix);
	}
	
	// set the polygon color
	public void setColor(Color c){
		color = c;
		texture = null;
		
		// get colored shaders from the manager
		vertexShader = sm.getColVertexShader(context);
		fragShader = sm.getColFragShader(context);
		
		// attach shaders to program
		vertexShader.attach(glProgram);
		fragShader.attach(glProgram);
		
		// link program with the shaders
		linkProgram();
	}
	
	public void setTexture(Texture t){
		texture = t;
		color = null;
		
		// get textured shaders from the manager
		vertexShader = sm.getTexVertexShader(context);
		fragShader = sm.getTexFragShader(context);
	
		// attach shaders to program
		vertexShader.attach(glProgram);
		fragShader.attach(glProgram);
		
		// link program with the shaders
		linkProgram();
	}
	
	private void linkProgram(){
		// link program with the shaders
		GLES20.glLinkProgram(glProgram);
		
		//check for errors in the linking
	    int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(glProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] != GLES20.GL_TRUE) {
			Log.v("LinkProgram", GLES20.glGetProgramInfoLog(glProgram));
		}		
	}
	
	// set the polygon texture
	public void setTexture(int resource){
		// get texture from the manager
		Texture t = tm.getTexture(context, resource);
		setTexture(t);
	}
	
	public void setTexturePosition(float[] positionArray){
		texturePosition = positionArray;
	}
	
	// set cull face
	private void setCullFace(){
		GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glFrontFace(GLES20.GL_CCW);
        GLES20.glCullFace(GLES20.GL_BACK);
	}
	
	// lighting
	private void applyLighting(){
		int uLightPosition;
		
        // get handle to fragment shader's uLightPosition member 
		// and set the light position to it
		uLightPosition = GLES20.glGetUniformLocation(glProgram, "uLightPosition");
        GLES20.glUniform3fv(uLightPosition, 1, lightPosition, 0);
	}
	
	// draw the polygon using a texture
	private void drawTextured(float[] matrix){
		int aPosition, uMatrix, aTexPosition;
		
        // Add program to OpenGL environment
        GLES20.glUseProgram(glProgram);
        
        // cull face
        setCullFace();
		
        // bind texture
        texture.setPositionArray(texturePosition);
		texture.activate();
		
        // enable a handle to the triangle vertices and
        // load the vertex data
        aPosition = GLES20.glGetAttribLocation(glProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(
	        	aPosition, Vertex.COORDS_PER_VEXTEX,
	        	GLES20.GL_FLOAT, false,
	        	Vertex.COORDS_PER_VEXTEX * Vertex.COORD_SIZE, 
	        	vertexBuffer
        	);
        
        // get handle to the transformation matrix and apply it
        uMatrix = GLES20.glGetUniformLocation(glProgram, "uMatrix");
        GLES20.glUniformMatrix4fv(uMatrix, 1, false, matrix, 0);
        
        // enable a handle to the texture positions
        // load the texture data
        aTexPosition = GLES20.glGetAttribLocation(glProgram, "aTexPosition");
        GLES20.glEnableVertexAttribArray(aTexPosition);
        GLES20.glVertexAttribPointer(
        		aTexPosition, 2,
	        	GLES20.GL_FLOAT, false,
	        	2 * Vertex.COORD_SIZE, 
	        	texture.getBuffer()
        	);
        
        // apply lighting
        applyLighting();
		
        // Draw the triangle and disable the vertex array
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexList.size());
        GLES20.glDisableVertexAttribArray(aPosition);		
	}
	
	// draw the polygon using just a color
	private void drawColored(float[] matrix){
		int aPosition, uMatrix, uColor;
		
        // Add program to OpenGL environment
        GLES20.glUseProgram(glProgram);
        
        // cull face
        setCullFace();
        
        // get handle to vertex shader's aPosition, 
        // enable a handle to the triangle vertices and
        // load the vertex data
        aPosition = GLES20.glGetAttribLocation(glProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(
	        	aPosition, Vertex.COORDS_PER_VEXTEX,
	        	GLES20.GL_FLOAT, false,
	        	Vertex.COORDS_PER_VEXTEX * Vertex.COORD_SIZE, 
	        	vertexBuffer
        	);
        
        // get handle to the transformation matrix and apply it
        uMatrix = GLES20.glGetUniformLocation(glProgram, "uMatrix");
        GLES20.glUniformMatrix4fv(uMatrix, 1, false, matrix, 0);
        
        // get handle to fragment shader's uColor member and set the color array to it
        uColor = GLES20.glGetUniformLocation(glProgram, "uColor");
        GLES20.glUniform4fv(uColor, 1, color.getArray(), 0);
        
        // apply lighting
        applyLighting();
        
        // Draw the triangle and disable the vertex array
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexList.size());
        GLES20.glDisableVertexAttribArray(aPosition);
	}

	// puts vertices into a float buffer, used by opengl 
	public FloatBuffer getFloatBuffer(){
		return vertexList.getFloatBuffer();
	}
	
	public void setLightPosition(float x, float y, float z){
		lightPosition[0] = x;
		lightPosition[1] = y;
		lightPosition[2] = z;
	}
}

