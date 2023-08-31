<%@ page import = "mgi.frontend.datamodel.QueryFormOption" %>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
  <title>HP-MP Search</title>

  <!-- import jquery UI specifically for this page -->
  <script type="text/javascript" src="${configBean.WEBSHARE_URL}js/jquery-1.10.2.min.js"></script>

  <link REL="stylesheet" href="${configBean.WEBSHARE_URL}css/mgi.css" type="text/css"/>

  <style>

    #ysf {
      padding: 4px;
    }

    #hmdcTermSearchTable {
      width:  98%;
      margin: 4px;
    }  

    #hmdcTermSearchTable, th {
      border: 1px solid;
      border-collapse: collapse;
      padding: 4px;
    }  
    #hmdcTermSearchTable, td {
      border: 1px solid;
      border-collapse: collapse;
      padding: 4px;
    }  
  </style>


</head>


<body bgcolor="#ffffff">

  <form name="PhenoPopup">

    <div style='padding:4px; padding-left:10px;'>
      <strong>Add Related Mammalian (MP) and Human (HPO) Phenotype Terms byID</strong>
    </div>
    <br><br>
    <div style='padding:4px; padding-left:10px;'>
      <label>Enter MP and/or HPO Term ID(s):</label>
      <input type="text" id="hpmpInput" name="hpmpInput" size="60">
    </div>

    <div style='padding-left:20px; padding:4px; width:600px;'>
      <input type="button" value="Search for related terms" onClick="populateTermTable()">
      <input type="button" value="Add IDs to HMDC search" onClick="populateParentWindow()">
      <input type="button" value="Clear Search" onClick="clearPopupForm()">
      <input type="button" value="Cancel" onClick="window.close()">
    </div>

  </form>

  <!-- generated "You Searched for..." inserted here -->
  <div id="errorText"></div>

  <!-- generated "You Searched for..." inserted here -->
  <div id="ysf"></div>

  <!-- generated table inserted here -->
  <div id="mpHpSummaryTable"></div>


  <script language="JavaScript">

  // gathers all checkboxes 
  function jqCheckboxes()
  {
    return $("[name='matchTermCheck']");
  }

  function clearPopupForm()
  {
    document.getElementById("hpmpInput").value = ""; 
    // clear the generated elements into the DOM
    $('#mpHpSummaryTable').html(" ");
    $('#ysf').html(" ");       }

  // updates parent window with ID list
  function populateParentWindow()
  {

    var inputIDs = '';
    jqCheckboxes().each(function(idx,checkbox){
      var chk = $(checkbox);
      if(chk.prop("checked")){
        inputIDs = inputIDs + ' ' + chk.val();
      }
    }); 

    window.opener.document.getElementById("formly_3_input_input_0").value = 
      window.opener.document.getElementById("formly_3_input_input_0").value + ' ' + $('#hpmpInput').val() + ' ' + inputIDs;
    window.opener.document.getElementById("formly_3_input_input_0").dispatchEvent(new Event('change'));

    // cleanup and exit
    window.close();
  }

  // create the summary table to be inserted
  function populateTermTable()
  {
    var inputIds = $('#hpmpInput').val();
    var inputIdsSplit = inputIds.split(/[ ,]+/);
    console.log(inputIdsSplit);
console.log(['joe', 'jane', 'mary'].includes('jane')); // true
    // ensure parent window exists
    if (window.opener && !window.opener.closed)
    {
      // call end-point and handle JSON retrieved
      $.get("${configBean.FEWI_URL}diseasePortal/searchPopupJson?id=" + inputIds, function(data) {
        try {
            console.log('JSON rows retrieved:' + data.summaryRows.length);
            $('#errorText').html("");

            if (data.summaryRows.length > 0) {
              // initial setup
              tbl = '<table id="hmdcTermSearchTable">';
              tbl = tbl + '<TR bgcolor="#ddd">' +
                          '<TH>Search Term (ID)</TH>' +
                          '<TH>Search Term Definition</TH>' +
                          '<TH>Match Method</TH>' +
                          '<TH>Match Type</TH>' +
                          '<TH>Matched Term</TH>' +
                          '<TH>Match Term Synonym</TH>' +
                          '<TH>Match Term Definition</TH>' +
                          '<TH>Add to Search</TH>' +
                          '</TR>';


              var stripeRowCount=0;
              var lastSearchId='';
              var rowBgColor='';
              var ysfList=[];
              for (var i = 0; i < data.summaryRows.length; i++) {
                thisRow = data.summaryRows[i];
                //console.log(thisRow);

                // determine backgroup color and "You searched for..."
                if (lastSearchId != thisRow.searchId) { 

                  // ysf
                  ysfList.push(thisRow.searchId);
                  
                  // row color
                  if(stripeRowCount % 2 != 0) {
                    rowBgColor='#F0F8FF';
                  }
                  else {rowBgColor='';}
                  stripeRowCount++;
                }
                lastSearchId = thisRow.searchId;
                
                // create table rows
                tbl = tbl + '<TR bgcolor="' + rowBgColor + '">' +
                          '<td>(' + thisRow.searchId + ')</br>' + thisRow.searchTerm + '</td>' +
                          '<td>' + thisRow.searchTermDefinition + '</td>' +
                          '<td>' + thisRow.matchMethod + '</td>' +
                          '<td>' + thisRow.matchType + '</td>' +
                          '<td>(' + thisRow.matchTermID + ')</br>' + thisRow.matchTermName + '</td>' +
                          '<td>' + thisRow.matchTermSynonym + '</td>' +
                          '<td>' + thisRow.matchTermDefinition + '</td>' +
                          '<td> <input name="matchTermCheck" type="checkbox" value="' + thisRow.matchTermID + '"> </td>' +
                          '</TR>';
              }

              tbl = tbl + '</table>';

              // insert the generated elements into the DOM
              $('#mpHpSummaryTable').html(tbl);
              $('#ysf').html(" You searched for... <br> " + ysfList.join(", "));
            }
            else {
              $('#errorText').html("No matching terms were found for your query.");

              // clear the generated elements into the DOM
              $('#mpHpSummaryTable').html(" ");
              $('#ysf').html(" ");            }

        } catch (e) {
          console.log('E3: Failed to get IDs to forward.');
        }
      }).fail(function() {
        console.log('E4: Failed to get IDs to forward.');
      });

    }

    // update the system summary
//    window.opener.updatePhenoSystemSummary();
    
  }

  </script>

</body>
</html>

