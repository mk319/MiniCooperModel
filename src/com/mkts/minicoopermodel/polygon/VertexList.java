package com.mkts.minicoopermodel.polygon;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

// list of vertices
public class VertexList extends ArrayList<Vertex> {
	private static final long serialVersionUID = 1L;
	
	// get vertices array
	public float[] getArray(){
		float[] vertexArray;
		
		vertexArray = new float[size() * 3];
		for(int i=0; i<size(); i++){
			Vertex v = get(i);
			int j = i * 3;
			
			vertexArray[j] = v.x;
			vertexArray[j+1] = v.y;
			vertexArray[j+2] = v.z;
		}
		
		return vertexArray;
	}
	
	// get 2D vertices array
	public float[] get2DArray(){
		float[] vertexArray;
		
		vertexArray = new float[size() * 2];
		for(int i=0; i<size(); i++){
			Vertex v = get(i);
			int j = i * 2;
			
			vertexArray[j] = v.x;
			vertexArray[j+1] = v.y;
		}
		
		return vertexArray;
	}
	
	// get a float buffer containing all vertices
	public FloatBuffer getFloatBuffer(){
		FloatBuffer vertexBuffer;
		ByteBuffer bb;
		
		bb = ByteBuffer.allocateDirect(size() * Vertex.COORDS_PER_VEXTEX * Vertex.COORD_SIZE);
        bb.order(ByteOrder.nativeOrder());
        
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(getArray());
        vertexBuffer.position(0);
		
		return vertexBuffer;
	}
}
