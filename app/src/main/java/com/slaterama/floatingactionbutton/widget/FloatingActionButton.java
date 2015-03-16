package com.slaterama.floatingactionbutton.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.slaterama.floatingactionbutton.R;

public class FloatingActionButton extends ImageButton
		implements RoundedButtonDelegate {

	protected static RoundedButtonBackgroundImpl newRoundedButtonImpl(
			RoundedButtonDelegate delegate, ColorStateList color, float cornerRadius,
			float elevation, float maxElevation, boolean useCompatPadding) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return new RoundedButtonBackgroundLollipop(delegate, color, cornerRadius, elevation,
					maxElevation, useCompatPadding);
		} else {
			return new RoundedButtonBackgroundEclairMr1(delegate, color, cornerRadius, elevation,
					maxElevation, useCompatPadding);
		}
	}

	protected boolean mPreventCornerOverlap;

	protected final Rect mShadowPadding = new Rect();

	protected final Rect mContentPadding = new Rect();

	protected RoundedButtonBackgroundImpl mImpl;

	public FloatingActionButton(Context context) {
		super(context, null);
	}

	public FloatingActionButton(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.floatingActionButtonStyle);
	}

	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context, attrs, defStyleAttr, 0);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr,
	                            int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initialize(context, attrs, defStyleAttr, defStyleRes);
	}

	protected void initialize(Context context, AttributeSet attrs, int defStyleAttr,
	                          int defStyleRes) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton,
				defStyleAttr, defStyleRes);

		ColorStateList color = a.getColorStateList(R.styleable.FloatingActionButton_fabColor);
		// TODO resolve color?

		float cornerRadius = a.getDimension(R.styleable.FloatingActionButton_fabCornerRadius,
				getResources().getDimension(R.dimen.fab_default_corner_radius));
		float elevation = a.getDimension(R.styleable.FloatingActionButton_fabElevation,
				getResources().getDimension(R.dimen.fab_default_elevation));
		float maxElevation = a.getDimension(R.styleable.FloatingActionButton_fabMaxElevation,
				getResources().getDimension(R.dimen.fab_default_max_elevation));
		boolean useCompatPadding = a.getBoolean(
				R.styleable.FloatingActionButton_fabUseCompatPadding, false);
		mPreventCornerOverlap = a.getBoolean(
				R.styleable.FloatingActionButton_fabPreventCornerOverlap, true);

		int defaultPadding = a.getDimensionPixelOffset(
				R.styleable.FloatingActionButton_fabContentPadding, 0);
		mContentPadding.left = a.getDimensionPixelOffset(
				R.styleable.FloatingActionButton_fabContentPaddingLeft, defaultPadding);
		mContentPadding.top = a.getDimensionPixelOffset(
				R.styleable.FloatingActionButton_fabContentPaddingTop, defaultPadding);
		mContentPadding.right = a.getDimensionPixelOffset(
				R.styleable.FloatingActionButton_fabContentPaddingRight, defaultPadding);
		mContentPadding.bottom = a.getDimensionPixelOffset(
				R.styleable.FloatingActionButton_fabContentPaddingBottom, defaultPadding);

		a.recycle();

		mImpl = newRoundedButtonImpl(this, color, cornerRadius, elevation,
				maxElevation, useCompatPadding);
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		// NO OP
	}

	@Override
	public void setPaddingRelative(int start, int top, int end, int bottom) {
		// NO OP
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public void setShadowPadding(int left, int top, int right, int bottom) {
		mShadowPadding.set(left, top, right, bottom);
		updatePadding();
	}

	public void setColor(ColorStateList color) {
		mImpl.setColor(color);
	}

	public float getCornerRadius() {
		return mImpl.getCornerRadius();
	}

	public void setCornerRadius(float cornerRadius) {
		mImpl.setCornerRadius(cornerRadius);
	}

	public float getSupportElevation() {
		return mImpl.getElevation();
	}

	public void setSupportElevation(float elevation) {
		mImpl.setElevation(elevation);
	}

	public float getMaxElevation() {
		return mImpl.getMaxElevation();
	}

	public void setMaxElevation(float maxElevation) {
		mImpl.setMaxElevation(maxElevation);
	}

	public boolean isUseCompatPadding() {
		return mImpl.isUseCompatPadding();
	}

	public void setUseCompatPadding(boolean useCompatPadding) {
		mImpl.setUseCompatPadding(useCompatPadding);
	}

	public boolean isPreventCornerOverlap() {
		return mPreventCornerOverlap;
	}

	public void setPreventCornerOverlap(boolean preventCornerOverlap) {
		if (preventCornerOverlap != mPreventCornerOverlap) {
			mPreventCornerOverlap = preventCornerOverlap;
			// TODO
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredWidthAndState = ViewCompat.getMeasuredWidthAndState(this);
		int measuredHeightAndState = ViewCompat.getMeasuredHeightAndState(this);
		// int measuredWidth = measuredWidthAndState & ViewCompat.MEASURED_SIZE_MASK;
		int measuredWidthState = measuredWidthAndState & ViewCompat.MEASURED_STATE_MASK;
		// int measuredHeight = measuredHeightAndState & ViewCompat.MEASURED_SIZE_MASK;
		int measuredHeightState = measuredHeightAndState & ViewCompat.MEASURED_STATE_MASK;

		float cornerRadius = mImpl.getCornerRadius();
		int fabWidth = (int) (2 * cornerRadius) + mShadowPadding.left + mShadowPadding.right;
		int fabHeight = (int) (2 * cornerRadius) + mShadowPadding.top + mShadowPadding.bottom;
		int resolvedWidthSizeAndState = ViewCompat.resolveSizeAndState(fabWidth, widthMeasureSpec,
				measuredWidthState);
		int resolvedHeightSizeAndState = ViewCompat.resolveSizeAndState(fabHeight,
				heightMeasureSpec, measuredHeightState);
		int resolvedWidth = resolvedWidthSizeAndState & ViewCompat.MEASURED_SIZE_MASK;
		int resolvedHeight = resolvedHeightSizeAndState & ViewCompat.MEASURED_SIZE_MASK;
		setMeasuredDimension(resolvedWidth, resolvedHeight);
	}

	public void updatePadding() {
		// TODO account for mPreventCornerOverlap
		float overlapPadding = 0.0f;

		super.setPadding((int) (mShadowPadding.left + mContentPadding.left + overlapPadding),
				(int) (mShadowPadding.top + mContentPadding.top + overlapPadding),
				(int) (mShadowPadding.right + mContentPadding.right + overlapPadding),
				(int) (mShadowPadding.bottom + mContentPadding.bottom + overlapPadding));
	}
}
