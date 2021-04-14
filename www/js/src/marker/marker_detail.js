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

			adjustDisplay(parent[0].id, action);
			ga_logEvent('MarkerDetailPageEvent', parent[0].id, action);
		});
		
		try {
			// if no minimap for this page, degrade gracefully
			loadMinimap();
		} catch (e) {};

	});

// Make any display adjustments needed when we open or close ribbons.  (Sometimes widths need to 
// be tweaked to help things line up.)
var adjustDisplay = function(ribbonID, action) {
	// fix widths related to strain gene table
	if ((action == 'Open') && (ribbonID == 'strainRibbon')) {	
		$('#strainGenesTableControls').width($('#strainGenesTableDiv').width());
		$('#sgLeftWrapper').width($('#strainGenesTableDiv').width());
	}
}
	
/*****************************************/
/*** JS support for strain marker form ***/
/*****************************************/
	
// all strains included in list of strain genes
var allStrains = [ '129S1/SvImJ', 'A/J', 'AKR/J', 'BALB/cJ', 'C3H/HeJ', 'C57BL/6J', 'C57BL/6NJ', 'CAROLI/EiJ',
	'CAST/EiJ', 'CBA/J', 'DBA/2J', 'FVB/NJ', 'LP/J', 'NOD/ShiLtJ', 'NZO/HlLtJ', 'PWK/PhJ', 'SPRET/EiJ', 'WSB/EiJ' ];

// list of names for DO/CC Founder strains
var parentalStrains = [ '129S1/SvImJ', 'A/J', 'C57BL/6J', 'CAST/EiJ', 'NOD/ShiLtJ', 'NZO/HlLtJ', 'PWK/PhJ', 'WSB/EiJ' ];

// converts any slashes occurring in string 's' to be underscores
var slashToUnderscore = function(s) {
	return s.replace(/\//g, '_');
}

// removes any slashes occurring in string 's'
var noSlash = function(s) {
	return s.replace(/\//g, '');
}

// get the full URL to Sanger SNPs, including those strains currently checked on the form
var getSangerUrl = function() {
	var st = "";
	var checkboxes = $('[type=checkbox][name=seqs]');
	for (i = 0; i < checkboxes.length; i++) {
		if (checkboxes[i].checked) {
			for (j = 0; j < allStrains.length; j++) {
				if (checkboxes[i].value.indexOf('_' + noSlash(allStrains[j]) + '_') >= 0) {
					st = st + '&st=' + slashToUnderscore(allStrains[j]);
				}
			}
		}
	}
	return getUrl('sanger') + st.toLowerCase();
}

// set all strain gene checkboxes to be not checked
var clearStrainGeneCheckboxes = function() {
	var checkboxes = $('[type=checkbox][name=seqs]');
	for (i = 0; i < checkboxes.length; i++) {
		checkboxes[i].checked = false;
	}
}

// check all the DO/CC Founder strains in the table of strain genes
var clickParentalStrainGenes = function() {
	clearStrainGeneCheckboxes();
	var checkboxes = $('[type=checkbox][name=seqs]');
	for (i = 0; i < checkboxes.length; i++) {
		for (j = 0; j < parentalStrains.length; j++) {
			// strain name (minus the slash) is embedded in the ID that is the checkbox's value string,
			// bordered by underscores on either side
			if (checkboxes[i].value.indexOf('_' + noSlash(parentalStrains[j]) + '_') >= 0) {
				checkboxes[i].checked = true;
			}
		}
	}
}

// get a munged strain name from 's', which is either an ID or a valid strain name.  The munged
// strain name has no slashes or other special characters.
var getMungedStrainName = function(s) {
	// If this is a strain gene ID, it will contain two underscores.  The strain name is between them.
	var pieces = s.split('_');
	var name = s;
	if (pieces.length == 3) {
		name = pieces[1];
	}
	return name.replace(/\//g, '').replace('SPRETEiJ', 'SPRETUSEiJ');
}

// handle when the strain ribbon's Go button is clicked
var strainRibbonGoButtonClick = function() {
	var option = $('#strainOp :selected')[0].value;
	var form = $('#strainMarkerForm')[0];
	
	// map of munged strain names that were checked by the user
	var checked = {};
	var checkboxes = $('[type=checkbox][name=seqs]');

	for (i = 0; i < checkboxes.length; i++) {
		if (checkboxes[i].checked) {
			checked[getMungedStrainName(checkboxes[i].value)] = 1;
		}
	}
	
	// if no boxes checked, give a message and bail out
	if ($.isEmptyObject(checked)) {
		alert("You must select at least one strain using the checkboxes in the right column.");
		return;
	}
		
	if (option == 'fasta') {
		// simples one -- just get the base pairs from seqfetch.
		
		ga_logEvent('MarkerDetailPageEvent', 'strainMarkerTable', 'FASTA download');
		form.action = getUrl('seqfetch');
		form.submit();
		
	} else if (option == 'mgv') {
		// We only want strains to appear in MGV if they're in the list of strains that have checks from the user.
		// The base URL has all of them, so we need to remove any unchecked ones.

		ga_logEvent('MarkerDetailPageEvent', 'strainMarkerTable', 'forward to MGV');

		// We already know which checkboxes were checked; now we need to eliminate unwanted ones from the MGV URL.
		var urlPieces = getUrl('mgv').split('&');
		var url = '';
		
		for (j = 0; j < urlPieces.length; j++) {
			if (url.length > 0) { url = url + '&'; }			// add joiner between parameters

			if (urlPieces[j].startsWith('genomes=')) {
				url = url + 'genomes=';							// add parameter name back in
				var strains = urlPieces[j].split('=')[1].split(',');
				var firstStrain = true;
				for (s = 0; s < strains.length; s++) {
					if (getMungedStrainName(strains[s]) in checked) {
						if (!firstStrain) { url = url + ','; }
						url = url + strains[s];
						firstStrain = false;
					}
				}
				
			} else {
				// other parameters can go back into the url as-is
				url = url + urlPieces[j];
			}
		}
		
		window.open(url, '_blank');
		
	} else if (option == 'snps') {
		window.open(getSangerUrl(), '_blank');
		ga_logEvent('MarkerDetailPageEvent', 'strainMarkerTable', 'forward to Sanger SNP QF');
		
	} else {
		console.log('Unrecognized value for strainOp: ' + option);
	}
}

// cache of URLs needed for the strain marker form, configured in the JSP (which has
// access to configBean and externalUrls)
urls = {}

// associate the given 'name' with the given 'url', so we can look it up in Javascript
var configureUrl = function(name, url) {
	urls[name] = url;
}

// get the URL associated with the given 'name' (as set up using configureUrl)
var getUrl = function(name) {
	if (name in urls) {
		return urls[name];
	}
	console.log('Unrecognized name for requested URL: ' + name);
	return null;
}