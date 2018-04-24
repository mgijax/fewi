/*
 * Javascript wiring for the strain form
 */

var strainNameUrl = fewiurl+"autocomplete/strainName?query=";

$(function() {
    // wire in the strain name autocomplete
    var strainNameAC = $( "#strainNameAC" ).autocomplete({
    	source: function( request, response ) {
    		$.ajax({
    			url: strainNameUrl + request.term,
    			dataType: "json",
    			success: function( data ) {
    				response(data['resultObjects']);
    			}
    		});
    	},
    	minLength: 1
    }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $('<li></li>')
			.data("item.autocomplete", item)
			.append("<a>" + item.label + "</a>")
			.appendTo(ul);
   	};
});


// wire the form submission
(function(){

	var $strainForm = $("#strainForm");

	/* Clicking GO submits strainForm */
	$strainForm.find("goButton").click(function(e){
		$("#strainForm").submit();
	});
})();