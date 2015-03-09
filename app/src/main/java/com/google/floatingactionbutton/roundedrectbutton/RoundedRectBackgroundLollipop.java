package com.google.floatingactionbutton.roundedrectbutton;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RoundedRectBackgroundLollipop extends RippleDrawable
		implements RoundedRectBackgroundImpl {

	public RoundedRectBackgroundLollipop(ColorStateList color) {
		super(color, null, null);
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
