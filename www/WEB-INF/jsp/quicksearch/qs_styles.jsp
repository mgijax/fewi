<style>
.helpCursor { cursor: help; }
.qsHeader { width: 100%; background-color: #F0F8FF; color: #002255; margin-top: 0.75em; 
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

#b1Results { max-height: 300px; overflow-y: auto; width: 100%; }
#b2Results { max-height: 300px; overflow-y: auto; width: 100%; }
#b4Results { max-height: 300px; overflow-y: auto; width: 100%; }
#b5Results { max-height: 300px; overflow-y: auto; width: 100%; }

#b1Table { border-collapse: collapse; width: 100% }
#b1Table th { font-weight: bold; padding: 3px; }
#b1Table td { padding: 3px; }
#b1Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b1Table tr:first-child { background-color: #dfefff; }

#b2Table { border-collapse: collapse; width: 100% }
#b2Table th { font-weight: bold; padding: 3px; }
#b2Table td { padding: 3px; }
#b2Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b2Table tr:first-child { background-color: #dfefff; }

#b3Table { border-collapse: collapse; width: 100% }
#b3Table th { font-weight: bold; padding: 3px; }
#b3Table td { padding: 3px; }
#b3Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b3Table tr:first-child { background-color: #dfefff; }

#b4Table { border-collapse: collapse; width: 100% }
#b4Table th { font-weight: bold; padding: 3px; }
#b4Table td { padding: 3px; }
#b4Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b4Table tr:first-child { background-color: #dfefff; }

#b5Table { border-collapse: collapse; width: 100% }
#b5Table th { font-weight: bold; padding: 3px; }
#b5Table td { padding: 3px; }
#b5Table td sup { padding: 3px; line-height: 1.9em; }
#b5Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b5Table tr:first-child { background-color: #dfefff; }

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
#errorDiv {
	color: red;
	padding-top: 10px;
	font-weight: bold;
}
</style>
