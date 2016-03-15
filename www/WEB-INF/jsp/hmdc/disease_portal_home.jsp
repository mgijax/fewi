<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Human - Mouse Disease Connection</title>

<meta http-equiv="X-UA-Compatible" content="chrome=1">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!-- import jquery UI specifically for this page -->
<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/disease_portal.css" />

<%@ include file="/WEB-INF/jsp/templates/templateHdpBodyStart.html" %>

<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" name="yui-history-iframe" src="${configBean.FEWI_URL}blank.html"></iframe>
<input id="yui-history-field" name="yui-history-field" type="hidden">

<div ng-app="civicClient">
	<ui-view></ui-view>
</div>

<script src="bower_components/angular/angular.js"></script>
<script src="bower_components/angular-ui-router/release/angular-ui-router.js"></script>
<script src="bower_components/api-check/dist/api-check.js"></script>
<script src="bower_components/angular-formly/dist/formly.js"></script>
<script src="bower_components/angular-bootstrap/ui-bootstrap-tpls.js"></script>
<script src="bower_components/angular-resource/angular-resource.js"></script>
<script src="bower_components/angular-formly-templates-bootstrap/dist/angular-formly-templates-bootstrap.js"></script>
<script src="search/index.js"></script>
<script src="search/forms/formConfig.js"></script>
<script src="search/forms/fieldWrappers/basicFieldWrappers.js"></script>
<script src="search/forms/fieldTypes/basicFieldTypes.js"></script>
<script src="search/forms/fieldTypes/multiInput.js"></script>
<script src="search/services/SearchService.js"></script>
<script src="search/views/SearchController.js"></script>
<script src="search/forms/queryBuilder.type.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
