<link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css"/>
<link rel="stylesheet" href="${configBean.FEWI_URL}assets/css/checkboxes.css"/>

<style>
.left { text-align: left; }
.font { font-size: 12px; }
.red { color: red; }
.noBorder { border: none; }
.hidden { display: none; }
.shown { display: block; clear: both; margin-bottom: 5px; text-align: left; }
.ui-widget-content a { color: -webkit-link; }
.ui-tabs .ui-tabs-panel { padding: 1em 1.4em 2.3em; }
</style>

<script>
	$(function() {
		$( "#querytabs" ).tabs(<c:if test="${not empty snpQueryForm.selectedTab}">{active: ${e:forJavaScript(snpQueryForm.selectedTab)}}</c:if>);
	});
</script>

<c:set var="userhelppage" value="SNP_help.shtml"/>
<c:set var="userhelpurl" value="${configBean.USERHELP_URL}${userhelppage}"/>

<div id="querytabs">
	<ul>
		<li><span class="label"><a href="#tabs-1">Search by Gene</a></span></li>
		<li><span class="label" id="regionTab"><a href="#tabs-2">Search by Region
<c:if test="${configBean.snpsOutOfSync == 'true'}">
<span style="font-size: 0.9em; color: red; font-weight: normal;">--disabled--</span>
</c:if>
		</a></span></li>
	</ul>
	<div>
		<div id="tabs-1">
			<form:form id="form1" method="GET" commandName="snpQueryForm" action="${configBean.FEWI_URL}snp/summary">
				<%@ include file="SNPForm_GeneSubmit.jsp" %><div style="padding-top: 5px;"><span style="margin-left: 10px;" class="label">Search for SNPs by Associated Gene(s)</span> - from ${buildNumber}</div>
				<div id="error1" class="red noBorder hidden"></div>
				<table width="100%" class="pad5 borderedTable">
					<%@ include file="SNPForm_AssociatedGenes.jsp" %>
					<%@ include file="SNPForm_StrainComparisons.jsp" %>
				</table>
				<%@ include file="SNPForm_GeneSubmit.jsp" %>
				<input type=hidden name="selectedTab" value="0" />
			</form:form>
		</div>
		<div id="tabs-2">
			<form:form id="form2" method="GET" commandName="snpQueryForm" action="${configBean.FEWI_URL}snp/summary">
				<%@ include file="SNPForm_LocationSubmit.jsp" %><div style="padding-top: 5px;"><span style="margin-left: 10px;" class="label">Search for SNPs by Genome Region</span> - from ${assemblyVersion}</div>
				<div id="error2" class="red noBorder hidden"></div>
				<table width="100%" class="pad5 borderedTable">
					<%@ include file="SNPForm_GenomeLocation.jsp" %>
					<%@ include file="SNPForm_StrainComparisons.jsp" %>
				</table>
				<%@ include file="SNPForm_LocationSubmit.jsp" %>
				<input type=hidden name="selectedTab" value="1" />
			</form:form>
		</div>
	</div>
</div>

<c:if test="${configBean.snpsOutOfSync == 'true'}">
<script>
// disable location fields and submit buttons if in out-of-sync mode 
$('#chromosomeDropList').attr('disabled', true);				// disable all three location fields
$('[name=coordinate]').attr('disabled', true);	
$('#coordinateUnitDropList').attr('disabled', true);	
$('[name=startMarker]').attr('disabled', true);	
$('[name=endMarker]').attr('disabled', true);	
$('#form2 [type=submit]').attr('disabled', true);
$('#outOfSyncLocationMessage').css({'display' : 'inline'});		// show the message
</script>
</c:if>

<c:if test="${not empty doccFounders}">
<script>
	var doccFounders = [
		<c:forEach var="strain" items="${doccFounders}">"${strain}", </c:forEach>
	];
</script>
</c:if>

<c:if test="${not empty mgpStrains}">
<script>
	var mgpStrains = [
		<c:forEach var="strain" items="${mgpStrains}">"${strain}", </c:forEach>
	];
</script>
</c:if>