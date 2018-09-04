var makeYoyo = function(yoyoDiv) {
	// add to the list of DIVs to manage
	yoyos.push(yoyoDiv);
	
	// tie in the event handler for scrolling
	$(window).scroll(handleScroll);
	
	// check to see if any initial movement of the DIV is needed
	handleScroll();
}

var yoyos = [];

var handleScroll = function(event) {
	var viewportTop = $(window).scrollTop();

	for (var i in yoyos) {
		var yoyo = yoyos[i];
		var parent = yoyo.parent();
		var topBorder = parent.offset().top;
		var bottomBorder = topBorder + parent.height();

		if (viewportTop < topBorder) {
			// not yet scrolled off the top at all
			if (yoyo.css('position') != 'relative') {
				yoyo.css('position', 'relative');
			}

		} else if (viewportTop > bottomBorder) {
			// parent element is scrolled off and not visible
			if (yoyo.css('position') != 'relative') {
				yoyo.css('position', 'relative');
			}

		} else {
			// viewportTop is between topBorder & bottomBorder,
			// need to float the yoyo to be visible
			if (yoyo.css('position') != 'fixed') {
				yoyo.css('position', 'fixed');
				yoyo.css('z-index', '99999');
			}
			var yoyoTop = bottomBorder - yoyo.height();
			var topOffset = Math.min(0, yoyoTop - viewportTop);
			yoyo.css('top', topOffset + 'px');
		}
	}
}

