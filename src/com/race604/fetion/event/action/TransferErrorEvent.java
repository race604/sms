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
 * Package  : net.solosky.maplefetion.event.action
 * File     : TransferErrorEvent.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-5-11
 * License  : Apache License 2.0 
 */
package com.race604.fetion.event.action;

import com.race604.fetion.event.ActionEvent;
import com.race604.fetion.event.ActionEventType;

/**
 *
 * 网络异常事件
 *
 * @author solosky <solosky772@qq.com>
 */
public class TransferErrorEvent extends ActionEvent
{

	public TransferErrorEvent()
	{
	}
	
	/* (non-Javadoc)
     * @see net.solosky.maplefetion.event.ActionEvent#getType()
     */
    @Override
    public ActionEventType getEventType()
    {
    	return ActionEventType.TRANSFER_ERROR;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "TransferErrorEvent [EventType=" + getEventType() + "]";
	}

}
