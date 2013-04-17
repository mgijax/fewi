<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<% 
  boolean hasDiseases = (Boolean) request.getAttribute("hasDiseaseModels");
  boolean hasImage = (Boolean) request.getAttribute("hasImage");
%>


<!-- Primary Image -->
<c:if test="${hasImage}">
	<div style="float:right;clear:right;text-align: center; width:200px; max-width: 200px; padding-left: 4px; padding-right: 4px; margin-left: 5px;">
		<a target="_blank" href="${configBean.FEWI_URL}image/pheno/${genotype.primaryImage.mgiID}" style="text-align: center;" class="MP">
			<img src="${configBean.PIXELDB_URL}${genotype.thumbnail.pixeldbNumericID}" height="${genotype.thumbnail.height}" width="${genotype.thumbnail.width}">
		</a>
		<p>${genotype.thumbnail.caption}</p>
	</div>
</c:if>

<!-- Systems -->
<div id="content" style="padding-left:5px;">
<div>

  <c:forEach var="mpSystem" items="${mpSystems}" varStatus="systemStatus">

    <div class='mpSystemRow' id="${mpSystem.cssId}">
      ${mpSystem.system}
    </div>
 
    <c:forEach var="term" items="${mpSystem.terms}" >

	<div class="termDiv" id="${mpSystem.cssId}_${term.cssId}">
      <div class="mpTerm" style="margin-left:${term.displayIndent}px;">
        <a class='MP' target="_blank" href='${configBean.WI_URL}searches/Phat.cgi?id=${term.termId}'>
        ${term.term}</a>

        (
        <c:forEach var="reference" items="${term.references}" varStatus="refStatus">
          <c:if test="${refStatus.index>0}">, </c:if><a class='MP' target="_blank" href='${configBean.FEWI_URL}reference/${reference.jnumID}'>${reference.jnumID}</a>
        </c:forEach>
        ) 

      </div>
		<div class="sexDiv" style="margin-left:${term.displayIndent+10}px;">
		<table>
		<c:forEach var="annotation" items="${term.annots}" varStatus="annotStatus">

		<tr class="<c:if test="${annotStatus.index>0}">borderTop</c:if>"><td class="sexTd" rowspan="1}">
				<c:if test="${annotation.sex=='M'}"><img class="mp_glyph" src="${configBean.FEWI_URL}assets/images/Mars_symbol.svg"/></c:if>
       			<c:if test="${annotation.sex=='F'}"><img class="mp_glyph" src="${configBean.FEWI_URL}assets/images/Venus_symbol.svg"/></c:if>
        		<c:if test="${!annotation.isCall}">N</c:if></td>
        	<td>
		      <c:forEach var="reference" items="${annotation.references}" varStatus="refStatus">
		      	  <!-- This is a check to suppress certain rows from displaying -->
			      <c:if test="${!annotation.isBoth || !annotation.isCall || reference.hasNotes || reference.hasNonMgiSource}">
				      <div class="refSection" >
				          <c:if test="${refStatus.index==0 && reference.hasNonMgiSource}">
			       				 <span class="sourceDisplay">${reference.sourceDisplay}</span>
			       			</c:if>
				        <c:forEach var="note" items="${reference.notes}" varStatus="noteStatus">
				        	<div class="mpNote">
				            &bull; ${note.note} 
				            <!-- check to determine when to display jnumID -->
				            <c:if test="${fn:length(term.references)>1 || annotation.hasNonEmptyGlyph }">(${reference.jnumID})</c:if>
				            </div>
				        </c:forEach> <!-- reference.notes -->
				       <c:if test="${fn:length(reference.notes)==0}">
				      	<span class="mpNote">(${reference.jnumID})</span>
				      	</c:if>
			      </div>
			      
			      </c:if> <!-- should we display this ref -->
		      </c:forEach> <!-- annotation.references --> 
		      </td>
      	</tr>
    	</c:forEach> <!-- term.sexes -->
    	</table>
     	</div>
	</div>
    </c:forEach> <!-- mpSystems.terms --> 
    <br/>
  </c:forEach> <!-- mpSystems -->  


</div>

<!-- Disease Models -->
<c:if test="${hasDiseaseModels}" >
	<div id="diseaseModelDiv">
		<table border='1' cellpadding='3' cellspacing='0' class='results'>
		<tr class='resultsHeaderYellow'>
			<td colspan='2' class='resultsHeader' style='align:left'><div align='left'>Mouse Models of Human Disease</div></td>
			<td class='resultsHeader'>OMIM ID</td><td class='resultsHeader'>Ref(s)</td>
		</tr>
		<c:forEach var="disease" items="${genotype.diseases}" varStatus="diseaseStatus">
			<tr class="${diseaseStatus.index % 2==0 ? ' stripe1' : ' stripe2'}">
				<c:choose>
				<c:when test="${disease.isNot}">
				<td>NOT</td><td>
				</c:when>
				<c:otherwise>
				<td colspan='2' rowspan='1'>
				</c:otherwise>
				</c:choose>
				<a class='MP' target="_blank" href="${configBean.FEWI_URL}disease/${disease.termID}">${disease.term}</a></td>
				<td>
					<a class='MP' target="_blank" href="http://www.omim.org/entry/${disease.termID}">${disease.termID}</a>
				</td>
				<td>
				<c:forEach var="reference" items="${disease.references}" varStatus="refStatus">
	          		<c:if test="${refStatus.index>0}">, </c:if><a class='MP' target="_blank" href='${configBean.FEWI_URL}reference/${reference.jnumID}'>${reference.jnumID}</a>
	        	</c:forEach>
	        	</td>
        	</tr>
		</c:forEach>
		</table>
		<br/>
	</div>
</c:if>
</div>
