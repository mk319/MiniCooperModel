package com.mkts.minicoopermodel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
import org.garrett.gldroid.polygon.Color;
import org.garrett.gldroid.polygon.Mesh;
import org.garrett.gldroid.polygon.Polygon;
import org.garrett.gldroid.polygon.PolygonFactory;
import org.garrett.gldroid.polygon.Vertex;
import org.garrett.gldroid.polygon.VertexList;
*/
import android.content.Context;

public class OBJParser {
	private static final Color DEFAULT_COLOR = new Color(0.8f, 0.8f, 0.8f);
	
    public static Mesh parse(Context context, int meshResource){
    	return parse(context, meshResource, -1);
    }
	
	public static Mesh parse(Context context, int meshResource, int textureResource){
		Mesh mesh;
		BufferedReader input;
		String line;
		VertexList vertices;
		VertexList normalVertices;
		VertexList textureVertices;
		boolean normalized = false;
		
		vertices = new VertexList();
		normalVertices = new VertexList();
		textureVertices = new VertexList();
		
		// create mesh and OBJ file reader
		mesh = new Mesh();
		input = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(meshResource)));
		
		try {
			while((line = input.readLine()) != null){
				line = line.trim();
				
				// vertex
				if(line.startsWith("v")){
					String [] tokens = line.split("[ ]+");
					
					// create vertex
					Vertex v = new Vertex();
					v.x = Float.valueOf(tokens[1]);
					v.y = Float.valueOf(tokens[2]);
					
					if(tokens.length < 4)
						v.z = 0;
					else
						v.z = Float.valueOf(tokens[3]);
					
					// check vertex type
					if(line.charAt(1) == 't')
						textureVertices.add(v);
					else if(line.charAt(1) == 'n')
						normalVertices.add(v);
					else 
						vertices.add(v);
				}
				// face
				else if(line.startsWith("f")){
					if(! normalized){
						normalizeVertices(vertices);
						normalized = true;
					}
					
					String [] tokens = line.split("[ ]+");
					int count = tokens.length - 1;
					int indices[][] = new int[count][3];
					
					for(int i=1; i<=count; i++){
						String[] values = tokens[i].split("/");
						
						if(tokens[i].matches("[0-9]+")){//f: v
							indices[i-1][0] = Integer.valueOf(values[0]) - 1;
							indices[i-1][1] = -1;
							indices[i-1][2] = -1;
						}

						if(tokens[1].matches("[0-9]+/[0-9]+")){//if: v/vt
							indices[i-1][0] = Integer.valueOf(values[0]) - 1;
							indices[i-1][1] = Integer.valueOf(values[1]) - 1;
							indices[i-1][2] = -1;							
						}
						
						if(tokens[i].matches("[0-9]+//[0-9]+")){ //f: v//vn
							indices[i-1][0] = Integer.valueOf(values[0]) - 1;
							indices[i-1][1] = -1;
							indices[i-1][2] = Integer.valueOf(values[2]) - 1;
						}
						if(tokens[1].matches("[0-9]+/[0-9]+/[0-9]+")){//f: v/vt/vn
							indices[i-1][0] = Integer.valueOf(values[0]) - 1;
							indices[i-1][1] = Integer.valueOf(values[1]) - 1;
							indices[i-1][2] = Integer.valueOf(values[2]) - 1;
						}
					}
					
					addTriangles(context, indices, mesh, vertices, textureVertices, normalVertices, textureResource);
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mesh;		
	}
	
	private static void addTriangles(Context context, int[][] indices, Mesh mesh, VertexList v, VertexList tv, VertexList nv, int resource){
		VertexList vList = new VertexList();
		VertexList tList = new VertexList();
		VertexList nList = new VertexList();
		int count = indices.length; // number of vertices
		Polygon p;

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
