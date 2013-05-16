<!-- jquery library (for the ageStage tab widget) -->
<script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.8.0.min.js"></script>

<style>
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
<!--[if IE]> -->
#ageStageDiv {width:21em;}
#theilerStage, #age {margin-left:7px;margin-right:7px;height:9.5em}
#ageStageTd {height:12em;}
<![endif]-->

</style>

<div id="expressionSearch" class="yui-navset">
<ul class="yui-nav">
    <li class="selected"><a href="#standard-gxd-expression-search"><em>Standard Search</em></a></li>
    <li><a href="#differential-gxd-expression-search"><em>Differential Expression Search</em></a></li>
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
			<input type="reset" id="reset1">
		</td>
	</tr>

	<tr class="stripe1">
		<td class="cat1">Genes</td>
		<td>
		<table class="noborder" cellpadding='0' cellspacing='0'>
		<tr>
			<td colspan='2'><h3>Find expression data for...</h3></td>
		</tr>
		<tr>
			<td valign='top' style="padding-right: 15px;">
				<hr>			
				One gene <span>e.g., Shh or kit oncogene</span><br/>
				Genes with similar nomenclature <span>e.g., Hoxa*</span>
			</td>
			<td style="font-weight: bold; padding: 0px 10px; text-align:center;">
				|<br/>OR<br/>|
			</td>
			<td valign='top' style="padding-left: 15px;">
				<hr>
				A set of genes defined by 
				<img id="gxdVocabHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
				<div id="gxdVocabHelp"> 
					<div class="hd">A set of genes defined by...</div> 
					<div class="bd">
						<p>You can find sets of genes associated with terms in MGI's vocabularies and ontologies:</p>
						<ul>
							<li>Function - Gene Ontology (<a href="">GO</a>)</li>
							<li>Phenotype - Mammalian Phenotype Ontology (<a href="">MP</a>)</li>
							<li>Disease - Online Mendelian Inheritance in Man (<a href="">OMIM</a>)</li>
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

