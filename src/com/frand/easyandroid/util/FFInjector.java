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

import com.frand.easyandroid.FFActivity;
import com.frand.easyandroid.annotation.FFEventListener;
import com.frand.easyandroid.annotation.FFResInject;
import com.frand.easyandroid.annotation.FFSelect;
import com.frand.easyandroid.annotation.FFViewInject;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

public class FFInjector {
	
	// 注解的单例，目的是运作快，省内存，又可扩展
	private static FFInjector instance;

	private FFInjector() {

	}

	public static FFInjector getInstance() {
		if (instance == null) {
			instance = new FFInjector();
		}
		return instance;
	}

	/**
	 * 将此activity里所有标注了注解的字段注入内容
	 * @param activity
	 */
	public void inJectAll(Activity activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(FFViewInject.class)) {
					injectView(activity, ((FFActivity)activity).getContentView(), field);
				} else if (field.isAnnotationPresent(FFResInject.class)) {
					injectResource(activity.getResources(), activity, field);
				}
			}
		}
	}

	/**
	 * 将此activity里所有标注了View注解的字段注入内容及方法
	 * @param activity
	 */
	private void injectView(Object object, View view, Field field) {
		if (field.isAnnotationPresent(FFViewInject.class)) {
			FFViewInject viewInject = field.getAnnotation(FFViewInject.class);
			int viewId = viewInject.id();
			try {
				field.setAccessible(true);
				field.set(object, view.findViewById(viewId));
			} catch (Exception e) {
				e.printStackTrace();
			}
			String clickMethod = viewInject.click();
			if (!TextUtils.isEmpty(clickMethod)) {
				setViewClickMethod(object, field, clickMethod);
			}
			String longClickMethod = viewInject.longClick();
			if (!TextUtils.isEmpty(longClickMethod)) {
				setViewLongClickMethod(object, field, longClickMethod);
			}
			String itemClickMethod = viewInject.itemClick();
			if (!TextUtils.isEmpty(itemClickMethod)) {
				setViewItemClickMethod(object, field, itemClickMethod);
			}
			String itemLongClickMethod = viewInject.itemLongClick();
			if (!TextUtils.isEmpty(itemLongClickMethod)) {
				setViewItemLongClickMethod(object, field, itemLongClickMethod);
			}
			FFSelect ffSelect = viewInject.select();
			if (!TextUtils.isEmpty(ffSelect.selected())) {
				setViewSelectListener(object, field, ffSelect.selected(),
						ffSelect.noSelected());
			}
		}
	}

	/**
	 * 设置此object对象的field字段的点击事件操作
	 * @param object
	 * @param field
	 * @param clickMethod
	 */
	private void setViewClickMethod(Object object, Field field,
			String clickMethod) {
		try {
			Object view = field.get(object);
			if (view instanceof View) {
				System.out.println("view clicked "+object.getClass().getName()+" "+field.getName());
				((View) view).setOnClickListener(new FFEventListener(object).onclick(clickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置此object对象的field字段的长按事件操作
	 * @param object
	 * @param field
	 * @param longClickMethod
	 */
	private void setViewLongClickMethod(Object object, Field field,
			String longClickMethod) {
		try {
			Object view = field.get(object);
			if (view instanceof View) {
				((View) view).setOnLongClickListener(new FFEventListener(
						object).onLongClick(longClickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置此object对象的field字段的itemClick事件操作
	 * @param object
	 * @param field
	 * @param itemClickMethod
	 */
	private void setViewItemClickMethod(Object object, Field field,
			String itemClickMethod) {
		try {
			Object view = field.get(object);
			if (view instanceof AbsListView) {
				((AbsListView) view)
						.setOnItemClickListener(new FFEventListener(object)
								.itemClick(itemClickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置此object对象的field字段的itemLongClick事件操作
	 * @param object
	 * @param field
	 * @param itemLongClickMethod
	 */
	private void setViewItemLongClickMethod(Object object, Field field,
			String itemLongClickMethod) {
		try {
			Object view = field.get(object);
			if (view instanceof AbsListView) {
				((AbsListView) view)
						.setOnItemLongClickListener(new FFEventListener(
								object).itemLongClick(itemLongClickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置此object对象的field字段的itemSelect事件操作
	 * @param object
	 * @param field
	 * @param select
	 * @param noSelect
	 */
	private void setViewSelectListener(Object object, Field field,
			String select, String noSelect) {
		try {
			Object view = field.get(object);
			if (view instanceof View) {
				((AdapterView<?>) view)
						.setOnItemSelectedListener(new FFEventListener(object)
								.itemSelect(select).noSelect(noSelect));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	/**
	 * 将资源注入对象的字段
	 * @param resources 提供所有的资源
	 * @param object 需要注入的对象承载者
	 * @param field 需要注入的字段
	 */
	private void injectResource(Resources resources, Object object, Field field) {
		if (field.isAnnotationPresent(FFResInject.class)) {
			FFResInject resourceJect = field.getAnnotation(FFResInject.class);
			int resourceID = resourceJect.id();
			try {
				field.setAccessible(true);
				String type = resources.getResourceTypeName(resourceID);
				if (type.equalsIgnoreCase("string")) {
					field.set(object, resources.getString(resourceID));
				} else if (type.equalsIgnoreCase("drawable")) {
					field.set(object, resources.getDrawable(resourceID));
				} else if (type.equalsIgnoreCase("layout")) {
					field.set(object, resources.getLayout(resourceID));
				} else if (type.equalsIgnoreCase("array")) {
					if (field.getType().equals(int[].class)) {
						field.set(object, resources.getIntArray(resourceID));
					} else if (field.getType().equals(String[].class)) {
						field.set(object, resources.getStringArray(resourceID));
					} else {
						field.set(object, resources.getStringArray(resourceID));
					}
				} else if (type.equalsIgnoreCase("color")) {
					if (field.getType().equals(Integer.TYPE)) {
						field.set(object, resources.getColor(resourceID));
					} else {
						field.set(object, resources.getColorStateList(resourceID));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将此activity类中的所有标记view id的变量都注入字段中
	 * @param activity
	 */
	public void injectView(Activity activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(FFViewInject.class)) {
					injectView(activity, ((FFActivity)activity).getContentView(), field);
				}
			}
		}
	}
	
	/**
	 * 将此fragment类中的所有标记view id的变量都注入字段中
	 * @param fragment
	 */
	public void injectView(Fragment fragment, View view) {
		Field[] fields = fragment.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(FFViewInject.class)) {
					injectView(fragment, view, field);
				}
			}
		}
	}

	/**
	 * 将此activity类中的所有标记资源的变量都注入字段中
	 * @param activity
	 */
	public void injectResource(Activity activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(FFResInject.class)) {
					injectResource(activity.getResources(), activity, field);
				}
			}
		}
	}
	
	/**
	 * 将此fragment类中的所有标记资源的变量都注入字段中
	 * @param fragment
	 */
	public void injectResource(Fragment fragment) {
		Field[] fields = fragment.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(FFResInject.class)) {
					injectResource(fragment.getActivity().getResources(), fragment, field);
				}
			}
		}
	}

}
