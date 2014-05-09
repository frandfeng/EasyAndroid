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

public final class FFCommandQueueManager {
	private static FFCommandQueueManager instance;
	private boolean initialized = false;
	private FFThreadPool pool;
	private FFCommandQueue queue;

	private FFCommandQueueManager() {
	}

	public static FFCommandQueueManager getInstance() {
		if (instance == null) {
			instance = new FFCommandQueueManager();
		}
		return instance;
	}

	public void initialize() {
		FFLogger.i(FFCommandQueueManager.this, "准备初始化command队列！");
		if (!initialized) {
			FFLogger.i(FFCommandQueueManager.this, "开始初始化command！");
			queue = new FFCommandQueue();
			pool = FFThreadPool.getInstance();
			FFLogger.i(FFCommandQueueManager.this, "完成初始化command！");
			pool.start();
			initialized = true;
		}
		FFLogger.i(FFCommandQueueManager.this, "初始化完成command！");
	}

	/**
	 * 从队列中获取Command
	 * 
	 * @return TAICommand
	 */
	public FFICommand getNextCommand() {
		FFLogger.i(FFCommandQueueManager.this, "获取Command！");
		FFICommand cmd = queue.getNextCommand();
		FFLogger.i(FFCommandQueueManager.this, "获取Command" + cmd + "完成！");
		return cmd;
	}

	/**
	 * 添加Command到队列中
	 */
	public void enqueue(FFICommand cmd) {
		FFLogger.i(FFCommandQueueManager.this, "添加" + cmd + "开始");
		queue.enqueue(cmd);
		FFLogger.i(FFCommandQueueManager.this, "添加" + cmd + "完成");
	}

	/**
	 * 清除队列
	 */
	public void clear() {
		queue.clear();
	}

	/**
	 * 关闭队列
	 */
	public void shutdown() {
		if (initialized) {
			queue.clear();
			pool.shutdown();
			initialized = false;
		}
	}
}
