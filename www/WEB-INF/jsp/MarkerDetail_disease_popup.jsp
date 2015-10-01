<c:set var="dId" value="${diseaseRow.get('diseaseId')}" />
<c:set var="dTerm" value="${diseaseRow.get('diseaseTerm')}" />

<div id="models${dId}" class="facetFilter; bottomBorder" style="display:none"/>
	<div class="hd">Mouse Models</div>
	<div class="bd" style="overflow: auto; max-height: 150px; max-width: 750px;">
		<a></a><!-- this empty 'a' tag is to keep Chrome and Safari from putting a selection box aroudn the first link displayed -->
		<p/>
		<strong>Human Disease Modeled:</strong> ${dTerm}<br/>
		<c:if test="${fn:length(MouseModels.get(dId)) > 0}">
			<c:set var="models" value="${MouseModels.get(dId)}" />
			<%@ include file="/WEB-INF/jsp/MarkerDetail_disease_popup_table.jsp" %>
		</c:if>
		<c:if test="${fn:length(NotMouseModels.get(dId)) > 0}">
			<p/>
			<strong>NOT Models</strong><br/>
			No similarity to the expected human disease phenotype was found.<br/>
			<c:set var="models" value="${NotMouseModels.get(dId)}" />
			<%@ include file="/WEB-INF/jsp/MarkerDetail_disease_popup_table.jsp" %>
		</c:if>
	</div>
</div>

 <script type="text/javascript">
	YAHOO.namespace("diseaseDetail.container");

	var show${dId} = function(e) {
		YAHOO.diseaseDetail.container.panel${dId}.show(YAHOO.diseaseDetail.container.panel${dId});
		pageTracker._trackEvent("MarkerDetailPageEvent", "disease model popup", "OMIM:${dId}");
	};

	YAHOO.util.Event.onDOMReady(function() {
		YAHOO.diseaseDetail.container.panel${dId} = new YAHOO.widget.Panel ("models${dId}", { visible:false, constraintoviewport:true, context:['show${dId}', 'tl', 'br', ['beforeShow', 'windowResize'] ] } );
		YAHOO.diseaseDetail.container.panel${dId}.render();
		YAHOO.util.Event.addListener("show${dId}", "click", show${dId});
		YAHOO.util.Event.addListener("YAHOO.diseaseDetail.container.panel${dId}", "move", YAHOO.diseaseDetail.container.panel${dId}.forceContainerRedraw);
		YAHOO.util.Event.addListener("YAHOO.diseaseDetail.container.panel${dId}", "mouseover", YAHOO.diseaseDetail.container.panel${dId}.forceContainerRedraw);
		var elem = document.getElementById("models${dId}");
		if (elem != null) {
			elem.style.display = '';	// make the div visible
		}
	});
</script>
