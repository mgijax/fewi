
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
	console.log('ribbonID = ' + ribbonID);
	console.log('action = ' + action);
	if ((action == 'Open') && (ribbonID == 'snpRibbon')) {	
		$('#snpContainer').css('display', 'flex');
	}
}