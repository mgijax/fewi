<%@ include file="/WEB-INF/jsp/allele_summary_header.jsp" %>

<div id="outer" >
	<span id="toggleImg" class="qfExpand"></span>
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to modify search</span></div>
	<div id="qwrap" style="display:none;">
		<%@ include file="/WEB-INF/jsp/allele_form.jsp" %>
	</div>
</div>

<div id="summary">
	<div id="breadbox" style="">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterSummary" class="filters">
				</div>
			</div>
		</div>
	</div>
	<div id="querySummary" style="width:700px;">
		<div class="innertube">
		<div id="query-ysf">
			<span style="font-weight: bold;  text-decoration: underline;  font-size: 110%;">You searched for...</span>
			<%@ include file="/WEB-INF/jsp/allele_ysf.jsp" %>
		</div>
		</div>
	</div>
	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/allele_summary_table.jsp" %>


<script type="text/javascript">
    var qDisplay = true;
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/allele_query.js"></script>


<%@ include file="/WEB-INF/jsp/allele_summary_footer.jsp" %>
