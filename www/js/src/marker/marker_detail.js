	$(function(){
		window.isIntegerFlank = function(flank) {
		// error if non-numeric flank
			if (isNaN(flank)) {
				alert ("An invalid value is specified for Flank (" + flank + "). Flank must be an integer.");
				return 0;
			}
	
			// error if flank is a float, not an integer
			if (flank.indexOf('.') != -1) {
				alert ("An invalid value is specified for Flank (" + flank + "). Flank must be an integer -- without a decimal point.");
				return 0
			}
	
			// error if flank has extra spaces
			if (flank.indexOf(' ') != -1) {
				alert ("An invalid value is specified for Flank (" + flank + "). Flank must be an integer -- without extra spaces.");
				return 0
			}
			return 1;
		}
	
		window.formatForwardArgs = function() {
			document.sequenceForm.action = document.sequenceFormPullDown.seqPullDown.options[document.sequenceFormPullDown.seqPullDown.selectedIndex].value;
			if (document.sequenceForm.action.indexOf("blast") >= 0) {
			    document.sequenceForm.target = "_blank";
			} else {
			    document.sequenceForm.target = "";
		        }
	
			// ensure we have a valid value for Flank before proceeding
			if (document.sequenceForm.flank1 && !isIntegerFlank(document.sequenceForm.flank1.value)) {
				return 1;
			}
			document.sequenceForm.submit();
		}
	
		window.formatFastaArgs = function() {
			// ensure we have a valid value for Flank before proceeding
			if (document.markerCoordForm.flank1 && !isIntegerFlank(document.markerCoordForm.flank1.value)) {
				return 1;
			}
			document.markerCoordForm.submit();
		}
	
		function initializeClusterMembersPopup () {
			var elem = document.getElementById("clusterMemberTable");

			if (elem != null) {
				var rows = elem.getElementsByTagName("tr").length;
				YAHOO.namespace("markerDetail.container");
		
				var props = { 
					visible:false, 
					constraintoviewport:true,
					context:['showClusterMembers', 'tl', 'br', [ 'beforeShow', 'windowResize' ] ] 
				};
		
				if (rows > 12) {
					props.height = "300px";
					props.width = (elem.offsetWidth + 40) + "px";
				}
				
				// make the div visible
				elem.style.display = '';
		
				/* Wire up cluster members popup show link */
				YAHOO.markerDetail.container.clusterMemberPanel = new YAHOO.widget.Panel(
					"clusterMemberDiv", 
					props
				);
				YAHOO.markerDetail.container.clusterMemberPanel.render();
				YAHOO.util.Event.addListener ("showClusterMembers", "click",
					YAHOO.markerDetail.container.clusterMemberPanel.show,
					YAHOO.markerDetail.container.clusterMemberPanel, 
					true
				);
				YAHOO.util.Event.addListener (
					"YAHOO.markerDetail.container.clusterMemberPanel", "move",
					YAHOO.markerDetail.container.clusterMemberPanel.forceContainerRedraw
				);
				YAHOO.util.Event.addListener (
					"YAHOO.markerDetail.container.clusterMemberPanel", "mouseover",
					YAHOO.markerDetail.container.clusterMemberPanel.forceContainerRedraw
				);
			}
		}
		initializeClusterMembersPopup();

		/* Wire up batch submit in cluster members popup */
		$("#clusterBatchLink").click(function(){
			$("#batchWebForm").submit();
		});
	
		function initializeTssPopup () {
			var elem = document.getElementById("tssTable");

			if (elem != null) {
				var rows = elem.getElementsByTagName("tr").length;
				YAHOO.namespace("markerDetail.container");
		
				var props = { 
					visible:false, 
					constraintoviewport:true,
					context:['showTss', 'tl', 'br', [ 'beforeShow', 'windowResize' ] ] 
				};
		
				if (rows > 12) {
					props.height = "300px";
					props.width = (elem.offsetWidth + 40) + "px";
				}
				
				// make the div visible
				elem.style.display = '';
		
				/* Wire up TSS popup show link */
				YAHOO.markerDetail.container.tssPanel = new YAHOO.widget.Panel("tssDiv", props);
				YAHOO.markerDetail.container.tssPanel.render();
				YAHOO.util.Event.addListener ("showTss", "click",
					YAHOO.markerDetail.container.tssPanel.show,
					YAHOO.markerDetail.container.tssPanel, 
					true
				);
				YAHOO.util.Event.addListener (
					"YAHOO.markerDetail.container.tssPanel", "move",
					YAHOO.markerDetail.container.tssPanel.forceContainerRedraw
				);
				YAHOO.util.Event.addListener (
					"YAHOO.markerDetail.container.tssPanel", "mouseover",
					YAHOO.markerDetail.container.tssPanel.forceContainerRedraw
				);
			}
		}
		initializeTssPopup();
	
		/* Wire up batch submit in cluster members popup */
		$("#tssBatchLink").click(function(){
			$("#tssBatchWebForm").submit();
		});
	
		window.log = function(msg) {
			// log a message to the browser console
			//setTimeout(function() { throw new Error(msg); }, 0);
			console.log(msg);
		}
	
		/* formatting of GXD section */
		function formatGxdSection () {
			var gxdHeading = $("#gxdHeading");
			if (gxdHeading.length > 0) {
					var gxdHeight = $('#gxd').height();
					var headingHeight = $('#gxdHeading').height();
					var logo = $('#gxdLogo');
					var imageHeight = logo.height();
			
					var imagePad = Math.round( (gxdHeight - imageHeight) / 2) - headingHeight;
		
					// add padding to center the logo vertically in the expression ribbon
					if (imagePad > 0) {
						logo.css('padding-top', imagePad + 'px');
					}
			}
		}
		formatGxdSection();
		$(window).resize(formatGxdSection);

		$(".toggleImage").click(function(event) {
			var parent = $(this).parents(".row");
			// find the parent ribbon div, then toggle any extra content
			parent.find(".extra").toggle();
			// toggle the image class
			parent.find(".toggleImage").toggleClass("hdExpand");
			parent.find(".toggleImage").toggleClass("hdCollapse");

			var action="Open";
			if($(this).hasClass("hdCollapse")) {
				$(this).text("less");
				$(this).attr('title', 'Show Less');
			}
			if($(this).hasClass("hdExpand")) {
				$(this).text("more");
				$(this).attr('title', 'Show More');
				action="Close";
			}

			ga_logEvent('MarkerDetailPageEvent', parent[0].id, action);
		});
		
		try {
			// if no minimap for this page, degrade gracefully
			loadMinimap();
		} catch (e) {};

	});


