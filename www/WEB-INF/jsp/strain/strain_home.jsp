<%-- Recombinase Home page --%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="MGI-Mouse Strains, SNPs, &amp; Polymorphisms"
	canonical="${configBean.FEWI_URL}home/strain"
	description="MGI integrates comparative data on inbred strain characteristics including SNPs, polymorphisms, and quantitative phenotypes."
	keywords="MGI, mgi, mice, mouse, murine, mus musculus, genes, genome, genomic, strains, inbred strains, SNPs, single nucleotide polymorphisms, PCR, polymerase chain reactions, RFLP, restriction fragment length polymorphisms, coding SNPs, RefSNPs"
/>

<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/home/homepages.css">
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/home/strain.css">


<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/home/homepages.js">
    /***********************************************
    * Ajax Tabs Content script- © Dynamic Drive DHTML code library (www.dynamicdrive.com)
    * This notice MUST stay intact for legal use
    * Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
    ***********************************************/
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<article>

  <section class="strainTitle">
    <h1>Strains, SNPs &amp; Polymorphisms</h1>
    <p>MGI integrates comparative data on inbred strain characteristics including SNPs, polymorphisms, and quantitative phenotypes.
    </p>
  </section>

			<div>
				<table id="strainImages">
					<tr valign="top" align="center">
					<td>
							<a href="${configBean.FAQ_URL}STRN_imgpop.shtml" onClick='javascript:openFaqWindow("STRN_imgpop.shtml"); return false;'>
								<img class="outlineImg" src="${configBean.FEWI_URL}assets/images/static/Strain_mice.gif" height="105">
							</a>
						</td>
						<td>
							<a href="${configBean.FAQ_URL}STRN_imgpop.shtml" onClick='javascript:openFaqWindow("STRN_imgpop.shtml"); return false;'>
								<img class="outlineImg" src="${configBean.FEWI_URL}assets/images/static/Strain_SNP.png" width="146" height="105">
							</a>
						</td>
					</tr>
					<tr valign="top" align="center">
						<td align="center">
							<em><a class="small homeLink" href="${configBean.FAQ_URL}STRN_imgpop.shtml" onClick='javascript:openFaqWindow("STRN_imgpop.shtml"); return false;'>
								Inbred strains C57BL/6J,<br> C3H/HeJ,  A/J and DBA/2J
							</a></em>
						</td>
						<td>
							<em><a class="small homeLink" href="${configBean.FAQ_URL}STRN_imgpop.shtml" onClick='javascript:openFaqWindow("STRN_imgpop.shtml"); return false;'>
								SNP Query Result
							</a></em>
						</td>
					</tr>
				</table>
			</div>

  <div style="clear: both;"></div>

  <div class="strainColumn top">
	  <section class="infoBlock queryForm">
	    <h2>Find</h2>
	    <h3>Strains</h3>
	    <div class="wrapper" style="padding-left: 22px">
	    	<%@ include file="/WEB-INF/jsp/strain/sub_strain_form.jsp" %>
	    </div>

		<h3>Strain Collections</h3>
		<ul>
			<li><a href="${configBean.FEWI_URL}strain/summary?isSequenced=1">Mouse Genome Project (MGP)</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary?attributes=inbred strain">Inbred strains</a></li>
			<li>Hybrid Diversity Panel (HDP)</li>
			<li><a href="${configBean.FEWI_URL}strain/summary?strainName=CC0*">Collaborative Cross (CC)</a></li>
			<li>Parental CC Strains</li>
			<li><a href="${configBean.FEWI_URL}strain/summary">All strains</a></li>
		</ul>
		
		<h3>SNPs</h3>
		Search for mouse SNPs from dbSNP by strain(s), genomic position, or associated genes.<br/>
		<a href="${configBean.FEWI_URL}snp" style="padding-left:22px">SNP Query</a>
	  </section>
  </div><!-- left column -->
  
  <div class="strainColumn top">
	<section class="infoBlock">
    	<h2>Other Data Access</h2>
		<div>
			<dl>
				<dt>Submit data for strains, alleles or phenotypes.</dt>
				<dd><a href="${configBean.MGIHOME_URL}submissions/amsp_submission.cgi" class="small homeLink">Allele, Strain &amp; Phenotype Submission Form</a></dd> 
				
				<dt>View guidelines for proper naming of mouse strains.</dt>
				<dd><a href="${configBean.MGIHOME_URL}nomen/strains.shtml" class="small homeLink">Guide to Strain Nomenclature</a></dd>

				<dt>Search for sources of mutant mice and cell lines.</dt>
				<dd><a href="${configBean.IMSRURL}" class="small homeLink">International Mouse Strain Resource (IMSR)</a></dd>

				<dt>Find information on spontaneous, induced, and genetically-engineered mutations and QTL.</dt>
				<dd><a href="${configBean.HOMEPAGES_URL}phenotypes.shtml" class="small homeLink">Phenotypes, Alleles &amp; Disease Models</a></dd>
			</dl>
		</div>
	</section>
  
	<section class="infoBlock faqs">
	    <h2>FAQs</h2>
	    <div class="wrapper">
	    	<%@ include file="/WEB-INF/jsp/static/home/strain_faqs_section.html" %>
	    </div>
	</section>
	  
	  <div style="clear:both"></div>
	  <section class="infoBlock alleleDataCounts">
	    <h2>Strains, SNPs, &amp; Polymorphisms Includes</h2>
	    <div class="wrapper">
	    	<%@ include file="/WEB-INF/jsp/strain/sub_strain_counts.jsp" %>
	    </div>
	  </section>
	  
  </div><!-- right column -->
  
  <div style="clear:both"></div>

  <article class="tabs">
  	<%@ include file="/WEB-INF/jsp/static/home/strain_footer_tabs.html" %>
  </article>

</article>


<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	// populate stats div
	$.ajax({
		url: fewiurl + 'home/statistics/polymorphisms_mini_home',
		success: function(data) {
			$('#statsDiv').html(data);
		}
	});
</script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/strain/strain_form.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
