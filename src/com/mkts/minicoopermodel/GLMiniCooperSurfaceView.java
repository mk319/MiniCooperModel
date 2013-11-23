package com.mkts.minicoopermodel;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/*
public class GLMiniCooperSurfaceView extends GLSurfaceView {
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	
	private float oldX, oldY;
	GLMiniCooperRenderer renderer;
	
	public GLMiniCooperSurfaceView(Context context){
		super(context);
		
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);

		// use a config chooser in order to enable the depth buffer
		setEGLConfigChooser(true);
		
		// set renderer
		renderer = new GLMiniCooperRenderer(context);
		setRenderer(renderer);
		
		// Render mode: continuously render
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent e) {
    	float x = e.getX();
        float y = e.getY();

        // set rotation values based on how much the user 
    	// moved his finger on the screen
    	switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - oldX;
                float dy = y - oldY;

                renderer.rotateX = dx * TOUCH_SCALE_FACTOR;
                renderer.rotateY = dy * TOUCH_SCALE_FACTOR;
                break;
        }
    	
        oldX = x;
        oldY = y;
        
        return true;
    }
}
*/