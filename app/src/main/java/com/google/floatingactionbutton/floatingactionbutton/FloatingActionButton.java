package com.google.floatingactionbutton.floatingactionbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

import com.google.floatingactionbutton.R;

/**
 * An ImageButton with a rounded corner background and shadow.
 * <p>
 * FloatingActionButton uses <code>elevation</code> property on L for shadows and falls back to a
 * custom shadow implementation on older platforms.
 * <p>
 * Due to expensive nature of rounded corner clipping, on platforms before L, FloatingActionButton
 * does not clip its children that intersect with rounded corners. Instead, it adds padding to
 * avoid such intersection (See {@link #setPreventCornerOverlap(boolean)} to change this behavior).
 * <p>
 * Before L, FloatingActionButton adds padding to its content and draws shadows to that area. This
 * padding amount is equal to <code>maxButtonElevation + (1 - cos45) * cornerRadius</code> on the
 * sides and <code>maxButtonElevation * 1.5 + (1 - cos45) * cornerRadius</code> on top and bottom.
 * <p>
 * Since padding is used to offset content for shadows, you cannot set padding on
 * FloatingActionButton. Instead, you can use content padding attributes in XML or
 * {@link #setContentPadding(int, int, int, int)} in code to set the padding between the edges of
 * the Button and children of FloatingActionButton.
 * <p>
 * Note that, if you specify exact dimensions for the FloatingActionButton, because of the shadows,
 * its content area will be different between platforms before L and after L. By using api version
 * specific resource values, you can avoid these changes. Alternatively, If you want
 * FloatingActionButton to add inner padding on platforms L and after as well, you can set
 * {@link #setUseCompatPadding(boolean)} to <code>true</code>.
 * <p>
 * To change FloatingActionButton's elevation in a backward compatible way, use
 * {@link #setButtonElevation(float)}. FloatingActionButton will use elevation API on L and before
 * L, it will change the shadow size. To avoid moving the View while shadow size is changing,
 * shadow size is clamped by {@link #getMaxButtonElevation()}. If you want to change elevation
 * dynamically, you should call {@link #setMaxButtonElevation(float)} when FloatingActionButton is
 * initialized.
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
public class FloatingActionButton extends ImageButton
		implements FloatingActionButtonDelegate {

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

	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr,
								int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
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
	 * Returns whether FloatingActionButton will add inner padding on platforms L and after.
	 *
	 * @return True FloatingActionButton adds inner padding on platforms L and after to have same
	 * dimensions with platforms before L.
	 */
	@Override
	public boolean getUseCompatPadding() {
		return mCompatPadding;
	}

	/**
	 * FloatingActionButton adds additional padding to draw shadows on platforms before L.
	 * <p>
	 * This may cause FloatingActionButtons to have different sizes between L and before L. If you
	 * need to align FloatingActionButton with other Views, you may need api version specific
	 * dimension resources to account for the changes.
	 * As an alternative, you can set this flag to <code>true</code> and FloatingActionButton will
	 * add the same padding values on platforms L and after.
	 * <p>
	 * Since setting this flag to true adds unnecessary gaps in the UI, default value is
	 * <code>false</code>.
	 *
	 * @param useCompatPadding True if FloatingActionButton should add padding for the shadows on
	 *                         platforms L and above.
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_cardUseCompatPadding
	 */
	public void setUseCompatPadding(boolean useCompatPadding) {
		if (mCompatPadding == useCompatPadding) {
			return;
		}
		mCompatPadding = useCompatPadding;
		IMPL.onCompatPaddingChanged(this);
	}

	/**
	 * Sets the padding between the Buttons's edges and the children of FloatingActionButton.
	 * <p>
	 * Depending on platform version or {@link #getUseCompatPadding()} settings,
	 * FloatingActionButton may update these values before calling
	 * {@link android.view.View#setPadding(int, int, int, int)}.
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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		if (!(IMPL instanceof FloatingActionButtonApi21)) {
			final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
			switch (widthMode) {
				case MeasureSpec.EXACTLY:
				case MeasureSpec.AT_MOST:
					final int minWidth = (int) Math.ceil(IMPL.getMinWidth(this));

					Log.d("FAB", String.format("minWidthDpi=%d",
							(int) (minWidth * 1.0f / getResources().getDisplayMetrics().density)));

					widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minWidth,
							MeasureSpec.getSize(widthMeasureSpec)), widthMode);
					break;
			}

			final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
			switch (heightMode) {
				case MeasureSpec.EXACTLY:
				case MeasureSpec.AT_MOST:
					final int minHeight = (int) Math.ceil(IMPL.getMinHeight(this));

					Log.d("FAB", String.format("minHeightDpi=%d",
							(int) (minHeight * 1.0f / getResources().getDisplayMetrics().density)));

					heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minHeight,
							MeasureSpec.getSize(heightMeasureSpec)), heightMode);
					break;
			}
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		} else {
//			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		}
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
	 * Returns the inner padding after the Button's left edge
	 *
	 * @return the inner padding after the Button's left edge
	 */
	public int getContentPaddingLeft() {
		return mContentPadding.left;
	}

	/**
	 * Returns the inner padding before the Button's right edge
	 *
	 * @return the inner padding before the Button's right edge
	 */
	public int getContentPaddingRight() {
		return mContentPadding.right;
	}

	/**
	 * Returns the inner padding after the Button's top edge
	 *
	 * @return the inner padding after the Button's top edge
	 */
	public int getContentPaddingTop() {
		return mContentPadding.top;
	}

	/**
	 * Returns the inner padding before the Button's bottom edge
	 *
	 * @return the inner padding before the Button's bottom edge
	 */
	public int getContentPaddingBottom() {
		return mContentPadding.bottom;
	}

	/**
	 * Updates the corner radius of the FloatingActionButton.
	 *
	 * @param radius The radius in pixels of the corners of the rectangle shape
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabCornerRadius
	 * @see #setRadius(float)
	 */
	public void setRadius(float radius) {
		IMPL.setRadius(this, radius);
	}

	/**
	 * Returns the corner radius of the FloatingActionButton.
	 *
	 * @return Corner radius of the FloatingActionButton
	 * @see #getRadius()
	 */
	public float getRadius() {
		return IMPL.getRadius(this);
	}

	/**
	 * Internal method used by FloatingActionButton implementations to update the padding.
	 *
	 * @hide
	 */
	@Override
	public void setShadowPadding(int left, int top, int right, int bottom) {
		mShadowBounds.set(left, top, right, bottom);
		super.setPadding(left + mContentPadding.left, top + mContentPadding.top,
				right + mContentPadding.right, bottom + mContentPadding.bottom);
	}

	/**
	 * Updates the backward compatible elevation of the FloatingActionButton.
	 *
	 * @param radius The backward compatible elevation in pixels.
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabElevation
	 * @see #getButtonElevation()
	 * @see #setMaxButtonElevation(float)
	 */
	public void setButtonElevation(float radius) {
		IMPL.setElevation(this, radius);
	}

	/**
	 * Returns the backward compatible elevation of the FloatingActionButton.
	 *
	 * @return Elevation of the FloatingActionButton
	 * @see #setButtonElevation(float)
	 * @see #getMaxButtonElevation()
	 */
	public float getButtonElevation() {
		return IMPL.getElevation(this);
	}

	/**
	 * Updates the backward compatible elevation of the FloatingActionButton.
	 * <p>
	 * Calling this method has no effect if device OS version is L or newer and
	 * {@link #getUseCompatPadding()} is <code>false</code>.
	 *
	 * @param radius The backward compatible elevation in pixels.
	 * @attr ref android.support.v7.cardview.R.styleable#FloatingActionButton_fabElevation
	 * @see #setButtonElevation(float)
	 * @see #getMaxButtonElevation()
	 */
	public void setMaxButtonElevation(float radius) {
		IMPL.setMaxElevation(this, radius);
	}

	/**
	 * Returns the backward compatible elevation of the FloatingActionButton.
	 *
	 * @return Elevation of the FloatingActionButton
	 * @see #setMaxButtonElevation(float)
	 * @see #getButtonElevation()
	 */
	public float getMaxButtonElevation() {
		return IMPL.getMaxElevation(this);
	}

	/**
	 * Returns whether FloatingActionButton should add extra padding to content to avoid overlaps
	 * with rounded corners on API versions 20 and below.
	 *
	 * @return True if FloatingActionButton prevents overlaps with rounded corners on platforms
	 * before L. Default value is <code>true</code>.
	 */
	@Override
	public boolean getPreventCornerOverlap() {
		return mPreventCornerOverlap;
	}

	/**
	 * On API 20 and before, FloatingActionButton does not clip the bounds of the Button for the
	 * rounded corners. Instead, it adds padding to content so that it won't overlap with the
	 * rounded corners. You can disable this behavior by setting this field to <code>false</code>.
	 * <p>
	 * Setting this value on API 21 and above does not have any effect unless you have enabled
	 * compatibility padding.
	 *
	 * @param preventCornerOverlap Whether FloatingActionButton should add extra padding to content
	 *                             to avoid overlaps with the FloatingActionButton corners.
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
