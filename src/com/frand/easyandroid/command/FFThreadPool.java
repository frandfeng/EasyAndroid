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

import com.frand.easyandroid.log.FFLogger;

public class FFThreadPool {
	// 线程的最大数量
	private static final int MAX_THREADS_COUNT = 2;
	private FFCommandThread threads[] = null;
	private boolean started = false;
	private static FFThreadPool instance;

	private FFThreadPool() {

	}

	public static FFThreadPool getInstance() {
		if (instance == null) {
			instance = new FFThreadPool();
		}
		return instance;
	}

	public void start() {
		if (!started) {
			FFLogger.i(FFThreadPool.this, "线程池开始运行！");
			int threadCount = MAX_THREADS_COUNT;

			threads = new FFCommandThread[threadCount];
			for (int threadId = 0; threadId < threadCount; threadId++) {
				threads[threadId] = new FFCommandThread(threadId);
				threads[threadId].start();
			}
			started = true;
			FFLogger.i(FFThreadPool.this, "线程池运行完成！");
		}
	}

	public void shutdown() {
		FFLogger.i(FFThreadPool.this, "关闭所有线程！");
		if (started) {
			for (FFCommandThread thread : threads) {
				thread.stop();
			}
			threads = null;
			started = false;
		}
		FFLogger.i(FFThreadPool.this, "关闭完所有线程！");
	}
}
