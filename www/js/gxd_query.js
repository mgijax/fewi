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
YAHOO.gxd.container.panelDifStruct1 = new YAHOO.widget.Panel("gxdDifStruct1Help", { width:"320px", draggable:false, visible:false, constraintoviewport:true,close:false } ); 
YAHOO.gxd.container.panelDifStruct1.render(); 
YAHOO.util.Event.addListener("gxdDifStruct1HelpImage", "mouseover", YAHOO.gxd.container.panelDifStruct1.show, YAHOO.gxd.container.panelDifStruct1, true);
YAHOO.util.Event.addListener("gxdDifStruct1HelpImage", "mouseout", YAHOO.gxd.container.panelDifStruct1.hide, YAHOO.gxd.container.panelDifStruct1, true);
YAHOO.gxd.container.panelDifStage = new YAHOO.widget.Panel("gxdDifStageHelp", { width:"320px", draggable:false, visible:false, constraintoviewport:true,close:false } ); 
YAHOO.gxd.container.panelDifStage.render(); 
YAHOO.util.Event.addListener("gxdDifStageHelpImage", "mouseover", YAHOO.gxd.container.panelDifStage.show, YAHOO.gxd.container.panelDifStage, true); 
YAHOO.util.Event.addListener("gxdDifStageHelpImage", "mouseout", YAHOO.gxd.container.panelDifStage.hide, YAHOO.gxd.container.panelDifStage, true); 

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
var tsBoxIDs = ["theilerStage","difTheilerStage1","difTheilerStage2"];
for(var j=0;j<tsBoxIDs.length;j++)
{
	var tsBox = YAHOO.util.Dom.get(tsBoxIDs[j]);
	if(tsBox!=null)
	{
		for(var i=0; i< tsBox.children.length; i++)
		{
			var option = tsBox.children[i];
			// check if we've defined the tooltip for this option
			if(tsTooltips[option.value])
			{
				var ttText = "<b>"+option.text+"</b>"+
					"<br/>"+tsTooltips[option.value];
				var tt = new YAHOO.widget.Tooltip("tsTT_"+j+"_"+i,{context:option, text:ttText,showdelay:1000});
			}
		}
	}
}

var QFHeight = 704;
var DifQFHeight = 150;
var currentQF = "standard";
var currentDifQF = "structure";
// GXD form tab control
YAHOO.widget.Tab.prototype.ACTIVE_TITLE = '';
var formTabs = new YAHOO.widget.TabView('expressionSearch');

formTabs.addListener("activeTabChange", function(e){
	if(formTabs.get('activeIndex')==0) currentQF = "standard";
	else currentQF = "differential";
});
//basic functions to manage the form tabs
var showStandardForm = function()
{ 
	currentQF = "standard";
	formTabs.selectTab(0); 
};
var showDifferentialForm = function()
{ 
	currentQF = "differential";
	formTabs.selectTab(1); 
};

//set up toggle for differential ribbons
function showDifStructuresQF()
{
	currentDifQF = "structure";
	showDifferentialForm();
	$("#difStructClosed").hide();
	$("#difStructOpen").show();
	$("#difStageClosed").show();
	$("#difStageOpen").hide();
}
function showDifStagesQF()
{
	currentDifQF = "stage";
	showDifferentialForm();
	$("#difStructClosed").show();
	$("#difStructOpen").hide();
	$("#difStageClosed").hide();
	$("#difStageOpen").show();
}
// attach the click handlers for ribbon toggle
$("#difStructClosed").click(showDifStructuresQF);
$("#difStageClosed").click(showDifStagesQF);

function getCurrentQF()
{
	if(currentQF=="differential")
	{
		if(currentDifQF=="stage") return YAHOO.util.Dom.get("gxdDifferentialQueryForm2");
		return YAHOO.util.Dom.get("gxdDifferentialQueryForm1");
	}
		
	return YAHOO.util.Dom.get("gxdQueryForm");
}

//GXD age/stage tab control
// COMMENTING OUT THE YUI2 way of doing this, because it breaks in IE on windows 7
//YAHOO.widget.Tab.prototype.ACTIVE_TITLE = '';
//var Tabs = new YAHOO.widget.TabView('ageStage');

