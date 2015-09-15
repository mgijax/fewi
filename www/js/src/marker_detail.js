
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
	
		window.toggleHomologyDetails = function() {
			toggle ("downArrowHomologs");
			toggle ("rightArrowHomologs");
			toggle ("humanHomologDetails");
			if (mgihomeUrl != null) {
				hitUrl (mgihomeUrl + "other/monitor.html", "toggleHomologyDetails=1");
			}
		}
	
		/* cluster membership */
	
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

		window.toggleHomologTags = function() {
			toggle("rightArrowHomologTag");
			toggle("downArrowHomologTag");
			toggle("moreHomologs");
		}

		window.alignLocationRibbonDivs = function(name) {
			if (name == 'LocationRibbon') {
				var coordTopDiv = document.getElementById('coordsTopDiv');
				var geneticTopDiv = document.getElementById('geneticTopDiv');
				var minimapDiv = document.getElementById('minimapDiv');

				if ((coordTopDiv != null) && (geneticTopDiv != null) && (minimapDiv != null)) {
					geneticTopDiv.style.height =
						coordTopDiv.getBoundingClientRect().height + 'px';
				}
			}
		};

		window.toggleRibbon = function(name) {
			var span = "toggle" + name;
			var opened = "opened" + name;
			var closed = "closed" + name;

			if(YAHOO.util.Dom.hasClass(span, 'hdCollapse')) {
				YAHOO.util.Dom.removeClass(span, 'hdCollapse');
				YAHOO.util.Dom.addClass(span, 'hdExpand');
				YAHOO.util.Dom.get(span).title = "Show More";
				pageTracker._trackEvent("MarkerDetailPageEvent", "close", name);
			} else if(YAHOO.util.Dom.hasClass(span, 'hdExpand')) {
				YAHOO.util.Dom.removeClass(span, 'hdExpand');
				YAHOO.util.Dom.addClass(span, 'hdCollapse');
				YAHOO.util.Dom.get(span).title = "Show Less";
				pageTracker._trackEvent("MarkerDetailPageEvent", "open", name);
			}

			window.showHideById(opened);
			window.showHideById(closed);

			window.alignLocationRibbonDivs(name);
		}

		window.showHideById = function showHideById(id) {
			if(document.getElementById(id).style.display=='none') {
				document.getElementById(id).style.display = '';
			} else {
				document.getElementById(id).style.display = 'none';
			}
		}

		$(".toggleImage").click(function(event) {
			var parent = $(this).parents(".row");
			// find the parent ribbon div, then toggle any extra content
			parent.find(".extra").toggle();
			// toggle the image class
			parent.find(".toggleImage").toggleClass("hdExpand");
			parent.find(".toggleImage").toggleClass("hdCollapse");
		});

	});


