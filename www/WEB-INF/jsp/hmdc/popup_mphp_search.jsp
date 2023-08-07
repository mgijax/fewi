<%@ page import = "mgi.frontend.datamodel.QueryFormOption" %>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
  <title>HP-MP Search</title>

  <!-- import jquery UI specifically for this page -->
  <script type="text/javascript" src="${configBean.WEBSHARE_URL}js/jquery-1.10.2.min.js"></script>

  <link REL="stylesheet" href="${configBean.WEBSHARE_URL}css/mgi.css" type="text/css"/>

  <style>

    #hmdcTermSearchTable {
      width:  100%;
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
      <label>Enter Term ID:</label>
      <input type="text" id="hpmpInput" name="hpmpInput">
    </div>

    <div style='padding-left:20px; padding:4px; width:400px;'>
      <input type="button" value="Search for related terms" onClick="populateTermTable()">
      <input type="button" value="Cancel" onClick="window.close()">
      <input type="button" value="Set Parent Window" onClick="populateParentWindow()">
    </div>

  </form>


  <!-- generated table will be inserted here -->
  <div id="mpHpSummaryTable"></div>




  <script language="JavaScript">

  function populateParentWindow()
  {

    window.opener.document.getElementById("formly_3_input_input_0").value = "MP:0000438";
    window.opener.document.getElementById("formly_3_input_input_0").dispatchEvent(new Event('change'));

    // cleanup and exit
    window.close();
  }

  
  function jqCheckboxes()
  {
    return $("[name='phenoSystem']");
  }


  // 
  var preselectedIDs = new Array();
  function populateTermTable()
  {
    var selectedIds = "";

    // ensure parent window exists
    if (window.opener && !window.opener.closed)
    {


//      $.get("http://frost.informatics.jax.org/diseasePortal/searchPopupJson?id=123", function(data) {
      $.get("${configBean.FEWI_URL}diseasePortal/searchPopupJson?id=123", function(data) {
        try {
            console.log('Got ' + data.summaryRows.length);

            // initial setup
            tbl = '<table id="hmdcTermSearchTable">';
            tbl = tbl + '<TR>' +
                        '<TH>Search Term (ID)</TH>' +
                        '<TH>Match Method</TH>' +
                        '<TH>Match Type</TH>' +
                        '<TH>Matched Term</TH>' +
                        '<TH>Match Term Synonym</TH>' +
                        '<TH>Match Term Definition</TH>' +
                        '<TH>Add to Search</TH>' +
                        '</TR>';

            for (var i = 0; i < data.summaryRows.length; i++) {
              thisRow = data.summaryRows[i];
              console.log(thisRow);
              tbl = tbl + '<TR>' +
                        '<td>(' + thisRow.searchId + ')</br>' + thisRow.matchTermName + '</td>' +
                        '<td>' + thisRow.matchMethod + '</td>' +
                        '<td>' + thisRow.matchType + '</td>' +
                        '<td>' + thisRow.matchTermName + '</br>(' + thisRow.matchTermID + ')</td>' +
                        '<td></td>' +
                        '<td></td>' +
                        '<td></td>' +
                        '</TR>';
            }

            tbl = tbl + '</table>' +


            // insert the generated table into the DOM
            $('#mpHpSummaryTable').html(tbl);


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

