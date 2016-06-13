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

public class FFResponse extends FFBaseEntity {
	private static final long serialVersionUID = 444834403356593608L;
	private String responseKey;
	private Bundle responseBundle;
	private int inAnim = 0;
	private int outAnim = 0;
	private boolean destroyBefore = false;

	public FFResponse() {
	}
	
	public FFResponse(boolean destroyBefore) {
		this.destroyBefore = destroyBefore;
	}

	public FFResponse(String responseKey) {
		this.responseKey = responseKey;
	}

	public FFResponse(String requestKey, boolean detroyBefore, Bundle requestBundle) {
		this.responseKey = requestKey;
		this.destroyBefore = detroyBefore;
		this.responseBundle = requestBundle;
	}
	
	public FFResponse(String requestKey, boolean detroyBefore,
			Bundle requestBundle, int inAnim, int outAnim) {
		this.responseKey = requestKey;
		this.destroyBefore = detroyBefore;
		this.responseBundle = requestBundle;
		this.inAnim = inAnim;
		this.outAnim = outAnim;
	}

	public String getResponseKey() {
		return responseKey;
	}

	public void setResponseKey(String responseKey) {
		this.responseKey = responseKey;
	}

	public Bundle getResponseBundle() {
		return responseBundle;
	}

	public void setResponseBundle(Bundle responseBundle) {
		this.responseBundle = responseBundle;
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
