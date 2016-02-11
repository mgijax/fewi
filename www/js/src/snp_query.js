window.snpqry = {};		// namespace for this module

snpqry.form1Height = 618;
snpqry.form2Height = 680;

snpqry.qwrapOpenHeight = -1;	// set this later when collapsing
snpqry.qfDisplay = true;	// show the query form (true) or not (false)?

snpqry.setQueryFormDisplay = function(bool) {
    snpqry.qfDisplay = bool;
}

snpqry.setQueryFormHeight = function() {
//    var activeTab = "#form1";
//    if (YAHOO.util.Dom.get("tabs-1").style.display == "none") {
	snpqry.qwrapOpenHeight = snpqry.form2Height;
//    } else {
//	snpqry.qwrapOpenHeight = snpqry.form1Height;
//    }
}

snpqry.getQueryFormHeight = function() {
    return snpqry.qwrapOpenHeight;
}

snpqry.changeVisibility = function(id) {

    if (YAHOO.util.Dom.getStyle(id, 'display') == 'none'){
        YAHOO.util.Dom.setStyle(id, 'display', 'block');
    } else {
        YAHOO.util.Dom.setStyle(id, 'display', 'none');
    }
}

snpqry.hideQF = function(oCallback) {
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    
    snpqry.setQueryFormHeight();

    	attributes = { height: { to: 0  }};

	try {
    	    setText(toggleLink, "Click to modify search");
	} catch (err) {
	    if (toggleLink != null) {
		toggleLink.innerHTML = "Click to modify search";
	    }
	}

	if (toggleImg != null) {
    	    YAHOO.util.Dom.removeClass(toggleImg, 'qfCollapse');
    	    YAHOO.util.Dom.addClass(toggleImg, 'qfExpand');    	
	}
	snpqry.qfDisplay = false;
	snpqry.updateQFView(attributes, oCallback);
}

snpqry.showQF = function(oCallback) { 
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    
    var qf = YAHOO.util.Dom.get('qwrap');
    	attributes = { height: { to: snpqry.getQueryFormHeight() }};

    	YAHOO.util.Dom.setStyle(qf, 'height', '0px');
    	YAHOO.util.Dom.setStyle(qf, 'display', 'none');
	if (toggleImg != null) {
    	    YAHOO.util.Dom.removeClass(toggleImg, 'qfExpand');
    	    YAHOO.util.Dom.addClass(toggleImg, 'qfCollapse');
	}

    	try {
	    setText(toggleLink, "Click to hide search");
	} catch (err) {
	    if (toggleLink != null) {
		toggleLink.innerHTML = "Click to hide search";
	    }
	}

    	snpqry.qfDisplay = true;
    	snpqry.changeVisibility('qwrap');
	snpqry.updateQFView(attributes, oCallback);
}

snpqry.updateQFView = function(attributes, oCallback) {
    var myAnim = new YAHOO.util.Anim('qwrap', attributes);
	
    if (!snpqry.qfDisplay){
	myAnim.onComplete.subscribe(function(){
		snpqry.changeVisibility('qwrap');
	});
    } else if (attributes.height.to != 0) {
	myAnim.onComplete.subscribe(function(){
		$("#qwrap").height("auto");
	});
    }
	
    if ((oCallback !== undefined) && !YAHOO.lang.isNull(oCallback)){	
	myAnim.onComplete.subscribe(oCallback);
    }
	
    myAnim.duration = 0.75;
    myAnim.animate();
}

snpqry.toggleQF = function(oCallback) {
    if (snpqry.qfDisplay){
	snpqry.hideQF(oCallback);
    } else {            
	snpqry.showQF(oCallback);
	try { $("#facetDialog_c").css({'visibility' : 'hidden'}); } catch (e) {}
    }
}

snpqry.toggleLink = YAHOO.util.Dom.get("toggleQF");
if (!YAHOO.lang.isUndefined(snpqry.toggleLink)){
	YAHOO.util.Event.addListener("toggleQF", "click", snpqry.toggleQF);
}
snpqry.toggleImg = YAHOO.util.Dom.get("toggleImg");
if (!YAHOO.lang.isUndefined(snpqry.toggleImg)){
	YAHOO.util.Event.addListener("toggleImg", "click", snpqry.toggleQF);
}

snpqry.interceptSubmit = function(e) {
	YAHOO.util.Event.preventDefault(e);	
	snpqry.toggleQF(function(){
		var form = YAHOO.util.Dom.get('snpQueryForm');
		form.submit();
	});
};

YAHOO.util.Event.addListener("snpQueryForm", "submit", snpqry.interceptSubmit);

snpqry.resetQF = function (e) {
	var errors = YAHOO.util.Dom.getElementsByClassName('qfError');		
	YAHOO.util.Dom.setStyle ( errors , 'display' , 'none' );

	YAHOO.util.Event.preventDefault(e); 

	var form = YAHOO.util.Dom.get("form1");

	form.nomen.value = "";
	//form.rangeDropList.value = 2000;
	form.referenceStrain.value = "";
	form.searchBySameDiff.value = "";
	form.searchGeneByList.value = "marker_symbol";
	snpqry.resetRadio();

	var form = YAHOO.util.Dom.get("form2");

	form.chromosomeDropList.value = "";
	form.coordinate.value = "";
	form.coordinateUnitDropList.value = "bp";
	form.referenceStrain.value = "";
	form.searchBySameDiff.value = "";

	snpqry.selectAll();
};

snpqry.resetRadio = function() {
	$("input:radio").each( function() { this.checked = (this.value == 2000) || (this.value == ""); });
};

snpqry.deselectAll = function() {
	$("input:checkbox").each(function(){ this.checked = false; });
};

snpqry.selectAll = function() {
	//$("#wrapper input[type=button]")
	$("input:checkbox").each(function(){ this.checked = true; });
};

YAHOO.util.Event.addListener("form1", "reset", snpqry.resetQF);
YAHOO.util.Event.addListener("form2", "reset", snpqry.resetQF);

YAHOO.util.Event.addListener($("#form1 #deselectButton"), "click", snpqry.deselectAll);
YAHOO.util.Event.addListener($("#form1 #selectButton"), "click", snpqry.selectAll);
YAHOO.util.Event.addListener($("#form2 #deselectButton"), "click", snpqry.deselectAll);
YAHOO.util.Event.addListener($("#form2 #selectButton"), "click", snpqry.selectAll);
