package com.hkrt.domain;
import java.util.List;
public class LuceneSearchResult<T> {
	private int pageNo = 1;    //��ǰҳ  
	private int pageSize = 20;  //ÿҳ��ʾ��¼��  
	private int recordCount;   //�ܼ�¼��  
	private double time;       //��ʱ  
	private List<T> datas;     //��ǰҳ������  
	private int stratNo;       //��ʼ��¼��  
	private int endNo;         //������¼��  
	private String keyword;    //�ؼ���  
	public int getPageNo() {  
	return pageNo;  
	}  
	public void setPageNo(int pageNo) {  
	this.pageNo = pageNo;  
	}  
	public int getPageSize() {  
	return pageSize;  
	}  
	public void setPageSize(int pageSize) {  
	this.pageSize = pageSize;  
	}  
	public int getRecordCount() {  
	return recordCount;  
	}  
	public void setRecordCount(int recordCount) {  
	this.recordCount = recordCount;  
	}  
	public List<T> getDatas() {  
	return datas;  
	}  
	public void setDatas(List<T> datas) {  
	this.datas = datas;  
	}  
	public double getTime() {  
	return time;  
	}  
	public void setTime(double time) {  
	this.time = time;  
	}  
	public String getKeyword() {  
	return keyword;  
	}  
	public void setKeyword(String keyword) {  
	this.keyword = keyword;  
	}  
	public int getStratNo() {  
	return stratNo;  
	}  
	public void setStratNo(int stratNo) {  
	this.stratNo = stratNo;  
	}  
	public int getEndNo() {  
	return endNo;  
	}  
	public void setEndNo(int endNo) {  
	this.endNo = endNo;  
	}  
}
