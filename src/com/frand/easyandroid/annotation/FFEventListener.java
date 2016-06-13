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
package com.frand.easyandroid.annotation;

import java.lang.reflect.Method;

import com.frand.easyandroid.exception.FFViewException;
import com.frand.easyandroid.log.FFLogger;
import com.frand.easyandroid.util.FFStringUtil;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 反射OnClickListener, OnItemClickListener, OnItemLongClickListener,
 * 		OnItemSelectedListener, OnLongClickListener
 * @author frand
 *
 */
public class FFEventListener implements OnClickListener, OnItemClickListener,
		OnItemLongClickListener, OnItemSelectedListener, OnLongClickListener {

	private Object handler;
	private String clickMethod;
	private String longClickMethod;
	private String itemClickMethod;
	private String itemLongClickMethod;
	private String itemSelectMethod;
	private String itemNoSelectMethod;

	public FFEventListener(Object handler) {
		this.handler = handler;
	}

	public FFEventListener onclick(String clickMethod) {
		this.clickMethod = clickMethod;
		return this;
	}

	@Override
	public void onClick(View v) {
		invokeClickMethod(handler, clickMethod, v);
	}

	/**
	 * 反射onClick方法
	 * @param handler 反射方法的对象、承载者
	 * @param methodName 要反射的方法的名称
	 * @param params 反射的参数
	 * @return
	 */
	private static Object invokeClickMethod(Object handler, String methodName,
			Object... params) {
		if (handler == null)
			return null;
		Method method = null;
		try {
			method = handler.getClass().getDeclaredMethod(methodName, View.class);
			if (method != null) {
				return method.invoke(handler, params);
			} else {
				throw new FFViewException("no such method");
			}
		} catch (Exception e) {
			FFLogger.e(FFEventListener.class.getName(), FFStringUtil.getErrorInfo(e));
		}
		return null;
	}

	public FFEventListener onLongClick(String longClickMethod) {
		this.longClickMethod = longClickMethod;
		return this;
	}

	@Override
	public boolean onLongClick(View v) {
		return invokeLongClickMethod(handler, longClickMethod, v);
	}

	/**
	 * 反射onLongClick方法
	 * @param handler 反射方法的对象、承载者
	 * @param methodName 要反射的方法的名称
	 * @param params 反射的参数
	 * @return
	 */
	private static boolean invokeLongClickMethod(Object handler,
			String methodName, Object... params) {
		if (handler == null)
			return false;
		Method method = null;
		try {
			method = handler.getClass().getDeclaredMethod(methodName,
					View.class);
			if (method != null) {
				Object object = method.invoke(handler, params);
				return object == null ? false : Boolean.valueOf(object
						.toString());
			} else {
				throw new FFViewException("no such method");
			}
		} catch (Exception e) {
			FFLogger.e(FFEventListener.class.getName(), FFStringUtil.getErrorInfo(e));
		}
		return false;
	}

	public FFEventListener itemClick(String itemClickMethod) {
		this.itemClickMethod = itemClickMethod;
		return this;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		invokeItemClickMethod(handler, itemClickMethod, arg0, arg1, arg2, arg3);
	}

	/**
	 * 反射onItemClick方法
	 * @param handler 反射方法的对象、承载者
	 * @param methodName 要反射的方法的名称
	 * @param params 反射的参数
	 * @return
	 */
	private static void invokeItemClickMethod(Object handler,
			String methodName, Object... params) {
		if (handler == null)
			return;
		Method method = null;
		try {
			method = handler.getClass().getDeclaredMethod(methodName,
					AdapterView.class, View.class, int.class, long.class);
			if (method != null) {
				method.invoke(handler, params);
			} else {
				throw new FFViewException("no such method");
			}
		} catch (Exception e) {
			FFLogger.e(FFEventListener.class.getName(), FFStringUtil.getErrorInfo(e));
		}
		return;
	}

	public FFEventListener itemLongClick(String itemLongClickMethod) {
		this.itemLongClickMethod = itemLongClickMethod;
		return this;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		return invokeItemLongClickMethod(handler, itemLongClickMethod, arg0,
				arg1, arg2, arg3);
	}

	/**
	 * 反射onItemLongClick方法
	 * @param handler 反射方法的对象、承载者
	 * @param methodName 要反射的方法的名称
	 * @param params 反射的参数
	 * @return
	 */
	private static boolean invokeItemLongClickMethod(Object handler,
			String itemLongClickMethod, Object... params) {
		if (handler == null)
			return false;
		Method method = null;
		try {
			method = handler.getClass().getDeclaredMethod(itemLongClickMethod,
					AdapterView.class, View.class, int.class, long.class);
			if (method != null) {
				Object object = method.invoke(handler, params);
				return object == null ? false : Boolean.valueOf(object
						.toString());
			} else {
				throw new FFViewException("no such method");
			}
		} catch (Exception e) {
			FFLogger.e(FFEventListener.class.getName(), FFStringUtil.getErrorInfo(e));
		}
		return false;
	}

	public FFEventListener itemSelect(String itemSelectMethod) {
		this.itemSelectMethod = itemSelectMethod;
		return this;
	}

	public FFEventListener noSelect(String itemNoSelectMethod) {
		this.itemNoSelectMethod = itemNoSelectMethod;
		return this;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		invokeItemSelectkMethod(handler, itemSelectMethod, arg0, arg1, arg2,
				arg3);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		invokeItemNoSelectMethod(handler, itemNoSelectMethod, arg0);
	}

	/**
	 * 反射onItemSelectClick方法
	 * @param handler 反射方法的对象、承载者
	 * @param methodName 要反射的方法的名称
	 * @param params 反射的参数
	 * @return
	 */
	private static void invokeItemSelectkMethod(Object handler,
			String itemSelectMethod, Object... params) {
		if (handler == null)
			return;
		Method method = null;
		try {
			method = handler.getClass().getDeclaredMethod(itemSelectMethod,
					AdapterView.class, View.class, int.class, long.class);
			if (method != null) {
				method.invoke(handler, params);
			} else {
				throw new FFViewException("no such method");
			}
		} catch (Exception e) {
			FFLogger.e(FFEventListener.class.getName(), FFStringUtil.getErrorInfo(e));
		}
	}

	/**
	 * 反射onItemNoSelectClick方法
	 * @param handler 反射方法的对象、承载者
	 * @param methodName 要反射的方法的名称
	 * @param params 反射的参数
	 * @return
	 */
	private static void invokeItemNoSelectMethod(Object handler,
			String itemNoSelectMethod, Object... params) {
		if (handler == null)
			return;
		Method method = null;
		try {
			method = handler.getClass().getDeclaredMethod(itemNoSelectMethod,
					AdapterView.class);
			if (method != null) {
				method.invoke(handler, params);
			} else {
				throw new FFViewException("no such method");
			}
		} catch (Exception e) {
			FFLogger.e(FFEventListener.class.getName(), FFStringUtil.getErrorInfo(e));
		}
	}

}