// general purpose function for changing tabs
function changeTab(tabElement,parentId)
{
    var eSelector = '#'+parentId;
     // remove the active-tab and place it on current object;
    $(eSelector+' .active-tab').removeClass("active-tab").
		addClass("inactive-tab");
    $(tabElement).removeClass("inactive-tab")
		.addClass("active-tab");
    
    // remove active content
    $(eSelector+' .active-content').removeClass("active-content")
        .addClass("inactive-content");
    
    // use tab index to find matching content and set it to active
    var tab_index = $(tabElement).index();
    $(eSelector+' .inactive-content').eq(tab_index).removeClass("inactive-content")
        .addClass("active-content");
}
//Script to set up and control the ageStage tab widget (using jquery)
var ageStageID = "ageStage";
function selectTheilerStage()
{ changeTab($('#'+ageStageID+' .tab-nav')[0],ageStageID); }
function selectAge()
{ changeTab($('#'+ageStageID+' .tab-nav')[1],ageStageID); }
function ageStageChange(e)
{ if(!$(this).hasClass("active-tab")) changeTab(this,ageStageID); }
// Init the event listener for clicking tabs
$('#'+ageStageID+' .tab-nav').click(ageStageChange);

// init the age/stage widgets for diff query form (tie the two together)
//var difAgeStage1ID = "ageStage2";
//var difAgeStage2ID = "ageStage3";
//function selectDifTheilerStage()
//{ 
//	changeTab($('#'+difAgeStage1ID+' .tab-nav')[0],difAgeStage1ID); 
//	changeTab($('#'+difAgeStage2ID+' .tab-nav')[0],difAgeStage2ID); 
//}
//function selectDifAge()
//{ 
//	changeTab($('#'+difAgeStage1ID+' .tab-nav')[1],difAgeStage1ID); 
//	changeTab($('#'+difAgeStage2ID+' .tab-nav')[1],difAgeStage2ID); 
//}
//function difAgeStageChange1(e)
//{ 
//	if(!$(this).hasClass("active-tab")) 
//	{
//		if(this.id=="stagesTab2") selectDifTheilerStage();
//		else if(this.id=="agesTab2") selectDifAge();
//	}
//}
//// Init the event listener for clicking tabs
//$('#'+difAgeStage1ID+' .tab-nav').click(difAgeStageChange1);
//
//function difAgeStageChange2(e)
//{ 
//	if(!$(this).hasClass("active-tab")) 
//	{
//		if(this.id=="stagesTab3") selectDifTheilerStage();
//		else if(this.id=="agesTab3") selectDifAge();
//	}
//}
//// Init the event listener for clicking tabs
//$('#'+difAgeStage2ID+' .tab-nav').click(difAgeStageChange2);

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
	
	// handle the differential stuff first
	var isDifStructure = YAHOO.util.Dom.get('difStructure1').value!="" &&
		YAHOO.util.Dom.get('difStructure2').value != "";
	var difStage1 = YAHOO.util.Dom.get("difTheilerStage1");
	var hasAnyStage=false;
	var difStages1 =[] 
	for(var key in difStage1.children)
	{
		if(difStage1[key]!=undefined && difStage1[key].selected)
		{
			// set to "Any" if stage "0" appears anywhere in the list
			if(difStage1[key].value=="0")
			{
				hasAnyStage=true;
				break;
			}
			difStages1.push(difStage1[key].value);
		}
	}
	
	var hasAnyStageAbove=false;
	var difStage2 = YAHOO.util.Dom.get("difTheilerStage2");
	var difStages2 = []
	for(var key in difStage2.children)
	{
		if(difStage2[key]!=undefined && difStage2[key].selected)
		{
			// set to "Any" if stage "-1" appears anywhere in the list
			if(difStage2[key].value=="-1")
			{
				hasAnyStageAbove=true;
				break;
			}
			difStages2.push(difStage2[key].value);
		}
	}
	var isDifStage = currentQF=="differential" && currentDifQF=="stage";
	
	if(isDifStructure)
	{
		// Differential Structures Section
		var el = new YAHOO.util.Element(document.createElement('span'));
		el.set('innerHTML',"Detected in <b>"+YAHOO.util.Dom.get('difStructure1').value+"</b>" +
				"<span class=\"smallGrey\"> includes synonyms & substructures</span>"+
				"<br/>but not detected or assayed in <b>"+
					YAHOO.util.Dom.get('difStructure2').value+"</b>"+
				"<span class=\"smallGrey\"> includes synonyms & substructures</span>");
		el.appendTo(searchParams);
	}
	else if(isDifStage)
	{
		// Differential Stages Section
		var el = new YAHOO.util.Element(document.createElement('span'));
		var detectedStages = [];
		var detectedStagesText = "Developmental stage(s):";
		if(hasAnyStage) detectedStagesText = "<b>Any</b> Developmental stage";
		else
		{
			for(var i=0;i<difStages1.length;i++)
			{
				detectedStages.push("<b>TS:"+difStages1[i]+"</b>");
			}
			detectedStagesText += " ("+detectedStages.join(" or ")+")";
		}
		var notDetectedStages = [];
		var notDetectedStagesText = "Developmental stage(s):";
		if(hasAnyStageAbove) notDetectedStagesText = "<b>Any Developmental stage not selected above</b>";
		else
		{
			for(var i=0;i<difStages2.length;i++)
			{
				notDetectedStages.push("<b>TS:"+difStages2[i]+"</b>");
			}
			notDetectedStagesText += " ("+notDetectedStages.join(" or ")+")";
		}
		
		var htmlText = "Detected at " +detectedStagesText+
				"<br/>but not detected or assayed at "+notDetectedStagesText;
		el.set('innerHTML',htmlText);
		el.appendTo(searchParams);
	}
	else
	{
		// Standard QF Section
		
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
	
		if (age.parentNode.className != "inactive-content") {
	
			// do age
			var ages="";
			var _ages = [];
			for(var key in age.children)
			{
				if(age[key]!=undefined && age[key].selected)
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
				if(ts[key]!=undefined && ts[key].selected)
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
	}
};


//
// Handle the animation for the queryform
//
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
	    attributes = { height: { to: currentQF=="differential" ? DifQFHeight : QFHeight }};
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
    		$("#qwrap").css("height","auto");
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
    		$("#qwrap").css("height","auto");
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
		window.querystring = getQueryString(this);
		
		newQueryState = true;
		if(typeof resultsTabs != 'undefined')
		{
			// go to genes tab for differential, and results tab for anything else
			if(currentQF=="differential") resultsTabs.selectTab(0);
			else resultsTabs.selectTab(2);
		}
		if(typeof gxdDataTable != 'undefined')
			gxdDataTable.setAttributes({ width: "100%" }, true);
	    
		toggleQF(openSummaryControl);
	}
};

