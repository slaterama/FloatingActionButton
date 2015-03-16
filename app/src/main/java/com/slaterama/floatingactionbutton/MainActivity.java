package com.slaterama.floatingactionbutton;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.slaterama.floatingactionbutton.widget.FloatingActionButton;


public class MainActivity extends Activity {

	/*
	private RoundedImageButton mImageButton1;
	private RoundedImageButton mImageButton2;
	*/
	private FloatingActionButton mFab1;

	static {
		LogEx.setTagFormat("Fab|%s", LogEx.Placeholder.SIMPLE_CLASS_NAME);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*
		mImageButton1 = (RoundedImageButton) findViewById(R.id.rounded_btn_1);
		mImageButton2 = (RoundedImageButton) findViewById(R.id.rounded_btn_2);
		*/
		mFab1 = (FloatingActionButton) findViewById(R.id.fab_1);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view) {
		switch (view.getId()) {
			/*
			case R.id.rounded_btn_1:

				break;
			case R.id.rounded_btn_2: {
				float density = getResources().getDisplayMetrics().density;
				float cornerRadius = mImageButton2.getCornerRadius() / density;
				if (cornerRadius == 6.0f) {
					mImageButton2.setCornerRadius(24.0f * density);
				} else {
					mImageButton2.setCornerRadius(6.0f * density);
				}
				break;
			}
			*/
			case R.id.fab_1: {
				float density = getResources().getDisplayMetrics().density;
				float cornerRadius = mFab1.getCornerRadius() / density;
				if (cornerRadius == 20.0f) {
					mFab1.setCornerRadius(28.0f * density);
				} else {
					mFab1.setCornerRadius(20.0f * density);
				}
				break;
			}
		}
	}
}
