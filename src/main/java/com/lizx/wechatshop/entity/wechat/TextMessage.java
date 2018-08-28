package com.lizx.wechatshop.entity.wechat;

/**
 * @author  作者 GuoZg E-mail: guozhenguo@huiyihuiying.com
 * @description 反馈给微信用户的文本消息
 * @date 创建时间：2017年4月6日 下午1:59:51 
 * @version 1.0  
 * @param 
 * @return
 */
public class TextMessage extends BaseMessage {
	
	public TextMessage(){
		super.setMsgType(BaseMessage.MSG_TYPE_TEXT);
	}
	
	private String Content;

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
	
	public String toXML() {
		
		StringBuffer xml = new StringBuffer();
		xml.append("<xml>");
		xml.append(super.toXML());
		xml.append("<Content><![CDATA[" +Content + "]]></Content>");
		xml.append("</xml>");
		return xml.toString();
	}
}
