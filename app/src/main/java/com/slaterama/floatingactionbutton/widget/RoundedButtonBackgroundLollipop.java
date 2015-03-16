package com.slaterama.floatingactionbutton.widget;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RoundedButtonBackgroundLollipop extends Drawable
		implements RoundedButtonBackgroundImpl {

	protected static StateListAnimator newDefaultStateListAnimator(View view,
	                                                               float elevation,
	                                                               float maxElevation) {
		Resources resources = view.getResources();
		int duration = resources.getInteger(android.R.integer.config_shortAnimTime);
		StateListAnimator stateListAnimator = new StateListAnimator();
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, PROPERTY_ELEVATION, elevation,
				maxElevation);
		animator.setDuration(duration);
		stateListAnimator.addState(SPECS_ENABLED_PRESSED, animator);
		animator = ObjectAnimator.ofFloat(view, PROPERTY_ELEVATION, maxElevation, elevation);
		animator.setDuration(duration);
		stateListAnimator.addState(SPECS_DEFAULT, animator);
		return stateListAnimator;
	}

	protected RoundedButtonDelegate mDelegate;
	protected RippleDrawable mRippleDrawable;
	protected ColorStateList mColor;
	protected float mCornerRadius;
	protected float mMaxElevation;
	protected boolean mUseCompatPadding;

	private final Paint mPaint;
	private final RectF mBoundsF;
	private final Rect mBoundsI;

	public RoundedButtonBackgroundLollipop(RoundedButtonDelegate delegate, ColorStateList color,
	                                       float cornerRadius, float elevation,
	                                       float maxElevation, boolean useCompatPadding) {
		mDelegate = delegate;
		mColor = color;
		mCornerRadius = cornerRadius;
		mMaxElevation = maxElevation;
		mUseCompatPadding = useCompatPadding;

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mPaint.setColor(color.getDefaultColor());
		mBoundsF = new RectF();
		mBoundsI = new Rect();

		mRippleDrawable = new RippleDrawable(color, this, null);

		View view = delegate.getView();
		view.setElevation(elevation);
		view.setBackground(mRippleDrawable);
		view.setClipToOutline(true);

		StateListAnimator stateListAnimator = view.getStateListAnimator();
		if (stateListAnimator == null) {
			view.setStateListAnimator(newDefaultStateListAnimator(view, elevation, maxElevation));
		}

		invalidatePadding(null);
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO ?
		// not supported because older versions do not support
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO ?
		// not supported because older versions do not support
	}

	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}

	@Override
	public void getOutline(Outline outline) {
		outline.setRoundRect(mBoundsI, mCornerRadius);
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		invalidatePadding(bounds);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRoundRect(mBoundsF, mCornerRadius, mCornerRadius, mPaint);
	}

	@Override
	public void setColor(ColorStateList color) {
		mColor = color;
		invalidateSelf();
	}

	@Override
	public float getCornerRadius() {
		return mCornerRadius;
	}

	@Override
	public void setCornerRadius(float cornerRadius) {
		if (cornerRadius != mCornerRadius) {
			mCornerRadius = cornerRadius;
			invalidateSelf();
			mDelegate.getView().requestLayout();
		}
	}

	@Override
	public float getElevation() {
		return mDelegate.getView().getElevation();
	}

	@Override
	public void setElevation(float elevation) {
		mDelegate.getView().setElevation(elevation);
	}

	@Override
	public float getMaxElevation() {
		return mMaxElevation;
	}

	@Override
	public void setMaxElevation(float maxElevation) {
		if (maxElevation != mMaxElevation) {
			mMaxElevation = maxElevation;
			invalidatePadding(null);
		}
	}

	@Override
	public boolean isUseCompatPadding() {
		return mUseCompatPadding;
	}

	@Override
	public void setUseCompatPadding(boolean useCompatPadding) {
		if (useCompatPadding != mUseCompatPadding) {
			mUseCompatPadding = useCompatPadding;
			invalidatePadding(null);
		}
	}

	protected void invalidatePadding(Rect bounds) {
		invalidateSelf();
		if (bounds == null) {
			bounds = getBounds();
		}
		int paddingHorizontal = 0;
		int paddingVertical = 0;
		mBoundsF.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
		mBoundsI.set(bounds);
		if (mUseCompatPadding) {
			paddingHorizontal = (int) Math.ceil(mMaxElevation);
			paddingVertical = (int) Math.ceil(mMaxElevation * SHADOW_MULTIPLIER);
			mBoundsI.inset(paddingHorizontal, paddingVertical);
			mBoundsF.set(mBoundsI);
		}
		mDelegate.setShadowPadding(paddingHorizontal, paddingVertical, paddingHorizontal,
				paddingVertical);
	}
}
