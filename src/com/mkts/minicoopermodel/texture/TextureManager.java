package com.mkts.minicoopermodel.texture;

import android.content.Context;
import android.util.SparseArray;

//this texture manager is used to create textures on demand
//if a already created texture is requested the manager returns it
//instead of creating a new one

public class TextureManager {
	
	// textures are indexed by their resource value
	private SparseArray<Texture> textures = new SparseArray<Texture>();
	
    public Texture getTexture(Context context, int resource){
    	Texture newTexture;
    	
    	if(textures.indexOfKey(resource) >= 0)
    		return textures.get(resource);
    	
    	newTexture = new Texture(context, resource);
    	textures.put(resource, newTexture);
    	return newTexture;
    }
}
