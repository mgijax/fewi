<% /*
* This file renders a slimgrid for the marker detail page, with the assumption
* that these variables are set properly in the including file:
*		- sgID : name of this slimgrid
*		- sgCells : List of MarkerGridCell objects which are the data for the
*			slimgrid
*		- sgTooltipTemplate : (optional) template of message to show when rolling
*			over a blue cell, should include <count> where the count should be
*			and <abbrev> where the heading abbreviation should be substituted
*		- sgShowAbbrev : (optional) if "true" then show the heading abbreviation
*			instead of the heading itself
*		- sgUrl : (optional) template for the URL to be used in making a link
*			from a cell to its popup window, including any of these tags as
*			necessary where data should be substituted:	<termID>, <term>,
*			<markerID>, <abbrev>
*		- sgUsePopup : empty to use a new tab, non-empty to use a popup window
*/ %>

<c:if test="${not empty sgCells}">
	<div id="${sgID}" style="display:inline-block">
		<table id="${sgID}Table">
			<tr>
				<c:forEach var="sgCell" items="${sgCells}">
					<c:set var="sgHeader" value="${sgCell.heading}"/>
					<c:if test="${sgShowAbbrev == 'true'}">
						<c:set var="sgHeader" value="${sgCell.headingAbbreviation}"/>
					</c:if>
					<c:set var="sgFontWeight" value=""/>
					<c:if test="${sgCell.colorLevel == 1}">
						<c:set var="sgFontWeight" value="bold"/>
					</c:if>
					<th class="sgHeader"><div class="sgHeaderDiv ${sgFontWeight} sgSmooth">${sgHeader}</div></th>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach var="sgCell" items="${sgCells}">
					<td class="cup sgWidth"></td>
				</c:forEach>
			</tr>
			<tr>
				<c:set var="idCounter" value="0"/>
				<c:forEach var="sgCell" items="${sgCells}">
					<c:set var="idCounter" value="${idCounter + 1}"/>
					<c:set var="sgColor" value=""/>
					<c:if test="${sgCell.colorLevel == 1}"><c:set var="sgColor" value="blue"/></c:if>
					<c:if test="${sgCell.colorLevel == 2}"><c:set var="sgColor" value="dogear"/></c:if>

					<c:set var="sgTooltip" value=""/>
					<c:if test="${(sgCell.colorLevel > 0) and (not empty sgTooltipTemplate)}">
						<c:set var="sgTooltip" value="${fn:replace(fn:replace(sgTooltipTemplate, '<count>', sgCell.value), '<abbrev>', sgCell.headingAbbreviation)}"/>
					</c:if>

					<c:set var="sgCellUrl" value=""/>
					<c:if test="${(sgCell.colorLevel > 0) and (not empty sgUrl)}">
						<c:set var="sgCellUrl" value="${fn:replace(fn:replace(fn:replace(fn:replace(sgUrl, '<markerID>', marker.primaryID), '<termID>', sgCell.termIDs), '<term>', sgCell.heading), '<abbrev>', sgCell.headingAbbreviation)}"/>
					</c:if>

					<td class="box sgWidth" title="${sgTooltip}"><a href="${sgCellUrl}" target="_blank"><div id="${sgID}${idCounter}Div" class="${sgColor}"></div></a></td>
				</c:forEach>
			</tr>
		</table>
	</div>
</c:if>
