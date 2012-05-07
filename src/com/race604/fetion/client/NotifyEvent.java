package com.race604.fetion.client;
/**
 * @author Wu Jing wujing@jike.com
 * @version Create at：2012-5-7 下午5:13:05
 * 
 **/
public abstract class NotifyEvent
{
	
	/**
	 * 默认构造函数
	 */
	public NotifyEvent()
	{
	}

	/**
	 * 返回通知事件类型
	 * @return
	 */
	public abstract EventType getEventType();
	
	public static enum EventType{
		/**
		 * 登录状态发生变化
		 */
		LOGIN_STATE,
		
		/**
		 * 客户端状态发生变化
		 */
		CLIENT_STATE,
		
		/**
		 * 好友消息
		 */
		BUDDY_MESSAGE,
		
		/**
		 * 群消息
		 */
		GROUP_MESSAGE,
		
		/**
		 * 系统消息
		 */
		SYSTEM_MESSAGE,
		
		/**
		 * 添加好友请求
		 */
		BUDDY_APPLICAION,
		
		/**
		 * 对方回复了添加好友的请求
		 */
		BUDDY_CONFIRMED,
		
		/**
		 * 对方状态发生改变
		 */
		BUDDY_PRESENCE,	
		
		/**
		 * 接收到了一个邀请
		 */
		INVITE_RECEIVED,
		
		/**
		 * 操作需要验证
		 */
		IMAGE_VERIFY
	}
}
