<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Accession Search</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="ACCESSION_ID_help.shtml">	
	<span class="titleBarMainTitle">Accession Search</span>
</div>


<!-- query form structural table -->

<table class="detailStructureTable">

<form:form method="GET" commandName="accessionQueryForm" action="${configBean.FEWI_URL}accession/summary">
<table class="queryStructureTable">

  <!-- row 1-->
  <tr>
    <td class="queryCat1">Enter an ID:</td>
    <td class="queryParams1">
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
          <form:input path="id" cols="40" class=""/>
        </div>
        <div style="float:left; text-align:left;">
        </div>
      </div>			
    </td>
  </tr>

  <!-- Submit/Reset-->
  <tr>  
    <td colspan="2" align="left">
      <input class="buttonLabel" value="Search" type="submit">
      &nbsp;&nbsp;
      <input type="reset">
    </td>
  </tr>
  
</table>
</form:form>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
