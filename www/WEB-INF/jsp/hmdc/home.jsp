<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="header.jsp" %>

<title>Human - Mouse Disease Connection</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="bodystart.jsp" %>

		<div class="container">
			<div style="border: 0px;margin-bottom:10px;text-align: center;">
				<span style="font-size:30px;color:black;">Human - Mouse: Disease Connection</span>
			</div>
		</div>

		<div ng-include="'/assets/hmdc/app/components/search/searchTemplate.html'"></div>

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

	</body>
</html>
