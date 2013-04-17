<!-- This is a template for displaying the genotype colored box. Requires a 'genotype' object -->
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page trimDirectiveWhitespaces="true" %>


<div class="${genotype.genotypeType}Geno genoBox"></div>