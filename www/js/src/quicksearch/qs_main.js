/* Name: qs_main.js
 * Purpose: main logic for quicksearch JSP page and data retrieval
 * Notes: Functions here will be prefixed by "qs".
 */

// main logic for quick search
var qsMain = function() {
	b3Fetch();		// bucket 3 : ID bucket
	b2Fetch();		// bucket 2 : vocab terms + strains bucket
};

