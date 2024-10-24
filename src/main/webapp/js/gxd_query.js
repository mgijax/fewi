var logging = true;	// is loggging to the console enabled? (true/false)

function log(msg) {
    // log a message to the browser console, if logging is enabled

    if (!logging) { return; }
    try {
	console.log(msg);
    } catch (c) {
        setTimeout(function() { throw new Error(msg); }, 0);
    }
}

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

YAHOO.gxd.container.panelStructure = new YAHOO.widget.Panel("gxdStructureHelp", { width:"320px", draggable:false, visible:false, constraintoviewport:true } );
YAHOO.gxd.container.panelStructure.render();
YAHOO.util.Event.addListener("gxdStructureHelpImage", "mouseover", YAHOO.gxd.container.panelStructure.show, YAHOO.gxd.container.panelStructure, true);


YAHOO.gxd.container.panelProfile = new YAHOO.widget.Panel("gxdProfileHelp", { width:"320px", draggable:false, visible:false, constraintoviewport:true,close:false } );
YAHOO.gxd.container.panelProfile.render();
YAHOO.util.Event.addListener("gxdProfileHelpImage", "mouseover", YAHOO.gxd.container.panelProfile.show, YAHOO.gxd.container.panelProfile, true);
YAHOO.util.Event.addListener("gxdProfileHelpImage", "mouseout", YAHOO.gxd.container.panelProfile.hide, YAHOO.gxd.container.panelProfile, true);

YAHOO.gxd.container.panelClassical = new YAHOO.widget.Panel("gxdProfileClassicalHelp", { width:"280px", draggable:false, visible:false, constraintoviewport:true,close:false } );
YAHOO.gxd.container.panelClassical.render();
YAHOO.util.Event.addListener("gxdProfileClassicalHelpImage", "mouseover", YAHOO.gxd.container.panelClassical.show, YAHOO.gxd.container.panelClassical, true);
YAHOO.util.Event.addListener("gxdProfileClassicalHelpImage", "mouseout", YAHOO.gxd.container.panelClassical.hide, YAHOO.gxd.container.panelClassical, true);

YAHOO.gxd.container.panelClassical = new YAHOO.widget.Panel("gxdProfileStageHelp", { width:"280px", draggable:false, visible:false, constraintoviewport:true,close:false } );
YAHOO.gxd.container.panelClassical.render();
YAHOO.util.Event.addListener("gxdProfileStageHelpImage", "mouseover", YAHOO.gxd.container.panelClassical.show, YAHOO.gxd.container.panelClassical, true);
YAHOO.util.Event.addListener("gxdProfileStageHelpImage", "mouseout", YAHOO.gxd.container.panelClassical.hide, YAHOO.gxd.container.panelClassical, true);


YAHOO.gxd.container.panelDifStruct1 = new YAHOO.widget.Panel("gxdDifStruct1Help", { width:"320px", draggable:false, visible:false, constraintoviewport:true,close:false } );
YAHOO.gxd.container.panelDifStruct1.render();
YAHOO.util.Event.addListener("gxdDifStruct1HelpImage", "mouseover", YAHOO.gxd.container.panelDifStruct1.show, YAHOO.gxd.container.panelDifStruct1, true);
YAHOO.util.Event.addListener("gxdDifStruct1HelpImage", "mouseout", YAHOO.gxd.container.panelDifStruct1.hide, YAHOO.gxd.container.panelDifStruct1, true);
YAHOO.gxd.container.panelDifStage = new YAHOO.widget.Panel("gxdDifStageHelp", { width:"320px", draggable:false, visible:false, constraintoviewport:true,close:false } );
YAHOO.gxd.container.panelDifStage.render();
YAHOO.util.Event.addListener("gxdDifStageHelpImage", "mouseover", YAHOO.gxd.container.panelDifStage.show, YAHOO.gxd.container.panelDifStage, true);
YAHOO.util.Event.addListener("gxdDifStageHelpImage", "mouseout", YAHOO.gxd.container.panelDifStage.hide, YAHOO.gxd.container.panelDifStage, true);
YAHOO.gxd.container.panelDifBoth = new YAHOO.widget.Panel("gxdDifStructStageHelp", { width:"320px", draggable:false, visible:false, constraintoviewport:true,close:false } );
YAHOO.gxd.container.panelDifBoth.render();
YAHOO.util.Event.addListener("gxdDifStructStageHelpImage", "mouseover", YAHOO.gxd.container.panelDifBoth.show, YAHOO.gxd.container.panelDifBoth, true);
YAHOO.util.Event.addListener("gxdDifStructStageHelpImage", "mouseout", YAHOO.gxd.container.panelDifBoth.hide, YAHOO.gxd.container.panelDifBoth, true);

//GXD tooltips
var tsTooltips = {
		1:"One cell stage",
		2:"Beginning of cell division; 2-4 cells",
		3:"Morula; 4-16 cells",
		4:"Blastocyst (inner cell mass apparent); 16-40 cells",
		5:"Blastocyst (zona free)",
		6:"Implantation",
		7:"Formation of egg cylinder",
		8:"Differentiation of egg cylinder",
		9:"Prestreak; early streak",
		10:"Midstreak; late streak; allantoic bud first appears; amnion forms",
		11:"Neural plate stage; elongated allantoic bud; early headfold; late headfold",
		12:"1-7 somites",
		13:"8-12 somites; turning of embryo",
		14:"13-20 somites; formation and closure of anterior neuropore",
		15:"21-29 somites; formation of posterior neuropore and forelimb bud",
		16:"30-34 somites; closure of posterior neuropore; formation of hindlimb and tail bud",
		17:"35-39 somites; deep indentation of lens vesicle",
		18:"40-44 somites; closure of lens vesicle",
		19:"45-47 somites; complete separation of lens vesicle",
		20:"48-51 somites; earliest sign of handplate digits",
		21:"52-55 somites; indentation of handplate",
		22:"56-~60 somites; distal separation of handplate digits",
		23:"Separation of footplate digits",
		24:"Reposition of umbilical hernia",
		25:"Digits joined together; skin wrinkled",
		26:"Long whiskers",
		27:"Newborn mouse",
		28:"Postnatal development"
		};

