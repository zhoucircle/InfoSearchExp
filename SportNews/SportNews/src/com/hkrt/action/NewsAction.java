package com.hkrt.action;

import java.util.List;

import com.hkrt.service.NewsService;
import com.opensymphony.xwork2.ActionContext;

public class NewsAction {
	
	NewsService newsService = new NewsService();
	private List News;
	public List getNews() {
		return News;
	}
	
	public String showAllTheNews(){
		News = newsService.showAllTheNews();
		ActionContext.getContext().getSession().put("allNews",News);
		return "allNews";
	}

}
