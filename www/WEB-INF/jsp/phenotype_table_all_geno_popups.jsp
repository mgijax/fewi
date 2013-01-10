<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>

<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_imports.jsp" %>

<c:forEach var="genotypeAssociation" items="${genotypeAssociations}" varStatus="genoStatus">

<div style="overflow:hidden;">
	<!-- Set all the values that the genoview jsp expects -->
	<c:set var="genotype" value="${genotypeAssociation.genotype}" scope="request"/>
	<c:set var="counter" value="${genotypeAssociation.genotypeSeq }"  scope="request"/>
	<c:set var="mpSystems" value="${genotypeAssociation.genotype.MPSystems }"  scope="request"/>
	<c:set var="hasImage" value="${genotypeAssociation.genotype.hasPrimaryImage }"  scope="request"/>
	<c:set var="hasDiseaseModels" value="${not empty genotypeAssociation.genotype.diseases }"  scope="request"/>
	<% Genotype genotype = (Genotype)request.getAttribute("genotype"); 
	  NotesTagConverter ntc = new NotesTagConverter();
	%>
	<!-- Header -->
	<div class='genoPopupHeader'>
<table STYLE="" CELLPADDING="0" CELLSPACING="0">

    <TR STYLE="">

      <TD >
        <a href="javascript:newWindow('${configBean.HOMEPAGES_URL}')" style='margin-left:2px; margin-right:10px;'><img src="http://www.informatics.jax.org/webshare/images/mgi_logo.gif" 
          alt="Mouse Genome Informatics" border="0" width="100"></a>  
      </TD>

      <TD class='genotypeType'>
        <%@ include file="/WEB-INF/jsp/phenotype_table_geno_box.jsp" %>
        <div>
          ${genotype.genotypeType}${counter}
        </div>
      </TD>

      <TD>
        &nbsp;&nbsp;&nbsp;
      </TD>

      <TD class='comboAndStrain'>
        <span class="genotypeCombo">
        <%
        	genotype.getCombination1();
 			ntc.convertNotes(genotype.getCombination1(), '|');       
        %>
		<%=FormatHelper.newline2HTMLBR(ntc.useNewWindows(ntc.convertNotes(genotype.getCombination1(), '|')))%>
        </span>
        <br/>
        <%=FormatHelper.superscript(genotype.getBackgroundStrain())%>
        
        <c:if test="${not empty genotype.cellLines}">
          <br/>
          <br/>
          <span class="cellLines">
          cell line(s): <%=FormatHelper.superscript(genotype.getCellLines())%>
          </span>
        </c:if>  
      </TD>

      <TD>
        <%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_legend.jsp" %>
      </TD>
    </TR>
</table>
	</div>
	
	<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_content.jsp" %>
	<div style="clear: both;"></div>
	</div>
    <br/>
</c:forEach>