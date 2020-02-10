package cn.qs.bean.sale;

import javax.persistence.Entity;

import cn.qs.bean.AbstractSequenceEntity;
@Entity
public class ActuallySale extends AbstractSequenceEntity {

	/**
	 * 销售用户名
	 */
	private String saleusername;

	/**
	 * 销售全名
	 */
	private String salefullname;

	/**
	 * 销售金额
	 */
	private String saleamount;

	/**
	 * 应酬金额
	 */
	private String socialamount;

	/**
	 * 出差金额
	 */
	private String awayamount;

	/**
	 * 年份
	 */
	private String yearnum;

	/**
	 * 月份
	 */
	private String monthnum;

	public String getSaleusername() {
		return saleusername;
	}

	public void setSaleusername(String saleusername) {
		this.saleusername = saleusername;
	}

	public String getSalefullname() {
		return salefullname;
	}

	public void setSalefullname(String salefullname) {
		this.salefullname = salefullname;
	}

	public String getSaleamount() {
		return saleamount;
	}

	public void setSaleamount(String saleamount) {
		this.saleamount = saleamount;
	}

	public String getSocialamount() {
		return socialamount;
	}

	public void setSocialamount(String socialamount) {
		this.socialamount = socialamount;
	}

	public String getAwayamount() {
		return awayamount;
	}

	public void setAwayamount(String awayamount) {
		this.awayamount = awayamount;
	}

	public String getYearnum() {
		return yearnum;
	}

	public void setYearnum(String yearnum) {
		this.yearnum = yearnum;
	}

	public String getMonthnum() {
		return monthnum;
	}

	public void setMonthnum(String monthnum) {
		this.monthnum = monthnum;
	}

}
