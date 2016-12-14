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
var resolveVocabTermIds = function(updateYST)
{
	var data = "";

	if($("#phenotypes").val().length > 0) {
		data = "ids="+$("#phenotypes").val();
	} else {
		data = "ids=";
	}

	var request = $.ajax({
		url:fewiurl+"autocomplete/vocabTerm/resolve",
		type: "post",
		data: data
	});
	if(updateYST) {
		request.done(function (response, textStatus, jqXHR){
			$("#ysf-phenotypes").text(response.ids);
			if(textStatus=="success") {
				$("#ysf-phenotypes").text(response.ids);
				if(response.error) {
					$("#errorTextString").html(response.error);
					$("#errorTextMessage").show();
				}
				if($("#showingQuery").prop("checked")) {
					if(response.ids && response.ids.length > 0) {
						$("#queryText").html("<b>Effective Phenotype Query:</b><br/> " + response.ids);
					} else {
						$("#queryText").html("<b>Effective Phenotype Query:</b><br/> ");
					}
				} else {
					$("#queryText").html("");
				}
			}
	   });
	} else {
		request.done(function (response, textStatus, jqXHR){
			if(textStatus=="success") {
				if(response.error) {
				}
				if($("#showingQuery").prop("checked")) {
					if(response.ids && response.ids.length > 0) {
						$("#queryText").html("<b>Effective Phenotype Query:</b><br/> " + response.ids);
					} else {
						$("#queryText").html("<b>Effective Phenotype Query:</b><br/> ");
					}
				} else {
					$("#queryText").html("");
				}
			}
	   });

	}
}

var getErrorMessages = function()
{
	//alert("Blah");
}

//set up any autocomplete behavior

/*
 * Phenotype / DO Disease autocomplete section
* Author: kstone
 */
// the split and extractLast functions define how we will delimit multiple selections
function split( val ) {
	return val.split( /,\s*/ );
}
function extractLast( term ) {
	if (typeof(term) == 'undefined') { return ""; }
	var term = split( term ).pop();
	var terms = term.split(/\s+/);
	while(terms[0] == "") { terms.shift(); }
	if(terms.length > 0) {
		if(terms[0] == "AND" || terms[0] == "OR") {
			terms.shift();
		}
		if(terms.length > 0 && terms[0] == "NOT") {
			terms.shift();
		}
	}
	if(terms.length > 0) {
		return terms.join(" ");
	} else {
		return "";
	}
}

$(function(){
	//var fewiurl = "http://cardolan.informatics.jax.org/";
	var phenotypesACUrl = fewiurl+"autocomplete/diseasePortal/phenotypes?query=";
	var disableColor = "#CCC";
	$.support.cors = true;
	
	// this section resets how tabbing works, so that it allows multiple autocomplete selections
	// NOTE: the ID of the phenotypes search box must be "phenotypes"
	var phenotypesAC = $( "#phenotypes" ).bind( "keydown", function( event ) {
		if ( event.keyCode === $.ui.keyCode.TAB && $( this ).data( "ui-autocomplete" ).menu.active ) {
			event.preventDefault();
		}
	})
	.bind("keyup", function(event) {
		if ( extractLast(this.value).length < 2) {
			$(".ui-autocomplete").hide();
		}
		if(this.value.slice(-2) == "\" ") {
			// And a comma after the end of a quote
			this.value = [this.value.slice(0, this.value.length -2), '",', ' '].join('');
			$(".ui-autocomplete").hide();
		}
		if(this.value.slice(-4) == "AND ") {
			this.value = [this.value.slice(0, this.value.length -4), 'AND,', ' '].join('');
			$(".ui-autocomplete").hide();
		}
		if(this.value.slice(-4) == "NOT ") {
			this.value = [this.value.slice(0, this.value.length -4), 'NOT,', ' '].join('');
			$(".ui-autocomplete").hide();
		}
		if(this.value.slice(-3) == "OR ") {
			this.value = [this.value.slice(0, this.value.length -3), 'OR,', ' '].join('');
			$(".ui-autocomplete").hide();
		}
		if($("#showingQuery").prop("checked")) {
			resolveVocabTermIds();
		}
	})
	// this section set the jquery UI autocomplete feature
	.autocomplete({
	source: function( request, response ) {
		if(extractLast(request.term).length >= 2) {
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
		}
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
		var oldterm = terms.pop();
		var oldterms = oldterm.split(/\s+/);
		while(oldterms[0] == "") { oldterms.shift(); }
		if(oldterms.length >= 1 && (oldterms[0] == "AND" || oldterms[0] == "OR")) {
			oldterm = oldterms.shift();
			terms.push(oldterm);
		}
		if(oldterms.length >= 1 && oldterms[0] == "NOT") {
			oldterm = oldterms.shift();
			terms.push(oldterm);
		}

		// add the selected item
		terms.push( ui.item.value );
		// add placeholder to get the comma-and-space at the end
		terms.push( "" );
		this.value = terms.join( ", " );
		if($("#showingQuery").prop("checked")) {
			resolveVocabTermIds();
		}
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

	var showQuery = $( "#showingQuery" ).click(function() {
		if(this.checked) {
			resolveVocabTermIds();
			$("#queryTextDiv").show();
		} else {
			$("#queryTextDiv").hide();
		}
	});

	var resetButton = $("#resetButton").click(function() {
		$("#phenotypes").val("");
		resolveVocabTermIds();
	});

});

