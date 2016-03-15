<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHeadNoReset.html" %>

<title>Human - Mouse Disease Connection</title>

<meta http-equiv="X-UA-Compatible" content="chrome=1">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!-- import jquery UI specifically for this page -->
<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/disease_portal.css" />
<link rel="stylesheet" type="text/css" href="/assets/css/hmdc/hmdc.css" />
<link rel="stylesheet" type="text/css" href="/assets/css/hmdc/search.css" />
<link rel="stylesheet" type="text/css" href="/assets/css/hmdc/queryBuilder.type.css" />

<%@ include file="/WEB-INF/jsp/templates/templateHdpBodyStart.html" %>

<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" name="yui-history-iframe" src="${configBean.FEWI_URL}blank.html"></iframe>
<input id="yui-history-field" name="yui-history-field" type="hidden">

<div ng-app="civicClient" class="container">
	<div class="searchViewBox" style="border: 0px;margin-bottom:30px;">
		<div style="text-align: center;">
			<a href="http://localhost.jax.org/diseasePortal" style="font-size:45px;color:black;">Human <img src="http://localhost.jax.org/webshare/images/hmdc_arrow.png" style='height:42px;'> Mouse: Disease Connection</a>
		</div>
	</div>
	
	<ui-view></ui-view>
	<div class="searchViewBox">
		<%@ include file="/WEB-INF/jsp/static/home/hmdc/resource_links.jsp" %>
	</div>
	<div class="searchViewBox">
		<%@ include file="/WEB-INF/jsp/static/home/hmdc/research_news.jsp" %>
	</div>
</div>

<script src="/assets/hmdc/bower_components/angular/angular.js"></script>
<script src="/assets/hmdc/bower_components/angular-ui-router/release/angular-ui-router.js"></script>
<script src="/assets/hmdc/bower_components/api-check/dist/api-check.js"></script>
<script src="/assets/hmdc/bower_components/angular-formly/dist/formly.js"></script>
<script src="/assets/hmdc/bower_components/angular-bootstrap/ui-bootstrap-tpls.js"></script>
<script src="/assets/hmdc/bower_components/angular-resource/angular-resource.js"></script>
<script src="/assets/hmdc/bower_components/angular-formly-templates-bootstrap/dist/angular-formly-templates-bootstrap.js"></script>
<script src="/assets/hmdc/search/index.js"></script>
<script src="/assets/hmdc/search/forms/formConfig.js"></script>
<script src="/assets/hmdc/search/forms/fieldWrappers/basicFieldWrappers.js"></script>
<script src="/assets/hmdc/search/forms/fieldTypes/basicFieldTypes.js"></script>
<script src="/assets/hmdc/search/forms/fieldTypes/multiInput.js"></script>
<script src="/assets/hmdc/search/services/SearchService.js"></script>
<script src="/assets/hmdc/search/views/SearchController.js"></script>
<script src="/assets/hmdc/search/forms/queryBuilder.type.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
