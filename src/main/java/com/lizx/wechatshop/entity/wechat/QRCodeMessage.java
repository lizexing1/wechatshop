package com.lizx.wechatshop.entity.wechat;
/**
 * 
 * <pre>项目名称：efilm    
 * 类名称：QRCodeMessage    
 * 类描述：    扫码返回地址
 * 创建人：李泽兴 lizexing@huiyihuiying.com    
 * 创建时间：2017年6月8日 下午4:42:38    
 * 修改人：李泽兴 lizexing@huiyihuiying.com
 * 修改时间：2017年6月8日 下午4:42:38    
 * 修改备注：       
 * @version </pre>
 */
public class QRCodeMessage extends BaseMessage{
	
	
	private String view;
	
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}

	public String toXML(){
		StringBuffer xml = new StringBuffer();
		xml.append("<xml>");
		xml.append(super.toXML());
		xml.append("<Event><![CDATA[scancode_push]]></Event>");
		xml.append("<EventKey><![CDATA[rselfmenu_0_1]]></EventKey>");
		xml.append("<ScanCodeInfo><ScanType><![CDATA[qrcode]]></ScanType><ScanResult><![CDATA["+view+"]]></ScanResult></ScanCodeInfo>");
		xml.append("</xml>");
		
		System.out.println("//////////////////////////////////"+xml.toString());
		return xml.toString();
	}
	
}
