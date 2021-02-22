package com.ruoyi.business.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.ruoyi.common.annotation.Excel;

/**
 * VIEW对象 view_bus_zdxx
 * 
 * @author yaowei
 * @date 2021-02-22
 */
public class ViewBusZdxx implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 主键 */
	private Long id;

	/** 站点名称 */
	@Excel(name = "站点名称")
	private String zdmc;

	/** 站点定额id */
	@Excel(name = "站点定额id")
	private Long zddeid;

	/** 服务项目 */
	@Excel(name = "服务项目")
	private String fwxm;

	/** 等级划分 */
	@Excel(name = "等级划分")
	private String djhf;

	/** 计价单位 */
	@Excel(name = "计价单位")
	private String jjdw;

	/** 定额 */
	@Excel(name = "定额")
	private BigDecimal de;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setZdmc(String zdmc) {
		this.zdmc = zdmc;
	}

	public String getZdmc() {
		return zdmc;
	}

	public void setZddeid(Long zddeid) {
		this.zddeid = zddeid;
	}

	public Long getZddeid() {
		return zddeid;
	}

	public void setFwxm(String fwxm) {
		this.fwxm = fwxm;
	}

	public String getFwxm() {
		return fwxm;
	}

	public void setDjhf(String djhf) {
		this.djhf = djhf;
	}

	public String getDjhf() {
		return djhf;
	}

	public void setJjdw(String jjdw) {
		this.jjdw = jjdw;
	}

	public String getJjdw() {
		return jjdw;
	}

	public void setDe(BigDecimal de) {
		this.de = de;
	}

	public BigDecimal getDe() {
		return de;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("id", getId()).append("zdmc", getZdmc())
				.append("zddeid", getZddeid()).append("fwxm", getFwxm()).append("djhf", getDjhf())
				.append("jjdw", getJjdw()).append("de", getDe()).toString();
	}
}
