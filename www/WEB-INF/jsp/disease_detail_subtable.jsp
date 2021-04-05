	  <c:set var="borders" value="allBorders"/>

	  <c:forEach var="diseaseGroupRow" items="${diseaseGroup.diseaseGroupRows}" varStatus="status">

	    <c:set var="diseaseRow" value="${diseaseGroupRow.diseaseRow}"/>
	    <c:set var="cluster" value="${diseaseRow.homologyCluster}"/>
	    <c:set var="clusterSource" value=""/>
	    <c:set var="homologyUrl" value=""/>
	    <c:set var="clusterKey" value=""/>

	    <c:if test="${not empty cluster}">
	      <c:set var="clusterSource" value="${cluster.secondarySource}"/>
	    </c:if>

	    <c:if test="${not empty clusterSource}">
	      <c:set var="homologyUrl" value="${configBean.FEWI_URL}homology/cluster/key/"/>
	      <c:if test="${fn:contains(clusterSource, 'Alliance Direct')}">
			<c:set var="clusterSource" value="Alliance of Genome Resources"/>
	      </c:if>
	      <c:if test="${fn:contains(clusterSource, 'Alliance Clustered')}">
			<c:set var="clusterSource" value="Alliance of Genome Resources"/>
	      </c:if>
	    </c:if>

	    <c:set var="rowCount" value="${rowCount + 1}"/>

	    <c:set var="stripe" value="stripe1"/>
	    <c:if test="${(rowCount % 2) == 0}">
	      <c:set var="stripe" value="stripe2"/>
	    </c:if>

	    <c:if test="${status.last}">
	      <c:set var="borders" value="${sectionBorder}"/>
	    </c:if>

	    <tr>${prefix}

	    <td class="${borders} ${stripe} leftAlign">
          ${diseaseGroupRow.annotatedDisease}
	    </td>

	    <td class="${borders} ${stripe} leftAlign">
	      <c:forEach var="hMarker" items="${diseaseRow.humanMarkers}" varStatus="hStatus">
	        <c:set var="hSymbol" value="${hMarker.symbol}"/>
	        <c:set var="hSymbol" value="${fn:replace(hSymbol, '<', '<<')}"/>
		<c:set var="hSymbol" value="${fn:replace(hSymbol, '>', '>>')}"/>
		<c:set var="hSymbol" value="${fn:replace(hSymbol, '<<', '<span class=\"superscript\">')}"/>
		<c:set var="hSymbol" value="${fn:replace(hSymbol, '>>', '</span>')}"/>
		<c:if test="${hMarker.isCausative == 1}">
		  <c:set var="hSymbol" value="<b>${hSymbol}</b>"/>
		</c:if>

		<c:if test="${clusterSource == 'Alliance Direct'}">
		  <c:set var="clusterKey" value="${hMarker.marker.allianceDirectOrganismOrtholog.homologyCluster.clusterKey}"/>
		</c:if>

		<c:if test="${clusterSource == 'Alliance Clustered'}">
		  <c:set var="clusterKey" value="${hMarker.marker.allianceClusteredOrganismOrtholog.homologyCluster.clusterKey}"/>
		</c:if>

		<c:if test="${not empty homologyUrl}">
		  <c:set var="hSymbol" value="<a href='${homologyUrl}${clusterKey}'>${hSymbol}</a>"/>
		</c:if>

	      ${hSymbol}<c:if test="${hMarker.isCausative == 1}">${asterisk}</c:if><c:if test="${!hStatus.last}">, </c:if>
	      </c:forEach>
	    </td>
	      <td class="${borders} ${stripe} leftAlign">
	      <c:forEach var="marker" items="${diseaseRow.mouseMarkers}" varStatus="mStatus">
	        <c:set var="symbol" value="${marker.symbol}"/>
	        <c:set var="symbol" value="${fn:replace(symbol, '<', '<<')}"/>
		<c:set var="symbol" value="${fn:replace(symbol, '>', '>>')}"/>
		<c:set var="symbol" value="${fn:replace(symbol, '<<', '<span class=\"superscript\">')}"/>
		<c:set var="symbol" value="${fn:replace(symbol, '>>', '</span>')}"/>
		<c:if test="${marker.isCausative == 1}">
		  <c:set var="symbol" value="<b>${symbol}</b>"/>
		</c:if>
	      <a href='${configBean.FEWI_URL}marker/${marker.primaryID}'>${symbol}</a><c:if test="${marker.isCausative == 1}">${asterisk}</c:if><c:if test="${!mStatus.last}">, </c:if>
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
		  <c:set var="cleanedDisease" value="${diseaseGroupRow.annotatedDisease}"/>
		  <% String cleanedDisease = (String)pageContext.getAttribute("cleanedDisease");
		  cleanedDisease = cleanedDisease.replaceAll("'", "");
		  %>
		  <a href="javascript:childWindow=window.open('${configBean.FEWI_URL}disease/modelsPopup/${diseaseRow.diseaseRowKey}?disease=<%=cleanedDisease%>', 'helpWindow${diseaseRow.diseaseRowKey}', 'width=800,height=500,resizable=yes,scrollbars=yes,alwaysRaised=yes'); childWindow.focus();">
			  ${modelCount} ${tag}model<c:if test="${modelCount > 1}">s</c:if> 
		  </a>

		</c:if>
	      </c:if>
	    </td>
	    <td class="${borders} ${stripe} leftAlign"><c:if test="${not empty diseaseRow.homologyCluster}">
	    ${diseaseRow.homologyCluster.secondarySource}
	    </c:if>
	    </td>
	    </tr>

	    <c:set var="prefix" value=""/>
	  </c:forEach>
