package com.slaterama.floatingactionbutton.widget;

import android.view.View;

public interface RoundedButtonDelegate {
	View getView();
	void setShadowPadding(int left, int top, int right, int bottom);
}
