<title>MGI-Strains and SNPs</title>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="MGI-Strains and SNPs"
	canonical="${configBean.FEWI_URL}home/strain"
	description="MGI integrates comparative data on inbred strain characteristics including SNPs, polymorphisms, and quantitative phenotypes."
	keywords="MGI, mgi, mice, mouse, murine, mus musculus, genes, genome, genomic, strains, inbred strains, CC Founders, SNPs, single nucleotide polymorphisms, PCR, polymerase chain reactions, RFLP, restriction fragment length polymorphisms, coding SNPs, RefSNPs"
/>

<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css">

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/home/homepages.css">
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/home/strain.css">

 <!-- Latest compiled and minified CSS -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <!-- jQuery library -->
  <!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script> -->
  <!-- Latest compiled JavaScript -->
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script> 


<!-- start body content -->

<style type="text/css">

  div {
  	/*border:1px solid red;*/
  }

  #homePageBodyInsert {
    width:920px;
    margin: 0 auto;
    margin-left: auto;
    margin-right: auto;
    position: relative;
    font-family: Verdana,Arial,Helvetica;
  }

.gridRow {
 display: -webkit-flex;
    -webkit-justify-content: space-evenly; 
    display: flex;
    justify-content: space-evenly;
    padding-top: 5px;
    padding-bottom: 5px;
}

.strainSectionSpacer {
    border: 1px solid #D0E0F0;
    background-color: #D0E0F0;
    height: 25px;
    display: flex; 
    align-items: center;
  }

.blankSectionSpacer {
    border: none;
    background-color: none;
    height: 5px;
  }
  
.collections {
	padding-top: 0px;
	font-size: 90%;
}

.collections li {
	padding-bottom: 2px;
}

.right {
    position:absolute;
    top: 1px;
    right: 20px;
    max-height: 23px;
}

#bottomSection {
    width:100%;
 }
 
.headerDate {
    font-size: 10px;
    font-weight: normal;
    padding-right: 3px;
    float: right;
    margin-top: -20px;
}

</style>

</head>
<body>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<div class="text-center">
  <h3>Strains and SNPs</h3>
  <p><i>MGI integrates comparative data on inbred strain characteristics including SNPs, polymorphisms, and quantitative phenotypes.</i></p> 
</div>

<div class="container">
<div class="row">
  <div class="col-md-7" style="">
  <div class="strainSectionSpacer"><h5>&nbsp;Strain Query <a href="http://www.informatics.jax.org/userhelp/STRAIN_search_help.shtml" onclick="javascript:openUserhelpWindow(&quot;STRAIN_search_help.shtml&quot;); return false;"><img class="right"  src="/assets/images/help_large_transp.gif" alt="Help"></a></h5></div>
  <div class="container">
  <div class="row">
  <div class="col-md-12" style="">
  <div class="strainColumn top"> 
	    <div class="wrapper" style="padding-left: 2px; padding-bottom: 8px;">
	    	<%@ include file="/WEB-INF/jsp/strain/sub_strain_form.jsp" %>
	    </div>
	    </div>
  </div>
  </div>
  </div>
</div>
<div class="col-md-5" style="">
  <div class="strainSectionSpacer"><h5>&nbsp;Strain Collections</h5></div>
  <div class="row">
  <div class="col-md-12" style="">
  <ul class="collections">
			<li><a href="${configBean.FEWI_URL}strain/summary?isSequenced=1" class="homeLink">Wellcome Sanger Institute's Mouse Genomes Project (MGP)</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary?attributes=inbred strain" class="homeLink">Inbred strains</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary?group=HDP" class="homeLink">Hybrid Diversity Panel (HDP)</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary?strainName=CC0*" class="homeLink">Collaborative Cross (CC)</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary?group=DOCCFounders" class="homeLink">DO/CC Founders</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary" class="homeLink">All strains</a></li>
		</ul>
		<div class="row">
 <div class="col-md-11" style="">
  <div class="strainSectionSpacer"><h5>&nbsp;SNPs, Strains &amp; Polymorphisms </h5></div>
  <div class="col-sm-1"></div>
  <div class="wrapper">
	    	<%@ include file="/WEB-INF/jsp/strain/sub_strain_counts.jsp" %>
	    </div>
  </div>
  </div>
</div>
</div>
  </div>
</div>
</div>
<div class="blankSectionSpacer"></div>
<div class="container">
<div class="gridRow">
<div class="row">
<div class="col-md-3" style="">
	<a href="${configBean.FEWI_URL}snp"><img style="width:260px; height:140;" src="${configBean.FEWI_URL}assets/images/static/SNPQueryImage.png" alt="Mouse SNP Query"></a>
	</div>
