<!-- jquery library (for the ageStage tab widget) -->

<%
	String sex = "";
	if (request.getParameter("sex") != null) {
		sex = request.getParameter("sex");
	}
	request.setAttribute("sex", sex);
%>
<style>
#locations
{
	float:left;
	padding:4px;
	height:28px;
	width:260px;
	background-color: #FFFFFF;
}
#gxdQueryForm table tr td table tr td ul li span { font-size: 10px; font-style: italic; }
#gxdQueryForm table tr td table tr td span { font-size: 10px; font-style: italic; }
span.smallGrey { font-size: 75%; color: #999999; }
#assayTypes1 > li,
#assayTypes2 > li,
#geneticBackground > li
{
	list-style-type: none;
	list-style-image: none;
	margin-left:20px;
}
.detected
{
	margin-left: 6px;
	margin-right: 3px;
}
#theilerStage, #age {width:75%;}

/* style for ageStage tab widget (+ some extra stuff to make it look like a YUI tabView) */

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
        background: linear-gradient(white, white 35%, #d8d8d8 65%, #d8d8d8) ;
	margin-top: 4px;
}
.inactive-tab:hover
{
	background: #bfdaff;
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

.buttonRow { background-color: white }
.redNot{ font-size:110%; color:red; font-weight:bold; }
.hide { display:none; }
.anatomyAC
{
	padding:2px 0px 12px 20px;
	width:300px;
	text-align:left;
}
#ageStage
{
    display: inline-block;
    <!--[if IE ]>
        display: inline-block;
        *display: inline;
        zoom: 1;
    <![endif]-->
    min-width: 100px;
}
#gxdStructureHelp {
    font-family: Verdana,Arial,Helvetica;
    font-size: 12px;
    font-weight: normal;
}
<!--[if IE]> -->
#ageStageDiv {width:21em;}
#theilerStage {margin-left:0px;margin-right:12px;height:9.5em}
#age {margin-left:7px;margin-right:7px;}
#ageStageTd {height:12em;}
<![endif]-->
</style>

<div id="standard-qf">
<form:form commandName="queryForm" class="gxdQf" method="GET" action="${configBean.FEWI_URL}gxd/htexp_index/summary">
<!-- query form table -->
<table class="pad5 borderedTable" width="100%">
	<tr class="buttonRow">
		<td align="left" colspan="2">
			<input class="buttonLabel" value="Search" type="submit" id="submit1" onClick="logSubmission()">
			&nbsp;&nbsp;
			<input type="reset" id="reset1">
			&nbsp;&nbsp;
		    Find experiments where expression was assayed in...
		</td>
	</tr>

	<tr class="stripe1">
		<td class="cat1Gxd">Anatomical<br/>structure</td>
		<td><input type="text" size="40" name="structure" id="structureAC"
			placeholder="any anatomical structure"
			value="<c:out value="${queryForm.structure}"/>" />
		    <input type="hidden" name="structureID", id="structureID" />

		</td>
	</tr>

 	<tr class="stripe2">
		<td class="cat2Gxd">Developmental<br/>stage</td>
		<td>
		    <div id="ageStage">
				<div class="tab-header">
					<div class="tab-nav active-tab" id="agesTab">Use Ages (dpc)</div>
					<div class="tab-nav inactive-tab" id="stagesTab">Use Theiler Stages</div>
				</div>
				<div id="ageStageDiv" class="tab-content">
					<div class="active-content">
						<form:select path="ageUnit" items="${queryForm.ages}">
							<form:options items="${ages}" />
						</form:select>
						<form:input path="ageRange" />
						<div style="text-align: left; font-size: 9px; padding-left: 6px; padding-top: 6px; max-width: 280px; ">
						  Examples: <br/>
						  &nbsp;Embryonic day 10.5 <br/>
						  &nbsp;Embryonic day 7.5-8.5 <br/>
						  &nbsp;Postnatal week 1, 6-8 <br/>
						</div>
					</div>
					<div class="inactive-content">
						<a href="${configBean.FEWI_URL}glossary/theiler" target="_blank">
						    <img style="margin-bottom:82px;" id="" src="${configBean.WEBSHARE_URL}images/help_icon.png" /></a>
						<form:select multiple="true" path="theilerStage" size="7" items="${queryForm.theilerStages}">
						    <form:options items="${theilerStages}" />
						</form:select>
					</div>
				</div>
        	</div>
		</td>
	</tr>

 	<tr class="stripe1">
		<td class="cat1Gxd">Mutant</td>
		<td>Samples mutated in <form:input id="mutatedIn" path="mutatedIn" class="formWidth" style="width:135px;"></form:input></td>
	</tr>

	<tr class="stripe2">
		<td class="cat2Gxd">Strain</td>
		<td><input type="text" size="40" name="strain" id="strainNameAC"
			placeholder="name"
			value="<c:out value="${queryForm.strain}"/>" /><br/>
			<span style="font-size: 85%">Use * for wildcard.</span>
		</td>
	</tr>

	<tr class="stripe1">
		<td class="cat1Gxd">Sex</td>
		<td>
			<fewi:radio name="sex" idPrefix="sex" items="${queryForm.sexOptions}" value="${sex}" />
		</td>
	</tr>

	<tr class="stripe2">
		<td class="cat2Gxd">Method</td>
		<td id="methodCheckboxes">
			<label><input id="mcb_1" name="method" type="checkbox" checked value=""> All</label><br>
			<label><input id="mcb_1_1" name="method" type="checkbox" checked value="transcription profiling by array"> transcription profiling by array</label><br>
			<label><input id="mcb_1_2" name="method" type="checkbox" checked value="RNA-seq"> RNA-seq</label><br>
			<label><input id="mcb_1_2_1" name="method" type="checkbox" checked value="bulk RNA-seq"> bulk RNA-seq</label><br>
			<label><input id="mcb_1_2_2" name="method" type="checkbox" checked value="single cell RNA-seq"> single cell RNA-seq</label><br>
			<label><input id="mcb_1_2_3" name="method" type="checkbox" checked value="spatial RNA-seq"> spatial RNA-seq</label><br>
		</td>
	</tr>
	<tr class="stripe1">
		<td class="cat1Gxd">Text</td>
		<td>
			<form:input id="text" path="text" class="formWidth" style="width:270px;"></form:input><br/>
      		<fewi:checkboxOptions items="${queryForm.textScopeOptions}" name="textScope" values="${queryForm.textScope}" divider="&nbsp;&nbsp;" />
		</td>
	</tr>
	<tr class="stripe2">
		<td class="cat2Gxd">ArrayExpress or GEO ID</td>
		<td>
			<form:input id="arrayExpressID" path="arrayExpressID" class="formWidth" style="width:270px;"></form:input><br/>
		</td>
	</tr>
	<tr class="buttonRow">
		<td colspan="3" align="left">
			<input class="buttonLabel" value="Search" type="submit"  id="submit2" onClick="logSubmission()">
			&nbsp;&nbsp;
			<input type="reset" id="reset2">
		</td>
	</tr>
</table>
</form:form>
</div>
