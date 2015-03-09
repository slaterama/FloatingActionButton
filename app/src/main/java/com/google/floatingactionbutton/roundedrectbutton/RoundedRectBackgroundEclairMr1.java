package com.google.floatingactionbutton.roundedrectbutton;

import android.content.res.ColorStateList;
import android.graphics.drawable.StateListDrawable;

public class RoundedRectBackgroundEclairMr1 extends StateListDrawable
		implements RoundedRectBackgroundImpl {
	public RoundedRectBackgroundEclairMr1(ColorStateList color) {
		super();
	}

	@Override
	public float getPadding() {
		return 0;
	}

	@Override
	public void setPadding(float padding, boolean insetForPadding, boolean insetForRadius) {

	}

	@Override
	public float getRadius() {
		return 0;
	}

	@Override
	public void setRadius(float radius) {

	}
}
