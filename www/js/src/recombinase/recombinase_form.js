/*
 * Javascript wiring for the recombianse form
 */

var structureUrl = fewiurl+"autocomplete/structure?query=";
var driverUrl = fewiurl+"autocomplete/driver?query=";
var disableColor = "#CCC";

$(function() {

    // wire in the structure autocomplete
    var creStructureAC = $( "#creStructureAC" ).autocomplete({
	source: function( request, response ) {
		$.ajax({
			url: structureUrl+request.term,
			dataType: "json",
			success: function( data ) {
				response($.map(data["resultObjects"], function( item ) {
					return {label: item.synonym, hasCre: item.hasCre,
						isStrictSynonym: item.isStrictSynonym,
						original: item.structure};
				}));
			}
		});
	},
	minLength: 1
    }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
	var value = item.label;
	if (item.isStrictSynonym)
	{
		var synonymColor = item.hasCre ? "#222" : disableColor;
		value += "<span style=\"color:"+synonymColor+"; font-size:0.9em; font-style:normal;\">[<span style=\"font-size:0.8em;\">syn. of</span> "+item.original+"]</span> ";
	}
	if (item.hasCre)
	{
		return $('<li></li>')
			.data("item.autocomplete",item)
			.append("<a>" + value + "</a>")
			.appendTo(ul);
	}
	// adding the item this way makes it disabled
	return $('<li class="ui-menu-item disabled" style="color:#CCC;"></li>')
		.data("item.autocomplete", item)
		.append('<span>'+value+'</span>')
		.appendTo(ul);
    };


});

$(function() {

    // wire in the driver autocomplete
    var creDriverAC = $( "#creDriverAC" ).autocomplete({
	source: function( request, response ) {
		$.ajax({
			url: driverUrl+request.term,
			dataType: "json",
			success: function( data ) {
				response($.map(data["resultObjects"], function( item ) {
					//return {label: item.driverDisplay, driver: item.driver};
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

	var $creForm = $("#creForm");

	/* Clicking GO submits creForm */
	$creForm.find("goButton").click(function(e){
		$("#creForm").submit();
	});

	/* Changing input values detects if "AND" text should appear */
	$creForm.find("input").keyup(function(e){
		displayAndDivider();
	});

	/* if both structure and driver are filled in, then display AND */
	var displayAndDivider = function() {

		if ($creForm[0].structure.value
				&& $creForm[0].driver.value) {
			// show
			$creForm.find("#creAndDivider").css("opacity","1");
		}
		else {
			// hide
			$creForm.find("#creAndDivider").css("opacity","0");
		}
	};
	displayAndDivider();

})();