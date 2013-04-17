	  <c:set var="borders" value="allBorders"/>

	  <c:forEach var="diseaseRow" items="${diseaseGroup.diseaseRows}" varStatus="status">

	    <c:set var="rowCount" value="${rowCount + 1}"/>

	    <c:set var="stripe" value="stripe1"/>
	    <c:if test="${(rowCount % 2) == 0}">
	      <c:set var="stripe" value="stripe2"/>
	    </c:if>

	    <c:if test="${status.last}">
	      <c:set var="borders" value="${sectionBorder}"/>
	    </c:if>

	    <tr>
	      <td>&nbsp;</td>
	      <td class="${borders} ${stripe} leftAlign">
	      <c:forEach var="marker" items="${diseaseRow.mouseMarkers}" varStatus="mStatus">
	        <c:set var="symbol" value="${marker.symbol}"/>
	        <c:set var="symbol" value="${fn:replace(symbol, '<', '<<')}"/>
		<c:set var="symbol" value="${fn:replace(symbol, '>', '>>')}"/>
		<c:set var="symbol" value="${fn:replace(symbol, '<<', '<span class=\"superscript\">')}"/>
		<c:set var="symbol" value="${fn:replace(symbol, '>>', '</span>')}"/>
	      <a href='${configBean.FEWI_URL}marker/${marker.primaryID}'>${symbol}</a><c:if test="${!mStatus.last}">, </c:if>
	      </c:forEach>
	    </td>
	    <td class="${borders} ${stripe} leftAlign">
	      <c:if test="${(empty diseaseRow.mouseModels) and (empty diseaseRow.notModels)}">&nbsp;</c:if>
	      <c:if test="${(not empty diseaseRow.mouseModels) or (not empty diseaseRow.notModels)}">
	        <c:set var="modelCount" value="${fn:length(diseaseRow.mouseModels)}"/>
		<c:set var="tag" value=""/>
		<c:if test="${modelCount == 0}">
		  <c:set var="modelCount" value="${fn:length(diseaseRow.notModels)}"/>
	  	  <c:set var="tag" value='"NOT" '/>
		</c:if>

		<c:if test="${modelCount > 0}">
		<span id="show${diseaseRow.diseaseRowKey}" class="link">View ${modelCount}</span> ${tag}model<c:if test="${modelCount > 1}">s</c:if>
		</c:if>
	      </c:if>
	    </td>
	    </tr>

	  </c:forEach>
