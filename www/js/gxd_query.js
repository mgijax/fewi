// Form display toggle
//   false = form is displayed
var qDisplay = false;

// The string that gets passed via the AJAX call   
var querystring = "";

// HTML/YUI page widgets
YAHOO.namespace("gxd.container"); 
YAHOO.gxd.container.panelVocab = new YAHOO.widget.Panel("gxdVocabHelp", { width:"520px", draggable:false, visible:false, constraintoviewport:true } ); 
YAHOO.gxd.container.panelVocab.render(); 
YAHOO.util.Event.addListener("gxdVocabHelpImage", "mouseover", YAHOO.gxd.container.panelVocab.show, YAHOO.gxd.container.panelVocab, true); 

//GXD tooltips
var tsTooltips = {
		1:"One cell egg",
		2:"Beginning of cell division",
		3:"Morula",
		4:"Advanced division/segmentation",
		5:"Blastocyst",
		6:"Implantation",
		7:"Formation of egg cylinder",
		8:"Differentiation of egg cylinder",
		9:"Advanced endometrial reaction; prestreak",
		10:"Amnion; midstreak",
		11:"Neural plate, presomite stage; no allantoic bud",
		12:"First somites; late head fold",
		13:"Turning",
		14:"Formation & closure anterior neuropore",
		15:"Formation of posterior neuropore, forelimb bud",
		16:"Closure post. neuropore, hindlimb & tail bud",
		17:"Deep lens indentation",
		18:"Closure lens vesicle",
		19:"Complete separation of lens vesicle",
		20:"Earliest sign of fingers",
		21:"Anterior footplate indented, marked pinna",
		22:"Fingers separate distally",
		23:"Toes separate",
		24:"Reposition of umbillical hernia",
		25:"Fingers and toes joined together",
		26:"Long whiskers",
		28:"Postnatal development"
		};
var tsBox = YAHOO.util.Dom.get("theilerStage");
for(var i=0; i< tsBox.children.length; i++)
{
	var option = tsBox.children[i];
	// check if we've defined the tooltip for this option
	if(tsTooltips[option.value])
	{
		var ttText = "<b>"+option.text+"</b>"+
			"<br/>"+tsTooltips[option.value];
		var tt = new YAHOO.widget.Tooltip("tsTT"+i,{context:option, text:ttText,showdelay:1000});
	}
}

// GXD form tab control
YAHOO.widget.Tab.prototype.ACTIVE_TITLE = '';
var tabView = new YAHOO.widget.TabView('expressionSearch');

//GXD age/stage tab control
//YAHOO.widget.Tab.prototype.ACTIVE_TITLE = '';
//var ageStageTabs = new YAHOO.widget.TabView('ageStage');

