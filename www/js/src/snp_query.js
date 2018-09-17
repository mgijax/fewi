window.snpqry = {};		// namespace for this module

snpqry.form1Height = 618;
snpqry.form2Height = 680;

snpqry.qwrapOpenHeight = -1;	// set this later when collapsing
snpqry.qfDisplay = true;	// show the query form (true) or not (false)?

snpqry.setQueryFormDisplay = function(bool) {
    snpqry.qfDisplay = bool;
}

snpqry.setQueryFormHeight = function() {
	snpqry.qwrapOpenHeight = snpqry.form2Height;
}

snpqry.getQueryFormHeight = function() {
    return snpqry.qwrapOpenHeight;
}

snpqry.changeVisibility = function(id) {

    if (YAHOO.util.Dom.getStyle(id, 'display') == 'none'){
        YAHOO.util.Dom.setStyle(id, 'display', 'block');
    } else {
        YAHOO.util.Dom.setStyle(id, 'display', 'none');
    }
}

snpqry.hideQF = function(oCallback) {
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    
    snpqry.setQueryFormHeight();

    	attributes = { height: { to: 0  }};

	try {
    	    setText(toggleLink, "Click to modify search");
	} catch (err) {
	    if (toggleLink != null) {
		toggleLink.innerHTML = "Click to modify search";
	    }
	}

	if (toggleImg != null) {
    	    YAHOO.util.Dom.removeClass(toggleImg, 'qfCollapse');
    	    YAHOO.util.Dom.addClass(toggleImg, 'qfExpand');    	
	}
	snpqry.qfDisplay = false;
	snpqry.updateQFView(attributes, oCallback);
}

snpqry.showQF = function(oCallback) { 
    var toggleLink = YAHOO.util.Dom.get('toggleLink');
    var toggleImg = YAHOO.util.Dom.get('toggleImg');
    
    var qf = YAHOO.util.Dom.get('qwrap');
    	attributes = { height: { to: snpqry.getQueryFormHeight() }};

    	YAHOO.util.Dom.setStyle(qf, 'height', '0px');
    	YAHOO.util.Dom.setStyle(qf, 'display', 'none');
	if (toggleImg != null) {
    	    YAHOO.util.Dom.removeClass(toggleImg, 'qfExpand');
    	    YAHOO.util.Dom.addClass(toggleImg, 'qfCollapse');
	}

    	try {
	    setText(toggleLink, "Click to hide search");
	} catch (err) {
	    if (toggleLink != null) {
		toggleLink.innerHTML = "Click to hide search";
	    }
	}

    	snpqry.qfDisplay = true;
    	snpqry.changeVisibility('qwrap');
	snpqry.updateQFView(attributes, oCallback);
}

snpqry.updateQFView = function(attributes, oCallback) {
    var myAnim = new YAHOO.util.Anim('qwrap', attributes);
	
    if (!snpqry.qfDisplay){
	myAnim.onComplete.subscribe(function(){
		snpqry.changeVisibility('qwrap');
	});
    } else if (attributes.height.to != 0) {
	myAnim.onComplete.subscribe(function(){
		$("#qwrap").height("auto");
	});
    }
	
    if ((oCallback !== undefined) && !YAHOO.lang.isNull(oCallback)){	
	myAnim.onComplete.subscribe(oCallback);
    }
	
    myAnim.duration = 0.75;
    myAnim.animate();
}

snpqry.toggleQF = function(oCallback) {
    if (snpqry.qfDisplay){
	snpqry.hideQF(oCallback);
    } else {            
	snpqry.showQF(oCallback);
	try { $("#facetDialog_c").css({'visibility' : 'hidden'}); } catch (e) {}
    }
}

snpqry.toggleLink = YAHOO.util.Dom.get("toggleQF");
if (!YAHOO.lang.isUndefined(snpqry.toggleLink)){
	YAHOO.util.Event.addListener("toggleQF", "click", snpqry.toggleQF);
}
snpqry.toggleImg = YAHOO.util.Dom.get("toggleImg");
if (!YAHOO.lang.isUndefined(snpqry.toggleImg)){
	YAHOO.util.Event.addListener("toggleImg", "click", snpqry.toggleQF);
}

// get the ID ('form1' or 'form2') of the active form
snpqry.getActiveFormID = function() {
	// the 'aria-controls' attribute of the active tab will be like 'tabs-1' or 'tabs-2'
    try {
    	return 'form' + $(".ui-tabs-active").attr('aria-controls').split('-')[1];
    } catch (err) {
    	return 'form1';
    }
};

