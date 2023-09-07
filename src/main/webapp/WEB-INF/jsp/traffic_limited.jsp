<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>MGI Traffic Limited</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
    
%>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper">
	<span class="titleBarMainTitle">MGI Traffic Limited</span>
</div>


<!-- structural table -->
<table class="detailStructureTable">

  <!-- ROW1 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Message
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
    	The recent MGI traffic from this IP address has exceeded our robot detection threshold.
    	If you are downloading MGI pages in an automated manner, please take care to spread out
    	your requests and not overwhelm the servers. If you believe you have received this message
    	in error, please contact User Support.
    </td>
  </tr>

<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

