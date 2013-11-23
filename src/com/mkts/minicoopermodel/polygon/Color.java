package com.mkts.minicoopermodel.polygon;

//represents a color
public class Color {
	private float red, green, blue, alpha;
	
	public Color(float red, float green, float blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 1.0f;
	}
	
	public Color(float red, float green, float blue, float alpha){
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	// color array
	public float[] getArray(){
		float[] color = { red, green, blue, alpha };
		return color;
	}
}