// only include those with special handling (to be added before right paren)
var tsTooltipTitles = {
	27:"; P0-P3",
	28:"; P4-adult"
};

var tsBoxIDs = ["theilerStage","difTheilerStage1","difTheilerStage2",
    "difTheilerStage3","difTheilerStage4"];
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
				var ttTitle = option.text;
				if (tsTooltipTitles[option.value]) {
				    ttTitle = ttTitle.replace(")",
					tsTooltipTitles[option.value] + ")");
				}
				var ttText = "<b>" + ttTitle + "</b>"+
					"<br/>"+tsTooltips[option.value];
				var tt = new YAHOO.widget.Tooltip("tsTT_"+j+"_"+i,{context:option, text:ttText,showdelay:1000});
			}
		}
	}
}

var QFHeight = 704;
var DifQFHeight = 150;
var BatchQFHeight = 153;
var currentQF = "standard";
// GXD form tab control
YAHOO.widget.Tab.prototype.ACTIVE_TITLE = '';
var formTabs = new YAHOO.widget.TabView('expressionSearch');

formTabs.addListener("activeTabChange", function(e){
	if(formTabs.get('activeIndex')==0) currentQF = "standard";
	else if(formTabs.get('activeIndex')==3) currentQF = "batch";
	else if(formTabs.get('activeIndex')==2) currentQF = "profile";
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
var showProfileSearchForm = function()
{
	currentQF = "profile";
	formTabs.selectTab(2);
};
var showBatchSearchForm = function()
{
	currentQF = "batch";
	formTabs.selectTab(3);
};

function getCurrentQF()
{
	if(currentQF=="differential") {
		return YAHOO.util.Dom.get("gxdDifferentialQueryForm3");
	} else if (currentQF == 'batch') {
		return YAHOO.util.Dom.get("gxdBatchQueryForm1");
	} else if (currentQF == 'profile') {
		return YAHOO.util.Dom.get("gxdProfileQueryForm");
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

// returns either "Any" or a list of the selected options
function parseStageOptions(id,anyValue)
{
	if(anyValue==undefined) anyValue="0";
	var stage = YAHOO.util.Dom.get(id);
	var stages =[]
	for(var key in stage.children)
	{
		if(stage[key]!=undefined && stage[key].selected)
		{
			// set to "Any" if stage "0" appears anywhere in the list
			if(stage[key].value==anyValue)
			{
				return "Any";
			}
			stages.push(stage[key].value);
		}
	}
	return stages;
}

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

	if (currentQF == 'profile') {

		// collect list of detected structures and not detected structured
		var posStructures = [];
		var negStructures = [];

		model.profileSpec.forEach( (m,i) => {
		    var stageClause = ""
		    var structureClause = ""
		    if (m.stages.length > 0) {
		        stageClause = `stage${m.stages.length > 1 ? 's' : ''} ${m.stages.join(', ')}`
		    }
		    if (m.structure != "") {
			structureClause = `${m.structure}`
		    }

		    if (stageClause !== "" || structureClause !== "") {
		        var clause = `<b> ${structureClause} ${stageClause} </b>`
			if (m.detected) {
			    posStructures.push(clause);
			} else {
			    negStructures.push(clause);
			}
		    }

		})

		// create You Searched For... strings
		var newInnerHTML = '';		
		if (posStructures.length > 0) {
			newInnerHTML = "Detected in " + posStructures.join(", ");
			if (negStructures.length > 0) {
				newInnerHTML = newInnerHTML + " and not detected or assayed in " + negStructures.join(", ");
			}
			if (profileNowhereElseCheckbox.checked) {
				newInnerHTML = newInnerHTML + " and not detected anywhere else. ";
			}
		} else {
				newInnerHTML = "Not detected in " + negStructures.join(", ");
		}

		// create span element, and add our crafted display
		var el = new YAHOO.util.Element(document.createElement('span'));
		el.set('innerHTML',newInnerHTML);
		el.appendTo(searchParams);

	}
	else if (currentQF == 'batch') {
		// only two fields matter for batch searches:  the count of IDs
		// and the scope in which to search

		if (YAHOO.util.Dom.get('ids').value != '') {
			var count = YAHOO.util.Dom.get('ids').value.trim().replace(/[\n]+/g, '\n').split('\n').length;
			var scope = YAHOO.util.Dom.get("idType").options[YAHOO.util.Dom.get("idType").selectedIndex].innerHTML;

			// Create a span
			var el = new YAHOO.util.Element(document.createElement('span'));
			//add the text node to the newly created span
			el.appendChild(document.createTextNode("Number of IDs/Symbols entered: "));

			// Create a bold
			var b = new YAHOO.util.Element(document.createElement('b'));
			var newContent = document.createTextNode(count);
			b.appendChild(newContent);
			el.appendChild(b);

			el.appendChild(new YAHOO.util.Element(document.createElement('br')));
			el.appendTo(searchParams);

			// Create a span
			var el = new YAHOO.util.Element(document.createElement('span'));
			//add the text node to the newly created span
			el.appendChild(document.createTextNode("Input Type: "));

			// Create a bold
			var b = new YAHOO.util.Element(document.createElement('b'));
			var newContent = document.createTextNode('' + scope);
			b.appendChild(newContent);
			el.appendChild(b);

			el.appendChild(new YAHOO.util.Element(document.createElement('br')));
			el.appendTo(searchParams);

			// Create a span
			var el = new YAHOO.util.Element(document.createElement('span'));
			el.addClass('countHere');

			//add the text node to the newly created span
			el.appendChild(document.createTextNode(""));

			var b = new YAHOO.util.Element(document.createElement('span'));
			var newContent = document.createTextNode(getYsfGeneCount() + ' matching genes with expression data');
			b.appendChild(newContent);
			el.appendChild(b);

			el.appendChild(new YAHOO.util.Element(document.createElement('br')));
			el.appendTo(searchParams);
		}
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

		if (YAHOO.util.Dom.get('locations').value != "") {

			// Create a span
			var el = new YAHOO.util.Element(document.createElement('span'));
			//add the text node to the newly created span
			el.appendChild(document.createTextNode("Genome location(s): "));

			// Create a bold
			var b = new YAHOO.util.Element(document.createElement('b'));
			var newContent = document.createTextNode(YAHOO.util.Dom.get('locations').value);
			// Build and append the nomenclature query parameter section
			b.appendChild(newContent);
			if (YAHOO.util.Dom.get('locations').value.indexOf(':') >= 0) {
				var units = document.createTextNode(" " + YAHOO.util.Dom.get('locationUnit').value);
				b.appendChild(units);
			}
			el.appendChild(b);

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
			b.appendChild(document.createTextNode(noScript(noAlert(YAHOO.util.Dom.get('structure').value))));
			el.appendChild(b);

			var sp = new YAHOO.util.Element(document.createElement('span'));
			sp.addClass("smallGrey");
			sp.appendChild(document.createTextNode(" includes substructures"));
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
		} // end of structure handling



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
			var structureID = YAHOO.util.Dom.get('structureID');

			if(structureID.value == "" || (structureID.value != "" && ts.value != 0)) {

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
				el.appendChild(document.createTextNode("at developmental stage(s): "));

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
		if(assayTypes.length > 0)
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

	console.log("in toggleQF");

	if(noAnimate==undefined) noAnimate=false;

	// ensure popups are hidden
	stagePopupPanel.hide();
	genePopupPanel.hide();
	stagePopupPanel.hide();

    var outer = YAHOO.util.Dom.get('outer');
	YAHOO.util.Dom.setStyle(outer, 'overflow', 'hidden');
    var qf = YAHOO.util.Dom.get('qwrap');
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    attributes =  { height: { to: 0 }};

	console.log("in toggleQF - currentQF:" + currentQF);
    var toHeight = QFHeight;
    if (currentQF == "differential") { toHeight = DifQFHeight; }
    else if (currentQF == "batch") { toHeight = BatchQFHeight; }
    else if (currentQF == "profile") { toHeight = BatchQFHeight; }
	console.log("in toggleQF - toHeight:" + toHeight);

    if (!YAHOO.lang.isNull(toggleLink) && !YAHOO.lang.isNull(toggleImg)
    		) {
	    attributes = { height: { to: toHeight }};
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
	ga_logEvent("GXD Query Form Submission", currentQF);
	YAHOO.util.Event.preventDefault(e);

	if (!runValidation()){
		// Do not allow any content to overflow the outer
		// div when it is hiding
		var outer = YAHOO.util.Dom.get('outer');
		YAHOO.util.Dom.setStyle(outer, 'overflow', 'hidden');

		if(typeof clearAllFilters != 'undefined')
		{
			clearAllFilters();
			prepFilters();
		}

		// Set the global querystring to the form values
		window.querystring = getQueryString(this);

		newQueryState = true;
		if(typeof resultsTabs != 'undefined')
		{
			// go to tissue x gene matrix for differential, and results tab for anything else
			if(currentQF=="differential") resultsTabs.selectTab(5);
			else resultsTabs.selectTab(2);
		}
		if(gxdDataTable != undefined)
			gxdDataTable.setAttributes({ width: "100%" }, true);

		toggleQF(openSummaryControl);

		if (currentQF == 'batch') {
			// convert spaces to escaped version before submission
			YAHOO.util.Dom.get('ids').value = YAHOO.util.Dom.get('ids').value.trimRight();
			//YAHOO.util.Dom.get('ids').value = YAHOO.util.Dom.get('ids').value.replace(/ /g, '%20');
		} else if (currentQF == 'standard') {
			// If this is a search that includes RNA-Seq data, log it.
			if (window.querystring.indexOf('assayType=RNA-Seq') >= 0) {
				ga_logEvent('GXD Query Form: Parameters', 'Included RNA-Seq data');
			}
		}
		resetYSF();
	}
};

YAHOO.util.Event.addListener("gxdQueryForm", "submit", interceptSubmit);
YAHOO.util.Event.addListener("gxdBatchQueryForm1", "submit", interceptSubmit);
YAHOO.util.Event.addListener("gxdDifferentialQueryForm3","submit", interceptSubmit);
YAHOO.util.Event.addListener("gxdProfileQueryForm","submit", interceptSubmit);


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

var profileFormCheck  = function() {

	var hasErrors = false; // return value

	// gather parameters from form; add to {rowNum:value} dictionary for 
	// downstream checking
	var profileForm = YAHOO.util.Dom.get("gxdProfileQueryForm");
	var hasPosStructure = false;

	model.profileSpec.forEach( (m,i) => {
	    if (m.detected === true && (m.structure !== "" || m.stages.length > 0)) {
		hasPosStructure = true;
	    }
	})

	// check for empty submission
	if (!hasPosStructure) {
		alert("Please specify Detected expression for at least one anatomical structure or stage.");
		hasErrors=true;
	}

	return hasErrors;
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

var difTSClick = function() {
	// if we have a click in the differential Theiler stage box, we need to blank out the
	// "anywhere else" checkbox

	if ($('#anywhereElse').length > 0) {
		$('#anywhereElse')[0].checked = false;
	}
}

var differentialRestriction  = function()
{
	// For a valid search we need...
	// 1. either a structure or stage for 'detected in'
	// 2. either a structure, a stage, or not 'anywhere else' for 'not detected in'
	// 3. if choosing both structure & stage (top or bottom ribbon), then you can't
	//		select just one in the other ribbon
	
	var form = YAHOO.util.Dom.get("gxdDifferentialQueryForm3");

	var structure = form.structure.value;
	var difStructure = form.difStructure.value;
	var hasStructure = (structure != null) && (structure.trim().length > 0);
	var hasDifStructure = (difStructure != null) && (difStructure.trim().length > 0);

	var stage = form.theilerStage;
	var difStage = form.difTheilerStage;

	// For the 'detected in' developmental stage, we need a choice other than "any" for it to count.
	var hasTS = (stage.selectedOptions.length > 1);
	var hasAnyTS = (stage.selectedOptions.length > 1);		// includes Any as a valid choice
	if (!hasTS) {
		for (var i in stage.selectedOptions) {
			if (stage.selectedOptions[i].value != '0' && stage.selectedOptions[i].value != undefined) {
				hasTS = true;
				hasAnyTS = true;
				break;
			} else if (stage.selectedOptions[i].value == '0') {
				hasAnyTS = true;
			}
		}
	}

	var hasDifTS = (difStage.selectedOptions.length >= 1);
	var anywhereElseChecked = form.anywhereElse.checked;
	
	// If we have 'any' structure for the detected TS choice, then 'any other' doesn't make sense as a
	// NOT detected TS choice.
	if (!hasTS) {
		if ((difStage.selectedOptions.length == 1) && (
				(difStage.selectedOptions[0].value == '-1') || (difStage.selectedOptions[0].value == '0') )) {
			hasDifTS = false;
		}
	}

	var error = '';
	
	if (hasStructure && !hasDifStructure && !anywhereElseChecked) {
		error = 'If you specify a structure in the top ribbon, then you must either specify a structure in the bottom ribbon or check "anywhere else".';

	} else if (!hasStructure && hasDifStructure) {
		error = 'If you specify a structure in the bottom ribbon, then you must also specify a structure in the top ribbon.';

	} else if (hasTS && !hasDifTS && !anywhereElseChecked) {
		error = 'If you specify a stage in the top ribbon, then you must either specify a stage in the bottom ribbon or check "anywhere else".';

	} else if (!hasTS && hasDifTS && !(hasAnyTS && hasStructure && hasDifStructure)) {
		error = 'If you specify a stage in the bottom ribbon, then you must also specify a stage in the top ribbon.';

	} else if (!(hasStructure || hasTS)) {
		error = 'In the top ribbon, please specify an anatomical structure or developmental stage or both.';

	} else if (!(hasDifStructure || hasDifTS || anywhereElseChecked)) {
		error = 'In the bottom ribbon, please specify an anatomical structure or developmental stage or both or check "anywhere else".';
	}

	setVisibility('differentialError', error.length > 0);
	$('#differentialError').html(error);
	
	return error.length > 0;
};

/* returns false if there are NO validation errors, true if there are some
 */
var runValidation  = function(){
	var result=false;

	if(currentQF == "standard")
	{
		result = geneRestriction() || mutationRestriction();
		setSubmitDisabled(result);
	}
	else if (currentQF == 'profile') {
		result = profileFormCheck();
	}
	else if (currentQF == 'batch') {
		result = false;			// no current validations
	}
	else if(currentQF=="differential") {
		result = differentialRestriction();
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
		setVisibility('difStructStageError',false);
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
        var termIdInput = YAHOO.util.Dom.get("annotationId");
        var vocabInput = YAHOO.util.Dom.get("vocabTerm");
        // Sigh. I have no clue what the rest of this code is supposed to be doing, so I'm
        // just going insert my little special case here and hope for the best.
        if (vocabInput.value === "") {
            termIdInput.value = ""
            return
        }
	// ignore keycodes between 9 and 40 (ctrl, shift, enter, tab, arrows, etc)
	if (vocabACState != "" && (keyCode <9 || keyCode >40)){
		if (vocabInput.value!="" && vocabInput.value == window.vocabACState) {
			// clear the state, the visual input, and the hidden term field input
			window.vocabACState = "";
			vocabInput.value = "";
			termIdInput.value = "";
		}
	}
};

YAHOO.util.Event.addListener(YAHOO.util.Dom.get("vocabTerm"), "keydown", clearVocabTerm);


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

/* get a string for a Theiler Stage range
 */
function tsRange(startStage, endStage) {
    var ts = "TS" + startStage;
    if (startStage != endStage) {
	ts = ts + "-" + endStage;
    }
    return ts;
}

/*
 * Anatomical Dictionary Auto Complete Section
 */
function makeStructureAC(inputID,containerID){

	var hiddenID = inputID + "ID"
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource(fewiurl + "autocomplete/gxdEmapa");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    //oDS.responseSchema = {resultsList: "resultObjects", fields:["structure", "synonym","isStrictSynonym"]};
	oDS.responseSchema = {resultsList: "resultObjects", fields:["structure", "synonym","isStrictSynonym","accID","startStage","endStage"]};

    //oDS.maxCacheEntries = 10;
    oDS.connXhrMode = "cancelStaleRequests";

    // Instantiate the AutoCompletes
    var oAC = new YAHOO.widget.AutoComplete(inputID, containerID, oDS);

    // Throttle requests sent
    oAC.queryDelay = .03;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 500;
    oAC.forceSelection = true;
	//oAC.forceSelection = false;
    //oAC.delimChar = ";";

    var removeSelectedID = function(oSelf,yuiE) {
    	var ac = yuiE[0];
    	if(ac.getInputEl().value.trim() == "")
    	{
		    var idBox = YAHOO.util.Dom.get(hiddenID);
	 	    idBox.value = "";
	 	    if (inputID == 'difStructure4') {
	 	   		$('#inCheckbox')[0].checked = false;			// clearn the in-structure checkbox
	 	   	}
    	}
    };
    oAC.selectionEnforceEvent.subscribe(removeSelectedID);

    // try to set the input field after itemSelect event
    oAC.suppressInputUpdate = true;
    var selectionHandler = function(sType, aArgs) {
	    var myAC = aArgs[0]; // reference back to the AC instance
	    var elLI = aArgs[1]; // reference to the selected LI element
	    var oData = aArgs[2]; // object literal of selected item's result data

	    //populate input box
	    var newInputBoxValue = oData[0]; // start with the EMAPA term
		var synonym = oData[1];
		var isStrictSynonym = oData[2];
	    var accID = oData[3];
		var tsStart = oData[4];
		var tsStop  = oData[5];
		if(isStrictSynonym) {
			newInputBoxValue = newInputBoxValue + " (" + synonym + ")"
		}
		newInputBoxValue = newInputBoxValue.toLowerCase();
		newInputBoxValue = newInputBoxValue + " "
			+ tsRange(tsStart, tsStop);

	    var inputBox = YAHOO.util.Dom.get(inputID);
	    inputBox.value = newInputBoxValue;

	    //populate hidden ID for param
	    var accID = oData[3];
	    var idBox = YAHOO.util.Dom.get(hiddenID);
	    idBox.value = accID;
	    
    };
    oAC.itemSelectEvent.subscribe(selectionHandler);

    oAC.formatResult = function(oResultData, sQuery, sResultMatch) {

//	    var userInputField = YAHOO.util.Dom.get("structure");
	    var userInputField = YAHOO.util.Dom.get(inputID);
	    var userInput = userInputField.value.toLowerCase();

		// some other piece of data defined by schema
		var term = oResultData[0];
		var synonym = oResultData[1];
		var isStrictSynonym = oResultData[2];
		var tsStart = oResultData[4];
		var tsStop  = oResultData[5];
		var value = term;
		if(isStrictSynonym) {
			value = value + " (" + synonym + ") "
		}
		value = value.toLowerCase();
		var value = value.replace(userInputField.value, "<b>" + userInputField.value + "</b>");
		value = value + " <span style=\"font-size:0.8em; font-style:normal;\"> "
			+ tsRange(tsStart, tsStop) + "</span>";

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

//
// Wire up the functionality to reset the query form
//
var resetQF = function (e) {

	console.log("resetQF started")

	// prevent default reset action
	if (e) YAHOO.util.Event.preventDefault(e);

	// standard QF
	var form = YAHOO.util.Dom.get("gxdQueryForm");
	form.nomenclature.value = "";
	form.vocabTerm.value = "";
	form.annotationId.value = "";
	form.structure.value = "";
	form.structureID.value = "";
	form.theilerStage.selectedIndex = 0;
	form.age.selectedIndex = 0;
	form.locations.value = "";
	$('#locationUnit').val('bp');
	selectTheilerStage();
	
	setAll('.allInSitu', '.inSituAssayType', true);
	setAll('.allBlot', '.blotAssayType', true);
	setAll('.allWholeGenome', '.wholeGenomeAssayType', false);
	
	form.detected3.checked=true;
	form.allSpecimen.checked=true;
	form.mutatedIn.value = "";

	// differential
	var difForm3 = YAHOO.util.Dom.get("gxdDifferentialQueryForm3");
	if(difForm3)
	{
		difForm3.structure.value="";
		difForm3.structureID.value="";
		difForm3.difStructure.value="";
		difForm3.difStructureID.value="";
		difForm3.theilerStage.selectedIndex=0;
		difForm3.difTheilerStage.selectedIndex=0;
		difForm3.inCheckbox.checked = false;
		difForm3.anywhereElse.checked = false;
		setVisibility('differentialError', false);
	}

	// profile
	var profileForm = YAHOO.util.Dom.get("gxdProfileQueryForm");

	if(profileForm)
	{
		resetProfileForm();

		// reset the No Where Else... check box
		profileForm.profileNowhereElseCheckbox.checked = false;
	}

	// batch
	var batchForm = YAHOO.util.Dom.get("gxdBatchQueryForm1");
	if (batchForm) {
		batchForm.idType.selectedIndex=0;
		batchForm.ids.value="";
		batchForm.fileType.selectedIndex=0;
		batchForm.idColumn.value="1";
		$("input[name='idFile']")[0].value = null;
		$("input[name='fileType']")[0].click();

		var msg = document.getElementById('uploadMessage');
		msg.innerHTML = '';
		msg.style.display = 'none';
	}

	// uncheck the "Show Additional Sample Data" box, if it exists
	if ($('#showHide').length > 0) {
		$('#showHide')[0].checked = !hideOptionalColumns;
	}

	// clear the validation errors
	clearValidation();

	// clear facets
	resetFacets();
};

// not only reset the QF, but also uncheck the show/hide additional columns
// button
var fullResetQF = function(e) {
	if ($('#showHide').length > 0) {
		if ($('#showHide')[0].checked) {
			flipOptionalColumns(true);
		}
	}
	resetQF(e);
}

YAHOO.util.Event.addListener("gxdQueryForm", "reset", fullResetQF);
YAHOO.util.Event.addListener("gxdBatchQueryForm1", "reset", fullResetQF);
YAHOO.util.Event.addListener("gxdDifferentialQueryForm1", "reset", fullResetQF);
YAHOO.util.Event.addListener("gxdDifferentialQueryForm2", "reset", fullResetQF);
YAHOO.util.Event.addListener("gxdDifferentialQueryForm3", "reset", fullResetQF);
YAHOO.util.Event.addListener("gxdProfileQueryForm", "reset", fullResetQF);

//
// Return the passed in form argument values in key/value URL format
//
var getQueryString = function(form) {
	console.log("---into getQueryString");
	if(form==undefined) form = getCurrentQF();
	if (form.id === 'gxdProfileQueryForm') {
	    return profileSpec2QueryString();
	}
	var _qs = [];
	for(var i=0; i<form.elements.length; i++)
	{
		var element = form.elements[i];
		console.log("getQueryString - element:" + element.name);
		if(element.name != ""
			&& element.name !="_theilerStage" && element.name !="_age"
			&& element.name !="_difTheilerStage" && element.name !="_difAge")
		{
			if(element.tagName=="TEXTAREA")
			{
				_qs.push(element.name + "="  +element.value.replace(/\s/g,' '));
			}
			else if((element.tagName=="INPUT") && (element.name != 'idFile'))
			{
				if(element.type=="checkbox" || element.type=="radio")
				{
					// exclude certain form parameters that are really only intended for on-form use only
					if(element.id == "inSituAll" || element.id == "blotAll" 
							|| element.id == "wholeGenomeAll" || element.id == "detected3") continue;
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
			else
			{
				log("Unknown field: " + element.name + " (type " + element.tagName + ")");
			}
		}
	}
	return _qs.join("&");
};

// force a "checked" setting on the checkbox for 'allClass' and all of its sub-checkboxes for
// the given 'subClass'

var setAll = function(allClass, subClass, checked) {
	$(allClass)[0].checked = checked;
	var checkboxes = $(subClass);
	
	for (var i = 0; i < checkboxes.length; i++) {
		checkboxes[i].checked = checked;
	}
}

// wire up the "all" assay type checkboxes to (un)check their corresponding subsets of boxes

var clickAll = function(allClass, subClass) {
	// Checkbox for 'allClass' has been clicked, so make sure checkboxes of 'subClass' are
	// set likewise.
	
	var checkboxes = $(subClass);
	var checked = $(allClass)[0].checked;
	
	for (var i = 0; i < checkboxes.length; i++) {
		checkboxes[i].checked = checked;
	}
}

$('.allInSitu').click(function() {
	clickAll('.allInSitu', '.inSituAssayType');
});
$('.allBlot').click(function() {
	clickAll('.allBlot', '.blotAssayType');
});
$('.allWholeGenome').click(function() {
	clickAll('.allWholeGenome', '.wholeGenomeAssayType');
});

// wire up the individual assay type checkboxes to (un) check their corresponding "all" checkboxes

var clickOne = function(allClass, subClass) {
	// Checkbox of 'subClass' has been clicked, so make sure corresponding checkbox of 'allClass'
	// is set appropriately.
	
	var allChecked = true;
	var checkboxes = $(subClass);
	
	for (var i = 0; i < checkboxes.length; i++) {
		allChecked = allChecked && checkboxes[i].checked;
	}
	
	if ($(allClass).length > 0) {
		$(allClass)[0].checked = allChecked;
	}
}

$('.inSituAssayType').click(function() {
	clickOne('.allInSitu', '.inSituAssayType');
});
$('.blotAssayType').click(function() {
	clickOne('.allBlot', '.blotAssayType');
});
$('.wholeGenomeAssayType').click(function() {
	clickOne('.allWholeGenome', '.wholeGenomeAssayType');
});

// Add the listener for mutatedIn onFocus
var mutatedInOnFocus = function(e)
{
	YAHOO.util.Dom.get("mutatedSpecimen").checked = true;
};
YAHOO.util.Event.addFocusListener(YAHOO.util.Dom.get("mutatedIn"),mutatedInOnFocus);

/* read a delimited file where one column contains marker-related IDs, parse it,
 * and update the 'ids' field in the batch QF.
 */
var readFile = function(e) {
	var input = e.target;
	var reader = new FileReader();
	var maxBatch = 5000;

	reader.onload = function() {
		var separator = ',';
		var extractedIDs = '';

		if (document.querySelector('input[name = "fileType"]:checked').value == 'tab') {
			separator = '\t';
		}

		// switch from 1-based column number from input to 0-based
		var colNum = document.getElementById('idColumn').value - 1;
		var text = reader.result.trimRight();	// no trailing newline
		var lines = text.split(/[\n\r]+/g);
		var badLines = 0;		// count of bad lines
		var idsAdded = 0;		// count of IDs added

		// split lines then extract specified column using specified
		// delimiter
		for (var lineNum in lines) {
			var line = lines[lineNum];
			var cols = line.split(separator);

			if (cols.length > colNum) {
				var tokens = cols[colNum].split(' ');
				for (var tokenNum in tokens) {
					if (extractedIDs.length > 0) {
						extractedIDs = extractedIDs + '\n' + tokens[tokenNum];
					} else {
						extractedIDs = tokens[tokenNum];
					}
					idsAdded++;
				}
			} else {
				badLines++;
			}
		}

		// if user submitted less than max batch threshold, we update text 
		// area and page messages
		if (lines.length > maxBatch){
			alert("Please reduce input to " + maxBatch + " ids/symbols");
		}
		else { // proceed are normal

			// insert IDs into text area
			var node = document.getElementById('ids');
			node.value = extractedIDs;

			// messages
			var myMsg = '';
			if (idsAdded > 0) {
				if (idsAdded > 1) {
					myMsg = idsAdded + ' lines were added';
				} else {
					myMsg = idsAdded + ' line was added';
				}
			}

			if (badLines > 0) {
				if (myMsg.length > 0) { myMsg = myMsg + '; '; }
				myMsg = myMsg + badLines + ' line(s) had too few columns';
			}

			var msg = document.getElementById('uploadMessage');
			msg.innerHTML = myMsg;
			msg.style.display = 'inline-block';
		}
		
	};
	reader.readAsText(input.files[0]);
};

// if the 'any other structure' button is clicked and will be checked, then we'll need to
// blank out the 'specify structure' box and clear the other checkbox.
var anywhereElseClick = function() {
	if ($('#anywhereElse').length > 0) {
		if ($('#anywhereElse')[0].checked) {
			$('#difStructure4')[0].value = '';
			$('#difStructure4ID')[0].value = '';
			$('#inCheckbox')[0].checked = false;
			$('#difTheilerStage4')[0].selectedIndex = 0;
		}
	}
};

// if the 'in this structure' checkbox is clicked and will be checked, then we'll need to
// blank out the 'anywhere else' checkbox
var inCheckboxClick = function() {
	if ($('#inCheckbox').length > 0) {
		if ($('#inCheckbox')[0].checked) {
			$('#anywhereElse')[0].checked = false;
		}
	}
};

// ensure user hasn't input more than the server can easily handle
function checkBatchInput(){

	var maxBatch = 5000;
	var idList = YAHOO.util.Dom.get('ids').value.trim().replace(/[\n]+/g, '\n').replace(/\s+/, '\n').split('\n');

	// alert if over threshold cap
	if (idList.length > maxBatch){
		alert("Please reduce input to " + maxBatch + " ids/symbols");
		return false;
	}

	// alert, if empty
	if (YAHOO.util.Dom.get('ids').value.trim() == ""){
		alert("Please ensure you've entered query parameters.");
		return false;
	}
	
	
	return true;
};


/* -------------------------------------------------------------------------------------------------
 * Profile Search 
 * -------------------------------------------------------------------------------------------------
 */

// profile form modes
const CLASSICAL = "classical"
const RNASEQ = "rnaseq"

// not expressed modes
const NOWHEREELSE = "nowhereElse"
const NOTINSTRUCTURES = "notInStructures"

// data that drives the form display
var model = {
    profileSpec: [], // list of {structure, structyreID, detected, stages}
    formMode: CLASSICAL, // "classical" or "rnaseq"
    notMode: null // "nowhereElse" or "notInStructures" (may be null if no not-conditions specified)
}

function resetProfileForm () {
    model.profileSpec = [{
	    structure: "",
	    structureID: "",
	    detected: true,
	    stages: []
	    },{
	    structure: "",
	    structureID: "",
	    detected: false,
	    stages: []
    }]
    model.notMode = null
    refreshProfileForm()

}

function profileSetMode () {
    const form = document.getElementById('gxdProfileQueryForm')
    model.formMode = form.querySelector('input[type="radio"][name="profileMode"]:checked').value;
    resetProfileForm();
}

function makeTheilerStageSelector (i) {
    return `
    <select id="profileStage${i}" name="profileStage${i}" multiple="multiple" size="3" onchange="theilerStageSelectorChanged(${i})" >
    <option value="0" selected="selected">Any developmental stage</option>
    <option value="1">TS 1 (0.0-2.5 dpc)</option>
    <option value="2">TS 2 (1.0-2.5 dpc)</option>
    <option value="3">TS 3 (1.0-3.5 dpc)</option>
    <option value="4">TS 4 (2.0-4.0 dpc)</option>
    <option value="5">TS 5 (3.0-5.5 dpc)</option>
    <option value="6">TS 6 (4.0-5.5 dpc)</option>
    <option value="7">TS 7 (4.5-6.0 dpc)</option>
    <option value="8">TS 8 (5.0-6.5 dpc)</option>
    <option value="9">TS 9 (6.25-7.25 dpc)</option>
    <option value="10">TS 10 (6.5-7.75 dpc)</option>
    <option value="11">TS 11 (7.25-8.0 dpc)</option>
    <option value="12">TS 12 (7.5-8.75 dpc)</option>
    <option value="13">TS 13 (8.0-9.25 dpc)</option>
    <option value="14">TS 14 (8.5-9.75 dpc)</option>
    <option value="15">TS 15 (9.0-10.25 dpc)</option>
    <option value="16">TS 16 (9.5-10.75 dpc)</option>
    <option value="17">TS 17 (10.0-11.25 dpc)</option>
    <option value="18">TS 18 (10.5-11.25 dpc)</option>
    <option value="19">TS 19 (11.0-12.25 dpc)</option>
    <option value="20">TS 20 (11.5-13.0 dpc)</option>
    <option value="21">TS 21 (12.5-14.0 dpc)</option>
    <option value="22">TS 22 (13.5-15.0 dpc)</option>
    <option value="23">TS 23 (15 dpc)</option>
    <option value="24">TS 24 (16 dpc)</option>
    <option value="25">TS 25 (17 dpc)</option>
    <option value="26">TS 26 (18 dpc)</option>
    <option value="27">TS 27 (newborn)</option>
    <option value="28">TS 28 (postnatal)</option>
    </select>
    `
}

function resetTheilerStageSelection (i) {
    const sel = document.getElementById(`profileStage${i}`);
    Array.from(sel.options).forEach(o => o.selected = (o.value === "0"));
}

function getTheilerStageSelection (i) {
    const sel = document.getElementById(`profileStage${i}`);
    const selected = Array.from(sel.options).filter(o => o.selected && o.value !== "0").map(o => o.value);
    return selected
}

function setTheilerStageSelection (i, stages) {
    const sel = document.getElementById(`profileStage${i}`);
    Array.from(sel.options).forEach(o => o.selected = false);
    if (!stages || stages.length === 0) stages = ["0"];
    stages.forEach(s => {
        const opt = sel.querySelector(`option[value="${s}"]`).selected = true
    });
}

function theilerStageSelectorChanged (i) {
    model.profileSpec[i].stages = getTheilerStageSelection (i)
    if (model.profileSpec[i].detected === false) {
        model.notMode = NOTINSTRUCTURES
    }
}

function makeProfileRow (model, i) {
    var profileRow = 
    `<tr id="profileStructureRow${i}" class="profile-structure-row ${model.detected ? 'detected' : 'not-detected'}" >
    <td >
	<button type="button" onClick="removeProfileRow(${i})" id="removeStructureRowButton${i}" 
	    class="removeButton" title="Remove this structure.">X</button>
	<input style="width: 320px; position: relative;" id="profileStructure${i}" name="profileStructure${i}" 
	    value="${model.structure}" placeholder="anatomical structure"></input>
	<input type="hidden" id="profileStructure${i}ID" name="profileStructureID${i}" 
	    value="${model.structureID}"/>
	<div class="anatomyAC" style="width: 400px;" id="profileStructureContainer${i}"></div>
    </td>
    <td>
        <span>and/or</span>
    </td>
    <td>
	${makeTheilerStageSelector(i)}
    </td>
    </tr>`;
    return profileRow;
}

// Encodes the current model state into an parameter string
function profileSpec2QueryString () {
    var qs = model.profileSpec.map ((m,i) => {
        return `profileStructure=${m.structure}&profileStructureID=${m.structureID}&profileDetected=${m.detected}&profileStage=${m.stages.join('|')}`
    }).join('&');
    qs += `&formMode=${model.formMode}&notMode=${model.notMode}`
    return qs;
}

// Parses a parameter string and refreshes the model data
function profileQueryString2Spec (qs) {
    const structures = []
    const structureIDs = []
    const detected = []
    const stages = []
    YAHOO.util.Dom.get("profileNowhereElseCheckbox").checked = false;
    qs.split('&').forEach(q => {
        const pts = q.split('=');
	if (pts[0] === "profileStructure") {
	    structures.push(pts[1])
	} else if (pts[0] === "profileStructureID") {
	    structureIDs.push(pts[1])
	} else if (pts[0] === "profileDetected") {
	    detected.push( pts[1] ==="true" )
	} else if (pts[0] === "profileStage") {
	    stages.push( pts[1].split("|").filter(x=>x))
	} else if (pts[0] === "formMode") {
	    model.formMode = pts[1]
	} else if (pts[0] === "notMode") {
	    model.notMode = pts[1]
	}
    })
    if (structures.length !== structureIDs.length || structures.length !== detected.length) {
        throw "query string contains different numbers of profile parameters."
    }
    var foundParams = false
    model.profileSpec = []
    structures.forEach((s,i) => {
	if (s) foundParams = true;
        model.profileSpec.push({
	    structure: s,
	    structureID: structureIDs[i],
	    detected: detected[i],
	    stages: stages[i]
	})
    })
    refreshProfileForm()
    return foundParams
}

function enableDisableSection (section, enable) {
    const sec = document.getElementById(section);
    if (!sec) return;

    const action = enable ? 'remove' : 'add';
    sec.classList[action]("disabledSection");

    const d = !enable;
    const inputs = sec.querySelectorAll('input')
    inputs.forEach(i => i.disabled=d);
    const buttons = sec.querySelectorAll('button');
    buttons.forEach(i => i.disabled=d);
    const selectors = sec.querySelectorAll('select');
    selectors.forEach(i => i.disabled=d);
}
function enableSection (s) {
    enableDisableSection(s, true)
}
function disableSection (s) {
    enableDisableSection(s, false)
}

function refreshEnabledDisabled () {
    const nwe = document.getElementById("profileNowhereElseCheckbox");
    const nweText = document.getElementById("nowhereElseText");

    const nis = document.getElementById("profileNotInStructuresCheckbox");
    const nisText = document.getElementById("profileNotInStructuresText");

    nwe.disabled = (model.notMode === NOTINSTRUCTURES);
    if (nwe.disabled) {
	nweText.classList.add("disabledText");
    } else {
	nweText.classList.remove("disabledText");
    }

    nis.disabled = (model.notMode === NOWHEREELSE);
    if (nis.disabled) {
	disableSection('notDetectedSection')
	disableSection('afterNotDetectedSection')
    }
    else {
	enableSection('notDetectedSection')
	enableSection('afterNotDetectedSection')
    }
}

function profileSetNotMode() {
    model.notMode = null
    if (document.getElementById("profileNowhereElseCheckbox").checked) model.notMode = NOWHEREELSE;
    if (document.getElementById("profileNotInStructuresCheckbox").checked) model.notMode = NOTINSTRUCTURES;
    refreshEnabledDisabled()
}

/*
 * Refreshes the DOM from the model
 * */
function refreshProfileForm () {
    const pform = document.getElementById("gxdProfileQueryForm");
    const ptbl = document.getElementById("profileStructureTable");

    const posTbody = ptbl.querySelector("#detectedSection");
    const posSpecs = model.profileSpec.map((m,i) => m.detected ? makeProfileRow(m,i) : '').join('');
    posTbody.innerHTML = posSpecs;

    const negTbody = ptbl.querySelector("#notDetectedSection");
    const negSpecs = model.profileSpec.map((m,i) => m.detected ? '' : makeProfileRow(m,i)).join('');
    negTbody.innerHTML = negSpecs;

    model.profileSpec.forEach((m,i) => {
	const objs = makeStructureAC(`profileStructure${i}`,`profileStructureContainer${i}`);
	objs.oAC.itemSelectEvent.subscribe(() => {
	    // when user selects from AC, update the model and the form
	    if (model.notMode !== NOTINSTRUCTURES) {
		pform.profileNowhereElseCheckbox.checked = false;
		pform.profileNotInStructuresCheckbox.checked = true;
		model.notMode = NOTINSTRUCTURES;
	    }
	    refreshProfileSpec (i) ;
	    refreshEnabledDisabled();
	});
	setTheilerStageSelection(i, m.stages);
    })

    const formModeRadio = pform.querySelector(`input[name="profileMode"][value="${model.formMode}"]`)
    formModeRadio.checked = true

    if (model.formMode === CLASSICAL) {
        pform.classList.replace(RNASEQ,CLASSICAL);
    }
    else if (model.formMode === RNASEQ) {
        pform.classList.replace(CLASSICAL,RNASEQ);
    }

    pform.profileNowhereElseCheckbox.checked = (model.notMode === NOWHEREELSE)
    pform.profileNotInStructuresCheckbox.checked = (model.notMode === NOTINSTRUCTURES)

    refreshEnabledDisabled()
}

function refreshProfileSpec (i) {
    const structure = document.getElementById(`profileStructure${i}`).value;
    const structureID = document.getElementById(`profileStructure${i}ID`).value;
    model.profileSpec[i].structure = structure
    model.profileSpec[i].structureID = structureID
}

function addProfileRow (detected) {
    if (model.profileSpec.length === 10) {
	alert("Maximum of ten anatomical structures allowed.")
	return
    }
    model.profileSpec.push({
        structure: "",
	structureID: "",
	detected: detected,
	stages: []
    })
    refreshProfileForm();
}

function removeProfileRow (i) {
	model.profileSpec.splice(i,1);
	refreshProfileForm();
}

function profileClearStages () {
	model.profileSpec.forEach(m => m.stages = []);
	refreshProfileForm();
}

function profileToggleShowStages () {
    const cb = document.getElementById("profileShowStagesCheckbox");
    const tbl = document.getElementById("profileStructureTable");
    if (cb.checked) {
        tbl.classList.remove("hideStages");
    }
    else {
	profileClearStages();
        tbl.classList.add("hideStages");
    }
}

resetProfileForm()
