package com.google.floatingactionbutton.floatingactionbutton;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class FloatingActionButtonJellybeanMr1 extends FloatingActionButtonEclairMr1 {

	@Override
	public void initStatic() {
		RoundRectDrawableWithShadow.sRoundRectHelper
				= new RoundRectDrawableWithShadow.RoundRectHelper() {
			@Override
			public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
									  Paint paint) {
				canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
			}
		};
	}
}

