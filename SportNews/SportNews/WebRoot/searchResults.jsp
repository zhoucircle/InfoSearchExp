<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page language="java" pageEncoding="utf-8"%>
<%@ include file="header.jsp" %>

<link rel="stylesheet" href="css/tablecloth.css" />
<script type="text/javascript" src="js/tablecloth.js"></script>

<div id="container">
   	<h1>SearchResults</h1>
   
 <table  border=1px >    
       <div>搜索结果：搜索关键字【${lSearchResult.keyword}】，共搜索到【${lSearchResult.recordCount }】个文件，耗时：${lSearchResult.time}秒，当前显示${lSearchResult.stratNo}—${lSearchResult.endNo}记录</div>    
       <c:forEach items="${request.lSearchResult.datas}" var="news">    
        <tr>  
          <td>    
           ${news.id}    
          </td>  
          <td>    
          ${news.title}    
          </td>    
          <td>
          ${news.body}
          </td>    
        </tr>    
        </c:forEach>    
</table>   
</div>
 <footer>

	<div class="wrapfooter">

	<p>Copyright © 2014 - <a href="#" target="_blank" title="组员信息">陈均煌，郭辰阳，潘海清，周环（无先后之分）</a></p>

	</div>

</footer>



</body></html>
