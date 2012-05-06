 /*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 /**
 * Project  : MapleFetion
 * Package  : net.solosky.maplefetion.util
 * File     : XMLHelper.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2009-11-23
 * License  : Apache License 2.0 
 */
package com.race604.fetion.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


import android.util.Log;

/**
 *
 * XML工具类
 *
 * @author solosky <solosky772@qq.com> 
 */
public class XMLHelper
{
	
	private static String TAG = "XMLHelper";
	/**
	 * 日志记录
	 */
	/**
	 * 从流中构建文档对象
	 * @return 构造成功返回根元素
	 * @throws JDOMException 
	 * @throws IOException 
	 */
	public static Element build(InputStream in) throws JDOMException, IOException
	{
		
		try {
			SAXBuilder builder = new SAXBuilder();
	        Document doc = builder.build(in);
	        return doc.getRootElement();
        } catch (JDOMException e) {
        	Log.w(TAG,"Cannot parse XML Stream:"+e);
        	throw e;
        } catch (IOException e) {
        	Log.w(TAG,"IOException occured when parsing XML Stream:"+e);
        	throw e;
        }
	}
	
	/**
	 * 从字符串中创建文档对象
	 * @param xml
	 * @return
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static Element build(String xml) throws JDOMException, IOException
	{
		try {
	        return build(new ByteArrayInputStream(xml.getBytes("utf8")));
        } catch (UnsupportedEncodingException e) {
	       return null;
        }
	}
	
	/**
	 * 以给定的路径查找列表元素
	 *   contacts/*contact 表示查找contacts下面的所有的名字为contact的节点
	 *   /results/contacts/*contact 和上面一样的结果 如果以/开始表示从根节点查询起
	 * @param tree
	 * @param path
	 * @return
	 */
	public static List findAll(Element tree, String path)
	{
		if(tree==null || path==null)	
			return new ArrayList();	//防止产生Null异常
		if(path.charAt(0)=='/')
			path = path.substring( path.indexOf('/', 1)+1);
		String[] paths = path.split("/");
		for(int i=0; i<paths.length-1; i++) {
			tree = tree.getChild(paths[i]);
			if(tree==null)
				return new ArrayList();	//防止产生Null异常
		}
		List els = null;
		String last = paths[paths.length-1];
		if(last.length()==1 && last.charAt(0)=='*') {
			els =  tree.getChildren();
		}else {
			els =  tree.getChildren(last.substring(1));
		}
		
		return els;
	}
	
	/**
	 * 以给定的路径查找特定的元素
	 *   contact/basic 表示查找contact下面子节点名字为basic的节点
	 *   /results/contact/basic 和上面一样的结果 如果以/开始表示从根节点查询起
	 * @param tree		
	 * @param path
	 * @return
	 */
	public static Element find(Element tree, String path)
	{
		if(tree==null || path==null)	
			return null;
		if(path.charAt(0)=='/')
			path = path.substring( path.indexOf('/', 1)+1);
		String[] paths = path.split("/");
		for(int i=0; i<paths.length; i++) {
			tree = tree.getChild(paths[i]);
			if(tree==null)
				return null;
		}
		return tree;
	}
}