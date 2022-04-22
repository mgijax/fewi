<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<c:choose>
  <c:when test="${empty alleleSystems}">
    <span>MGI has not yet included tissue activity data for this allele in any anatomical systems.</span>
  </c:when>
  <c:otherwise>

<%
NotesTagConverter ntc = new NotesTagConverter(); 
%>

<!-- Pull in the PrettyGoodGrid -->
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/pgg.css">
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/pgg.js"></script>

<!-- A place to draw the table -->
<div id="alleleSystemTableWrapper"></div>

<script type="text/javascript">

 var columnData = [
     'Activity in Systems/Structures',
     'E 0-8.9',
     'E 9.0-13.9',
     'E 14-19.5',
     'P 0-21',
     'Post-weaning',
     'Adult'
 ]
 var recombinaseTableRowData = [] 
 var alleleSystem = null

 <c:forEach var="alleleSystem" items="${alleleSystems}" >
  alleleSystem = {
      name: '${alleleSystem.system}',
      cellData: JSON.parse('${alleleSystem.cellData}'),
      children: []
  }
  <c:forEach var="structure" items="${alleleSystem.recombinaseSystemStructures}">
      alleleSystem.children.push({
          name: '${structure.structure}',
          cellData: JSON.parse('${structure.cellData}')
      })
  </c:forEach>
  recombinaseTableRowData.push(alleleSystem)
 </c:forEach>

 console.log("Recombinase table data:", recombinaseTableRowData)
 pgg.renderTable ("alleleSystemTableWrapper", recombinaseTableRowData, columnData)

</script>


  </c:otherwise>
</c:choose>


