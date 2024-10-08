package com.bolsadeideas.springboot.blogpost.app.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Action {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String url;
	
	private Integer parent_resource_id;
	
	private String roles_id;
	
	public Action() {
	}

	public Action(String name, Integer resource_id, String roles_id) {
		this.name = name;
		this.parent_resource_id = resource_id;
		this.roles_id = roles_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getParent_resource_id() {
		return parent_resource_id;
	}

	public void setParent_resource_id(Integer parent_resource_id) {
		this.parent_resource_id = parent_resource_id;
	}

	public String getRoles_id() {
		return roles_id;
	}

	public void setRoles_id(String roles_id) {
		this.roles_id = roles_id;
	}
	
	
	
	

}
