<style type="text/css">

.genoLegendButton{
	padding: 2px; 
	max-width:32px; 
	padding-left: 4px; 
	padding-right: 4px;
}

.genoLegendButton{}
.genoLegendAlleleCombo{}
.genoLegendStrain{}
.genoLegendCellLine{}

#genoLegendWrapper{max-width:1000px;}
#genoLegendWrapper .hmGenoButton {
  border-right: 2px solid rgb(209, 109, 0); 
  border-bottom: 2px solid rgb(249, 149, 0);
}
#genoLegendWrapper .htGenoButton {
  border-right: 2px solid rgb(0, 166, 202); 
  border-bottom: 2px solid rgb(0, 166, 202); 
}
#genoLegendWrapper .cxGenoButton {
  border-right: 2px solid rgb(118, 88, 175);
  border-bottom: 2px solid rgb(118, 88, 175);
}
#genoLegendWrapper .cnGenoButton {
  border-right: 2px solid rgb(126, 168, 88); 
  border-bottom: 2px solid rgb(126, 168, 88); 
}
#genoLegendWrapper .tgGenoButton {
  border-right: 2px solid rgb(215, 73, 158); 
  border-bottom: 2px solid rgb(215, 73, 158); 
}
#genoLegendWrapper .otGenoButton {
  border-right: 2px solid #99755A; 
  border-bottom: 2px solid #AC8B72; 
}
</style>

<!-- Genotype Legend Table -->
<div id="genoLegendWrapper">
  <div id="genoLegendDiv"></div>
</div>

<script type="text/javascript">

<%
// Generating the genotype legend data content; this array will be used 
// to make the table, injected at datatable instantiation
%>
jsData = {
  genoLegendData: 
  [
    <c:forEach var="phenoTableGenotype" items="${phenoTableGenotypes}" varStatus="gStatus">
      <c:set var="genotype" value="${phenoTableGenotype.genotype}" scope="request"/>
      <% 
	    // unhappily resorting to scriptlet
		Genotype genotype = (Genotype)request.getAttribute("genotype"); 
	    String allComp = genotype.getCombination1().trim();
		allComp = ntc.convertNotes(allComp, '|');
		allComp = FormatHelper.newline2HTMLBR(allComp.replace("\"", "'"));
		String diseaseModel = new String("");
		if (genotype.hasDiseases()) {
			diseaseModel = new String("&nbsp;Disease Model");
		}
	  %>
      {
       genotypeCol:"<a style='text-decoration: none;' class='genoLink' title='phenotype details' href='${configBean.FEWI_URL}allele/genoview/${phenoTableGenotype.genotype.primaryID}' target='new' onClick=\"javascript:popupGenotype ('${configBean.FEWI_URL}allele/genoview/${phenoTableGenotype.genotype.primaryID}?counter=${phenoTableGenotype.genotypeSeq}', '${phenoTableGenotype.genotypeSeq}'); return false;\" ><span style='font-size:80%;' class='${genotype.genotypeType}Geno ${genotype.genotypeType}GenoButton genoLegendButton' >${phenoTableGenotype.genotype.genotypeType}${phenoTableGenotype.genotypeSeq}</span></a><span><%=diseaseModel%></span>", 
       allCompCol:"<span style='font-size:80%;'> <%=allComp%> </span>", 
       genBackCol:"<span style='font-size:80%;'><%=FormatHelper.superscript(genotype.getBackgroundStrain())%></span>", 
       <c:if test="${not empty genotype.cellLines}">
        cellLineCol:"<span style='font-size:80%;'><%=FormatHelper.superscript(genotype.getCellLines())%></span>"
       </c:if>  
	  }<c:if test="${not gStatus.last}">,</c:if>
    </c:forEach>
  ]
};
	
initGenoLegendTable = function() {

  var genoLegendColDefs = [
    {key:"genotypeCol",  label:"Genotype"},
    {key:"allCompCol", label:"Allelic Composition"},
    {key:"genBackCol", label:"Genetic Background"},
    {key:"cellLineCol", label:"Cell Line(s)"}
  ];

  var genoLegendDataSrc = new YAHOO.util.DataSource(jsData.genoLegendData);
  genoLegendDataSrc.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
  genoLegendDataSrc.responseSchema = 
  {
    fields: ["genotypeCol","allCompCol","genBackCol","cellLineCol"]
  };

  // Instantiate the table object, causing it to display on the page.
  // Large tables will be set to scrollable 
  <c:if test="${phenoTableGenoSize > 4}">
    var genoLegendDataTable = new YAHOO.widget.ScrollingDataTable("genoLegendDiv", genoLegendColDefs, genoLegendDataSrc, {height:"7em"});
  </c:if>
  <c:if test="${phenoTableGenoSize < 5}">
    var genoLegendDataTable = new YAHOO.widget.DataTable("genoLegendDiv", genoLegendColDefs, genoLegendDataSrc, {});
  </c:if>

  return {oDS: genoLegendDataSrc, oDT: genoLegendDataTable};

}();


</script>