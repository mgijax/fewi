/* File: hideshow.js
 * Purpose: To provide Javascript functions for manipulating MGI HTML pages
 *	dynamically.  This will initially be used for the allele detail page
 *	for the phenoview release, and will hide/show table rows as well as
 *	swapping images and such.
 * Assumes: mgi.js is also loaded for the current page, as we use its
 *	hitUrl() function.
 */

/* --- general purpose (not page-specific) -------------------------------- */

// set this to be the base URL of mgihome if you want to track user clicks
var mgihomeUrl = null;

// used to set the global mgihomeUrl to activate tracking of user clicks
function setMgihomeUrl (url)
{
    mgihomeUrl = url;
    return;
}

// toggle the visibility of an individual element identified by ID 'i'.
// returns true if it was toggled, or false if no element named 'i' was found
function toggle (i)
{
    var elem = document.getElementById(i);
    if (elem == null) { return false; }

    var status = (elem.style.display == '');

    if (status) { elem.style.display = 'none'; }
    else { elem.style.display = ''; }

    return true;
}

// force the individual element identified by ID 'i' to be hidden.
// returns true if it was hidden, or false if no element named 'i' was found
function hide (i)
{
    var elem = document.getElementById(i);
    if (elem == null) { return false; }

    elem.style.display = 'none';
    return true;
}

// force the individual element identified by ID 'i' to be displayed.
// returns true if it was shown, or false if no element named 'i' was found
function show (i)
{
    var elem = document.getElementById(i);
    if (elem == null) { return false; }

    elem.style.display = '';
    return true;
}

// for all elements which have IDs beginning with 'prefix', followed by
// ascending numbers starting at 1, toggle their display (shown and hidden).
// returns true if at least one element's display was toggled, false if not.
function toggleAll (prefix)
{
    var i = 0;
    var changed = true;
    var anyChanged = false;
    var id;

    while (changed)
    {
	i = i + 1;
	id = prefix + i;
	if (toggle(id) == true) { anyChanged = true; }
	else { changed = false; }
    }

    return anyChanged;
}

// for all elements which have IDs beginning with 'prefix', followed by
// ascending numbers starting at 1, make them visible.
// returns true if at least one element was shown, false if not.
function showAll (prefix)
{
    var i = 0;
    var changed = true;
    var anyChanged = false;
    var id;

    while (changed)
    {
	i = i + 1;
	id = prefix + i;
	if (show(id)) { anyChanged = true; }
	else { changed = false; }
    }

    return anyChanged;
}

// for all elements which have IDs beginning with 'prefix', followed by
// ascending numbers starting at 1, hide them.
// returns true if at least one element was hidden, false if not.
function hideAll (prefix)
{
    var i = 0;
    var changed = true;
    var anyChanged = false;
    var id;

    while (changed)
    {
	i = i + 1;
	id = prefix + i;
	if (hide(id)) { anyChanged = true; }
	else { changed = false; }
    }

    return anyChanged;
}

// for elements which have IDs beginning with 'prefix', followed by ascending
// numbers from 'm' to 'n', toggle their display (shown and hidden).
// returns true if at least one element was toggled, false if not.
function toggleMulti (prefix, m, n)
{
    var i;
    var anyChanged = false;
    var id;

    for (i = m; i <= n; i++)
    {
	id = prefix + i;
	if (toggle(id) == true) { anyChanged = true; }
    }

    return anyChanged;
}

// for elements which have IDs beginning with 'prefix', followed by ascending
// numbers from 'm' to 'n', show them.
// returns true if at least one element was shown, false if not.
function showMulti (prefix, m, n)
{
    var i;
    var anyChanged = false;
    var id;

    for (i = m; i <= n; i++)
    {
	id = prefix + i;
	if (show(id) == true) { anyChanged = true; }
    }

    return anyChanged;
}

// for elements which have IDs beginning with 'prefix', followed by ascending
// numbers from 'm' to 'n', hide them.
// returns true if at least one element was hidden, false if not.
function hideMulti (prefix, m, n)
{
    var i;
    var anyChanged = false;
    var id;

    for (i = m; i <= n; i++)
    {
	id = prefix + i;
	if (hide(id) == true) { anyChanged = true; }
    }

    return anyChanged;
}

// for the image identified by ID 'i', swap its visible image to whichever is
// not shown of 'image1' or 'image2'.  'image1' and 'image2' are URLs.
// returns true if it was swapped, or false if element 'i' was not found.
function swapImage (i, image1, image2)
{
    var elem = document.getElementById(i);
    if (elem == null) { return false; }

    var temp;
    if (image1.indexOf("..") == 0) { temp = image1.slice (2, image1.length); }
    else { temp = image1; }
    var image1shown = (elem.src.indexOf (temp) >= 0);
    if (image1shown) { elem.src = image2; }
    else { elem.src = image1; }

    return true;
}

// for the image identified by ID 'i', force its visible image to be 'image'.
// ('image' is a URL)
// returns true if it was set, or false if element 'i' was not found.
function setImage (i, image)
{
    var elem = document.getElementById(i);
    if (elem == null) { return false; }

    elem.src = image;
    return true;
}

