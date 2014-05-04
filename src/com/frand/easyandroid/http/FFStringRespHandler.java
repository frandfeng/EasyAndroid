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
package com.frand.easyandroid.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

public class FFStringRespHandler extends FFHttpRespHandler {

	public void onStart(int reqTag, String url) {
	}
	public void onFailure(Throwable error, int reqTag, String reqUrl) {
	}
	protected void onSuccess(String resp, int reqTag, String reqUrl) {
	}
	public void onFinish(int reqTag, String url) {
	}
	protected void onProgress(int reqTag, String reqUrl) {
	}

	/**
	 * 请求到结果时，根据请求的结果的状态码，内容来判断是否得到对的结果
	 * 如果是对的，将其标志和路径封装成一个对象，以SUCCESS_MESSAGE为key值发送到handler当中去统一处理
	 * 如果是错的，则将请求失败的原因，标志和路径封装成一个对象，以FAILURE_MESSAGE为key值发送到handler当中去统一处理
	 * @param response 请求的结果
	 * @param reqTag 请求的标志
	 * @param reqUrl 请求的路径
	 */
	protected void sendRespMsg(HttpResponse response, int reqTag, String reqUrl) {
		StatusLine status = response.getStatusLine();
		String responseBody = null;
		try {
			HttpEntity entity = null;
			HttpEntity temp = response.getEntity();
			if (temp != null) {
				entity = new BufferedHttpEntity(temp);
				responseBody = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (IOException e) {
			sendFailureMsg(e, reqTag, reqUrl);
		}
		if (status.getStatusCode() >= 300) {
			sendFailureMsg(new HttpResponseException(status.getStatusCode(),
					status.getReasonPhrase()), reqTag, reqUrl);
		} else {
			sendSuccMsg(responseBody, reqTag, reqUrl);
		}
	}
}
