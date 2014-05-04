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
package com.frand.easyandroid.command;

public class FFActivityCommand extends FFCommand {

	public final static String FFACTIVITYCOMMAND = "FFActivityCommand";

	@Override
	protected void executeCommand() {
		FFRequest request = getRequest();
		FFResponse response = new FFResponse(request.getRequestKey(),
				request.isDestroyBefore());
		setResponse(response);
		notifyListener(true);
	}

	protected void notifyListener(boolean success) {
		FFIResponseListener responseListener = getResponseListener();
		if (responseListener != null) {
			sendMessage(command_success);
		}
	}
}