// Updates the "You searched for" section
var updateQuerySummary = function() {
	var summaryDiv = new YAHOO.util.Element('searchSummary');
	summaryDiv.innerHTML = "";
	var searchParams = summaryDiv.getElementsByTagName('div');

	// if this is the first load of the page, the children will not have
	// been created yet.
	if (searchParams.length == 0) {
		// Create the place holder
		var el = new YAHOO.util.Element(document.createElement('div'));
		el.appendTo(summaryDiv);
		searchParams = summaryDiv.getElementsByTagName('div')[0];
	} else {
		searchParams = searchParams[0];
	}

	// Remove all the existing summary items
	searchParams.innerHTML = "";
	var el = new YAHOO.util.Element(document.createElement('span'));
	var b = new YAHOO.util.Element(document.createElement('b'));
	var newContent = document.createTextNode("You searched for: ");
	b.appendChild(newContent);
	el.appendChild(b);
	el.appendChild(new YAHOO.util.Element(document.createElement('br')));
	el.appendTo(searchParams);
	
	// Create all the relevant search parameter elements
	if (YAHOO.util.Dom.get('nomenclature').value != "") {
		
		// Create a span
		var el = new YAHOO.util.Element(document.createElement('span'));
		//add the text node to the newly created span
		el.appendChild(document.createTextNode("Gene nomenclature: "));

		// Create a bold
		var b = new YAHOO.util.Element(document.createElement('b'));
		var newContent = document.createTextNode(YAHOO.util.Dom.get('nomenclature').value);
		// Build and append the nomenclature query parameter section
		b.appendChild(newContent);
		el.appendChild(b);
		
		var sp = new YAHOO.util.Element(document.createElement('span'));
		sp.addClass("smallGrey");
		sp.appendChild(document.createTextNode(" current symbol, name, synonyms"));
		el.appendChild(sp);
		
		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendTo(searchParams);
	}

	if (YAHOO.util.Dom.get('vocabTerm').value != "" && YAHOO.util.Dom.get("annotationId").value!="") {
		
		// Create a span
		var el = new YAHOO.util.Element(document.createElement('span'));
		//add the text node to the newly created span
		el.appendChild(document.createTextNode("Genes annotated to "));

		// Create a bold
		var b = new YAHOO.util.Element(document.createElement('b'));
		var c = YAHOO.util.Dom.get('vocabTerm').value;
		var s = c.split(" - ");
		var newValue = document.createTextNode(s[1] + ": "+s[0]);
		// Build and append the nomenclature query parameter section
		b.appendChild(newValue);
		el.appendChild(b);

		var sp = new YAHOO.util.Element(document.createElement('span'));
		sp.addClass("smallGrey");
		sp.appendChild(document.createTextNode(" includes subterms"));
		el.appendChild(sp);

		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendTo(searchParams);
	}
	// do detected
	var detectedText = "Assayed";
	// 1 = Yes, 2 = No
	if(YAHOO.util.Dom.get("detected1").checked)
	{
		detectedText = "Detected";
	}
	if(YAHOO.util.Dom.get("detected2").checked)
	{
		detectedText = "Not detected";
	}

	// do structure
	// Create all the relevant search parameter elements
	if (YAHOO.util.Dom.get('structure').value != "") {
		
		el = new YAHOO.util.Element(document.createElement('span'));

		b = new YAHOO.util.Element(document.createElement('b'));
		b.appendChild(document.createTextNode(detectedText));
		el.appendChild(b);

		el.appendChild(document.createTextNode(" in "));

		b = new YAHOO.util.Element(document.createElement('b'));
		b.appendChild(document.createTextNode(YAHOO.util.Dom.get('structure').value));
		el.appendChild(b);

		var sp = new YAHOO.util.Element(document.createElement('span'));
		sp.addClass("smallGrey");
		sp.appendChild(document.createTextNode(" includes synonyms & substructures"));
		el.appendChild(sp);

		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendTo(searchParams);


	} else {

		el = new YAHOO.util.Element(document.createElement('span'));

		b = new YAHOO.util.Element(document.createElement('b'));
		b.appendChild(document.createTextNode(detectedText));
		el.appendChild(b);

		el.appendChild(document.createTextNode(" in "));

		b = new YAHOO.util.Element(document.createElement('b'));
		b.appendChild(document.createTextNode("any structures"));
		el.appendChild(b);

		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendTo(searchParams);
	}



	// If the user selected age to search by, show the age display,
	// otherwise show the TS display as default
	var age = YAHOO.util.Dom.get('age');

	if (age.parentNode.className != "yui-hidden") {

		// do age
		var ages="";
		var _ages = [];
		for(var key in age.children)
		{
			if(age[key].selected)
			{
				// set to "Any" if stage "ANY" appears anywhere in the list
				if(age[key].value=="ANY") ages = "Any";
				else _ages.push(age[key].innerHTML);
			}
		}

		el = new YAHOO.util.Element(document.createElement('span'));
		el.appendChild(document.createTextNode("at age(s): "));

		// ensure that "Any" is displayed if somehow no ages are selected
		if (_ages.length == 0 || ages != "") {
			b = new YAHOO.util.Element(document.createElement('b'));
			b.appendChild(document.createTextNode("Any"));
			el.appendChild(b);
		} else {
			var cnt = 0;
			for(var i in _ages) {
				b = new YAHOO.util.Element(document.createElement('b'));
				var ageText = "";
				if(cnt == 0) ageText += "(";
				ageText += _ages[i];
				if(cnt == _ages.length-1) ageText+=")";
				b.appendChild(document.createTextNode(ageText));
				el.appendChild(b);
				if(cnt < _ages.length-1) el.appendChild(document.createTextNode(" or "));
				cnt+=1;
			}
		}
		
		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendTo(searchParams);
	} else {

		// Build and append the theiler stage query parameter section
		var ts = YAHOO.util.Dom.get('theilerStage');
		var _stages = [];
		for(var key in ts.children)
		{
			if(ts[key].selected)
			{
				// set to "Any" if stage "0" appears anywhere in the list
				if(ts[key].value=="0") stages = "Any";
				else _stages.push("TS:"+ts[key].value);
			}
		}

		el = new YAHOO.util.Element(document.createElement('span'));
		el.appendChild(document.createTextNode("at Developmental stage(s): "));

		// ensure that "Any" is displayed if somehow no ages are selected
		if (_stages.length == 0) {
			b = new YAHOO.util.Element(document.createElement('b'));
			b.appendChild(document.createTextNode("Any"));
			el.appendChild(b);
		} else {
			var cnt = 0;
			for(var i in _stages) {
				b = new YAHOO.util.Element(document.createElement('b'));
				var stageText = "";
				if(cnt == 0) stageText +="(";
				stageText += _stages[i];
				if(cnt == _stages.length-1) stageText += ")";
				b.appendChild(document.createTextNode(stageText));
				el.appendChild(b);
				if(cnt < _stages.length-1) el.appendChild(document.createTextNode(" or "));
				cnt+=1;
			}
		}
		
		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendTo(searchParams);
	}


	// do genetic background
	var gbText = "Specimens: ";
	if(YAHOO.util.Dom.get("mutatedSpecimen").checked)
	{
		var mutatedIn = YAHOO.util.Dom.get("mutatedIn");
		if (mutatedIn.value != "")
		{
			// mutated in specific nomenclature
			el = new YAHOO.util.Element(document.createElement('span'));
			el.appendChild(document.createTextNode(gbText));
			b = new YAHOO.util.Element(document.createElement('b'));
			b.appendChild(document.createTextNode("Mutated in "+mutatedIn.value));
			el.appendChild(b);
			
			var sp = new YAHOO.util.Element(document.createElement('span'));
			sp.addClass("smallGrey");
			sp.appendChild(document.createTextNode(" current symbol, name, synonyms"));
			el.appendChild(sp);
			
			el.appendChild(new YAHOO.util.Element(document.createElement('br')));
			el.appendTo(searchParams);
		}
	}
	else if (YAHOO.util.Dom.get("isWildType").checked)
	{
		// only wild type specimens
		el = new YAHOO.util.Element(document.createElement('span'));
		el.appendChild(document.createTextNode(gbText));
		b = new YAHOO.util.Element(document.createElement('b'));
		b.appendChild(document.createTextNode("Wild type only"));
		el.appendChild(b);
		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendTo(searchParams);
	}
	
	// do assay types
	var boxes = YAHOO.util.Selector.query(".assayType");
	var assayTypes = [];
	for(var key in boxes)
	{
		var box = boxes[key];
		if(box.checked)
		{
			assayTypes.push(box.value);
		}
	}
	if(assayTypes.length > 0 && !YAHOO.util.Dom.get("assayType-ALL").checked)
	{

		el = new YAHOO.util.Element(document.createElement('span'));
		el.appendChild(document.createTextNode("Assayed by "));

		var cnt = 0;
		for(var i in assayTypes) {
			b = new YAHOO.util.Element(document.createElement('b'));
			var assayTypeText = "";
			if(cnt == 0) assayTypeText +="(";
			assayTypeText += assayTypes[i];
			if(cnt == assayTypes.length-1) assayTypeText += ")";
			b.appendChild(document.createTextNode(assayTypeText));
			el.appendChild(b);
			if(cnt < assayTypes.length-1) el.appendChild(document.createTextNode(" or "));
			cnt+=1;
		}
		
		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendTo(searchParams);
	}
};


