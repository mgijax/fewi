// --------- some global configurable variables ----------
// Form display toggle
//   false = form is displayed
var qDisplay = false;

// The string that gets passed via the AJAX call   
var querystring = "";

var qfId = "diseasePortalQueryForm";

var QFHeight = 100; // height of the qf during animation (should be close to the actual pixel height, but need not be exact)


// ---------- functions for handling form submit action -----------
//Instead of submitting the form, do an AJAX request
var interceptSubmit = function(e) 
{
	YAHOO.util.Event.preventDefault(e);	
	
	if (!runValidation()){
		// Do not allow any content to overflow the outer
		// div when it is hiding
		var outer = YAHOO.util.Dom.get('outer');
		YAHOO.util.Dom.setStyle(outer, 'overflow', 'hidden');

		if(_GF) _GF.init();
		
		// Set the global querystring to the form values
		window.querystring = getQueryString();
		
		mgiTab.newQueryState = true;
		
		if(typeof resultsTabs != 'undefined')
		{
			// go to grid tab first for every query
			resultsTabs.selectTab(0);
		}

		toggleQF(openSummaryControl);

		if(typeof hdpDataTable != 'undefined')
			hdpDataTable.setAttributes({ width: "100%" }, true);
	}
};
YAHOO.util.Event.addListener(qfId, "submit", interceptSubmit);


//----- functions for resetting the queryform
//
//Wire up the functionality to reset the query form
//
var resetQF = function (e) {
	if (e) YAHOO.util.Event.preventDefault(e); 
	var form = YAHOO.util.Dom.get(qfId);
	form.phenotypes.value = "";
	form.genes.value = "";
	form.locations.value = "";
	form.organism[0].checked = true;
	
	//form.fGene.value = "";
	//form.fHeader.value = "";
	if(_GF && !_GF.isState(_GF.gridState.working)) _GF.resetFields();
	
	// clear the validation errors
	clearValidation();
};

YAHOO.util.Event.addListener(qfId, "reset", resetQF);

var runValidation  = function()
{
	return false;
};
var clearValidation = function()
{
	//pass
}


// -------------- functions for the animation of the queryform ------------------
//
//Handle the animation for the queryform
//
var qfAnim = new MGIQFAnimator("diseasePortalSearch");
var toggleQF = qfAnim.toggleQF;

// ---------- functions for the "You Search For: " text
// Updates the "You searched for" section
var updateQuerySummary = function() {
	var summaryDiv = $('#searchSummary');
	summaryDiv.html("");
	var ysfText = "<b>You searched for: </b>";
	
	var values = serializeQF();
	if ("phenotypes" in values && values["phenotypes"]!="")
	{
		ysfText += "<br/>"
		ysfText += "Phenotypes or Diseases matching [<b id=\"ysf-phenotypes\">"+values["phenotypes"]+"</b>]";
	}
	if ("genes" in values && values["genes"]!="")
	{
		ysfText += "<br/>"
		ysfText += "Genes matching [<b>"+$('<div/>').text(values["genes"]).html()+"</b>]";
	}
	if ("locations" in values && values["locations"]!="")
	{
		ysfText += "<br/>"
		var organism = values["organism"] == "human" ? "Human" : "Mouse";
		ysfText += organism+" locations matching [<b>"+$('<div/>').text(values["locations"]).html()+"</b>]";
	}
	
	summaryDiv.append(ysfText);
	
	// make sure IDs turn into terms (just for the display purposes)
	resolveVocabTermIds();
};

/*
 * makes an ajax request to adjust the display of "phenotypes" field in
 * the "you searched for" section.
 * 	It resolves any term IDs into their term names.
 * 	This only really needs to be used if the user had autocomplete interaction...
 */ 
var resolveVocabTermIds = function()
{
	var data = "ids="+$("#ysf-phenotypes").text();
	var request = $.ajax({
		url:fewiurl+"autocomplete/vocabTerm/resolve",
		type: "post",
		data: data
	});
	request.done(function (response, textStatus, jqXHR){
		if(textStatus=="success") $("#ysf-phenotypes").text(response);
    })
}

//--------- Functions for controlling opening the summary and rolling up the form (without animation) ---------------
//Open all the controls tagged with the summaryControl class
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


