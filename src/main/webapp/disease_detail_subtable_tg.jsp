<%@ page import="java.net.*" %>

	  <c:set var="borders" value="allBorders"/>

	  <c:forEach var="diseaseGroupRow" items="${diseaseGroup.diseaseGroupRows}" varStatus="status">

	    <c:set var="diseaseRow" value="${diseaseGroupRow.diseaseRow}"/>

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
	        ${diseaseGroupRow.annotatedDisease}
	      </td>
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
		  <c:set var="cleanedDisease" value="${diseaseGroupRow.annotatedDisease}"/>
		  <% String cleanedDisease = (String)pageContext.getAttribute("cleanedDisease");
		  cleanedDisease = cleanedDisease.replaceAll("'", "");
		  %>
          <a href="javascript:childWindow=window.open('${configBean.FEWI_URL}disease/modelsPopup/${diseaseRow.diseaseRowKey}?disease=<%=cleanedDisease%>', 'helpWindowTG${diseaseRow.diseaseRowKey}', 'width=800,height=500,resizable=yes,scrollbars=yes,alwaysRaised=yes'); childWindow.focus();">
			  ${modelCount} ${tag}model<c:if test="${modelCount > 1}">s</c:if> 
		  </a>
		</c:if>
	      </c:if>
	    </td>
	    </tr>

	  </c:forEach>
