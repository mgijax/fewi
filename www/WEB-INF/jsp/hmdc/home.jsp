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
				<div class="queryBuilder">

					<div class="row hideRow">
						<div class="col-xs-12">
							<span class="buttonStyle" ng-hide="vm.mustHide" ng-click="vm.mustHide = !vm.mustHide">Click to hide search</span>
							<span class="buttonStyle" ng-show="vm.mustHide" ng-click="vm.mustHide = !vm.mustHide">Click to modify search</span>
						</div>
					</div>

					<div class="row searchRow" ng-hide="vm.mustHide">
						<div class="col-xs-12">
							<h3>Search by:</h3>
						</div>
					</div>

					<div class="row" ng-hide="vm.mustHide">
						<div class="col-xs-12">
							<form ng-submit="vm.onSubmit()" novalidate>
								<formly-form
									model="vm.model"
									fields="vm.fields"
									form="vm.form"
									options="vm.options">
								</formly-form>
							</form>
						</div>
					</div>

					<div ng-if="vm.model.queries.length > 1">
						<div class="row" ng-hide="vm.mustHide">
							<div class="col-xs-12">
								<div class="operator-prompt">
									<div class="operator-select">
										<formly-form fields="vm.operatorField" model="vm.model"></formly-form>
									</div>
									<span class="prompt-text"> the above conditions together</span>
								</div>
							</div>
						</div>
					</div>

					<div class="row" ng-hide="vm.mustHide">
						<div class="col-xs-12">
							<div class="button-row pull-left">
								<button
									type="submit"
									class="btn btn-primary submit-button"
									ng-disabled="vm.form.$invalid"
									ng-click="vm.onSubmit()"
									ng-bind="vm.buttonLabel">
									BUTTON LABEL
								</button>
								<span class="text-danger" ng-if="vm.form.$pristine === false && vm.form.$invalid">
									Ensure all fields are specified to submit your search.
								</span>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="container-fluid searchViewBox" ng-show="displayTabs">

				<div class="btn-group pad30left" ng-dropdown-multiselect="" options="selectedPhenoTypes" selected-model="selectedPhenoTypesModel" translation-texts="selectPhenoTypesCustemText" events="handleEvents"></div>

				<div class="btn-group pad30left" ng-dropdown-multiselect="" options="selectedDiseases" selected-model="selectedDiseasesModel" translation-texts="selectDiseasesCustemText" events="handleEvents"></div>

				<div class="btn-group pad30left" uib-dropdown>
					<button type="button" uib-dropdown-toggle>
						Gene Filter
						<span class="caret"></span>
					</button>
					<ul class="dropdown-menu genefiltermenu">
						<li ng-repeat="(term, item) in selectedGenes">
							<label><input type="checkbox" ng-model="item.selected" ng-click="filterChanged()">{{ item.term }}</label>
						</li>
					</ul>
				</div>
				<br><br>
				<uib-tabset>
					<uib-tab ng-repeat="tab in vm.tabs" heading="{{ tab.count != 0 ? tab.heading + ' (' + tab.count + ')' : tab.heading }}" active="tab.active">
						<div ng-include src="tab.template"></div>
					</uib-tab>
				</uib-tabset>
			</div>

			<div class="container searchViewBox">
				<div class="row">
					<div class="col-sm-6">
						<div class="mycontent-left">
							<%@ include file="/WEB-INF/jsp/static/home/hmdc/resource_links.jsp" %>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="mycontent-right">
							<%@ include file="/WEB-INF/jsp/static/home/hmdc/research_news.jsp" %>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
