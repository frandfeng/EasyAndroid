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
package com.frand.easyandroid.log;

public interface FFILogger {
	
	public void v(String tag, String message);
	public void d(String tag, String message);
	public void i(String tag, String message);
	public void w(String tag, String message);
	public void e(String tag, String message);
	public void open();
	public void close();
	public void println(int priority, String tag, String message);
	
}
