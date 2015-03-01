package com.google.floatingactionbutton.floatingactionbutton;

import android.graphics.drawable.Drawable;

public interface FloatingActionButtonDelegate {
	void setBackgroundDrawable(Drawable paramDrawable);

	Drawable getBackground();

	boolean getUseCompatPadding();

	boolean getPreventCornerOverlap();

	float getRadius();

	void setShadowPadding(int left, int top, int right, int bottom);
}