//
// Handle the animation for the queryform
//
var QFHeight = 704;
var toggleQF = function(oCallback,noAnimate) {
	if(noAnimate==undefined) noAnimate=false;
	
    var outer = YAHOO.util.Dom.get('outer');
	YAHOO.util.Dom.setStyle(outer, 'overflow', 'hidden');
    var qf = YAHOO.util.Dom.get('qwrap');
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    attributes =  { height: { to: 0 }};
    	
    if (!YAHOO.lang.isNull(toggleLink) && !YAHOO.lang.isNull(toggleImg)
    		) {
	    attributes = { height: { to: QFHeight }};
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
	}
    var animComplete;
    // define what to do after the animation finishes
    if(qDisplay)
    {
    	animComplete = function(){
    		changeVisibility('qwrap');
			//YAHOO.util.Dom.get("qwrap").show();
			//YAHOO.util.Dom.get("expressionSearch").show();
			//YAHOO.util.Dom.get('nomenclature').focus();
    	};
    }
    else
    {
    	animComplete = function(){
    		YAHOO.util.Dom.setStyle(outer, 'overflow', 'visible');
			
			if (/MSIE (\d+.\d+);/.test(navigator.userAgent)) {
				// Reduce the ie/yui quirky behavior
		    	YAHOO.util.Dom.setStyle(qf, 'display', 'none');
		    	YAHOO.util.Dom.setStyle(qf, 'display', 'block');
		    	YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('expressionSearch'), 'display', 'none');
		    	YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('expressionSearch'), 'display', 'block');
		    	YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('toggleQF'), 'display', 'none');
		    	YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('toggleQF'), 'display', 'block');				
			}
    	};
    }
    if(!noAnimate)
    {
		var myAnim = new YAHOO.util.Anim('qwrap', attributes);
		
		myAnim.onComplete.subscribe(animComplete);
	
		if (!YAHOO.lang.isNull(oCallback)){	
			myAnim.onComplete.subscribe(oCallback);
		}
		
		myAnim.duration = 0.75;
		myAnim.animate();
    }
    else
    {
    	YAHOO.util.Dom.setStyle("qwrap", 'height', attributes["height"]["to"]+'px');
    	animComplete();
    }
};


