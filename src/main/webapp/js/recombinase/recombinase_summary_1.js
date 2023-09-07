// first section of Javascript to be filled in the recombinase_summary.jsp
// (There are 3 sections because HTML markup goes in between them.)
// This file primarily contains functions related to the system checkboxes
// at the top of the page.

// define a namespace within the YAHOO object so we can keep variables that
// we can access from anywhere in the page.
YAHOO.namespace ('mgiData');
YAHOO.mgiData.selectedSystem = "";

//function flipColumn (fieldname) {
//	if (fieldname == "") { return; }
//	var checkboxID = getCheckboxID (fieldname);
//	var thisCheckBox = document.getElementById(checkboxID);
//	if (YAHOO.util.Dom.hasClass(thisCheckBox, "checkboxSelected")) {
//		hideColumn (fieldname);
//	} else {
//		showColumn (fieldname);
//	}
//}

function hide (i) {
    var elem = document.getElementById(i);
    if (elem == null) { return false; }

    elem.style.display = 'none';
    return true;
}	
function show (i)
{
    var elem = document.getElementById(i);
    if (elem == null) { return false; }

    elem.style.display = '';
    return true;
}
