package com.viglet.shiohara.persistence.model.post;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Fetch;

import com.viglet.shiohara.persistence.model.post.type.ShPostType;
import com.viglet.shiohara.persistence.model.region.ShRegion;
import com.viglet.shiohara.persistence.model.site.ShSite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the ShPost database table.
 * 
 */
@Entity
@NamedQuery(name = "ShPost.findAll", query = "SELECT s FROM ShPost s")
public class ShPost implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private String summary;

	private String title;

	// bi-directional many-to-one association to ShPostType
	@ManyToOne
	@JoinColumn(name = "post_type_id")
	private ShPostType shPostType;

	// bi-directional many-to-one association to ShPostAttr
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "shPost", cascade = CascadeType.ALL)
	@Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	private List<ShPostAttr> shPostAttrs;

	// bi-directional many-to-one association to ShRegion
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "shPost", cascade = CascadeType.ALL)
	@Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	private List<ShRegion> shRegions;

	// bi-directional many-to-one association to ShRegion
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	private List<ShSite> shSites = new ArrayList<>();

	public ShPost() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ShPostType getShPostType() {
		return this.shPostType;
	}

	public void setShPostType(ShPostType shPostType) {
		this.shPostType = shPostType;
	}

	public List<ShPostAttr> getShPostAttrs() {
		return this.shPostAttrs;
	}

	public void setShPostAttrs(List<ShPostAttr> shPostAttrs) {
		this.shPostAttrs = shPostAttrs;
	}

	public ShPostAttr addShPostAttr(ShPostAttr shPostAttr) {
		getShPostAttrs().add(shPostAttr);
		shPostAttr.setShPost(this);

		return shPostAttr;
	}

	public ShPostAttr removeShPostAttr(ShPostAttr shPostAttr) {
		getShPostAttrs().remove(shPostAttr);
		shPostAttr.setShPost(null);

		return shPostAttr;
	}

	public List<ShRegion> getShRegions() {
		return this.shRegions;
	}

	public void setShRegions(List<ShRegion> shRegions) {
		this.shRegions = shRegions;
	}

	public ShRegion addShRegion(ShRegion shRegion) {
		getShRegions().add(shRegion);
		shRegion.setShPost(this);

		return shRegion;
	}

	public ShRegion removeShRegion(ShRegion shRegion) {
		getShRegions().remove(shRegion);
		shRegion.setShPost(null);

		return shRegion;
	}

	public List<ShSite> getShSites() {
		return this.shSites;
	}

	public void setShSites(List<ShSite> shSites) {
		this.shSites = shSites;
	}

	public ShSite addShSite(ShSite shSite) {
		getShSites().add(shSite);
		shSite.addShPost(this);

		return shSite;
	}

	public ShSite removeShSite(ShSite shSite) {
		getShSites().remove(shSite);
		shSite.removeShPost(this);

		return shSite;
	}
}