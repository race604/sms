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
 * File     : DeleteScheduleSMSResponseHandler.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-6-26
 * License  : Apache License 2.0 
 */
package com.race604.fetion.client.response;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.race604.fetion.client.FetionContext;
import com.race604.fetion.client.XMLHelper;
import com.race604.fetion.client.dialog.Dialog;
import com.race604.fetion.data.FetionException;
import com.race604.fetion.data.FetionStore;
import com.race604.fetion.data.ScheduleSMS;
import com.race604.fetion.event.ActionEvent;
import com.race604.fetion.event.action.ActionEventListener;
import com.race604.fetion.sipc.SipcResponse;

/**
 *
 * 删除定时短信的回复处理器
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class DeleteScheduleSMSResponseHandler extends AbstractResponseHandler
{

	/**
	 * @param context
	 * @param dialog
	 * @param listener
	 */
	public DeleteScheduleSMSResponseHandler(FetionContext context,
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
		List delScList = XMLHelper.findAll(root, "/results/schedule-sms-list/*schedule-sms");
		Iterator it = delScList.iterator();
		FetionStore store = this.context.getFetionStore();
		while(it.hasNext()){
			Element e = (Element) it.next();
			int scId = Integer.parseInt(e.getAttributeValue("id"));
			ScheduleSMS sc = store.getScheduleSMS(scId);
			if(sc!=null){
				store.deleteScheduleSMS(sc);
			}
		}
		
		Element sclist = XMLHelper.find(root, "/results/schedule-sms-list");
		if(sclist!=null){
			int ver = Integer.parseInt(sclist.getAttributeValue("version"));
			this.context.getFetionStore().getStoreVersion().setScheduleSMSVersion(ver);
		}
		
		return super.doActionOK(response);
	}
}
