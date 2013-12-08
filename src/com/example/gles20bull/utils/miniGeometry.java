package com.example.gles20bull.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class miniGeometry {
	
	private int _numVertices;
	private int _numFaces;
	private int _idxCount = 3;
	public PartEntry entry;
	private int numParts;
	
	public ArrayList<Float> vertices;
	public ArrayList<Float> normals;
	public ArrayList<Float> texCoords;
	public ArrayList<Short> indices;	
	public ArrayList<PartEntry> _groups;
	
	public miniGeometry(Context context) throws FileNotFoundException {
		vertices = new ArrayList<Float>();
		normals = new ArrayList<Float>();
		texCoords = new ArrayList<Float>();
		indices = new ArrayList<Short>();
		_groups = new ArrayList<PartEntry>();
		
		String temp;
		String[] tempArray;
		
		try {
			int rId = context.getResources().getIdentifier("com.example.gles20bull:raw/"+"mini_geometry", null, null);
			BufferedReader in = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(rId)));
			temp = in.readLine().trim();
			_numVertices = Integer.parseInt(temp);
			
			for(int i = 0; i < _numVertices; i++) {
				temp = in.readLine().trim();
				tempArray = temp.split("[ ]+");
				vertices.add(Float.parseFloat(tempArray[0]));
				vertices.add(Float.parseFloat(tempArray[1]));
				vertices.add(Float.parseFloat(tempArray[2]));
				normals.add(Float.parseFloat(tempArray[3]));
				normals.add(Float.parseFloat(tempArray[4]));
				normals.add(Float.parseFloat(tempArray[5]));
				texCoords.add(Float.parseFloat(tempArray[6]));
				texCoords.add(Float.parseFloat(tempArray[7]));
			}
					
			temp = in.readLine().trim();
			_numFaces = Integer.parseInt(temp);
			
			for(int i = 0; i < _numFaces; i++) {
				temp = in.readLine().trim();
				tempArray = temp.split("[ ]+");
				indices.add(Short.parseShort(tempArray[0]));
				indices.add(Short.parseShort(tempArray[1]));
				indices.add(Short.parseShort(tempArray[2]));
			}
			
			temp = in.readLine().trim();
			numParts = Integer.parseInt(temp);
			
			for(int i = 0; i < numParts; i++) {
				entry = new PartEntry();
				temp = in.readLine().trim();
				tempArray = temp.split("[ ]+");
				entry.start = Integer.parseInt(tempArray[0]);
				entry.end = Integer.parseInt(tempArray[1]);
				tempArray = temp.split("[0-9]+[ ][0-9]+[ ]");
				entry.name = tempArray[1];
				_groups.add(entry);
			}
			
			in.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public int getNumVertices() {
		return _numVertices;
	}
	
	public float[] getVertexData() {
		float[] array = new float[vertices.size()];
		int i = 0;
		
		for(Float f: vertices) {
			array[i++] = (f != null ? f : Float.NaN);
		}
		return array;
	}
	
	public float[] getTexData() {
		float[] array = new float[texCoords.size()];
		int i = 0;
		
		for(Float f: texCoords) {
			array[i++] = (f != null ? f : Float.NaN);
		}
		return array;
	}
	
	public float[] getNormalData() {
		float[] array = new float[normals.size()];
		int i = 0;
		
		for(Float f: normals) {
			array[i++] = (f != null ? f : Float.NaN);
		}
		return array;
	}
	
	public int getNumFaces() {
		return _numFaces;
	}
	
	public int getNumParts() {
		return numParts;
	}
	
	public int getIndicesPerFace() {
		return _idxCount;
	}
	
	public short[] getIndices() {
		short[] array = new short[indices.size()];
		int i = 0;
		for(Short f : indices) {
			if(f != null) {
				array[i++] = f;
			}
		}
		return array;
	}
	
	public ArrayList<PartEntry> getGroups() {
		return _groups;
	}
	
	public ArrayList<String> getParts(){
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < _groups.size(); i++) {
			names.add(_groups.get(i).name);
		}
		return names;
	}
}