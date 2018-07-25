<title>MGI-Strains and SNPs</title>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="MGI-Mouse Strains, SNPs, &amp; Polymorphisms"
	canonical="${configBean.FEWI_URL}home/strain"
	description="MGI integrates comparative data on inbred strain characteristics including SNPs, polymorphisms, and quantitative phenotypes."
	keywords="MGI, mgi, mice, mouse, murine, mus musculus, genes, genome, genomic, strains, inbred strains, CC Founders, SNPs, single nucleotide polymorphisms, PCR, polymerase chain reactions, RFLP, restriction fragment length polymorphisms, coding SNPs, RefSNPs"
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

 <!-- Latest compiled and minified CSS -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <!-- jQuery library -->
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
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
    height: 35px;
  }

.blankSectionSpacer {
    border: none;
    background-color: none;
    height: 10px;
  }

.collections {
	padding-top: 0px;
}
.collections li {
	padding-bottom: 2px;
}

.right {float:right}

</style>

</head>
<body>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<div class="text-center">
  <h3>Strains and SNPs</h3>
  <p><i>MGI integrates comparative data on inbred strain characteristics including SNPs, polymorphisms, and quantitative phenotypes.</i></p> 
</div>

<div class="container">
  <div class="col-sm-9" style="">
  <div id="" class="strainSectionSpacer"><h5>Strain Query <a href="http://www.informatics.jax.org/userhelp/STRAIN_search_help.shtml" onclick="javascript:openUserhelpWindow(&quot;STRAIN_search_help.shtml&quot;); return false;"><img class="right"  src="http://www.informatics.jax.org/webshare/images/help_large_transp.gif" alt="Help"></a></h5></div>
  </div>
  <div class="col-sm-3" style="">
  <div id="" class="strainSectionSpacer"><h5>Strain Collections</h5></div>
  </div>
</div>
<div class="container">
<div class="row">
  <div class="col-sm-9" style="">
  <div class="strainColumn top">
	  <section class="infoBlock queryForm">
	    <h2>Find</h2>
	    <div style="padding-left: 0.5em; padding-bottom: 0.5em;">
	    <h3>Strains</h3>
	    <div class="wrapper" style="padding-left: 20px; padding-bottom: 8px;">
	    	<%@ include file="/WEB-INF/jsp/strain/sub_strain_form.jsp" %>
	    </div>
	    </div>
	    </div>
	    </div>
  <div class="col-sm-3" style="">
		<ul class="collections">
			<li><a href="${configBean.FEWI_URL}strain/summary?isSequenced=1" class="homeLink">Wellcome Sanger Institute's <br>Mouse Genomes Project (MGP)</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary?attributes=inbred strain" class="homeLink">Inbred strains</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary?group=HDP" class="homeLink">Hybrid Diversity Panel (HDP)</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary?strainName=CC0*" class="homeLink">Collaborative Cross (CC)</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary?group=DOCCFounders" class="homeLink">DO/CC Founders</a></li>
			<li><a href="${configBean.FEWI_URL}strain/summary" class="homeLink">All strains</a></li>
		</ul>
  </div>
</div>
</div>
<div class="container">
<div class="gridRow">
<div class="col-md-4" style="">
				      <a href="${configBean.FEWI_URL}snp"><img style="width:350px; height:170;" src="${configBean.FEWI_URL}assets/images/static/SNPQueryImage.png" alt="Mouse SNP Query"></a>
				    </div>
				    <div class="col-md-4" style="">
				      <a href="${configBean.FEWI_URL}allele"><img style="width:350px; height:170;" src="${configBean.FEWI_URL}assets/images/static/Alleleqf.png" alt="Phenotypes, Alleles & Disease Models Search"></a>
				    </div>
				    <div class="col-md-4" style="">
				      <a href="http://www.findmice.org/index.jsp"><img style="width:350px;" src="${configBean.FEWI_URL}assets/images/static/IMSRLink.png" alt="IMSR"></a>
				    </div>
				    </div>
<div class="col-md-4" style="">
				       <a href=""${configBean.MGIHOME_URL}nomen/strains.shtml"><img style="width:350px;" src="${configBean.FEWI_URL}assets/images/static/4Strains.png" alt="Nomenclature Guidelines"></a>
				    </div>
				    <div class="col-md-4" style="">
				      <a href="${configBean.MGIHOME_URL}submissions/amsp_submission.cgi"><img style="width:350px;" src="${configBean.FEWI_URL}assets/images/static/SubmitLink.png" alt="Submit Your Data"></a>
				    </div>
				    <div class="col-md-4" style="">
				      <a href="${configBean.FEWI_URL}snp"><img style="width:350px;" src="${configBean.FEWI_URL}assets/images/static/FAQimage.png" alt="FAQ"></a>
				    </div>
				    </div>
				    </div>
<div id="" class="blankSectionSpacer"></div>

<div class="container">
  <div class="col-sm-9" style="">
  <div id="" class="strainSectionSpacer"><h5>SNPs, Strains &amp; Polymorphisms </h5></div>
  <div class="wrapper">
	    	<%@ include file="/WEB-INF/jsp/strain/sub_strain_counts.jsp" %>
	    </div>
  </div>
  <div class="col-sm-3" style="">
  </div>
</div>
 <article class="tabs" style="margin-left:30px; max-width:1164px;">
  	<%@ include file="/WEB-INF/jsp/static/home/strain_footer_tabs.html" %>
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
