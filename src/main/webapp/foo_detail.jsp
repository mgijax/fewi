<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Foo Detail</title>

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
	<span class="titleBarMainTitle">Foo Detail</span>
</div>


<!-- structural table -->
<table class="detailStructureTable">


  <!-- ROW1 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Category 1
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      foo data
    </td>
  </tr>


  <!-- ROW2 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Category 2
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      foo data
    </td>
  </tr>


  <!-- ROW3 - Dynamic row; example of general existance test, and looping. -->
  <!-- The 'not empty' test first looks for the existance of 'references', -->
  <!-- ensures it's not null, and ensures it's not empty.  Only then, -->
  <!-- will it render this section.  Also, an example of a for-each loop. -->

  <c:if test="${not empty references}">

    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
        References
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >

        <c:forEach var="ref" items="${references}" >
          ${ref.jnumID} <br/>
        </c:forEach>
    
      </td>
    </tr>
 
  </c:if>


<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
