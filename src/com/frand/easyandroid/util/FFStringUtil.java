/*
 * Copyright (C) 2014-4-17 frandfeng
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
package com.frand.easyandroid.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author frandfeng
 * @time 2014-4-17 下午4:07:31 class description
 */
public class FFStringUtil {

	/**
	 * 判断是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		boolean isMatches = false;
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			isMatches = true;
		}
		return isMatches;
	}
	
	/** 
     * 手机号验证 
     * @param  str 
     * @return 验证通过返回true 
     */  
    public static boolean isMobile(String str) {   
        Pattern p = null;  
        Matcher m = null;  
        boolean b = false;   
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号  
        m = p.matcher(str);  
        b = m.matches();   
        return b;  
    }
    
    /**
     * 判断是否符合email格式 
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
    	String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    	Pattern p = Pattern.compile(str);
    	Matcher m = p.matcher(email);
    	return m.matches();
    }
    
    /**
     * 将传入的string裁剪前几个字符
     * @param string 传入的string
     * @param trim 裁剪字符的数量
     * @return
     */
	public static String trim(String string, int trim) {
		return string.length()>trim?string.substring(0, trim):string;
	}
	
	/** 
     * 获取错误的信息  
     * @param arg1 
     * @return 
     */  
	public static String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error= writer.toString();
        return error;
    }
}
