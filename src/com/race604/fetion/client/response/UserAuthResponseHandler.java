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
 * Package  : net.solosky.maplefetion.client.response
 * File     : UserAuthResponseHandler.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-3-14
 * License  : Apache License 2.0 
 */
package com.race604.fetion.client.response;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.race604.fetion.client.BeanHelper;
import com.race604.fetion.client.FetionContext;
import com.race604.fetion.client.SystemException;
import com.race604.fetion.client.UriHelper;
import com.race604.fetion.client.XMLHelper;
import com.race604.fetion.client.dialog.Dialog;
import com.race604.fetion.data.Buddy;
import com.race604.fetion.data.Cord;
import com.race604.fetion.data.Credential;
import com.race604.fetion.data.DigestHelper;
import com.race604.fetion.data.FetionException;
import com.race604.fetion.data.FetionStore;
import com.race604.fetion.data.Relation;
import com.race604.fetion.data.StringHelper;
import com.race604.fetion.data.User;
import com.race604.fetion.event.ActionEvent;
import com.race604.fetion.event.action.ActionEventListener;
import com.race604.fetion.event.action.FailureEvent;
import com.race604.fetion.event.action.FailureType;
import com.race604.fetion.sipc.SipcResponse;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 */
public class UserAuthResponseHandler extends AbstractResponseHandler
{

	/**
     * @param client
     * @param dialog
     * @param listener
     */
    public UserAuthResponseHandler(FetionContext client, Dialog dialog,
            ActionEventListener listener)
    {
	    super(client, dialog, listener);
    }

	/* (non-Javadoc)
	 * @see net.solosky.maplefetion.client.response.AbstractResponseHandler#doActionOK(net.solosky.maplefetion.sipc.SipcResponse)
	 */
	@Override
	protected ActionEvent doActionOK(SipcResponse response)
			throws FetionException
	{
	
		Element root = XMLHelper.build(response.getBody().toSendString());
		
		FetionStore store = this.context.getFetionStore();
		
		//解析个人信息，飞信真有意思，这里却不简写，map.xml里面全是简写的，所以这里只能手动注入了。
		Element personal = XMLHelper.find(root, "/results/user-info/personal");
		User user = this.context.getFetionUser();
		user.setEmail(personal.getAttributeValue("register-email"));

		int personalVersion = Integer.parseInt(personal.getAttributeValue("version"));
		Element contactList = XMLHelper.find(root, "/results/user-info/contact-list");
		int contactVersion = Integer.parseInt(contactList.getAttributeValue("version"));
		
		//联系人和个人信息版本信息
		store.getStoreVersion().setPersonalVersion(personalVersion);
		store.getStoreVersion().setContactVersion(contactVersion);
		user.getStoreVersion().setPersonalVersion(personalVersion);
		user.getStoreVersion().setContactVersion(contactVersion);
		
		//个人信息
		BeanHelper.toBean(User.class, user, personal);
		
		
		//一定要对飞信列表加锁，防止其他飞信操作获取到空的数据
		synchronized (store) {

			//解析分组列表
			List list = XMLHelper.findAll(root, "/results/user-info/contact-list/buddy-lists/*buddy-list");
			Iterator it = list.iterator();
			while(it.hasNext()) {
				Element e = (Element) it.next();
				store.addCord(new Cord(Integer.parseInt(e.getAttributeValue("id")), e.getAttributeValue("name")));
			}
			
			//解析好友列表
			list = XMLHelper.findAll(root, "/results/user-info/contact-list/buddies/*b");
			it = list.iterator();
			while(it.hasNext()) {
				Element e = (Element) it.next();
				String uri = e.getAttributeValue("u");
				
				Buddy b = UriHelper.createBuddy(uri);
				b.setUserId(Integer.parseInt(e.getAttributeValue("i")));
				b.setLocalName(e.getAttributeValue("n"));
				b.setUri(e.getAttributeValue("u"));
				b.setCordId(e.getAttributeValue("l"));
				b.setRelation(Relation.valueOf(Integer.parseInt(e.getAttributeValue("r"))));
				
				store.addBuddy(b);
			}
			
			//处理 chat-friend..
	    	//这个chat-friend具体是什么含义我也没搞得太清楚，目前猜测里面的名单可能和用户是陌生人关系
			list = XMLHelper.findAll(root, "/results/user-info/contact-list/chat-friends/*c");
			it = list.iterator();
    		while(it.hasNext()){
    			Element e = (Element) it.next();
    			Buddy b = UriHelper.createBuddy(e.getAttributeValue("u"));
				b.setUserId(Integer.parseInt(e.getAttributeValue("i")));
				b.setUri(e.getAttributeValue("u"));
        		b.setRelation(Relation.STRANGER);
        		store.addBuddy(b);
    		}
    		
    		//处理Blacklist
    		list = XMLHelper.findAll(root, "/results/user-info/contact-list/blacklist/*k");
			it = list.iterator();
    		while(it.hasNext()){
    			Element e = (Element) it.next();
    			String uri = e.getAttributeValue("u");
    			Buddy b = store.getBuddyByUri(uri);
    			if(b!=null) {
    				b.setRelation(Relation.BANNED);
    			}
    		}
    		
    		
    		//处理Crendeticals
    		Element credentialList = XMLHelper.find(root, "/results/credentials");
    		
    		user.setSsiCredential(this.decryptCredential(credentialList.getAttributeValue("kernel")));
    		
    		list = XMLHelper.findAll(root, "/results/credentials/*credential");
    		it = list.iterator();
    		while(it.hasNext()) {
    			Element e = (Element) it.next();
    			String domain = e.getAttributeValue("domain");
    			Credential c = store.getCredential(domain);
    			if(c==null) {
    				c = new Credential(domain, null);
    				store.addCredential(c);
    			}
    			c.setCredential(this.decryptCredential(e.getAttributeValue("c")));
    		}
        }
		
		
		return super.doActionOK(response);
	}
	
    private String decryptCredential(String c) throws FetionException {
    	User user = this.context.getFetionUser();
    	try {
            byte[] encrypted = StringHelper.base64Decode(c);
            byte[] decrypted = DigestHelper.AESDecrypt(encrypted, user.getAesKey(), user.getAesIV());
            return new String(decrypted,"utf8");
        } catch (IOException ex) {
        	throw new SystemException("decrypt credential failed.", ex);
        }
    }
    
	

	@Override
	protected ActionEvent doRequestFailure(SipcResponse response)
			throws FetionException {
		return new FailureEvent(FailureType.REGISTER_FORBIDDEN);
	}

	@Override
	protected ActionEvent doNotAuthorized(SipcResponse response)
			throws FetionException
	{
		return new FailureEvent(FailureType.AUTHORIZATION_FAIL);
	}
    
    
}
