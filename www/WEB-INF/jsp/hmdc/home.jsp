<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="header.jsp" %>

<title>Human - Mouse Disease Connection</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="bodystart.jsp" %>

		<div ng-controller="SearchController">

			<div class="container">
				<div style="border: 0px;margin-bottom:10px;text-align: center;">
					<span style="font-size:30px;color:black;">Human - Mouse: Disease Connection</span>
				</div>
			</div>

			<div class="container searchView">
				<div ng-include="'/assets/hmdc/search/views/search.tpl.html'"></div>
			</div>

			<div class="container-fluid searchViewBox" ng-show="vm.results" resize>
				<div ng-include="'/assets/hmdc/search/views/results.tpl.html'"></div>
			</div>

			<div class="container searchViewBox">
				<div class="row">
					<div class="col-sm-6">
						<%@ include file="/WEB-INF/jsp/static/home/hmdc/resource_links.jsp" %>
					</div>
					<div class="col-sm-6">
						<%@ include file="/WEB-INF/jsp/static/home/hmdc/research_news.jsp" %>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
