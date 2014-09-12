var myTabs = new YAHOO.widget.TabView("batchSource");

var toggleQF = function(oCallback) {
	
    var qf = YAHOO.util.Dom.get('qwrap');
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    
    var attributes = { height: { to: 310 }};

    if (!qDisplay){
    	attributes = { height: { to: 0  }};
    	setText(toggleLink, "Click to modify search");
    	YAHOO.util.Dom.removeClass(toggleImg, 'qfCollapse');
    	YAHOO.util.Dom.addClass(toggleImg, 'qfExpand');    	
    	qDisplay = true;
    } else {            
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

var toggleLink = YAHOO.util.Dom.get("toggleQF");
if (!YAHOO.lang.isUndefined(toggleLink)){
	YAHOO.util.Event.addListener("toggleQF", "click", toggleQF);
}
var toggleImg = YAHOO.util.Dom.get("toggleImg");
if (!YAHOO.lang.isUndefined(toggleImg)){
	YAHOO.util.Event.addListener("toggleImg", "click", toggleQF);
}

var interceptSubmit = function(e) {
	YAHOO.util.Event.preventDefault(e);	
	var form = YAHOO.util.Dom.get("batchQueryForm");
	toggleQF(function(form){
		document.forms["batchQueryForm"].submit();
	});
};

YAHOO.util.Event.addListener("batchQueryForm", "submit", interceptSubmit);

var resetQF = function (e) {
	YAHOO.util.Event.preventDefault(e); 
	var form = document.forms["batchQueryForm"];
	form.idType.selectedIndex = 0;
	form.ids.value = "";
	
	form.attributes[0].checked = true ;
	for (i = 1; i < form.attributes.length; i++){
		form.attributes[i].checked = false ;
	}
	
	form.association9.checked="checked";
	
	form.fileType1.checked="checked";
	form.idColumn.value = "1";
	
	form.attributes.nomenclature.checked="checked";
	form.association9.checked = "checked";

};

YAHOO.util.Event.addListener("batchQueryForm", "reset", resetQF);
