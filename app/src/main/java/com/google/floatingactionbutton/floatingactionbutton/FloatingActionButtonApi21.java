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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class FloatingActionButtonApi21 implements FloatingActionButtonImpl {

	@Override
	public void initialize(FloatingActionButtonDelegate fab, Context context,
						   int backgroundColor, int selectedColor,
						   float radius, float elevation, float maxElevation) {
		final RippleRoundRectDrawable backgroundDrawable =
				new RippleRoundRectDrawable(backgroundColor, selectedColor, radius);
		fab.setBackgroundDrawable(backgroundDrawable);
		View view = (View) fab;
		view.setClipToOutline(true);
		view.setElevation(elevation);
		setMaxElevation(fab, maxElevation);

		// Set the outline provider for this view. The provider is given the outline which it can
		// then modify as needed. In this case we set the outline to be an oval fitting the height
		// and width.
		/* TODO
		fab.setOutlineProvider(new ViewOutlineProvider() {
			@TargetApi(Build.VERSION_CODES.LOLLIPOP)
			@Override
			public void getOutline(View view, Outline outline) {
				outline.setOval(0, 0, getWidth(), getHeight());
			}
		});
		END TODO */

		// Finally, enable clipping to the outline, using the provider we set above
		// TODO fab.setClipToOutline(true);
	}

	@Override
	public void setRadius(FloatingActionButtonDelegate fab, float radius) {
		((RippleRoundRectDrawable) (fab.getBackground())).getContent().setRadius(radius);
	}

	@Override
	public void initStatic() {
	}

	@Override
	public void setMaxElevation(FloatingActionButtonDelegate fab, float maxElevation) {
		((RippleRoundRectDrawable) (fab.getBackground())).getContent().setPadding(maxElevation,
				fab.getUseCompatPadding(), fab.getPreventCornerOverlap());
		updatePadding(fab);
	}

	@Override
	public float getMaxElevation(FloatingActionButtonDelegate fab) {
		return ((RippleRoundRectDrawable) (fab.getBackground())).getContent().getPadding();
	}

	@Override
	public float getMinWidth(FloatingActionButtonDelegate fab) {
		return getRadius(fab) * 2;
	}

	@Override
	public float getMinHeight(FloatingActionButtonDelegate fab) {
		return getRadius(fab) * 2;
	}

	@Override
	public float getRadius(FloatingActionButtonDelegate fab) {
		return ((RippleRoundRectDrawable) (fab.getBackground())).getContent().getRadius();
	}

	@Override
	public void setElevation(FloatingActionButtonDelegate fab, float elevation) {
		((View) fab).setElevation(elevation);
	}

	@Override
	public float getElevation(FloatingActionButtonDelegate fab) {
		return ((View) fab).getElevation();
	}

	@Override
	public void updatePadding(FloatingActionButtonDelegate fab) {
		if (!fab.getUseCompatPadding()) {
			fab.setShadowPadding(0, 0, 0, 0);
			return;
		}
		float elevation = getMaxElevation(fab);
		final float radius = getRadius(fab);
		int hPadding = (int) Math.ceil(RoundRectDrawableWithShadow
				.calculateHorizontalPadding(elevation, radius, fab.getPreventCornerOverlap()));
		int vPadding = (int) Math.ceil(RoundRectDrawableWithShadow
				.calculateVerticalPadding(elevation, radius, fab.getPreventCornerOverlap()));
		fab.setShadowPadding(hPadding, vPadding, hPadding, vPadding);
	}

	@Override
	public void onCompatPaddingChanged(FloatingActionButtonDelegate fab) {
		setMaxElevation(fab, getMaxElevation(fab));
	}

	@Override
	public void onPreventCornerOverlapChanged(FloatingActionButtonDelegate fab) {
		setMaxElevation(fab, getMaxElevation(fab));
	}
}