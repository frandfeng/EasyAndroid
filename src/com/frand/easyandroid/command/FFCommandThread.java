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

public class FFCommandThread implements Runnable {
	private int threadId;
	private Thread thread = null;
	private boolean running = false;
	private boolean stop = false;

	public FFCommandThread(int threadId) {
		FFLogger.i(FFCommandThread.this, "CommandThread::ctor");
		this.threadId = threadId;
		thread = new Thread(this);
	}

	public void run() {
		FFLogger.i(FFCommandThread.this, "CommandThread::run-enter");
		while (!stop) {
			FFLogger.i(FFCommandThread.this, "CommandThread::get-next-command");
			FFICommand cmd = FFCommandQueueManager.getInstance()
					.getNextCommand();
			FFLogger.i(FFCommandThread.this, "CommandThread::to-execute");
			cmd.execute();
			FFLogger.i(FFCommandThread.this, "CommandThread::executed");
		}
		FFLogger.i(FFCommandThread.this, "CommandThread::run-exit");
	}

	public void start() {
		thread.start();
		running = true;
	}

	public void stop() {
		stop = true;
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public int getThreadId() {
		return threadId;
	}
}