// --------- Functions for serializing/deserializing QF parameters ---------------
var serializeQF = function()
{
	var values = {};
	$.each($('#'+qfId).serializeArray(), function(i, field) {
	    values[field.name] = field.value;
	});
	return values;
}
//
// Return the passed in form argument values in key/value URL format
//
var getQueryString = function() 
{
	var values = serializeQF();
	var params = [];
	if("phenotypes" in values && values["phenotypes"]!="") params.push("phenotypes="+values["phenotypes"]);
	if("genes" in values && values["genes"]!="") params.push("genes="+values["genes"]);
	if("locations" in values && values["locations"]!="")
	{
		params.push("locations="+values["locations"]);
		params.push("organism="+values["organism"]);
	}
	// try to add grid filters if they exist
	var hasGridFilters = false;
	if(!_GF.isState(_GF.gridState.working))
	{
		gridParams = [];
		if("fGene" in values && values["fGene"]!="")
		{
			hasGridFilters = true;
			params.push("fGene="+values["fGene"]);
			gridParams.push("fGene="+values["fGene"]);
		}
		if("fHeader" in values && values["fHeader"]!="")
		{
			hasGridFilters = true;
			params.push("fHeader="+values["fHeader"]);
			gridParams.push("fHeader="+values["fHeader"]);
		}
		var gridFilterQuery = gridParams.join("&");
		_GF.prevFilterQuery = gridFilterQuery;
	}
	else if(_GF.prevFilterQuery && _GF.prevFilterQuery!="")
	{
		hasGridFilters = true;
		params.push(_GF.prevFilterQuery);
	}
	if(_GF) _GF.toggleFiltersIndicator(hasGridFilters);
	
	
	var qstring = params.join("&");
	// escape some url sensitive characters
	qstring = qstring.replace(/%/g,"%25").replace(/#/g,"%23");
	return qstring;
};

// parses request parameters and resets and values found with their matching form input element
// returns false if no parameters were found
// responsible for repopulating the form during history manager changes
function reverseEngineerFormInput(request)
{
	var params = parseRequest(request);
	var formID = "#"+qfId

	var foundParams = false;
	resetQF();
	for(var key in params)
	{
		if(key!=undefined && key!="" && params[key].length>0)
		{
			// jQuery is better suited to resolving form name parameters
			var jqInput = $(formID+" [name='"+key+"']");
			if(jqInput.length < 1) jqInput = $(formID+" #"+key);
			if(jqInput!=undefined && jqInput!=null && jqInput.length > 0)
			{
				input = jqInput[0];
				if(input.tagName=="INPUT")
				{
					foundParams = true;
					// do radio boxes
					if(input.type == "radio")
					{
						// if it's a radio, then the name probably matched several items.
						// loop through them to figure out which radio needs to be selected
						for(var i=0; i<jqInput.length; i++)
						{
							var radioInput = jqInput[i];
							radioInput.checked = radioInput.value == params[key];
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
						input.value = decodeURIComponent(params[key]);
					}
				}
				else if(input.tagName=="TEXTAREA")
				{
					input.value = decodeURIComponent($.trim(params[key]));
				}
			}
		}
	}
	return foundParams;
}


// set up any autocomplete behavior

/*
 * Phenotype / OMIM Disease autocomplete section
* Author: kstone
 */
function split( val ) {
	return val.split( /,\s*/ );
}
function extractLast( term ) {
	return split( term ).pop();
}
//var fewiurl = "http://cardolan.informatics.jax.org/";
var phenotypesACUrl = fewiurl+"autocomplete/diseasePortal/phenotypes?query=";
var disableColor = "#CCC";
// I can't figure out CORS behavior in YUI2
// resorting to jQuery implementation
$.support.cors = true;

var phenotypesAC = $( "#phenotypes" ).bind( "keydown", function( event ) {
	if ( event.keyCode === $.ui.keyCode.TAB &&
			$( this ).data( "ui-autocomplete" ).menu.active ) {
		event.preventDefault();
	}
})
.autocomplete({
source: function( request, response ) {
	$.ajax({
		url: phenotypesACUrl+extractLast( request.term ),
		dataType: "json",
		success: function( data ) {
			response($.map(data["summaryRows"], function( item ) {
				return {label: item.termId, formattedTerm: item.formattedTerm};
			}));
		}
	});
},
focus: function() {
	// prevent value inserted on focus
	return false;
},
minLength: 2,
select: function( event, ui ) {
	var terms = split( this.value );
	// remove the current input
	terms.pop();
	// add the selected item
	terms.push( ui.item.value );
	// add placeholder to get the comma-and-space at the end
	terms.push( "" );
	this.value = terms.join( ", " );
	return false;
}
}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
var value = item.formattedTerm;
//if (item.isStrictSynonym)
//{ 
//	//var synonymColor = item.hasCre ? "#222" : disableColor;
//	value += " <span style=\"color:"+synonymColor+"; font-size:0.9em; font-style:normal;\">[<span style=\"font-size:0.8em;\">syn. of</span> "+item.original+"]</span> "; 
//}
//if (item.hasCre)
//{
	return $('<li></li>')
		.data("item.autocomplete",item)
		.append("<a>" + value + "</a>")
		.appendTo(ul);
//}
// adding the item this way makes it disabled
//return $('<li class="ui-menu-item disabled" style="color:#CCC;"></li>')
//	.data("item.autocomplete", item)
//	.append('<span>'+value+'</span>')
//	.appendTo(ul);
};