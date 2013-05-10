<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Cre Allele Summary</title>

<style>
body {z-index=-2;}

.yui-skin-sam .yui-dt th{
  background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1300px;
}
.yui-skin-sam th.yui-dt-asc,.yui-skin-sam th.yui-dt-desc{
  background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1400px;
}

.yui-skin-sam th.yui-dt-sortable .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creSortableArrow.png)
  no-repeat right;
}
.yui-skin-sam th.yui-dt-asc .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creDownArrow.png)
  no-repeat right;
}
.yui-skin-sam th.yui-dt-desc .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creUpArrow.png)
  no-repeat right;
}
.yui-dt a {
  text-decoration: none;
}
.yui-dt img {
  border: none;
}

.pageAdvice {
  font-size: 11px;
  font-style: italic;
  color: #002255;
  padding:2px;
}
.selectText {
  font-size:10px;
}
.smallerCellText{
  font-size:10px;
}
.colSelectContainer{
  width:700px;
  position: relative;
  height:200px;
  border: 1px #AAA solid;
}
.colSelectSubContainer{
  /*border: 1px #999999 solid;*/
}
#summaryResetButton {
font-size: 12px;
font-family: Verdana,Arial,Helvetica;
color: #002255;
font-weight: bolder;
background-color: #eeeeee;
border: 1px #7D95B9 solid;
padding: 2px;
cursor: pointer;
}

table.checkBoxSelectTable{
    border-collapse:collapse;
    border:1px solid #AAA;
    border-spacing:2px;
    white-space:nowrap;
    width:auto;
    line-height:1.1;
    line-height:110%;
}
table.checkBoxSelectTable td{
    font-size:11px;
    white-space:nowrap;
    border-spacing:4px;
    padding:4px;
}
span.smallGrey {
font-size: 75%;
color: #999999;
}
</style>

<!-- Browser History Manager source file -->
<script src="http://yui.yahooapis.com/2.8.1/build/history/history-min.js"></script>

${templateBean.templateBodyStartHtml}

<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="RECOMBINASE_summary_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">Recombinase Alleles - Tissue Summary</span>
</div>
<!-- end header bar -->

<br/>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/recombinase_summary_1.js"></script>
<div id="summary" style="width:1150px;">
	<div id="breadbox">
		<div id="contentcolumn" style="margin:0px;">
			<div class="innertube"></div>
		</div>
	</div> 
	<div id="querySummary" style="width:700px;">
		<div class="innertube" >
				<span class="enhance">You searched for:</span><br/>
		<c:if test="${not empty recombinaseQueryForm.system}"><span class="label">Anatomical System</span> equals 
			<span class="label">${fn:replace(recombinaseQueryForm.system,";", ",") }</span><br/>
			<script type="text/javascript">
				YAHOO.mgiData.selectedSystem="${recombinaseQueryForm.system}";
			</script>		
			</c:if>
		<c:if test="${not empty recombinaseQueryForm.driver}"><span class="label">Driver</span> equals 
			<span class="label">${fn:replace(recombinaseQueryForm.driver,";", ",") }</span><br/></c:if>
		<c:if test="${not empty recombinaseQueryForm.structure}">
			<b>Activity assayed</b> in <b>${recombinaseQueryForm.structure}</b> 
			<span class="smallGrey"> includes synonyms &amp; substructures</span>
			<br/></c:if>
    <span class="pageAdvice" style="height: 20px;">
	    Click column headings to sort table data.
    </span>
		</div>
	</div>
	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
</div>

<div id="dynamicdata"></div>

<div id="paginationWrap" style="width:1150px;">
	<div id="paginationBottom">&nbsp;</div>
</div>


<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${queryString}";
</script>


<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/recombinase_summary_3.js"></script>


${templateBean.templateBodyStopHtml}
