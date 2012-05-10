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
 * File     : FutureActionEventListener.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-1-11
 * License  : Apache License 2.0 
 */
package com.race604.fetion.event.action;

import com.race604.fetion.event.ActionEvent;

/**
 *
 * 异步通知结果监听器
 *
 * @author solosky <solosky772@qq.com>
 */
public class FutureActionEventListener implements ActionEventListener
{
	private ActionEventFuture future;
	
	public FutureActionEventListener(ActionEventFuture future)
	{
		this.future = future;
	}

	/* (non-Javadoc)
	 * @see net.solosky.maplefetion.client.dialog.ActionEventListener#fireEevent(net.solosky.maplefetion.event.ActionEvent)
	 */
	@Override
	public void fireEevent(ActionEvent event)
	{
		if(this.future!=null)
			this.future.setActionEvent(event);
	}
}