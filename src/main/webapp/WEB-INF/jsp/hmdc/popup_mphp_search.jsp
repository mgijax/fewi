<%@ page import = "org.jax.mgi.fe.datamodel.QueryFormOption" %>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
  <title>HP-MP Search</title>

  <!-- import jquery UI specifically for this page -->
  <script type="text/javascript" src="${configBean.WEBSHARE_URL}js/jquery-1.10.2.min.js"></script>

  <link REL="stylesheet" href="${configBean.WEBSHARE_URL}css/mgi.css" type="text/css"/>

  <style>

    #betaNotice {
      padding: 4px;
      color: darkred;
    }
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
      <span id='betaNotice'>--(BETA Version)--</span>
    </div>
    <div style='padding:4px; padding-left:10px;'>
      <strong>Add Related Mammalian (MP) and Human (HPO) Phenotype Terms by ID</strong>
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

  window.onload = function () {
      const inp = document.getElementById("hpmpInput")
      inp.value = window.opener.popupOpenerInput.value
  }

  // gathers all checkboxes 
  function jqCheckboxes()
  {
    return $("[name='matchTermCheck']");
  }

  function clearPopupForm()
  {
    document.getElementById("hpmpInput").value = ""; 
    // clear the generated elements of the DOM
    $('#mpHpSummaryTable').html(" ");
    $('#ysf').html(" ");       }

  // updates parent window with ID list
  function populateParentWindow()
  {
    // gather IDs of checkboxes
    var inputIDs = '';
    jqCheckboxes().each(function(idx,checkbox){
      var chk = $(checkbox);
      if(chk.prop("checked")){
        inputIDs = inputIDs + ' ' + chk.val();
      }
    }); 

    // parent window's input value
    const input = window.opener.popupOpenerInput

    var distinctIDs = [];
    var fullIdString = input.value.trim() + ' ' + $('#hpmpInput').val().trim() + ' ' + inputIDs;
    var nonDistinctIDs = fullIdString.trim().split(/[ ,]+/);
    for (var i = 0; i < nonDistinctIDs.length; i++) {
      thisInputID = nonDistinctIDs[i].trim();
      if (!distinctIDs.includes(thisInputID)){
        distinctIDs.push(thisInputID);
      }
    }

    //input.value = input.value + ' ' + $('#hpmpInput').val() + ' ' + inputIDs;
    input.value = distinctIDs.join(", ");
    input.dispatchEvent(new Event('change'));

    // cleanup and exit
    window.close();
  }

  // create the summary table to be inserted
  function populateTermTable()
  {
    var inputIds = $('#hpmpInput').val().trim();
    inputIds = inputIds.replace(/,*$/, ''); // remove trailing comma
    inputIds = inputIds.trim();
    var inputIdsSplit = inputIds.split(/[ ,]+/);

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
              var resultIDs=[];
              for (var i = 0; i < data.summaryRows.length; i++) {
                thisRow = data.summaryRows[i];
                //console.log(thisRow);

                // determine backgroup color and "You searched for..."
                if (lastSearchId != thisRow.searchId) { 

                  // ysf
                  resultIDs.push(thisRow.searchId);
                  
                  // row color
                  if(stripeRowCount % 2 != 0) {
                    rowBgColor='#F0F8FF';
                  }
                  else {rowBgColor='';}
                  stripeRowCount++;
                }
                lastSearchId = thisRow.searchId;

                // checking for nulls
                displaySearchTermDefinition = thisRow.searchTermDefinition;
                if (displaySearchTermDefinition==null) {displaySearchTermDefinition=" ";}
                displayMatchTermSynonym = thisRow.matchTermSynonym;
                if (displayMatchTermSynonym==null) {displayMatchTermSynonym=" ";}
                displayMatchTermDefinition = thisRow.matchTermDefinition;
                if (displayMatchTermDefinition==null) {displayMatchTermDefinition=" ";}                

                // create table rows
                tbl = tbl + '<TR bgcolor="' + rowBgColor + '">' +
                          '<td>(' + thisRow.searchId + ')</br>' + thisRow.searchTerm + '</td>' +
                          '<td>' + displaySearchTermDefinition + '</td>' +
                          '<td>' + thisRow.matchMethod + '</td>' +
                          '<td>' + thisRow.matchType + '</td>' +
                          '<td>(' + thisRow.matchTermID + ')</br>' + thisRow.matchTermName + '</td>' +
                          '<td>' + displayMatchTermSynonym + '</td>' +
                          '<td>' + displayMatchTermDefinition + '</td>' +
                          '<td> <input name="matchTermCheck" type="checkbox" value="' + thisRow.matchTermID + '"> </td>' +
                          '</TR>';
              }

              tbl = tbl + '</table>';

              // generate ysf string
              var inputIdsNotFound=[];
              for (var i = 0; i < inputIdsSplit.length; i++) {
                thisInputID = inputIdsSplit[i].trim();
                if (!resultIDs.includes(thisInputID)){
                  inputIdsNotFound.push(thisInputID);
                }
              }
              var ysfString = " You searched for... <br> ";
              if (inputIdsNotFound.length > 0) {
                ysfString = ysfString + inputIdsNotFound.join(", ") + "; No matching terms were found for these term IDs <br><br>";
              }
              ysfString = ysfString + resultIDs.join(", ");

              // insert the generated elements into the DOM
              $('#ysf').html(ysfString);
              $('#mpHpSummaryTable').html(tbl);
            }
            else { // no results

              // update the generated elements and inject into the DOM
              $('#mpHpSummaryTable').html(" ");
              $('#ysf').html(" You searched for... <br> " + inputIdsSplit.join(", ") + "; No matching terms were found for these term IDs <br><br>");            
            }

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

