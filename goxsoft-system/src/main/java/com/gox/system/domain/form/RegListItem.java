package com.gox.system.domain.form;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gox.common.plugin.AutoId;

public class RegListItem{
	@AutoId
	@JSONField(serialize = false)
	private Long id;
	@JSONField(serialize = false)
	private Long configId;
	@JSONField(name = "pattern")
	private String pattern;

	@JSONField(name = "message")
	private String message;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPattern(){
		return pattern;
	}

	public String getMessage(){
		return message;
	}
}