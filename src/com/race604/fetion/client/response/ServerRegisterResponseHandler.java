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
 * File     : ServerRegisterResponseHandler.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-3-14
 * License  : Apache License 2.0 
 */
package com.race604.fetion.client.response;

import com.race604.fetion.client.FetionContext;
import com.race604.fetion.client.dialog.Dialog;
import com.race604.fetion.data.FetionException;
import com.race604.fetion.event.ActionEvent;
import com.race604.fetion.event.action.ActionEventListener;
import com.race604.fetion.event.action.SuccessEvent;
import com.race604.fetion.sipc.SipcHeader;
import com.race604.fetion.sipc.SipcResponse;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 */
public class ServerRegisterResponseHandler extends AbstractResponseHandler
{

	/**
     * @param client
     * @param dialog
     * @param listener
     */
    public ServerRegisterResponseHandler(FetionContext client, Dialog dialog,
            ActionEventListener listener)
    {
	    super(client, dialog, listener);
    }


	/* (non-Javadoc)
	 * @see net.solosky.maplefetion.client.response.AbstractResponseHandler#doNotAuthorized(net.solosky.maplefetion.sipc.SipcResponse)
	 */
	@Override
	protected ActionEvent doNotAuthorized(SipcResponse response)
			throws FetionException
	{
		SipcHeader header = response.getHeader(SipcHeader.WWWAUTHENTICATE);
		this.dialog.getSession().setAttribute(SipcHeader.WWWAUTHENTICATE, header);
		return new SuccessEvent();
		
	}
}
