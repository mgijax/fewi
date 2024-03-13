<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>MGI Disease Model Summary</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<meta name="description" content="Disease models annotated from a reference">
<meta name="keywords" content="MGI, mouse, disease models">
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<div id="titleBarWrapper" >
  <span class="titleBarMainTitle">Disease Models by Reference</span>
</div>



<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>

<!-- structural table -->
<style type="text/css">
td.doAnnot {
    border: 1px solid gray;
    padding : 5px;
    border-collapse: collapse; 
    vertical-align: middle;
    line-height: 150%;
}
td.resultsHeader {
    border: 1px solid gray;
}
</style>

<!-- header table -->
<table class="summaryHeader">
<tr >
  <td class="summaryHeaderCat1">
       <b>Reference</b>
  </td>
  <td class="summaryHeaderData1">

    <a style="font-size:x-large;  font-weight: bold; padding-bottom:10px;"
      href="${configBean.FEWI_URL}reference/${reference.jnumID}">
      ${reference.jnumID}
    </a>

    <div style="padding:4px;"> </div>

    ${reference.shortCitation}
  </td>
</tr>
</table>

<table id="resultsTable" style="width:100%; margin-top:20px;">

<tr class="stripe2">
	<td class="resultsHeader">Allelic Composition<br/>(Genetic Background)</td>
	<td class="resultsHeader">Annotated Term</td>
</tr>

<!-- data rows, one per genotype with subrows for annotated terms -->

<c:set var="termDetail" value="${configBean.FEWI_URL}disease/"/>
<c:set var="count2" value="${0}" />

<c:forEach var="dmg" items="${reference.getDiseaseModelsGrouped()}">

  <c:set var="count2" value="${(count2 + 1) % 2}" />
  <c:set var="g" value="${dmg.get(0).genotype}"/>
  <tr class="stripe${count2+1}">
      <td class="doAnnot">
          ${ntc.convertNotes(g.combination3,'|')}
          <br/>
          <c:choose>
              <c:when test="${not empty g.strainID}">
                  (<a href="${configBean.FEWI_URL}strain/${g.strainID}" target="_blank">${g.backgroundStrain}</a>)
              </c:when>
              <c:otherwise>
                  (${g.backgroundStrain})
              </c:otherwise>
          </c:choose>
      </td>
      <td class="doAnnot">
          <table>
          <c:forEach var="dm" items="${dmg}">
              <tr><td><a href="${termDetail}${dm.diseaseID}">${dm.disease}</a></td></tr>
          </c:forEach>
          </table>
      </td>
  </tr>

</c:forEach>

<!-- close structural table and page template-->
</table>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
