// first section of Javascript to be filled in the recombinase_summary.jsp
// (There are 3 sections because HTML markup goes in between them.)
// This file primarily contains functions related to the system checkboxes
// at the top of the page.

// define a namespace within the YAHOO object so we can keep variables that
// we can access from anywhere in the page.
YAHOO.namespace ('mgiData');
YAHOO.mgiData.selectedSystem = "";

function getCheckboxID (fieldname) {
	var abbrev = fieldname.substr(0,3).toLowerCase();
	if (abbrev == "adi") { return "adiposeTissueCheckbox"; }
	else if (abbrev == "ali") { return "alimentarySystemCheckbox"; }
	else if (abbrev == "bra") { return "branchialArchesCheckbox"; }
	else if (abbrev == "car") { return "cardiovascularSystemCheckbox"; }
	else if (abbrev == "cav") { return "cavitiesAndLiningsCheckbox"; }
	else if (abbrev == "end") { return "endocrineSystemCheckbox"; }
	else if (abbrev == "hea") { return "headCheckbox"; }
	else if (abbrev == "hem") { return "hemolymphoidSystemCheckbox"; }
	else if (abbrev == "int") { return "integumentalSystemCheckbox"; }
	else if (abbrev == "lim") { return "limbsCheckbox"; }
	else if (abbrev == "liv") { return "liverAndBiliarySystemCheckbox"; }
	else if (abbrev == "mes") { return "mesenchymeCheckbox"; }
	else if (abbrev == "mus") { return "muscleCheckbox"; }
	else if (abbrev == "ner") { return "nervousSystemCheckbox"; }
	else if (abbrev == "ren") { return "renalAndUrinarySystemCheckbox"; }
	else if (abbrev == "rep") { return "reproductiveSystemCheckbox"; }
	else if (abbrev == "res") { return "respiratorySystemCheckbox"; }
	else if (abbrev == "sen") { return "sensoryOrgansCheckbox"; }
	else if (abbrev == "ske") { return "skeletalSystemCheckbox"; }
	else if (abbrev == "tai") { return "tailCheckbox"; }
	else if (abbrev == "ear") { return "earlyEmbryoCheckbox"; }
	else if (abbrev == "ext") { return "extraEmbryonicCheckbox"; }
	else if (abbrev == "emb") { return "embryoOtherCheckbox"; }
	else if (abbrev == "pos") { return "postnatalOtherCheckbox"; }
	else if (abbrev == "all") {
		if (fieldname == "Allele Synonyms") { return "synonymsCheckbox"; }
		else if (fieldname == "Allele Type") { return "alleleTypeCheckbox"; }
	}
	else if (abbrev == "ind") { return "inducibleCheckbox"; }
	else if (abbrev == "ims") { return "imsrCheckbox"; }
	else if (abbrev == "ref") { return "referenceCheckbox"; }
	return "";
}
function getColumnName (fieldname) {
	var abbrev = fieldname.substr(0,3).toLowerCase();
	if (abbrev == "adi") { return "inAdiposeTissue"; }
	else if (abbrev == "ali") { return "inAlimentarySystem"; }
	else if (abbrev == "bra") { return "inBranchialArches"; }
	else if (abbrev == "car") { return "inCardiovascularSystem"; }
	else if (abbrev == "cav") { return "inCavitiesAndLinings"; }
	else if (abbrev == "end") { return "inEndocrineSystem"; }
	else if (abbrev == "hea") { return "inHead"; }
	else if (abbrev == "hem") { return "inHemolymphoidSystem"; }
	else if (abbrev == "int") { return "inIntegumentalSystem"; }
	else if (abbrev == "lim") { return "inLimbs"; }
	else if (abbrev == "liv") { return "inLiverAndBiliarySystem"; }
	else if (abbrev == "mes") { return "inMesenchyme"; }
	else if (abbrev == "mus") { return "inMuscle"; }
	else if (abbrev == "ner") { return "inNervousSystem"; }
	else if (abbrev == "ren") { return "inRenalAndUrinarySystem"; }
	else if (abbrev == "rep") { return "inReproductiveSystem"; }
	else if (abbrev == "res") { return "inRespiratorySystem"; }
	else if (abbrev == "sen") { return "inSensoryOrgans"; }
	else if (abbrev == "ske") { return "inSkeletalSystem"; }
	else if (abbrev == "tai") { return "inTail"; }
	else if (abbrev == "ear") { return "inEarlyEmbryo"; }
	else if (abbrev == "ext") { return "inExtraembryonicComponent"; }
	else if (abbrev == "emb") { return "inEmbryoOther"; }
	else if (abbrev == "pos") { return "inPostnatalOther"; }
	else if (abbrev == "all") {
		if (fieldname == "Allele Synonyms") { return "synonyms"; }
		else if (fieldname == "Allele Type") { return "alleleType"; }
	}
	else if (abbrev == "ind") { return "inducibleNote"; }
	else if (abbrev == "ims") { return "imsrCount"; }
	else if (abbrev == "ref") { return "countOfReferences"; }
	return "";
}
function flipColumn (fieldname) {
	if (fieldname == "") { return; }
	var checkboxID = getCheckboxID (fieldname);
	var thisCheckBox = document.getElementById(checkboxID);
	if (YAHOO.util.Dom.hasClass(thisCheckBox, "checkboxSelected")) {
		hideColumn (fieldname);
	} else {
		showColumn (fieldname);
	}
}
function hideColumn (fieldname) {
	if (fieldname == "") { return; }
	var checkboxID = getCheckboxID (fieldname);
	var thisCheckBox = document.getElementById(checkboxID);
	var columnName = getColumnName (fieldname);
	var myDataTable = YAHOO.mgiData.myDataTable;
		myDataTable.hideColumn (columnName);
		YAHOO.util.Dom.removeClass(thisCheckBox, "checkboxSelected");
		thisCheckBox.checked = false;
}
function showColumn (fieldname) {
	if ((fieldname == "") || (fieldname == null)) { return; }
	var checkboxID = getCheckboxID (fieldname);
	var thisCheckBox = document.getElementById(checkboxID);
	var columnName = getColumnName (fieldname);
	var myDataTable = YAHOO.mgiData.myDataTable;
		myDataTable.showColumn (columnName);
		YAHOO.util.Dom.addClass(thisCheckBox, "checkboxSelected");
		thisCheckBox.checked = true;
}
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
