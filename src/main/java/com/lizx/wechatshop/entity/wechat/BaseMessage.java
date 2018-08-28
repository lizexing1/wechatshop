package com.lizx.wechatshop.entity.wechat;

import java.util.Date;

/**
 * 请求消息基类（普通用户 -> 公众帐号）
 * 
 * @author yadong
 * @date 2013-09-11
 */
public class BaseMessage {
	public final static String MSG_TYPE_TEXT = "TEXT";
	// 开发者微信号
	private String ToUserName;
	// 发送方帐号（一个OpenID）
	private String FromUserName;
	// 消息创建时间 （整型）
	private long CreateTime;
	// 消息类型
	private String MsgType;
	
	public BaseMessage() {
		Date date = new Date();
		this.setCreateTime(date.getTime());
	}

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(long createTime) {
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	
	protected String toXML() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ToUserName><![CDATA[" + ToUserName + "]]></ToUserName>");
		xml.append("<FromUserName><![CDATA["+ FromUserName + "]]></FromUserName>");
		xml.append("<CreateTime>" + CreateTime + "</CreateTime>");
		xml.append("<MsgType><![CDATA[" +MsgType+ "]]></MsgType>");
		System.out.println(xml.toString());
		return xml.toString();
	}
}
