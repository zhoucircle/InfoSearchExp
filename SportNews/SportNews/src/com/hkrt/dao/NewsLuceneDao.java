package com.hkrt.dao;

import com.hkrt.domain.LuceneSearchResult;
import com.hkrt.domain.News;
public interface NewsLuceneDao {
	public static final String FIELD_ID="id";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_CONTENTS = "contents";
	// 索引存放目录 Thread.currentThread().getContextClassLoader().getResource("").getPath()+"index_dir"
	 public static final String INDEX_DIR ="c://indextest";
	/**
	 * 对所有文件进行重新索引
	 */
	public void rebuildAllIndex();
	/**
	 * 对指定上传文件对象进行索引并追加到已有的索引文件中
	 * @param news
	 */
	public void doIndexSingle(News news);
	/**
	 * 根据关键字搜索,返回符合条件的分页数据
	 * @param keyword   关键字
	 * @param pageNo    起始页
	 * @param pageSize  每页要显示的记录数
	 * @return LuceneSearchResult对象
	 */
	public LuceneSearchResult<News> doSeacher(String keyword, int pageNo,int pageSize);
	/**
	 * 更新文件的索引
	 * @param news
	 */
	public void updateIndex(News news);
	/**
	 * 根据文件id删除索引
	 * @param id
	 */
	public void deleteIndex(Integer id);
}