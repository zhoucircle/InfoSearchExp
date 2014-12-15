<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

  
  <body>
    <div id="container">
   	<h1>所有新闻</h1>
   	<table border=1px>
   	<tr>
   		<th class="category"><s:text name="新闻标题"/></th>
   		<th class="category"><s:text name="新闻内容"/></th>
   		
   	</tr>
   	<s:iterator value="News">
   	<tr>
   		<td><s:property value="title" /></td>
   		<td><s:property value="body" /></td>
   		
   	</tr>
   </s:iterator>
  </body>
</html>
