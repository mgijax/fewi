<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_imports.jsp" %>

<% Genotype genotype = (Genotype)request.getAttribute("genotype"); 

	NotesTagConverter ntc = new NotesTagConverter();
%>

<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_header.jsp" %>
<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_content.jsp" %>

