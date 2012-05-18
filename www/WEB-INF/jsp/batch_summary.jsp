<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>Batch Summary</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

${templateBean.templateBodyStartHtml}

<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html" ></iframe>
<input id="yui-history-field" type="hidden">

<!-- header bar -->
<div id="titleBarWrapper" userdoc="BATCH_help.shtml">	
	<span class="titleBarMainTitle">Batch Summary</span>
</div>

<div id="outer"  class="bluebar">
	<span id="toggleImg" class="qfExpand"></span>
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to modify search</span></div>
	<div id="qwrap" style="display:none;">
		<%@ include file="/WEB-INF/jsp/batch_form.jsp" %>
	</div>
</div>

<div id="resultbar" class="bluebar">Results</div>

<div id="summary">

	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterSummary" class="filters">
					&nbsp;<span id="defaultText"  style="display:none;">No filters selected. Filter results below.</span>
					<span id="filterList"></span><br/>					
					<span id="fCount" style="display:none;" ><span id="filterCount">0</span> result(s) match after applying filter(s)</span>
				</div>
			</div>
		</div>
	</div>

	<div id="querySummary">
		<div class="innertube">
		<span class="title">You searched for:</span><br/>
		<span class="label">Number of IDs/symbols entered:</span> 
			${inputIdCount}<br/>
		<span class="label">Input Type:</span>
			${batchQueryForm.idTypeSelection}<br/>
		<c:if test="${batchQueryForm.hasFile}">
			<span class="label">Input File Name: </span> 
				${batchQueryForm.fileName}<br/>
			<span class="label">Input File Type:</span> 
				${batchQueryForm.fileType}<br/>
			<span class="label">ID/Symbol Column:</span> 
				${batchQueryForm.idColumn}<br/>				
		</c:if>
		<span class="label">Output options:</span> 
			${batchQueryForm.outputOptions}<br/>
		<span id="markerCount"></span> matching genes/markers found.
		</div>
	</div>

	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
</div>
	
<div id="toolbar" class="bluebar">
	<div id="downloadDiv">
		<span class="label">Export:</span> 
		<a id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a> 
		<a id="excelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a> 
	</div>
	<div id="filterDiv"></div>
	<div id="otherDiv"></div>
</div>

<!-- data table div: filled by YUI, called via js below -->
<div id="batchdata"></div>
<div id="paginationWrap">
	<div id="paginationBottom">&nbsp;</div>
</div>

<script type="text/javascript">
	var ncbiBuild = "${configBean.ASSEMBLY_VERSION}";

	// attribute columns
	var nomenclature = ${batchQueryForm.nomenclature};
	var loco = ${batchQueryForm.location};
	var ensembl = ${batchQueryForm.ensembl};
	var entrez = ${batchQueryForm.entrez};
	var vega = ${batchQueryForm.vega};
	
	// additional columns
	var go = ${batchQueryForm.go};
	var mp = ${batchQueryForm.mp};
	var omim = ${batchQueryForm.omim};
	var allele = ${batchQueryForm.allele};
	var exp = ${batchQueryForm.exp};
	var refsnp = ${batchQueryForm.refsnp};	
	var refseq = ${batchQueryForm.refseq};
	var uniprot = ${batchQueryForm.uniprot};

	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${queryString}";
	var qDisplay = true;

</script>

<!-- including this file will start the data injection -->
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/batch_query.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/batch_summary.js"></script>

${templateBean.templateBodyStopHtml}

