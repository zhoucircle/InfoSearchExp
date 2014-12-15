<%@ include file="header.jsp" %>

<style type="text/css">
	@import url('css/style2.css');
</style>

<div id="formOutsideField">
<div border="border:1px solid #000000;" >
	<div id="wrapper">
		<h3 id="sign-in-tab" class="active">Search</h3>
		<h3 id="register-tab"></h3>
		<!-- BEGIN FORM SECTION -->
		<ul id="form-section">
			<!-- BEGIN SIGN IN FORM -->
    
    <form class="sign-in-form"  action="searchAction!getSearchResults.action" method="post">
    	<li>
    		<label>
    		<span>关键字</span>
    		<input name="keywords">
    		</label>
    		
    	</li>
    	<li>
    		<button name="sign-in-submit" type="submit" >搜索</button>
    	</li>
    	<!-- <li>
    		<a href="showAllNewsAction!showAllTheNews.action">显示所有新闻</a>
    	</li> -->
    	
			<div style="clear: both;"></div>
	</form><!-- END OF SIGN IN FORM -->

	</ul><!-- END OF FORM SECTION -->
	</div><!-- END OF WRAPPER DIV -->
</div>
</div>
    	
    </form>
    

<%@ include file="footer.jsp" %>
