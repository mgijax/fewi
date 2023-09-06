<%-- Recombinase Home page --%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="MGI-Mouse Recombinase (cre)"
	canonical="${configBean.FEWI_URL}home/recombinase"
	description="MGI collects and annotates expression and activity data for recombinase-containing transgenes and knock-in alleles."
	keywords="MGI, mgi, mice, mouse, murine, mus musculus, genes, genome, genomic, recombinase activity, cre, transgene, knock-in, allele, promoter, driver, DNA recombination, loxP, conditional mutagenesis, knock-out, inducible,construct, specificity pattern, tissue, assay, recombinase reporter, reporter gene, result, staining, specimen, age, system, activity"
/>

<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/home/homepages.css">
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/home/recombinase.css">


<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/home/homepages.js">
    /***********************************************
    * Ajax Tabs Content script- © Dynamic Drive DHTML code library (www.dynamicdrive.com)
    * This notice MUST stay intact for legal use
    * Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
    ***********************************************/
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<article>

  <section class="creTitle">
    <h1>Recombinase (cre) Activity</h1>
    <p>MGI collects and annotates expression and activity data for 
  	  recombinase-containing transgenes and knock-in alleles.
    </p>
  </section>
  <img id="recombinaseIcon" src="${configBean.FEWI_URL}assets/images/static/Recombinase.png">

  <div style="clear: both;"></div>

  <section class="infoBlock accessData">
    <h2>Access Data</h2>
    
    <div class="wrapper">
	    <section>
	      <h3>Find recombinase-carrying alleles
	      	<a href="${configBean.USERHELP_URL}RECOMBINASE_search_help.shtml" target="_blank"><img src="${configBean.WEBSHARE_URL}images/help_icon.png"/></a>
	      </h3>
	      <%@ include file="/WEB-INF/jsp/recombinase/sub_recombinase_form.jsp" %>
	    </section>
	    
	    <section style="clear: both">
		  <hr class="hrblue"/>
	      <h3>Retrieve all alleles</h3>
	      <%@ include file="/WEB-INF/jsp/static/home/recombinase_access_data_section2.html" %>
	      <hr class="hrblue"/>
	    </section>
	    
	    <section>
	      <h3>Related searches</h3>
	      <%@ include file="/WEB-INF/jsp/static/home/recombinase_access_data_section3.html" %>
	    </section>
    </div>
  </section>
  
  <div class="column">
  
	  <section class="infoBlock faqs">
	    <h2>FAQs</h2>
	    <div class="wrapper">
	    	<%@ include file="/WEB-INF/jsp/static/home/recombinase_faqs_section.html" %>
	    </div>
	  </section>
	  
	  <section class="infoBlock alleleDataCounts">
	    <h2>Recombinase Allele Data Include</h2>
	    <div class="wrapper">
	    	<%@ include file="/WEB-INF/jsp/recombinase/sub_recombinase_counts.jsp" %>
	    </div>
	  </section>
	  
  </div>
  
  <div style="clear:both"></div>

  <section class="infoBlock projectNews">
    <h2>Recombinase Project News</h2>
    <div class="wrapper">
    	<%@ include file="/WEB-INF/jsp/static/home/recombinase_project_news_section.html" %>
    </div>
  </section>
  
  <article class="tabs">
  	<%@ include file="/WEB-INF/jsp/static/home/recombinase_footer_tabs.html" %>
  </article>

</article>


<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	// populate stats div
	$.ajax({
		url: fewiurl + 'home/statistics/recombinase_mini_home',
		success: function(data) {
			$('#statsDiv').html(data);
		}
	});
</script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/recombinase/recombinase_form.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
