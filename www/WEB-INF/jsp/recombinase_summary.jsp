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
    padding:2px;
    white-space:nowrap;
    width:auto;
    line-height:1.1;
    line-height:110%;
}
table.checkBoxSelectTable td{
    font-size:11px;
    white-space:nowrap;
}
</style>

<!-- Browser History Manager source file -->
<script src="http://yui.yahooapis.com/2.8.1/build/history/history-min.js"></script>

${templateBean.templateBodyStartHtml}

<iframe id="yui-history-iframe" src="/fewi/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="RECOMBINASE_summary_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">Recombinase Alleles - Tissue Summary</span>
</div>
<!-- end header bar -->

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/recombinase_summary_1.js"></script>

<div id="checkboxes">
  <table class="checkBoxSelectTable">
    <tr><td colspan="4" class="pageAdvice">You can control the data displayed below.</td>
      <td colspan="2">&nbsp;</td></tr>
    <tr><td colspan="4" class="pageAdvice">Check the boxes to show Anatomical System
      columns containing links to data and images.</td>
      <td colspan="2" class="pageAdvice">Hide or show other columns.</td></tr>
	<tr>
	  <td><input type="checkbox" id="adiposeTissueCheckbox" 
        onClick="flipColumn('Adipose Tissue');">Adipose&nbsp;Tissue</input>
	  </td>
	  <td><input type="checkbox" id="headCheckbox"
        onClick="flipColumn('Head');">Head</input>
	  </td>
	  <td><input type="checkbox" id="muscleCheckbox"
        onClick="flipColumn('Muscle');">Muscle</input>
	  </td>
	  <td><input type="checkbox" id="skeletalSystemCheckbox"
        onClick="flipColumn('Skeletal System');">Skeletal&nbsp;System</input>
	  </td>
	  <td colspan="2"><input type="checkbox" id="synonymsCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('Allele Synonyms');">Allele&nbsp;Synonyms</input>
	  </td>
	</tr>
	<tr>
	  <td><input type="checkbox" id="alimentarySystemCheckbox"
        onClick="flipColumn('Alimentary System');">Alimentary&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="hemolymphoidSystemCheckbox"
        onClick="flipColumn('Hemolymphoid System');">Hemolymphoid&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="nervousSystemCheckbox"
        onClick="flipColumn('Nervous System');">Nervous&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="tailCheckbox"
        onClick="flipColumn('Tail');">Tail</input>
	  </td>
	  <td><input type="checkbox" id="alleleTypeCheckbox"
        onClick="flipColumn('Allele Type');">Allele&nbsp;Type</input>
	  </td>
	  <td><input type="checkbox" id="imsrCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('IMSR');">IMSR</input>
	  </td>
	</tr>
	<tr>
	  <td><input type="checkbox" id="branchialArchesCheckbox"
        onClick="flipColumn('Branchial Arches');">Branchial&nbsp;Arches</input>
	  </td>
	  <td><input type="checkbox" id="integumentalSystemCheckbox"
        onClick="flipColumn('Integumental System');">Integumental&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="renalAndUrinarySystemCheckbox"
        onClick="flipColumn('Renal and Urinary System');">Renal&nbsp;and&nbsp;Urinary&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="earlyEmbryoCheckbox"
        onClick="flipColumn('Early Embryo');">Early&nbsp;Embryo,&nbsp;All&nbsp;Tissues</input>
	  </td>
	  <td><input type="checkbox" id="inducibleCheckbox"
        onClick="flipColumn('Inducible');">Inducible</input>
	  </td>
	  <td><input type="checkbox" id="referenceCheckbox" checked="checked"
		class="checkboxSelected"
        onClick="flipColumn('References');">References</input>
	  </td>
	</tr>
	<tr>
	  <td><input type="checkbox" id="cardiovascularSystemCheckbox"
        onClick="flipColumn('Cardiovascular System');">Cardiovascular&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="limbsCheckbox"
        onClick="flipColumn('Limbs');">Limbs</input>
	  </td>
	  <td><input type="checkbox" id="reproductiveSystemCheckbox"
        onClick="flipColumn('Reproductive System');">Reproductive&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="extraEmbryonicCheckbox"
        onClick="flipColumn('Extraembryonic Component');">Extraembryonic&nbsp;Component</input>
	  </td>
	  <td colspan="2">&nbsp;</td>
	</tr>
	<tr>
	  <td><input type="checkbox" id="cavitiesAndLiningsCheckbox"
        onClick="flipColumn('Cavities and Linings');">Cavities&nbsp;And&nbsp;Linings</input>
	  </td>
	  <td><input type="checkbox" id="liverAndBiliarySystemCheckbox"
        onClick="flipColumn('Liver and Biliary System');">Liver&nbsp;and&nbsp;Biliary&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="respiratorySystemCheckbox"
        onClick="flipColumn('Respiratory System');">Respiratory&nbsp;System</input>
	  </td>
	  <td colspan="3"><input type="checkbox" id="embryoOtherCheckbox"
        onClick="flipColumn('Embryo Other');">Embryo-other&nbsp;(Embryonic&nbsp;structures&nbsp;not&nbsp;listed&nbsp;above)</input>
	  </td>
	</tr>
	<tr>
	  <td><input type="checkbox" id="endocrineSystemCheckbox"
        onClick="flipColumn('Endocrine System');">Endocrine&nbsp;System</input>
	  </td>
	  <td><input type="checkbox" id="mesenchymeCheckbox"
        onClick="flipColumn('Mesenchyme');">Mesenchyme</input>
	  </td>
	  <td><input type="checkbox" id="sensoryOrgansCheckbox"
        onClick="flipColumn('Sensory Organs');">Sensory&nbsp;Organs</input>
	  </td>
	  <td colspan="2"><input type="checkbox" id="postnatalOtherCheckbox"
        onClick="flipColumn('Postnatal Other');">Postnatal-other&nbsp;(Postnatal&nbsp;structures&nbsp;not&nbsp;listed&nbsp;above)</input>
	  </td>
	  <td><span id="summaryResetButton" onClick="resetCheckboxes(); window.location.reload();">Reset Page</span></td>
	</tr>
  </table>
</div>
<div>

</div><br/>


<div id="summary" style="width:1150px;">
	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
			</div>
		</div>
	</div>
	<div id="querySummary">
		<div class="innertube">
				<span class="enhance">You searched for:</span><br/>
		<c:if test="${not empty recombinaseQueryForm.system}"><span class="label">Anatomical System</span> equals 
			<span class="label">${fn:replace(recombinaseQueryForm.system,";", ",") }</span><br/>
			<script type="text/javascript">
				YAHOO.mgiData.selectedSystem="${recombinaseQueryForm.system}";
			</script>		
			</c:if>
		<c:if test="${not empty recombinaseQueryForm.driver}"><span class="label">Driver</span> equals 
			<span class="label">${fn:replace(recombinaseQueryForm.driver,";", ",") }</span><br/></c:if>
    <span class="pageAdvice" style="height: 20px;">
	    Click column headings to sort table data.  Drag headings to rearrange columns.
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