// hide the error rows (if visible)
snpqry.hideErrors = function() {
	$('#error1').removeClass("shown");
	$('#error1').addClass("hidden");
	$('#error2').removeClass("shown");
	$('#error2').addClass("hidden");
};

$.fn.scrollView = function () {
    return this.each(function () {
        $('html, body').animate({
            scrollTop: $(this).offset().top
        }, 1000);
    });
}

// show the error message on the current form, presenting the given message
snpqry.showError = function(msg) {
	var formNumber = snpqry.getActiveFormID().replace('form', '');
	$('#error' + formNumber).html(msg);
	$('#error' + formNumber).removeClass('hidden');
	$('#error' + formNumber).addClass('shown');
	
	// now scroll to the error message, if it's off the screen
	var err = document.getElementById('error' + formNumber);
	var errTop = err.getBoundingClientRect().top;
	var viewportHeight = window.innerHeight || document.documentElement.clientHeight;
	if ((errTop < 0) || (errTop > viewportHeight)) {
		$('#querytabs').scrollView();
	}
};

// Validate the query form.  If no errors, return true and hide error DIV.
// If errors, update and show the error DIV, then return false.
// show error DIV on the form
snpqry.validateQF = function(e) {
	var formID = snpqry.getActiveFormID();
	
	// 1. if on region tab, must specify chromosome and coordinates (or marker range)
	if (formID == 'form2') {
		var hasChrom = ($('#chromosomeDropList').val().length != 0);
		var hasCoord = ($('[name=coordinate]').val().trim().length != 0);
		var hasStartMarker = ($('[name=startMarker]').val().length != 0);
		var hasEndMarker = ($('[name=endMarker]').val().length != 0);
		
		if (!( (hasChrom && hasCoord) || (hasStartMarker && hasEndMarker)) ) {
			snpqry.showError('Your query is missing required parameters.  When searching by region, you must specify both Chromosome and Genome Coordinates or specify a Marker Range.');
			return false;
		}
		if ((hasChrom || hasCoord) && (hasStartMarker || hasEndMarker)) {
			snpqry.showError('You may search using Chromosome and Genome Coordinates or by Marker Range, but not both.');
			return false;
		}
	}
	
	// 2. if choose to use a reference strain, must specify at least one
	var expectRef = $('#' + formID + ' [name=referenceMode]:checked').val();
	var refStrains = $('#' + formID + ' [name=referenceStrains]:checked');
	if ((refStrains == undefined) && (refStrains == null)) {
		refStrains = [];
	}
	if ((refStrains.length == 0) && (expectRef == 'yes')) {
		snpqry.showError("Your query is missing a required parameter. If you choose to 'Compare to one or more Reference Strains', you must select at least one Reference Strain.");
		return false;
	}

	// 3. if on gene tab, must specify something in nomenclature field
	if (formID == 'form1') {
		if ($('#nomen').val().trim().length == 0) {
			snpqry.showError('Your query is missing a required parameter. When searching by gene, you must specify a value for Gene Symbol/Name.');
			return false;
		}
	}
	return true;
};

snpqry.interceptSubmit = function(e) {
	YAHOO.util.Event.preventDefault(e);	
	if (snpqry.validateQF(e)) {
		snpqry.toggleQF(function(){
			if ($('[name=allReferenceStrainsRequired]:checked').val() == 'no') {
				for (var x = 0; x < $('.filterItem').length; x++) {
					if ($('.filterItem')[x].id.startsWith('allele')) {
						$('.filterItem')[x].click();
						break;
					}
				}
			}
			var form = YAHOO.util.Dom.get(snpqry.getActiveFormID());
			form.submit();
		});
	}
};

YAHOO.util.Event.addListener("form1", "submit", snpqry.interceptSubmit);
YAHOO.util.Event.addListener("form2", "submit", snpqry.interceptSubmit);

snpqry.resetQF = function (e) {
	var errors = YAHOO.util.Dom.getElementsByClassName('qfError');		
	YAHOO.util.Dom.setStyle ( errors , 'display' , 'none' );

	YAHOO.util.Event.preventDefault(e); 

	snpqry.hideErrors();
	var form = YAHOO.util.Dom.get("form1");

	form.nomen.value = "";
	//form.rangeDropList.value = 2000;
	snpqry.refDeselectAll();
	form.searchGeneByList.value = "homologSymbols";
	snpqry.resetRadio();

	var form = YAHOO.util.Dom.get("form2");

	form.chromosomeDropList.value = "";
	form.coordinate.value = "";
	form.coordinateUnitDropList.value = "bp";
	snpqry.refDeselectAll();
	form.startMarker.value = "";
	form.endMarker.value = "";

	$('input[name=allowNullsForReferenceStrains][value=no]')[0].click();	// default: no nulls for reference strains
	$('input[name=allowNullsForReferenceStrains][value=no]')[1].click();	// default: no nulls for reference strains

	$('input[name=allowNullsForComparisonStrains][value=yes]')[0].click();	// default: nulls okay for comparison strains
	$('input[name=allowNullsForComparisonStrains][value=yes]')[1].click();	// default: nulls okay for comparison strains

	$('input[name=referenceMode][value=no]')[0].click();	// back to comparison-only mode (no reference)
	$('input[name=referenceMode][value=no]')[1].click();	// back to comparison-only mode (no reference)
	snpqry.selectAll();
};

