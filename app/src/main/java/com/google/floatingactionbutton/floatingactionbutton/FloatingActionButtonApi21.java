package com.google.floatingactionbutton.floatingactionbutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.google.floatingactionbutton.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FloatingActionButtonApi21 implements FloatingActionButtonImpl {

	protected int mInsetShadow;

	@Override
	public void initialize(FloatingActionButtonDelegate fab, Context context, int backgroundColor,
						   float radius, float elevation, float maxElevation) {
		mInsetShadow = context.getResources().getDimensionPixelSize(R.dimen.fab_compat_inset_shadow);
		final RoundRectDrawable backgroundDrawable = new RoundRectDrawable(backgroundColor, radius);
		fab.setBackgroundDrawable(backgroundDrawable);
		View view = (View) fab;
		view.setClipToOutline(true);
		view.setElevation(elevation);
		setMaxElevation(fab, maxElevation);
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
		if (fab.getUseCompatPadding()) {
			return RoundRectDrawableWithShadow.getMinWidth(
					getMaxElevation(fab), getRadius(fab), mInsetShadow);
		} else {
			return getRadius(fab) * 2;
		}
	}

	@Override
	public float getMinHeight(FloatingActionButtonDelegate fab) {
		if (fab.getUseCompatPadding()) {
			return RoundRectDrawableWithShadow.getMinHeight(
					getMaxElevation(fab), getRadius(fab), mInsetShadow);
		} else {
			return getRadius(fab) * 2;
		}
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
