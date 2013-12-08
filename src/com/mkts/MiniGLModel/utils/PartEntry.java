package com.mkts.MiniGLModel.utils;

public class PartEntry {
	public String name;
	public int start;
	public int end;
	
	@Override
	public String toString() {
		return "name:[" + name +"] start:[" + 
                start + "] end:{" + end + "]";
	}
}
