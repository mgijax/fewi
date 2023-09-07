/* Paginator: JQuery paginator controls, customized for MGI
 * Notes: Provides for multiple pagination controls per page, identified by CSS class.
 */

// holds CSS class names in which we've already instantiated a paginator control
var alreadyDone = {};

// Removes any existing paginator controls in the element(s) identified by the CSS class.
var pgClearPaginator = function(cssClass) {
	if (cssClass in alreadyDone) {
		delete alreadyDone[cssClass];
	}
	$("." + cssClass).html("");
}

// update the paginator(s) identified by the given cssClass; assumes we will replace the
// contents of any HTML elements (presumably DIVs) that have that class with an 
// updated paginator.  Do not include the '.' in the CSS class name.  Callback should take
// three parameters: cssClass name, page number, page size.
var pgUpdatePaginator = function(cacheName, cssClass, totalCount, pageLimit, callback, hasLastButton) {
	if (hasLastButton === null) { hasLastButton = false; }
	if (cssClass in alreadyDone) { return; }
	alreadyDone[cssClass] = true;
	
	if (totalCount == 0) {
		$("." + cssClass).html("");
		return;
	}
	
	console.log('instantiating paginators for .' + cssClass + ' with ' + totalCount + ' rows');
	if (totalCount == null) {
		alert("You cannot have a paginator with a null totalCount.");
	}
	if (pageLimit == null) { pageLimit = 100; }
	if (callback == null) {
		callback = function(page) {
			alert("You forgot to define a callback for the paginator (page " + page + ")");
		}
	}
	
	$("." + cssClass).paging(totalCount, {
        format: '[< nncnn >]',		// first, prev, five page numbers with current in middle, next, last
        perpage: pageLimit,
        onSelect: function(page) {
            callback(cacheName, page);
        },
        onFormat: function(type) {
            switch (type) {
                case 'block': // n and c
                   	if (this.active) {
	                   	if (this.value != this.page) return '<a href="#">' + this.value + '</a> ';
	                   	else return '<span class="current">' + this.value + '</span> ';
                   	}
	                return '<span class="disabled">' + this.value + '</span> ';
                case 'next': // >
                    if (this.active) return '<a href="#">next&gt;</a> ';
	                return '<span class="disabled">next&gt;</span> ';
                case 'prev': // <
                	if (this.active) return '<a href="#">&lt;prev</a> ';
	                return '<span class="disabled">&lt;prev</span> ';
                case 'first': // [
                    if (this.active) return '<a href="#">&lt;&lt;first</a> ';
	                return '<span class="disabled">&lt;&lt;first</span> ';
                case 'last': // ]
                	if (hasLastButton != true) return '';
                    if (this.active) return '<a href="#">last&gt;&gt;</a>';
	                return '<span class="disabled">last&gt;&gt;</span> ';
            }
        }
    });
	// remove the underlines from the paginator's links
	$('.' + cssClass + ' a').css({ 'text-decoration': 'none'})
};