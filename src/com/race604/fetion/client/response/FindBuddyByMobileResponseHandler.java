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
 * File     : FindBuddyByMobileResponseHandler.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-4-15
 * License  : Apache License 2.0 
 */
package com.race604.fetion.client.response;

import org.jdom.Element;

import com.race604.fetion.client.FetionContext;
import com.race604.fetion.client.XMLHelper;
import com.race604.fetion.client.dialog.Dialog;
import com.race604.fetion.data.Buddy;
import com.race604.fetion.data.FetionException;
import com.race604.fetion.event.ActionEvent;
import com.race604.fetion.event.action.ActionEventListener;
import com.race604.fetion.event.action.FailureEvent;
import com.race604.fetion.event.action.FailureType;
import com.race604.fetion.event.action.FindBuddySuccessEvent;
import com.race604.fetion.sipc.SipcResponse;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 */
public class FindBuddyByMobileResponseHandler extends AbstractResponseHandler
{
	/**
     * @param context
     * @param dialog
     * @param listener
     */
    public FindBuddyByMobileResponseHandler(FetionContext context,
            Dialog dialog, ActionEventListener listener)
    {
	    super(context, dialog, listener);
    }



	/* (non-Javadoc)
	 * @see net.solosky.maplefetion.client.response.AbstractResponseHandler#doActionOK(net.solosky.maplefetion.sipc.SipcResponse)
	 */
	@Override
	protected ActionEvent doActionOK(SipcResponse response)
			throws FetionException
	{
		Element root = XMLHelper.build(response.getBody().toSendString());
		Element personal = XMLHelper.find(root, "/results/contact");
		if(personal!=null) {
			int userId = Integer.parseInt(personal.getAttributeValue("user-id"));
			Buddy buddy = this.context.getFetionStore().getBuddyByUserId(userId);
			if(buddy!=null) {
				return new FindBuddySuccessEvent(buddy);				//找到该用户并且是好友，操作正确完成
			}else {
				return new FailureEvent(FailureType.BUDDY_NOT_FOUND);	//找到该用户但不是好友
			}
		}else{
			return new FailureEvent(FailureType.SIPC_FAIL);				//服务器没有返回数据
		}
	}
    
    

}
