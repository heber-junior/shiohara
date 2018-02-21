package com.viglet.shiohara.exchange;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ShChannelExchange {
	private UUID globalId;
	
	private UUID id;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm'Z'")
	private Date date;

	private String name;

	private UUID parentChannel;

	public UUID getGlobalId() {
		return globalId;
	}

	public void setGlobalId(UUID globalId) {
		this.globalId = globalId;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getParentChannel() {
		return parentChannel;
	}

	public void setParentChannel(UUID parentChannel) {
		this.parentChannel = parentChannel;
	}

}
