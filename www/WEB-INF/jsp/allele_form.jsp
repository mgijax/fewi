<style type="text/css">
  .relativePos { position:relative; }

  .categoryDiv {
  	display: inline-block;
  	height: 208px;
  	vertical-align: top;
  	border: 2px solid #DFEFFF;
  }
  .categoryHeader {
    height:16px;
    padding:3px;
    position: relative;
  	background-color: #D0E0F0;
  }
  .categorySelection {
    height:180px;
    padding:3px;
    background-color: #F9F9F9;
  }
  .userhelpLink {
    font-size: 12px;
    font-family: Arial,Helvetica;
    font-weight: bold;
  }
  #selectedMpTextDiv ul {
    font-size: 10px;
    font-weight: normal;
  }
</style>

<script>
  function alleleQfReset()
  {
    // clear the main form
    document.getElementById("alleleQueryForm").reset();

    // clear the div containing the selected MP text
   $("#selectedMpTextDiv").html("");

    // reset all check boxes and textareas
    $('#alleleQueryForm').find('input[type=checkbox]:checked').removeAttr('checked');
    $('#alleleQueryForm').find('input[type=text], textarea').val("");

    $("#chromosomeDropList").val("any").change();
    //document.getElementById('chromosomeDropList').reset();
  }

  function updatePhenoSystemSummary()
  {
 	var items = [];
	<c:forEach var="entry" items="${alleleQueryForm.phenoSystemWidgetValues}">
		items.push({key:"${entry.key}",value:"${entry.value}"});
	</c:forEach>

	var phenoInput = $("#phenotype").val();
	// check to see if any exist in the input
	var mpTextLines = [];
	for(var i=0;i<items.length;i++)
	{
		var item = items[i];
		if (phenoInput.search(item.key) > -1)
      	{
			mpTextLines.push("<li>"+item.value+" ("+item.key+")</li>");
        }
		if(mpTextLines.length>0)
		{
			$("#selectedMpTextDiv").html("Categories selected from systems list:<ul>" + mpTextLines.join("") + "</ul>");
		}
		else
		{
			$("#selectedMpTextDiv").html("");
		}
	}
  }
  $(function(){
	  $("#phenotype").change(updatePhenoSystemSummary);
	  updatePhenoSystemSummary();
  });
</script>


