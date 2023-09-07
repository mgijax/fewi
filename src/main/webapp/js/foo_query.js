// this function handles the hide/show animation of the query form.
// query form must be inside the qwrap div structure for this to work.
var toggleQF = function(oCallback) {

    var qf = YAHOO.util.Dom.get('qwrap');
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    // set height so after close the qwrap is the right size
    var attributes = { height: { to: 165 }};
    // hide it
    if (!qDisplay){
    	attributes = { height: { to: 0  }};
    	setText(toggleLink, "Click to modify search");
    	YAHOO.util.Dom.removeClass(toggleImg, 'qfCollapse');
    	YAHOO.util.Dom.addClass(toggleImg, 'qfExpand');    	
    	qDisplay = true;
    } else {   
    // show it
    	YAHOO.util.Dom.setStyle(qf, 'height', '0px');
    	YAHOO.util.Dom.setStyle(qf, 'display', 'none');
    	YAHOO.util.Dom.removeClass(toggleImg, 'qfExpand');
    	YAHOO.util.Dom.addClass(toggleImg, 'qfCollapse');
    	setText(toggleLink, "Click to hide search");
    	qDisplay = false;
    	changeVisibility('qwrap');
    }
	var myAnim = new YAHOO.util.Anim('qwrap', attributes);
	
	if (qDisplay){
		myAnim.onComplete.subscribe(function(){
			changeVisibility('qwrap');
		});
	}
	
	if (!YAHOO.lang.isNull(oCallback)){	
		myAnim.onComplete.subscribe(oCallback);
	}
	
	myAnim.duration = 0.75;
	myAnim.animate();
};

// toggle the controls to reflect state of qf
var toggleLink = YAHOO.util.Dom.get("toggleQF");
if (!YAHOO.lang.isUndefined(toggleLink)){
	YAHOO.util.Event.addListener("toggleQF", "click", toggleQF);
}
var toggleImg = YAHOO.util.Dom.get("toggleImg");
if (!YAHOO.lang.isUndefined(toggleImg)){
	YAHOO.util.Event.addListener("toggleImg", "click", toggleQF);
}

// catch submit from foo query form so we can animate the hide
var interceptSubmit = function(e) {
	YAHOO.util.Event.preventDefault(e);	
	toggleQF(function(){
		var form = YAHOO.util.Dom.get('fooQueryForm');
		form.submit();
	});
};
YAHOO.util.Event.addListener("fooQueryForm", "submit", interceptSubmit);

// custom reset for qf so that reset actually clears QF when on summary page
var resetQF = function (e) {
	
	var errors = YAHOO.util.Dom.getElementsByClassName('qfError');		
	YAHOO.util.Dom.setStyle ( errors , 'display' , 'none' );

	YAHOO.util.Event.preventDefault(e); 
	var form = YAHOO.util.Dom.get("fooQueryForm");
	form.param1.value = "";
	form.param2.value = "";
	form.param3.value = "";

};
YAHOO.util.Event.addListener("fooQueryForm", "reset", resetQF);
