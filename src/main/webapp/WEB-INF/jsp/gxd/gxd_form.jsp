<!-- jquery library (for the ageStage tab widget) -->

<style>
#locations
{
	float:left;
	padding:4px;
	height:28px;
	width:260px;
	background-color: #FFFFFF;
}
.floatLeft {
	float:left;
}
#gxdQueryForm table tr td table tr td ul li span { font-size: 10px; font-style: italic; }
#gxdQueryForm table tr td table tr td span { font-size: 10px; font-style: italic; }
span.smallGrey { font-size: 75%; color: #999999; }
#assayTypes1 > li,
#assayTypes2 > li,
#assayTypes3 > li,
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
	background:#bfdaff ;
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
.anatomyAC {
	padding:2px 0px 12px 0px;
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
.andOr {
	text-align: center;
	width: 75px;
}
#ageStage3 {
	padding-left: 15px;
}
#ageStage4 {
	padding-left: 15px;
}
#difTheilerStage4 {
	width: 190px;
}
#difTheilerStage3 {
	width: 190px;
}
#difStructure3 {
	width: 270px;
	margin-left: 5px;
}
#difStructure4 {
	width: 270px;
	margin-left: 5px;
}
<!--[if IE]> -->
#ageStageDiv {width:21em;}
#theilerStage {margin-left:0px;margin-right:12px;height:9.5em}
#age {margin-left:7px;margin-right:7px;height:9.5em}w
#ageStageTd {height:12em;}
<![endif]-->

</style>

<div id="expressionSearch" class="yui-navset">
<ul class="yui-nav">
    <li class="selected"><a href="#standard-gxd-expression-search"><em>Standard Search</em></a></li>
    <li><a href="#profile-search"><em>Expression Profile Search</em></a></li>
    <li><a href="#batch-search"><em>Batch Search</em></a></li>