// Attache the animation handler to the toggleQF div
var toggleLink = YAHOO.util.Dom.get("toggleQF");
if (!YAHOO.lang.isUndefined(toggleLink)){
	YAHOO.util.Event.addListener("toggleQF", "click", toggleQF);
}

// Open all the controls tagged with the summaryControl class
function openSummaryControl()
{
	var summaryControls = YAHOO.util.Selector.query(".summaryControl");
	for(var i=0;i<summaryControls.length;i++)
	{
		YAHOO.util.Dom.setStyle(summaryControls[i],"display","block");
	}		
	// also ensure that the qf is closed
	// call the toggle function with no animation
	if(qDisplay==false) toggleQF(null,true);
}

// Close all the controls tagged with the summaryControl class
function closeSummaryControl()
{
	var summaryControls = YAHOO.util.Selector.query(".summaryControl");
	for(var i=0;i<summaryControls.length;i++)
	{
		YAHOO.util.Dom.setStyle(summaryControls[i],"display","none");
	}		
	// also ensure that qf is open
	// call the toggle function with no animation
	if(qDisplay==true) toggleQF(null,true);
};

// Instead of submitting the form, do an AJAX request
var interceptSubmit = function(e) {
	YAHOO.util.Event.preventDefault(e);	
	
	if (!runValidation()){
		// Do not allow any content to overflow the outer
		// div when it is hiding
		var outer = YAHOO.util.Dom.get('outer');
		YAHOO.util.Dom.setStyle(outer, 'overflow', 'hidden');
		
		// Set the global querystring to the form values
		window.querystring = getQueryString(YAHOO.util.Dom.get("gxdQueryForm"));
		
		newQueryState = true;
		if(typeof resultsTabs != 'undefined')
			resultsTabs.selectTab(2);
		if(typeof gxdDataTable != 'undefined')
			gxdDataTable.setAttributes({ width: "100%" }, true);
	    
		toggleQF(openSummaryControl);
	}
};

YAHOO.util.Event.addListener("gxdQueryForm", "submit", interceptSubmit);

/*
 * The following functions handle form validation/restriction
 */

var setVisibility = function(id, isVisible) {
    if (isVisible){
        YAHOO.util.Dom.setStyle(id, 'display', 'block');
    } else {
        YAHOO.util.Dom.setStyle(id, 'display', 'none');
    }
};

var setSubmitDisabled = function(isVisible) {
	// toggle the submit buttons
	var sub1 = YAHOO.util.Dom.get("submit1");
	var sub2 = YAHOO.util.Dom.get("submit2");
	sub1.disabled = isVisible;
	sub2.disabled = isVisible;
};

