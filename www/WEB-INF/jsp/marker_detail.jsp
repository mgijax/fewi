<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>${marker.symbol} Detail</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker marker = (Marker)request.getAttribute("marker");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
    
%>

<style type="text/css">
</style>

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="marker_help.shtml">	
	<span class="titleBarMainTitle">${marker.symbol} Detail</span>
</div>


<!-- structural table -->
<table class="detailStructureTable">


  <!-- ROW1 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Symbol
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${marker.symbol}
    </td>
  </tr>


  <!-- ROW2 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Name
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${marker.name}
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
${templateBean.templateBodyStopHtml}
