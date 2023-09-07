// updates the 'searched-for' section of the page
var updateQuerySummary = function() {
	var searchSummary = new YAHOO.util.Element('searchSummary');

	// Remove all the existing summary items
	searchSummary.innerHTML = "";
	document.getElementById('searchSummary').innerHTML = '';

	// if we searched by state, add to search summary
	if (searchedStage != "") {

       // Create a container span
		var el = new YAHOO.util.Element(document.createElement('span'));

		//add the text node to the newly created span
		var b = new YAHOO.util.Element(document.createElement('b'));
		var newContent = document.createTextNode("Data Summary: ");
		b.appendChild(newContent);
		el.appendChild(b);
		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendChild(document.createTextNode("at Developmental stage: "));
		var b2 = new YAHOO.util.Element(document.createElement('b'));
		var newContent2 = document.createTextNode("(TS:" + searchedStage + ")");
		b2.appendChild(newContent2);
		el.appendChild(b2);
		el.appendTo(searchSummary);
	}

	// if we searched by state, add to search summary
	if (searchedAssayType != "") {

       // Create a container span
		var el = new YAHOO.util.Element(document.createElement('span'));

		//add the text node to the newly created span
		var b = new YAHOO.util.Element(document.createElement('b'));
		var newContent = document.createTextNode("Data Summary: ");
		b.appendChild(newContent);
		el.appendChild(b);
		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendChild(document.createTextNode("Assayed by "));
		var b2 = new YAHOO.util.Element(document.createElement('b'));
		var newContent2 = document.createTextNode("(" + searchedAssayType + ")");
		b2.appendChild(newContent2);
		el.appendChild(b2);
		el.appendTo(searchSummary);
	}

	// if we searched by structure, add to search summary
	if ((typeof searchedStruture != 'undefined') && (searchedStructure != "")) {

       // Create a container span
		var el = new YAHOO.util.Element(document.createElement('span'));

		//add the text node to the newly created span
		var b = new YAHOO.util.Element(document.createElement('b'));
		var newContent = document.createTextNode("Data Summary: ");
		b.appendChild(newContent);
		el.appendChild(b);
		el.appendChild(new YAHOO.util.Element(document.createElement('br')));
		el.appendChild(document.createTextNode("Expression results in "));
		var b2 = new YAHOO.util.Element(document.createElement('b'));
		var newContent2 = document.createTextNode(searchedStructure);
		b2.appendChild(newContent2);
		el.appendChild(b2);
		el.appendTo(searchSummary);
	}
}; 