// Validation function for preventing submit of both vocab and nomen searches
var geneRestriction  = function() {
	var form = YAHOO.util.Dom.get("gxdQueryForm");
	var nomen = form.nomenclature.value;
	var vocab = form.vocabTerm.value;

	// determine error state
	var setVisible = false;	
	if (nomen.replace(/^\s+|\s+$/g, '') != '' && vocab.replace(/^\s+|\s+$/g, '') != ''){
		setVisible = true;
	} 
	setSubmitDisabled(setVisible);
	// hide/show error message
	setVisibility('geneError', setVisible);
	return setVisible;
};

var mutationRestriction  = function() {

	var form = YAHOO.util.Dom.get("gxdQueryForm");
	var mutated = form.mutatedIn.value;
	var selected = '';
	var selectVisible = false;
	var geneVisible = false;
	
    var radios = document.getElementsByName('geneticBackground');
    for (i = 0; i < radios.length; i++) {
        if (radios[i].checked) {
            selected =  radios[i].id;
        }
    }
	if (mutated.replace(/^\s+|\s+$/g, '') != '') {
		if (selected != 'mutatedSpecimen') {
			selectVisible = true;
		}
	}  else {
		if (selected == 'mutatedSpecimen'){
			geneVisible = true;
		}
	}
	
	setVisibility('mutatedSelectError', selectVisible);
	setVisibility('mutatedGeneError', geneVisible);
	return selectVisible | geneVisible;
};

var runValidation  = function(){
	var result = geneRestriction() | mutationRestriction();
	setSubmitDisabled(result);
	return result;
};

YAHOO.util.Event.addListener(YAHOO.util.Dom.get("nomenclature"), "keyup", runValidation); 
YAHOO.util.Event.addListener(YAHOO.util.Dom.get("nomenclature"), "change", runValidation); 
YAHOO.util.Event.addListener(YAHOO.util.Dom.get("vocabTermAutoComplete"), "keyup", runValidation); 

YAHOO.util.Event.addListener(YAHOO.util.Dom.get("mutatedIn"), "keyup", runValidation); 
YAHOO.util.Event.addListener(YAHOO.util.Dom.get("mutatedIn"), "change", runValidation); 
YAHOO.util.Event.addListener(YAHOO.util.Dom.get("mutatedSpecimen"), "click", runValidation);
YAHOO.util.Event.addListener(YAHOO.util.Dom.get("isWildType"), "click", runValidation); 
YAHOO.util.Event.addListener(YAHOO.util.Dom.get("allSpecimen"), "click", runValidation); 

// clear the vocab input box when the user changes the value after selecting
// a term
var vocabACState = "";

var clearVocabTerm = function(e) {
	// don't do anything if vocabACState is not set, or if enter key (or TAB) is pressed
	var keyCode = e.keyCode;
	if(e.charCode) keyCode = e.charCode;
	// ignore keycodes between 9 and 40 (ctrl, shift, enter, tab, arrows, etc)
	if (vocabACState != "" && (keyCode <9 || keyCode >40)){
		var termIdInput = YAHOO.util.Dom.get("annotationId");
		var vocabInput = YAHOO.util.Dom.get("vocabTerm");
		if (vocabInput.value!="" && vocabInput.value == window.vocabACState) {
			// clear the state, the visual input, and the hidden term field input
			window.vocabACState = "";
			vocabInput.value = "";
			termIdInput.value = "";
		}
	}
};

YAHOO.util.Event.addListener(YAHOO.util.Dom.get("vocabTerm"), "keypress", clearVocabTerm);


/*
 * Vocab Term Auto Complete Section
 */

