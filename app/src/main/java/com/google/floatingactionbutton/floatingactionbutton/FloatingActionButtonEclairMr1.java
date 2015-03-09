/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.floatingactionbutton.floatingactionbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

class FloatingActionButtonEclairMr1 implements FloatingActionButtonImpl {

	final RectF sCornerRect = new RectF();

	@Override
	public void initStatic() {
		// Draws a round rect using 7 draw operations. This is faster than using
		// canvas.drawRoundRect before JBMR1 because API 11-16 used alpha mask textures to draw
		// shapes.
		RoundRectDrawableWithShadow.sRoundRectHelper
				= new RoundRectDrawableWithShadow.RoundRectHelper() {
			@Override
			public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
									  Paint paint) {
				final float twoRadius = cornerRadius * 2;
				final float innerWidth = bounds.width() - twoRadius;
				final float innerHeight = bounds.height() - twoRadius;
				sCornerRect.set(bounds.left, bounds.top,
						bounds.left + cornerRadius * 2, bounds.top + cornerRadius * 2);

				canvas.drawArc(sCornerRect, 180, 90, true, paint);
				sCornerRect.offset(innerWidth, 0);
				canvas.drawArc(sCornerRect, 270, 90, true, paint);
				sCornerRect.offset(0, innerHeight);
				canvas.drawArc(sCornerRect, 0, 90, true, paint);
				sCornerRect.offset(-innerWidth, 0);
				canvas.drawArc(sCornerRect, 90, 90, true, paint);

				//draw top and bottom pieces
				canvas.drawRect(bounds.left + cornerRadius, bounds.top,
						bounds.right - cornerRadius, bounds.top + cornerRadius,
						paint);
				canvas.drawRect(bounds.left + cornerRadius,
						bounds.bottom - cornerRadius, bounds.right - cornerRadius,
						bounds.bottom, paint);

				//center
				canvas.drawRect(bounds.left, bounds.top + cornerRadius,
						bounds.right, bounds.bottom - cornerRadius, paint);
			}
		};
	}

	@Override
	public void initialize(FloatingActionButtonDelegate fab, Context context,
						   int backgroundColor, int pressedColor,
						   float radius, float elevation, float maxElevation) {
		RoundRectDrawableWithShadow background = createBackground(context, backgroundColor, radius,
				elevation, maxElevation);

		RoundRectDrawableWithShadow backgroundPressed = createBackground(context, pressedColor, radius,
				elevation, maxElevation);

		background.setAddPaddingForCorners(fab.getPreventCornerOverlap());
		backgroundPressed.setAddPaddingForCorners(fab.getPreventCornerOverlap());

		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {android.R.attr.state_pressed}, backgroundPressed);
		states.addState(new int[] {}, background);

		fab.setBackgroundDrawable(states);
		updatePadding(fab);
	}

	RoundRectDrawableWithShadow createBackground(Context context, int backgroundColor,
												 float radius, float elevation, float maxElevation) {
		return new RoundRectDrawableWithShadow(context.getResources(), backgroundColor, radius,
				elevation, maxElevation);
	}

	@Override
	public void updatePadding(FloatingActionButtonDelegate fab) {
		Rect shadowPadding = new Rect();
		getShadowBackground(fab).getMaxShadowAndCornerPadding(shadowPadding);
		((View)fab).setMinimumHeight((int) Math.ceil(getMinHeight(fab)));
		((View)fab).setMinimumWidth((int) Math.ceil(getMinWidth(fab)));
		fab.setShadowPadding(shadowPadding.left, shadowPadding.top,
				shadowPadding.right, shadowPadding.bottom);
	}

	@Override
	public void onCompatPaddingChanged(FloatingActionButtonDelegate fab) {
		// NO OP
	}

	@Override
	public void onPreventCornerOverlapChanged(FloatingActionButtonDelegate fab) {
		getShadowBackground(fab).setAddPaddingForCorners(fab.getPreventCornerOverlap());
		updatePadding(fab);
	}

	@Override
	public void setRadius(FloatingActionButtonDelegate fab, float radius) {
		getShadowBackground(fab).setCornerRadius(radius);
		updatePadding(fab);
	}

	@Override
	public float getRadius(FloatingActionButtonDelegate fab) {
		return getShadowBackground(fab).getCornerRadius();
	}

	@Override
	public void setElevation(FloatingActionButtonDelegate fab, float elevation) {
		getShadowBackground(fab).setShadowSize(elevation);
	}

	@Override
	public float getElevation(FloatingActionButtonDelegate fab) {
		return getShadowBackground(fab).getShadowSize();
	}

	@Override
	public void setMaxElevation(FloatingActionButtonDelegate fab, float maxElevation) {
		getShadowBackground(fab).setMaxShadowSize(maxElevation);
		updatePadding(fab);
	}

	@Override
	public float getMaxElevation(FloatingActionButtonDelegate fab) {
		return getShadowBackground(fab).getMaxShadowSize();
	}

	@Override
	public float getMinWidth(FloatingActionButtonDelegate fab) {
		return getShadowBackground(fab).getMinWidth();
	}

	@Override
	public float getMinHeight(FloatingActionButtonDelegate fab) {
		return getShadowBackground(fab).getMinHeight();
	}

	private RoundRectDrawableWithShadow getShadowBackground(FloatingActionButtonDelegate fab) {

		// TODO Not a great solution

		StateListDrawable stateListDrawable = (StateListDrawable) fab.getBackground();
		return (RoundRectDrawableWithShadow) stateListDrawable.getCurrent();
		// return ((RoundRectDrawableWithShadow) fab.getBackground());
	}
}