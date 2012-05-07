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
 * Project  : MapleFetion2
 * Package  : net.solosky.maplefetion.client
 * File     : HttpApplication.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-1-15
 * License  : Apache License 2.0 
 */
package com.race604.fetion.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.race604.fetion.data.Buddy;
import com.race604.fetion.data.FetionConfig;
import com.race604.fetion.data.LocaleSetting;
import com.race604.fetion.data.User;
import com.race604.fetion.data.VerifyImage;
import com.race604.fetion.data.XMLHelper;

import android.graphics.Bitmap;
import android.util.Base64;

/**
 * 
 * 飞信的HTTP应用
 * 
 * 飞信的很多地方都是使用了HTTP这样的模式 比如设置头像，设置自定义头像等
 * 
 * @author solosky <solosky772@qq.com>
 */
public class HttpApplication {
	/**
	 * 
	 * 设置自定义头像
	 * 
	 * @param user
	 *            用户编号
	 * @param setting
	 *            自适应配置
	 * @param image
	 *            图片流
	 * @return 返回头像的crc校验码
	 * @throws IOException
	 */
	public static long setPortrait(FetionContext context, Bitmap image)
			throws IOException {
		// TODO 修改这里，可以用来设置头像
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// ImageIO.write(image, "JPG", out);
		// String setPortraitUrl =
		// context.getLocaleSetting().getNodeText("/config/http-applications/set-portrait");
		// if(setPortraitUrl==null) setPortraitUrl =
		// FetionConfig.getString("server.set-portrait");
		// byte [] bytes = HttpHelper.doFetchData(setPortraitUrl,
		// "POST", "image/jpeg",
		// findCredential(setPortraitUrl, context),
		// new ByteArrayInputStream(out.toByteArray()));
		// return Long.parseLong(new String(bytes));
		return 0;
	}

	/**
	 * 获取头像
	 * 
	 * @param context
	 * @param buddy
	 * @param size
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getPortrait(FetionContext context, Buddy buddy,
			int size) throws IOException {
		// TODO 设置头像
		// String getPortraitUrl =
		// context.getLocaleSetting().getNodeText("/config/http-applications/get-portrait");
		// if(getPortraitUrl==null) getPortraitUrl =
		// FetionConfig.getString("server.get-portrait");
		// getPortraitUrl = StringHelper.format("{0}?Uri={1}&Size={2}&c={3}",
		// getPortraitUrl,
		// StringHelper.urlEncode(buddy.getUri()),
		// Integer.toString(size),
		// StringHelper.urlEncode(context.getFetionUser().getSsiCredential().trim()));
		// byte[] bytes = HttpHelper.doFetchData(getPortraitUrl, "GET", null,
		// findCredential(getPortraitUrl, context), null);
		// return ImageIO.read(new ByteArrayInputStream(bytes));
		return null;
	}

	/**
	 * 获取验证码图片
	 * 
	 * @param user
	 *            用户对象
	 * @param setting
	 *            配置
	 * @param alg
	 *            算法
	 * @param type
	 *            类型
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 * @throws ParseException
	 */
	public static VerifyImage fetchVerifyImage(User user,
			LocaleSetting setting, String alg, String type) throws IOException,
			JDOMException {
		String picurl = setting.getNodeText("/config/servers/get-pic-code");
		if (picurl == null)
			picurl = FetionConfig.getString("server.verify-pic-uri");
		picurl += "?algorithm=" + alg;
		URL url = new URL(picurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty("User-Agent", "IIC2.0/PC "
				+ FetionClient.PROTOCOL_VERSION); // 必须要加这个，否则失败 很奇怪
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			Element e = XMLHelper.build(conn.getInputStream());
			Element pic = XMLHelper.find(e, "/results/pic-certificate");
			String pid = pic.getAttributeValue("id");
			String code = pic.getAttributeValue("pic");
			byte[] base64Data = code.getBytes();
			// byte[] bytes = Base64.decodeBase64(base64Data);
			byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
			return new VerifyImage(pid, bytes, alg, type);
		} else {
			throw new IOException("Http response is not OK. code="
					+ conn.getResponseCode());
		}
	}

	/**
	 * 根据域名获取对应的SSIC
	 * 
	 * @param url
	 * @param context
	 * @return
	 */
	private static String findCredential(String url, FetionContext context) {
		String ssic = url.startsWith("https") ? context.getFetionUser()
				.getSsiCredential() : context.getFetionStore()
				.getCredential("fetion.com.cn").getCredential();
		return ssic.trim();
	}
}
