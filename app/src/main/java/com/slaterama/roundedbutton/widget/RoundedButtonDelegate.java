package com.slaterama.roundedbutton.widget;

import android.view.View;

public interface RoundedButtonDelegate {
	// used to calculate content padding
	double COS_45 = Math.cos(Math.toRadians(45));

	View getView();
	void setShadowPadding(int left, int top, int right, int bottom);
}
