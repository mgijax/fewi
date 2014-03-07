<%@ page import = "mgi.frontend.datamodel.QueryFormOption" %>

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
    Select <STRONG>Anatomical Systems Affected by Phenotypes</STRONG> to add to the query.
  </div>

  <div style='padding-left:20px; padding:4px; width:400px;'>
    <input type="button" value="Add To Query" onClick="populateParent()">
    <input type="button" value="Cancel" onClick="window.close()">
  </div>

  <div style='margin:4px; padding:4px; overflow:hidden; border:2px solid gray;'>
    <div style='width:200px; float:left;'>
      <label><input id="mp0005375" type="checkbox" />adipose tissue
      <br/></label>
      <label><input id="mp0005386" type="checkbox" />behavior/neurological
      <br/></label>
      <label><input id="mp0005385" type="checkbox" />cardiovascular system
      <br/></label>
      <label><input id="mp0005384" type="checkbox" />cellular
      <br/></label>
      <label><input id="mp0005382" type="checkbox" />craniofacial
      <br/></label>
      <label><input id="mp0005381" type="checkbox" />digestive/alimentary system
      <br/></label>
      <label><input id="mp0005380" type="checkbox" />embryogenesis
      <br/></label>
      <label><input id="mp0005379" type="checkbox" />endocrine/exocrine glands
      <br/></label>
      <label><input id="mp0005378" type="checkbox" />growth/size
      <br/></label>
      <label><input id="mp0005377" type="checkbox" />hearing/vestibular/ear
      <br/></label>
      <label><input id="mp0005397" type="checkbox" />hematopoietic system
      <br/></label>
      <label><input id="mp0005376" type="checkbox" />homeostasis/metabolism
      <br/></label>
      <label><input id="mp0005387" type="checkbox" />immune system
      <br/></label>
      <label><input id="mp0010771" type="checkbox" />integument
      <br/></label>
      <label><input id="mp0005371" type="checkbox" />limbs/digits/tail
      </label>
    </div>

    <div style='width:250px; float:right;'>
      <label><input id="mp0005370" type="checkbox" />liver/biliary system
      <br/></label>
      <label><input id="mp0010768" type="checkbox" />mortality/aging
      <br/></label>
      <label><input id="mp0005369" type="checkbox" />muscle
      <br/></label>
      <label><input id="mp0003631" type="checkbox" />nervous system
      <br/></label>
      <label><input id="mp0003012" type="checkbox" />phenotype not analyzed
      <br/></label>
      <label><input id="mp0002873" type="checkbox" />normal phenotype
      <br/></label>
      <label><input id="mp0005395" type="checkbox" />other phenotype
      <br/></label>
      <label><input id="mp0001186" type="checkbox" />pigmentation
      <br/></label>
      <label><input id="mp0005367" type="checkbox" />renal/urinary system
      <br/></label>
      <label><input id="mp0005389" type="checkbox" />reproductive system
      <br/></label>
      <label><input id="mp0005388" type="checkbox" />respiratory system
      <br/></label>
      <label><input id="mp0005390" type="checkbox" />skeleton
      <br/></label>
      <label><input id="mp0005394" type="checkbox" />taste/olfaction
      <br/></label>
      <label><input id="mp0002006" type="checkbox" />tumorigenesis
      <br/></label>
      <label><input id="mp0005391" type="checkbox" />vision/eye
      <br/></label>
    </div>

  </div>

  <div style='padding-left:20px; padding:4px; width:400px;'>
    <input type="button" value="Add To Query" onClick="populateParent()">
    <input type="button" value="Cancel" onClick="window.close()">
  </div>

  </form>


  <script language="JavaScript">

  var preselectedIDs = new Array();

  function populateParent()
  {
    var selectedMpIds = "";
    var selectedMpText = "";

    // ensure parent window exists
    if (window.opener && !window.opener.closed)
    {
      if (document.getElementById('mp0005375').checked) {
        selectedMpIds = selectedMpIds + "MP:0005375 "
        selectedMpText = selectedMpText + "<li>adipose tissue (MP:0005375)";
      }
      if (document.getElementById('mp0005386').checked) {
        selectedMpIds = selectedMpIds + "MP:0005386 "
        selectedMpText = selectedMpText + "<li>behavior/neurological (MP:0005386)";
      }
      if (document.getElementById('mp0005385').checked) {
        selectedMpIds = selectedMpIds + "MP:0005385 "
        selectedMpText = selectedMpText + "<li>cardiovascular system (MP:0005385)";
      }
      if (document.getElementById('mp0005384').checked) {
        selectedMpIds = selectedMpIds + "MP:0005384 "
        selectedMpText = selectedMpText + "<li>cellular (MP:0005384)";
      }
      if (document.getElementById('mp0005382').checked) {
        selectedMpIds = selectedMpIds + "MP:0005382 "
        selectedMpText = selectedMpText + "<li>craniofacial (MP:0005382)";
      }
      if (document.getElementById('mp0005381').checked) {
        selectedMpIds = selectedMpIds + "MP:0005381 "
        selectedMpText = selectedMpText + "<li>digestive/alimentary system (MP:0005381)";
      }
      if (document.getElementById('mp0005380').checked) {
        selectedMpIds = selectedMpIds + "MP:0005380 "
        selectedMpText = selectedMpText + "<li>embryogenesis (MP:0005380)";
      }
      if (document.getElementById('mp0005379').checked) {
        selectedMpIds = selectedMpIds + "MP:0005379 "
        selectedMpText = selectedMpText + "<li>endocrine/exocrine glands (MP:0005379)";
      }
      if (document.getElementById('mp0005378').checked) {
        selectedMpIds = selectedMpIds + "MP:0005378 "
        selectedMpText = selectedMpText + "<li>growth/size (MP:0005378)";
      }
      if (document.getElementById('mp0005377').checked) {
        selectedMpIds = selectedMpIds + "MP:0005377 "
        selectedMpText = selectedMpText + "<li>hearing/vestibular/ear (MP:0005377)";
      }
      if (document.getElementById('mp0005397').checked) {
        selectedMpIds = selectedMpIds + "MP:0005397 "
        selectedMpText = selectedMpText + "<li>hematopoietic system (MP:0005397)";
      }
      if (document.getElementById('mp0005376').checked) {
        selectedMpIds = selectedMpIds + "MP:0005376 "
        selectedMpText = selectedMpText + "<li>homeostasis/metabolism (MP:0005376)";
      }
      if (document.getElementById('mp0005387').checked) {
        selectedMpIds = selectedMpIds + "MP:0005387 "
        selectedMpText = selectedMpText + "<li>immune system (MP:0005387)";
      }
      if (document.getElementById('mp0010771').checked) {
        selectedMpIds = selectedMpIds + "MP:0010771 "
        selectedMpText = selectedMpText + "<li>integument (MP:0010771)";
      }
      if (document.getElementById('mp0005371').checked) {
        selectedMpIds = selectedMpIds + "MP:0005371 "
        selectedMpText = selectedMpText + "<li>limbs/digits/tail (MP:0005371)";
      }
      if (document.getElementById('mp0005370').checked) {
        selectedMpIds = selectedMpIds + "MP:0005370 "
        selectedMpText = selectedMpText + "<li>liver/biliary system (MP:0005370)";
      }
      if (document.getElementById('mp0010768').checked) {
        selectedMpIds = selectedMpIds + "MP:0010768 "
        selectedMpText = selectedMpText + "<li>mortality/aging (MP:0010768)";
      }
      if (document.getElementById('mp0005369').checked) {
        selectedMpIds = selectedMpIds + "MP:0005369 "
        selectedMpText = selectedMpText + "<li>muscle (MP:0005369)";
      }
      if (document.getElementById('mp0003631').checked) {
        selectedMpIds = selectedMpIds + "MP:0003631 "
        selectedMpText = selectedMpText + "<li>nervous system (MP:0003631)";
      }
      if (document.getElementById('mp0003012').checked) {
        selectedMpIds = selectedMpIds + "MP:0003012 "
        selectedMpText = selectedMpText + "<li>phenotype not analyzed (MP:0003012)";
      }
      if (document.getElementById('mp0002873').checked) {
        selectedMpIds = selectedMpIds + "MP:0002873 "
        selectedMpText = selectedMpText + "<li>normal phenotype (MP:0002873)";
      }
      if (document.getElementById('mp0005395').checked) {
        selectedMpIds = selectedMpIds + "MP:0005395 "
        selectedMpText = selectedMpText + "<li>other phenotype (MP:0005395)";
      }
      if (document.getElementById('mp0001186').checked) {
        selectedMpIds = selectedMpIds + "MP:0001186 "
        selectedMpText = selectedMpText + "<li>pigmentation (MP:0001186)";
      }
      if (document.getElementById('mp0005367').checked) {
        selectedMpIds = selectedMpIds + "MP:0005367 "
        selectedMpText = selectedMpText + "<li>renal/urinary system (MP:0005367)";
      }
      if (document.getElementById('mp0005389').checked) {
        selectedMpIds = selectedMpIds + "MP:0005389 "
        selectedMpText = selectedMpText + "<li>reproductive system (MP:0005389)";
      }
      if (document.getElementById('mp0005388').checked) {
        selectedMpIds = selectedMpIds + "MP:0005388 "
        selectedMpText = selectedMpText + "<li>respiratory system (MP:0005388)";
      }
      if (document.getElementById('mp0005390').checked) {
        selectedMpIds = selectedMpIds + "MP:0005390 "
        selectedMpText = selectedMpText + "<li>skeleton (MP:0005390)";
      }
      if (document.getElementById('mp0005394').checked) {
        selectedMpIds = selectedMpIds + "MP:0005394 "
        selectedMpText = selectedMpText + "<li>taste/olfaction (MP:0005394)";
      }
      if (document.getElementById('mp0002006').checked) {
        selectedMpIds = selectedMpIds + "MP:0002006 "
        selectedMpText = selectedMpText + "<li>tumorigenesis (MP:0002006)";
      }
      if (document.getElementById('mp0005391').checked) {
        selectedMpIds = selectedMpIds + "MP:0005391 "
        selectedMpText = selectedMpText + "<li>vision/eye (MP:0005391)";
      }
    }


    // if user has selected an MP, add new query to Phenotypes box
    if (selectedMpIds != "")
    {
        // append to phenotype form parameter if it wasn't empty
        if (window.opener.document.alleleQueryForm.phenotype.value != ""){
          var phenoInput = window.opener.document.alleleQueryForm.phenotype.value;

          // remove existing MP IDs from pheno free-text field to avoid dupes
          // and ensure we've removed unselected checks
          phenoInput = phenoInput.replace("MP:0005375"," ");
          phenoInput = phenoInput.replace("MP:0005386"," ");
          phenoInput = phenoInput.replace("MP:0005385"," ");
          phenoInput = phenoInput.replace("MP:0005384"," ");
          phenoInput = phenoInput.replace("MP:0005382"," ");
          phenoInput = phenoInput.replace("MP:0005381"," ");
          phenoInput = phenoInput.replace("MP:0005380"," ");
          phenoInput = phenoInput.replace("MP:0005379"," ");
          phenoInput = phenoInput.replace("MP:0005378"," ");
          phenoInput = phenoInput.replace("MP:0005377"," ");
          phenoInput = phenoInput.replace("MP:0005397"," ");
          phenoInput = phenoInput.replace("MP:0005376"," ");
          phenoInput = phenoInput.replace("MP:0005387"," ");
          phenoInput = phenoInput.replace("MP:0010771"," ");
          phenoInput = phenoInput.replace("MP:0005371"," ");
          phenoInput = phenoInput.replace("MP:0005370"," ");
          phenoInput = phenoInput.replace("MP:0010768"," ");
          phenoInput = phenoInput.replace("MP:0005369"," ");
          phenoInput = phenoInput.replace("MP:0003631"," ");
          phenoInput = phenoInput.replace("MP:0003012"," ");
          phenoInput = phenoInput.replace("MP:0002873"," ");
          phenoInput = phenoInput.replace("MP:0005395"," ");
          phenoInput = phenoInput.replace("MP:0001186"," ");
          phenoInput = phenoInput.replace("MP:0005367"," ");
          phenoInput = phenoInput.replace("MP:0005389"," ");
          phenoInput = phenoInput.replace("MP:0005388"," ");
          phenoInput = phenoInput.replace("MP:0005390"," ");
          phenoInput = phenoInput.replace("MP:0005394"," ");
          phenoInput = phenoInput.replace("MP:0002006"," ");
          phenoInput = phenoInput.replace("MP:0005391"," ");
          while (phenoInput.search("  ") > -1) {
            phenoInput = phenoInput.replace("  "," ");
          }


          window.opener.document.alleleQueryForm.phenotype.value =
            phenoInput + " " + selectedMpIds;
          window.opener.document.getElementById("selectedMpTextDiv").innerHTML = "You selected:<ul>" + selectedMpText + "</ul>";
        }
        else {
          window.opener.document.alleleQueryForm.phenotype.value = selectedMpIds;
          window.opener.document.getElementById("selectedMpTextDiv").innerHTML = "You selected:<ul>" + selectedMpText + "</ul>";
        }
    }

    // cleanup and exit
    window.close();
  }


  $(window).load(function () {

    // If the phenotype text box isn't empty, we need to scan for MP term IDs
    // that may have already been entered.  We then activate those checks.
    if (window.opener.document.alleleQueryForm.phenotype.value != ""){
      var phenoInput = window.opener.document.alleleQueryForm.phenotype.value;

      if (phenoInput.search("MP:0005375") > -1) {
        $("#mp0005375").prop("checked", true);
      }
      if (phenoInput.search("MP:0005386") > -1) {
        $("#mp0005386").prop("checked", true);
      }
      if (phenoInput.search("MP:0005385") > -1) {
        $("#mp0005385").prop("checked", true);
      }
      if (phenoInput.search("MP:0005384") > -1) {
        $("#mp0005384").prop("checked", true);
      }
      if (phenoInput.search("MP:0005382") > -1) {
        $("#mp0005382").prop("checked", true);
      }
      if (phenoInput.search("MP:0005381") > -1) {
        $("#mp0005381").prop("checked", true);
      }
      if (phenoInput.search("MP:0005380") > -1) {
        $("#mp0005380").prop("checked", true);
      }
      if (phenoInput.search("MP:0005379") > -1) {
        $("#mp0005379").prop("checked", true);
      }
      if (phenoInput.search("MP:0005378") > -1) {
        $("#mp0005378").prop("checked", true);
      }
      if (phenoInput.search("MP:0005377") > -1) {
        $("#mp0005377").prop("checked", true);
      }
      if (phenoInput.search("MP:0005397") > -1) {
        $("#mp0005397").prop("checked", true);
      }
      if (phenoInput.search("MP:0005376") > -1) {
        $("#mp0005376").prop("checked", true);
      }
      if (phenoInput.search("MP:0005387") > -1) {
        $("#mp0005387").prop("checked", true);
      }
      if (phenoInput.search("MP:0010771") > -1) {
        $("#mp0010771").prop("checked", true);
      }
      if (phenoInput.search("MP:0005371") > -1) {
        $("#mp0005371").prop("checked", true);
      }
      if (phenoInput.search("MP:0005370") > -1) {
        $("#mp0005370").prop("checked", true);
      }
      if (phenoInput.search("MP:0010768") > -1) {
        $("#mp0010768").prop("checked", true);
      }
      if (phenoInput.search("MP:0005369") > -1) {
        $("#mp0005369").prop("checked", true);
      }
      if (phenoInput.search("MP:0003631") > -1) {
        $("#mp0003631").prop("checked", true);
      }
      if (phenoInput.search("MP:0003012") > -1) {
        $("#mp0003012").prop("checked", true);
      }
      if (phenoInput.search("MP:0002873") > -1) {
        $("#mp0002873").prop("checked", true);
      }
      if (phenoInput.search("MP:0005395") > -1) {
        $("#mp0005395").prop("checked", true);
      }
      if (phenoInput.search("MP:0001186") > -1) {
        $("#mp0001186").prop("checked", true);
      }
      if (phenoInput.search("MP:0005367") > -1) {
        $("#mp0005367").prop("checked", true);
      }
      if (phenoInput.search("MP:0005389") > -1) {
        $("#mp0005389").prop("checked", true);
      }
      if (phenoInput.search("MP:0005388") > -1) {
        $("#mp0005388").prop("checked", true);
      }
      if (phenoInput.search("MP:0005390") > -1) {
        $("#mp0005390").prop("checked", true);
      }
      if (phenoInput.search("MP:0005394") > -1) {
        $("#mp0005394").prop("checked", true);
      }
      if (phenoInput.search("MP:0002006") > -1) {
        $("#mp0002006").prop("checked", true);
      }
      if (phenoInput.search("MP:0005391") > -1) {
        $("#mp0005391").prop("checked", true);
      }



    }

  });

  </script>

</body>
</html>

