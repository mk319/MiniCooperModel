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
		
		myRenderer = new glRenderer(context);
		setRenderer(myRenderer);
		
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
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
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	        mScaleFactor *= detector.getScaleFactor();
	        
	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 2.5f));

	        invalidate();
	        myRenderer.scale = mScaleFactor;
	        return true;
	    }
	}
	
}

