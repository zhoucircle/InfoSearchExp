package com.hkrt.service;

import java.util.List;

import com.hkrt.dao.NewsDAO;

public class NewsService {
	NewsDAO newsDao = new NewsDAO();

	public List showAllTheNews() {
		// TODO Auto-generated method stub
		List news = newsDao.findAll();
		return news;
	}

}
