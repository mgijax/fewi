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
#age {margin-left:7px;margin-right:7px;height:9.5em}
#ageStageTd {height:12em;}
<![endif]-->
</style>

<div id="standard-qf">
<form:form commandName="queryForm" class="gxdQf">
<!-- query form table -->
<table class="pad5 borderedTable" width="100%">
	<tr class="buttonRow">
		<td align="left" colspan="2">
			<input class="buttonLabel" value="Search" type="submit" id="submit1">
			&nbsp;&nbsp;
			<input type="reset" id="reset1">
			&nbsp;&nbsp;
		    Find experiments where expression was assayed in...
		</td>
	</tr>

	<tr class="stripe1">
		<td class="cat1Gxd">Anatomical<br/>structure</td>
		<td>Coming in US96...</td>
	</tr>

 	<tr class="stripe2">
		<td class="cat2Gxd">Developmental<br/>stage</td>
		<td>Coming in US97...</td>
	</tr>

	<tr class="stripe1">
		<td class="cat1Gxd">Sex</td>
		<td>Coming in US97...</td>
	</tr>

 	<tr class="stripe2">
		<td class="cat2Gxd">Mutant</td>
		<td>Coming in US94...</td>
	</tr>
	<tr class="stripe1">
		<td class="cat1Gxd">Method</td>
		<td>Coming in US92...</td>
	</tr>
	<tr class="stripe2">
		<td class="cat2Gxd">Text</td>
		<td>Coming in US90...</td>
	</tr>
	<tr class="buttonRow">
		<td colspan="3" align="left">
			<input class="buttonLabel" value="Search" type="submit"  id="submit2">
			&nbsp;&nbsp;
			<input type="reset" id="reset2">
		</td>
	</tr>
</table>
</form:form>
</div>