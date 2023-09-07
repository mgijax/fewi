<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.NotesTagConverter" %>
<%@ page import = "org.jax.mgi.fewi.config.ContextLoader" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.HashMap" %>
<%@ page import = "java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${antibody.name} MGI Antibody Detail - ${antibody.primaryID}</title>
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/>

<c:set var="antigen" value="${antibody.antigen}"/>

<c:set var="antibodyType" value=""/>
<c:if test="${(antibody.antibodyType == 'Polyclonal') or (antibody.antibodyType == 'Monoclonal')}">
  <c:set var="antibodyType" value="${antibody.antibodyType} "/>
</c:if>

<c:set var="antibodyClass" value="${antibody.antibodyClass}"/>
<c:if test="${antibodyClass == 'Not Applicable'}">
  <c:set var="antibodyClass" value=""/>
</c:if>
<c:if test="${(antibody.antibodyClass == 'Not Specified') and (antibody.antibodyType != 'Monoclonal')}">
  <c:set var="antibodyClass" value=""/>
</c:if>

<meta name="description" content="View ${antibodyType}antibody ${antibody.name} with antigen, genes, and references."/>
<meta name="keywords" content="MGI, GXD, mouse, mice, murine, Mus musculus, antibody, antigen, antiserum, ${antibody.name}, <c:if test='${not empty antibody.synonyms}'>${antibody.synonyms},</c:if> ${antibody.primaryID}, ${antigen.name}"/>

<%  // Pull detail object into servlet scope
    Antibody antibody = (Antibody) request.getAttribute("antibody");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1Gxd","detailCat2Gxd");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
%>

<style type="text/css">
.topBorder { border-top-color: #000000;
    border-top-style:solid;
    border-top-width:1px; }

.bottomBorder { border-bottom-color: #000000;
    border-bottom-style:solid;
    border-bottom-width:1px; }

.leftBorder { border-left-color :#000000; 
    border-left-style:solid;
    border-left-width:1px; }

.rightBorder { border-right-color :#000000; 
    border-right-style:solid;
    border-right-width:1px; }

.allBorders {
    border-top: thin solid gray;
    border-bottom: thin solid gray;
    border-left: thin solid gray;
    border-right: thin solid gray;
    padding:3px;
    text-align:center;
}

.leftAlign {
    text-align:left;
}

.bottomBorderDark { border-bottom-color: #000000;
    border-bottom-style:solid;
    border-bottom-width:2px; }

