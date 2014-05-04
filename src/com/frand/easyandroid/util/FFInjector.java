/*
 * Copyright (C) 2013 frandfeng
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
package com.frand.easyandroid.util;

import java.lang.reflect.Field;

import com.frand.easyandroid.annotation.FFEventListener;
import com.frand.easyandroid.annotation.FFResInject;
import com.frand.easyandroid.annotation.FFSelect;
import com.frand.easyandroid.annotation.FFViewInject;

import android.app.Activity;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

public class FFInjector {
	private static FFInjector instance;

	private FFInjector() {

	}

	public static FFInjector getInstance() {
		if (instance == null) {
			instance = new FFInjector();
		}
		return instance;
	}

	public void inJectAll(Activity activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(FFViewInject.class)) {
					injectView(activity, field);
				} else if (field.isAnnotationPresent(FFResInject.class)) {
					injectResource(activity, field);
				}
			}
		}
	}

	private void injectView(Activity activity, Field field) {
		if (field.isAnnotationPresent(FFViewInject.class)) {
			FFViewInject viewInject = field.getAnnotation(FFViewInject.class);
			int viewId = viewInject.id();
			try {
				field.setAccessible(true);
				field.set(activity, activity.findViewById(viewId));
			} catch (Exception e) {
				e.printStackTrace();
			}
			String clickMethod = viewInject.click();
			if (!TextUtils.isEmpty(clickMethod)) {
				setViewClickMethod(activity, field, clickMethod);
			}
			String longClickMethod = viewInject.longClick();
			if (!TextUtils.isEmpty(longClickMethod)) {
				setViewLongClickMethod(activity, field, longClickMethod);
			}
			String itemClickMethod = viewInject.itemClick();
			if (!TextUtils.isEmpty(itemClickMethod)) {
				setViewItemClickMethod(activity, field, itemClickMethod);
			}
			String itemLongClickMethod = viewInject.itemLongClick();
			if (!TextUtils.isEmpty(itemLongClickMethod)) {
				setViewItemLongClickMethod(activity, field, itemLongClickMethod);
			}
			FFSelect ffSelect = viewInject.select();
			if (!TextUtils.isEmpty(ffSelect.selected())) {
				setViewSelectListener(activity, field, ffSelect.selected(),
						ffSelect.noSelected());
			}
		}
	}

	private void setViewClickMethod(Activity activity, Field field,
			String clickMethod) {
		try {
			Object object = field.get(activity);
			if (object instanceof View) {
				((View) object)
						.setOnClickListener(new FFEventListener(activity)
								.onclick(clickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setViewLongClickMethod(Activity activity, Field field,
			String longClickMethod) {
		try {
			Object object = field.get(activity);
			if (object instanceof View) {
				((View) object).setOnLongClickListener(new FFEventListener(
						activity).onLongClick(longClickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setViewItemClickMethod(Activity activity, Field field,
			String itemClickMethod) {
		try {
			Object object = field.get(activity);
			if (object instanceof AbsListView) {
				((AbsListView) object)
						.setOnItemClickListener(new FFEventListener(activity)
								.itemClick(itemClickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setViewItemLongClickMethod(Activity activity, Field field,
			String itemLongClickMethod) {
		try {
			Object object = field.get(activity);
			if (object instanceof AbsListView) {
				((AbsListView) object)
						.setOnItemLongClickListener(new FFEventListener(
								activity).itemLongClick(itemLongClickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setViewSelectListener(Activity activity, Field field,
			String select, String noSelect) {
		try {
			Object object = field.get(activity);
			if (object instanceof View) {
				((AdapterView<?>) object)
						.setOnItemSelectedListener(new FFEventListener(activity)
								.itemSelect(select).noSelect(noSelect));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	private void injectResource(Activity activity, Field field) {
		if (field.isAnnotationPresent(FFResInject.class)) {
			FFResInject resourceJect = field.getAnnotation(FFResInject.class);
			int resourceID = resourceJect.id();
			try {
				field.setAccessible(true);
				Resources resources = activity.getResources();
				String type = resources.getResourceTypeName(resourceID);
				if (type.equalsIgnoreCase("string")) {
					field.set(activity,
							activity.getResources().getString(resourceID));
				} else if (type.equalsIgnoreCase("drawable")) {
					field.set(activity,
							activity.getResources().getDrawable(resourceID));
				} else if (type.equalsIgnoreCase("layout")) {
					field.set(activity,
							activity.getResources().getLayout(resourceID));
				} else if (type.equalsIgnoreCase("array")) {
					if (field.getType().equals(int[].class)) {
						field.set(activity, activity.getResources()
								.getIntArray(resourceID));
					} else if (field.getType().equals(String[].class)) {
						field.set(activity, activity.getResources()
								.getStringArray(resourceID));
					} else {
						field.set(activity, activity.getResources()
								.getStringArray(resourceID));
					}

				} else if (type.equalsIgnoreCase("color")) {
					if (field.getType().equals(Integer.TYPE)) {
						field.set(activity,
								activity.getResources().getColor(resourceID));
					} else {
						field.set(activity, activity.getResources()
								.getColorStateList(resourceID));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void injectView(Activity activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(FFViewInject.class)) {
					injectView(activity, field);
				}
			}
		}
	}

	public void injectResource(Activity activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(FFResInject.class)) {
					injectResource(activity, field);
				}
			}
		}
	}

}