<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- query form structural table -->
<form method="GET" action="${configBean.FEWI_URL}allele/summary" id="alleleQueryForm" name="alleleQueryForm">
<table class="queryStructureTable borderedTable pad5 ">

  <!-- form submit/reset params -->
  <tr>
   <td class="" colspan="2" style="background-color: #FFFFFF;">
    <span style="font-style:italic;">
    Search for mutant or genetically engineered alleles, transgenes, or QTL
    variants by phenotype, disease, nomenclature, chromosomal location, or
    allele categories. </span><br/>
    <hr>
    <input class="buttonLabel" value="Search" type="submit">&nbsp;&nbsp;
    <input type="button" value="Reset" onClick="alleleQfReset()">
   </td>
  </tr>

  <!-- phenotypes & disease -->
  <tr class="stripe1">
    <td class="cat1">Mouse phenotypes & <br/>mouse models of <br/>human disease </td>
    <td>
    <div class='relativePos' style='height:125px;'>

      <div style="position:absolute; top:40px; left:0px; ">
        <a class='userhelpLink'
          href="http://www.informatics.jax.org/userhelp/ALLELE_help.shtml#m_phenotypes"
          onclick='javascript:openUserhelpWindow("ALLELE_help.shtml#m_phenotypes"); return false;'
        >Phenotype / Disease:</a>
      </div>

      <div style="position:absolute; top:0px; left:135px; ">

        <textarea id="phenotype" name="phenotype"
        placeholder="Phenotype terms, disease terms, or IDs"
        style="background-color: rgb(238,238,238); margin-left:2px; padding:2px; width: 380px; height: 40px;"
        ><c:out value="${alleleQueryForm.phenotype}"/></textarea>
        <br/>

        <EM><STRONG>Select: </STRONG></EM>
        <a href="javascript:childWindow=window.open('${configBean.FEWI_URL}allele/phenoPopup',
          'mywindow', 'status,width=540,height=400'); childWindow.focus()">Anatomical Systems Affected by Phenotypes</a>
        <br/>

        <EM><STRONG>Browse: </STRONG></EM>
        <A HREF="${configBean.FEWI_URL}vocab/mp_ontology">Full Mammalian Phenotype (MP) Ontology</a>
        <br/>

        <EM><STRONG>Browse: </STRONG></EM>
        <A HREF="${configBean.FEWI_URL}disease">Human Disease Ontology (DO)</a>
        <br/>

        <span class="example" style='line-height:160%;'>
		  <a onclick="javascript:openUserhelpWindow('MISC_boolean_search_help.shtml#boolean_operators'); return false;" href="MISC_boolean_search_help.shtml#boolean_operators">Hints</a>
		   for using AND, OR, AND NOT, quotes, partial word matching,...
		  <br/>Examples: MP:0009754 AND MP:0009751 &nbsp; Alzheimer &nbsp; DOID:178 OR DOID:114 &nbsp; hippocamp* OMIM:601419
		</span>
        <br/>

      </div>

      <div id="selectedMpTextDiv" style="position:absolute; top:0px; left:540px; width:350px;"></div>

    </div>
    </td>
  </tr>

  <!-- nomen & genome location -->
  <tr class="stripe2">
    <td class="cat2">Nomenclature <br/>& genome location </td>
    <td>
    <div class='relativePos' style='height:220px;'>

      <div style="position:absolute; top:8px; left:8px; width:500px;">
        <a class='userhelpLink'
        href="http://www.informatics.jax.org/userhelp/ALLELE_help.shtml#nomenclature"
        onclick='javascript:openUserhelpWindow("ALLELE_help.shtml#nomenclature"); return false;'
        >Gene/Marker, or Allele</a>:
        <span style='margin-left:2px; padding:2px;'>
          <input type="text" size="36" name="nomen" style='margin-left:2px; padding:2px;'
          placeholder="Symbols, names or synonyms" value="<c:out value="${alleleQueryForm.nomen}"/>"></input>
        </span><br/>
        <span class="example" style='margin-left:142px;'>
          Example: Pax6*&nbsp;&nbsp;&nbsp;*Sox2*&nbsp;&nbsp;&nbsp;*Yah&nbsp;&nbsp;&nbsp;*tm1b(KOMP)*
        </span>

      </div>

      <div style="position:absolute; top:95px; left:8px; ">
      <a class='userhelpLink'
      href="http://www.informatics.jax.org/userhelp/ALLELE_help.shtml#chromosome"
      onclick='javascript:openUserhelpWindow("ALLELE_help.shtml#chromosome"); return false;'
      >Chromosome(s)</a>:
      </div>
      <div style="position:absolute; top:60px; left:100px; width:150px;">
      <select id="chromosomeDropList" name="chromosome" multiple="multiple" size="7">
        <fewi:selectOptions items="${alleleQueryForm.chromosomeDisplay}" values="${alleleQueryForm.chromosome}" />
      </select>
      </div>

      <div style="position:absolute; top:60px; left:268px; width:500px;">
      <a class='userhelpLink'
      href="http://www.informatics.jax.org/userhelp/ALLELE_help.shtml#cmp"
      onclick='javascript:openUserhelpWindow("ALLELE_help.shtml#cmp"); return false;'
      >cM Position</a>:
      <br/>
      <span style='margin-left:25px;'>
        between <input type="text" size="15" name="cm" value="<c:out value="${alleleQueryForm.cm}"/>"></input>
      </span>
      <br/>
      <span class="example" style='margin-left:80px;'>
        Example: 10.0-40.0
      </span>

      <br/><br/>
      <a class='userhelpLink'
      href="http://www.informatics.jax.org/userhelp/ALLELE_help.shtml#coordinates"
      onclick='javascript:openUserhelpWindow("ALLELE_help.shtml#coordinates"); return false;'
      >Genome Coordinates</a>: <span class="example">(from GRCm39)</span>
      <br/>
      <span style='margin-left:25px;'>
        between <input type="text" size="23" name="coordinate" value="<c:out value="${alleleQueryForm.coordinate}"/>"></input>
      <select name="coordUnit">
       <fewi:selectOptions items="${alleleQueryForm.coordUnitDisplay}" value="${alleleQueryForm.coordUnit}" />
      </select>
      </span>
      <br/>
      <div class="example" style='margin-left:80px;'>
        Example:<br/>
        125.618-125.622 Mbp <br/>
        1-125618205 bp <br/>
        125618205 - 999999999bp
      </div>
      <br/>
      </div>
    </div>
    </td>
  </tr>


  <!-- allele type, subtype, mutation, and collection -->
  <tr class="stripe1">
    <td class="cat1">Categories</td>
    <td>

    <!-- exclude cell lines -->
    <div style='display: inline;'><i>Limit search by selecting from these options.</i>
    	<label style='margin-left: 20px;'><input name="isCellLine" type="checkbox" value="0" <c:if test="${alleleQueryForm.isCellLine=='0'}">checked="checked"</c:if> /> Exclude alleles existing only as cell lines</label>
    </div>
    <br><br>
    <!-- allele types -->
    <div class='relativePos categoryDiv' style='margin-right: 10px;'>
      <div class='categoryHeader' style="width:232px; " >
      <a class='userhelpLink'
	      href="${configBean.USERHELP_URL}ALLELE_phenotypic_categories_help.shtml#method"
	      onclick="javascript:openUserhelpWindow('ALLELE_phenotypic_categories_help.shtml#method'); return false;"
	      >Generation Method</a>
      </div>
      <div class='categorySelection' style="width:232px; overflow: auto; " >
      	<fewi:checkboxOptions items="${alleleQueryForm.alleleTypeDisplay}" name="alleleType" values="${alleleQueryForm.alleleType}" />
      </div>
    </div>

    <!-- allele subtypes -->
    <div class='relativePos categoryDiv' style='margin-right: 10px;'>
      <div class='categoryHeader' style="width:385px; " >
      <a class='userhelpLink'
	href="${configBean.USERHELP_URL}ALLELE_phenotypic_categories_help.shtml#attributes"
	onclick="javascript:openUserhelpWindow('ALLELE_phenotypic_categories_help.shtml#attributes'); return false;"
	      >Allele Attributes</a>
      </div>
      <div class='categorySelection' style="width:385px; overflow: auto; " >
      	<div style="float: left; margin-right: 20px;">
      		<fewi:checkboxOptions items="${alleleQueryForm.subTypeGroup1}" name="alleleSubType" values="${alleleQueryForm.alleleSubType}" />
      		<br/>
      		<fewi:checkboxOptions items="${alleleQueryForm.subTypeGroup2}" name="alleleSubType" values="${alleleQueryForm.alleleSubType}" />
      	</div>
      	<div>
      		<fewi:checkboxOptions items="${alleleQueryForm.subTypeGroup3}" name="alleleSubType" values="${alleleQueryForm.alleleSubType}" />
      	</div>
      	<div style="clear:both;"></div>
      </div>
    </div>

    <!-- molecular mutations -->
    <div class='relativePos categoryDiv' style='margin-right: 10px;'>
      <div class='categoryHeader' style="width:225px; " >
      <a class='userhelpLink'
	href="${configBean.USERHELP_URL}ALLELE_phenotypic_categories_help.shtml#mutations"
	onclick="javascript:openUserhelpWindow('ALLELE_phenotypic_categories_help.shtml#mutations'); return false;"
	      >Molecular Mutations</a>
      </div>
      <div class='categorySelection' style="width:225px; overflow: auto; " >
      	<fewi:checkboxOptions items="${mutationValues}" name="mutation" values="${alleleQueryForm.mutation}" />
      </div>
    </div>

    <!-- collections -->
    <div class='relativePos categoryDiv'>
      <div class='categoryHeader' style="width:225px; " >
      <a class='userhelpLink'
	href="${configBean.USERHELP_URL}ALLELE_phenotypic_categories_help.shtml#collection"
	onclick="javascript:openUserhelpWindow('ALLELE_phenotypic_categories_help.shtml#collection'); return false;"
	      >Project Collections</a>
      </div>
      <div class='categorySelection' style="width:225px; overflow: auto; " >
      	<fewi:checkboxOptions items="${collectionValues}" name="collection" values="${alleleQueryForm.collection}" />
      </div>
    </div>

    </td>
  </tr>

  <!-- form submit/reset params -->
  <tr>
   <td class="" colspan="2" style="background-color: #FFFFFF;">
    <input class="buttonLabel" value="Search" type="submit">&nbsp;&nbsp;
    <input type="button" value="Reset" onClick="alleleQfReset()">
   </td>
  </tr>

</table>
</form>
