<c:set var="functionID" value="${fn:replace(disease.diseaseID, ':', '_')}_dialog" />

<div id="${functionID}" class="facetFilter; bottomBorder" style="display:none">
	<div class="hd">${disease.disease}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
	<div class="bd" style="overflow: auto; max-height: 150px; max-width: 750px;">
		<c:forEach var="secondardId" items="${disease.vocabTerm.secondaryIds}">
			<c:choose>
				<c:when test="${disease.diseaseID.equals(secondardId.accID) || (!fn:startsWith(secondardId.accID, 'DOID:') && !fn:startsWith(secondardId.accID, 'HP:'))}">
					<span style="font-size: smaller;">${idLinker.getLinkWithClass(secondardId, secondardId.accID, 'MP')}</span><br>
				</c:when>    
				<c:otherwise>
					<span style="font-size: smaller;">${secondardId.accID}</span><br>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</div>
</div>

 <script type="text/javascript">
	YAHOO.namespace("diseaseDetail.container");

	var show_${functionID} = function(e) {
		YAHOO.diseaseDetail.container.panel${functionID}.show(YAHOO.diseaseDetail.container.panel${functionID});
	};

	YAHOO.util.Event.onDOMReady(function() {
		YAHOO.diseaseDetail.container.panel${functionID} = new YAHOO.widget.Panel ("${functionID}", { visible:false, constraintoviewport:true, context:['show_${functionID}', 'tl', 'br', ['beforeShow', 'windowResize'] ] } );
		YAHOO.diseaseDetail.container.panel${functionID}.render();
		YAHOO.util.Event.addListener("show_${functionID}", "click", show_${functionID});
		YAHOO.util.Event.addListener("YAHOO.diseaseDetail.container.panel${functionID}", "move", YAHOO.diseaseDetail.container.panel${functionID}.forceContainerRedraw);
		YAHOO.util.Event.addListener("YAHOO.diseaseDetail.container.panel${functionID}", "mouseover", YAHOO.diseaseDetail.container.panel${functionID}.forceContainerRedraw);
		var elem = document.getElementById("${functionID}");
		if (elem != null) {
			elem.style.display = '';	// make the div visible
		}
	});
</script>
