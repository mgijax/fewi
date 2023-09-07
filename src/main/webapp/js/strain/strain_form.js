/*
 * Javascript wiring for the strain form
 */

var strainNameUrl = fewiurl+"autocomplete/strainName?query=";

var convertAngleBrackets = function(s) {
	return s.replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

jQuery(function() {
    // wire in the strain name autocomplete
    var strainNameAC = jQuery( "#strainNameAC" ).autocomplete({
    	source: function( request, response ) {
    		jQuery.ajax({
    			url: strainNameUrl + request.term,
    			dataType: "json",
    			success: function( data ) {
    				response(data['resultObjects']);
    			}
    		});
    	},
    	minLength: 1
    }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return jQuery('<li></li>')
			.data("item.autocomplete", item)
			.append("<a>" + convertAngleBrackets(item.label) + "</a>")
			.appendTo(ul);
   	};
});


// wire the form submission
(function(){

	var strainForm = jQuery("#strainForm");

	/* Clicking GO submits strainForm */
	strainForm.find("goButton").click(function(e){
		jQuery("#strainForm").submit();
	});
})();