<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%
   StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
   StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
%>

${templateBean.templateHeadHtml}

<fewi:simpleseo
	title="Phenotype Detail MGI Mouse (${genotype.primaryID})"
	description="${seodescription}"
	keywords="${seokeywords}"
/>

${templateBean.templateBodyStartHtml}

<%@ include file="phenotype_table_geno_popup_imports.jsp" %>

<fewi:pagetitle title="Phenotype Detail" userdoc="ALLELE_detail_pheno_summary_help.shtml#see_annot" />

<%@ include file="phenotype_table_geno_popup_header.jsp" %>

<%@ include file="phenotype_table_geno_popup_content.jsp" %>

${templateBean.templateBodyStopHtml}
