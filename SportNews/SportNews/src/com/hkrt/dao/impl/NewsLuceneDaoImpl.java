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
	/** 获取语法解析器 */
	public Analyzer getAnalyzer() {
		return new StandardAnalyzer(Version.LUCENE_30);
	}

	/** 打开索引的存放目录 */
	public Directory openDirectory() {
		try {
			System.out.println(new File(INDEX_DIR)	+ "-------打开索引--------------");
			return FSDirectory.open(new File(INDEX_DIR));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 对文件的指定属性映射成域,返回文件文档对象 */  
	public Document createForumuploadDocument(News news) {
		Document doc = new Document(); // 创建一个文档对象
		//id 域
		@SuppressWarnings("deprecation")
		Field field = new Field(FIELD_ID,String.valueOf(news.getId()),Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(field);
		// title域
		@SuppressWarnings("deprecation")
		Field field1 = new Field(FIELD_TITLE, String.valueOf(news.getTitle()),Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field1);
		// content域
		@SuppressWarnings("deprecation")
		Field field2 = new Field(FIELD_CONTENTS, String.valueOf(news.getBody()), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field2);
		return doc;
	}

	public void deleteIndex(Integer id) {
		IndexReader ir = null;  
		try {  
			ir = IndexReader.open(this.openDirectory(), false);  //打开指定目录下索引文件的索引读取器  
			ir.deleteDocuments(new Term(FIELD_ID,String.valueOf(id)));  //删除符合条件的Document  
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
		//创建索引写入器  
		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(openDirectory(), getAnalyzer(),false, IndexWriter.MaxFieldLength.UNLIMITED);
			Document doc = this.createForumuploadDocument(news);
			indexWriter.addDocument(doc);
			indexWriter.optimize(); // 对索引进行优化
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (indexWriter != null) {
					indexWriter.close(); // 关闭IndexWriter,把内存中的数据写到文件
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
			// 创建一个索引搜索器
			searcher = new IndexSearcher(this.openDirectory(), true);
			// 用多域查询解析器来创建一个查询器,
			Query query = MultiFieldQueryParser.parse(Version.LUCENE_30,keyword, new String[] { FIELD_TITLE, FIELD_CONTENTS },
			new BooleanClause.Occur[] {BooleanClause.Occur.SHOULD,BooleanClause.Occur.SHOULD }, this.getAnalyzer());
			long begin = System.currentTimeMillis();
			System.out.println("查询开始时间是"+begin);
			// 查询结集信息类
			TopDocs ts = searcher.search(query, null, 100000);
			// 获取命中的数量
			lsr.setRecordCount(ts.totalHits);
			// 用这个进行高亮显示，默认是<b>..</b>
			SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style=color:red>", "</span>");
			// 构造高亮:指定高亮的格式,指定查询评分
			Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));highlighter.setTextFragmenter(new SimpleFragmenter(Integer.MAX_VALUE));
			// 获取匹配到的结果集
			ScoreDoc[] hits = ts.scoreDocs;
			List<News> ais = new ArrayList<News>();
			int pageCount = (lsr.getRecordCount() + pageSize - 1) / pageSize; // 总页数
			int start = 0; // 要开始返回的文档编号
			int end = 0; // 要结束返回的文档编号
			if (pageCount > 0) {
				start = (pageNo - 1) * pageSize;
				end = start + pageSize;
				if (pageNo == pageCount) { // 处理最后一页的结束文档的编号
					end = start + (lsr.getRecordCount() % pageSize);
				}
			}
			if (start < end) {
				lsr.setStratNo(start + 1);
				lsr.setEndNo(end);
			}
			for (int i = start; i < end; i++) { // 循环获取分页数据
				// 通过内部编号从搜索器中得到对应的文档
				Document doc = searcher.doc(hits[i].doc);
				News news = new News();
				news.setTitle(doc.getField(FIELD_TITLE).stringValue());
				news.setBody(doc.getField(FIELD_CONTENTS).stringValue()); 
				// 处理文件名称的高亮显示问题
				String title = doc.getField(FIELD_TITLE).stringValue();
				String title2 = highlighter.getBestFragment(this.getAnalyzer(),FIELD_TITLE, title);
				if (title2 == null) {
					news.setTitle(title);
				} else {
					news.setTitle(title2);
				}
				// 文件描述高亮显示
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
				ais.add(news); // 把符合条件的数据添加到List
			}
			lsr.setTime((System.currentTimeMillis() - begin) / 1000.0); // 计算搜索耗时秒数
			lsr.setDatas(ais); // 把查询到的数据添加到LuceneSearchResult
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		} finally {
			if (searcher != null) {
				try {
					searcher.close(); // 关闭搜索器
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
		
		System.out.println("开始读入数据库中的数据……");
		@SuppressWarnings("unchecked")
		List<News> data = this.newsDao.findAll();
		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(this.openDirectory(), getAnalyzer(),true, IndexWriter.MaxFieldLength.UNLIMITED);
			System.out.println("建立索引……");
			// 设置打开使用复合文件
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
			indexWriter.optimize(); // 对索引进行优化
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (indexWriter != null) {
					indexWriter.close();// 关闭IndexWriter,把内存中的数据写到文件
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

