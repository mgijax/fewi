<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="header.jsp" %>
<title>Human - Mouse Disease Connection</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<base href="${configBean.FEWI_URL}diseasePortal"/>

<%@ include file="bodystart.jsp" %>

		<div class="container">
			<div style="border: 0px;margin-bottom:10px;text-align: center;padding-top: 1px;">
				<span style="font-size:30px;color:black;">Human - Mouse: Disease Connection</span>
			</div>
		</div>
		<div ng-include="'${configBean.FEWI_URL}/assets/hmdc/app/components/search/searchTemplate.html'"></div>

		<div class="container searchViewBox">
			<div class="row">
				<div class="col-sm-12">
					<%@ include file="/WEB-INF/jsp/static/home/hmdc/summary_box.jsp" %>
				</div>
			</div>
		</div>

		<div class="container searchViewBox">
			<div class="row">
                            <div style="padding: 16px;">
                                <%@ include file="/WEB-INF/jsp/static/home/hmdc/resource_links.jsp" %>
                            </div>
			</div>
		</div>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
