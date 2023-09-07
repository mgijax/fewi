// --------- some global configurable variables ----------
// Form display toggle
//   false = form is displayed
var qDisplay = false;

// The string that gets passed via the AJAX call   
var querystring = "";

var qfId = "diseasePortalQueryForm";

var QFHeight = 100; // height of the qf during animation (should be close to the actual pixel height, but need not be exact)


// register any help panels
YAHOO.namespace("hdp.container"); 
YAHOO.hdp.container.panelVcf = new YAHOO.widget.Panel("locationsFileHelp", { width:"520px", draggable:false, visible:false, constraintoviewport:true } ); 
YAHOO.hdp.container.panelVcf.render(); 
//YAHOO.util.Event.addListener("locationsFileHelpImg", "mouseover", YAHOO.hdp.container.panelVcf.show, YAHOO.hdp.container.panelVcf, true);
var _locationsFileHelpTOID;
$("#locationsFileHelpImg").on("mouseover",function(e){
	_locationsFileHelpTOID = setTimeout(function(){YAHOO.hdp.container.panelVcf.show()},500);
});
$("#locationsFileHelpImg").on("mouseout",function(e){
	if(_locationsFileHelpTOID) clearTimeout(_locationsFileHelpTOID);
});

YAHOO.hdp.container.panelQueryHelp = new YAHOO.widget.Panel("queryHelp", { width:"520px", draggable:false, visible:false, constraintoviewport:true } );
YAHOO.hdp.container.panelQueryHelp.render();

var _queryHelpTOID;
$("#queryHelpImg").on("mouseover",function(e){
   _queryHelpTOID = setTimeout(function(){YAHOO.hdp.container.panelQueryHelp.show()},500);
});
$("#queryHelpImg").on("mouseout",function(e){
   if(_queryHelpTOID) clearTimeout(_queryHelpTOID);
});



