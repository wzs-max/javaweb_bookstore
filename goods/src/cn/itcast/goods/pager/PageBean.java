package cn.itcast.goods.pager;

import java.util.List;
/*
 * 分页bean
 */
public class PageBean<T> {
	
	private int pc;//当前页码
	private int tr;//总记录数
	private int ps;//每页总记录数
	private String url;//请求路径
	private int tp;//总页数
	private List<T> beanList;//数据
	
	public int getTp() {
		return tr % ps == 0 ? tr / ps : tr / ps +1;
	}
	
	
	
	
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getTr() {
		return tr;
	}
	public void setTr(int tr) {
		this.tr = tr;
	}
	public int getPs() {
		return ps;
	}
	public void setPs(int ps) {
		this.ps = ps;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<T> getBeanList() {
		return beanList;
	}
	public void setBeanList(List<T> beanList) {
		this.beanList = beanList;
	}



	@Override
	public String toString() {
		return "PageBean [pc=" + pc + ", tr=" + tr + ", ps=" + ps + ", url=" + url + ", tp=" + tp + ", beanList="
				+ beanList + ", getTp()=" + getTp() + ", getPc()=" + getPc() + ", getTr()=" + getTr() + ", getPs()="
				+ getPs() + ", getUrl()=" + getUrl() + ", getBeanList()=" + getBeanList() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	
	
}
