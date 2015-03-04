package com.google.floatingactionbutton.floatingactionbutton;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.UNSPECIFIED;

public class ViewUtils {

	public static String measureSpecModeToString(int mode) {
		switch (mode) {
			case EXACTLY:
				return "EXACTLY";
			case AT_MOST:
				return "AT_MOST";
			case UNSPECIFIED:
				return "UNSPECIFIED";
			default:
				return "";
		}
	}

	private ViewUtils() {
	}
}
