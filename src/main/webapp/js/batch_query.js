var myTabs = new YAHOO.widget.TabView("batchSource");

var toggleQF = function(oCallback) {
	
    var qf = YAHOO.util.Dom.get('qwrap');
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    
    var attributes = { height: { to: 310 }};

    if (!qDisplay){
    	attributes = { height: { to: 0  }};
    	setText(toggleLink, "Click to modify search");
    	YAHOO.util.Dom.removeClass(toggleImg, 'qfCollapse');
    	YAHOO.util.Dom.addClass(toggleImg, 'qfExpand');    	
    	qDisplay = true;
    } else {            
    	YAHOO.util.Dom.setStyle(qf, 'height', '0px');
    	YAHOO.util.Dom.setStyle(qf, 'display', 'none');
    	YAHOO.util.Dom.removeClass(toggleImg, 'qfExpand');
    	YAHOO.util.Dom.addClass(toggleImg, 'qfCollapse');
    	setText(toggleLink, "Click to hide search");
    	qDisplay = false;
    	changeVisibility('qwrap');
    }
	var myAnim = new YAHOO.util.Anim('qwrap', attributes);
	
	if (qDisplay){
		myAnim.onComplete.subscribe(function(){
			changeVisibility('qwrap');
		});
	}
	
	if (!YAHOO.lang.isNull(oCallback)){	
		myAnim.onComplete.subscribe(oCallback);
	}
	myAnim.duration = 0.75;
	myAnim.animate();
};

var toggleLink = YAHOO.util.Dom.get("toggleQF");
if (!YAHOO.lang.isUndefined(toggleLink)){
	YAHOO.util.Event.addListener("toggleQF", "click", toggleQF);
}
var toggleImg = YAHOO.util.Dom.get("toggleImg");
if (!YAHOO.lang.isUndefined(toggleImg)){
	YAHOO.util.Event.addListener("toggleImg", "click", toggleQF);
}

var showQF = function() {
    /* show the query form (called upon certain page loads) */
	
    var qf = YAHOO.util.Dom.get('qwrap');
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    
    var attributes = { height: { to: 310 }};

    YAHOO.util.Dom.setStyle(qf, 'height', '0px');
    YAHOO.util.Dom.setStyle(qf, 'display', 'none');
    YAHOO.util.Dom.removeClass(toggleImg, 'qfExpand');
    YAHOO.util.Dom.addClass(toggleImg, 'qfCollapse');
    setText(toggleLink, "Click to hide search");
    qDisplay = false;
    changeVisibility('qwrap');

    var myAnim = new YAHOO.util.Anim('qwrap', attributes);
	
    myAnim.duration = 0.75;
    myAnim.animate();
};

var interceptSubmit = function(e) {
	YAHOO.util.Event.preventDefault(e);	
	var form = YAHOO.util.Dom.get("batchQueryForm");
	toggleQF(function(form){
		document.forms["batchQueryForm"].submit();
	});
};

YAHOO.util.Event.addListener("batchQueryForm", "submit", interceptSubmit);

var resetQF = function (e) {
	YAHOO.util.Event.preventDefault(e); 
	var form = document.forms["batchQueryForm"];
	form.idType.selectedIndex = 0;
	form.ids.value = "";
	
	form.attributes[0].checked = true ;
	for (i = 1; i < form.attributes.length; i++){
		form.attributes[i].checked = false ;
	}
	
	form.association9.checked="checked";
	
	form.fileType1.checked="checked";
	form.idColumn.value = "1";
	
	form.attributes.nomenclature.checked="checked";
	form.association9.checked = "checked";

};

YAHOO.util.Event.addListener("batchQueryForm", "reset", resetQF);

/* read a delimited file where one column contains marker-related IDs, parse it,
 * and update the 'ids' field in the batch QF.  Copied & modified from gxd_query.js.
 */
var readFile = function(e) {
	var input = e.target;
	var reader = new FileReader();

	reader.onload = function() {
		var separator = ',';
		var extractedIDs = '';

		if (document.querySelector('input[name = "fileType"]:checked').value == 'tab') {
			separator = '\t';
		}

		// switch from 1-based column number from input to 0-based
		var colNum = document.getElementById('idColumn').value - 1;
		var text = reader.result.trimRight();	// no trailing newline
		var lines = text.split(/[\n\r]+/g);
		var badLines = 0;		// count of bad lines
		var idsAdded = 0;		// count of IDs added

		for (var lineNum in lines) {
			var line = lines[lineNum];
			var cols = line.split(separator);

			if (cols.length > colNum) {
				var tokens = cols[colNum].split(' ');
				for (var tokenNum in tokens) {
					if (extractedIDs.length > 0) {
						extractedIDs = extractedIDs + '\n' + tokens[tokenNum];
					} else {
						extractedIDs = tokens[tokenNum];
					}
					idsAdded++;
				}
			} else {
				badLines++;
			}
		}

		// split lines then extract specified column using specified
		// delimiter

		var node = document.getElementById('ids');
		node.value = extractedIDs;
		
		var myMsg = '';

		if (idsAdded > 0) {
			if (idsAdded > 1) {
				myMsg = idsAdded + ' lines were added';
			} else {
				myMsg = idsAdded + ' line was added';
			}
		}

		if (badLines > 0) {
			if (myMsg.length > 0) { myMsg = myMsg + '; '; }
			myMsg = myMsg + badLines + ' line(s) had too few columns';
		}

		var msg = document.getElementById('uploadMessage');
		msg.innerHTML = myMsg;
		msg.style.display = 'inline-block';
		$('#enterTextButton').click();			// switch tabs to show the loaded IDs
	};
	reader.readAsText(input.files[0]);
};