snpqry.resetRadio = function() {
	$("input[name=withinRange]").each( function() { this.checked = (this.value == 2000) || (this.value == ""); });
};

snpqry.deselectAll = function() {
	$("input[name=selectedStrains]").each(function(){ this.checked = false; });
};

snpqry.refDeselectAll = function() {
	$("input[name=referenceStrains]").each(function(){ this.checked = false; });
};

snpqry.selectAll = function() {
	snpqry.refDeselectAll();
	$("input[name=selectedStrains]").each(function() { this.checked = true; });
};

snpqry.doccSelectAll = function() {
	snpqry.deselectAll();
	for (var i in doccFounders) {
		var strain = doccFounders[i];
		var box = $("input[name=selectedStrains][value='" + strain + "']").each(function() { this.checked = true; });
		var box = $("input[name=referenceStrains][value='" + strain + "']").each(function() { this.checked = false; });
	}
};

snpqry.mgpSelectAll = function() {
	snpqry.deselectAll();
	for (var i in mgpStrains) {
		var strain = mgpStrains[i];
		var box = $("input[name=selectedStrains][value='" + strain + "']").each(function() { this.checked = true; });
		var box = $("input[name=referenceStrains][value='" + strain + "']").each(function() { this.checked = false; });
	}
};

/* update the strain checkboxes on 'toForm' to match those on 'fromForm'
*/
snpqry.updateQF = function(fromForm, toForm) {
	// first copy the values from the strain-specific checkboxes
	
	var fromBoxes = $(fromForm + ' [name=selectedStrains]');
	var checkedState = {};	// maps from checkbox value to its checked state (true or false)
	for (var i in fromBoxes) {
		var box = fromBoxes[i];
		checkedState[box.value] = box.checked;
	}

	var toBoxes = $(toForm + ' [name=selectedStrains]');
	for (var i in toBoxes) {
		var box = toBoxes[i];
		if (box.value in checkedState) {
			box.checked = checkedState[box.value];
		}
	}
	
	// then copy the reference strain select

	var fromRefStrain = $(fromForm + ' [name=referenceStrain] option:selected').val()
	var toRefStrain = $(toForm + ' [name=referenceStrain] option[value="' + fromRefStrain + '"]');
	if (toRefStrain.length > 0) {
		toRefStrain[0].selected = true;
	}
};

/* update the strain checkboxes on QF1 (search by gene) to match those on QF2 (search by region) */
snpqry.updateQF1 = function() {
	snpqry.updateQF('#form2', '#form1');
};

/* update the strain checkboxes on QF2 (search by region) to match those on QF1 (search by gene) */
snpqry.updateQF2 = function() {
	snpqry.updateQF('#form1', '#form2');
};

YAHOO.util.Event.addListener("form1", "reset", snpqry.resetQF);
YAHOO.util.Event.addListener("form2", "reset", snpqry.resetQF);

YAHOO.util.Event.addListener($("#form1 #deselectButton"), "click", snpqry.deselectAll);
YAHOO.util.Event.addListener($("#form1 #selectButton"), "click", snpqry.selectAll);
YAHOO.util.Event.addListener($("#form2 #deselectButton"), "click", snpqry.deselectAll);
YAHOO.util.Event.addListener($("#form2 #selectButton"), "click", snpqry.selectAll);
YAHOO.util.Event.addListener($("#form1 #refDeselectButton"), "click", snpqry.refDeselectAll);
YAHOO.util.Event.addListener($("#form2 #refDeselectButton"), "click", snpqry.refDeselectAll);
YAHOO.util.Event.addListener($("#form1 #mgpSelectButton"), "click", snpqry.mgpSelectAll);
YAHOO.util.Event.addListener($("#form2 #mgpSelectButton"), "click", snpqry.mgpSelectAll);
YAHOO.util.Event.addListener($("#form1 #doccSelectButton"), "click", snpqry.doccSelectAll);
YAHOO.util.Event.addListener($("#form2 #doccSelectButton"), "click", snpqry.doccSelectAll);