package com.mkts.MiniGLModel;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import com.mkts.MiniGLModel.R;

public class MainActivity extends Activity {
	
	private  GLSurfaceView glView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		glView = new MyGLSurfaceView(this);
		setContentView(glView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		glView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		glView.onResume();
	}

	
}
