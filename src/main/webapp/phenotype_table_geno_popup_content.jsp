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
<div id="content" style="padding-top:5px;padding-left:5px;">
<div>

  <c:forEach var="mpSystem" items="${mpSystems}" varStatus="systemStatus">

    <div class='mpSystemRow' id="${mpSystem.cssId}">
      ${mpSystem.system}
    </div>
 
    <c:forEach var="term" items="${mpSystem.terms}" >

	<div class="termDiv" id="${mpSystem.cssId}_${term.cssId}">
      <div class="mpTerm" style="margin-left:${term.displayIndent}px;">
        <a class='MP ${fn:replace(term.termId, ":", "")}' target="_blank" href='${configBean.FEWI_URL}vocab/mp_ontology/${term.termId}'>
        ${term.term}</a>

        (
        <c:forEach var="reference" items="${term.references}" varStatus="refStatus">
          <c:if test="${refStatus.index>0}">, </c:if><a class='MP' target="_blank" href='${configBean.FEWI_URL}reference/${reference.jnumID}'>${reference.jnumID}</a>
        </c:forEach>
        ) 
	<c:set var="showRefs" value=""/>
	<c:if test="${fn:length(term.references) > 1}">
	    <c:set var="showRefs" value="1"/>
	</c:if>

      </div>
		<div class="sexDiv" style="margin-left:${term.displayIndent+10}px;">
		<table>
		<c:forEach var="annotation" items="${term.annots}" varStatus="annotStatus">

		    <c:set var="showMgi" value=""/>
		    <c:forEach var="reference" items="${annotation.references}">
		        <c:if test="${reference.hasNonMgiSource}">
			  <c:set var="showMgi" value="1"/>
			</c:if>
		    </c:forEach>

		<tr class="<c:if test="${annotStatus.index>0}">borderTop</c:if>"><td class="sexTd" rowspan="1}">
				<c:if test="${annotation.sex=='M'}"><img class="mp_glyph" src="${configBean.FEWI_URL}assets/images/Mars_symbol.svg"/></c:if>
       			<c:if test="${annotation.sex=='F'}"><img class="mp_glyph" src="${configBean.FEWI_URL}assets/images/Venus_symbol.svg"/></c:if>
        		<c:if test="${!annotation.isCall}">N</c:if></td>
        	<td>
		      <c:forEach var="reference" items="${annotation.references}" varStatus="refStatus">
		      	  <!-- This is a check to suppress certain rows from displaying -->
			      <c:if test="${!annotation.isBoth || !annotation.isCall || reference.hasNotes || reference.hasNonMgiSource || (not empty showMgi)}">
				      <div class="refSection" >
				          <c:if test="${(not empty showMgi) || reference.hasNonMgiSource}">
					  <span class="sourceDisplay" onMouseOver="return overlib('${reference.sourceDescription}', LEFT, WIDTH, 200);" onMouseOut="nd();">${reference.sourceDisplay}</span>
			       			</c:if>
				        <c:forEach var="note" items="${reference.notes}" varStatus="noteStatus">
				        	<div class="mpNote">
				            &bull; ${note.note} 
				            <!-- check to determine when to display jnumID -->
				            <c:if test="${not empty showRefs}">(${reference.jnumID})</c:if>
				            </div>
				        </c:forEach> <!-- reference.notes -->
				       <c:if test="${fn:length(reference.notes)==0 && not empty showRefs}">
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
<style>
td.outline { border: 1px solid black; }
</style>
	<c:if test="${hasDiseaseModels}" >
		<div id="diseaseModelDiv">
			<table border='1' cellpadding='3' cellspacing='0' class='results' style="border-collapse: collapse;">
				<tr class='resultsHeaderYellow'>
					<td colspan='2' class='resultsHeader outline padded' style='align:left'><div align='left'>Mouse Models of Human Disease</div></td>
					<td class='resultsHeader outline padded'>DO ID</td>
					<td class='resultsHeader outline padded'>OMIM ID(s)</td>
					<td class='resultsHeader outline padded'>Ref(s)</td>
				</tr>
				<c:forEach var="disease" items="${genotype.diseases}" varStatus="diseaseStatus">
					<tr class="${diseaseStatus.index % 2==0 ? ' stripe1' : ' stripe2'}">
						<c:choose>
							<c:when test="${disease.isNot}">
								<td class="outline padded">NOT</td>
								<td>
							</c:when>
							<c:otherwise>
								<td class="outline padded" colspan='2' rowspan='1'>
							</c:otherwise>
						</c:choose>
							<a class='MP' target="_blank" href="${configBean.FEWI_URL}disease/${disease.termID}">${disease.term}</a>
						</td>
						<td class="outline padded">
							<a class='MP' target="_blank" href="http://www.disease-ontology.org/?id=${disease.termID}">${disease.termID}</a>
						</td>
						<td class="outline padded">
							<c:forEach var="omimId" items="${disease.omimIds}">
								<a class='MP' target="_blank" href="http://www.omim.org/entry/${omimId}">OMIM:${omimId}</a><br>
							</c:forEach>
						</td>
						<td class="outline padded">
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