YAHOO.util.Event.addListener("gxdQueryForm", "submit", interceptSubmit);
YAHOO.util.Event.addListener("gxdDifferentialQueryForm1","submit",interceptSubmit);
YAHOO.util.Event.addListener("gxdDifferentialQueryForm2","submit",interceptSubmit);

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
	return selectVisible || geneVisible;
};

var difStructureRestriction  = function() 
{
	var form = YAHOO.util.Dom.get("gxdDifferentialQueryForm1");
	var structure = form.structure.value;
	var difStructure = form.difStructure.value;
	
	var setVisible = structure == '' || difStructure == '';
	
	setVisibility('difStructureError', setVisible);
	return setVisible;
};

var difStageRestriction  = function() 
{
	var form = YAHOO.util.Dom.get("gxdDifferentialQueryForm2");
	var stage = form.theilerStage;
	var difStage = form.difTheilerStage;
	
	var hasAnyStage=false;
	for(var key in stage.children)
	{
		if(stage[key]!=undefined && stage[key].selected)
		{
			// set to "Any" if stage "-1" appears anywhere in the list
			if(stage[key].value=="0")
			{
				hasAnyStage=true;
				break;
			}
		}
	}

	var hasAnyStageAbove=false;
	for(var key in difStage.children)
	{
		if(difStage[key]!=undefined && difStage[key].selected)
		{
			// set to "Any" if stage "-1" appears anywhere in the list
			if(difStage[key].value=="-1")
			{
				hasAnyStageAbove=true;
				break;
			}
		}
	}
	var setVisible = hasAnyStage && hasAnyStageAbove;
	
	setVisibility('difStageError', setVisible);
	return setVisible;
};

