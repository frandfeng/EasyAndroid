/*
 * Copyright (C) 2014-3-27 frandfeng
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
package com.frand.easyandroid.helpers;

import android.content.Context;

import com.frand.easyandroid.http.FFHttpClient;
import com.frand.easyandroid.http.FFHttpRespHandler;
import com.frand.easyandroid.http.FFRequestParams;
import com.frand.easyandroid.http.FFHttpRequest.ReqType;

/** 
 * @author frandfeng
 * @time 2014-3-27 下午5:19:50 
 * class description 
 */
public class BaseHttpHelper {
	
	protected FFHttpClient httpClient = new FFHttpClient();
	protected Context context;
	
	public enum ReqAPI {
		LISENCE,
		ERROR,
		ERRORLOG,
	}
	
	private static final String HTTP_REL_PRIX_FRAND = "http://frand.java.fjjsp01.com/AiDaRen/";
	
	public BaseHttpHelper(Context context) {
		this.context = context;
	}
	
	public void request(ReqAPI reqAPI, ReqType reqType, FFRequestParams params,
			FFHttpRespHandler handler) {
		String url = HTTP_REL_PRIX_FRAND;
		if(reqAPI==ReqAPI.LISENCE) {
			url += "project.do";
		} else if (reqAPI==ReqAPI.ERROR) {
			url += "error.do";
		} else if (reqAPI==ReqAPI.ERRORLOG) {
			url += "errorlog.do";
		}
		httpClient.post(context, reqAPI.ordinal(), url, params, handler);
	}
	
}
