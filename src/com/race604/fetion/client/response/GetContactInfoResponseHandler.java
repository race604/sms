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
 * Package  : net.solosky.maplefetion.protocol.response
 * File     : GetContectInfoResponseHandler.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2009-11-30
 * License  : Apache License 2.0 
 */
package com.race604.fetion.client.response;

import org.jdom.Element;

import com.race604.fetion.client.BeanHelper;
import com.race604.fetion.client.FetionContext;
import com.race604.fetion.client.XMLHelper;
import com.race604.fetion.client.dialog.Dialog;
import com.race604.fetion.data.Buddy;
import com.race604.fetion.data.BuddyExtend;
import com.race604.fetion.data.FetionException;
import com.race604.fetion.event.ActionEvent;
import com.race604.fetion.event.action.ActionEventListener;
import com.race604.fetion.sipc.SipcResponse;

/**
 * 
 * 获取一位好友详细信息的回调函数
 * 
 * @author solosky <solosky772@qq.com>
 */
public class GetContactInfoResponseHandler extends AbstractResponseHandler
{
	/**
	 * 好友对象
	 */
	private Buddy buddy;
	
	/**
     * @param context
     * @param dialog
     * @param listener
     */
    public GetContactInfoResponseHandler(FetionContext context, Dialog dialog, 
    		Buddy buddy, ActionEventListener listener)
    {
	    super(context, dialog, listener);
	    this.buddy = buddy;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.solosky.maplefetion.client.ResponseHandler#handle(net.solosky.maplefetion
	 * .sipc.SipcResponse)
	 */
	@Override
	public ActionEvent doActionOK(SipcResponse response) throws FetionException
	{
		
		Element root = XMLHelper.build(response.getBody().toSendString());
		Element contact = XMLHelper.find(root, "/results/contact");
		
		BeanHelper.toBean(Buddy.class, buddy, contact);
		
		BuddyExtend extend = buddy.getExtend();
		if(extend==null) {
			extend = new BuddyExtend();
			buddy.setExtend(extend);
		}
		BeanHelper.toBean(BuddyExtend.class, extend, contact);
		context.getFetionStore().flushBuddy(buddy);
		
		return super.doActionOK(response);
	}
}
