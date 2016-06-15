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
							<div class="button-row pull-right">
								<span class="text-danger" ng-if="vm.form.$pristine === false && vm.form.$invalid">
									Ensure all fields are specified to submit your search.
								</span>
								<button
									type="submit"
									class="btn btn-primary submit-button"
									ng-disabled="vm.form.$invalid"
									ng-click="vm.onSubmit()"
									ng-bind="vm.buttonLabel">
									BUTTON LABEL
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="container-fluid searchViewBox" ng-show="vm.results" resize>
				<uib-tabset>
					<uib-tab ng-repeat="(key, value) in vm.tabs" heading="{{ value.count != 0 ? value.heading + ' (' + value.count + ')' : value.heading }}" active="value.active">
						<div ng-include src="value.template"></div>
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
