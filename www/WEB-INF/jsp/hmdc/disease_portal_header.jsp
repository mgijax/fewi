<!DOCTYPE html>
<!--===================== Template Head: disease_portal_header.jsp ===========================-->

<html ng-app="civicClient" ng-controller="StyleController">
	<head>

		<!-- import jquery UI specifically for this page -->
		<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/hmdc/bower_components/bootstrap/dist/css/bootstrap.css" />
		<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/hmdc/bower_components/bootstrap/spacelab/bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/hmdc/bower_components/ng-dialog/css/ngDialog.min.css" />
		<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/hmdc/bower_components/ng-dialog/css/ngDialog-theme-default.css" />
		<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/hmdc/bower_components/ng-cells/dist/0.4.0/ng-cells.css" />
		<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/css/hmdc/search.css" />

		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular/angular.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-route/angular-route.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/api-check/dist/api-check.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-formly/dist/formly.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-bootstrap/ui-bootstrap-tpls.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-resource/angular-resource.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-sanitize/angular-sanitize.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-smart-table/dist/smart-table.min.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/ng-dialog/js/ngDialog.min.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/ng-cells/dist/0.4.0/ng-cells.min.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/bower_components/angular-formly-templates-bootstrap/dist/angular-formly-templates-bootstrap.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/search/index.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/search/forms/formConfig.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/search/forms/fieldWrappers/basicFieldWrappers.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/search/forms/fieldTypes/basicFieldTypes.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/search/forms/fieldTypes/multiInput.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/search/services/SearchService.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/search/services/NaturalSort.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/search/views/StyleController.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/search/views/SearchController.js"></script>
		<script src="${configBean.FEWI_URL}/assets/hmdc/search/forms/queryBuilder.type.js"></script>

		<meta http-equiv="X-UA-Compatible" content="chrome=1">

