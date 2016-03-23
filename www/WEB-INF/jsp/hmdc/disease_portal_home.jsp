<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="disease_portal_header.jsp" %>

<title>Human - Mouse Disease Connection</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%@ include file="disease_portal_bodystart.jsp" %>

<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" name="yui-history-iframe" src="${configBean.FEWI_URL}blank.html"></iframe>
<input id="yui-history-field" name="yui-history-field" type="hidden">

<div ng-app="civicClient" ng-controller="SearchController">
	<div class="container">

		<div style="border: 0px;margin-bottom:30px;">
			<div style="text-align: center;">
				<a href="${configBean.FEWI_URL}/diseasePortal" style="font-size:45px;color:black;">Human <img src="${configBean.WEBSHARE_URL}images/hmdc_arrow.png" style='height:42px;'> Mouse: Disease Connection</a>
			</div>
		</div>

		<label>Hide the query form: <input type="checkbox" ng-model="mustHide" /></label><br />
		<div class="animate-show" ng-hide="mustHide">
			<ui-view></ui-view>
		</div>

	</div>

	<div style="display:none;" class="searchViewBox" ng-style="loaded" ng-hide="emptyResults">
		<div id="resultbar" class="bluebar" style="background-color: #ffdab3;">Results</div>
		<%@ include file="/WEB-INF/jsp/hmdc/disease_portal_summary.jsp" %>
	</div>
	  
	<div class="container">
		<div class="searchViewBox">
			<%@ include file="/WEB-INF/jsp/static/home/hmdc/resource_links.jsp" %>
		</div>
		<div class="searchViewBox">
			<%@ include file="/WEB-INF/jsp/static/home/hmdc/research_news.jsp" %>
		</div>
	</div>
</div>

<script type="text/javascript">
    var fewiurl = "http://www.informatics.jax.org/";
    var assemblyBuild = "GRCm38";
</script>

<c:set var="jsHome" value="${configBean.FEWI_URL}assets/js"/>
<script type="text/javascript" src="${jsHome}/fewi_utils.js"></script>
<script type="text/javascript" src="${jsHome}/filters.js"></script>
<script type="text/javascript" src="${jsHome}/disease_portal_filters.js"></script>
<!--
<script type="text/javascript" src="${jsHome}/disease_portal_query.js"></script>
-->

<%    
   String queryString = (String) request.getAttribute("querystring");
   // need to url encode the querystring
   request.setAttribute("encodedQueryString", FormatHelper.encodeQueryString(queryString));
%>

<script type="text/javascript">
   var querystring = "${encodedQueryString}";
</script>
<!--
<script type="text/javascript" src="${jsHome}/disease_portal_summary.js"></script>
-->

<script type="text/javascript">
   hmdcFilters.prepFilters('${configBean.FEWI_URL}');
</script>


<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular/angular.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-ui-router/release/angular-ui-router.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/api-check/dist/api-check.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-formly/dist/formly.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-bootstrap/ui-bootstrap-tpls.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-resource/angular-resource.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-formly-templates-bootstrap/dist/angular-formly-templates-bootstrap.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/search/index.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/search/forms/formConfig.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/search/forms/fieldWrappers/basicFieldWrappers.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/search/forms/fieldTypes/basicFieldTypes.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/search/forms/fieldTypes/multiInput.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/search/services/SearchService.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/search/views/SearchController.js"></script>
<script src="${configBean.FEWI_URL}/assets/hmdc/search/forms/queryBuilder.type.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
