package com.hkrt.service;

import com.hkrt.dao.impl.NewsLuceneDaoImpl;
import com.hkrt.domain.LuceneSearchResult;
import com.hkrt.domain.News;

public class SearchService {

	NewsLuceneDaoImpl nlDaoImpl = new NewsLuceneDaoImpl();
	
	public LuceneSearchResult<News> getSearchResults(String keywords) {
		// TODO Auto-generated method stub
		System.out.println("service¿‡keywords «£∫"+keywords);
		LuceneSearchResult<News> lsr = nlDaoImpl.doSeacher(keywords,1,20);
		return lsr;
	}

	public void buildIndex() {
		nlDaoImpl.rebuildAllIndex();
		// TODO Auto-generated method stub
		
	}

}