// ---------- functions for handling form submit action -----------
//Instead of submitting the form, do an AJAX request
var interceptSubmit = function(e) 
{
	e.preventDefault();
	
	if (!runValidation()){
		// Do not allow any content to overflow the outer
		// div when it is hiding
		$("#outer").css("overflow","hidden");

		if(_GF) _GF.init();
		
		hmdcFilters.callbacksOff()
		hmdcFilters.clearAllFilters();
		hmdcFilters.callbacksOn()

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
$("#"+qfId).on("submit",interceptSubmit);


//----- functions for resetting the queryform
//
//Wire up the functionality to reset the query form
//
var resetQF = function (e) {
	var fromButtonClick = false;
	if (e)
	{
		if(typeof e.preventDefault == 'function') e.preventDefault(); 
		fromButtonClick = true;
	}
	
	var form = YAHOO.util.Dom.get(qfId);
	form.phenotypes.value = "";
	form.genes.value = "";
	form.locations.value = "";
	form.organism[1].checked = true;
	form.locationsFileName.value = "";
	form.geneFileName.value = "";
	form.enableVcfFilter = "true";
	window.querystring = '';

	//form.fGene.value = "";
	//form.fHeader.value = "";
	if(_GF && !_GF.isState(_GF.gridState.working)) _GF.resetFields();
	
	if(fromButtonClick)
	{
		if (typeof resetLocationsFileFields == 'function')
		{
			resetLocationsFileFields();
		}
		if (typeof resetGeneFileFields == 'function')
		{
			resetGeneFileFields();
		}
	}

	// don't forget to match the mirrored organism radio
	$("#organismMouse2").prop("checked",true);
	$("#enableVcfFilter").prop("checked",true);

	// clear the validation errors
	clearValidation();
	return false;
};

$("#"+qfId).on("reset",resetQF);

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

	ysfText += "<span id=\"errorTextMessage\" style=\"display: none;\"><br>There is an error in your query, indicated by bolded text:<br><span id=\"errorTextString\"></span><br><b>Query was modified and run in the following way:</b></span>";
	
	var values = serializeQF();
	if ("phenotypes" in values && values["phenotypes"]!="")
	{
		ysfText += "<br/>"
		ysfText += "Phenotypes or Diseases matching: <b id=\"ysf-phenotypes\">"+values["phenotypes"]+"</b>";
	}
	if ("genes" in values && values["genes"]!="")
	{
		ysfText += "<br/>"
		ysfText += "Genes matching [<b>"+$('<div/>').text(values["genes"]).html()+"</b>]";
	}
	if ("geneFileName" in values && values["geneFileName"]!="")
	{
		ysfText += "<br/>"
		ysfText += "Genes matching [<b id=\"ysf-geneFile\">file="+$('<div/>').text(values["geneFileName"]).html()+"</b>]";
	}
	if ("locations" in values && values["locations"]!="")
	{
		ysfText += "<br/>"
		var organism = values["organism"] == "human" ? "Human" : "Mouse";
		ysfText += organism+" loci overlapping interval: [<b>"+$('<div/>').text(values["locations"]).html()+"</b>]";
	}
	if ("locationsFileName" in values && values["locationsFileName"]!="")
	{
		ysfText += "<br/>"
		var organism = values["organism"] == "human" ? "Human" : "Mouse";
		ysfText += organism+" loci overlapping interval: [<b id=\"ysf-locationsFile\">file="+$('<div/>').text(values["locationsFileName"]).html()+"</b>]";
	}
	
	summaryDiv.append(ysfText);
	
	// make sure IDs turn into terms (just for the display purposes)
	resolveVocabTermIds(true);
	
	// check if file upload is still cached
	checkFileUploadCache();

	// Run query againt parser to check for errors
	getErrorMessages();
};


/*
 * makes an ajax request to adjust the display of "locationsFile" field in
 * the "you searched for" section.
 * 	It displays a warning if there is no cached version of the file, which is required to return any results.
 */ 
var checkFileUploadCache = function()
{
	var data = querystring;
	var request = $.ajax({
		url:fewiurl+"diseasePortal/isLocationsFileCached",
		type: "post",
		data: data
	});
	request.done(function (response, textStatus, jqXHR){
		if(textStatus=="success")
		{
			// display warning if it doesn't exist
			$("#ysf-locationsFile").append("&nbsp;<span style=\"color:red;\">"+response+"</span>");
			
			// update the cached message
		    var values = serializeQF();
			if (response=="" && "locationsFileName" in values && values["locationsFileName"]!="")
			{
				$("#locationsFileNotify").show().html("<span>(Using cached file ["+values["locationsFileName"]+"])</span>");
			}
			else
			{
				$("#locationsFileNotify").hide().html("");
			}
		}
    })
    
    var request2 = $.ajax({
		url:fewiurl+"diseasePortal/isGeneFileCached",
		type: "post",
		data: data
	});
	request.done(function (response, textStatus, jqXHR){
		if(textStatus=="success")
		{
			// display warning if it doesn't exist
			$("#ysf-geneFile").append("&nbsp;<span style=\"color:red;\">"+response+"</span>");
			
			// update the cached message
//		    var values = serializeQF();
//			if (response=="" && "geneFileName" in values && values["geneFileName"]!="")
//			{
//				//$("#geneFileNotify").show().html("<br/><span>(Using cached file ["+values["geneFileName"]+"])</span>");
//			}
//			else
//			{
//				$("#geneFileNotify").hide().html("");
//			}
		}
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
	repositionUploadWidgets();
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
	if("geneFileName" in values && values["geneFileName"]!="")
	{
		params.push("geneFileName="+values["geneFileName"]);
	}
	if("locations" in values && values["locations"]!="")
	{
		params.push("locations="+values["locations"]);
		params.push("organism="+values["organism"]);
	}
	if("locationsFileName" in values && values["locationsFileName"]!="")
	{
		params.push("locationsFileName="+values["locationsFileName"]);
		if("disableVcfFilter" in values && values["disableVcfFilter"]=="true")
		{
			params.push("disableVcfFilter=true");
		}
		params.push("organism="+values["organism"]);
	}
	
	if("numDCol" in values && values["numDCol"]!="") params.push("numDCol="+values["numDCol"]);
	
	if("featureTypeFilter" in values && values["featureTypeFilter"]!="")
	{
		params.push("featureTypeFilter=" + values["featureTypeFilter"]);
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

	hmdcFilters.setAllFilters(params);

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
							$(radioInput).trigger("change");
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
	if(typeof refreshEnableVcfFilterValue == 'function')
	{
		refreshEnableVcfFilterValue();
	}

	return foundParams;
}
