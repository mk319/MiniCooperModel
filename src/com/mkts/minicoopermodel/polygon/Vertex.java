package com.mkts.minicoopermodel.polygon;

public class Vertex {
	public static final int COORDS_PER_VEXTEX = 3;
	public static final int COORD_SIZE = 4; // float
	
	public float x, y, z;
	
	public Vertex(){
		x = y = z = 0;
	}
	
	public Vertex(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString(){
		return "(" + x + ", " + y + ", " + z + ")";
	}
}