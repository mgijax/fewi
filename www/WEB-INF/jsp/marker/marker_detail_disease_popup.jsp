<c:set var="functionID" value="${fn:replace(diseaseRow.get('diseaseId'), ':', '_')}" />
<c:set var="dId" value="${diseaseRow.get('diseaseId')}" />
<c:set var="dTerm" value="${diseaseRow.get('diseaseTerm')}" />

<div id="models${functionID}" class="facetFilter; bottomBorder" style="display:none">
	<div class="hd">Mouse Models</div>
	<div class="bd" style="overflow: auto; max-height: 150px; max-width: 750px;">
		<a></a><!-- this empty 'a' tag is to keep Chrome and Safari from putting a selection box aroudn the first link displayed -->
		<p/>
		<strong>Human Disease Modeled:</strong> ${dTerm}<br/>
		<c:if test="${fn:length(MouseModels.get(dId)) > 0}">
			<c:set var="models" value="${MouseModels.get(dId)}" />
			<%@ include file="marker_detail_disease_popup_table.jsp" %>
		</c:if>
		<c:if test="${fn:length(NotMouseModels.get(dId)) > 0}">
			<p/>
			<strong>NOT Models</strong><br/>
			No similarity to the expected human disease phenotype was found.<br/>
			<c:set var="models" value="${NotMouseModels.get(dId)}" />
			<%@ include file="marker_detail_disease_popup_table.jsp" %>
		</c:if>
	</div>
</div>

 <script type="text/javascript">
	YAHOO.namespace("diseaseDetail.container");

	var show${functionID} = function(e) {
		YAHOO.diseaseDetail.container.panel${functionID}.show(YAHOO.diseaseDetail.container.panel${functionID});
		ga_logEvent("MarkerDetailPageEvent", "disease model popup", "${functionID}");
	};

	YAHOO.util.Event.onDOMReady(function() {
		YAHOO.diseaseDetail.container.panel${functionID} = new YAHOO.widget.Panel ("models${functionID}", { visible:false, constraintoviewport:true, context:['show${functionID}', 'tl', 'br', ['beforeShow', 'windowResize'] ] } );
		YAHOO.diseaseDetail.container.panel${functionID}.render();
		YAHOO.util.Event.addListener("show${functionID}", "click", show${functionID});
		YAHOO.util.Event.addListener("YAHOO.diseaseDetail.container.panel${functionID}", "move", YAHOO.diseaseDetail.container.panel${functionID}.forceContainerRedraw);
		YAHOO.util.Event.addListener("YAHOO.diseaseDetail.container.panel${functionID}", "mouseover", YAHOO.diseaseDetail.container.panel${functionID}.forceContainerRedraw);
		var elem = document.getElementById("models${functionID}");
		if (elem != null) {
			elem.style.display = '';	// make the div visible
		}
	});
</script>
