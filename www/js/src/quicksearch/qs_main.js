/* Name: qs_main.js
 * Purpose: main logic for quicksearch JSP page and data retrieval
 * Notes: Functions here will be prefixed by "qs".
 */

// main logic for quick search
var qsMain = function() {
	b1Fetch();		// bucket 1 : markers + alleles bucket
	b2Fetch();		// bucket 2 : vocab terms + strains bucket
	b3Fetch();		// bucket 3 : ID bucket
};

// find a string beginning with the given string 'c' that doesn't appear in string 's'
var findTag = function(c, s) {
  	if ((s === null) || (s.indexOf(c) < 0)) { return c; }
  	return findTag(c + c[0], s);
};
  
// convert MGI superscript notation <...> to HTML superscript tags
var qsSuperscript = function(s) {
	if (s === null) { return s; }
    var openTag = findTag('{', s);
  	return s.split('<').join(openTag).split('>').join('</sup>').split(openTag).join('<sup>');
};
  
// return a string listing counts for search results
var qsResultHeader = function(start, end, total, dataType) {
	var plural = "";
	if ((end - start) > 1) {
		plural = "s";
	}
	return "Showing " + commaDelimit(start) + "-" + commaDelimit(end) + " of " + commaDelimit(total) + " " + dataType + plural;
};