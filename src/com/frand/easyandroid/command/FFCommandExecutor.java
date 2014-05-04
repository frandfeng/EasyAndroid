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

import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.frand.easyandroid.exception.FFNoCommandException;
import com.frand.easyandroid.util.FFLogger;

public class FFCommandExecutor {
	private final HashMap<String, Class<? extends FFICommand>> commands = new HashMap<String, Class<? extends FFICommand>>();

	private static final FFCommandExecutor instance = new FFCommandExecutor();
	private boolean initialized = false;

	public FFCommandExecutor() {
		ensureInitialized();
	}

	public static FFCommandExecutor getInstance() {
		return instance;
	}

	public void ensureInitialized() {
		if (!initialized) {
			initialized = true;
			FFLogger.i(FFCommandExecutor.this, "CommandExecutor初始化");
			FFCommandQueueManager.getInstance().initialize();
			FFLogger.i(FFCommandExecutor.this, "CommandExecutor初始化完成");
		}
	}

	/**
	 * 所有命令终止或标记为结束
	 */
	public void terminateAll() {

	}

	/**
	 * 命令入列
	 * 
	 * @param commandKey
	 *            命令ID
	 * @param request
	 *            提交的参数
	 * @param listener
	 *            响应监听器
	 * @throws FFNoCommandException
	 */
	public void enqueueCommand(String commandKey, FFRequest request,
			FFIResponseListener listener) throws FFNoCommandException {
		final FFICommand cmd = getCommand(commandKey);
		enqueueCommand(cmd, request, listener);
	}

	public void enqueueCommand(FFICommand command, FFRequest request,
			FFIResponseListener listener) throws FFNoCommandException {
		if (command != null) {
			command.setRequest(request);
			command.setResponseListener(listener);
			FFCommandQueueManager.getInstance().enqueue(command);
		}
	}

	public void enqueueCommand(FFICommand command, FFRequest request)
			throws FFNoCommandException {
		enqueueCommand(command, null, null);
	}

	public void enqueueCommand(FFICommand command)
			throws FFNoCommandException {
		enqueueCommand(command, null);
	}

	private FFICommand getCommand(String commandKey)
			throws FFNoCommandException {
		FFICommand rv = null;
		if (commands.containsKey(commandKey)) {
			Class<? extends FFICommand> cmd = commands.get(commandKey);
			FFLogger.i(this, "commandkey=" + cmd.getName());
			if (cmd != null) {
				int modifiers = cmd.getModifiers();
				if ((modifiers & Modifier.ABSTRACT) == 0
						&& (modifiers & Modifier.INTERFACE) == 0) {
					try {
						rv = cmd.newInstance();
					} catch (Exception e) {
						e.printStackTrace();
						throw new FFNoCommandException("没发现"+commandKey+"命令");
					}
				} else {
					throw new FFNoCommandException("没发现"+commandKey+"命令");
				}
			}
		}
		return rv;
	}

	public void registerCommand(String commandKey,
			Class<? extends FFICommand> command) {
		if (command != null) {
			commands.put(commandKey, command);
		}
	}

	public void unregisterCommand(String commandKey) {
		commands.remove(commandKey);
	}
}
