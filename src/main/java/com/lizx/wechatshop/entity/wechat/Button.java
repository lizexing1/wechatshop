package com.lizx.wechatshop.entity.wechat;

import java.util.ArrayList;

/** 
 * @author  作者 GuoZg E-mail: guozhenguo@huiyihuiying.com
 * @description 微信菜单按钮
 * @date 创建时间：2017年4月5日 下午8:33:34 
 * @version 1.0  
 */
public class Button {
	
	
	public static final String TYPE_CLICK = "click";
	public static final String TYPE_VIEW = "view";
	public static final String TYPE_SCANCODE_PUSH = "scancode_push";
	public static final String TYPE_SCANCODE_WAITMSG = "scancode_waitmsg";
	public static final String TYPE_PIC_SYSPHOTO = "pic_sysphoto";
	public static final String TYPE_PIC_PHOTO_OR_ALBUM = "pic_photo_or_album";
	public static final String TYPE_PIC_WEIXIN = "pic_weixin";
	public static final String TYPE_LOCATION_SELECT = "location_select";
	public static final String TYPE_MEDIA_ID = "media_id";
	public static final String TYPE_VIEW_LIMITED = "click";
	
	/**菜单的响应动作类型**/
	private String type;
	/**菜单名称**/
	private String name;
	/**菜单KEY值，用于消息接口推送，不超过128字节**/
	private String key;
	/**view类型必须,网页链接，用户点击菜单可打开链接，不超过1024字节**/
	private String url;
	/**media_id类型和view_limited类型必须,调用新增永久素材接口返回的合法media_id**/
	private String media_id;
	/**子菜单**/
	private ArrayList<Button> sub_button;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMedia_id() {
		return media_id;
	}
	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}
	public ArrayList<Button> getSub_button() {
		return sub_button;
	}
	public void setSub_button(ArrayList<Button> sub_button) {
		this.sub_button = sub_button;
	}
}
