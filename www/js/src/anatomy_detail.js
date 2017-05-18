// Javascript for anatomy detail (browser)

function log(msg) {
    // log a message to the browser console
    console.log("anatomy_detail.js: " + msg);
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

var gq_disableColor = "#CCC";	// color to use for disabled structures

function makeStructureAC(inputID,containerID){
    var structureAC = $("#" + inputID).autocomplete({
		source: function( request, response ) {
			$.ajax({
				url: fewiurl + "autocomplete/gxdEmapa?query=" + request.term,
				dataType: "json",
				success: function( data ) {
					response($.map(data["resultObjects"], function( item ) {
						return {synonym: item.synonym, 
							isStrictSynonym: item.isStrictSynonym, label: item.structure,
							original: item.structure};
					}));
				}
			});
		},
		minLength: 2,					// start making autocomplete suggestions at 2 characters
		autoFocus: true,				// automatically select first item on Enter
		select: function (event, ui) {
			$("#" + inputID).val(ui.item.label);
			$('[name=vocabBrowserSearchForm]').submit();
		}
    }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		var value = highlight(inputID, item.original);
		if (item.isStrictSynonym) {
			value = highlight(inputID, item.synonym);
			var synonymColor = item.hasCre ? "#222" : gq_disableColor;
			value += " <span style=\"color:"+synonymColor+"; font-size:0.9em; font-style:normal;\">[<span style=\"font-size:0.8em;\">syn. of</span> "+item.original+"]</span> ";
		}
		return $('<li class="ui-menu-item"></li>')
			.data("item.autocomplete", item)
			.append('<span>'+value+'</span>')
			.appendTo(ul);
    };
};

// highlights occurrences of the search tokens in 'term'
function highlight(inputID, term) {
	var tokens = $('#' + inputID).val().trim().split(/\s/);	// split search string into tokens by whitespace
	var words = term.split(/\s/);							// words of term split by whitespace
	
	// sort tokens by length then alpha, so we match longer ones first
	tokens.sort(function(a,b) { return a.length - b.length || a.localeCompare(b); })
	
	var wordCount = words.length;
	var tokenCount = tokens.length;
	
	for (var w = 0; w < wordCount; w++) {
		var originalWord = words[w];
		for (var t = 0; t < tokenCount; t++) {
			var newWord = boldPrefix(originalWord, tokens[t]);
			if (newWord != originalWord) {
				words[w] = newWord;
				break;			// found a token that matched, so move on to next word
			}
		}
	}
	return words.join(' ');
};

// if 'prefix' occurs at the start of 'word' (case insensitive), then bold it in the return string
function boldPrefix(word, prefix) {
    return word.replace(new RegExp('^(' + prefix + ')(.*)','ig'), '<b>$1</b>$2');
};
