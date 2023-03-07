<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.IDLinker" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% IDLinker idLinker = (IDLinker)request.getAttribute("idLinker"); %>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="${allele.symbol} ${subtitle} MGI Mouse (${allele.primaryID})"
	canonical="${configBean.FEWI_URL}allele/${allele.primaryID}"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>

<% 
    StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
%>

<script type="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></script>
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/allele/allele_detail.css">


<script type="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></script>

<script language="Javascript">
function toggleMutationDescription ()
{
    toggle ("downArrowMutationDescription");
    toggle ("rightArrowMutationDescription");
    toggle ("mutationDescription");
    if (mgihomeUrl != null) {
        hitUrl (mgihomeUrl + "other/monitor.html", "toggleMutationDescription=1");
    }
}

function toggleRecomGrid() {
    toggle("recomRibbonTeaser");
    toggle("recomRibbonWrapper");
    recombinaseGrid.resetView()
}

function toggleUserNotes() {
    toggle("recomUserNotesTeaser");
    toggle("recomUserNotes");
}

function toggleSequenceTags() {
    toggle("rightArrowSeqTag");
    toggle("downArrowSeqTag");
    toggle("seqTagTable");
}
function toggleExpressesComponent() {
    toggle("rightArrowExpressesComponent");
    toggle("downArrowExpressesComponent");
    toggle("expressesComponentTable");
}
function toggleDrivenBy() {
    toggle("rightArrowDrivenBy");
    toggle("downArrowDrivenBy");
    toggle("drivenByTable");
}
function formatForwardArgs() {
      // fill and submit either seqfetchForm or blastForm

      var i = document.seqPullDownForm.seqPullDown.selectedIndex;
      var seqStr = document.sequenceForm.seq.value;

      if (document.sequenceForm.seq.length > 1)
      {
	 for (var j = 0; j < document.sequenceForm.seq.length; j++)
	 {
	    if (document.sequenceForm.seq[j].checked)
	    {
		seqStr = document.sequenceForm.seq[j].value;
		break;
	    }
	 }
      }

      if (document.seqPullDownForm.seqPullDown.options[i].value == "download")
      {
	 document.seqfetchForm.seq1.value = seqStr;
	 document.seqfetchForm.submit();
      }
      else
      {
	 document.blastForm.seq1.value = seqStr;
	 document.blastForm.submit();
      }
}

function formatFastaArgs() {
    // ensure we have a valid value for Flank before proceeding
    if (!isIntegerFlank(document.alleleCoordForm.flank1.value)) {
        return 1;
    }
    document.alleleCoordForm.submit();
}
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- initialize variables for fixed-width div -->

<c:set var="fixedDivOpen" value='<div style="width:1000px; min-width:1000px; max-width:1000px">' />
<c:set var="fixedDivClose" value='</div>' />

<!-- header bar -->
<div id="titleBarWrapper" userdoc="ALLELE_detail_help.shtml">
	<div class="yourInputButton">
		<form name="YourInputForm">
			<input class="searchToolButton" value="Your Input Welcome" name="yourInputButton" onclick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?accID=${allele.primaryID}&amp;dataDate=<fmt:formatDate type='date' value='${databaseDate}' dateStyle='short'/>")' onmouseover="return overlib('We welcome your corrections and new data.  Click here to contact us.', LEFT, WIDTH, 200, TIMEOUT, 2000);" onmouseout="nd();" type="button">
		</form>
	</div>
    <div name="centeredTitle">
	  <span class="titleBarMainTitle">${title}</span><br/>
	  ${subtitle}
	</div>
</div>


