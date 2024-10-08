package com.bolsadeideas.springboot.blogpost.app.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Role {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer id;
	
	@NotEmpty
	private String name;
	
	@NotEmpty
	private String code;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name="role_resource",
    joinColumns = @JoinColumn(name="role_id"),
    inverseJoinColumns = @JoinColumn(name="resource_id")) 
	private List<Resource> resources;

	public Role() {
	}

	

	public Role(Integer id, String name, String code) {
		this.id = id;
		this.name = name;
		this.code = code;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<Resource> getResources() {
		return resources;
	}
	
}
