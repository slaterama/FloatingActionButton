package com.slaterama.floatingactionbutton;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

public class ViewCompatEx extends android.support.v4.view.ViewCompat {

	interface ViewCompatExImpl {
		void setBackground(View view, Drawable background);
	}

	@TargetApi(Build.VERSION_CODES.BASE)
	@SuppressWarnings("deprecation")
	static class BaseViewCompatExImpl implements ViewCompatExImpl {
		@Override
		public void setBackground(View view, Drawable background) {
			view.setBackgroundDrawable(background);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	static class JellybeanViewCompatExImpl implements ViewCompatExImpl {
		@Override
		public void setBackground(View view, Drawable background) {
			view.setBackground(background);
		}
	}

	static final ViewCompatExImpl IMPL;
	static {
		final int version = Build.VERSION.SDK_INT;
		if (version > Build.VERSION_CODES.JELLY_BEAN) {
			IMPL = new JellybeanViewCompatExImpl();
		} else {
			IMPL = new BaseViewCompatExImpl();
		}
	}

	public static void setBackground(View view, Drawable background) {
		IMPL.setBackground(view, background);
	}
}