/* --- specific to mutation description on allele detail page ------------- */

// toggle the table of sequence tags in the Mutation Description section of
// the allele detail page
function toggleSequenceTags ()
{
    toggle ("downArrowSeqTag");
    toggle ("rightArrowSeqTag");
    toggle ("seqTagTable");
    if (mgihomeUrl != null) {
        hitUrl (mgihomeUrl + "other/monitor.html", "toggleSequenceTags=1");
    }
}

// toggle the Gbrowse thumbnail section in the Mutation Description section of
// the allele detail page
function toggleGenomeContext ()
{
    toggle ("downArrowGenome");
    toggle ("rightArrowGenome");
    toggle ("genomeContextTable");
    if (mgihomeUrl != null) {
        hitUrl (mgihomeUrl + "other/monitor.html", "toggleGenomeContext=1");
    }
}

/* --- specific to phenotype summary grid --------------------------------- */

// toggles a phenotype header row open/closed, including showing or hiding the
// child rows and changing the button image.  (pheno grid)
// If global mgihomeUrl is non-null, we also do tracking of this event.
function togglePhenoRow (parentRow, startChildRow, endChildRow)
{
    hide ("subhead" + parentRow);
    toggle ("bandaid" + parentRow);
    toggleMulti ("childRow", startChildRow, endChildRow);
    toggle ("rightArrow" + parentRow);
    toggle ("downArrow" + parentRow);
    if (mgihomeUrl != null) {
        hitUrl (mgihomeUrl + "other/monitor.html", "togglePhenoRow=1");
    }
    return;
}

// show all child rows in the phenotype table, and set the icons on all
// parent rows.  (pheno grid)
// If global mgihomeUrl is non-null, we also do tracking of this event.
function showPheno()
{
    var i;
    showAll ("childRow");
    for (i = 1; i < 1000; i++)		// assume < 1000 parent rows
    {
	show ("bandaid" + i);
	show ("downArrow" + i);
	if (!hide ("rightArrow" + i))
	{
	    break;			// exit when past last parent row
	}
    }
    if (mgihomeUrl != null) {
        hitUrl (mgihomeUrl + "other/monitor.html", "showAllPheno=1");
    }
    return;
}

// hide all child rows in the phenotype table, and set the icons on all
// parent rows.  (pheno grid)
// If global mgihomeUrl is non-null, we also do tracking of this event.
function hidePheno()
{
    var i;
    hideAll ("childRow");
    for (i = 1; i < 1000; i++)		// assume < 1000 parent rows
    {
	hide ("subhead" + i);
	hide ("bandaid" + i);
	show ("rightArrow" + i);
	if (!hide ("downArrow" + i))
	{
	    break;			// exit when beyond max parent row
	}
    }
    if (mgihomeUrl != null) {
        hitUrl (mgihomeUrl + "other/monitor.html", "hideAllPheno=1");
    }
    return;
}

// toggle the row with the genotype column labels in the pheno grid.
// if global 'mgihomeUrl' is non-null, also track this event.
function toggleColumnLabels (rowLabel)
{
    toggle (rowLabel);
    if (mgihomeUrl != null) {
        hitUrl (mgihomeUrl + "other/monitor.html", "showPhenoLabels=1");
    }
    return;
}

/* --- specific to phenotype details -------------------------------------- */

// toggle an individual genotype section open/closed.  (pheno details section)
// If global mgihomeUrl is non-null, we also do tracking of this event.
function toggleGeno (i)
{
    toggle ("geno" + i + "-1");		// headings
    toggle ("geno" + i + "-3");		// details
    toggle ("genoRightArrow" + i);
    toggle ("genoDownArrow" + i);
    if (mgihomeUrl != null) { 
        hitUrl (mgihomeUrl + "other/monitor.html", "toggleGenoRow=1");
    }
    return;
}

// show an individual genotype section.  (pheno details section)
function showGeno (i)
{
    show ("geno" + i + "-1");		// headings
    show ("geno" + i + "-3");		// details
    hide ("genoRightArrow" + i);
    show ("genoDownArrow" + i);
    return;
}

// hide an individual genotype section.  (pheno details section)
function hideGeno (i)
{
    hide ("geno" + i + "-1");		// headings
    hide ("geno" + i + "-3");		// details
    hide ("genoDownArrow" + i);
    show ("genoRightArrow" + i);
    return;
}

// hide all child rows in the genotype table, and set the icons on all
// parent rows.  (pheno details section)
// If global mgihomeUrl is non-null, we also do tracking of this event.
function hideAllGeno()
{
    var i;
    for (i = 1; i < 1000; i++)		// assume < 1000 parent rows
    {
	// "-1" row is header which exists only for rows 2..
	// "-3" row always exists, so if it doesn't we can exit loop

	hide ("geno" + i + "-1");		// headings
	if (!hide ("geno" + i + "-3"))		// details
	{
	    break;
	}
	hide ("genoDownArrow" + i);
	show ("genoRightArrow" + i);
    }
    if (mgihomeUrl != null) {
        hitUrl (mgihomeUrl + "other/monitor.html", "hideAllGeno=1");
    }
    return;
}

