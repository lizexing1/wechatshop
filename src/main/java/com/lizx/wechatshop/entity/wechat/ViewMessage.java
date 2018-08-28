package com.lizx.wechatshop.entity.wechat;

/**
 * @author  作者 GuoZg E-mail: guozhenguo@huiyihuiying.com
 * @description 用户点击链接触发事件
 * @date 创建时间：2017年4月6日 下午1:59:51 
 * @version 1.0  
 * @param 
 * @return
 */
public class ViewMessage extends BaseMessage {
	
	public ViewMessage(){
		super.setMsgType(BaseMessage.MSG_TYPE_TEXT);
	}
	
	private String view;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}
	
	public String toXML() {
		
		StringBuffer xml = new StringBuffer();
		xml.append("<xml>");
		xml.append(super.toXML());
		xml.append("<Event><![CDATA[VIEW]]></Event>");
		xml.append("<EventKey><![CDATA[" + view + "]]></EventKey>");
		xml.append("</xml>");
		System.out.println("******************************"+xml.toString());
		return xml.toString();
	}
}
