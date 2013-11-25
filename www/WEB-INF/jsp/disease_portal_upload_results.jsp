<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%-- This is merely a page to communicate whether a file upload (via POST) was successful, or if not why. --%>
<div id="<c:out value="${empty success ? 'error' : 'success'}"/>">
	<c:out escapeXml="false" value="${empty success ? error : success}"/>
	<c:if test="${not empty vcfOutput}">
		<ul style="text-align:left;">
		<li>${vcfOutput.rowsWithCoordinates} Coordinates Included</li>
		<li>${vcfOutput.rowsKickedWithId} Rows Ignored Due to Non-Empty ID Column</li>
		<li>${vcfOutput.rowsKickedWithNotPass} Rows Ignored Due to FILTER Other Than 'PASS'</li>
		<c:if test="${not empty mouseMatch}"><li>${mouseMatch} Genes Matched On Mouse Genome</li></c:if>
		<c:if test="${not empty humanMatch}"><li>${humanMatch} Genes Matched On Human Genome</li></c:if>
		</ul>
	</c:if>
	<c:if test="${empty success}">
		<br/>Sometimes this is due to an incompatible file format (Unicode text).
			Refer to our <a target="_blank" href="${configBean.USERDOCS_URL}disease_connection_help.shtml#vcf" 
				onclick="javascript:openUserhelpWindow('disease_connection_help.shtml#vcf'); return false;">help doc</a> 
				for any questions about data-processing or file formats.
	</c:if>
	<c:if test="${not empty success}">
		<br/>Please verify your organism selection before hitting "GO".
	</c:if>
</div>