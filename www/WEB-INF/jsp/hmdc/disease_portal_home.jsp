<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="disease_portal_header.jsp" %>

<title>Human - Mouse Disease Connection</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%@ include file="disease_portal_bodystart.jsp" %>

<div style="display: none;width: 175px;">
	<form>
		<div class="form-group">
			<label>Theme</label>
			<select class="form-control" ng-model="css" ng-options="boot.url as boot.name for boot in boots"></select>
		</div>
	</form>
</div>

<div ng-controller="SearchController">

	<div class="container">
		<div style="border: 0px;margin-bottom:30px;text-align: center;">
			<a href="${configBean.FEWI_URL}/diseasePortal" style="font-size:45px;color:black;">Human <img src="${configBean.WEBSHARE_URL}images/hmdc_arrow.png" style='height:42px;'> Mouse: Disease Connection</a>
		</div>
	</div>

	<div class="container searchView">
		<div ng-include="'/assets/hmdc/search/views/search.tpl.html'"></div>
	</div>

	<div class="container-fluid searchViewBox" ng-show="vm.results">
		<div ng-include="'/assets/hmdc/search/views/results.tpl.html'"></div>
	</div>

	<div class="container searchViewBox">
		<%@ include file="/WEB-INF/jsp/static/home/hmdc/resource_links.jsp" %>
	</div>

	<div class="container searchViewBox">
		<%@ include file="/WEB-INF/jsp/static/home/hmdc/research_news.jsp" %>
	</div>

</div>

</body>
</html>
