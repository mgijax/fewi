/* Name: gxdht_query.js
 * Purpose: supports the high-throughput expression query form
 */
 
/*** logging support for debugging ***/

var logging = true;	// is logging to the console enabled? (true/false)

function log(msg) {
    // log a message to the browser console, if logging is enabled

    if (logging) {
	    try {
    		console.log(msg);
    	} catch (c) {
    	    setTimeout(function() { throw new Error(msg); }, 0);
    	}
   	}
}

/*** module-level variables ***/

var gq_qfVisible = false;		// is the form visible? (true = yes, false = no)
var gq_disableColor = "#CCC";	// color to use for disabled structures

//GXD tooltips for Theiler Stage
var gq_tsTooltips = {
		1:"One cell stage",
		2:"Beginning of cell division; 2-4 cells",
		3:"Morula; 4-16 cells",
		4:"Blastocyst (inner cell mass apparent); 16-40 cells",
		5:"Blastocyst (zona free)",
		6:"Implantation",
		7:"Formation of egg cylinder",
		8:"Differentiation of egg cylinder",
		9:"Prestreak; early streak",
		10:"Midstreak; late streak; allantoic bud first appears; amnion forms",
		11:"Neural plate stage; elongated allantoic bud; early headfold; late headfold",
		12:"1-7 somites",
		13:"8-12 somites; turning of embryo",
		14:"13-20 somites; formation and closure of anterior neuropore",
		15:"21-29 somites; formation of posterior neuropore and forelimb bud",
		16:"30-34 somites; closure of posterior neuropore; formation of hindlimb and tail bud",
		17:"35-39 somites; deep indentation of lens vesicle",
		18:"40-44 somites; closure of lens vesicle",
		19:"45-47 somites; complete separation of lens vesicle",
		20:"48-51 somites; earliest sign of handplate digits",
		21:"52-55 somites; indentation of handplate",
		22:"56-~60 somites; distal separation of handplate digits",
		23:"Separation of footplate digits",
		24:"Reposition of umbilical hernia",
		25:"Digits joined together; skin wrinkled",
		26:"Long whiskers",
		27:"Newborn mouse",
		28:"Postnatal development"
		};

/*** functions ***/

// reset the fields on the query form
var gq_reset = function(e) {
	e.preventDefault();

	// structure ribbon
	$('input:text[name=structure]').val('');
	
	// age/stage ribbon
	document.getElementById('theilerStage').selectedIndex = 0
	document.getElementById('ageUnit').selectedIndex = 0
	document.getElementById('ageRange').value = ""
	document.getElementById('ageRange').disabled = false
	selectAge();
	
	// Sex ribbon
	$('input:radio[name=sex]').prop('checked', false);
	$('input:radio[name=sex][value=""]').prop('checked', true);
	
	// Mutant ribbon
	$('#mutatedIn').val('');
	
	// Method ribbon
	$('input:checkbox[name=method]').prop('checked', true);
	
	// Text ribbon
	$('input:text[name=text]').val('');
	$('input:checkbox[name=textScope]').prop('checked', true);
	
	// ID ribbon
	$('input:text[name=arrayExpressID]').val('');
	
	// strain ribbon
	$('input:text[name=strain]').val('');
};

(function () {
	$('#ageUnit').on('change', e=> {
	    var sel = e.target.value;
	    if (['P','E','A','N'].indexOf(sel) >= 0){
		$('#ageRange')[0].value = ''
		$('#ageRange')[0].disabled = true
	    }
	    else if (sel === "ANY") {
	        $('#ageRange')[0].value = ''
		$('#ageRange')[0].disabled = false
	    }
	    else {
		$('#ageRange')[0].disabled = false
	    }
	});
})();

// Initialize hierarchical checkbox behavior
// The hierarchy of checkboxes is encoded in their id attributes.
// All the accessor functions defined below are based on this encoding.
// The root has id "mcb_1". Its children have ids "mcb_1_1", "mcb_1_2", etc.
// The children of "mcb_1_2" have ids "mcb_1_2_1" "mcb_1_2_2", etc.
(function () {
    function getParentId (tgtId) {
        const parts = tgtId.split("_")
	if (parts.length > 2) {
	    parts.pop()
	    return parts.join("_")
	}
    }
    function getParent (tgtId) {
	const pid = getParentId(tgtId)
	if (pid) return $('#' + pid);
    }

    function getDescendants (tgtId) {
        const matchString = `input[type="checkbox"][id^="${tgtId}_"]`
	const desc = $(matchString)
	return desc
    }
    function getAncestors (tgtId) {
	const anc = []
	var pid = getParentId (tgtId)
	while (pid) {
	    anc.push(document.getElementById(pid))
	    pid = getParentId (pid)
	}
	return $(anc)
    }
    function getChildren (tgtId) {
	const kids = []
	var n = 1
	while (true) {
	    const chId = tgtId + "_" + n
	    const child = document.getElementById(chId)
	    if (!child) break
	    kids.push(child)
	    n += 1
	}
	return $(kids)
    }
    function getSiblings (tgtId) {
	const pid = getParentId (tgtId)
	if (pid) {
	    return getChildren(pid)
	}
	return $([])
    }

    // if all siblings are checked, then check the parent and recurse up
    function checkSiblings (tgtId) {
	const sibs = getSiblings(tgtId)
	var allChecked = true;
	sibs.each((i,s) => {allChecked = allChecked && s.checked})
	const p = getParent(tgtId)
	if (allChecked && p) {
	    // check the parent and recurse up the tree
	    p.prop('checked', true)
	    checkSiblings(p.attr('id'))
	}
    }

    $('#methodCheckboxes').on('change', e => {
	const tgt = e.target
	const tgtId = tgt.getAttribute('id')
	const tgtChecked = tgt.checked
	if (tgtChecked) {
	    // check all descendants
	    getDescendants(tgtId).prop('checked', true)
	    // possibly check ancestors
	    checkSiblings(tgtId)

	} else {
	    // uncheck all descendants
	    getDescendants(tgtId).prop('checked', false)
	    // uncheck all ancestors
	    getAncestors(tgtId).prop('checked', false)
	}
    });

    $('#methodCheckboxes input[type="checkbox"]').each((i, cb) => {
	cb = $(cb)
        const depth = cb.attr('id').split('_').length - 1;
	const indent = 16 * depth ;
	cb.css('margin-left', indent + 'px')
    })

    function parseParams () {
	    var srch = document.location.search.slice(1);
	    if (srch === '')
	    	return {};
	    const pieces = srch.split('&');
	    const pmap = pieces.reduce((a,v) => {
		const vpieces = v.split('=')
		const n = vpieces[0]
		const vv = vpieces[1].replaceAll('+', ' ')
		if (a[n] === undefined) {
		    a[n] = [ vv ]
		} else {
		    a[n].push(vv)
		}
		return a
	    }, {});
	    return pmap;
    }

    function resetFromParams () {
	    const pmap = parseParams()
	    const methodCbs = $('input:checkbox[name=method]');
	    methodCbs.each((i, cb) => {
		cb.checked =  pmap['method'] ? (pmap['method'].indexOf(cb.value) >= 0) : true;
	    })
    }

    resetFromParams();

})();