(function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource(fewiurl + "autocomplete/vocabTerm");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList: "summaryRows", 
    		fields:["markerCount", "termId", "formattedTerm","inputTerm","hasExpression"]};
    //oDS.maxCacheEntries = 10;
    oDS.connXhrMode = "cancelStaleRequests";
    
    var oAC = new YAHOO.widget.AutoComplete("vocabTerm", "vocabTermContainer", oDS);
    
    //HACK: maybe we should figure out how to subclass this widget if we want to reuse it.
    // override behavior of selectItem event
    oAC._selectItem = function(elListItem) {
	    this._bItemSelected = true;
	    this._updateValue(elListItem);
	    this._sPastSelections = this._elTextbox.value;
	    this._clearInterval();
	    this.itemSelectEvent.fire(this, elListItem, elListItem._oResultData);
	    YAHOO.log("Item selected: " + YAHOO.lang.dump(elListItem._oResultData), "info", this.toString());
	    // turning off the automatic toggle, so that we can do it in the select event.
	    //this._toggleContainer(false);
	};

    // Throttle requests sent
    oAC.queryDelay = .03;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 500;
    oAC.forceSelection = true;

    // Do _something_ to the tabs for IE to prevent 
    // the over
    //oAC.containerExpandEvent.subscribe(function(){if (/MSIE (\d+.\d+);/.test(navigator.userAgent)) {YAHOO.util.Dom.setStyle('ageStage', 'z-index', '0');}}); 
    //oAC.containerCollapseEvent.subscribe(function(){if (/MSIE (\d+.\d+);/.test(navigator.userAgent)) {YAHOO.util.Dom.setStyle('ageStage', 'z-index', '1000');}}); 

    //oAC.alwaysShowContainer=true;
    //oAC.delimChar = ";";
    
    //go back to autocomplete after warning is closed
    var refocusAC = function(e) {
    	var inputBox = YAHOO.util.Dom.get("vocabTerm");
    	inputBox.focus();
    	oAC.sendQuery(inputBox.value);
    };
    
	YAHOO.gxd.container.panelAlert = new YAHOO.widget.Panel("vocabWarning", 
			{ visible:false, 
			context:["vocabTermAutoComplete","tl","bl", ["beforeShow"]],
			width:"400px", draggable:false, constraintoviewport:true } ); 
	YAHOO.gxd.container.panelAlert.render(); 
    
    // try to set the input field after itemSelect event
    oAC.suppressInputUpdate = true;
    var selectionHandler = function(sType, aArgs) { 
	    var myAC = aArgs[0]; // reference back to the AC instance 
	    var elLI = aArgs[1]; // reference to the selected LI element 
	    var oData = aArgs[2]; // object literal of selected item's result data 
  
	    //populate input box with another value (the base structure name)
	    var markerCount = oData[0];
	    var termId = oData[1];
	    var formattedTerm = oData[2];
	    var inputTerm = oData[3];
	    var hasExpression = oData[4];
	   
	    var inputBox = YAHOO.util.Dom.get("vocabTerm");
	    var idBox = YAHOO.util.Dom.get("annotationId");
	    idBox.value = "";
	    

	    
	    // does the term have expression data associated?
	    if (hasExpression)
	    {
	    	vocabACState = inputTerm;
	    	inputBox.value = inputTerm;
	    	idBox.value = termId;
	    	oAC._toggleContainer(false);
	    }
	    else
	    {
	    	// check the count of associated markers
	    	if(markerCount > 0)
	    	{
	    		oAC._toggleContainer(false);
		    	var warningBox = YAHOO.util.Dom.get("vocabWarningText");
		    	warningBox.innerHTML = "";
		    	var alertText = "The genes annotated with the vocabulary term you selected (<b>"+inputTerm+"</b>) do not have any gene expression data associated with them.";
		    	warningBox.innerHTML = alertText;

		    	YAHOO.gxd.container.panelAlert.show();
	    		var nodes = YAHOO.util.Selector.query("#vocabWarning .container-close");
	    		YAHOO.util.Event.on(nodes, 'click', refocusAC);
	    	}
	    	else
	    	{
	    		oAC._toggleContainer(true);
	    		//oAC.sendQuery ( inputBox.value );
	    	}
	    }
	    
	}; 
    oAC.itemSelectEvent.subscribe(selectionHandler); 
    oAC.selectionEnforceEvent.subscribe(runValidation);
    
    oAC.formatResult = function(oData, sQuery, sResultMatch) {
	    //var markerCount = oData[0];
	    //var termKey = oData[1];
	    var formattedTerm = oData[2];
	   // var inputTerm = oData[3];
	    //var hasExpression = oData[4];
    	 return formattedTerm;
    }; 

    return {
        oDS: oDS,
        oAC: oAC
    };
})();

/*
 * Anatomical Dictionary Auto Complete Section
 */
