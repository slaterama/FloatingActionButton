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
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.google.floatingactionbutton.R;

/**
 * A FrameLayout with a rounded corner background and shadow.
 * <p>
 * CardView uses <code>elevation</code> property on L for shadows and falls back to a custom shadow
 * implementation on older platforms.
 * <p>
 * Due to expensive nature of rounded corner clipping, on platforms before L, CardView does not
 * clip its children that intersect with rounded corners. Instead, it adds padding to avoid such
 * intersection (See {@link #setPreventCornerOverlap(boolean)} to change this behavior).
 * <p>
 * Before L, CardView adds padding to its content and draws shadows to that area. This padding
 * amount is equal to <code>maxCardElevation + (1 - cos45) * cornerRadius</code> on the sides and
 * <code>maxCardElevation * 1.5 + (1 - cos45) * cornerRadius</code> on top and bottom.
 * <p>
 * Since padding is used to offset content for shadows, you cannot set padding on CardView.
 * Instead,
 * you can use content padding attributes in XML or {@link #setContentPadding(int, int, int, int)}
 * in code to set the padding between the edges of the Card and children of CardView.
 * <p>
 * Note that, if you specify exact dimensions for the CardView, because of the shadows, its content
 * area will be different between platforms before L and after L. By using api version specific
 * resource values, you can avoid these changes. Alternatively, If you want CardView to add inner
 * padding on platforms L and after as well, you can set {@link #setUseCompatPadding(boolean)} to
 * <code>true</code>.
 * <p>
 * To change CardView's elevation in a backward compatible way, use
 * {@link #setSupportElevation(float)}. CardView will use elevation API on L and before L, it will
 * change the shadow size. To avoid moving the View while shadow size is changing, shadow size is
 * clamped by {@link #getSupportMaxElevation()}. If you want to change elevation dynamically, you
 * should call {@link #setSupportMaxElevation(float)} when CardView is initialized.
 *
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabBackgroundColor
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabCornerRadius
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabElevation
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabMaxElevation
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabUseCompatPadding
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabPreventCornerOverlap
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabContentPadding
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabContentPaddingLeft
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabContentPaddingTop
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabContentPaddingRight
 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabContentPaddingBottom
 */
public class FloatingActionButton extends ImageButton implements FloatingActionButtonDelegate {

	private static final FloatingActionButtonImpl IMPL;

	static {
		if (Build.VERSION.SDK_INT >= 21) {
			IMPL = new FloatingActionButtonApi21();
		} else if (Build.VERSION.SDK_INT >= 17) {
			IMPL = new FloatingActionButtonJellybeanMr1();
		} else {
			IMPL = new FloatingActionButtonEclairMr1();
		}
		IMPL.initStatic();
	}

	private boolean mCompatPadding;

	private boolean mPreventCornerOverlap;

	private final Rect mContentPadding = new Rect();

	private final Rect mShadowBounds = new Rect();


	public FloatingActionButton(Context context) {
		super(context);
		initialize(context, null, 0);
	}

