
var toggleQF = function(oCallback) {

    var qf = YAHOO.util.Dom.get('qwrap');
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    
    var attributes = { height: { to: 405 }};

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
	toggleQF(function(){
		var form = YAHOO.util.Dom.get('referenceQueryForm');
		form.submit();
	});
};

YAHOO.util.Event.addListener("referenceQueryForm", "submit", interceptSubmit);

(function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource(fewiurl + "autocomplete/author");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList: "summaryRows", fields:["author", "isGenerated"]};
    //oDS.maxCacheEntries = 10;
    oDS.connXhrMode = "cancelStaleRequests";

    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("author", "authorContainer", oDS);
    // Throttle requests sent
    oAC.queryDelay = .3;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 5000;
    oAC.forceSelection = true;
    oAC.delimChar = ";";
    
    oAC.formatResult = function(oResultData, sQuery, sResultMatch) {
    	   var sKey = sResultMatch;
    	 
    	   // some other piece of data defined by schema
    	   var isGenerated = oResultData[1];
    	   if (isGenerated){
    		   sKey = sKey + " <span class='autocompleteHighlight'>(all)</span>";
    	   }

    	  return (sKey);
    	}; 
    	
    var toggleVis = function(){
        if (YAHOO.util.Dom.getStyle('authHelp', 'display') == 'none'){
            YAHOO.util.Dom.setStyle('authHelp', 'display', 'block');
        }
    };
    
    oAC.itemSelectEvent.subscribe(toggleVis); 

    return {
        oDS: oDS,
        oAC: oAC
    };
})();


(function(){
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource(fewiurl + "autocomplete/journal");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList : "resultStrings"};
    oDS.maxCacheEntries = 10;
    oDS.connXhrMode = "cancelStaleRequests";

    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("journal", "journalContainer", oDS);
    // Throttle requests sent
    oAC.queryDelay = .3;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 5000;
    oAC.forceSelection = false;
    oAC.delimChar = ";";
    
    var toggleVis = function(){
        if (YAHOO.util.Dom.getStyle('journalHelp', 'display') == 'none'){
            YAHOO.util.Dom.setStyle('journalHelp', 'display', 'block');
        }
    };
    
    oAC.itemSelectEvent.subscribe(toggleVis); 
    return {
        oDS: oDS,
        oAC: oAC
    };
})();

var resetQF = function (e) {
	YAHOO.util.Dom.setStyle('authHelp', 'display', 'none');
	YAHOO.util.Dom.setStyle('journalHelp', 'display', 'none');
	
	var errors = YAHOO.util.Dom.getElementsByClassName('qfError');		
	YAHOO.util.Dom.setStyle ( errors , 'display' , 'none' );

	YAHOO.util.Event.preventDefault(e); 
	var form = YAHOO.util.Dom.get("referenceQueryForm");
	form.author.value = "";
	form.authorScope1.checked="checked";
	form.journal.value = "";
	form.year.value = "";
	form.text.value = "";
	form.inTitle1.checked="checked"
	form.inAbstract1.checked="checked"
	form.id.value = "";
};

YAHOO.util.Event.addListener("referenceQueryForm", "reset", resetQF);

