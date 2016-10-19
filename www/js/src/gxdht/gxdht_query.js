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

	// Sex ribbon
	$('input:radio[name=sex]').prop('checked', false);
	$('input:radio[name=sex][value=""]').prop('checked', true);
	
	// Mutant ribbon
	$('#mutatedIn').val('');
	
	// Method ribbon
	$('input:radio[name=method]').prop('checked', false);
	$('input:radio[name=method][value=""]').prop('checked', true);
	
	// Text ribbon
	$('input:text[name=text]').val('');
	$('input:checkbox[name=textScope]').prop('checked', true);
};

log("loaded gxdht_query.js");