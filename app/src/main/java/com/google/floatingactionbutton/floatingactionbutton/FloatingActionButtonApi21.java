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

import com.google.floatingactionbutton.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class FloatingActionButtonApi21 implements FloatingActionButtonImpl {

	/* FAB */
	float mInsetShadow;
	/* END FAB */

	@Override
	public void initialize(FloatingActionButtonDelegate fab, Context context, int backgroundColor,
						   float radius, float elevation, float maxElevation) {
		final RoundRectDrawable backgroundDrawable = new RoundRectDrawable(backgroundColor, radius);
		fab.setBackgroundDrawable(backgroundDrawable);
		View view = (View) fab;
		view.setClipToOutline(true);
		view.setElevation(elevation);
		setMaxElevation(fab, maxElevation);

		/* FAB */
		mInsetShadow = context.getResources().getDimensionPixelSize(R.dimen.fab_compat_inset_shadow);
		/* END FAB */
	}

	@Override
	public void setRadius(FloatingActionButtonDelegate fab, float radius) {
		((RoundRectDrawable) (fab.getBackground())).setRadius(radius);
	}

	@Override
	public void initStatic() {
	}

	@Override
	public void setMaxElevation(FloatingActionButtonDelegate fab, float maxElevation) {
		((RoundRectDrawable) (fab.getBackground())).setPadding(maxElevation,
				fab.getUseCompatPadding(), fab.getPreventCornerOverlap());
		updatePadding(fab);
	}

	@Override
	public float getMaxElevation(FloatingActionButtonDelegate fab) {
		return ((RoundRectDrawable) (fab.getBackground())).getPadding();
	}

	@Override
	public float getMinWidth(FloatingActionButtonDelegate fab) {
		/* FAB */
		return (fab.getUseCompatPadding()
				? RoundRectDrawableWithShadow.getMinWidth(
					getMaxElevation(fab), getRadius(fab), mInsetShadow)
				: getRadius(fab) * 2);
		// return getRadius(fab) * 2;
		/* END FAB */
	}

	@Override
	public float getMinHeight(FloatingActionButtonDelegate fab) {
		/* FAB */
		return (fab.getUseCompatPadding()
				? RoundRectDrawableWithShadow.getMinHeight(
					getMaxElevation(fab), getRadius(fab), mInsetShadow)
				: getRadius(fab) * 2);
		// return getRadius(fab) * 2;
		/* END FAB */
	}

	@Override
	public float getRadius(FloatingActionButtonDelegate fab) {
		return ((RoundRectDrawable) (fab.getBackground())).getRadius();
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

		/* FAB */
		LogEx.d(String.format("FloatingActionButtonApi21.java:updatePadding hPadding=%d, vPadding=%d", hPadding, vPadding));
		/* END FAB */

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