<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>MGI Traffic Report</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
    
%>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper">
	<span class="titleBarMainTitle">MGI Traffic Report</span>
</div>

<style>
#trafficTable { border-collapse: collapse; }
#trafficTable tr th { background-color: #dfefff; font-weight: bold; text-align: center; border: 1px solid black; padding: 3px; }
#trafficTable tr td { background-color: #ffffff; text-align: left; border: 1px solid black; padding: 3px;}
#trafficTable tr td:first-child { text-align: center; }
</style>
<!-- structural table -->
<table id="trafficTable">

  <!-- ROW1 -->
  <tr >
    <th>
      User (based on unique IP)
    </th>
    <th>
      Hit times (ms)
    </th>
  </tr>

  <c:forEach var="ipAddress" items="${monitor.getIPs()}" varStatus="status">
    <tr>
      <td>${status.index}</td>
      <td>
      	<c:forEach var="hit" items="${monitor.getHits(ipAddress)}" varStatus="status">
      		${hit}<c:if test="${!status.last}">, </c:if>
      	</c:forEach>
      </td>
    </tr>
  </c:forEach>
<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

