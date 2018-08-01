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
	}
	if($(this).hasClass("hdExpand")) {
		$(this).text("more");
		$(this).attr('title', 'Show More');
		action="Close";
	}

	adjustDisplay(parent[0].id, action);
	ga_logEvent('StrainDetailPageEvent', parent[0].id, action);
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
var sdSnpDir = 'asc';				// direction of sort of snp table ('asc' or 'desc')

var initialize = function(fewiUrl, strainID) {
	sdFewiUrl = fewiUrl;
	sdStrainID = strainID;
}

var loadSnpTable = function(sortBy) {
	if ((sdStrainID == null) || (sdFewiUrl == null)) {
		console.log('Error: need to call initialize()');
		return;
	}

	// If this is same as last column sorted, swap the direction of the sort.
	if (sdSnpSortBy == sortBy) {
		if (sdSnpDir == 'asc') {
			sdSnpDir = 'desc';
		} else {
			sdSnpDir = 'asc';
		}
	} else {
		// Otherwise, this is a new column sort, go to default direction for that column.
		sdSnpSortBy = sortBy;
		if (sdSnpSortBy == 'strain') {
			sdSnpDir = 'asc';
		} else {
			sdSnpDir = 'desc';
		}
	}
	
	var myUrl = sdFewiUrl + 'strain/snpTable/' + sdStrainID + '?sortBy=' + sdSnpSortBy + '&dir=' + sdSnpDir;
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
		}
	});
}