var runValidation  = function(){
	var result=false;
	if(currentQF == "standard")
	{
		result = geneRestriction() || mutationRestriction();  
		setSubmitDisabled(result);
	}
	else if(currentDifQF=="structure")
	{
		result = difStructureRestriction();
		//YAHOO.util.Dom.get("submit3").disabled=result;
	}
	else if(currentDifQF=="stage")
	{
		result = difStageRestriction();
	}
	return result;
};
var clearValidation = function()
{
	if(currentQF == "standard") runValidation();
	else
	{
		setVisibility('difStructureError', false);
		setVisibility('difStageError',false);
	}
}

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
function makeStructureAC(inputID,containerID){
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource(fewiurl + "autocomplete/structure");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList: "resultObjects", fields:["structure", "synonym","isStrictSynonym"]};
    //oDS.maxCacheEntries = 10;
    oDS.connXhrMode = "cancelStaleRequests";

    // Instantiate the AutoCompletes
    var oAC = new YAHOO.widget.AutoComplete(inputID, containerID, oDS);

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
	    var inputBox = YAHOO.util.Dom.get(inputID);
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
};
makeStructureAC("structure","structureContainer");
makeStructureAC("difStructure1","difStructureContainer1");
makeStructureAC("difStructure2","difStructureContainer2");

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
	
	var difForm1 = YAHOO.util.Dom.get("gxdDifferentialQueryForm1");
	if(difForm1)
	{
		difForm1.structure.value="";
		difForm1.difStructure.value="";
	}
	var difForm2 = YAHOO.util.Dom.get("gxdDifferentialQueryForm2");
	if(difForm2)
	{
		difForm2.theilerStage.selectedIndex=0;
		difForm2.difTheilerStage.selectedIndex=0;	
		//difForm2.age.selectedIndex=0;
		//difForm2.difAge.selectedIndex=0;	
		//selectDifTheilerStage();
	}
	// clear the validation errors
	clearValidation();
};

YAHOO.util.Event.addListener("gxdQueryForm", "reset", resetQF);
YAHOO.util.Event.addListener("gxdDifferentialQueryForm1", "reset", resetQF);
YAHOO.util.Event.addListener("gxdDifferentialQueryForm2", "reset", resetQF);

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
	if(form==undefined) form = getCurrentQF();
	var _qs = [];
	for(var i=0; i<form.elements.length; i++)
	{
		var element = form.elements[i];
		if(element.name != "" 
			&& element.name !="_theilerStage" && element.name !="_age"
			&& element.name !="_difTheilerStage" && element.name !="_difAge") {
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
				if (element.parentNode.className != "inactive-content") {
					for(var key in element.children)
					{
						if(element[key]!=undefined && element[key].selected)
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
	var formID = "#gxdQueryForm";
	var foundDifStruct=false;
	var foundDifStage=false;
	for(var key in params)
	{
		if(key == "detected")
		{
			// HACK for the radio buttons
			params["detected1"] = params[key];
			params["detected2"] = params[key];
		}
		else if(key == "difStructure") foundDifStruct=true;
		else if(key == "difTheilerStage" || key=="difAge") foundDifStage=true;
	}
	// make sure correct form is visible
	// this code allows for flexibility to add third ribbon
	if(foundDifStruct && foundDifStage) { } // add third ribbon here
	else if (foundDifStruct) 	
	{
		formID = "#gxdDifferentialQueryForm1";
		showDifStructuresQF();
	}
	else if (foundDifStage)
	{
		formID = "#gxdDifferentialQueryForm2";
		showDifStagesQF();
	}
	
	var foundParams = false;
	resetQF();
	for(var key in params)
	{
		if(key!=undefined && key!="" && key!="detected" && params[key].length>0)
		{
			//var input = YAHOO.util.Dom.get(key);
			// jQuery is better suited to resolving form name parameters
			var input = $(formID+" [name='"+key+"']");
			if(input.length < 1) input = $(formID+" #"+key);
			if(input!=undefined && input!=null && input.length > 0)
			{
				input = input[0];
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
						//if(foundDifStage) selectDifAge();
						//else selectAge();
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
						if(input[key]!=undefined)
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