(function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource(fewiurl + "autocomplete/structure");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList: "resultObjects", fields:["structure", "synonym","isStrictSynonym"]};
    //oDS.maxCacheEntries = 10;
    oDS.connXhrMode = "cancelStaleRequests";

    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("structure", "structureContainer", oDS);

    // Throttle requests sent
    oAC.queryDelay = .03;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 500;
    oAC.forceSelection = true;
    //oAC.delimChar = ";";
    
    // try to set the input field after itemSelect event
    oAC.suppressInputUpdate = true;
    var selectionHandler = function(sType, aArgs) { 
	    var myAC = aArgs[0]; // reference back to the AC instance 
	    var elLI = aArgs[1]; // reference to the selected LI element 
	    var oData = aArgs[2]; // object literal of selected item's result data 
	    //populate input box with another value (the base structure name)
	    var structure = oData[1]; // 0 = term, 1 = ACtext
	    var inputBox = YAHOO.util.Dom.get("structure");
 	    inputBox.value = structure;
    }; 
    oAC.itemSelectEvent.subscribe(selectionHandler); 
    
    oAC.formatResult = function(oResultData, sQuery, sResultMatch) {
    	 
    	   // some other piece of data defined by schema
    	   var synonym = oResultData[1];
    	   var isStrictSynonym = oResultData[2];
    	  var value = synonym;
    	  if(isStrictSynonym) value += " <span style=\"color:#222; font-size:0.8em; font-style:normal;\">[synonym]</span>";
    	  return (value);
    	}; 
    	
    var toggleVis = function(){
        if (YAHOO.util.Dom.getStyle('structureHelp', 'display') == 'none'){
            YAHOO.util.Dom.setStyle('structureHelp', 'display', 'block');
        }
    };
    
    oAC.itemSelectEvent.subscribe(toggleVis); 

    return {
        oDS: oDS,
        oAC: oAC
    };
})();

//
// Wire up the functionality to reset the query form
//
var resetQF = function (e) {
	if (e) YAHOO.util.Event.preventDefault(e); 
	var form = YAHOO.util.Dom.get("gxdQueryForm");
	form.nomenclature.value = "";
	form.vocabTerm.value = "";
	form.annotationId.value = "";
	form.structure.value = "";
	form.theilerStage.selectedIndex = 0;
	form.age.selectedIndex = 0;
	selectTheilerStage();
	allAssayTypesBox.checked = true;
	for(var key in assayTypesBoxes)
	{
		assayTypesBoxes[key].checked = true;
	}
	form.detected3.checked=true;
	form.allSpecimen.checked=true;
	form.mutatedIn.value = "";
	// clear the validation errors
	runValidation();
};

YAHOO.util.Event.addListener("gxdQueryForm", "reset", resetQF);


//
//Parse the URL parameters into key value pairs and return the 
//array
//
function parseRequest(request){
	var reply = {};
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		if(!reply[kv[0]]){
			reply[kv[0]] = [];
		}
		reply[kv[0]].push(kv[1]);
	}
	return reply;
};


//
// Return the passed in form argument values in key/value URL format
//
var getQueryString = function(form) {
	var _qs = [];
	for(var i=0; i<form.elements.length; i++)
	{
		var element = form.elements[i];
		if(element.name != "" && element.name !="_theilerStage" && element.name !="_age") {
			if(element.tagName=="INPUT")
			{
				if(element.type=="checkbox" || element.type=="radio")
				{
					// don't add any assay type params if the all box is checked
					// also ignore their "all/either" options
					if( (element.name=="assayType" && allAssayTypesBox.checked)
							|| element.id == "assayType-ALL" || element.id == "detected3") continue;
					else if(element.name=="geneticBackground")
					{
						if(element.id=="isWildType")
						{
							if(element.checked)
							{
								_qs.push(element.id + "=true");
							}
						}
					}
					else if(element.checked)
					{
						_qs.push(element.name + "="  +element.value);
					}
				}
				else
				{
					// ignore mutatedIn field if the corresponding radio button is not checked
					if(element.name=="mutatedIn" && (element.value=="" || !YAHOO.util.Dom.get("mutatedSpecimen").checked))
						continue;
					
					_qs.push(element.name + "="  +element.value);
				}
				
			}
			else if (element.tagName=="SELECT")
			{
				if (element.parentNode.className != "yui-hidden") {
					for(var key in element.children)
					{
						if(element[key].selected)
						{
							_qs.push(element.name+"="+element[key].value);
						}
					}					
				}
			}
		}
	}
	return _qs.join("&");
};