<div class="col-md-3" style="">
	<a href="${configBean.FEWI_URL}allele"><img style="width:260px; height:140;" src="${configBean.FEWI_URL}assets/images/static/Alleleqf.png" alt="Phenotypes, Alleles & Disease Models Search"></a>
	</div>
<div class="col-md-3" style="">
	<a href="${configBean.WI_URL}mgv"><img style="width:260px; height:140;" src="${configBean.FEWI_URL}assets/images/static/MGVLink.png" alt="MGV"></a>
	</div>
<div class="col-md-3" style="">
	<a href="${configBean.JBROWSE_URL}"><img style="width:260px; height:140;" src="${configBean.FEWI_URL}assets/images/static/JBrowseLink.png" alt="JBrowse"></a>
	</div>
</div>
</div>
<div class="blankSectionSpacer"></div>
<div class="gridRow">
<div class="row">
<div class="col-md-3" style="">
	<a href="http://www.findmice.org/index.jsp"><img style="width:260px; height:140;" src="${configBean.FEWI_URL}assets/images/static/IMSRLink.png" alt="IMSR"></a>
	</div>
<div class="col-md-3" style="">
	 <a href="${configBean.MGIHOME_URL}nomen/strains.shtml"><img style="width:260px; height:140;" src="${configBean.FEWI_URL}assets/images/static/4Strains.png" alt="Nomenclature Guidelines"></a>
	</div>
<div class="col-md-3" style="">
	<a href="${configBean.MGIHOME_URL}submissions/amsp_submission.cgi"><img style="width:260px; height:140;" src="${configBean.FEWI_URL}assets/images/static/SubmitLink.png" alt="Submit Your Data"></a>
	</div>
 <div class="col-md-3" style="">
	<a href="${configBean.FAQ_URL}FAQ.shtml#faq_strain"><img style="width:260px; height:140;" src="${configBean.FEWI_URL}assets/images/static/FAQimage.png" alt="FAQ"></a>
	</div>
</div>
</div>
</div>
<div id="" class="blankSectionSpacer"></div>

<div style="clear:both"></div>

<div class="container">
  <ul class="nav nav-tabs">
    <li><a data-toggle="tab" href="#about">About</a></li>
    <li><a data-toggle="tab" href="#help">Help Documents</a></li>
    <li><a data-toggle="tab" href="#collab">Collaborators</a></li>
    <li class="active"><a data-toggle="tab" href="#links">Other Links</a></li>
  </ul>
  <div class="tab-content">
  <div id="about" class="tab-pane fade">
    <table>
	<tr>
	<td>
		<p><strong>Strains</strong></p>
		<p>Inbred strains of mice represent unique fixed genotypes that  can be repeatedly accessed as homogeneous experimental individuals, with  predictable phenotypes and defined allelic composition. Hundreds of inbred
		strains of mice have been described and new strains continue to be developed,  taking advantage of the rich genetic diversity among the existing strains and  the ease with which the mouse genome can be manipulated.<br /><br />
		
		MGI serves as a  registry for mouse strains worldwide, maintaining the authoritative  nomenclature for existing strains. Comparative data on inbred strain  characteristics, SNPs, polymorphisms, and quantitative
		phenotypes are  integrated with other genetic, genomic, and biological data in MGI.</p>
		
		<p><strong>SNPs (single nucleotide polymorphisms)</strong></p>
		
		<p>MGI provides comprehensive information about reference SNPs including the reference flanking sequence, assays that define the SNP, and gene/marker associations with their corresponding function class annotations. 
		Each SNP detail page includes links to popular gene browsers including the MGI JBrowse Genome Browser.</p>
		
		<p><strong>Other molecular polymorphisms</strong></p>
		
		<p>MGI includes data on <abbr title="Restriction Fragment Length Polymorphism">RFLP</abbr> and PCR based polymorphisms. Probes and restriction enzymes used for <abbr title="Restriction Fragment Length Polymorphism">RFLP</abbr> analysis and primer sequences used for PCR are provided, with fragment sizes and variants for each strain tested. Links to these data can be found in the Polymorphisms section of gene and marker detail pages.</p>
		
		<p><strong>Strain characteristics and historical origins</strong></p>
		<p>MGI holds information on comparative strain characteristics as originally curated by Dr. Michael Festing. These narratives provide key phenotypic traits of major inbred strains, such as behavior, physiology, anatomy,
		drug responses, immunology, infection, and reproduction. The Genealogy of Inbred Strains provides a "pedigree" of relationships of strains since their origin. The Genealogy
		Chart graphically displays the movement and development of inbred strains and is particularly useful in looking at
		dispersion of strains and how inbreeding (and allele fixation) occurred in relation to conserved sequence blocks observed in SNP analysis  Data are fully referenced.</p>
	</td>
	</tr>
