/*
 * Copyright (C) 2016-2019 Alexandre Oliveira <alexandre.oliveira@viglet.com> 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.viglet.shiohara.persistence.model.post.relator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.viglet.shiohara.persistence.model.post.ShPostDraftAttr;

/**
 * The persistent class for the ShRelatorItem database table.
 * 
 */
@Entity
@NamedQuery(name = "ShRelatorItemDraft.findAll", query = "SELECT rid FROM ShRelatorItemDraft rid")
@JsonIgnoreProperties({ "shPostAttr", "shParentPostAttr" })
public class ShRelatorItemDraft implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "com.viglet.shiohara.jpa.ShUUIDGenerator")
	@GeneratedValue(generator = "UUID")
	@Column(name = "id", updatable = false, nullable = false)
	private String id;

	private String title;
	
	private String summary;
	
	private int ordinal;
	
	// bi-directional many-to-one association to ShPostAttr
	@OneToMany(mappedBy = "shParentRelatorItem", orphanRemoval = true, fetch = FetchType.LAZY)
	// Need this join, because without this show error when try to remove a relator item.
	@Fetch(org.hibernate.annotations.FetchMode.JOIN)
	@Cascade({CascadeType.ALL})
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Set<ShPostDraftAttr> shChildrenPostAttrs = new HashSet<ShPostDraftAttr>();

	// bi-directional many-to-one association to ShPost
	@ManyToOne(fetch = FetchType.LAZY) // (cascade = {CascadeType.ALL})
	@JoinColumn(name = "post_attr_id")
	private ShPostDraftAttr shParentPostAttr;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<ShPostDraftAttr> getShChildrenPostAttrs() {
		return shChildrenPostAttrs;
	}

	public void setShChildrenPostAttrs(Set<ShPostDraftAttr> shChildrenPostAttrs) {
		this.shChildrenPostAttrs.clear();
		if (shChildrenPostAttrs != null) {
			this.shChildrenPostAttrs.addAll(shChildrenPostAttrs);
		}
	}

	public ShPostDraftAttr getShParentPostAttr() {
		return shParentPostAttr;
	}

	public void setShParentPostAttr(ShPostDraftAttr shParentPostAttr) {
		this.shParentPostAttr = shParentPostAttr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

}