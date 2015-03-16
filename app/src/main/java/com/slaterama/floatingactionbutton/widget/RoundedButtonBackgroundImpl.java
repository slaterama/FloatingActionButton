package com.slaterama.floatingactionbutton.widget;

import android.content.res.ColorStateList;

public interface RoundedButtonBackgroundImpl {
	float SHADOW_MULTIPLIER = 1.5f;

	String PROPERTY_ELEVATION = "elevation";
	int[] SPECS_ENABLED_PRESSED = new int[]
			{android.R.attr.state_enabled, android.R.attr.state_pressed};
	int[] SPECS_DEFAULT = new int[0];

	void setColor(ColorStateList color);

	float getCornerRadius();

	void setCornerRadius(float cornerRadius);

	float getElevation();

	void setElevation(float elevation);

	float getMaxElevation();

	void setMaxElevation(float maxElevation);

	boolean isUseCompatPadding();

	void setUseCompatPadding(boolean useCompatPadding);

	/*
	boolean isPreventCornerOverlap();

	void setPreventCornerOverlap(boolean preventCornerOverlap);
	*/
}
