<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Glossary:${glossaryTerm.displayName}</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  
    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
%>

<style type="text/css">
	#glossaryTable
	{
		margin:10px 20px 10px 40px;
	}
	#glossaryTable td
	{
		padding: 8px;
	}
</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="${configBean.MGIHOME_URL}homepages/help.shtml">
    <div name="centeredTitle">
	  <span class="titleBarMainTitle">Glossary Term</span>
	</div>
</div>


<!-- structural table -->
<table id="glossaryTable">

  <!-- ROW1 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Glossary Term
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${glossaryTerm.displayName}
      <div style="float:right;"><a href="${configBean.FEWI_URL}glossary">MGI Glossary</a></div>
    </td>
  </tr>
  
  <!-- ROW2 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Definition
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
    <c:set var="definition" value="${fn:replace(glossaryTerm.definition,'!FEWI_URL!', configBean.FEWI_URL)}" />
    <c:set var="definition" value="${fn:replace(definition,'FEWI_URL', configBean.FEWI_URL)}" />
      ${definition}
    </td>
  </tr>
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
