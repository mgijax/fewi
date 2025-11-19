// Name: strain_detail.js
// Purpose: supports the strain detail page

//--- open/close event handling ---//

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
		makeViewPanelFloat();
	}
	if($(this).hasClass("hdExpand")) {
		$(this).text("more");
		$(this).attr('title', 'Show More');
		action="Close";
	}

	adjustDisplay(parent[0].id, action);
});

// Make any display adjustments needed when we open or close ribbons.  (Sometimes widths need to 
// be tweaked to help things line up.)
var adjustDisplay = function(ribbonID, action) {
	// fix widths related to SNP tables
	if ((action == 'Open') && (ribbonID == 'snpRibbon')) {	
		$('#snpContainer').css('display', 'flex');
	}
}

//--- SNP table functions ---//

var sdFewiUrl = null;				// value of configBean.FEWI_URL
var sdStrainID = null;				// current strain's primary ID
var sdSnpSortBy = 'strain';			// current sort of snp table ('strain' or chromosome)
var sdSnpDir = 'desc';				// direction of sort of snp table ('asc' or 'desc')
var sdMode = 'all';					// mode of table (all, same, diff)
var alreadyLoadedOnce = false;		// have we already loaded the SNP table once?  (for GA tracking)

var initialize = function(fewiUrl, strainID) {
	sdFewiUrl = fewiUrl;
	sdStrainID = strainID;
}

var loadSnpTable = function(sortBy, mode) {
	if ((sdStrainID == null) || (sdFewiUrl == null)) {
		console.log('Error: need to call initialize()');
		return;
	}

	// add a loading message to the pre-existing table (if there is one)
	$('#snpTable td:first').html('<div style="display:flex">'
		+ '<div><img src="' + sdFewiUrl + 'assets/images/loading.gif" style="height:20px"></div>'
		+ '<div style="padding-top:2px; font-weight: bold">Loading...</div></div>');

	// If this is same as last column sorted (and the same mode), swap the direction of the sort.
	if ((sdSnpSortBy == sortBy) && (sdMode == mode)) {
		if (sdSnpDir == 'asc') {
			sdSnpDir = 'desc';
		} else {
			sdSnpDir = 'asc';
		}
	} else if (sdMode != mode) {
		// change in mode (all, same, diff) but same sortBy & direction
		sdMode = mode; 
	} else {
		// Otherwise, this is a new column sort, go to default direction for that column.
		sdSnpSortBy = sortBy;
		if (sdSnpSortBy == 'strain') {
			sdSnpDir = 'asc';
		} else {
			sdSnpDir = 'desc';
		}
	}
	
	// We don't want a GA event tracked for the initial table load, but we do for subsequent interaction.
	alreadyLoadedOnce = true;
	var myUrl = sdFewiUrl + 'strain/snpTable/' + sdStrainID + '?sortBy=' + sdSnpSortBy + '&dir=' + sdSnpDir + '&mode=' + sdMode;
	
	$.ajax({'url': myUrl, 'datatype': 'html', 'success':
		function(html) {
			$('#snpContainer').html(html);

			// seems silly to make a table border the same color as the background, but we need it to help the
			// header cells line up with the color cells in the scrollable table below
			var snpHeaderBorderColor = $('#snpContainer').parent().css('background-color');
			$('#snpTableHeader th').css({
				'border-left' : '1px solid ' + snpHeaderBorderColor,
				'border-right' : '1px solid ' + snpHeaderBorderColor
			});
			
			// define click handling for column headers of table
			$(".snpHeaderCell").click(function(event) {
				loadSnpTable($(this).html().split(/[^0-9a-zA-Z]+/)[0], sdMode);
			});
			$("#comparisonStrainLabel").click(function(event) {
				loadSnpTable('strain', sdMode);
			});
			
			// define click handling for the radio buttons
			$('[name=mode]').click(function(event) {
				loadSnpTable(sdSnpSortBy, $(this)[0].value);
			});
			
			// ensure that the correct radio button is checked
			var buttons = $('[name=mode][value=' + sdMode + ']');
			if (buttons.length > 0) {
				buttons[0].checked = true;
			}
			
			makeViewPanelFloat();
		}
	});
}

var makeViewPanelFloat = function() {
	// make the View controls float when scrolling
	makeYoyo($('#snpViewPanel'));
}
