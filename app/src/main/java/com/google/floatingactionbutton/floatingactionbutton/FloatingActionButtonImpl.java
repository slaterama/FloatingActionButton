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
/**
 * Interface for platform specific CardView implementations.
 */
interface FloatingActionButtonImpl {
	void initialize(FloatingActionButtonDelegate fab, Context context,
					int backgroundColor, int selectedColor,
					float radius, float elevation, float maxElevation);

	void setRadius(FloatingActionButtonDelegate fab, float radius);

	float getRadius(FloatingActionButtonDelegate fab);

	void setElevation(FloatingActionButtonDelegate fab, float elevation);

	float getElevation(FloatingActionButtonDelegate fab);

	void initStatic();

	void setMaxElevation(FloatingActionButtonDelegate fab, float maxElevation);

	float getMaxElevation(FloatingActionButtonDelegate fab);

	float getMinWidth(FloatingActionButtonDelegate fab);

	float getMinHeight(FloatingActionButtonDelegate fab);

	void updatePadding(FloatingActionButtonDelegate fab);

	void onCompatPaddingChanged(FloatingActionButtonDelegate fab);

	void onPreventCornerOverlapChanged(FloatingActionButtonDelegate fab);
}