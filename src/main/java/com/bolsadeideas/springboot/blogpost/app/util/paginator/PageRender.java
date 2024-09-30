package com.bolsadeideas.springboot.blogpost.app.util.paginator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

public class PageRender<T> {
	
	private String url;
	private Page<T> page;
	
	private int totalPages;
	private int numPerPage;
	private int actualPage;
	
	private List<PageItem> paginas;
	
	public PageRender(String url, Page<T> page) {
		this.url = url;
		this.page = page;
		this.paginas = new ArrayList<PageItem>();
		
		this.numPerPage = page.getSize();
		this.totalPages = page.getTotalPages();
		this.actualPage = page.getNumber() + 1;
		
		int desde, hasta;
		if(totalPages <= numPerPage){
			desde =1;
			hasta = totalPages;
		}else {
			if(actualPage <= numPerPage/2){
				desde = 1;
				hasta = numPerPage;
			}else if(actualPage >= totalPages - numPerPage/2) {
				desde = totalPages - numPerPage +1;
				hasta = numPerPage;
			}else {
				desde = actualPage - numPerPage/2;
				hasta = numPerPage;
			}
			
		}
		
		for(int i=0; i < hasta; i++) {
			paginas.add(new PageItem(desde + i, actualPage == desde + i));
		}
	}

	public String getUrl() {
		return url;
	}

	public Page<T> getPage() {
		return page;
	}

	public int getActualPage() {
		return actualPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public List<PageItem> getPaginas() {
		return paginas;
	}
	
	public boolean isFirst() {
		return page.isFirst();
	}
	
	public boolean isLast() {
		return page.isLast();
	}
	
	public boolean isHasNext() {
		return page.hasNext();
	}
	
	public boolean isHasPrevius() {
		return page.hasPrevious();
	}
	

}
