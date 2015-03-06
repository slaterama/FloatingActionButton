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

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;

/**
 * Very simple drawable that draws a rounded rectangle background with arbitrary corners and also
 * reports proper outline for L.
 * <p>
 * Simpler and uses less resources compared to GradientDrawable or ShapeDrawable.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class RippleRoundRectDrawable extends RippleDrawable {
	ColorStateList mColor;
	RoundRectDrawable mContent;
	Drawable mMask;

	public RippleRoundRectDrawable(ColorStateList color, RoundRectDrawable content, Drawable mask) {
		super(color, content, mask);
		mColor = color;
		mContent = content;
		mMask = mask;
	}

	public RippleRoundRectDrawable(int backgroundColor, int selectedColor, float radius) {
		this(new ColorStateList(
				new int[][]
						{
								new int[]{}
						},
				new int[]
						{
								selectedColor
						}
				),
				new RoundRectDrawable(backgroundColor, radius), null);
	}

	public RoundRectDrawable getContent() {
		return mContent;
	}
}