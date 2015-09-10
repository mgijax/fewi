<c:if test="${not empty typeFilter}">
  <script>
    var messageSpan = document.getElementById('defaultText');
    var savedText = messageSpan.innerHTML;

    messageSpan.innerHTML = 'Retrieving full data set before filtering...';
    messageSpan.style.display = '';

    setTimeout(function() {
      var filterVal = "${typeFilter}";
      facets['typeFilter'] = [ filterVal ];
      var filteredState = generateRequest(myDataTable.getState().sortedBy, 0,
        myDataTable.get('paginator').getRowsPerPage());
      handleHistoryNavigation(filteredState);
      setTimeout(function() {
        messageSpan.innerHTML = savedText;
      }, 1500);
    }, 1500);
  </script>
</c:if>
