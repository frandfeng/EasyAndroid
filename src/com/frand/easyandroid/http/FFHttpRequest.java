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

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import com.frand.easyandroid.util.FFLogger;

public class FFHttpRequest implements Runnable {
	private final AbstractHttpClient client;
	private final HttpContext context;
	private final HttpUriRequest request;
	private final FFHttpRespHandler responseHandler;
	private final int reqTag;
	private final String reqUrl;
	private int executionCount;
	
	public enum ReqType {
		PUT,
		GET,
		POST,
		DOWNLOAD,
		DELETE
	}

	public FFHttpRequest(AbstractHttpClient client, HttpContext context,
			HttpUriRequest request, FFHttpRespHandler responseHandler,
			int reqTag, String reqUrl) {
		this.client = client;
		this.context = context;
		this.request = request;
		this.responseHandler = responseHandler;
		this.reqTag = reqTag;
		this.reqUrl = reqUrl;
		if(responseHandler instanceof FFFileRespHandler) {
			FFFileRespHandler fileResponseHandler = (FFFileRespHandler) responseHandler;
			File tempFile = fileResponseHandler.getTempFile();
			if(tempFile.exists()) {
				long previousFileSize = tempFile.length();
				fileResponseHandler.setPreviousFileSize(previousFileSize);
				this.request.setHeader("RANGE", "bytes="+previousFileSize+"-");
				FFLogger.i(this, "set previous file size: "+previousFileSize);
			}
		}
	}

	@Override
	public void run() {
		try {
			if (responseHandler != null) {
				responseHandler.sendStartMsg(reqTag, reqUrl);
			}
			makeRequestWithRetries();
			if (responseHandler != null) {
				responseHandler.sendFinishMsg(reqTag, reqUrl);
			}
		} catch (IOException e) {
			if (responseHandler != null) {
				responseHandler.sendFailureMsg(e, reqTag, reqUrl);
				responseHandler.sendFinishMsg(reqTag, reqUrl);
			}
		}
	}

	private void makeRequest() throws IOException {
		if (!Thread.currentThread().isInterrupted()) {
			try {
				HttpResponse response = client.execute(request, context);
				if (!Thread.currentThread().isInterrupted()) {
					if (responseHandler != null) {
						responseHandler.sendRespMsg(response, reqTag, reqUrl);
					}
				} else {
				}
			} catch (IOException e) {
				if (!Thread.currentThread().isInterrupted()) {
					throw e;
				}
			}
		}
	}

	private void makeRequestWithRetries() throws ConnectException {
		boolean retry = true;
		IOException cause = null;
		HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
		while (retry) {
			try {
				makeRequest();
				return;
			} catch (UnknownHostException e) {
				if (responseHandler != null) {
					responseHandler.sendFailureMsg(e, reqTag, reqUrl);
				}
				return;
			} catch (SocketException e) {
				if (responseHandler != null) {
					responseHandler.sendFailureMsg(e, reqTag, reqUrl);
				}
				return;
			} catch (SocketTimeoutException e) {
				if (responseHandler != null) {
					responseHandler.sendFailureMsg(e, reqTag, reqUrl);
				}
				return;
			} catch (IOException e) {
				cause = e;
				retry = retryHandler.retryRequest(cause, ++executionCount,
						context);
			} catch (NullPointerException e) {
				cause = new IOException("NPE in HttpClient" + e.getMessage());
				retry = retryHandler.retryRequest(cause, ++executionCount, context);
			}
		}
		ConnectException ex = new ConnectException();
		ex.initCause(cause);
		throw ex;
	}
}
