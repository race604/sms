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
 * File     : AddBuddyToBlackListResponseHandler.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-7-2
 * License  : Apache License 2.0 
 */
package com.race604.fetion.client.response;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.race604.fetion.client.BeanHelper;
import com.race604.fetion.client.FetionContext;
import com.race604.fetion.client.XMLHelper;
import com.race604.fetion.client.dialog.Dialog;
import com.race604.fetion.data.Buddy;
import com.race604.fetion.data.FetionException;
import com.race604.fetion.data.FetionStore;
import com.race604.fetion.data.Relation;
import com.race604.fetion.event.ActionEvent;
import com.race604.fetion.event.action.ActionEventListener;
import com.race604.fetion.sipc.SipcResponse;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 */
public class AddBuddyToBlackListResponseHandler extends AbstractResponseHandler
{

	/**
     * @param context
     * @param dialog
     * @param listener
     */
    public AddBuddyToBlackListResponseHandler(FetionContext context,
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
    	FetionStore store = this.context.getFetionStore();
    	synchronized (store) {
    		
        	List list = XMLHelper.findAll(root, "/results/contacts/blacklist/*blocked");
        	Iterator it = list.iterator();
        	
    		while(it.hasNext()) {
        		Element e = (Element) it.next();
        		String uri = e.getAttributeValue("uri");
        		Buddy buddy = store.getBuddyByUri(uri);
        		if(buddy!=null) {
        			BeanHelper.setValue(buddy, "relation", Relation.BANNED);
        			context.getFetionStore().flushBuddy(buddy);
        		}
        	}
    		
    		Element el =  XMLHelper.find(root, "/results/contacts");
    		String version = el.getAttributeValue("version");
    		if(version!=null) {
    			store.getStoreVersion().setContactVersion(Integer.parseInt(version));
    		}
        }
    	
	    return super.doActionOK(response);
    }

}
