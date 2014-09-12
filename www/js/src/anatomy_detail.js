// Javascript for anatomy detail (browser)

function log(msg) {
    // log a message to the browser console
    setTimeout(function() { throw new Error(msg); }, 0);
}

//GXD tooltips
var tsTooltips = {
		"TS1":"One cell egg",
		"TS2":"Beginning of cell division",
		"TS3":"Morula",
		"TS4":"Advanced division/segmentation",
		"TS5":"Blastocyst",
		"TS6":"Implantation",
		"TS7":"Formation of egg cylinder",
		"TS8":"Differentiation of egg cylinder",
		"TS9":"Advanced endometrial reaction; prestreak",
		"TS10":"Amnion; midstreak",
		"TS11":"Neural plate, presomite stage; no allantoic bud",
		"TS12":"First somites; late head fold",
		"TS13":"Turning",
		"TS14":"Formation & closure anterior neuropore",
		"TS15":"Formation of posterior neuropore, forelimb bud",
		"TS16":"Closure post. neuropore, hindlimb & tail bud",
		"TS17":"Deep lens indentation",
		"TS18":"Closure lens vesicle",
		"TS19":"Complete separation of lens vesicle",
		"TS20":"Earliest sign of fingers",
		"TS21":"Anterior footplate indented, marked pinna",
		"TS22":"Fingers separate distally",
		"TS23":"Toes separate",
		"TS24":"Reposition of umbillical hernia",
		"TS25":"Fingers and toes joined together",
		"TS26":"Long whiskers",
		"TS28":"Postnatal development"
		};
var tsBoxIDs = ["stageLinker"];
for(var j=0;j<tsBoxIDs.length;j++)
{
	var tsBox = YAHOO.util.Dom.get(tsBoxIDs[j]);
	if(tsBox!=null)
	{
		for(var i=0; i< tsBox.children.length; i++)
		{
			var option = tsBox.children[i];
			var ts = option.text.split(" ")[0];

			// check if we've defined the tooltip for this option
			if(tsTooltips[ts])
			{
				var ttText = "<b>"+option.text+"</b>"+
					"<br/>"+tsTooltips[ts];
//				var tt = new YAHOO.widget.Tooltip("tsTT_"+j+"_"+i,{context:option, text:ttText,showdelay:1000,xyoffset:[100,0]});
			}
		}
	}
}

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
 * Anatomical Dictionary Auto Complete Section (modified from gxd_query.js)
 */
function makeStructureAC(inputID,containerID){
    // disable the autocomplete for now

    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource(fewiurl + "autocomplete/emapa");
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

    // try to set the input field after itemSelect event
    oAC.suppressInputUpdate = true;
    var selectionHandler = function(sType, aArgs) { 
	    //log("selectionHandler() called");
	    var myAC = aArgs[0]; // reference back to the AC instance 
	    var elLI = aArgs[1]; // reference to the selected LI element 
	    var oData = aArgs[2]; // object literal of selected item's result data 
	    //populate input box with another value (the base structure name)
	    var structure = oData[1]; // 0 = term, 1 = ACtext
	    var inputBox = YAHOO.util.Dom.get(inputID);
	    inputBox.value = structure;
	    refreshSearchPane();
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
    	
    return {
        oDS: oDS,
        oAC: oAC
    };
};