	public FloatingActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs, 0);
	}

	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context, attrs, defStyleAttr);
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		// NO OP
	}

	public void setPaddingRelative(int start, int top, int end, int bottom) {
		// NO OP
	}

	/**
	 * Returns whether CardView will add inner padding on platforms L and after.
	 *
	 * @return True CardView adds inner padding on platforms L and after to have same dimensions
	 * with platforms before L.
	 */
	@Override
	public boolean getUseCompatPadding() {
		return mCompatPadding;
	}

	/**
	 * CardView adds additional padding to draw shadows on platforms before L.
	 * <p>
	 * This may cause Cards to have different sizes between L and before L. If you need to align
	 * CardView with other Views, you may need api version specific dimension resources to account
	 * for the changes.
	 * As an alternative, you can set this flag to <code>true</code> and CardView will add the same
	 * padding values on platforms L and after.
	 * <p>
	 * Since setting this flag to true adds unnecessary gaps in the UI, default value is
	 * <code>false</code>.
	 *
	 * @param useCompatPadding True if CardView should add padding for the shadows on platforms L
	 *                         and above.
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabUseCompatPadding
	 */
	public void setUseCompatPadding(boolean useCompatPadding) {
		if (mCompatPadding == useCompatPadding) {
			return;
		}
		mCompatPadding = useCompatPadding;
		IMPL.onCompatPaddingChanged(this);
	}

	/**
	 * Sets the padding between the Card's edges and the children of CardView.
	 * <p>
	 * Depending on platform version or {@link #getUseCompatPadding()} settings, CardView may
	 * update these values before calling {@link android.view.View#setPadding(int, int, int, int)}.
	 *
	 * @param left   The left padding in pixels
	 * @param top    The top padding in pixels
	 * @param right  The right padding in pixels
	 * @param bottom The bottom padding in pixels
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabContentPadding
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabContentPaddingLeft
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabContentPaddingTop
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabContentPaddingRight
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabContentPaddingBottom
	 */
	public void setContentPadding(int left, int top, int right, int bottom) {
		mContentPadding.set(left, top, right, bottom);
		IMPL.updatePadding(this);
	}

	@SuppressWarnings("all")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		String resourceEntryName = "??";
		if (!isInEditMode()) {
			resourceEntryName = getContext().getResources().getResourceEntryName(getId());
		}

		boolean includePadding = (getUseCompatPadding() || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int superMeasuredWidthAndState = ViewCompat.getMeasuredWidthAndState(this);
		int superMeasuredHeightAndState = ViewCompat.getMeasuredHeightAndState(this);

		int superMeasuredWidth = superMeasuredWidthAndState & View.MEASURED_SIZE_MASK;
		int superMeasuredWidthState = superMeasuredWidthAndState & View.MEASURED_STATE_MASK;

		int superMeasuredHeight = superMeasuredHeightAndState & View.MEASURED_SIZE_MASK;
		int superMeasuredHeightState = superMeasuredHeightAndState & View.MEASURED_STATE_MASK;

		/*
		int w = 0;
		int h = 0;

		int pleft = getPaddingLeft();
		int pright = getPaddingRight();
		int ptop = getPaddingTop();
		int pbottom = getPaddingBottom();
		*/

		// Get size requested and size mode
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		/*
		boolean resizeWidth = widthMode != MeasureSpec.EXACTLY;
		boolean resizeHeight = heightMode != MeasureSpec.EXACTLY;
		*/

		final String widthModeString = ViewUtils.measureSpecModeToString(widthMode);
		final String heightModeString = ViewUtils.measureSpecModeToString(heightMode);

		float elevationFactor = (includePadding ? getSupportMaxElevation() : 0);
		int fabWidth = (int) ((getRadius() + elevationFactor) * 2);
		int resolvedWidthSizeAndState = resolveSizeAndState(
				Math.max(fabWidth, superMeasuredWidth), widthMeasureSpec, superMeasuredWidthState);
		int resolvedWidth = resolvedWidthSizeAndState & View.MEASURED_SIZE_MASK;
		int resolvedWidthState = resolvedWidthSizeAndState & View.MEASURED_STATE_MASK;

		elevationFactor *= RoundRectDrawableWithShadow.SHADOW_MULTIPLIER;
		int fabHeight = (int) ((getRadius() + elevationFactor) * 2);
		int resolvedHeightSizeAndState = resolveSizeAndState(
				Math.max(fabHeight, superMeasuredHeight), heightMeasureSpec, superMeasuredHeightState);
		int resolvedHeight = resolvedHeightSizeAndState & View.MEASURED_SIZE_MASK;
		int resolvedHeightState = resolvedHeightSizeAndState & View.MEASURED_STATE_MASK;

		float density = getContext().getResources().getDisplayMetrics().density;
		int resolvedWidthDp = (int) (resolvedWidth / density);
		int resolvedHeightDp = (int) (resolvedHeight / density);
		LogEx.d(String.format("fab=%s, resolvedWidthDp=%d, resolvedHeightDp=%d", resourceEntryName, resolvedWidthDp, resolvedHeightDp));

		//if (resizeWidth || resizeHeight) {

		//} else {
			/* We are either don't want to preserve the drawables aspect ratio,
               or we are not allowed to change view dimensions. Just measure in
               the normal way.
            */
		/*
			w += pleft + pright;
			h += ptop + pbottom;

			w = Math.max(w, getSuggestedMinimumWidth());
			h = Math.max(h, getSuggestedMinimumHeight());
			widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
			heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
		}
		*/

		/*
		if (IMPL instanceof FloatingActionButtonApi21 == false // FAB  || mCompatPadding  END FAB //) {
			final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
			switch (widthMode) {
				case MeasureSpec.EXACTLY:
				case MeasureSpec.AT_MOST:
					final int minWidth = (int) Math.ceil(IMPL.getMinWidth(this));
					widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minWidth,
							MeasureSpec.getSize(widthMeasureSpec)), widthMode);
					break;
			}

			final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
			switch (heightMode) {
				case MeasureSpec.EXACTLY:
				case MeasureSpec.AT_MOST:
					final int minHeight = (int) Math.ceil(IMPL.getMinHeight(this));
					heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minHeight,
							MeasureSpec.getSize(heightMeasureSpec)), heightMode);
					break;
			}
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
		*/

		setMeasuredDimension(resolvedWidth, resolvedHeight);
	}

	private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, defStyleAttr,
				R.style.FloatingActionButton_Light);
		int backgroundColor = a.getColor(R.styleable.FloatingActionButton_fabBackgroundColor, 0);
		float radius = a.getDimension(R.styleable.FloatingActionButton_fabCornerRadius, 0);
		float elevation = a.getDimension(R.styleable.FloatingActionButton_fabElevation, 0);
		float maxElevation = a.getDimension(R.styleable.FloatingActionButton_fabMaxElevation, 0);
		mCompatPadding = a.getBoolean(R.styleable.FloatingActionButton_fabUseCompatPadding, false);
		mPreventCornerOverlap = a.getBoolean(R.styleable.FloatingActionButton_fabPreventCornerOverlap, true);
		int defaultPadding = a.getDimensionPixelSize(R.styleable.FloatingActionButton_fabContentPadding, 0);
		mContentPadding.left = a.getDimensionPixelSize(R.styleable.FloatingActionButton_fabContentPaddingLeft,
				defaultPadding);
		mContentPadding.top = a.getDimensionPixelSize(R.styleable.FloatingActionButton_fabContentPaddingTop,
				defaultPadding);
		mContentPadding.right = a.getDimensionPixelSize(R.styleable.FloatingActionButton_fabContentPaddingRight,
				defaultPadding);
		mContentPadding.bottom = a.getDimensionPixelSize(R.styleable.FloatingActionButton_fabContentPaddingBottom,
				defaultPadding);
		if (elevation > maxElevation) {
			maxElevation = elevation;
		}
		a.recycle();
		IMPL.initialize(this, context, backgroundColor, radius, elevation, maxElevation);
	}

	/**
	 * Returns the inner padding after the Card's left edge
	 *
	 * @return the inner padding after the Card's left edge
	 */
	public int getContentPaddingLeft() {
		return mContentPadding.left;
	}

	/**
	 * Returns the inner padding before the Card's right edge
	 *
	 * @return the inner padding before the Card's right edge
	 */
	public int getContentPaddingRight() {
		return mContentPadding.right;
	}

	/**
	 * Returns the inner padding after the Card's top edge
	 *
	 * @return the inner padding after the Card's top edge
	 */
	public int getContentPaddingTop() {
		return mContentPadding.top;
	}

	/**
	 * Returns the inner padding before the Card's bottom edge
	 *
	 * @return the inner padding before the Card's bottom edge
	 */
	public int getContentPaddingBottom() {
		return mContentPadding.bottom;
	}

	/**
	 * Updates the corner radius of the CardView.
	 *
	 * @param radius The radius in pixels of the corners of the rectangle shape
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabCornerRadius
	 * @see #setRadius(float)
	 */
	public void setRadius(float radius) {
		IMPL.setRadius(this, radius);
	}

	/**
	 * Returns the corner radius of the CardView.
	 *
	 * @return Corner radius of the CardView
	 * @see #getRadius()
	 */
	public float getRadius() {
		return IMPL.getRadius(this);
	}

	/**
	 * Internal method used by CardView implementations to update the padding.
	 *
	 * @hide
	 */
	@Override
	public void setShadowPadding(int left, int top, int right, int bottom) {
		mShadowBounds.set(left, top, right, bottom);

		/* FAB */
		//LogEx.d(String.format("left=%d, top=%d, right=%d, bottom=%d", left, top, right, bottom));
		/* END FAB */

		super.setPadding(left + mContentPadding.left, top + mContentPadding.top,
				right + mContentPadding.right, bottom + mContentPadding.bottom);
	}

	/* FAB */
	/*
	 * Updates the backward compatible elevation of the CardView.
	 *
	 * @param radius The backward compatible elevation in pixels.
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabElevation
	 * @see #getCardElevation()
	 * @see #setMaxCardElevation(float)
	public void setCardElevation(float radius) {
		IMPL.setElevation(this, radius);
	}
	*/

	/*
	 * Returns the backward compatible elevation of the CardView.
	 *
	 * @return Elevation of the CardView
	 * @see #setCardElevation(float)
	 * @see #getMaxCardElevation()
	public float getCardElevation() {
		return IMPL.getElevation(this);
	}
	*/

	/*
	 * Updates the backward compatible elevation of the CardView.
	 * <p>
	 * Calling this method has no effect if device OS version is L or newer and
	 * {@link #getUseCompatPadding()} is <code>false</code>.
	 *
	 * @param radius The backward compatible elevation in pixels.
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabElevation
	 * @see #setCardElevation(float)
	 * @see #getMaxCardElevation()
	public void setMaxCardElevation(float radius) {
		IMPL.setMaxElevation(this, radius);
	}
	*/

	/*
	 * Returns the backward compatible elevation of the CardView.
	 *
	 * @return Elevation of the CardView
	 * @see #setMaxCardElevation(float)
	 * @see #getCardElevation()
	public float getMaxCardElevation() {
		return IMPL.getMaxElevation(this);
	}
	*/

	/**
	 * Updates the backward compatible elevation of the CardView.
	 *
	 * @param radius The backward compatible elevation in pixels.
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabElevation
	 * @see #getSupportElevation()
	 * @see #setSupportMaxElevation(float)
	 */
	public void setSupportElevation(float radius) {
		IMPL.setElevation(this, radius);
	}

	/**
	 * Returns the backward compatible elevation of the CardView.
	 *
	 * @return Elevation of the CardView
	 * @see #setSupportElevation(float)
	 * @see #getSupportMaxElevation()
	 */
	public float getSupportElevation() {
		return IMPL.getElevation(this);
	}

	/**
	 * Updates the backward compatible elevation of the CardView.
	 * <p>
	 * Calling this method has no effect if device OS version is L or newer and
	 * {@link #getUseCompatPadding()} is <code>false</code>.
	 *
	 * @param radius The backward compatible elevation in pixels.
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabElevation
	 * @see #setSupportElevation(float)
	 * @see #getSupportMaxElevation()
	 */
	public void setSupportMaxElevation(float radius) {
		IMPL.setMaxElevation(this, radius);
	}

	/**
	 * Returns the backward compatible elevation of the CardView.
	 *
	 * @return Elevation of the CardView
	 * @see #setSupportMaxElevation(float)
	 * @see #getSupportElevation()
	 */
	public float getSupportMaxElevation() {
		return IMPL.getMaxElevation(this);
	}
	/* END FAB */

	/**
	 * Returns whether CardView should add extra padding to content to avoid overlaps with rounded
	 * corners on API versions 20 and below.
	 *
	 * @return True if CardView prevents overlaps with rounded corners on platforms before L.
	 *         Default value is <code>true</code>.
	 */
	@Override
	public boolean getPreventCornerOverlap() {
		return mPreventCornerOverlap;
	}

	/**
	 * On API 20 and before, CardView does not clip the bounds of the Card for the rounded corners.
	 * Instead, it adds padding to content so that it won't overlap with the rounded corners.
	 * You can disable this behavior by setting this field to <code>false</code>.
	 * <p>
	 * Setting this value on API 21 and above does not have any effect unless you have enabled
	 * compatibility padding.
	 *
	 * @param preventCornerOverlap Whether CardView should add extra padding to content to avoid
	 *                             overlaps with the CardView corners.
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabPreventCornerOverlap
	 * @see #setUseCompatPadding(boolean)
	 */
	public void setPreventCornerOverlap(boolean preventCornerOverlap) {
		if (preventCornerOverlap == mPreventCornerOverlap) {
			return;
		}
		mPreventCornerOverlap = preventCornerOverlap;
		IMPL.onPreventCornerOverlapChanged(this);
	}
}