package com.hkrt.dao;

import com.hkrt.domain.LuceneSearchResult;
import com.hkrt.domain.News;
public interface NewsLuceneDao {
	public static final String FIELD_ID="id";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_CONTENTS = "contents";
	// �������Ŀ¼ Thread.currentThread().getContextClassLoader().getResource("").getPath()+"index_dir"
	 public static final String INDEX_DIR ="c://indextest";
	/**
	 * �������ļ�������������
	 */
	public void rebuildAllIndex();
	/**
	 * ��ָ���ϴ��ļ��������������׷�ӵ����е������ļ���
	 * @param news
	 */
	public void doIndexSingle(News news);
	/**
	 * ���ݹؼ�������,���ط��������ķ�ҳ����
	 * @param keyword   �ؼ���
	 * @param pageNo    ��ʼҳ
	 * @param pageSize  ÿҳҪ��ʾ�ļ�¼��
	 * @return LuceneSearchResult����
	 */
	public LuceneSearchResult<News> doSeacher(String keyword, int pageNo,int pageSize);
	/**
	 * �����ļ�������
	 * @param news
	 */
	public void updateIndex(News news);
	/**
	 * �����ļ�idɾ������
	 * @param id
	 */
	public void deleteIndex(Integer id);
}