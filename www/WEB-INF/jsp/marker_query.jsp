<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>Marker Query</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
</style>

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="marker_help.shtml">	
	<span class="titleBarMainTitle">Marker Query</span>
</div>


<!-- query form structural table -->

<table class="detailStructureTable">


<form:form method="GET" commandName="markerQueryForm" action="${configBean.FEWI_URL}marker/summary">
<table class="queryStructureTable">

  <!-- row 1-->
  <tr>
    <td class="queryCat1">Row 1</td>
    <td class="queryParams1">
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
          <form:input path="param1" cols="40" class=""/>
        </div>
        <div style="float:left; text-align:left;">
          Enter something param1
        </div>
      </div>			
    </td>
  </tr>


  <!-- row 2-->
  <tr>
    <td class="queryCat2">Row 2</td>
    <td class="queryParams2">
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
          <form:input path="param2" cols="40" class=""/>
        </div>
        <div style="float:left; text-align:left;">
          Enter something param2
        </div>
      </div>			
    </td>
  </tr>


  <!-- row 3-->
  <tr>
    <td class="queryCat1">Row 3</td>
    <td class="queryParams1">
      <div style="position:relative;">
        <div style="float:left; width:300px;text-align:left;">
          <form:input path="param2" cols="40" class=""/>
        </div>
        <div style="float:left; text-align:left;">
          Enter something param3
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

${templateBean.templateBodyStopHtml}