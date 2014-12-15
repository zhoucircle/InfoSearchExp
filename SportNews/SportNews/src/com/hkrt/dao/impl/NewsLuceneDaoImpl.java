package com.hkrt.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import com.hkrt.dao.NewsDAO;
import com.hkrt.dao.NewsLuceneDao;
import com.hkrt.domain.LuceneSearchResult;
import com.hkrt.domain.News;
public class NewsLuceneDaoImpl implements NewsLuceneDao {
	 NewsDAO newsDao = new NewsDAO();
	/** ��ȡ�﷨������ */
	public Analyzer getAnalyzer() {
		return new StandardAnalyzer(Version.LUCENE_30);
	}

	/** �������Ĵ��Ŀ¼ */
	public Directory openDirectory() {
		try {
			System.out.println(new File(INDEX_DIR)	+ "-------������--------------");
			return FSDirectory.open(new File(INDEX_DIR));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** ���ļ���ָ������ӳ�����,�����ļ��ĵ����� */  
	public Document createForumuploadDocument(News news) {
		Document doc = new Document(); // ����һ���ĵ�����
		//id ��
		@SuppressWarnings("deprecation")
		Field field = new Field(FIELD_ID,String.valueOf(news.getId()),Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(field);
		// title��
		@SuppressWarnings("deprecation")
		Field field1 = new Field(FIELD_TITLE, String.valueOf(news.getTitle()),Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field1);
		// content��
		@SuppressWarnings("deprecation")
		Field field2 = new Field(FIELD_CONTENTS, String.valueOf(news.getBody()), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field2);
		return doc;
	}

	public void deleteIndex(Integer id) {
		IndexReader ir = null;  
		try {  
			ir = IndexReader.open(this.openDirectory(), false);  //��ָ��Ŀ¼�������ļ���������ȡ��  
			ir.deleteDocuments(new Term(FIELD_ID,String.valueOf(id)));  //ɾ������������Document  
		} catch (IOException e) {  
			e.printStackTrace();  
		}finally{  
			if(ir != null){  
				try {  
					ir.close();  
				} catch (IOException e) {  
					e.printStackTrace();  
				}  
			}  
		}  
	}

	@Override
	public void doIndexSingle(News news) {
		//��������д����  
		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(openDirectory(), getAnalyzer(),false, IndexWriter.MaxFieldLength.UNLIMITED);
			Document doc = this.createForumuploadDocument(news);
			indexWriter.addDocument(doc);
			indexWriter.optimize(); // �����������Ż�
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (indexWriter != null) {
					indexWriter.close(); // �ر�IndexWriter,���ڴ��е�����д���ļ�
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public LuceneSearchResult<News> doSeacher(String keyword, int pageNo,int pageSize) {
		LuceneSearchResult<News> lsr = new LuceneSearchResult<News>();
		lsr.setPageNo(pageNo);
		lsr.setPageSize(pageSize);
		lsr.setKeyword(keyword);
		IndexSearcher searcher = null;
		try {
			// ����һ������������
			searcher = new IndexSearcher(this.openDirectory(), true);
			// �ö����ѯ������������һ����ѯ��,
			Query query = MultiFieldQueryParser.parse(Version.LUCENE_30,keyword, new String[] { FIELD_TITLE, FIELD_CONTENTS },
			new BooleanClause.Occur[] {BooleanClause.Occur.SHOULD,BooleanClause.Occur.SHOULD }, this.getAnalyzer());
			long begin = System.currentTimeMillis();
			System.out.println("��ѯ��ʼʱ����"+begin);
			// ��ѯ�Ἧ��Ϣ��
			TopDocs ts = searcher.search(query, null, 100000);
			// ��ȡ���е�����
			lsr.setRecordCount(ts.totalHits);
			// ��������и�����ʾ��Ĭ����<b>..</b>
			SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style=color:red>", "</span>");
			// �������:ָ�������ĸ�ʽ,ָ����ѯ����
			Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));highlighter.setTextFragmenter(new SimpleFragmenter(Integer.MAX_VALUE));
			// ��ȡƥ�䵽�Ľ����
			ScoreDoc[] hits = ts.scoreDocs;
			List<News> ais = new ArrayList<News>();
			int pageCount = (lsr.getRecordCount() + pageSize - 1) / pageSize; // ��ҳ��
			int start = 0; // Ҫ��ʼ���ص��ĵ����
			int end = 0; // Ҫ�������ص��ĵ����
			if (pageCount > 0) {
				start = (pageNo - 1) * pageSize;
				end = start + pageSize;
				if (pageNo == pageCount) { // �������һҳ�Ľ����ĵ��ı��
					end = start + (lsr.getRecordCount() % pageSize);
				}
			}
			if (start < end) {
				lsr.setStratNo(start + 1);
				lsr.setEndNo(end);
			}
			for (int i = start; i < end; i++) { // ѭ����ȡ��ҳ����
				// ͨ���ڲ���Ŵ��������еõ���Ӧ���ĵ�
				Document doc = searcher.doc(hits[i].doc);
				News news = new News();
				news.setTitle(doc.getField(FIELD_TITLE).stringValue());
				news.setBody(doc.getField(FIELD_CONTENTS).stringValue()); 
				// �����ļ����Ƶĸ�����ʾ����
				String title = doc.getField(FIELD_TITLE).stringValue();
				String title2 = highlighter.getBestFragment(this.getAnalyzer(),FIELD_TITLE, title);
				if (title2 == null) {
					news.setTitle(title);
				} else {
					news.setTitle(title2);
				}
				// �ļ�����������ʾ
				String contents1 = doc.getField(FIELD_CONTENTS).stringValue();
				String contents2 = highlighter.getBestFragment(this.getAnalyzer(), FIELD_CONTENTS, contents1);
				if (contents2 == null) {
					news.setBody(contents1);
				} else {
					if (contents2.length() > 512) {
						news.setBody(contents2.substring(0, 512) + "...");
					} else {
						news.setBody(contents2);
					}
				}
				ais.add(news); // �ѷ���������������ӵ�List
			}
			lsr.setTime((System.currentTimeMillis() - begin) / 1000.0); // ����������ʱ����
			lsr.setDatas(ais); // �Ѳ�ѯ����������ӵ�LuceneSearchResult
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		} finally {
			if (searcher != null) {
				try {
					searcher.close(); // �ر�������
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	return lsr;
	
	}

	@Override
	public void rebuildAllIndex() {
		File file = new File(INDEX_DIR);
		if (file.exists()) {
			for (File subFile : file.listFiles()) {
				subFile.delete();
			}
		} else {
			file.mkdirs();
		}
		
		System.out.println("��ʼ�������ݿ��е����ݡ���");
		@SuppressWarnings("unchecked")
		List<News> data = this.newsDao.findAll();
		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(this.openDirectory(), getAnalyzer(),true, IndexWriter.MaxFieldLength.UNLIMITED);
			System.out.println("������������");
			// ���ô�ʹ�ø����ļ�
			// indexWriter.setUseCompoundFile(true);
			int size = data == null ? 0 : data.size();
			for (int i = 0; i < size; i++) {
				News news = data.get(i);
				Document doc = createForumuploadDocument(news);
				indexWriter.addDocument(doc);
				if (i % 20 == 0) {
					indexWriter.commit();
				}
			}
			indexWriter.optimize(); // �����������Ż�
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (indexWriter != null) {
					indexWriter.close();// �ر�IndexWriter,���ڴ��е�����д���ļ�
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void updateIndex(News news) {
		this.deleteIndex(news.getId());  
		this.doIndexSingle(news);  
	}
	public NewsDAO getNewsDao() {
		return newsDao;
	}
	public void setNewsDao(NewsDAO newsDao) {
		this.newsDao = newsDao;
	}
}

