<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Accession ID Results - MGI</title>

<meta name="description" content="Search Mouse Genome Informatics for genes, genome features, sequences, alleles, human disease, references, ES cell lines, SNPs, probes, clones, and other genomic data using accession ids"> 
<meta name="keywords" content="MGI, mouse, genes, genome features, sequence, allele, disease, reference, cell lines, SNP, ortholog, probe, clone, phenotype, anatomy, gene ontology, GO, MP, anatomical dictionary, AD"> 
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/>
 
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_summary_help.shtml">	
	<span class="titleBarMainTitle">Accession ID Results</span>
</div>

<!-- paginator -->
<table style="width:100%;">
  <tr>
    <td class="paginator">
      <div id="paginationTop">&nbsp;</div>
    </td>
  </tr>
</table>

<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata"></div>

<!-- including this file will start the data injection -->
<script type="text/javascript">
  <%@ include file="/js/accession_summary.js" %>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

