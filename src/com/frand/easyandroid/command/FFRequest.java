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

import android.os.Bundle;

public class FFRequest extends FFBaseEntity {
	private static final long serialVersionUID = 444834403356593608L;
	private String requestKey;
	private Bundle requestBundle;
	private int inAnim = 0;
	private int outAnim = 0;
	private boolean destroyBefore = false;

	public FFRequest() {
	}
	
	public FFRequest(boolean destroyBefore) {
		this.destroyBefore = destroyBefore;
	}

	public FFRequest(String requestKey) {
		this.requestKey = requestKey;
	}
	
	public FFRequest(String requestKey, boolean detroyBefore) {
		this.requestKey = requestKey;
		this.destroyBefore = detroyBefore;
	}

	public String getRequestKey() {
		return requestKey;
	}

	public void setRequestKey(String requestKey) {
		this.requestKey = requestKey;
	}

	public Bundle getRequestBundle() {
		return requestBundle;
	}

	public void setRequestBundle(Bundle requestBundle) {
		this.requestBundle = requestBundle;
	}

	public boolean isDestroyBefore() {
		return destroyBefore;
	}

	public void setDestroyBefore(boolean destroyBefore) {
		this.destroyBefore = destroyBefore;
	}

	public int getInAnim() {
		return inAnim;
	}

	public void setInAnim(int inAnim) {
		this.inAnim = inAnim;
	}

	public int getOutAnim() {
		return outAnim;
	}

	public void setOutAnim(int outAnim) {
		this.outAnim = outAnim;
	}
	
}
