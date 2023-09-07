/* Name: qs_bucket5.js
 * Purpose: data retrieval and processing logic for the quicksearch's second data bucket (alleles)
 * Notes: Functions here will be prefixed by "b5".
 */

// Globals

var b5Failed = false;		// did retrieval for this bucket fail?
var b5PageSize = 100;
var b5CacheName = 'alleleCache';

// Having received 'data' from the server, show it on the page.
function b5Show(data) {
	var tbl = '';
	var toShow = b5PageSize;
	if (data.rows.length > 0) {
		tbl = '<TABLE ID="b5Table">';
		tbl = tbl + '<TR><TH>Score' + qsScoreHelp() + '</TH><TH>Type</TH><TH>Symbol</TH><TH>Name</TH><TH>Chr</TH><TH>Location (Genome Build)</TH><TH>Str</TH><TH>Best Match</TH></TR>';

		toShow = Math.min(100, data.rows.length);
		for (var i = 0; i < toShow; i++) {
			var item = data.rows[i];
			tbl = tbl + '<TR><TD>' + qsFormatStars(item.stars) + '</TD>';
			tbl = tbl + '<TD class="small">' + item.featureType + '</TD>';

			var symbol = qsSuperscript(item.symbol);
			var name = qsSuperscript(item.name);
			var bestMatchText = qsSuperscript(item.bestMatchText);
			
			if (item.detailUrl === null) {
				tbl = tbl + '<TD>' + symbol + '</TD>';
			} else {
				tbl = tbl + '<TD><a target="_blank" href="' + item.detailUri + '">' + symbol + '</a></TD>';
			}
			tbl = tbl + '<TD class="nameCol">' + name + '</TD>';

			if (item.chromosome === null) {
				tbl = tbl + '<TD class="small">&nbsp;</TD>';
			} else {
				tbl = tbl + '<TD class="small">' + item.chromosome + '</TD>';
			}

			if (item.location === null) {
				tbl = tbl + '<TD>&nbsp;</TD>';
			} else if (item.location.indexOf('-') < 0){
				tbl = tbl + '<TD class="nowrap small">' + item.location + '</TD>';
			} else {
				tbl = tbl + '<TD class="nowrap small">' + item.location + ' (' + genomeBuild + ')</TD>';
			}

			if (item.strand === null) {
				tbl = tbl + '<TD>&nbsp;</TD>';
			} else {
				tbl = tbl + '<TD class="small">' + item.strand + '</TD>';
			}
			if (item.bestMatchType === null) {
				tbl = tbl + '<TD>&nbsp;</TD></TR>';
			} else {
				tbl = tbl + '<TD><span class="termType">' + item.bestMatchType + '</span><span class="small">: ' + bestMatchText + '</span></TD></TR>';
			}
		}
	} else {
		console.log("No b5Results");
	}
	var header = qsResultHeader(data.start, data.end, data.totalCount);
	var firstTime = false;							// first time through for the current search?
	if ($('#aCount').html().indexOf('(') < 0) {		// if no count on the Alleles tab yet, then yes.
		firstTime = true;
	}
	$('#aCount').html("(" + commaDelimit(data.totalCount) + ")");
	$('#b5Counts').html(header);
	$('#b5Results').html(tbl);
	pgUpdatePaginator(b5CacheName, 'allelePaginator', data.totalCount, b5PageSize, dcGetPage);
	if (firstTime) {
		qsStyleTabText(data.totalCount, 2);
	}
	console.log("Populated " + data.rows.length + " b5Results");
};

// Fetch the data items for bucket 5 (matches by allele)
var b5Fetch = function() {
		qsShowSpinner('#b5Results');
		var url = fewiurl + '/quicksearch/alleleBucket?' + getQuerystring();
		dcStartCache(b5CacheName, url, b5Show, b5PageSize, '#b5Results');
		dcGetPage(b5CacheName, 1);
};

// process the download option choice for the Alleles tab
var b5Download = function() {
	qsDownload('aDownloads');
};

// process the forward option choice for the Alleles tab
var b5Forward = function() {
	qsForward('aForwards');
};