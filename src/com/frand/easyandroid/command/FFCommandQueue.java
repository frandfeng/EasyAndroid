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

import java.util.concurrent.LinkedBlockingQueue;

import com.frand.easyandroid.util.FFLogger;

public class FFCommandQueue {
	private LinkedBlockingQueue<FFICommand> theQueue = new LinkedBlockingQueue<FFICommand>();

	public FFCommandQueue() {
		FFLogger.i(FFCommandQueue.this, "初始化Command队列");
	}

	public void enqueue(FFICommand cmd) {
		FFLogger.i(FFCommandQueue.this, "添加Command到队列");
		theQueue.add(cmd);
	}

	public synchronized FFICommand getNextCommand() {
		FFLogger.i(FFCommandQueue.this, "获取Command");
		FFICommand cmd = null;
		try {
			FFLogger.i(FFCommandQueue.this, "CommandQueue::to-take");
			cmd = theQueue.take();
			FFLogger.i(FFCommandQueue.this, "CommandQueue::taken");
		} catch (InterruptedException e) {
			FFLogger.i(FFCommandQueue.this, "没有获取到Command");
			e.printStackTrace();
		}
		FFLogger.i(FFCommandQueue.this, "返回Command" + cmd);
		return cmd;
	}

	public synchronized void clear() {
		FFLogger.i(FFCommandQueue.this, "清空所有Command");
		theQueue.clear();
	}
}
