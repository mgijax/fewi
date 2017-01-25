<%@ include file="/WEB-INF/jsp/allele_summary_header.jsp" %>

<!-- header table -->
<%@ include file="/WEB-INF/jsp/marker_header.jsp" %>
<br/>

<div id="summary">
	<c:if test="${not empty mutationInvolves}">
	<div id="leftcolumn" style="float: left; vertical-align: middle; font-size: 125%; font-weight: bold; padding-top: 30px">
		Genomic mutations involving ${marker.symbol}
	</div>
	</c:if>
	<div id="rightcolumn" style="float: right;">
			<div class="innertube">
				<div id="paginationTop">&nbsp;</div>
			</div>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/allele_summary_table.jsp" %>

<%@ include file="/WEB-INF/jsp/allele_summary_footer.jsp" %>
