<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.NotesTagConverter" %>
<%@ page import = "org.jax.mgi.fewi.util.ImageUtils" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%
  NotesTagConverter ntc = new NotesTagConverter();
%>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/assay_detail_shared_content.jsp" %>

<%
  // iterate to avoid incorrect right-style
  String tmp = rightTdStyles.getNext();
%>

<script>
function toggleSpecimenInfo(idToHide, idToShow) {
    $(idToHide).hide();
    $(idToShow).show();
}
</script>


<!-- --------------------- Result Details --------------------- -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Results
    </td>
    <td class="<%=rightTdStyles.getNext() %>">

    <div style='padding:4px;'>

    <c:forEach var="assaySpecimen" items="${assay.assaySpecimens}" varStatus="specCount">
	<!-- hook for anchor link --><div id="${assaySpecimen.cssId}"></div>
		<!-- --------------------- Image Panes --------------------- -->
		<div id="specImagePanes${specCount.index}" style="padding:2px;">
			<c:forEach var="imagepane" items="${assaySpecimen.imagePanes}" varStatus="paneCount">
				<c:if test="${not empty imagepane.image.pixeldbNumericID}">
				<div id="imagePaneDiv${paneCount.index}" class="imagePaneWrapper">
					<a href="${configBean.FEWI_URL}image/${imagepane.image.mgiID}">
					<% ImagePane imagepane = (ImagePane) pageContext.getAttribute("imagepane"); %>
		            <%= ImageUtils.createImagePaneHTML(imagepane,300,250) %>
			      	<%= FormatHelper.superscript(imagepane.getCombinedLabel()) %></a>
		            <a href="${configBean.FEWI_URL}image/${imagepane.image.mgiID}#copyright" class="copySymbol">&copy;</a>
		        </div>
		        </c:if>
			</c:forEach>
		</div>
	
      <c:set var="genotype" value="${assaySpecimen.genotype}" scope="request"/>
      <% 
	    // unhappily resorting to scriptlet
	    String allCompNoBR = new String();
        String allCompWithBR = new String();
		Genotype genotype = (Genotype)request.getAttribute("genotype"); 
	    if ( (genotype != null) && (genotype.getCombination1() != null) ) {
          String allComp = genotype.getCombination1().trim();
          allComp = ntc.convertNotes(allComp, '|');
          // add semicolon as age text separator
          allCompNoBR = "; "+FormatHelper.replaceNewline(allComp.replace("\"", "'"),", ");
          allCompWithBR = FormatHelper.newline2HTMLBR(allComp.replace("\"", "'"));
        }
	  %>

      <!-- Default Specimen Info (not hidden) -->
      <div id='defaultSpecInfo${specCount.index}' style='padding:2px;'>

        <div style='padding-botton:3px;'>
        <span style='font-weight:bold;'>Specimen 
          <span style='' id='specimenLabel'>
          	<% AssaySpecimen assaySpecimen = (AssaySpecimen) pageContext.getAttribute("assaySpecimen"); %>
	      	<%= FormatHelper.superscript(assaySpecimen.getSpecimenLabel()) %>:</span>
        </span> 
        ${assaySpecimen.age}<%=allCompNoBR%> 
        <span class="specimenOpen" onClick='toggleSpecimenInfo(defaultSpecInfo${specCount.index}, hiddenSpecInfo${specCount.index} );'>
          (more <img src='http://www.informatics.jax.org/webshare/images/rightArrow.gif' />)
        </span>          

        <c:if test="${not empty assaySpecimen.specimenNote}">
          <br/>
          <span class='' >
            Note: <%= ntc.convertNotes(FormatHelper.superscript(assaySpecimen.getSpecimenNote()) , '|') %>
          </span>
        </c:if>
        </div>

      </div>      

      <!-- Hidden Specimen Info -->
      <div id='hiddenSpecInfo${specCount.index}' style='padding:2px; display: none;'>

        <div style='padding-botton:3px;'>
        <span style='font-weight:bold;'>Specimen 
          <span style='' id='specimenLabel'>${assaySpecimen.specimenLabel}:</span>
        </span> 
        <span class="specimenClose" style='padding-left:10px;'
          onClick='toggleSpecimenInfo(hiddenSpecInfo${specCount.index}, defaultSpecInfo${specCount.index} );'>
          (close <img src='http://www.informatics.jax.org/webshare/images/downArrow.gif' />)
        </span>          

        </div>

        <table class='assayData'>

        <c:if test="${not empty assaySpecimen.genotype.backgroundStrain}">
        <tr>
         <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
           <span class='label'>Genetic Background:</span>
         </td>
         <td>
           <span class='' >
    	    <c:set var="backgroundStrain" value="${assaySpecimen.genotype.backgroundStrain}" scope="request"/>
            <% String backgroundStrain = (String)request.getAttribute("backgroundStrain"); %>
	      	<%= FormatHelper.superscript(backgroundStrain) %> 
           </span>
         </td>
        </tr>
        </c:if>

        <c:if test="${not empty assaySpecimen.genotype.combination1}">
        <tr>
         <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
           <span class='label'>Mutant Allele(s):</span>
         </td>
         <td>
           <span class='' >
             <%=allCompWithBR%>
           </span>
         </td>
        </tr>
        </c:if>

        <c:if test="${not empty assaySpecimen.age}">
        <tr>
         <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
           <span class='label'>Age:</span>
         </td>
         <td>
           <span class='' >
             ${assaySpecimen.age}
           </span>
         </td>
        </tr>
        </c:if>
  
        <c:if test="${not empty assaySpecimen.ageNote}">
        <tr>
         <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
           <span class='label'>Age Note:</span>
         </td>
         <td>
           <span class='' >
             ${assaySpecimen.ageNote}
           </span>
         </td>
        </tr>
        </c:if>

        <c:if test="${not empty assaySpecimen.sex}">
        <tr>
         <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
           <span class='label'>Sex:</span>
         </td>
         <td>
           <span class='' >
             ${assaySpecimen.sex}
           </span>
         </td>
        </tr>
        </c:if>

        <c:if test="${not empty assaySpecimen.hybridization}">
        <tr>
         <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
           <span class='label'>Type:</span>
         </td>
         <td>
           <span class='' >
             ${assaySpecimen.hybridization}
           </span>
         </td>
        </tr>
        </c:if>

        <c:if test="${not empty assaySpecimen.fixation}">
        <tr>
         <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
           <span class='label'>Fixation:</span>
         </td>
         <td>
           <span class='' >
             ${assaySpecimen.fixation}
           </span>
         </td>
        </tr>
        </c:if>

        <c:if test="${not empty assaySpecimen.embeddingMethod}">
        <tr>
         <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
           <span class='label'>Embedding:</span>
         </td>
         <td>
           <span class='' >
             ${assaySpecimen.embeddingMethod}
           </span>
         </td>
        </tr>
        </c:if>

        <c:if test="${not empty assaySpecimen.specimenNote}">
        <tr>
         <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
           <span class='label'>Note:</span>
         </td>
         <td>
           <span class='' >
            <%= ntc.convertNotes(FormatHelper.superscript(assaySpecimen.getSpecimenNote()) , '|') %>
           </span>
         </td>
        </tr>
        </c:if>
      
        </table>
        </div>      

        <table style='' id='assayResultTable'>
          <tr BGCOLOR="#E0E0E0">
          <th style='padding:4px;' align=left>Structure</th>
          <c:if test="${assaySpecimen.hasCellTypeData}">				<!-- include cell-type data if available -->
 	        <th style='padding:4px;' align=left>Cell Type</th>
          </c:if>
          <th style='padding:4px;' align=left>Level</th>
          <th style='padding:4px;' align=left>Pattern</th>
          <th style='padding:4px; max-width:20em;' align=left>Image</th>
          <th style='padding:4px;' align=left>Note</th>
          </tr>
  
          <c:forEach var="specimenResult" items="${assaySpecimen.specimenResults}" >

			<c:set var="structureLink" value="${configBean.FEWI_URL}vocab/gxd/anatomy/${specimenResult.structureTerm.primaryId}"/>
			
            <c:if test="${not assaySpecimen.hasCellTypeData}">				<!-- no cell-type data, so omit column -->
              <tr>
              <td style=''><a href="${structureLink}">${specimenResult.structure}</a></td>
              <td style=''>${specimenResult.level}</td>
              <td style=''>${specimenResult.pattern}</td>
              <td style=''>
                <c:forEach var="imagepane" items="${specimenResult.imagepanes}" varStatus="istatus">
				  <% ImagePane imagepane = (ImagePane) pageContext.getAttribute("imagepane"); %>
                  <c:if test="${istatus.index>0 }">, </c:if>
                  <c:choose>
	                <c:when test="${not empty imagepane.image.pixeldbNumericID}">
		                <a href='${configBean.FEWI_URL}image/${imagepane.image.mgiID}'>
		                <%= FormatHelper.superscript(imagepane.getCombinedLabel()) %></a>
	                </c:when>
	                <c:otherwise>
	                	<%= FormatHelper.superscript(imagepane.getCombinedLabel()) %>
	                </c:otherwise>
                  </c:choose>
                </c:forEach>
              </td>
              <td style=''>
            	<c:if test="${not empty specimenResult.note }">
	          		<% SpecimenResult specimenResult = (SpecimenResult) pageContext.getAttribute("specimenResult"); %>
			      	<%= FormatHelper.formatVerbatim(specimenResult.getNote()) %>
		      	</c:if>
              </td>
              </tr>
            </c:if>

            <c:if test="${assaySpecimen.hasCellTypeData}">				<!-- has cell-type data, so include column -->
              <c:forEach var="cellType" items="${specimenResult.cellTypes}">
                <tr>
                <td style=''><a href="${structureLink}">${specimenResult.structure}</a></td>
                <td style=''>${cellType.cellType}</td>
                <td style=''>${specimenResult.level}</td>
                <td style=''>${specimenResult.pattern}</td>
                <td style=''>
                  <c:forEach var="imagepane" items="${specimenResult.imagepanes}" varStatus="istatus">
				    <% ImagePane imagepane = (ImagePane) pageContext.getAttribute("imagepane"); %>
                    <c:if test="${istatus.index>0 }">, </c:if>
                    <c:choose>
	                  <c:when test="${not empty imagepane.image.pixeldbNumericID}">
		                <a href='${configBean.FEWI_URL}image/${imagepane.image.mgiID}'>
		                  <%= FormatHelper.superscript(imagepane.getCombinedLabel()) %></a>
	                  </c:when>
	                  <c:otherwise>
	                	<%= FormatHelper.superscript(imagepane.getCombinedLabel()) %>
	                  </c:otherwise>
                    </c:choose>
                  </c:forEach>
                </td>
                <td style=''>
            	  <c:if test="${not empty specimenResult.note }">
	          		<% SpecimenResult specimenResult = (SpecimenResult) pageContext.getAttribute("specimenResult"); %>
			      	<%= FormatHelper.formatVerbatim(specimenResult.getNote()) %>
		      	  </c:if>
                </td>
                </tr>
                </c:forEach>
            </c:if>

          </c:forEach>

        </table>

      <div style='padding-top:6px;padding-bottom:6px;'>
       <hr class='specDivider'>
      </div>
    </c:forEach> <!-- forEach assaySpecimen -->
    </div>

    </td>
  </tr>

<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