</table>
  </div>
  <div id="help" class="tab-pane fade">
    <table>
		<tr><td>
		  Detailed explanations for using Strains, SNPs &amp; Polymorphisms query forms and tools:	
			<ul>
			    <li>Strain Search <a href="${configBean.USERHELP_URL}STRAIN_search_help.shtml" target="_blank">help</a></li>
				<li>SNP Query Form <a href="${configBean.USERHELP_URL}SNP_help.shtml" target="_blank">help</a></li>
				<li>Guidelines for Nomenclature of Mouse and Rat Strains <a href="${configBean.MGIHOME_URL}nomen/strains.shtml" target="_blank">help</a></li>
				<li>Mouse Strain 129 Substrain Nomenclature <a href="${configBean.MGIHOME_URL}nomen/strain_129.shtml" target="_blank">help</a></li>
			</ul>
		</td></tr>
	</table>
  </div>
  <div id="collab" class="tab-pane fade">
    <table>
	<tr>
		<td>
            Collaborators and Data Providers:<br>
            <br>
            Mouse SNP data in MGI are obtained from the <a href='https://www.ncbi.nlm.nih.gov/SNP/snp_summary.cgi' target="_blank">dbSNP</a> resource at NCBI<br>
            <br>
            The registered mouse strains in MGI include strains
            <ul>          
				<li>in public repositories like the International Mouse Strain Resource (<a href="${configBean.IMSRURL}">IMSR</a>)</li>
				<li>held by members of the Federation of International Mouse Resources (FIMRe)</li>
				<li>described as source material for sequences in NCBI <a href="https://www.ncbi.nlm.nih.gov/genbank/" target="_blank">GenBank</a> entries</li>
				<li>submitted by researchers</li>
				<li>included in the scientific literature</li>
            </ul>
    </td>
	</tr>
</table>
  </div>
  <div id="links" class="tab-pane fade in active">
   <table>
	<tr>
		<td>
			<ul id="sendData">
				<li><a href="${configBean.MGIHOME_URL}genealogy/" title="View a chart depicting the origins and relationships of inbred mouse strains." target="_blank">Genealogy Chart of Inbred Strains</a></li>
				<li><a href="${configBean.FTP_URL}datasets/index.html#major_histo" target="_blank">Major Histocompatibility Complex H2 Haplotypes for Strains</a></li>
				<li><a href="${configBean.FTP_URL}datasets/index.html#Moore" target="_blank">Polymorphisms in Microsatellite Markers for 129X1/SvJ vs Other Inbred Strains</a></li>
				<li><a href="${configBean.FTP_URL}reports/index.html#strain" target="_blank">ES Cell Lines Used for Genetic Engineering and their Strain of Origin</a></li>
				<li><a href="https://phenome.jax.org" target="_blank">Mouse Phenome Database</a> (MPD)</li>
				<li><a href="${configBean.MGIHOME_URL}nomen/strain_129.shtml" target="_blank">Mouse Strain 129 Substrain Nomenclature</a></li>
			</ul>
		</td>
		<td>
			<ul>
				<li><a href="http://www.informatics.jax.org/inbred_strains/" target="_blank">Characteristics of Inbred Strains</a> of Mice and Rats by M. Festing</li>
				<li><a href="https://compgen.unc.edu/wp/?page_id=99" target="_blank">Collaborative Cross Project</a> at the University of North Carolina</li> 
                <li><a href="https://www.sanger.ac.uk/sanger/Mouse_SnpViewer/rel-1505" target="_blank">Mouse Genomes Project</a> at the Wellcome Sanger Institute</li>
                <li><a href="https://www.ebi.ac.uk/eva/" target="_blank">European Variation Archive</a> (EVA) at EBI</li>
                <li>Ensembl <a href="https://www.ensembl.org/info/docs/tools/vep/index.html" target="_blank">Variant Effect Predictor</a> (VEP)</li>
			</ul>
		</td>
	</tr>
</table>
  </div>
</div>
</div>

<!--AJAX TABS CALLS-->

<script type="text/javascript" src="${configBean.WEBSHARE_URL}js/ajaxtabs.js">
    /***********************************************
    * Ajax Tabs Content script- © Dynamic Drive DHTML code library (www.dynamicdrive.com)
    * This notice MUST stay intact for legal use
    * Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
    ***********************************************/
</script>

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

<style>
#overDiv table tr td table tr td { background-color: #EFEFEF; }
</style>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
