package com.mkts.MiniGLModel;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MyGLSurfaceView extends GLSurfaceView {

	float touchedX = 0;
	float touchedY = 0;
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;
	private final glRenderer myRenderer;
	
	public MyGLSurfaceView(Context context) {
		super(context);
		
		setEGLContextClientVersion(2);		
		setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
		
		myRenderer = new glRenderer(context);
		setRenderer(myRenderer);
		
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//ScaleDetector listens to every event
		mScaleDetector.onTouchEvent(event); 
		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			touchedX = event.getX();
			touchedY = event.getY();
			break;
		} 
		case MotionEvent.ACTION_MOVE: {
			myRenderer.xAngle += (touchedX - event.getX()) / 2f;
			myRenderer.yAngle += (touchedY - event.getY()) / 2f;
			
			touchedX = event.getX();
			touchedY = event.getY();
			break;
		}		
	}
		return true;
	}
	//Listener class for pinch to zoom
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    final float MAX_SCALE = 2.0f;
	    final float MIN_SCALE = 0.2f;
	    
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	        //gets the scale factor for pinch to zoom
	    	mScaleFactor *= detector.getScaleFactor();
	        
	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(MIN_SCALE, Math.min(mScaleFactor, MAX_SCALE));

	        invalidate();
	        myRenderer.scale = mScaleFactor;
	        return true;
	    }
	}
	
}