<!-- structural table -->
<table id="structuralTable" class="detailStructureTable">

  <!-- ROW0 : table of contents -->
  <tr><!-- <%= rightTdStyles.getNext() %> -->
    <td colspan="2" class="<%= rightTdStyles.getNext() %>">
      <div style="min-width:850px; max-width:1120px; font-size: 0.9em; padding-left:10px; text-align:center;" ID="TOC">
      <c:if test="${alleleDetail.hasNomenclature}">
        <a href="#summary" class='MP'>Summary</a>
      </c:if>
      <c:if test="${alleleDetail.hasMutationOrigin}">
        | <a href="#${typeCategory}Origin" class='MP'>${typeCategory}&nbsp;origin</a>
      </c:if>
      <c:if test="${alleleDetail.hasMutationDescription}">
        | <a href="#${typeCategory}Description" class='MP'>${typeCategory} description</a>
      </c:if>
      <c:if test="${alleleDetail.hasExpression}">
        | <a href="#expression" class='MP'>Expression</a>
      </c:if>
      <c:if test="${alleleDetail.hasRecombinaseData}">
        | <a href="#recombinase" class='MP'>Recombinase activity</a>
      </c:if>
      <c:if test="${alleleDetail.hasPhenotypes}">
        | <a href="#phenotypes" class='MP'>Phenotypes</a>
      </c:if>
      <c:if test="${alleleDetail.hasDiseaseModel}">
        | <a href="#diseaseModels" class='MP'>Disease&nbsp;models</a>
      </c:if>
      <c:if test="${alleleDetail.hasTumorData}">
        | <a href="#tumor" class='MP'>Tumor data</a>
      </c:if>
      <c:if test="${alleleDetail.hasIMSR}">
        | <a href="#imsr" class='MP'>Find&nbsp;Mice&nbsp;(IMSR)</a>
      </c:if>
      <c:if test="${alleleDetail.hasNotes}">
        | <a href="#notes" class='MP'>Notes</a>
      </c:if>
      <c:if test="${alleleDetail.hasReferences}">
        | <a href="#references" class='MP'>References</a>
      </c:if>
      </div>
    </td>
  </tr>

  <!-- ROW1 : summary -->
  <tr>
    <td id="summaryHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="summary"></a>Summary</td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${fixedDivOpen}
      <c:if test="${hasPrimaryPhenoImage}">
        <table width="100%" border="none" >
	<tr width="100%"><td style='width:70%; vertical-align:top'>
      </c:if>
      <table id="nomenTable" style='width: 100%'>
	<tr style='width: 100%'>
	  <td class="rightBorderThinGray label padded right"><font class="label">${symbolLabel}: </font></td>
	  <td class="padded"><font class="enhance">${symbolSup}</font></td>
	</tr>
	<tr>
	  <td class="rightBorderThinGray label padded right"><font class="label">Name: </font></td>
	  <td class="padded">${nameSup}</td>
	</tr>
	<tr>
	  <td class="rightBorderThinGray label padded right"><font class="label">MGI ID: </font></td>
	  <td class="padded">${allele.primaryID}</td>
	</tr>
	<c:if test="${not empty synonyms}">
	  <tr>
	    <td class="rightBorderThinGray label padded right"><font class="label">Synonyms:</font></td>
	    <td class="padded">${synonyms}</td>
	  </tr>
	</c:if>
	<c:if test="${not empty marker}">
	  <c:set var="markerLink" value="${markerSymbolSup}"/>
	  <c:if test="${not empty linkToMarker}">
	      <c:set var="markerLink" value="<a href='${configBean.FEWI_URL}marker/${marker.primaryID}' class='MP'>${markerSymbolSup}</a>"/>
	  </c:if>
	  <tr>
	    <td class="rightBorderThinGray label padded right"><font class="label">${markerLabel}:</font></td>
	    <td class="padded">${markerLink}&nbsp;&nbsp;<c:if test="${not empty genomicLocation}"><span class="italic">Location:</span> ${genomicLocation}&nbsp;&nbsp;</c:if><c:if test="${not empty geneticLocation}"><span class="italic">Genetic Position:</span> ${geneticLocation}</c:if>
	    </td>
	  </tr>
	</c:if>

	<c:if test="${not empty linkToAlliance}">
	  <tr>
	    <td class="rightBorderThinGray label padded right"><font class="label">Alliance:</font></td>
	    <td class="padded">
                <a class="MP" href="${fn:replace(externalUrls.AGR_Allele, '@@@@', allele.primaryID)}">${symbolSup}</a> page
	    </td>
	  </tr>
        </c:if>

        <c:if test="${
            allele.collection == 'EUCOMM' ||
            allele.collection == 'IMPC' ||
            allele.collection == 'KOMP-CSD' ||
            allele.collection == 'KOMP-Regeneron' ||
            allele.collection == 'NorCOMM'
            }">
	  <tr>
	    <td class="rightBorderThinGray label padded right"><font class="label">IMPC:</font></td>
	    <td class="padded"><a class="MP" href="${fn:replace(externalUrls.KnockoutMouse, '@@@@', marker.primaryID)}">${markerSymbolSup}</a> gene page</td>
	  </tr>
        </c:if>

	<c:if test="${not empty qtlNote}">
	  <tr>
	    <td class="rightBorderThinGray label padded right"><font class="label">QTL Note:</font></td>
	    <td class="padded">${qtlNote}</td>
	  </tr>
	</c:if>
      </table>

      <c:if test="${hasPrimaryPhenoImage}">
        </td><td class='padLR' style='vertical-align:center; width=*; text-align:left'>
	<c:if test="${(not empty thumbnailImage) and (not empty thumbnailImage.caption)}">
	  <span class='small' style='line-height:150%'>${thumbnailImage.caption}</span><p>
	</c:if>
	<span>Show the <a href="${configBean.FEWI_URL}image/phenoSummary/allele/${allele.primaryID}" class='MP'>${imageCount} phenotype image(s)</a> involving this allele.</span>
	</td><td class='padLR' style='text-align:right; width=1%'>
	<a href="${configBean.FEWI_URL}image/pheno/${primaryImage.mgiID}"><img id="primaryPhenoImage" src='${configBean.PIXELDB_URL}${thumbnailImage.pixeldbNumericID}'${thumbnailDimensions}></a>
	</td></tr></table>
      </c:if>
      ${fixedDivClose}
    </td>
  </tr>


  <!-- ROW2 : mutation/variant origin -->
  <c:if test="${alleleDetail.hasMutationOrigin}">
  <tr>
    <td id="originHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="${typeCategory}Origin"></a>${typeCategory}<br>origin</td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${fixedDivOpen}
      <table id="mutationOriginTable">
	<c:if test="${not empty mutantCellLines}">
	<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">${mutantCellLineLabel}:</font>&nbsp;</td>
	<td class="padded" width="*">${mutantCellLines}</td>
	</tr>
	</c:if>

	<c:if test="${not empty transmissionLabel}">
	<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">${transmissionLabel}:</font>&nbsp;</td>
	<td class="padded" width="*">
	  ${transmissionPhrase}
	  <c:if test='${not empty transmissionReference}'>
	    <a href='${configBean.FEWI_URL}reference/${transmissionReference.jnumID}' class='MP'>${transmissionReference.jnumID}</a>
	  </c:if>
	</td>
	</tr>
	</c:if>

	<c:if test="${not empty parentCellLine}">
	<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">Parent Cell Line:</font>&nbsp;</td>
	<td class="padded" width="*">
	  ${parentCellLine} (${parentCellLineType})
	</td>
	</tr>
	</c:if>

	<c:if test="${not empty backgroundStrain}">
	<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">${strainLabel}:</font>&nbsp;</td>
	<td class="padded" width="*">
		<c:if test="${not empty allele.strainID}">
			<a href="${configBean.FEWI_URL}strain/${allele.strainID}" class="MP" target="_blank">${backgroundStrain}</a>
		</c:if>
		<c:if test="${empty allele.strainID}">
			${backgroundStrain}
		</c:if>
	</td>
	</tr>
	</c:if>

	<c:if test="${not empty allele.collection}">
	  <tr>
	    <td class="rightBorderThinGray label padded right"><font class="label">Project Collection:</font></td>
	    <td class="padded">
                ${allele.collection}
            </td>
	  </tr>
	</c:if>

      </table>
      ${fixedDivClose}
    </td>

  </tr>
  </c:if>

  <!-- ROW3 : mutation/variant description -->
  <c:if test="${alleleDetail.hasMutationDescription}">
  <tr>
    <td id="descriptionHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="${typeCategory}Description"></a>${typeCategory}<br>description</td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${fixedDivOpen}
      <table id="mutationDescriptionTable">
      <tr>
      <td style="width:70%; vertical-align:top;">
      <table>
		<c:set var="typeLabel" value="Allele"/>
		<c:if test="${typeCategory == 'Transgene'}">
		  <c:set var="typeLabel" value="Transgene"/>
		</c:if>
		<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">${typeLabel} Type:</font>&nbsp;</td>
		<td width="1%">&nbsp;</td>
		<td class="padded" id="alleleTypeDisplay">${allele.alleleType} <c:if test="${not empty allele.alleleSubType}">(${allele.alleleSubType})</c:if></td>
		</tr>

		<!-- comma-delimited set of mutations -->
		<c:if test="${not empty allele.inducibleNote}">
		<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">Inducer:</font>&nbsp;</td>
		<td width="1%">&nbsp;</td>
		<td class="padded" width="*">${allele.inducibleNote}</td>
		</tr>
		</c:if>

		<!-- comma-delimited set of mutations -->
		<c:if test="${not (empty mutations and empty mutationInvolves)}">
		<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">${mutationLabel}:</font>&nbsp;</td>
		<td width="1%">&nbsp;</td>
		<td class="padded" width="*">${mutations}
		  <c:if test="${not empty vector}">&nbsp;&nbsp;&nbsp;&nbsp;<I>Vector:</I>&nbsp;${vector}</c:if>
		  <c:if test="${not empty vectorType}">&nbsp;&nbsp;&nbsp;&nbsp;<I>Vector&nbsp;Type:</I>&nbsp;${vectorType}</c:if>
		</td>
		</tr>
		</c:if>

		<c:if test="${not empty mutationInvolves}">
		<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%" style="vertical-align: top"></td>
		<td width="1%">&nbsp;</td>
		<td class="padded" width="*">
		  <span style="line-height: 175%"><span style="font-weight: bold">${symbolSup}</span> involves ${allele.countOfMutationInvolvesMarkers} genes/genome features (<c:forEach var="m" items="${mutationInvolves}" varStatus="status"><a href="${configBean.FEWI_URL}marker/${m.relatedMarkerID}">${m.relatedMarkerSymbol}</a><c:if test="${!status.last}">, </c:if></c:forEach><c:if test="${allele.countOfMutationInvolvesMarkers > 3}"> ...</c:if>)</span>
		  <a href="${configBean.FEWI_URL}allele/mutationInvolves/${allele.primaryID}" class="markerNoteButton" style="display:inline" onClick="javascript:popupWide('${configBean.FEWI_URL}allele/mutationInvolves/${allele.primaryID}', ${allele.alleleKey}); return false;">View&nbsp;all</a>
		</td>
		</tr>
		</c:if>

		<c:if test="${not empty expressesComponent}">
		<tr>
		    <td class="rightBorderThinGray" align="right" width="1%" nowrap="nowrap" valign="top">&nbsp;</td>
		    <td class="padded" style="vertical-align: top;">
		      <div id="rightArrowExpressesComponent" onClick="toggleExpressesComponent();" style="float: right; cursor: pointer; position: relative; z-index: 1;"><img src="${configBean.WEBSHARE_URL}images/rightArrow.gif" border="0"></div>
		      <div id="downArrowExpressesComponent" onClick="toggleExpressesComponent();" style="cursor: pointer; position: relative; z-index: 1; display: none;"><img src="${configBean.WEBSHARE_URL}images/downArrow.gif" border="0"></div>
		    </td>
		    <td>
			<font style="font-weight: bold">${symbolSup}</font> expresses
			${fn:length(expressesComponent)} gene<c:if test="${fn:length(expressesComponent) > 1}">s</c:if>
			<div id="expressesComponentTable" style="display: none; margin-top: 2px">
			    <c:set var="ecTitle" value="Knock-in expresses:"/>
			    <c:if test="${allele.alleleType == 'Transgenic'}">
			        <c:set var="ecTitle" value="Transgene expresses:"/>
			    </c:if>
			    <font class="label">${ecTitle}</font><br/>
			    <table class="detail">
				<tr>
				    <td class="detailCat3 cm">Organism</td>
				    <td class="detailCat3 cm">Expressed&nbsp;Gene</td>
				    <c:if test="${showOrthologColumn}"><td class="detailCat3 cm">Homolog&nbsp;in&nbsp;Mouse</td></c:if>
				    <td class="detailCat3 cm">Note</td>
				</tr>
				<c:forEach var="ecMarker" items="${expressesComponent}" varStatus="ecStatus">
				    <c:set var="ecId" value="${ecMarker.relatedMarkerID}" />
				    <c:set var="ecLink" value="(<a href='${configBean.FEWI_URL}marker/${ecId}' target='_blank'>${ecId}</a>)"/>
			            <c:if test="${ecMarker.relatedMarker.organism != 'mouse'}">
				        <c:set var="ecLink" value="(<a href='${fn:replace(urls.Entrez_Gene, '@@@@', ecId)}' target='_blank'>${ecId}</a>)"/>
			            </c:if>
				    <c:if test="${empty ecId}">
				        <c:set var="ecLink" value="" />
				    </c:if>
				    <tr>
					<td class="cm">${ecMarker.relatedMarker.organism}</td>
					<td class="cm">${ecMarker.relatedMarker.symbol} ${ecLink}</td>
					<c:if test="${showOrthologColumn}">
					    <td class="cm">
					       <table style="text-align:left;">
					       <c:forEach var="eco" items ="${ecOrthologs[ecStatus.index]}" varStatus="ecoStatus">
						   <tr>
					           <td>${eco.symbol}</td>
						   <td>&nbsp;(<a href='${configBean.FEWI_URL}marker/${eco.primaryID}' target='_blank'>${eco.primaryID}</a>)</td>
						   </tr>
					       </c:forEach>
					       </table>
					    </td>
					</c:if>
					<td class="lm"><font class="small">${ecMarker.note}</font></td>
				    </tr>
				</c:forEach> 
			    </table>
			</div>

		    </td>
		</tr>
		</c:if>

		<c:if test="${not empty drivenBy}">
		<tr>
		    <td class="rightBorderThinGray" align="right" width="1%" nowrap="nowrap" valign="top">&nbsp;</td>
		    <td class="padded" style="vertical-align: top;">
		      <div id="rightArrowDrivenBy" onClick="toggleDrivenBy();" style="float: right; cursor: pointer; position: relative; z-index: 1;"><img src="${configBean.WEBSHARE_URL}images/rightArrow.gif" border="0"></div>
		      <div id="downArrowDrivenBy" onClick="toggleDrivenBy();" style="cursor: pointer; position: relative; z-index: 1; display: none;"><img src="${configBean.WEBSHARE_URL}images/downArrow.gif" border="0"></div>
		    </td>
		    <td>
			<font style="font-weight: bold">${symbolSup}</font> expression driven by
			${fn:length(drivenBy)} gene<c:if test="${fn:length(drivenBy) > 1}">s</c:if>
			<div id="drivenByTable" style="display: none; margin-top: 2px">
			    <c:set var="dbTitle" value="Knock-in expression driven by:"/>
			    <c:if test="${allele.alleleType == 'Transgenic'}">
			        <c:set var="dbTitle" value="Transgene expression driven by:"/>
			    </c:if>
			    <font class="label">${dbTitle}</font><br/>
			    <table class="detail">
				<tr>
				    <td class="detailCat3 cm">Organism</td>
				    <td class="detailCat3 cm">Driver&nbsp;Gene</td>
				    <td class="detailCat3 cm">Note</td>
				</tr>
				<c:forEach var="dbMarker" items="${drivenBy}" varStatus="dbStatus">
				    <c:set var="dbId" value="${dbMarker.relatedMarkerID}" />
				    <c:set var="dbLink" value="(<a href='${configBean.FEWI_URL}marker/${dbId}' target='_blank'>${dbId}</a>)"/>
			            <c:if test="${dbMarker.relatedMarker.organism != 'mouse'}">
				        <c:set var="dbLink" value="(<a href='${fn:replace(urls.Entrez_Gene, '@@@@', dbId)}' target='_blank'>${dbId}</a>)"/>
			            </c:if>
				    <c:if test="${empty dbId}">
				        <c:set var="dbLink" value="" />
				    </c:if>
				    <tr>
					<td class="cm">${dbMarker.relatedMarker.organism}</td>
					<td class="cm">${dbMarker.relatedMarker.symbol} ${dbLink}</td>
					<td class="lm"><font class="small">${dbMarker.note}</font></td>
				    </tr>
				</c:forEach> 
			    </table>
			</div>

		    </td>
		</tr>
		</c:if>

		<c:set var="molecularRefs" value=""/>
		<c:forEach var="ref" items="${allele.molecularReferences}" varStatus="status">
		<c:set var="molecularRefs" value="${molecularRefs}<a href='${configBean.FEWI_URL}reference/${ref.jnumID}' class='MP'>${ref.jnumID}</a>"/><c:if test="${!status.last}"><c:set var="molecularRefs" value="${molecularRefs}, "/></c:if>
		</c:forEach>

		<c:if test="${not empty description}">
		<tr>
		  <c:if test="${fn:length(description) > 100}">
		    <td class="rightBorderThinGray" align="right" width="1%" nowrap="nowrap" valign="top">&nbsp;</td>
		    <td class="padded" style="vertical-align: top;">
		      <div id="downArrowMutationDescription" onClick="toggleMutationDescription();" style="cursor: pointer; position: relative; z-index: 1;"><img src="${configBean.WEBSHARE_URL}images/downArrow.gif" border="0"></div>
		      <div id="rightArrowMutationDescription" onClick="toggleMutationDescription();" style="float: right; cursor: pointer; position: relative; z-index: 1; display: none;"><img src="${configBean.WEBSHARE_URL}images/rightArrow.gif" border="0"></div>
		    </td>
		    <td class="padded" width="*">Mutation details<span id='mutationDescription'>:&nbsp;<font class='small'>${description}
		      <c:if test="${not empty molecularRefs}">(<i>${molecularRefs}</i>)</c:if>
		      <c:if test="${not empty allele.incidentalMutations}">
			    Additional
			    <a href="${configBean.FTP_URL}datasets/incidental_muts/${allele.incidentalMutation.filename}">incidental mutations </a>
			    were detected in sequencing for the causative mutation,
			    ${symbolSup}, and may be present in stocks carrying  this mutation.
			  </c:if>
			</font></span>
		    </td>
		  </c:if>
		  <c:if test="${fn:length(description) <= 100}">
		    <td class="rightBorderThinGray" align="right" width="1%" valign="top">&nbsp;</td>
		    <td style="vertical-align: top;">&nbsp;</td>
		    <td class="padded" width="*">
				<span id='mutationDescription'><font class='small'>${description} <c:if test="${not empty molecularRefs}">(<i>${molecularRefs}</i>)</c:if>
		    	<c:if test="${not empty allele.incidentalMutations}">
			    Additional
			    <a href="${configBean.FTP_URL}datasets/incidental_muts/${allele.incidentalMutation.filename}">incidental mutations </a>
			    were detected in sequencing for the causative mutation,
			    ${symbolSup}, and may be present in stocks carrying  this mutation.
		    	</c:if>
				</font></span>
		    </td>
		  </c:if>
		</tr>
		</c:if>

		<c:if test="${(not empty allele.inheritanceMode) and (allele.inheritanceMode != 'Not Applicable')}">
		<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">Inheritance:</font>&nbsp;</td>
		<td width="1%">&nbsp;</td>
		<td class="padded" width="*">${allele.inheritanceMode}</td>
		</tr>
		</c:if>

		<c:if test="${(not empty sequenceCount) and (sequenceCount > 0)}">
		<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">Sequence Tags:</font>&nbsp;</td>
		<td width="1%" class='padded' style='vertical-align:top'>
		  <div style='float:right; cursor:pointer; position:relative; z-index:1' id='rightArrowSeqTag' onClick='toggleSequenceTags()'><img src='${configBean.WEBSHARE_URL}images/rightArrow.gif'></div>
		  <div style='float:right; cursor:pointer; display:none; position:relative; z-index:1' id='downArrowSeqTag' onClick='toggleSequenceTags()'><img src='${configBean.WEBSHARE_URL}images/downArrow.gif'></div>
		</td>
		<td class="padded" width="*" style='vertical-align:top'>
		  Sequence tag details (${sequenceCount} tag<c:if test='${sequenceCount > 1}'>s</c:if>)
		  <form name='seqfetchForm' method='GET' action='${configBean.SEQFETCH_URL}'><input type='hidden' name='seq1' value=''></form>
		  <form name='blastForm' method='GET' action='${configBean.FEWI_URL}sequence/blast' target='_blank'><input type='hidden' name='blastSpec' value='OGP__10090__9559'><input type='hidden' name='seq1' value=''></form>
		  <style>
		  td.seqTagTH { text-align: left; background-color: #D0E0F0; padding: 4px; border: 1px solid black; }
		  td.seqTag { text-align: center; vertical-align: middle; padding: 4px; border: 1px solid black; }
		  td.seqTagLeft { text-align: left; vertical-align: middle; padding: 4px; border: 1px solid black; }
		  </style>

		  <% // define holder for sequences' primary IDs and logical databases
		     Sequence seq;
		     String primaryID;
		     String logicalDB;
		  %>

		  <table id='seqTagTable' style='display:none'>
		    <tr>
		      <td class='seqTagTH'>Tag ID</td>
		      <td class='seqTagTH'>GenBank ID</td>
		      <td class='seqTagTH'>Method</td>
		      <td class='seqTagTH'>Tag Location (trapped strand)*</td>
		      <td class='seqTagTH'>Select</td>
		    </tr>
		    <form name='sequenceForm' method='GET'>
		  <c:if test="${not empty representativeSeq}">
		    <% seq = (Sequence) request.getAttribute("representativeSeq"); %>
		    <tr>
		      <td class='seqTag'>${representativeSeq.primaryID}</td>
		      <c:if test='${not empty representativeSeq.preferredGenBankID}'>
		        <td class='seqTag'>${representativeSeq.preferredGenBankID.accID}
			<br/>
			(<a href='${configBean.FEWI_URL}sequence/${representativeSeq.preferredGenBankID.accID}' class='MP'>MGI Seq Detail</a>)
		        </td>
		      </c:if>
		      <td class='seqTag'>${representativeSeq.tagMethod}</td>
		      <td class='seqTagLeft'>${representativeSeq.sequenceTagLocation}</td>
		      <td class='seqTag'><input type='radio' name='seq' value='<%= FormatHelper.getSeqForwardValue(seq) %>'></td>
		    </tr>
		  </c:if>
		  <c:if test="${not empty otherSequences}">
		    <c:forEach var="seq" items="${otherSequences}">
			<% seq = (Sequence) pageContext.getAttribute("seq"); %>
		    <tr>
		      <td class='seqTag'>${seq.primaryID}</td>
		      <c:if test='${not empty seq.preferredGenBankID}'>
		        <td class='seqTag'>${seq.preferredGenBankID.accID}
			<br/>
			(<a href='${configBean.FEWI_URL}sequence/${seq.preferredGenBankID.accID}' class='MP'>MGI Seq Detail</a>)
			</td>
		      </c:if>
		      <td class='seqTag'>${seq.tagMethod}</td>
		      <td class='seqTagLeft'>${seq.sequenceTagLocation}</td>
		      <td class='seqTag'><input type='radio' name='seq' value='<%= FormatHelper.getSeqForwardValue(seq) %>'></td>
		    </tr>
		    </c:forEach>
		  </c:if>
		    <tr>
		  </form>
		      <td colspan="2" class='seqTag' style='text-align:center'><b>* ${assemblyVersion}</b><br/>(strand of gene trap mutagenesis)</td>
		      <td colspan="3" class='seqTag' style='text-align:right'>
			<form name='seqPullDownForm' id='seqPullDownForm'><i>Selected Tags:</i>
			  <select name='seqPullDown'>
			    <option value='download' selected>download
			    <option value='blast'>BLAST at NCBI
			  </select>
			  <input type='button' value='Go' onClick='formatForwardArgs()'>
			</form>
		      </td>
		    </tr>
		  </table>
		</td>
		</tr>
		</c:if>

		<c:if test="${not empty jbrowseLink}">
		<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">Genome Context:</font>&nbsp;</td>
		<td width="1%" class='padded' style='vertical-align:top'>
		  <div style='float:right; cursor:pointer; position:relative; z-index:1' id='rightArrowGenome' onClick='toggleGenomeContext()'><img src='${configBean.WEBSHARE_URL}images/rightArrow.gif'></div>
		  <div style='float:right; cursor:pointer; display:none; position:relative; z-index:1' id='downArrowGenome' onClick='toggleGenomeContext()'><img src='${configBean.WEBSHARE_URL}images/downArrow.gif'></div>
		</td>
		<td class="padded" style='vertical-align:top'>
		  Genome Browser view of this mutation<br/>
		  <table id='genomeContextTable' style='display:none'>
		    <tr><td class='padded' style='text-align:center; background-color: #ffffff'>
		      ${jbrowseExtraLine}
		      <a href='${jbrowseLink}' class='MP' target='_new'><span style='font-size: 90%'>${jbrowseLabel}</span></a>
		    </td></tr>
		  </table>
		</td></tr>
		</c:if>
      </table>
      </td>
	    <c:if test="${not empty molecularThumbnail and not empty molecularThumbnail.pixeldbNumericID}">
	   		<c:if test="${(not empty molecularThumbnail) and (not empty molecularThumbnail.caption)}">
					<td class="padLR"><span class='small' style='line-height:150%'>${molecularThumbnail.caption}</span></td>
			</c:if>
			<td class='padLR' style='text-align:right'>
	   		<a href="${configBean.FEWI_URL}image/molecular/${molecularImage.mgiID}">
	   			<img src='${configBean.PIXELDB_URL}${molecularThumbnail.pixeldbNumericID}'>
	   		</a>
	   		</td>
	   	</c:if>
      </tr>
      </table>
      ${fixedDivClose}
    </td>
  </tr>
  </c:if>

  <!-- ROW4 : recombinase activity -->
  <c:if test="${alleleDetail.hasRecombinaseData}">
  <tr>
    <td id="recombinaseHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="recombinase"></a>Recombinase<br>activity</td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${fixedDivOpen}
      <table id="recombinaseTable">
	<tr>
	  <td style="vertical-align:top;" class="rightBorderThinGray padLR" align="right" width="1%" nowrap="nowrap">
	    <font class="label">Activity:  </font></td>
	  <td class='padLR'>
	      <div id='recomRibbonTeaser' style='${recomTeaserStyle}' onClick='toggleRecomGrid();'>
	      <img src='${configBean.WEBSHARE_URL}images/rightArrow.gif'>&nbsp;Tissue activity of this recombinase allele
	    </div>
	    <div id='recomRibbonWrapper' style='${recomWrapperStyle}'>
	      <span onClick='toggleRecomGrid();' style='cursor:pointer; padding-left:1px; padding-right:2px;'><img src='${configBean.WEBSHARE_URL}images/downArrow.gif'></span>
	      <div id='recomTableInsert'>
		<div id='recomTable_loading'>
		  <img src='${configBean.WEBSHARE_URL}images/loading.gif' alt='Loading...' />
	        </div>
	      </div>
	    </div>
	  </td>
	</tr>

	<tr>
            <c:set var="driverMarker" value="${allele.driverMarker}"/>
            <c:set var="driverID" value="${driverMarker.primaryID}"/>
            <c:set var="driverOrg" value="${fn:toLowerCase(driverMarker.organism)}"/>
            <c:if test="${driverOrg == 'not specified'}">
                <c:set var="driverOrg" value="species not specified"/>
            </c:if>
            <c:if test="${not empty driverMarker.mouseMarkerId}">
                <c:set var="driverID" value="${driverMarker.mouseMarkerId}"/>
            </c:if>
            <c:set var="driverAllianceID" value="${driverMarker.getAllianceLinkID()}"/>

	  <td style='vertical-align:top;' class='rightBorderThinGray padLR padTop' align='right' width='1%' nowrap='nowrap'>
	    <font class='label'>Driver: </font>
	  </td>
	  <td class='padLR padTop'>
	    <span style='padding-left:14px;'>${driverMarker.symbol}</span>
            <span class="small">(${driverOrg})</span>
            <div>
                <span class='small' style="padding-left: 20px;">Summary of all recombinase alleles
                  <a class='MP' href='${configBean.FEWI_URL}recombinase/summary?driver=${driverMarker.symbol}'>driven by ${driverMarker.symbol}</a>.
                </span>
            </div>
            <c:if test="${(not empty driverID) and (allele.countOfRecombinaseResults > 0)}">
              <div>
              <span class='small' style='padding-left: 20px'>
                    <a class='MP' href="${configBean.FEWI_URL}gxd/recombinasegrid/${driverID}?alleleID=${allele.primaryID}">
                        Comparative matrix view of recombinase activities
                    </a>
              </span>
              </div>
            </c:if>
            <c:if test="${not empty driverAllianceID}">
            <div>
            <span class="small" style="padding-left: 20px;">
                <a class="MP" href="${fn:replace(externalUrls.AGR_Gene, '@@@@', driverAllianceID)}">Alliance ${driverOrg} ${driverMarker.symbol} gene page</a>
            </span>
            </div>
            </c:if>
	  </td>
	</tr>

      <c:if test="${not empty recombinaseUserNote}">
      <tr>
	<td style="vertical-align:top;" class="rightBorderThinGray padLR padTop" align="right" width="1%" nowrap="nowrap">
	  <font class='label'>User Notes: </font>
	</td>
	<td class="padLR padTop">
	  <div id="recomUserNotesTeaser" style='cursor: pointer' onClick='toggleUserNotes();'>
	    <img src='${configBean.WEBSHARE_URL}images/rightArrow.gif'>&nbsp;Additional information submitted by the research community
	  </div>
	  <div id="recomUserNotes" style="display:none">
	    <span onClick='toggleUserNotes()' style='cursor:pointer; float:left; padding-left:1px; padding-right:2px'><img src='${configBean.WEBSHARE_URL}images/downArrow.gif'></span>
	    <div style='padding-left:12px'>
	      Additional information submitted by the research community<br/>
	      ${recombinaseUserNote}
            </div>
	  </div>
	</td>
      </tr>
      </c:if>

      <tr>
	<td style="vertical-align:top;" class="rightBorderThinGray padLR" style='padding-top:8px' align="right" width="1%" nowrap="nowrap">&nbsp;</td>
	<td style="padLR" style='padding-top:8px'>
	  <div class="yourInputButton" style='padding-left:14px'>
	    <form name="YourInputForm"><br style='font-size:0.5em;'/>
	      <input type="button" class="creSubmitButton" value="Your Observations Welcome" name="yourInputButton" onClick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?accID=${allele.primaryID}&dataDate=<fmt:formatDate type='date' value='${databaseDate}' dateStyle='short'/>")' onmouseover="return overlib('Submit observations (e.g., ectopic activity) about this allele.', LEFT, WIDTH, 200, TIMEOUT, 2000);" onmouseout="nd();">
	    </form>
	  </div>
	</td>
      </tr>

      </table>
      ${fixedDivClose}
    </td>
  </tr>
  </c:if>

  <!-- ROW5 : phenotypes -->
  <c:if test="${alleleDetail.hasPhenotypes}">
  <tr>
    <td id="phenotypesHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="phenotypes"></a>Phenotypes</td>
    <td class="<%=rightTdStyles.getNext() %>">
        <div id="phenoTableInsert">
		<div id="phenoTable_loading"><img src='${configBean.WEBSHARE_URL}images/loading.gif' alt="Loading..." /> </div>
        </div>

        <div class='' style='margin-top:8px;margin-left:4px;'>
          <a class="" href='${configBean.FEWI_URL}allele/allgenoviews/${allele.primaryID}' target="_blank" title='phenotype details'>View</a> phenotypes and curated references for all genotypes (concatenated display).
        </div>
    </td>
  </tr>
  </c:if>

  <!-- ROW6 : disease models -->
  <c:if test="${alleleDetail.hasDiseaseModel}">
  <tr>
    <td id="diseaseModelsHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="diseaseModels"></a>Disease&nbsp;models</td>
    <td class="<%=rightTdStyles.getNext() %>">
      <div id="diseaseTableInsert">
	<div id="diseaseTable_loading"><img src='${configBean.WEBSHARE_URL}images/loading.gif' alt='Loading...'/> </div>
      </div>
    </td>
  </tr>
  </c:if>

  <!-- ROW7 : expression -->
  <c:if test="${alleleDetail.hasExpression}">
  <tr>
    <td id="expressionHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="expression"></a>Expression</td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${fixedDivOpen}
      <table id="expressionTable">
	  <c:if test="${allele.countOfExpressionAssayResults > 0}">
      	<tr><td class="rightBorderThinGray padded" align="right" width="1%" nowrap="nowrap"><font class="label">In Mice Carrying this Mutation: </font></td>
      	<td class='padded'><a href="${configBean.FEWI_URL}gxd/allele/${allele.primaryID}" class="MP">${allele.countOfExpressionAssayResults} assay results</a></td></tr>
	  </c:if>
	  <c:if test="${anatomyTermCount > 0}">
      	<tr><td class="rightBorderThinGray padded" align="right" width="1%" nowrap="nowrap"><font class="label">In Structures Affected by this Mutation: </font></td>
      	<td class='padded'><a href="${configBean.FEWI_URL}vocab/gxd/anatomy/by_allele/${allele.primaryID}" class="MP">${anatomyTermCount} anatomical structures</a></td></tr>
	  </c:if>
	  </table>
      ${fixedDivClose}
    </td>
  </tr>
  </c:if>

  <!-- ROW8 : Tumor (MMHCdb) -->
  <c:if test="${alleleDetail.hasTumorData}">
  <tr>
    <td id="tumorHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="tumor"></a>Tumor Data</td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${fixedDivOpen}
      <table id="tumorTable">
      <tr>
        <td>
          List all tumor models in MMHCdb carrying
          <a class="MP" href="${fn:replace(externalUrls.MMHCdb, '@@@@', allele.primaryID)}">${symbolSup}</a>
        </td>
      </tr>
      </table>
      ${fixedDivClose}
    </td>
  </tr>
  </c:if>

  <!-- ROW9 : IMSR -->
  <c:if test="${alleleDetail.hasIMSR}">
  <tr>
    <td id="imsrHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="imsr"></a>Find&nbsp;Mice&nbsp;(IMSR)</td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${fixedDivOpen}
      <table id="imsrTable">
	<tr><td colspan="2"><span class="small">Mouse strains and cell lines
	  available from the International Mouse Strain Resource
	  (<a href="${configBean.IMSRURL}index" class='MP'>IMSR</a>)</td></tr>
	<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">Carrying this Mutation:</font>&nbsp;</td>
	<td class="padded" width="*">
	  Mouse Strains: ${imsrStrains}
	  &nbsp;&nbsp;&nbsp;&nbsp;
	  Cell Lines: ${imsrCellLines}
	</td>
	</tr>
	<c:if test="${(not empty marker) and (marker.markerType != 'Transgene')}">
	<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">Carrying any ${markerSymbolSup} Mutation:</font>&nbsp;</td>
	<td class="padded" width="*">
	  ${imsrForMarker}
	</td>
	</tr>
	</c:if>
      </table>
      ${fixedDivClose}
    </td>
  </tr>
  </c:if>

  <!-- ROW10 : notes -->
  <c:if test="${alleleDetail.hasNotes}">
  <tr>
    <td id="notesHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="notes"></a>Notes</td>
    <td class="<%=rightTdStyles.getNext() %>">
    
    <section class="qtlSection small spaced">
    
		<c:if test="${not empty derivationNote}">
	      <div class="topNote">${derivationNote}</div>
		</c:if>
		<c:if test="${not empty generalNote}">
		  <div class="topNote">${generalNote}</div>
		</c:if>
		<c:if test="${not empty knockoutNote}">
		  <div class="topNote">${knockoutNote}</div>
		</c:if>
		
		
		<c:if test="${not empty qtlCandidateGenes}">
		<h3 class="label">Candidate Genes</h3><p>
	  	  <c:forEach var="qtlExpt" items="${qtlCandidateGenes}">
	  	    <section class="notesBlock">
		      <h4><a href="${configBean.FEWI_URL}reference/${qtlExpt.jnumID}" class='MP'>${qtlExpt.jnumID}</a></h4>
		      <fewi:qtlNote value="${qtlExpt.note}" />
		    </section>
		  </c:forEach>
		</c:if>
		<c:if test="${not empty qtlExpts}">
		  <h3 class="label">Mapping and Phenotype information for this QTL,
		    its variants and associated markers</h3>
	  	  <c:forEach var="qtlExpt" items="${qtlExpts}">
	  	    <section class="notesBlock">
		      <h4><a href="${configBean.FEWI_URL}reference/${qtlExpt.jnumID}" class='MP'>${qtlExpt.jnumID}</a></h4>
		      		    
		      <c:if test="${not empty qtlExpt.refNote}">
		        <section class="qtlRefNoteSec">
		          <h5 class="label qtl">QTL Reference Notes</h4>
		          <p>${qtlExpt.refNote}</p>
		        </section>
		      </c:if>
		      
		      <fewi:qtlNote value="${qtlExpt.note}" />
		    </section>
		    
		  </c:forEach>
		</c:if>
	
	</section>
    </td>
  </tr>
  </c:if>

  <!-- ROW11 : reference -->
  <c:if test="${alleleDetail.hasReferences}">
  <tr>
    <td id="referencesHeader" class="<%=leftTdStyles.getNext() %>">
      <a name="references"></a>References</td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${fixedDivOpen}
      <table id="referenceTable">
      <c:set var="originalRef" value="${allele.originalReference}"/>
      <c:if test="${not empty originalRef}">
	<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">Original:</font>&nbsp;</td>
	<td class="padded" width="*"><a href="${configBean.FEWI_URL}reference/${originalRef.jnumID}" class="MP">${originalRef.jnumID}</a> ${originalRef.shortCitation}</td>
	</tr>
      </c:if>
      <c:if test="${allele.countOfReferences > 0}">
	<tr><td class="rightBorderThinGray" align="right" nowrap="nowrap" width="1%"><font class="label">All:</font>&nbsp;</td>
	<td class="padded" width="*"><a href="${configBean.FEWI_URL}reference/allele/${allele.primaryID}?typeFilter=Literature" class="MP">${allele.countOfReferences} reference(s)</a></td>
	</tr>
      </c:if>
      </table>
      ${fixedDivClose}
    </td>
  </tr>
  </c:if>

<!-- close structural table and page template-->
</table>
<script type="text/javascript">
// code to do Ajax calls

// init (basically the onload function)
$(function()
{
    MGIAjax.loadContent(
	"${configBean.FEWI_URL}allele/phenotable/${allele.primaryID}",
	"phenoTableInsert");
    MGIAjax.loadContent(
	"${configBean.FEWI_URL}allele/diseasetable/${allele.primaryID}",
	"diseaseTableInsert");
    MGIAjax.loadContent(
	"${configBean.FEWI_URL}recombinase/allele/${allele.primaryID}",
	"recomTableInsert");
})
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
