package com.slaterama.floatingactionbutton.widget;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.StateSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.slaterama.floatingactionbutton.GraphicsCompat;
import com.slaterama.floatingactionbutton.LogEx;
import com.slaterama.floatingactionbutton.R;
import com.slaterama.floatingactionbutton.ViewCompatEx;

@TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
public class RoundedButtonBackgroundEclairMr1 extends Drawable
		implements RoundedButtonBackgroundImpl {

	protected final static DecelerateInterpolator INTERPOLATOR = new DecelerateInterpolator(3.0f);

	protected RoundedButtonDelegate mDelegate;
	protected ColorStateList mColor;
	protected float mCornerRadius;
	protected float mElevation;
	protected float mMaxElevation;
	protected float mVisualElevation;

	private final Paint mPaint;
	private final Paint mCornerShadowPaint;
	private final Paint mEdgeShadowPaint;
	private final Paint mSolidPaint;
	private final long mShortAnimTime;

	final RectF mButtonBounds;
	Path mCornerShadowPath;

	private final int mShadowStartColor;
	private final int mShadowEndColor;
	final float mInsetShadowExtra;

	private boolean mDirty = true;

	private boolean mPressed = false;
	private long mAnimStartTime;
	private float mAnimFromElevation;
	private float mAnimToElevation;
	private long mAnimDuration;

	@SuppressWarnings("unused")
	public RoundedButtonBackgroundEclairMr1(RoundedButtonDelegate delegate, ColorStateList color,
	                                        float cornerRadius, float elevation,
	                                        float maxElevation, boolean useCompatPadding) {
		mDelegate = delegate;
		View view = delegate.getView();

		Resources resources = view.getResources();
		mShortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime);
		mShadowStartColor = resources.getColor(R.color.rounded_btn_shadow_start_color);
		mShadowEndColor = resources.getColor(R.color.rounded_btn_shadow_end_color);
		mInsetShadowExtra = resources.getDimension(R.dimen.rounded_btn_compat_inset_shadow);

		mColor = color;
		mCornerRadius = cornerRadius;
		mElevation = elevation;
		mVisualElevation = elevation;
		mMaxElevation = maxElevation;

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mPaint.setColor(color.getDefaultColor());
		mCornerShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mCornerShadowPaint.setStyle(Paint.Style.FILL);
		mCornerShadowPaint.setDither(true);
		mEdgeShadowPaint = new Paint(mCornerShadowPaint);

		mSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mSolidPaint.setColor(mShadowStartColor);
		mSolidPaint.setStyle(Paint.Style.FILL);
		mSolidPaint.setDither(true);

		mButtonBounds = new RectF();

		ViewCompatEx.setBackground(view, this);

		invalidatePadding();
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
		mCornerShadowPaint.setAlpha(alpha);
		mEdgeShadowPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
		mCornerShadowPaint.setColorFilter(cf);
		mEdgeShadowPaint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}

	@Override
	public boolean isStateful() {
		return mColor.isStateful();
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mDirty = true;
	}

	@Override
	protected boolean onStateChange(int[] stateSet) {
		boolean animate = false;
		if (StateSet.stateSetMatches(SPECS_ENABLED_PRESSED, stateSet) && !mPressed) {
			animateShadow(true);
			animate = true;
		} else if (StateSet.stateSetMatches(SPECS_DEFAULT, stateSet) && mPressed) {
			animateShadow(false);
			animate = true;
		}

		int color = mColor.getColorForState(stateSet, mColor.getDefaultColor());
		if (mPaint.getColor() == color) {
			return super.onStateChange(stateSet) || animate;
		} else {
			mPaint.setColor(color);
			invalidateSelf();
			return true;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if (mDirty) {
			mDirty = false;
			buildComponents(getBounds());
		}
		canvas.translate(0, mVisualElevation / 2);
		drawShadow(canvas);
		canvas.translate(0, -mVisualElevation / 2);
		GraphicsCompat.drawRoundRect(canvas, mButtonBounds, mCornerRadius, mCornerRadius, mPaint);

		if (mAnimStartTime > 0L) {
			long elapsedDuration = System.currentTimeMillis() - mAnimStartTime;
			if (elapsedDuration > mAnimDuration) {
				mAnimStartTime = 0L;
				mVisualElevation = mAnimToElevation;
			} else {
				float interpolatedTime = INTERPOLATOR.getInterpolation(elapsedDuration /
						(float) mAnimDuration);
				mVisualElevation = mAnimFromElevation +
						(mAnimToElevation - mAnimFromElevation) * interpolatedTime;
			}
			mDirty = true;
			invalidateSelf();
		}
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
			mDirty = true;
			invalidateSelf();
			mDelegate.getView().requestLayout();
		}
	}

	@Override
	public float getElevation() {
		return mElevation;
	}

	@Override
	public void setElevation(float elevation) {
		if (elevation != mElevation) {
			mElevation = elevation;
			mVisualElevation = elevation;
			mAnimStartTime = 0L;
			mDirty = true;
			invalidateSelf();
		}
	}

	@Override
	public float getMaxElevation() {
		return mMaxElevation;
	}

	@Override
	public void setMaxElevation(float maxElevation) {
		if (maxElevation != mMaxElevation) {
			mMaxElevation = maxElevation;
			invalidatePadding();
		}
	}

	@Override
	public boolean isUseCompatPadding() {
		return true;
	}

	@Override
	public void setUseCompatPadding(boolean useCompatPadding) {
		// NO OP
	}

	private void drawShadow(Canvas canvas) {
		float insetShadow = mVisualElevation / 2 + mInsetShadowExtra;
		final float edgeShadowTop = -mCornerRadius - mVisualElevation;
		final float edgeShadowBottom = Math.min(-mCornerRadius + insetShadow, 0.0f);
		float inset = mCornerRadius;
		final boolean drawHorizontalEdges = mButtonBounds.width() > 2 * mCornerRadius;
		final boolean drawVerticalEdges = mButtonBounds.height() > 2 * mCornerRadius;

		// LT
		int saved = canvas.save();
		canvas.translate(mButtonBounds.left + inset, mButtonBounds.top + inset);
		canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
		if (drawHorizontalEdges) {
			canvas.drawRect(0, edgeShadowTop,
					mButtonBounds.width() - 2 * inset, edgeShadowBottom,
					mEdgeShadowPaint);
		}
		canvas.restoreToCount(saved);
		// RB
		saved = canvas.save();
		canvas.translate(mButtonBounds.right - inset, mButtonBounds.bottom - inset);
		canvas.rotate(180f);
		canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
		if (drawHorizontalEdges) {
			canvas.drawRect(0, edgeShadowTop,
					mButtonBounds.width() - 2 * inset, edgeShadowBottom,
					mEdgeShadowPaint);
		}
		canvas.restoreToCount(saved);
		// LB
		saved = canvas.save();
		canvas.translate(mButtonBounds.left + inset, mButtonBounds.bottom - inset);
		canvas.rotate(270f);
		canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
		if (drawVerticalEdges) {
			canvas.drawRect(0, edgeShadowTop,
					mButtonBounds.height() - 2 * inset, edgeShadowBottom,
					mEdgeShadowPaint);
		}
		canvas.restoreToCount(saved);
		// RT
		saved = canvas.save();
		canvas.translate(mButtonBounds.right - inset, mButtonBounds.top + inset);
		canvas.rotate(90f);
		canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
		if (drawVerticalEdges) {
			canvas.drawRect(0, edgeShadowTop,
					mButtonBounds.height() - 2 * inset, edgeShadowBottom,
					mEdgeShadowPaint);
		}
		canvas.restoreToCount(saved);
		// Center
		if (drawHorizontalEdges && drawVerticalEdges) {
			saved = canvas.save();
			canvas.translate(mButtonBounds.left + inset, mButtonBounds.top + inset);
			canvas.drawRect(0, 0,
					mButtonBounds.width() - 2 * inset, mButtonBounds.height() - 2 * inset,
					mSolidPaint);
			canvas.restoreToCount(saved);
		}
	}

	private void buildShadowCorners() {
		if (mVisualElevation == mMaxElevation) {
//			LogEx.d();
		}

		float insetShadow = mVisualElevation / 2 + mInsetShadowExtra;
		float innerRadius = Math.max(mCornerRadius - insetShadow, 0.0f);
		float outerRadius = mCornerRadius + mVisualElevation;
		RectF innerBounds = new RectF(-innerRadius, -innerRadius, innerRadius, innerRadius);
		RectF outerBounds = new RectF(-outerRadius, -outerRadius, outerRadius, outerRadius);

		if (mCornerShadowPath == null) {
			mCornerShadowPath = new Path();
		} else {
			mCornerShadowPath.reset();
		}
		mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
		mCornerShadowPath.moveTo(-innerRadius, 0);
		mCornerShadowPath.lineTo(-outerRadius, 0);
		// outer arc
		mCornerShadowPath.arcTo(outerBounds, 180f, 90f, false);
		// inner arc
		mCornerShadowPath.arcTo(innerBounds, 270f, -90f, false);
		mCornerShadowPath.close();

		final float startRatio = innerRadius / outerRadius;
		final int[] colors = new int[]{mShadowStartColor, mShadowStartColor, mShadowEndColor};
		final float[] stops = new float[]{0.0f, startRatio, 1.0f};
		RadialGradient radialGradient = new RadialGradient(0, 0, outerRadius,
				colors, stops, Shader.TileMode.CLAMP);
		mCornerShadowPaint.setShader(radialGradient);

		// We offset the content elevation/2 pixels up to make it more realistic.
		// this is why edge shadow shader has some extra space
		// When drawing bottom edge shadow, we use that extra space.
		LinearGradient linearGradient = new LinearGradient(
				0, 0,
				0, -outerRadius,
				colors, stops, Shader.TileMode.CLAMP);
		mEdgeShadowPaint.setShader(linearGradient);
	}

	private void buildComponents(Rect bounds) {
		// Button is offset SHADOW_MULTIPLIER * maxElevation to account for the shadow shift.
		// We could have different top-bottom offsets to avoid extra gap above but in that case
		// center aligning Views inside the Button would be problematic.
		final float verticalOffset = mMaxElevation * SHADOW_MULTIPLIER;
		mButtonBounds.set(bounds.left + mMaxElevation, bounds.top + verticalOffset,
				bounds.right - mMaxElevation, bounds.bottom - verticalOffset);
		buildShadowCorners();
	}

	protected void invalidatePadding() {
		invalidateSelf();
		int paddingHorizontal = (int) Math.ceil(mMaxElevation);
		int paddingVertical = (int) Math.ceil(mMaxElevation * SHADOW_MULTIPLIER);
		mDelegate.setShadowPadding(paddingHorizontal, paddingVertical, paddingHorizontal,
				paddingVertical);
	}

	protected void animateShadow(boolean pressed) {
		mPressed = pressed;
		mAnimStartTime = System.currentTimeMillis();
		mAnimFromElevation = mVisualElevation;
		mAnimToElevation = (pressed ? mMaxElevation : mElevation);
		float fraction = (Math.abs(mAnimToElevation - mAnimFromElevation) /
				Math.abs(mMaxElevation - mElevation));
		mAnimDuration = (long) (mShortAnimTime * fraction);
		mDirty = true;
		invalidateSelf();
	}
}