<!-- 	<tr class="stripe2">
		<td class="cat2">Chromosomal location</TD>
		<td>
			<div style="width:300px;text-align:left;">
				inputs go here
			</div>
		</td>
	</tr> -->

	<tr class="stripe2">
		<td class="cat2">Anatomical structure or stage</td>
		<td>
			<table class="noborder">
			<tr>
			<td colspan="3"> Find assay results where expression is&nbsp;&nbsp;&nbsp;&nbsp;<form:radiobuttons class="detected" path="detected" items="${gxdQueryForm.detectedOptions}" /><br />
			<hr style="border:none; border-bottom: solid black 1px; height: 1px;" width="100%" /></td>
			</tr>
			<tr><th>Anatomical Structures:</th>
			<th>AND&nbsp;/&nbsp;OR</th>
			<th>&nbsp;&nbsp;&nbsp;&nbsp;Developmental Stages (dpc):</th>
			</tr>
			<tr>
			<td valign="middle">
			<div style="width:300px;text-align:left; right-padding:10px;" id="structureAutoComplete">
				<form:input id="structure" path="structure" style="width:270px;"></form:input>
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

 	<tr class="stripe1">
		<td class="cat1">Mutant / wild type</td>
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
	<tr class="stripe2">
		<td class="cat2">Assay types</td>
		<td>
			<label id="allAssayTypeLabel"><input type="checkbox" id="assayType-ALL" name="assayType-ALL" class="assayType-ALL" checked="true"/> 
				Find expression data in any assay type</label>
			<div id="assayTypes" style="width:800px;text-align:left;position:relative;">
			<ul id="assayTypes1" >
			<c:forEach items="${gxdQueryForm.assayTypes}" var="assayType" varStatus="status">
				<c:if test="${status.count == 5 }">
					</ul><ul style="position:absolute; left:300px; top:0px;" id="assayTypes2">
				</c:if>
		       <li>
		       		<label class="assayTypeLabel"><input type="checkbox" id="assayType" name="assayType" class="assayType" value="<c:out value="${assayType}"/>" checked="true"/>
		       		<c:out value="${assayType}"/></label>
		       	</li>
			       
		   </c:forEach>
		   </ul>
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
<div id="differential-qf">
<table class="pad5 borderedTable" width="100%">
	<tr class="stripe1">
		<td class="cat1">Anatomical Structure</td>
		<td>
			<div id="difStructClosed" style="cursor:pointer;" class="hide">
			<img src="http://www.informatics.jax.org/webshare/images/rightArrow.gif"/> Search for genes expressed in some <b>anatomical structures</b> but not others.
			</div>
			<div id="difStructOpen">
			<img src="http://www.informatics.jax.org/webshare/images/downArrow.gif"/> Search for genes expressed in some <b>anatomical structures</b> but not others.
			<br/><br/>
			<form:form commandName="gxdDifferentialQueryForm"  id="gxdDifferentialQueryForm1" class="gxdQf">
			<table class="noborder">
			<tr><td colspan="2">
				Find genes where expression is detected in<br/>
				<div style="padding:2px 0px 12px 20px; width:300px;text-align:left;" id="difStructureAutoComplete1">
				<input id="difStructure1" name="structure" style="width:270px;"></input>
				<div id="difStructureContainer1"></div>
				</div>
			</td></tr>
			<tr><td colspan="2">
				and expression is <span class="redNot">NOT</span> detected or analyzed in 
				<img id="gxdDifStruct1HelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
				<div id="gxdDifStruct1Help"> 
					<div class="hd"><b>NOT</b> detected or analyzed in...</div> 
					<div class="bd">
						<p>The search will return genes whose expression was absent (not detected), 
						as well as genes whose expression has not been analyzed or recorded in the database for the specified structure.</p>
					</div> 
				</div> 
			</td></tr>
			<tr><td colspan="2">
				<div style="padding:2px 0px 12px 20px;width:300px;text-align:left;" id="difStructureAutoComplete2">
				<input id="difStructure2" name="difStructure" style="width:270px;"></input>
				<div id="difStructureContainer2"></div>
				</div>
			</td></tr>
			<tr><td colspan="2">
				<div id="difStructureError" class="small error" style="display:none; white-space:nowrap;padding-bottom:4px;">
				*Please enter a structure in <i>both</i> search boxes.</div>
				<input class="buttonLabel" value="Search" type="submit" id="submit3">
				&nbsp;&nbsp;
				<input type="reset" id="reset3">
			</td></tr>
			</table>
			</form:form>
			</div>
		</td>
	</tr>
	<tr class="stripe2">
		<td class="cat2">Developmental Stage</td>
		<td>
		<div id="difStageClosed" style="cursor:pointer;">
		<img src="http://www.informatics.jax.org/webshare/images/rightArrow.gif"/> Search for genes expressed at some <b>developmental stages</b> but not others.
		</div>
		<div id="difStageOpen" class="hide">
		<img src="http://www.informatics.jax.org/webshare/images/downArrow.gif"/> Search for genes expressed at some <b>developmental stages</b> but not others.
		<br/><br/>
		<form:form commandName="gxdDifferentialQueryForm" id="gxdDifferentialQueryForm2" class="gxdQf">
		<table class="noborder">
			<tr>
			<td colspan="2" style="vertical-align:top;">
			Find genes where expression is detected at
			</td>
			<td>
				<div style="padding-left:40px;">
				<div id="ageStage2">
					<!-- <div class="tab-header">
						<div class="tab-nav active-tab" id="stagesTab2">Use Theiler Stages</div>
						<div class="tab-nav inactive-tab" id="agesTab2">Use Ages (dpc)</div>
					</div>
					<div id="ageStageDiv2" class="tab-content">
						<div class="active-content"> -->
							<form:select multiple="true" path="theilerStage" id="difTheilerStage1" size="7" items="${gxdQueryForm.theilerStagesRibbon2}">
	                        <form:options items="${theilerStagesRibbon2}" />
	                        </form:select>
						<!-- </div> -->
<%-- 						<div class="inactive-content">
							 <form:select multiple="true" path="age" size="7" items="${gxdQueryForm.ages}">
	                        <form:options items="${ages}" />
	                        </form:select>
						</div> --%>
					<!-- </div> -->
		        </div>               
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="2" style="vertical-align:top;">
				and expression is <span class="redNot" style="vertical-align:top;">NOT</span> detected or analyzed at 
				<img id="gxdDifStageHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" />
				<div id="gxdDifStageHelp"> 
					<div class="hd"><b>NOT</b> detected or analyzed in...</div> 
					<div class="bd">
						<p>The search will return genes whose expression was absent (not detected), 
						as well as genes whose expression has not been analyzed or recorded in the database for the specified developmental stage(s).</p>
					</div> 
				</div> 
			</td>
			<td>
				<div style="padding-left:40px;">
		        <div id="ageStage3">
					<!-- <div class="tab-header">
						<div class="tab-nav active-tab" id="stagesTab3">Use Theiler Stages</div>
						<div class="tab-nav inactive-tab" id="agesTab3">Use Ages (dpc)</div>
					</div>
					<div id="ageStageDiv3" class="tab-content">
						<div class="active-content"> -->
							<form:select multiple="true" path="difTheilerStage" id="difTheilerStage2" size="7" items="${gxdQueryForm.difTheilerStages}">
	                        <form:options items="${theilerStages}" />
	                        </form:select>
						<!-- </div> -->
