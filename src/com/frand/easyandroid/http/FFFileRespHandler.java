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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;

import com.frand.easyandroid.exception.FFFileExistException;
import com.frand.easyandroid.log.FFLogger;

public class FFFileRespHandler extends FFHttpRespHandler {
	
	public final static int TIME_OUT = 30000;
	private final static int BUFFER_SIZE = 1024 * 8;

	private static final String TEMP_SUFFIX = ".download";
	/*最终要保存的文件*/
	private File file;
	/*临时生成的文件*/
	private File tempFile;
	/*基本的文件路径，由此与fileName合成为最终的文件*/
	private File baseDirFile;
	/*对文件进行操作的输出流*/
	private RandomAccessFile outputStream;
	/*已经下载到的文件大小*/
	private long downloadSize;
	/*以前下载到的文件大小，downloadSize=previousFileSize+此次复制到的大小*/
	private long previousFileSize;
	/*文件的总大小*/
	private long totalSize;
	private long networkSpeed;
	private long previousTime;
	private long totalTime;
	private boolean interrupt = false;
	private boolean timerInterrupt = false;
	private Timer timer = new Timer();
	private static final int TIMERSLEEPTIME = 100;

	public FFFileRespHandler(String rootFile, String fileName) {
		super();
		this.baseDirFile = new File(rootFile);
		this.file = new File(rootFile, fileName);
		this.tempFile = new File(rootFile, fileName + TEMP_SUFFIX);
		init();
	}

	public FFFileRespHandler(String filePath) {
		super();
		this.file = new File(filePath);
		this.baseDirFile = new File(this.file.getParent());
		this.tempFile = new File(filePath + TEMP_SUFFIX);
		init();
	}

	private void init() {
		if (!this.baseDirFile.exists()) {
			this.baseDirFile.mkdirs();
		}
		FFLogger.i(this, "init completely baseDirFile="+baseDirFile.getAbsolutePath()
				+", file="+file.getAbsolutePath()+", tempFile="+tempFile.getAbsolutePath());
	}

	@Override
	protected void onStart(int reqTag, String reqUrl) {
	}

	@Override
	protected void onFailure(Throwable error, int reqTag, String reqUrl) {
	}

	@Override
	protected void onSuccess(String resp, int reqTag, String reqUrl) {
	}

	@Override
	protected void onFinish(int reqTag, String reqUrl) {
	}
	
	public boolean isInterrupt() {
		return interrupt;
	}

	public void setInterrupt(boolean interrupt) {
		this.interrupt = interrupt;
	}

	public long getDownloadSize() {
		return downloadSize;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public double getDownloadSpeed() {
		return this.networkSpeed;
	}

	public void setPreviousFileSize(long previousFileSize) {
		this.previousFileSize = previousFileSize;
	}

	public File getFile() {
		return file;
	}

	public long getTotalTime() {
		return this.totalTime;
	}

	public File getTempFile() {
		return tempFile;
	}

	private void stopTimer() {
		timerInterrupt = true;
		timer.cancel();
	}
	
	private void stopCopy() {
		interrupt = true;
	}
	
	@Override
	protected void sendRespMsg(HttpResponse response, int reqTag, String reqUrl) {
		FFLogger.i(this, "start to send response message");
		Throwable error = null;
		long result = -1;
		try {
			// 获取文件的大小
			long contentLength = response.getEntity().getContentLength();
			// -1的解决方式ContentLength 在手机访问的时候出现了问题，返回为-1
			if (contentLength == -1) {
				contentLength = response.getEntity().getContent().available();
			}
			// 获取已下载到的文件的大小
			previousFileSize = 0;
			File tempFile = this.getTempFile();
			if(tempFile.exists()) {
				previousFileSize = this.getTempFile().length();
			}
			totalSize = contentLength + previousFileSize;
			FFLogger.i(this, "totalSize: " + totalSize + ",contentLength: "+contentLength+",previousFileSize: "+previousFileSize);
			if (file.exists() && totalSize == file.length()) {
				FFLogger.i(this, "Output file already exists. Skipping download.");
				throw new FFFileExistException("Output file already exists. Skipping download.");
			} else if (tempFile.exists()) {
				previousFileSize = tempFile.length();
			}
			outputStream = new ProgressReportingRandomAccessFile(tempFile, "rw");
			InputStream input = response.getEntity().getContent();
			startTimer(reqTag, reqUrl);
			int bytesCopied = copy(input, outputStream);
			if ((previousFileSize + bytesCopied) != totalSize && totalSize != -1 && !interrupt) {
				throw new IOException("Download incomplete: " + bytesCopied + " != " + totalSize);
			} else if (interrupt) {
				throw new Exception("download has been paused");
			}
			result = bytesCopied;
		} catch (FileNotFoundException e) {
			error = e;
		} catch (FFFileExistException e) {
			error = e;
		} catch (IllegalStateException e) {
			error = e;
		} catch (IOException e) {
			error = e;
		} catch (Exception e) {
			error = e;
		}
		// 停止打印
		stopTimer();
		// 保证timer被关闭
		try {
			Thread.sleep(TIMERSLEEPTIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (result == -1 || interrupt || error != null) {
			if (error != null) {
				if (error instanceof FFFileExistException) {
					sendSuccMsg("下载成功！", reqTag, reqUrl);
				} else {
					sendFailureMsg(error, reqTag, reqUrl);
				}
			}
			return;
		}
		tempFile.renameTo(file);
		sendSuccMsg("下载成功！", reqTag, reqUrl);
	}

	private void startTimer(final int reqTag, final String reqUrl) {
		timerInterrupt = false;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				while (!timerInterrupt) {
					sendProgressMsg(totalSize, getDownloadSize(), networkSpeed, reqTag, reqUrl);
					try {
						Thread.sleep(TIMERSLEEPTIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, 0, 1000);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
			}
		}).start();
	}
	
	public void PauseDownload() {
		stopCopy();
		stopTimer();
	}

	public int copy(InputStream input, RandomAccessFile out) throws IOException {
		interrupt = false;
		if (input == null || out == null) {
			return -1;
		}
		byte[] buffer = new byte[BUFFER_SIZE];
		BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
		int count = 0, n = 0;
		long errorBlockTimePreviousTime = -1, expireTime = 0;
		try {
			out.seek(out.length());
			previousTime = System.currentTimeMillis();
			while (!interrupt) {
				n = in.read(buffer, 0, BUFFER_SIZE);
				if (n == -1) {
					break;
				}
				out.write(buffer, 0, n);
				count += n;
				if (networkSpeed == 0) {
					if (errorBlockTimePreviousTime > 0) {
						expireTime = System.currentTimeMillis()-errorBlockTimePreviousTime;
						if (expireTime > TIME_OUT) {
							throw new ConnectTimeoutException("connection time out.");
						}
					} else {
						errorBlockTimePreviousTime = System.currentTimeMillis();
					}
				} else {
					expireTime = 0;
					errorBlockTimePreviousTime = -1;
				}
			}
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
	private class ProgressReportingRandomAccessFile extends RandomAccessFile {
		private int progress = 0;

		public ProgressReportingRandomAccessFile(File file, String mode)
				throws FileNotFoundException {
			super(file, mode);
		}

		@Override
		public void write(byte[] buffer, int offset, int count) throws IOException {
			super.write(buffer, offset, count);
			progress += count;
			totalTime = System.currentTimeMillis() - previousTime;
			downloadSize = progress + previousFileSize;
			if (totalTime > 0) {
				networkSpeed = (long) ((progress / totalTime) / 1.024);
			}
		}
	}
}