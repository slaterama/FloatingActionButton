package com.google.floatingactionbutton.floatingactionbutton;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public class CardViewEx extends CardView {

	public CardViewEx(Context context) {
		super(context);
	}

	public CardViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CardViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// Get size requested and size mode
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		final String widthModeString;
		final String heightModeString;

		// Determine width
		switch (widthMode) {
			case MeasureSpec.EXACTLY:
				widthModeString = "EXACTLY";
				break;
			case MeasureSpec.AT_MOST:
				widthModeString = "AT_MOST";
				break;
			case MeasureSpec.UNSPECIFIED:
				widthModeString = "UNSPECIFIED";
				break;
			default:
				widthModeString = "[Unknown]";
		}

		// Determine height
		switch (heightMode) {
			case MeasureSpec.EXACTLY:
				heightModeString = "EXACTLY";
				break;
			case MeasureSpec.AT_MOST:
				heightModeString = "AT_MOST";
				break;
			case MeasureSpec.UNSPECIFIED:
				heightModeString = "UNSPECIFIED";
				break;
			default:
				heightModeString = "[Unknown]";
		}

		String resourceEntryName = "??";
		if (!isInEditMode()) {
			resourceEntryName = getContext().getResources().getResourceEntryName(getId());
		}
//		LogEx.d(String.format("fab=%s, widthMeasureSpec=(%s, %d), heightMeasureSpec=(%s, %d)", resourceEntryName, widthModeString, widthSize, heightModeString, heightSize));
	}
}
