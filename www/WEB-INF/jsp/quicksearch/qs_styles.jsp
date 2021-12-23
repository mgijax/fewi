<style>
.helpCursor { cursor: help; }
.qsHeader { background-color: #F0F8FF; color: #002255; margin-top: 0.75em; 
	font-size: 18px; font-weight: bold; line-height: 1.25; vertical-align: top;
	padding-left: 5px; padding-right: 5px; padding-top: 2px; padding-bottom: 2px; 
	clear: both;
	}
.resultCount { font-size: 10px; font-weight: normal; color: #676767; }
.hidden { display: none; }
.shown { display: inline; }

.qsButton {
    font-size: 12px;
    font-family: Verdana,Arial,Helvetica;
    color: #002255;
    font-weight: bolder;
    background-color: #eeeeee;
    border-width: 1px;
    border-style: solid;
    border-color: #7d95b9;
    padding: 2px;
    display: inline;
    text-decoration: none;
    cursor: hand;
}

#filterSummary { margin-top: 5px; margin-bottom: 5px; }

#b1Results { max-height: 500px; overflow-y: auto; width: 100%; }
#b2Results { max-height: 500px; overflow-y: auto; width: 100%; }
#b3Results { max-height: 500px; overflow-y: auto; width: 100%; }
#b4Results { max-height: 500px; overflow-y: auto; width: 100%; }
#b5Results { max-height: 500px; overflow-y: auto; width: 100%; }

#b1Table { border-collapse: collapse; width: 100% }
#b1Table th { font-weight: bold; padding: 3px; }
#b1Table td { padding: 3px; }
#b1Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b1Table tr:first-child { background-color: #dfefff; position: sticky; top:0px; z-index:1; }
#b1Table a { text-decoration: none; color: blue; }
#b1Header a { text-decoration: none; color: blue; }

#b2Table { border-collapse: collapse; width: 100% }
#b2Table th { font-weight: bold; padding: 3px; }
#b2Table td { padding: 3px; }
#b2Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b2Table tr:first-child { background-color: #dfefff; position: sticky; top:0px; z-index:1 }
#b2Table a { text-decoration: none; color: blue; }
#b2Header a { text-decoration: none; color: blue; }

#b3Table { border-collapse: collapse; width: 100% }
#b3Table th { font-weight: bold; padding: 3px; }
#b3Table td { padding: 3px; }
#b3Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b3Table tr:first-child { background-color: #dfefff; position: sticky; top:0px; z-index:1 }
#b3Table a { text-decoration: none; color: blue; }
#b3Header a { text-decoration: none; color: blue; }

#b4Table { border-collapse: collapse; width: 100% }
#b4Table th { font-weight: bold; padding: 3px; }
#b4Table td { padding: 3px; }
#b4Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b4Table tr:first-child { background-color: #dfefff; position: sticky; top:0px; z-index:1 }
#b4Table a { text-decoration: none; color: blue; }
#b4Header a { text-decoration: none; color: blue; }

#b5Table { border-collapse: collapse; width: 100% }
#b5Table th { font-weight: bold; padding: 3px; }
#b5Table td { padding: 3px; }
#b5Table td sup { padding: 3px; line-height: 1.9em; }
#b5Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b5Table tr:first-child { background-color: #dfefff; position: sticky; top:0px; z-index:1 }
#b5Table a { text-decoration: none; color: blue; }
#b5Header a { text-decoration: none; color: blue; }

.noWrap { white-space: nowrap; }
.facetFilter .yui-panel .bd { width: 285px; }

.termType { font-variant: small-caps; font-size: 10px; font-family: Verdana, Arial, Helvetica; }
.nameCol { width: 25%; }
.termCol { width: 40%; }
.dataCol { width: 10%; }
.bestMatchCol { } 
.small { font-size: 10px; }

#featurePaginator {
	float: right;
    font-size: 12px;
    margin-top: 10px;
    font-weight: normal;
    margin-right: 10px;
    text-decoration: none;
}
#allelePaginator {
	float: right;
    font-size: 12px;
    margin-top: 10px;
    font-weight: normal;
    margin-right: 10px;
    text-decoration: none;
}
#vocabPaginator {
	float: right;
    font-size: 12px;
    margin-top: 10px;
    font-weight: normal;
    margin-right: 10px;
    text-decoration: none;
}
#strainPaginator {
	float: right;
    font-size: 12px;
    margin-top: 10px;
    font-weight: normal;
    margin-right: 10px;
    text-decoration: none;
}
#otherIdPaginator {
	float: right;
    font-size: 12px;
    margin-top: 10px;
    font-weight: normal;
    margin-right: 10px;
    text-decoration: none;
}
#errorDiv {
	color: red;
	padding-top: 10px;
	font-weight: bold;
}
.filtered {
	color: #97454C;
	text-align: center;
}
.filteredText {
	font-size:80%;
	text-decoration: italic;
}
.filterItem {
    white-space: nowrap;
    font-size: 10px;
    color: #025;
    border: 1px solid #7d95b9;
    padding: 2px 1.5em 2px 2px;
    margin: 1em .2em;
    cursor: pointer;
    background: url(${configBean.WEBSHARE_URL}images/remove.gif) no-repeat right;
}
.removeFilterDiv {
	float: none; 
	padding-top: 5px;
	width: 100%;
}
#hasResults {
	color: #002255;
	font-weight: bold;
}
#noResults {
	color: black;
	font-weight: normal;
}
#featureDownloads {
	display: inline-block;
	margin-left: 250px;
	font-size: 12px;
}
#featureDownloads a {
	font-size: 12px;
	font-family: Arial,Helvetica;
	color: #000001;
	font-weight: normal;
	font-size: 20px;
}
#featureDownloads .export {
	display: inline;
	font-family: Verdana,Arial,sans-serif;
	font-size: 1.1em;
}
#alleleDownloads {
	display: inline-block;
	margin-left: 250px;
	font-size: 12px;
}
#alleleDownloads a {
	font-size: 12px;
	font-family: Arial,Helvetica;
	color: #000001;
	font-weight: normal;
	font-size: 20px;
}
#alleleDownloads .export {
	display: inline;
	font-family: Verdana,Arial,sans-serif;
	font-size: 1.1em;
}
#vocabDownloads {
	display: inline-block;
	margin-left: 250px;
	font-size: 12px;
}
#vocabDownloads a {
	font-size: 12px;
	font-family: Arial,Helvetica;
	color: #000001;
	font-weight: normal;
	font-size: 20px;
}
#vocabDownloads .export {
	display: inline;
	font-family: Verdana,Arial,sans-serif;
	font-size: 1.1em;
}
#strainDownloads {
	display: inline-block;
	margin-left: 250px;
	font-size: 12px;
}
#strainDownloads a {
	font-size: 12px;
	font-family: Arial,Helvetica;
	color: #000001;
	font-weight: normal;
	font-size: 20px;
}
#strainDownloads .export {
	display: inline;
	font-family: Verdana,Arial,sans-serif;
	font-size: 1.1em;
}
#otherIDDownloads {
	display: inline-block;
	margin-left: 250px;
	font-size: 12px;
}
#otherIDDownloads a {
	font-size: 12px;
	font-family: Arial,Helvetica;
	color: #000001;
	font-weight: normal;
	font-size: 20px;
}
#otherIDDownloads .export {
	display: inline;
	font-family: Verdana,Arial,sans-serif;
	font-size: 1.1em;
}
</style>

<!-- from https://developers.google.com/fonts/docs/material_icons -->
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">