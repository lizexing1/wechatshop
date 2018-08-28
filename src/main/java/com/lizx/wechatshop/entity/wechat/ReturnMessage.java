package com.lizx.wechatshop.entity.wechat;
/** 
 * @author  作者 GuoZg E-mail: guozhenguo@huiyihuiying.com
 * @description 
 * @date 创建时间：2016年12月9日 下午4:29:58 
 * @version 1.0  
 */
public class ReturnMessage {
	
	private Integer errcode;
	private String errmsg;
	
	public Integer getErrcode() {
		return errcode;
	}
	public void setErrcode(Integer errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	
	
}