<%-- 						<div class="inactive-content">
							 <form:select multiple="true" path="difAge" size="7" items="${gxdQueryForm.difAges}">
	                        <form:options items="${ages}" />
	                        </form:select>
						</div> --%>
					</div> 
		        </div>                            
			</td>
				</div>
			</td>
		</tr>
		<tr><td colspan="3">
			<div id="difStageError" class="small error" style="display:none; white-space:nowrap;padding-bottom:4px;">
				*Please select a specific stage(s) in one of the select boxes.</div>
				<input class="buttonLabel" value="Search" type="submit" id="submit4">
				&nbsp;&nbsp;
				<input type="reset" id="reset4">
		</td></tr>
		</table>
		</form:form>
		</div>
		</td>
	</tr>
	<tr class="stripe1">
		<td class="cat1">Structure and Stage</td>
		<td>
		<div id="difStructStageClosed" style="cursor:pointer;">
		<img src="http://www.informatics.jax.org/webshare/images/rightArrow.gif"/> Search for genes expressed in <b>some anatomical structures and stages</b> but not others.
		</div>
		<div id="difStructStageOpen" class="hide">
		<img src="http://www.informatics.jax.org/webshare/images/downArrow.gif"/> Search for genes expressed in <b>some anatomical structures and stages</b> but not others.
		<br/><br/>
		<form:form commandName="gxdDifferentialQueryForm" id="gxdDifferentialQueryForm3" class="gxdQf">
		<table class="noborder">
			<tr>
			<td colspan="2" style="vertical-align:top;">
			Find genes where expression is detected in<br/>
			<div class="anatomyAC" id="difStructureAutoComplete3">
				<input id="difStructure3" name="structure" style="width:270px;"></input>
				<div id="difStructureContainer3"></div>
			</td>
			<td>
				<div style="padding-left:40px;">
				at<br/>
				<div id="ageStage4">
							<form:select multiple="true" path="theilerStage" id="difTheilerStage3" size="7" items="${gxdQueryForm.theilerStages}">
	                        <form:options items="${theilerStages}" />
	                        </form:select>
		        </div>               
				</div>
			</td>
		</tr>
		<tr  style="height:8px;"><td colspan="4"></td></tr>
		<tr>
			<td colspan="2" style="vertical-align:top;">
				and expression is <span class="redNot" style="vertical-align:top;">NOT</span> detected or analyzed in 
				<img id="gxdDifStructStageHelpImage" src="${configBean.WEBSHARE_URL}images/help_icon.png" /><br/>
				<div id="gxdDifStructStageHelp"> 
					<div class="hd"><b>NOT</b> detected or analyzed in...</div> 
					<div class="bd">
						<p>The search will return genes whose expression was absent (not detected), 
						as well as genes whose expression has not been analyzed or recorded in the database for the specified structure and stage(s).</p>
					</div> 
				</div> 
				<div class="anatomyAC" id="difStructureAutoComplete4">
				<input id="difStructure4" name="difStructure" style="width:270px;"></input>
				<div id="difStructureContainer4"></div>
			</td>
			<td>
				<div style="padding-left:40px;">
				at<br/>
		        <div id="ageStage4">
							<form:select multiple="true" path="difTheilerStage" id="difTheilerStage4" size="7" items="${gxdQueryForm.difTheilerStages}">
	                        <form:options items="${theilerStages}" />
	                        </form:select>
					</div> 
		        </div>                            
			</td>
				</div>
			</td>
		</tr>
		<tr><td colspan="3">
			<div id="difStructStageError" class="small error" style="display:none; white-space:nowrap;padding-bottom:4px;">
				*Please enter a structure in <i>both</i> search boxes.</div>
				<input class="buttonLabel" value="Search" type="submit" id="submit5">
				&nbsp;&nbsp;
				<input type="reset" id="reset5">
		</td></tr>
		</table>
		</form:form>
		</div>
		</td>
	</tr>
</table>
</div>
</div>
</div>