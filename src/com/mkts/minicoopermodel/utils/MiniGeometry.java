package com.mkts.minicoopermodel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.garrett.gldroid.polygon.Mesh;
import org.garrett.gldroid.polygon.Polygon;
import org.garrett.gldroid.polygon.PolygonFactory;

import android.content.Context;

import com.mkts.minicoopermodel.R;
import com.mkts.minicoopermodel.polygon.VertexList;
import com.mkts.minicoopermodel.polygon.Vertex;

class PartEntry
{
	public String name;
	public int start;
	public int end;
}

public class MiniGeometry {

	private int _numVertices;
	
	private float[] data;
	
	private int _numFaces;
	private int _inxCount;
	private short _indices;
	
	
	public MiniGeometry(Context context) {
		// TODO Auto-generated constructor stub
		
		String filename;
		filename = "mini_geometry.txt";
		_inxCount = 3;
		
		BufferedReader input;
		String line;
		VertexList vertices;
		VertexList normalVertices;
		VertexList textureVertices;
		
		vertices = new VertexList();
		normalVertices = new VertexList();
		textureVertices = new VertexList();
		
		input = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.mini_geometry)));
		
		try{
			_numVertices = Integer.parseInt(input.readLine());
			
			for(int i=0;i<_numVertices;++i){			
				line = input.readLine();
				line = line.trim();
				String [] tokens = line.split("[ ]+");
				
				Vertex v = new Vertex();
				v.x = Float.valueOf(tokens[0]);
				v.y = Float.valueOf(tokens[1]);
				v.z = Float.valueOf(tokens[2]);
				
				vertices.add(v);
				
				Vertex vn = new Vertex();
				vn.x = Float.valueOf(tokens[3]);
				vn.y = Float.valueOf(tokens[4]);
				vn.z = Float.valueOf(tokens[5]);
				
				normalVertices.add(v);
				
				Vertex vt = new Vertex();
				vt.x = Float.valueOf(tokens[6]);
				vt.y = Float.valueOf(tokens[7]);
				
				vertices.add(vt);
			}
			
			_numFaces = Integer.parseInt(input.readLine());
			int indices[][] = new int [_numFaces][3];
			for(int i=0;i<_numFaces;++i){
				line = input.readLine();
				line = line.trim();
				String [] tokens = line.split("[ ]+");
				indices[i][0] = Integer.valueOf(tokens[0]);
				indices[i][1] = Integer.valueOf(tokens[1]);
				indices[i][2] = Integer.valueOf(tokens[2]);
				
			}
			addTriangles(context, indices, mesh, vertices, textureVertices, normalVertices, textureResource);
		}catch (IOException e){e.printStackTrace();}
	}
	
	private static void addTriangles(Context context, int[][] indices, Mesh mesh, VertexList v, VertexList tv, VertexList nv, int resource){
		VertexList vList = new VertexList();
		VertexList tList = new VertexList();
		VertexList nList = new VertexList();
		int count = indices.length; // number of vertices
		MiniCooper p;

		// put vertices into corresponding list for
		// further polygon creation
		for(int i=0; i<count; i++){
			int vIdx, tIdx, nIdx;
			
			vIdx = indices[i][0];
			tIdx = indices[i][1];
			nIdx = indices[i][2];
			
			if(vIdx != -1)
				vList.add(v.get(vIdx));
			
			if(tIdx != -1)
				tList.add(tv.get(tIdx));
			
			if(nIdx != -1)
				nList.add(nv.get(nIdx));	
		}

		// create polygon
		p = PolygonFactory.createPolygon(context, vList, tList, nList);
		
		if(resource != -1)
			p.setTexture(resource);
		else
			p.setColor(DEFAULT_COLOR);
		
		mesh.add(p);
	}
	
	private static void normalizeVertices(VertexList vList){
		float highest = 0;
		
		// found the highest value
		for(int i=0; i<vList.size(); i++){
			Vertex v = vList.get(i);
			
			highest = Math.max(Math.abs(v.x), highest);
			highest = Math.max(Math.abs(v.y), highest);
			highest = Math.max(Math.abs(v.z), highest);
		}
		
		if(highest == 0)
			return;
		
		// divide all coordinates by the highest value
		for(int i=0; i<vList.size(); i++){
			Vertex v = vList.get(i);
			v.x = v.x / highest;
			v.y = v.y / highest;
			v.z = v.z / highest;
		}
	}

}

