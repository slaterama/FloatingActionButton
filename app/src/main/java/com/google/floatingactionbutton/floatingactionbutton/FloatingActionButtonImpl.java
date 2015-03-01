package com.google.floatingactionbutton.floatingactionbutton;

import android.content.Context;

public interface FloatingActionButtonImpl {
	void initialize(FloatingActionButtonDelegate fab, Context context, int backgroundColor, float radius,
					float elevation, float maxElevation);

	void setRadius(FloatingActionButtonDelegate fab, float radius);

	float getRadius(FloatingActionButtonDelegate fab);

	void setElevation(FloatingActionButtonDelegate fab, float elevation);

	float getElevation(FloatingActionButtonDelegate fab);

	void initStatic();

	void setMaxElevation(FloatingActionButtonDelegate fab, float maxElevation);

	float getMaxElevation(FloatingActionButtonDelegate fab);

	float getMinWidth(FloatingActionButtonDelegate fab);

	float getMinHeight(FloatingActionButtonDelegate fab);

	void updatePadding(FloatingActionButtonDelegate fab);

	void onCompatPaddingChanged(FloatingActionButtonDelegate fab);

	void onPreventCornerOverlapChanged(FloatingActionButtonDelegate fab);
}
