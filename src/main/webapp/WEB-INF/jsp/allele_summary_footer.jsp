<script type="text/javascript">
	var panels = [];

	var show_dialog = function(panel_id) {
		//console.log("show_dialog(" + panel_id + ")");
		panels[panel_id].show(panels[panel_id]);
	};

	var setup_panel = function(panel_id) {
		if(panels[panel_id] == null) {
			panels[panel_id] = new YAHOO.widget.Panel ('dialog(' + panel_id + ')', { visible:false, constraintoviewport:true, context:['show_dialog(' + panel_id + ')', 'tl', 'br', ['beforeShow', 'windowResize'] ] } );
			panels[panel_id].render();
			YAHOO.util.Event.addListener('show_dialog(' + panel_id + ')', "click", function() { show_dialog(panel_id); });
			YAHOO.util.Event.addListener(panels[panel_id], "move", panels[panel_id].forceContainerRedraw);
			YAHOO.util.Event.addListener(panels[panel_id], "mouseover", panels[panel_id].forceContainerRedraw);
			var elem = document.getElementById('dialog(' + panel_id + ')');
			if (elem != null) { elem.style.display = '';	}
			//console.log("setup finished: " + panel_id);
		}
	};
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/allele_summary.js"></script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