// parses request parameters and resets and values found with their matching form input element
// returns false if no parameters were found
// responsible for repopulating the form during history manager changes
function reverseEngineerFormInput(request)
{
	var params = parseRequest(request);
	for(var key in params)
	{
		if(key == "detected")
		{
			// HACK for the radio buttons
			params["detected1"] = params[key];
			params["detected2"] = params[key];
		}
	}
	var foundParams = false;
	resetQF();
	for(var key in params)
	{
		if(key!=undefined && key!="" && params[key].length>0)
		{
			var input = YAHOO.util.Dom.get(key);
			if(input!=undefined && input!=null)
			{
				if(input.tagName=="INPUT")
				{
					foundParams = true;
					// do radio boxes
					if(input.type == "radio")
					{
						if(key=="isWildType")
						{
							YAHOO.util.Dom.get("isWildType").checked = true;
						}
						else if(input.value == params[key])
						{
							input.checked=true;
						}
					}
					// do check boxes
					else if(input.type=="checkbox")
					{
						var options = [];
						for(var i=0;i<params[key].length;i++)
						{
							options.push(decodeURIComponent(params[key][i]));
						}
						// The YUI.get() only returns one checkbox, but we want the whole set. 
						// The class should also be set to the same name.
						var boxes = YAHOO.util.Selector.query("."+key);
						for(var i=0;i<boxes.length;i++)
						{
							var box = boxes[i];
							var checked = false;
							for(var j=0;j<options.length;j++)
							{
								if(options[j] == box.value)
								{
									checked = true;
									box.checked = true;
									break;
								}
							}
							if(!checked)
							{
								box.checked = false;
							}
						}
					}
					else
					{
						if (key == "mutatedIn")
						{
							YAHOO.util.Dom.get("mutatedSpecimen").checked = true;
						}
						input.value = decodeURIComponent(params[key][0]);
					}
				}
				else if(input.tagName=="SELECT")
				{
					if (input.name == "age") {
						// open the age tab
						selectAge();
					}
					foundParams = true;
					var options = [];
					// decode all the options first
					for(var i=0;i<params[key].length;i++)
					{
						options.push(decodeURIComponent(params[key][i]));
					}
					// find which options need to be selected, and select them.
					for(var key in input.children)
					{
						var selected = false;
						for(var j=0;j<options.length;j++)
						{
							if(options[j] == input[key].value)
							{
								selected = true;
								input[key].selected = true;
								break;
							}
						}
						if(!selected)
						{
							input[key].selected = false;
						}
					}
				}
			}
		}
	}
	assayTypesCheck();
	return foundParams;
}

// add the check box listeners for assay types
var allAssayTypesBox = YAHOO.util.Dom.get("assayType-ALL");
var assayTypesBoxes = YAHOO.util.Selector.query(".assayType");
var allAssayTypesLabel = YAHOO.util.Dom.get("allAssayTypeLabel");
var assayTypesLabels = YAHOO.util.Selector.query(".assayTypeLabel");
var allAssayTypesCheck = function(e)
{
	// set everything to the same checked value as the all box
	var allChecked = allAssayTypesBox.checked;
	for(var key in assayTypesBoxes)
	{
		assayTypesBoxes[key].checked = allChecked;
	}
};
YAHOO.util.Event.addListener(allAssayTypesLabel, "click", allAssayTypesCheck); 
var assayTypesCheck = function(e)
{
	// check the current value of all check boxes to see if all needs to be checked/unchecked
	var allChecked = true;
	for(key in assayTypesBoxes)
	{
		if(assayTypesBoxes[key].checked==false)
		{
			allChecked = false;
		}
	}
	allAssayTypesBox.checked = allChecked;
};
for(var key in assayTypesLabels)
{
	YAHOO.util.Event.addListener(assayTypesLabels[key], "click", assayTypesCheck); 
}

// Add the listener for mutatedIn onFocus
var mutatedInOnFocus = function(e)
{
	YAHOO.util.Dom.get("mutatedSpecimen").checked = true;
};
YAHOO.util.Event.addFocusListener(YAHOO.util.Dom.get("mutatedIn"),mutatedInOnFocus);