.stripe1 { background-color: #FFFFFF; }
.stripe2 { background-color: #DDDDDD; }
.headerStripe { background-color: #D0E0F0; }
.underline { border-bottom: 1px gray dotted; }

.link { color:#000099;
    cursor: pointer;
    border-bottom: 1px #000099 solid;
    text-decoration: none; }

.superscript { vertical-align: super; font-size: 90%}
.padded { padding: 4px; }
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_antibody_help.shtml">	
    <div id="gxdLogoDiv">
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
    </div>
    <div id="gxdCenteredTitle">
	<span class="titleBarMainTitleGxd">Antibody</span>
	<br>Detail
    </div>
    <div id="headerRightGxd">
    </div>
</div>


<!-- structural table -->
<table class="detailStructureTable">


  <!-- ROW1 : antibody -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Antibody
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
    <table>
	<tr>
	<td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Name:</span></td>
	<td class="padded"> <fewi:super value="${antibody.name}"/> </td>
	</tr>
	<c:if test="${not empty antibody.synonyms}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Synonym<c:if test="${fn:contains(antibody.synonyms, ',')}">s</c:if>:</span></td>
	  <td class="padded"> <fewi:super value="${antibody.synonyms}"/> </td>
	</tr>
	</c:if>
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Host:</span></td>
	  <td class="padded">${antibody.host}</td>
	</tr>
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Type:</span></td>
	  <td class="padded">${antibody.antibodyType}</td>
	</tr>
	<c:if test="${not empty antibodyClass}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Class:</span></td>
	  <td class="padded">${antibodyClass}</td>
	</tr>
	</c:if>
	<c:if test="${not empty antibody.note}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Note:</span></td>
	  <td class="padded">${antibody.note}</td>
	</tr>
	<tr>
	</c:if>
	<td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">MGI Accession ID:</span></td>
	<td class="padded">${antibody.primaryID}</td>
	</tr>
      </table>
    </td>
  </tr>


  <!-- ROW2 : antigen -->
  <c:if test="${not empty antigen}">
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Antigen
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
    <table>
	<tr>
	<td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Name:</span></td>
	<td class="padded"> <fewi:super value="${antigen.name}"/> </td>
	</tr>
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Species:</span></td>
	  <td class="padded">${antigen.species}</td>
	</tr>
	<c:if test="${(antigen.strain != 'Not Specified') && (antigen.strain != 'Not Applicable')}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Strain:</td>
	  <td class="padded">${antigen.strain}</td>
	</tr>
	</c:if>
	<c:if test="${(antigen.sex != 'Not Specified') && (antigen.sex != 'Not Applicable')}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Sex:</td>
	  <td class="padded">${antigen.sex}</td>
	</tr>
	</c:if>
	<c:if test="${(antigen.age != 'Not Specified') && (antigen.age != 'Not Applicable')}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Age:</td>
	  <td class="padded">${antigen.age}</td>
	</tr>
	</c:if>
	<c:if test="${(antigen.tissue != 'Not Specified') && (antigen.tissue != 'Not Applicable')}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Tissue:</td>
	  <td class="padded">${antigen.tissue}</td>
	</tr>
	</c:if>
	<c:if test="${not empty antigen.tissueDescription}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Tissue description:</td>
	  <td class="padded">${antigen.tissueDescription}</td>
	</tr>
	</c:if>
	<c:if test="${(antigen.cellLine != 'Not Specified') && (antigen.cellLine != 'Not Applicable')}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Cell Line:</td>
	  <td class="padded">${antigen.cellLine}</td>
	</tr>
	</c:if>
	<c:if test="${not empty antigen.regionCovered}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Region covered:</td>
		  <td class="padded">${antigen.regionCovered}
	</tr>
	</c:if>
	<c:if test="${not empty antigen.note}">
	<tr>
	  <td class="rightBorderThinGray padded" width="1%" nowrap="nowrap" align="right"><span class="label">Note:</td>
	  <td class="padded">${antigen.note}</td>
	</tr>
	</c:if>
      </table>
    </td>
  </tr>
  </c:if>

  <!-- ROW3 : genes -->
  <c:if test="${not empty antibody.markers}">
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
	    Gene<c:if test="${fn:length(antibody.markers) > 1}">s</c:if>
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
	    <c:forEach var="m" items="${antibody.sortedMarkers}">
	    <a href="${configBean.FEWI_URL}marker/${m.primaryID}">${m.symbol}</a>&nbsp;&nbsp;${m.name}<br/>
	    </c:forEach>
    </td>
  </tr>
  </c:if>

  <!-- ROW4 : expression -->
  <c:if test="${antibody.expressionResultCount > 0}">
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Expression 
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
	    Gene Expression Data (${antibody.expressionResultCount} <a href="${configBean.FEWI_URL}gxd/summary?antibodyKey=${antibody.antibodyKey}">results</a>)
    </td>
  </tr>
  </c:if>

  <!-- ROW5 : references -->
  <c:if test="${not empty antibody.references}">
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
	    Reference<c:if test="${fn:length(antibody.references) > 1}">s</c:if>
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
	    <c:forEach var="r" items="${antibody.references}">
	    <a href="${configBean.FEWI_URL}reference/${r.jnumID}">${r.jnumID}</a> ${r.miniCitation}<br/>
	    </c:forEach>
    </td>
  </tr>
  </c:if>

<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
