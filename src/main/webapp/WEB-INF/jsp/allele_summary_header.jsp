<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>MGI Allele Summary</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<meta name="description" content="Alleles matching your query">
<meta name="keywords" content="MGI, mouse, allele, disease models">
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/>

<script type="text/javascript" src="${configBean.WEBSHARE_URL}js/hideshow.js"></script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<div id="titleBarWrapper" userdoc="ALLELE_summary_help.shtml">
  <span class="titleBarMainTitle">Phenotypes, Alleles & Disease Models Search</span>
</div>

