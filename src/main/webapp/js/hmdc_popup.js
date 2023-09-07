/* javascript supporting the HMDC grid cell popup, aka marker detail MP slimgrid popup */

var imsrUrl = '';

  /* must be called before showDialog() is called; lets the module know the URL for IMSR */
  var setImsrUrl = function(s) {
  	imsrUrl = s;
  };
  
  // find a string beginning with the given string 'c' that doesn't appear in string 's'
  var findTag = function(c, s) {
  	if (s.indexOf(c) < 0) { return c; }
  	return findTag(c + c[0], s);
  };
  
  // convert MGI superscript notation <...> to HTML superscript tags
  var superscript = function(s) {
    var openTag = findTag('{', s);
  	return s.split('<').join(openTag).split('>').join('</sup>').split(openTag).join('<sup>');
  };
  
  // turn the given accID and optional parameters into a link to IMSR
  var makeImsrLink = function(accID, imsrCount, optionalParms) {
  	var s = '(';
  	if (imsrCount == 0) {
  		s = s + imsrCount + " available)";
  	} else {
  		s = s + "<a target='_blank' class='findMice' href='" + imsrUrl + "summary?"
			+ optionalParms + "&gaccid=" + accID + "'>" + imsrCount + " available)</a>";
  	}
  	return s;
  };
  
  // format the turn allele ID and IMSR count into a link to get corresponding data from IMSR
  var alleleImsrLink = function(accID, imsrCount) {
  	return makeImsrLink(accID, imsrCount, 'states=archived&states=embryo&states=live&states=ovaries&states=sperm');
  };

  // format the turn marker ID and IMSR count into a link to get corresponding data from IMSR
  var markerImsrLink = function(accID, imsrCount) {
  	return makeImsrLink(accID, imsrCount, '');
  };
  
  // show the popup with IMSR info when the user clicks a Find Mice button
  var showDialog = function(event, genoclusterKey) {
	event.cancelBubble=true;
	var alleleCellID = "fm" + genoclusterKey + "a";
	$("#dialog").dialog();
	$(".ui-dialog").position({my:"left top", at:"left top", of:$("#" + alleleCellID), collision:"fit"});
	var gcKey = '' + genoclusterKey;
	var msg = "unknown genocluster key: " + genoclusterKey;

	if (gcKey in genoclusters) {
		var allZero = true;
		
		var header = "Using the International Mouse Strain Resource "
			+ "(<a target='_blank' href='" + imsrUrl + "'>IMSR</a>)<br/>";
		var msg = "Mouse lines carrying:<br/>";
		
		var gc = genoclusters[gcKey];
		for (var i = 0; i < gc.length; i++) {
			var g = gc[i];
			msg = msg + superscript(g[1]) + " mutation " + alleleImsrLink(g[0], g[2]);
			
			// if transgene, only show the allele part and omit the redundant marker part
			if (g[1] != g[4]) {
				msg = msg + "; any " + superscript(g[4]) + " mutation " + markerImsrLink(g[3], g[5]);
			}
			msg = msg + "<br/>";

			if ((g[2] > 0) || (g[5] > 0)) {
				allZero = false;
			}
		}
		
		// special message if all alleles and markers have no data in IMSR
		if (allZero) {
			msg = "No mouse lines available in IMSR.<br/>"
				+ "Click a genotype row to see annotation details and<br/>view publication links for author information.";
		}
	}
	// format the dialog with the new message and resize the popup after a 50ms wait (to let the formatting finish).
	// if the box would overflow the window, then allow wrapping.
	$("#dialog").hide().html("<div style='font-size: 90%'>" + msg + "</div>").fadeIn('fast').width('auto');
	setTimeout(function() {
		$(".ui-dialog").width('auto');
		if ($(".ui-dialog").width() > $(window).width()) {
			$(".ui-dialog").css('white-space', 'normal');
		}
	}, 50);
  };