</ul>
<div class="yui-content">
<div id="standard-qf">
<form:form commandName="gxdQueryForm" class="gxdQf">
<!-- query form table -->
<table class="pad5 borderedTable" width="100%">
	<tr>
		<td colspan="2" align="left">
			<input class="buttonLabel" value="Search" type="submit" id="submit1">
			&nbsp;&nbsp;
			<input type="reset" id="reset1">&nbsp;&nbsp;<span style='font-weight: bold;'>
			Default settings do not include RNA-Seq data; select assay type RNA-Seq to include.
			</span>			
			
		</td>
	</tr>

	<tr class="stripe1">
		<td class="cat1Gxd">Genes</td>
		<td>
		<table class="noborder" cellpadding='0' cellspacing='0'>
		<tr>
			<td colspan='2'><h3>Find expression data for...</h3></td>
		</tr>
		<tr>
			<td valign='top' style="padding-right: 15px;">
				<hr>
				One gene <span>e.g., Shh or kit oncogene</span><br/>
				Genes with similar nomenclature <span>e.g., Hoxa*</span><br/>
				List of genes <span>e.g., Wnt1, Bmp7, En1, Isl1...</span>
			</td>
			<td style="font-weight: bold; padding: 0px 10px; text-align:center;">
				|<br/>OR<br/>|
			</td>
			<td valign='top' style="padding-left: 15px;">
				<hr>
				A set of genes defined by
				<img id="gxdVocabHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
				<div id="gxdVocabHelp" style="visibility: hidden; height:0px;">
					<div class="hd">A set of genes defined by...</div>
					<div class="bd">
						<p>You can find sets of genes associated with terms in MGI's vocabularies and ontologies:</p>
						<ul>
							<li>Function - Gene Ontology (<a href="${configBean.FEWI_URL}vocab/gene_ontology">GO</a>)</li>
							<li>Phenotype - Mammalian Phenotype Ontology (<a href="${configBean.FEWI_URL}vocab/mp_ontology">MP</a>)</li>
							<li>Disease - Disease Ontology (<a href="${configBean.FEWI_URL}disease">DO</a>)</li>
						</ul>
					</div>
				</div>
				<ul style="padding: 0px;">
					<li>Function <span>e.g., cell-cell signalling</span></li>
					<li>Phenotype <span>e.g., obese</span></li>
					<li>Disease <span>e.g., DiGeorge Syndrome</span></li>
				</ul>
			</td>
		</tr>
		<tr>
			<td colspan="3" style="padding: 0px; height: 1em; text-align:center;">
				<div id="geneError" class="small error" style="display:none;">
				*Input not allowed in both boxes.
				Please clear one field before submitting your query.</div>
			</td>
		</tr>
		<tr>
			<td style="padding-right: 15px;" valign="top">
				<form:input id="nomenclature" path="nomenclature" class="formWidth" style="width:270px;"></form:input>
			</td>
			<td>&nbsp;</td>
			<td style="padding-left: 15px;"  valign="top">
				<div id="vocabTermAutoComplete" style="padding-left: 15px;">
				<form:input id="vocabTerm" path="vocabTerm" class="formWidth"></form:input>
				<input type="hidden" id="annotationId" name="annotationId" value=""/>
				<div id="vocabTermContainer"></div>
				</div>
				<br clear="all" />
			</td>
		</tr>
		</table>
		</td>
	</tr>

 	<tr class="stripe2">
		<td class="cat2Gxd">Genome location</td>
		<td>
		    <div style='vertical-align:top; height:40px;padding-left:5px;'>
		      <div style='float: left'>
		        <form:textarea path="locations" id="locations" style="" cols="26" rows="2" />
				<form:select multiple="false" path="locationUnit" size="1" items="${gxdQueryForm.locationUnits}" style='margin-left: 3px'>
                	<form:options items="${locationUnits}" />
                </form:select>
              </div>
	          <div style='float:left; padding-left: 5px;'>Find expression data for a set of genes defined by </br>genomic interval(s) (genome build ${configBean.ASSEMBLY_VERSION}).</div>
		    </div>
		    <div style='font-size: 10px; font-style: italic;'>e.g., Chr12:3000000-10000000 </div>
		</td>
	</tr>

	<tr class="stripe1">
		<td class="cat1Gxd">Anatomical structure or stage</td>
		<td>
			<table class="noborder">
			<tr>
			<td colspan="3"> Find assay results where expression is&nbsp;&nbsp;&nbsp;&nbsp;<form:radiobuttons class="detected" path="detected" items="${gxdQueryForm.detectedOptions}" /><br />
			<hr style="border:none; border-bottom: solid black 1px; height: 1px;" width="100%" /></td>
			</tr>
			<tr><th>Anatomical Structures:
				<img id="gxdStructureHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
				<div id="gxdStructureHelp" style="visibility: hidden; height:0px;">
					<div class="hd">Anatomical Structures...</div>
					<div class="bd">
					<p>Use the Anatomical Structure field
					to quickly find and select anatomy
					terms for searching expression data.</p>
					<p>To explore the full anatomy
					ontology, you can use the
					<a href="${configBean.FEWI_URL}vocab/gxd/anatomy/EMAPA:16039" target="_blank">Mouse Developmental Anatomy Browser.</a></p>
					</div>
				</div></th>
			<th>AND&nbsp;/&nbsp;OR</th>
			<th>&nbsp;&nbsp;&nbsp;&nbsp;Developmental Stages:</th>
			</tr>
			<tr>
			<td valign="middle">
			<div style="width:300px;text-align:left; right-padding:10px;" id="structureAutoComplete">
				<form:input id="structure" path="structure" style="width:270px;"></form:input>
				<input type="hidden" id="structureID" name="structureID" value=""/>
				<div id="structureContainer"></div>
			</div>
			<div id="structureHelp" style="display:none;" class="example">Continue typing to add another structure.</div>
			<br />
			</td>
			<td>&nbsp;</td>
			<td valign="top" id="ageStageTd">
		        <div id="ageStage">
					<div class="tab-header">
						<div class="tab-nav active-tab" id="stagesTab">Use Theiler Stages</div>
						<div class="tab-nav inactive-tab" id="agesTab">Use Ages (dpc)</div>
					</div>
					<div id="ageStageDiv" class="tab-content">
						<div class="active-content">
							<a href="${configBean.FEWI_URL}glossary/theiler" target="_blank"><img style="margin-bottom:82px;" id="" src="${configBean.WEBSHARE_URL}images/help_icon.png" /></a>
							<form:select multiple="true" path="theilerStage" size="7" items="${gxdQueryForm.theilerStages}">
	                        <form:options items="${theilerStages}" />
	                        </form:select>
						</div>
						<div class="inactive-content">
							 <form:select multiple="true" path="age" size="7" items="${gxdQueryForm.ages}">
	                        <form:options items="${ages}" />
	                        </form:select>
						</div>
					</div>
		        </div>
			</td>
			</tr>
			</table>
		</td>
	</tr>

 	<tr class="stripe2">
		<td class="cat2Gxd">Mutant / wild type</td>
		<td>
				<div style="width:500px;text-align:left;">
				Find expression data in ...
				<ul id="geneticBackground">
				<li><label><input type="radio" id="mutatedSpecimen" name="geneticBackground"/> Specimens mutated in gene: </label>
					<form:input id="mutatedIn" path="mutatedIn" style="width:220px;"></form:input></li>
				<li><label><input type="radio" id="isWildType" name="geneticBackground"/> Wild type specimens only</label></li>
				<li><label><input type="radio" id="allSpecimen" name="geneticBackground" checked="true"/> All specimens</label></li>
				</ul>
					<div style="padding-left: 15px; height: 1em;">
						<div id="mutatedSelectError" class="small error" style="display:none; white-space:nowrap;">
						*Please clarify your search by either selecting the
						<i>'Specimens mutated in'</i> button or clearing its gene box.</div>
						<div id="mutatedGeneError" class="small error" style="display:none; white-space:nowrap;">
						*You have selected <i>'Specimens mutated in gene'</i>.
						Please enter a gene.</div>
					</div>
				</div>
		</td>
	</tr>
	<tr class="stripe1">
		<td class="cat1Gxd">Assay types</td>
		<c:set var="assayTypeCount" value="0"/>
		<td>
			<label id="findAssayTypeLabel">Find expression data by assay type</label><p/>
			<div>
				<div style="padding-left: 20px; float:left;">
					<label id="allInSituLabel"><input type="checkbox" id="inSituAll" name="inSituAll" class="allInSitu" checked="true"/>
						In situ assays</label>
					<ul id="assayTypes1" >
					<c:forEach items="${gxdQueryForm.inSituAssayTypes}" var="assayType" varStatus="status">
						<c:set var="assayTypeCount" value="${assayTypeCount + 1}"/>
						<li>
							<label class="assayTypeLabel"><input type="checkbox" id="assayType${assayTypeCount}" name="assayType" class="inSituAssayType assayType" value="<c:out value="${assayType}"/>" checked="true"/>
				       		<c:out value="${assayType}"/>
				       		<c:if test="${assayType == 'RNA in situ'}"> hybridization</c:if>
				       		</label>
				       	</li>
					</c:forEach>
					</ul>
				</div>
				<div style="padding-left: 40px; float:left;">
					<label id="allBlotLabel"><input type="checkbox" id="blotAll" name="blotAll" class="allBlot" checked="true"/>
						Blot assays</label>
					<ul id="assayTypes2" >
					<c:forEach items="${gxdQueryForm.blotAssayTypes}" var="assayType" varStatus="status">
						<c:set var="assayTypeCount" value="${assayTypeCount + 1}"/>
						<li>
							<label class="assayTypeLabel"><input type="checkbox" id="assayType${assayTypeCount}" name="assayType" class="blotAssayType assayType" value="<c:out value="${assayType}"/>" checked="true"/>
				       		<c:out value="${assayType}"/>
				       		</label>
				       	</li>
					</c:forEach>
					</ul>
				</div>
				<div style="padding-left: 40px; float:left;">
					<label id="allWholeGenomeLabel"><input type="checkbox" id="wholeGenomeAll" name="allWholeGenome" class="allWholeGenome" checked="false"/>
						Whole genome assays</label>
					<ul id="assayTypes3" >
					<c:forEach items="${gxdQueryForm.wholeGenomeAssayTypes}" var="assayType" varStatus="status">
						<c:set var="assayTypeCount" value="${assayTypeCount + 1}"/>
						<li>
							<label class="assayTypeLabel"><input type="checkbox" id="assayType${assayTypeCount}" name="assayType" class="wholeGenomeAssayType assayType" value="<c:out value="${assayType}"/>" checked="false"/>
				       		<c:out value="${assayType}"/>
				       		</label>
				       	</li>
					</c:forEach>
					</ul>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="3" align="left">
			<input class="buttonLabel" value="Search" type="submit"  id="submit2">
			&nbsp;&nbsp;
			<input type="reset" id="reset2">
		</td>
	</tr>
</table>

<div id="vocabWarning">
    <div class="hd">Warning. Invalid Selection</div>
        <div id="vocabWarningText" class="bd"></div>
</div>

</form:form>
</div>

<div id="profile-qf">
<%@ include file="gxd_form_profile.jsp" %>
</div><!-- profile-qf -->

<div id="batch-qf">
<%@ include file="gxd_form_batch.jsp" %>
</div><!-- batch-qf -->

</div><!-- yui-content -->
</div><!-- expressionSearch -->