// show all child rows in the genotype table, and set the icons on all
// parent rows.  (pheno details section)
// If global mgihomeUrl is non-null, we also do tracking of this event.
function showAllGeno()
{
    var i;
    for (i = 1; i < 1000; i++)		// assume < 1000 parent rows
    {
	// "-1" row is header which exists only for rows 2..
	// "-3" row always exists, so if it doesn't we can exit loop

	show ("geno" + i + "-1");		// headings
	if (!show ("geno" + i + "-3"))		// details
	{
	    break;
	}
	hide ("genoRightArrow" + i);
	show ("genoDownArrow" + i);
    }
    if (mgihomeUrl != null) {
        hitUrl (mgihomeUrl + "other/monitor.html", "showAllGeno=1");
    }
    return;
}

/* --- specific to genotype popup windows --------------------------------- */

var popupNextX = 0;	// x position of top-left corner of next popup
var popupNextY = 0;	// y position of top-left corner of next popup

// pop up a new window for displaying details from the given 'url' for the
// given genotype key.
function popupGenotype (url, genoKey)
{
    // new window will be named using the genotype key with a prefix
    var windowName;
    windowName = "genoPopup" + genoKey;

    // open the window small but scrollable and resizable
    var child = window.open (url, windowName,
	'width=500,height=300,resizable=yes,scrollbars=yes,alwaysRaised=yes');

    // move the new window and bring it to the front
    child.moveTo (popupNextX, popupNextY);
    child.focus();

    // set the position for the next new window (at position 400,400 we will
    // start over at 0,0)

    if (popupNextX >= 400) {
	popupNextX = 0;
	popupNextY = 0;
    }
    else {
	popupNextX = popupNextX + 20;
	popupNextY = popupNextY + 20;
    }
    return;
}

/* --- specific to mutation involves windows ------------------------------ */

// pop up a new window for displaying details from the given 'url' for the
// given genotype key.
function popupWide (url, uniqueKey)
{
    // new window will be named using the unique key with a prefix
    var windowName;
    windowName = "widePopup" + uniqueKey;

    // open the window small but scrollable and resizable
    var child = window.open (url, windowName,
	'width=1000,height=500,resizable=yes,scrollbars=yes,alwaysRaised=yes');

    // move the new window and bring it to the front
    child.moveTo (popupNextX, popupNextY);
    child.focus();

    // set the position for the next new window (at position 400,400 we will
    // start over at 0,0)

    if (popupNextX >= 400) {
	popupNextX = 0;
	popupNextY = 0;
    }
    else {
	popupNextX = popupNextX + 20;
	popupNextY = popupNextY + 20;
    }
    return;
}

/* --- specific to other popup windows ------------------------------------ */

// note that these are not the smaller, minimalistic genotype windows; these
// are larger, fully-functional windows.

var windowCounter = 0;		// counter of sub-windows of current window
var windowPrefix = "";		// prefix for sub-windows of current window

// used internally to initialize global 'windowPrefix'; should not be called
// outside this module
function setWindowPrefix()
{
    // array of characters we will use to compose our window prefix code
    chars = Array(26);
    chars[0] = 'a';  chars[1] = 'b';  chars[2] = 'c'; chars[3] = 'd';
    chars[4] = 'e';  chars[5] = 'f';  chars[6] = 'g'; chars[7] = 'h';
    chars[8] = 'i';  chars[9] = 'j';  chars[10] = 'k'; chars[11] = 'l';
    chars[12] = 'm';  chars[13] = 'n';  chars[14] = 'o'; chars[15] = 'p';
    chars[16] = 'q';  chars[17] = 'r';  chars[18] = 's'; chars[19] = 't';
    chars[20] = 'u';  chars[21] = 'v';  chars[22] = 'w'; chars[23] = 'x';
    chars[24] = 'y';  chars[25] = 'z';

    // compose the six-character window prefix for this window.  (Six
    // should be enough, since the odds of generating two identical ones are
    // less than 1 in 308,000,000.  All we need is to not have two the same
    // for whatever windows (which use this javascript module) the user has
    // opened since restarting his browser.  Otherwise, they start
    // over-writing any existing child windows from the window with the
    // matching prefix (via calls to newWindow()).  (not fatal, just annoying)

    windowPrefix = "";
    for (i = 0; i < 6; i++) {
	index = Math.round(Math.random() * 26);		// random int 0..25
	windowPrefix = windowPrefix + chars[index]
    }
    return;
}

// pop open a fully-functional window for the given 'url'.
function newWindow (url)
{
    // if this is the first new window popped open from this window, then
    // initialize the window prefix for this one
    if (windowPrefix == "") { setWindowPrefix(); }

    // new window will be named using the prefix and an ascending counter
    var windowName = windowPrefix + windowCounter;
    windowCounter = windowCounter + 1;

    // pop open the window and bring it to the front
    var child = window.open (url, windowName, 'width=800,height=600,status=yes,toolbar=yes,location=yes,menubar=yes,resizable=yes,scrollbars=yes,directories=yes');
    child.focus();
    return;
}
