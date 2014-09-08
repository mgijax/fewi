/*
 * This file handles all the functions necessary for the autocomplete(s) 
 * 	used by the human disease portal
 * 
 * @author kstone
 */


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

//set up any autocomplete behavior

/*
 * Phenotype / OMIM Disease autocomplete section
* Author: kstone
 */
// the split and extractLast functions define how we will delimit multiple selections
function split( val ) {
    // first pass, split by comma-space
    var firstPassTerms = val.split( /,\s*/ );

    // second pass, break booleans out into separate terms
    var terms = [];
    var maxLength = firstPassTerms.length;

    for (var i=0; i < maxLength; i++) {
	var term = firstPassTerms[i];

	if (term.indexOf("AND NOT ") == 0) {
	    terms.push("AND NOT ");
	    terms.push(term.slice(8));
	} else if (term.indexOf("OR ") == 0) {
	    terms.push("OR ");
	    terms.push(term.slice(3));
	} else if (term.indexOf("AND ") == 0) {
	    terms.push("AND ");
	    terms.push(term.slice(4));
	} else {
	    terms.push(term);
	}
    } 
    return terms; 
}

function extractLast( term ) {
	return split( term ).pop();
}

$(function(){
	//var fewiurl = "http://cardolan.informatics.jax.org/";
	var phenotypesACUrl = fewiurl+"autocomplete/diseasePortal/phenotypes?query=";
	var disableColor = "#CCC";
	$.support.cors = true;
	
	// this section resets how tabbing works, so that it allows multiple autocomplete selections
	// NOTE: the ID of the phenotypes search box must be "phenotypes"
	var phenotypesAC = $( "#phenotypes" ).bind( "keydown", function( event ) {
		if ( event.keyCode === $.ui.keyCode.TAB &&
				$( this ).data( "ui-autocomplete" ).menu.active ) {
			event.preventDefault();
		}
	})
	// this section set the jquery UI autocomplete feature
	.autocomplete({
	source: function( request, response ) {
		$.ajax({
			url: phenotypesACUrl+extractLast( request.term ),
			dataType: "json",
			success: function( data ) {
				response($.map(data["summaryRows"], function( item ) {
					// this function handles the json response.
					// we redefine the data format we will use in the render functions
					return {label: item.termId, formattedTerm: item.formattedTerm};
				}));
			}
		});
	},
	focus: function() {
		// prevent value inserted on focus
		return false;
	},
	minLength: 2, // number of characters to trigger autocomplete
	select: function( event, ui ) {
		// the select function is called when user selects an item
		var terms = split( this.value );
		// remove the current input
		terms.pop();
		// add the selected item
		terms.push( ui.item.value );
		// add placeholder to get the comma-and-space at the end
		terms.push( "" );
		this.value = terms.join( ", " );

		// replace any extra commas
		this.value = this.value.replace("AND , ", "AND ");
		return false;
	}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		// the render item function is how the item displays in the autocomplete
		var value = item.formattedTerm;
		return $('<li></li>')
			.data("item.autocomplete",item)
			.append("<a>" + value + "</a>")
			.appendTo(ul);
		// adding the item this way makes it disabled
	//	return $('<li class="ui-menu-item disabled" style="color:#CCC;"></li>')
	//		.data("item.autocomplete", item)
	//		.append('<span>'+value+'</span>')
	//		.appendTo(ul);
	};
});

