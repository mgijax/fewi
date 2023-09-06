<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.NotesTagConverter" %>
<%@ page import = "org.jax.mgi.fewi.config.ContextLoader" %>
<%@ page import = "org.jax.mgi.fewi.util.DiseaseModelFilter" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.HashMap" %>
<%@ page import = "java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  
// Pull detail object into servlet scope
DiseaseRow diseaseRow = (DiseaseRow) request.getAttribute("diseaseRow");

String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
NotesTagConverter ntc = new NotesTagConverter();

%>

<style type="text/css">

body {
  margin:0px;
}

#diseaseBrowserModelsPopup {
  font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;
  font-size:12px;
  background-color: #f5f5f5;
  padding:12px;
  width:100%;
  height:100%;
}

#diseaseBrowserModelsPopup  table, th, td {
  border: 1px solid black;
  padding: 2px;
  font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;
  font-size:12px;
  border-collapse: collapse;
}

#diseaseBrowserModelsPopup th {
  background-color: #DFEFFF;
}

#diseaseBrowserModelPopupTable {
}

.bold {
  font-weight: bold;
}

a {text-decoration: none;}

</style>

<body>
<div id="diseaseBrowserModelsPopup">

<span class="bold">Human Disease Modeled:</span> <span id="diseaseDisplay">${disease}</span>

<c:if test="${not empty diseaseRow.causativeMouseMarkers}">
  <br/>
  <span class="bold">Associated Mouse Gene:</span> <span id="markerDisplay">
  <c:forEach var="marker" items="${diseaseRow.causativeMouseMarkers}">
	<a href="${configBean.FEWI_URL}marker/${marker.primaryID}" target="_blank">
		${marker.symbol}
	</a><c:if test="${!status.last}"><br/></c:if>
  </c:forEach>
  </span>
</c:if>


<!-- Positive Models -->
<c:if test="${not empty diseaseRow.mouseModels}">

<table id="diseaseBrowserModelPopupTable">

<tr>
	<th>Allelic Composition</th>
	<th>Genetic Background</th>
	<th>Reference</th>
	<th>Phenotypes</th>
</tr>

<c:forEach var="model" items="${diseaseRow.mouseModels}">
<% DiseaseModel model = (DiseaseModel)pageContext.getAttribute("model"); %>

<tr>
	<td> 
		<%=FormatHelper.newline2HTMLBR(ntc.useNewWindows(ntc.convertNotes(model.getAllelePairs(), '|')))%>
	</td>
	<td>
		<c:choose>
			<c:when test="${not empty model.genotype.strainID}">
				<a href="${configBean.FEWI_URL}strain/${model.genotype.strainID}" target="_blank"><fewi:super value="${model.backgroundStrain}"/></a>
			</c:when>
			<c:otherwise>
				<%=FormatHelper.superscript(model.getBackgroundStrain())%>
			</c:otherwise>
		</c:choose>
	</td>
	<td> 
		<c:forEach var="ref" items="${model.references}" varStatus="status">
			<a href="${configBean.FEWI_URL}reference/${ref.jnumID}" target="_blank">
			${ref.jnumID}
			</a><c:if test="${!status.last}"><br/></c:if>
		</c:forEach>	
	</td>
	<td> 
      <a href="${configBean.FEWI_URL}allele/genoview/${model.genotypeID}" target="_blank">View</a>
	</td>
	</tr>
</c:forEach>

</table>
</c:if>

<br/>
<br/>

<!-- NOT Models -->
<c:if test="${not empty diseaseRow.notModels}">

<span class="bold">NOT Models</span><br/>
No similarity to the expected human disease phenotype was found.

<table id="diseaseBrowserNotModelPopupTable">

<tr>
	<th>Allelic Composition</th>
	<th>Genetic Background</th>
	<th>Reference</th>
	<th>Phenotypes</th>
</tr>

<c:forEach var="model" items="${diseaseRow.notModels}">
<% DiseaseModel model = (DiseaseModel)pageContext.getAttribute("model"); %>

<tr>
	<td> 
		<%=FormatHelper.newline2HTMLBR(ntc.convertNotes(model.getAllelePairs(), '|'))%>
	</td>
	<td>
		<c:choose>
			<c:when test="${not empty model.genotype.strainID}">
				<a href="${configBean.FEWI_URL}strain/${model.genotype.strainID}" target="_blank"><fewi:super value="${model.backgroundStrain}"/></a>
			</c:when>
			<c:otherwise>
				<%=FormatHelper.superscript(model.getBackgroundStrain())%>
			</c:otherwise>
		</c:choose>
	</td>
	<td> 
		<c:forEach var="ref" items="${model.references}" varStatus="status">
			<a href="${configBean.FEWI_URL}reference/${ref.jnumID}" target="_blank">
			${ref.jnumID}
			</a><c:if test="${!status.last}"><br/></c:if>
		</c:forEach>	
	</td>
	<td> 
      <a href="${configBean.FEWI_URL}allele/genoview/${model.genotypeID}" target="_blank">View</a>
	</td>
	</tr>
</c:forEach>

</table>
</c:if>








</div>
</body>