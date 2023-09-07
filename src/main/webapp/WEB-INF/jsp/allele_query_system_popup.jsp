<%@ page import = "org.jax.mgi.fe.datamodel.QueryFormOption" %>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
  <title>Phenotypes Selection List</title>

  <!-- import jquery UI specifically for this page -->
  <script type="text/javascript" src="${configBean.WEBSHARE_URL}js/jquery-1.10.2.min.js"></script>

  <link REL="stylesheet" href="${configBean.WEBSHARE_URL}css/mgi.css" type="text/css"/>

</head>


<body bgcolor="#ffffff">
  <form name="PhenoPopup">
  <div style='padding:4px; padding-left:10px;'>
    <a style='float:right;' href="javascript:childWindow=window.open('${configBean.USERHELP_URL}ALLELE_phenotype_highlevel_help.shtml', 'helpWindow', 'width=1000,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes'); childWindow.focus();"><IMG SRC="${configBean.WEBSHARE_URL}images/help_small.jpg"/></A>
    Select <strong>Anatomical Systems Affected by Phenotypes</strong> to add to the query.
  </div>

  <div style='padding-left:20px; padding:4px; width:400px;'>
    <input type="button" value="Add To Query" onClick="populateParent()">
    <input type="button" value="Cancel" onClick="window.close()">
  </div>

  <div style='margin:4px; padding:4px; overflow:hidden; border:2px solid gray;'>
    <div style='width:200px; float:left;'>
    	<fewi:checkboxOptions items="${widgetValues1}" name="phenoSystem" />
    </div>

    <div style='width:250px; float:right;'>
    	<fewi:checkboxOptions items="${widgetValues2}" name="phenoSystem" />
    </div>

  </div>

  <div style='padding-left:20px; padding:4px; width:400px;'>
    <input type="button" value="Add To Query" onClick="populateParent()">
    <input type="button" value="Cancel" onClick="window.close()">
  </div>

  </form>


  <script language="JavaScript">

  
  function jqCheckboxes()
  {
	  return $("[name='phenoSystem']");
  }
  var preselectedIDs = new Array();

  function populateParent()
  {
    var selectedMpIds = "";
    var selectedMpText = "";

    // ensure parent window exists
    if (window.opener && !window.opener.closed)
    {
    	jqCheckboxes().each(function(idx,checkbox){
    		var chk = $(checkbox);
    		if(chk.prop("checked"))
    		{
    			selectedMpIds = selectedMpIds + chk.val()+" ";
    	       // selectedMpText = selectedMpText + "<li>"+chk.parent().text().trim()+" ("+chk.val()+")</li>";
    		}
    	});
    }


    // if user has selected an MP, add new query to Phenotypes box
    if (selectedMpIds != "")
    {
        // append to phenotype form parameter if it wasn't empty
        if (_form.phenotype.value != "")
        {
          var phenoInput = _form.phenotype.value;

          // get all possible values and remove them from input
          jqCheckboxes().each(function(idx,checkbox){
    		var chk = $(checkbox);
    		// remove existing MP IDs from pheno free-text field to avoid dupes
            // 	and ensure we've removed unselected checks
            phenoInput = phenoInput.replace(chk.val()," ");
          });
         
          // clean out extra spaces
          while (phenoInput.search("  ") > -1) {
            phenoInput = phenoInput.replace("  "," ");
          }

          // update the you selected section
          _form.phenotype.value = phenoInput + " " + selectedMpIds;
         // window.opener.document.getElementById("selectedMpTextDiv").innerHTML = "Categories selected from systems list:<ul>" + selectedMpText + "</ul>";
        }
        else 
        {
          _form.phenotype.value = selectedMpIds;
         // window.opener.document.getElementById("selectedMpTextDiv").innerHTML = "Categories selected from systems list:<ul>" + selectedMpText + "</ul>";
        }
    }

    // update the system summary
    window.opener.updatePhenoSystemSummary();
    
    // cleanup and exit
    window.close();
  }

  // global form reference
  var _form=null;

  $(window).load(function () 
  {
	// try to set the form
	_form = window.opener.document.<c:out value="${not empty formName ? formName : 'alleleQueryForm' }"/>;
    // If the phenotype text box isn't empty, we need to scan for MP term IDs
    // that may have already been entered.  We then activate those checks.
    if (_form.phenotype.value != "")
    {
      var phenoInput = _form.phenotype.value;

      jqCheckboxes().each(function(idx,checkbox){
  			var chk = $(checkbox);
          	if (phenoInput.search(chk.val()) > -1) 
          	{
              chk.prop("checked", true);
            }
        });
    }
  });

  </script>

</body>
</html>

