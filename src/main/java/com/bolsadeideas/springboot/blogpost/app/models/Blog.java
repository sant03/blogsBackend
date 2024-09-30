package com.bolsadeideas.springboot.blogpost.app.models;

import java.util.List;

public class Blog {
	
	private String title;
	
	private String content;
	
	private String category;
	
	private List<String> tags;
	
	public Blog() {
	}

	public Blog(String title, String content, String category, List<String> tags) {
		super();
		this.title = title;
		this.content = content;
		this.category = category;
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "Blog [title=" + title + ", content=" + content + ", category=" + category + ", tags=" + tags + "]";
	}
	
	

}
