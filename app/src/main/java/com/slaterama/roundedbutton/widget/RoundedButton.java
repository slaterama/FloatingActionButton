package com.slaterama.roundedbutton.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.slaterama.roundedbutton.R;

public class RoundedButton extends Button
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

	protected static void updateOverlapPadding(float cornerRadius, boolean preventCornerOverlap,
	                                           Rect overlapPadding) {
		if (preventCornerOverlap) {
			int padding = (int) Math.ceil((1 - COS_45) * cornerRadius);
			overlapPadding.set(padding, padding, padding, padding);
		} else {
			overlapPadding.setEmpty();
		}
	}

	protected boolean mPreventCornerOverlap;

	protected final Rect mShadowPadding = new Rect();

	protected final Rect mOverlapPadding = new Rect();

	protected final Rect mContentPadding = new Rect();

	protected RoundedButtonBackgroundImpl mImpl;

	public RoundedButton(Context context) {
		super(context, null);
	}

	public RoundedButton(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.roundedButtonStyle);
	}

	public RoundedButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context, attrs, defStyleAttr, 0);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public RoundedButton(Context context, AttributeSet attrs, int defStyleAttr,
	                            int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initialize(context, attrs, defStyleAttr, defStyleRes);
	}

	protected void initialize(Context context, AttributeSet attrs, int defStyleAttr,
	                          int defStyleRes) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedButton,
				defStyleAttr, defStyleRes);

		ColorStateList color = a.getColorStateList(R.styleable.RoundedButton_roundedBtnColor);
		// TODO resolve color?

		float cornerRadius = a.getDimension(R.styleable.RoundedButton_roundedBtnCornerRadius,
				getResources().getDimension(R.dimen.fab_default_corner_radius));
		float elevation = a.getDimension(R.styleable.RoundedButton_roundedBtnElevation,
				getResources().getDimension(R.dimen.fab_default_elevation));
		float maxElevation = a.getDimension(R.styleable.RoundedButton_roundedBtnMaxElevation,
				getResources().getDimension(R.dimen.fab_default_max_elevation));
		boolean useCompatPadding = a.getBoolean(
				R.styleable.RoundedButton_roundedBtnUseCompatPadding, false);
		mPreventCornerOverlap = a.getBoolean(
				R.styleable.RoundedButton_roundedBtnPreventCornerOverlap, true);
		updateOverlapPadding(cornerRadius, mPreventCornerOverlap, mOverlapPadding);

		int defaultPadding = a.getDimensionPixelOffset(
				R.styleable.RoundedButton_roundedBtnContentPadding, 0);
		mContentPadding.left = a.getDimensionPixelOffset(
				R.styleable.RoundedButton_roundedBtnContentPaddingLeft, defaultPadding);
		mContentPadding.top = a.getDimensionPixelOffset(
				R.styleable.RoundedButton_roundedBtnContentPaddingTop, defaultPadding);
		mContentPadding.right = a.getDimensionPixelOffset(
				R.styleable.RoundedButton_roundedBtnContentPaddingRight, defaultPadding);
		mContentPadding.bottom = a.getDimensionPixelOffset(
				R.styleable.RoundedButton_roundedBtnContentPaddingBottom, defaultPadding);

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
			updateOverlapPadding(getCornerRadius(), preventCornerOverlap, mOverlapPadding);
			updatePadding();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int measuredWidthAndState = ViewCompat.getMeasuredWidthAndState(this);
		int measuredHeightAndState = ViewCompat.getMeasuredHeightAndState(this);
		int measuredWidth = measuredWidthAndState & ViewCompat.MEASURED_SIZE_MASK;
		int measuredWidthState = measuredWidthAndState & ViewCompat.MEASURED_STATE_MASK;
		int measuredHeight = measuredHeightAndState & ViewCompat.MEASURED_SIZE_MASK;
		int measuredHeightState = measuredHeightAndState & ViewCompat.MEASURED_STATE_MASK;

		float cornerRadius = mImpl.getCornerRadius();
		int minWidth = (int) (2 * cornerRadius) + mShadowPadding.left + mShadowPadding.right;
		int minHeight = (int) (2 * cornerRadius) + mShadowPadding.top + mShadowPadding.bottom;
		int resolvedWidthSizeAndState = ViewCompat.resolveSizeAndState(
				Math.max(measuredWidth, minWidth), widthMeasureSpec, measuredWidthState);
		int resolvedHeightSizeAndState = ViewCompat.resolveSizeAndState(
				Math.max(measuredHeight, minHeight), heightMeasureSpec, measuredHeightState);
		int resolvedWidth = resolvedWidthSizeAndState & ViewCompat.MEASURED_SIZE_MASK;
		int resolvedHeight = resolvedHeightSizeAndState & ViewCompat.MEASURED_SIZE_MASK;

		setMeasuredDimension(resolvedWidth, resolvedHeight);
	}

	public void updatePadding() {
		super.setPadding(mShadowPadding.left + mOverlapPadding.left + mContentPadding.left,
				mShadowPadding.top + mOverlapPadding.top + mContentPadding.top,
				mShadowPadding.right + mOverlapPadding.right + mContentPadding.right,
				mShadowPadding.bottom + mOverlapPadding.bottom + mContentPadding.bottom);
	}
}
