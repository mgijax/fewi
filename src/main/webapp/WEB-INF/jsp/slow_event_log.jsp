<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Slow Event Log</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
    
%>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper">
	<span class="titleBarMainTitle">Slow Event Log</span>
</div>

<style>
.eventTable { border-collapse: collapse; }
.eventTable tr th { background-color: #dfefff; font-weight: bold; text-align: center; border: 1px solid black; padding: 3px; }
.eventTable tr td { background-color: #ffffff; text-align: left; border: 1px solid black; padding: 3px;}
.eventTable tr td:first-child { text-align: center; }
.eventType { padding-top: 10px; font-weight: bold; padding-bottom: 5px; font-size: 1.2em; }
</style>

<c:forEach var="eventType" items="${monitor.getEventTypes()}" varStatus="status">
<div class="eventType">${eventType}</div>
<table class="eventTable">

  <!-- ROW1 -->
  <tr >
    <th>#</th>
    <th>Started</th>
    <th>Elapsed (ms)</th>
    <th>Query</th>
  </tr>

  <c:forEach var="event" items="${monitor.getSlowEvents(eventType)}" varStatus="eStatus">
    <tr>
      <td>${eStatus.index}</td>
      <td>${event.startDate}</td>
      <td style="text-align:right;">${event.elapsedTime}</td>
      <td>${event.identifier}</td>
      </td>
    </tr>
  </c:forEach>
</table>

<c:if test="${monitor.hasFailures(eventType)}">
  <div class="eventType">${eventType} Failures</div>
  <table class="eventTable">

    <!-- ROW1 -->
    <tr >
      <th>#</th>
      <th>Started</th>
      <th>Elapsed (ms)</th>
      <th>Query</th>
      <th>Failure Reason</th>
    </tr>

    <c:forEach var="event" items="${monitor.getFailedEvents(eventType)}" varStatus="fStatus">
      <tr>
        <td>${fStatus.index}</td>
        <td>${event.startDate}</td>
        <td style="text-align:right;">${event.elapsedTime}</td>
        <td>${event.identifier}</td>
        <td>${event.failure}</td>
        </td>
      </tr>
    </c:forEach>
  </table>
</c:if>

</c:forEach>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

