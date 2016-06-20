<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>
<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>

<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_imports.jsp" %>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<c:forEach var="genotype" items="${genoCluster.genotypes}" varStatus="genoStatus">

<div style="overflow:hidden;">
	<!-- Set all the values that the genoview jsp expects -->
	<c:set var="genotype" value="${genotype}" scope="request"/>
	<c:set var="counter" value="${''}"  scope="request"/>
	<c:set var="mpSystems" value="${genotype.MPSystems }"  scope="request"/>
	<c:set var="hasImage" value="${genotype.hasPrimaryImage }"  scope="request"/>
	<c:set var="hasDiseaseModels" value="${not empty genotype.diseases }"  scope="request"/>
	<% Genotype genotype = (Genotype)request.getAttribute("genotype"); 
	  NotesTagConverter ntc = new NotesTagConverter();
	  StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	  StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
	%>
	<%@ include file="../phenotype_table_geno_popup_header.jsp" %>

	<!-- Header -->
<!--
	<div class='genoPopupHeader'>
<table STYLE="" CELLPADDING="0" CELLSPACING="0">

    <TR STYLE="">

      <TD >
        <a href="${configBean.HOMEPAGES_URL}" style='margin-left:2px; margin-right:10px;'><img src="http://www.informatics.jax.org/webshare/images/mgi_logo.gif" 
          alt="Mouse Genome Informatics" border="0" width="100"></a>  
      </TD>

      <TD class='genotypeType'>
      </TD>

      <TD>
        &nbsp;&nbsp;&nbsp;
      </TD>

      <TD class='comboAndStrain'>
        <span class="genotypeCombo">
		<fewi:genotype newWindow="${true}" value="${genotype}" />
        </span>
        <br/>
        <fewi:super value="${genotype.backgroundStrain}" />
        <c:if test="${genotype.isConditional==1}"><br/><br/>Conditional</c:if>
        
        
        <c:if test="${not empty genotype.cellLines}">
          <br/>
          <br/>
          <span class="cellLines">
          cell line(s): <fewi:super value="${genotype.cellLines}" />
          </span>
        </c:if>  
      </TD>

      <TD>
        <%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_legend.jsp" %>
      </TD>
      <TD>
        <%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_source.jsp" %>
      </TD>
    </TR>
</table>
	</div>
-->	
	<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_content.jsp" %>
	<div style="clear: both;"></div>
	</div>
    <br/>
</c:forEach>
<!--<fooscript type="text/javascript" src="${configBean.WEBSHARE_URL}js/mgi_template01.js"></fooscript>-->
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