// initialize the autocompletes
$(function() {
// wire in the structure autocomplete (liberally copied from recombinase_form.js)
    var structureAC = $( "#structureAC" ).autocomplete({
	source: function( request, response ) {
		$.ajax({
			url: fewiUrl + "autocomplete/structure?query=" + request.term,
			dataType: "json",
			success: function( data ) {
				response($.map(data["resultObjects"], function( item ) {
					return {accID:item.accID, label: item.synonym, hasGxdHT: item.hasGxdHT,
						isStrictSynonym: item.isStrictSynonym,
						original: item.structure};
				}));
			}
		});
	},
	select: function (evt, ui) {
	    $( "#structureID" )[0].value = ui.item.accID
	},
	minLength: 1
    }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
	var value = item.label;
	if (item.isStrictSynonym)
	{
		var synonymColor = item.hasCre ? "#222" : gq_disableColor;
		value += "<span style=\"color:"+synonymColor+"; font-size:0.9em; font-style:normal;\">[<span style=\"font-size:0.8em;\">syn. of</span> "+item.original+"]</span> ";
	}
	if (item.hasGxdHT)
	{
		return $('<li></li>')
			.data("item.autocomplete",item)
			.append("<a>" + value + "</a>")
			.appendTo(ul);
	}
	// adding the item this way makes it disabled
	return $('<li class="ui-menu-item disabled" style="color:#999;"></li>')
		.data("item.autocomplete", item)
		.append('<span>'+value+'</span>')
		.appendTo(ul);
    };
    
    // wire in the strain name autocomplete
    var strainNameUrl = fewiUrl + "autocomplete/strainName?tag=GXDHT&query=";
    var strainNameAC = $( "#strainNameAC" ).autocomplete({
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
			.append("<a>" + item.label + "</a>")
			.appendTo(ul);
   	};
});

var showQF = function(callback) {
	$('#standard-qf').slideDown('slow', callback);
	$('#toggleLink').html('Click to hide search');
	$('#toggleImg').removeClass('qfExpand').addClass('qfCollapse');
};

var hideQF = function(callback) {
	$('#standard-qf').slideUp('slow', callback);
	$('#toggleLink').html('Click to modify search');
	$('#toggleImg').removeClass('qfCollapse').addClass('qfExpand');
};

var toggleQueryForm = function(callback) {
	if ($('#standard-qf').css('display') == 'none') {
		showQF();
	} else {
		hideQF();
	}
};

// general purpose function for changing tabs
function changeTab(tabElement,parentId)
{
    var eSelector = '#'+parentId;
     // remove the active-tab and place it on current object;
    $(eSelector+' .active-tab').removeClass("active-tab").
		addClass("inactive-tab");
    $(tabElement).removeClass("inactive-tab")
		.addClass("active-tab");

    // remove active content
    $(eSelector+' .active-content').removeClass("active-content")
        .addClass("inactive-content");

    // use tab index to find matching content and set it to active
    var tab_index = $(tabElement).index();
    $(eSelector+' .inactive-content').eq(tab_index).removeClass("inactive-content")
        .addClass("active-content");
}
//Script to set up and control the ageStage tab widget (using jquery)
var ageStageID = "ageStage";
function selectTheilerStage()
{ changeTab($('#'+ageStageID+' .tab-nav')[1],ageStageID); }
function selectAge()
{ changeTab($('#'+ageStageID+' .tab-nav')[0],ageStageID); }
function ageStageChange(e)
{ if(!$(this).hasClass("active-tab")) changeTab(this,ageStageID); }
// Init the event listener for clicking tabs
$('#'+ageStageID+' .tab-nav').click(ageStageChange);

function logSubmission() {
	ga_logEvent('GXD RNA-Seq and Microarray Experiment Search', 'QF Submitted');
}
log("loaded gxdht_query.js");
