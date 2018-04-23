/*
 * Javascript wiring for the strain form
 */

var strainNameUrl = fewiurl+"autocomplete/strainName?query=";
var disableColor = "#CCC";

$(function() {
    // wire in the strain name autocomplete
    var strainNameAC = $( "#strainNameAC" ).autocomplete({
    	source: function( request, response ) {
    		$.ajax({
    			url: strainNameUrl + request.term,
    			dataType: "json",
    			success: function( data ) {
    				response($.map(data["resultObjects"], function( item ) {
    					return {label: item.driverDisplay, value: item.driver};
    				}));
    			}
    		});
    	},
    	minLength: 1
    }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		var value = item.label;
		return $('<li></li>')
			.data("item.autocomplete", item)
			.append("<a>" + value + "</a>")
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