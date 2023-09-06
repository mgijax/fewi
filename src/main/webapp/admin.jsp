<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>fewi admin</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
    
%>

<style type="text/css">
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="foo_help.shtml">	
	<span class="titleBarMainTitle">fewi admin</span>
</div>


<!-- structural table -->
<table class="detailStructureTable">


  <!-- ROW1 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Memory
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
	Maximum ${maxMemory} bytes allowed to JVM<BR>
	Currently ${totalMemory} bytes allocated to JVM<BR>
	<c:if test="${empty gc}">
	    ${initialFreeMemory} bytes free :
	    <A HREF="admin/gc">run garbage collector</A><BR>
	</c:if>
	<c:if test="${not empty gc}">
	    ${initialFreeMemory} bytes free initially<BR>
	    Garbage collector ran in ${elapsed} seconds<BR>
	    ${finalFreeMemory} bytes free after garbage collection :
	    <A HREF="">run garbage collector</A> again<BR>
	</c:if>
    </td>
  </tr>


  <!-- ROW2 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Processors
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${processors} available to JVM
    </td>
  </tr>

  <!-- ROW3 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Caches
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
	    N/A
    </td>
  </tr>

<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

