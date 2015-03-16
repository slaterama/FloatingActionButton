package com.slaterama.floatingactionbutton;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class GraphicsCompat {

	private GraphicsCompat() {
	}

	interface GraphicsImpl {
		void drawRoundRect(@NonNull Canvas canvas, @Nullable RectF rect, float rx, float ry,
		                   @Nullable Paint paint);
		void drawRoundRect(@NonNull Canvas canvas, float left, float top, float right, float bottom,
		                   float rx, float ry, @NonNull Paint paint);
	}

	static class BaseGraphicsImpl implements GraphicsImpl {
		private final RectF mCornerRect = new RectF();

		@Override
		public void drawRoundRect(@NonNull Canvas canvas, @Nullable RectF rect, float rx, float ry,
		                          @Nullable Paint paint) {
			// Draws a round rect using 7 draw operations. This is faster than using
			// canvas.drawRoundRect before JBMR1 because API 11-16 used alpha mask textures to draw
			// shapes.
			if (rect != null) {
				final float twoRx = rx * 2;
				final float twoRy = ry * 2;
				final float innerWidth = rect.width() - twoRx;
				final float innerHeight = rect.height() - twoRy;
				mCornerRect.set(rect.left, rect.top, rect.left + twoRx, rect.top + twoRy);

				canvas.drawArc(mCornerRect, 180, 90, true, paint);
				mCornerRect.offset(innerWidth, 0);
				canvas.drawArc(mCornerRect, 270, 90, true, paint);
				mCornerRect.offset(0, innerHeight);
				canvas.drawArc(mCornerRect, 0, 90, true, paint);
				mCornerRect.offset(-innerWidth, 0);
				canvas.drawArc(mCornerRect, 90, 90, true, paint);

				//draw top and bottom pieces
				canvas.drawRect(rect.left + rx, rect.top, rect.right - rx, rect.top + ry, paint);
				canvas.drawRect(rect.left + rx, rect.bottom - ry, rect.right - rx, rect.bottom,
						paint);

				//center
				canvas.drawRect(rect.left, rect.top + ry, rect.right, rect.bottom - ry, paint);
			}
		}

		@Override
		public void drawRoundRect(@NonNull Canvas canvas, float left, float top, float right,
		                          float bottom, float rx, float ry, @NonNull Paint paint) {
			drawRoundRect(canvas, new RectF(left, top, right, bottom), rx, ry, paint);
		}

		/*
		@Override
		public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius, Paint paint) {
			// Draws a round rect using 7 draw operations. This is faster than using
			// canvas.drawRoundRect before JBMR1 because API 11-16 used alpha mask textures to draw
			// shapes.
			final float twoRadius = cornerRadius * 2;
			final float innerWidth = bounds.width() - twoRadius;
			final float innerHeight = bounds.height() - twoRadius;
			mCornerRect.set(bounds.left, bounds.top,
					bounds.left + cornerRadius * 2, bounds.top + cornerRadius * 2);

			canvas.drawArc(mCornerRect, 180, 90, true, paint);
			mCornerRect.offset(innerWidth, 0);
			canvas.drawArc(mCornerRect, 270, 90, true, paint);
			mCornerRect.offset(0, innerHeight);
			canvas.drawArc(mCornerRect, 0, 90, true, paint);
			mCornerRect.offset(-innerWidth, 0);
			canvas.drawArc(mCornerRect, 90, 90, true, paint);

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
		*/
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	static class JellybeanMr1GraphicsImpl implements GraphicsImpl {
		@Override
		public void drawRoundRect(@NonNull Canvas canvas, @Nullable RectF rect, float rx, float ry,
		                          @Nullable Paint paint) {
			canvas.drawRoundRect(rect, rx, ry, paint);
		}

		@Override
		public void drawRoundRect(@NonNull Canvas canvas, float left, float top, float right,
		                          float bottom, float rx, float ry, @NonNull Paint paint) {
			canvas.drawRoundRect(new RectF(left, top, right, bottom), rx, ry, paint);
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	static class LollipopGraphicsImpl extends JellybeanMr1GraphicsImpl {
		@Override
		public void drawRoundRect(@NonNull Canvas canvas, float left, float top, float right,
		                          float bottom, float rx, float ry, @NonNull Paint paint) {
			canvas.drawRoundRect(left, top, right, bottom, rx, ry, paint);
		}
	}

	static final GraphicsImpl IMPL;
	static {
		final int version = Build.VERSION.SDK_INT;
		if (version >= Build.VERSION_CODES.LOLLIPOP) {
			IMPL = new LollipopGraphicsImpl();
		} else if (version >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			IMPL = new JellybeanMr1GraphicsImpl();
		} else {
			IMPL = new BaseGraphicsImpl();
		}
	}

	public static void drawRoundRect(@NonNull Canvas canvas, @Nullable RectF rect, float rx,
	                                 float ry, @Nullable Paint paint) {
		IMPL.drawRoundRect(canvas, rect, rx, ry, paint);
	}

	public static void drawRoundRect(@NonNull Canvas canvas, float left, float top, float right,
	                                 float bottom, float rx, float ry, @Nullable Paint paint) {
		IMPL.drawRoundRect(canvas, left, top, right, bottom, rx, ry, paint);
	}
}
