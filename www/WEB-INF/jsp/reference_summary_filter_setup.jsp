<c:if test="${not empty typeFilter}">
  <script>
    var messageSpan = document.getElementById('defaultText');
    var savedText = messageSpan.innerHTML;

    messageSpan.innerHTML = 'Retrieving full data set before filtering...';
    messageSpan.style.display = '';

    var testCount = 0;
    var gotCount = false;

    var checkCount = function(testCount) {
	var el = document.getElementById("totalCount");
	if (el != null) {
	    if ((el.innerHTML != "0") || (testCount >= 10)) {
		var filterVal = "${typeFilter}";
		facets['typeFilter'] = [ filterVal ];
		var filteredState = generateRequest(
		    myDataTable.getState().sortedBy, 0,
		    myDataTable.get('paginator').getRowsPerPage());
		handleHistoryNavigation(filteredState);
		setTimeout(function() {
		    messageSpan.innerHTML = savedText; }, 1500);
	    } else {
		setTimeout (function() { checkCount(testCount + 1); }, 500);
	    }
	}
    };
    setTimeout (function() { checkCount(0); }, 500);
  </script>
</c:if>
