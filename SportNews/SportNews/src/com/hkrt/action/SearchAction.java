package com.hkrt.action;

import com.hkrt.domain.LuceneSearchResult;
import com.hkrt.domain.News;
import com.hkrt.service.SearchService;
import com.opensymphony.xwork2.ActionContext;

public class SearchAction {
	public String keywords;
	
	public LuceneSearchResult<News> lSearchResult;
	


	SearchService searchService = new SearchService();
	
	public String getSearchResults(){
		System.out.println("�ؼ�����" + keywords);
		searchService.buildIndex();
		lSearchResult = searchService.getSearchResults(keywords);
		System.out.println(lSearchResult.getKeyword()+"����"+lSearchResult.getTime());
		
		for(int i = 0 ; i < lSearchResult.getDatas().size();++i){
			System.out.print("�ĵ�����" + lSearchResult.getDatas().get(i).getTitle()+"  ");
			System.out.println("�ĵ�����" + lSearchResult.getDatas().get(i).getBody());
		}
		
		
		ActionContext.getContext().getSession().put("searchResults",lSearchResult);
		return "searchResults";
	}
	
}
