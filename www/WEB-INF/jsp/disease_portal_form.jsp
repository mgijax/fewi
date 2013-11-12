<style>
span.smallGrey { font-size: 75%; color: #999999; }

#locationsDiv 
{
	display: table;
}
#locationsDiv div
{
	display: table-cell;
	vertical-align: middle;
	padding: 0px 6px;
}
.organism
{
	margin-left: 6px;
	margin-right: 3px;
}
.tab-nav
{
	float: left;
	/*margin-right: 2px; */
	padding: 5px 10px;
	cursor: pointer;
}
.tab-content
{
	clear: both;
	text-align:center;
	border: 1px solid gray;
	border-top-width: 0;
	padding: .25em .5em;
}
.inactive-tab
{
	border: 1px solid #A3A3A3;
	border-bottom-color: black;
	background: #D8D8D8 url(http://yui.yahooapis.com/2.8.2r1/build/assets/skins/sam/sprite.png) repeat-x;
	margin-top: 4px;
}
.inactive-tab:hover
{
	background:#bfdaff url(http://yui.yahooapis.com/2.8.2r1/build/assets/skins/sam/sprite.png) repeat-x left -1300px;
	outline:0;
}
.active-tab, .active-tab a:hover
{
	border: 1px solid gray;
	border-top-color: black;
	border-bottom-width: 0;
	padding-bottom: 10px;
}
.active-content
{
    display: block;
}
.inactive-content
{
    display: none;
}

.redNot{ font-size:110%; color:red; font-weight:bold; }
.hide { display:none; }
.anatomyAC
{
	padding:2px 0px 12px 20px; 
	width:300px;
	text-align:left;
}
#alertBoxPosition
{
	top: -200px;left: 200px;
}
<![endif]-->

</style>

<div id="diseasePortalSearch" class="yui-navset">
<ul class="yui-nav">
    <li class="selected"><a href="#disease-portal-search"><em>Human Disease Search</em></a></li>
</ul>
<div class="yui-content">
<div id="standard-qf">
	<form:form method="GET" commandName="diseasePortalQueryForm" action="${configBean.FEWI_URL}diseasePortal/summary">
	<table WIDTH="100%" class="pad5 borderedTable">		
		<tr>
			<td colspan="2" align="left">
				<input class="buttonLabel" value="Search" type="submit" id="submit1">
				&nbsp;&nbsp;
				<input type="reset" id="reset1">
			</td>
		</tr>	
	  <!-- row 1-->
	  <tr>
	    <td class="queryCat1">Phenotypes and Diseases</td>
	    <td class="queryParams1">
	      <div style="position:relative;">
	      	<div style="text-align:left;">
	          Query by Phenotypes or Diseases (OMIM). <i>Can be terms or IDs.</i>
	        </div>
	        <div style="padding-left: 20px; text-align:left;">
	          <form:textarea path="phenotypes" style="height:60px; width:500px;" class=""/>
	        </div>

	      </div>			
	    </td>
	  </tr>	
	  <tr>
	    <td class="queryCat2">Genes</td>
	    <td class="queryParams2">
	      <div style="position:relative;">
	      	<div style="text-align:left;">
	          Query by gene nomenclature. <i>Can be symbols,names or IDs.</i>
	        </div>
	        <div style="padding-left: 20px; text-align:left;">
	          <form:textarea path="genes" style="height:60px; width:500px;" class=""/>
	        </div>
	        <br/><div style="text-align:left;">
	          Query by gene region. (e.g. 13:22210730-22311689)
	        </div>
	        <div style="margin-left: 20px; text-align:left;" id="locationsDiv">
	          <div><form:textarea path="locations" style="height:60px; width:500px;" class=""/></div>
	          <div><b>in</b></div> <div style="width:6em;"><form:radiobuttons class="organism" path="organism" items="${diseasePortalQueryForm.organismOptions}" /></div>
	        </div>
	        <!-- <br>
	       	  <div style="height:50px;width:0px;" id="locationsFileHome"></div> -->
	      </div>			
	    </td>
	  </tr>	
	  <tr>
			<td colspan="2" align="left">
				<input class="buttonLabel" value="Search" type="submit" id="submit2">
				&nbsp;&nbsp;
				<input type="reset" id="reset2">
			</td>
		</tr>	
	 </table>	
	 <input type="hidden" name="fGene" id="fGene" />
	 <input type="hidden" name="fHeader" id="fHeader" />
     <input id="locationsFileName" type="hidden" name="locationsFileName" value="${locationsFileName}">
	</form:form>
	<!-- This will be positioned inside of locationsFileHome. We need it out here, because you can't define a form inside of another. -->
	<%-- <div id="locationsFileDiv" style="position: relative; top: -69px; left: 142px;">
		<form id="hiddenFileForm" name="hiddenFileForm" target="hiddenfileform_if" action="${configBean.FEWI_URL}diseasePortal/uploadFile" 
			enctype="multipart/form-data" method="POST">
       		Or Upload a VCF File: <input id="locationsFileInput" type="file" name="file">
       		<input type="hidden" name="field" value="locationsFile">
       		<input type="hidden" name="type" value="vcf">
       	</form>
	</div> --%>
</div>
</div>
</div>