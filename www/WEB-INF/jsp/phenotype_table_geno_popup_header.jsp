<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<style>
.genotypeCombo a {text-decoration:none;}
</style>

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
		<fewi:genotype value="${genotype}" newWindow="${true}" />
        </span>
        <br/>
        <fewi:super value="${genotype.backgroundStrain}"/>
        
        <c:if test="${not empty genotype.cellLines}">
          <br/>
          <br/>
          <span class="cellLines">
          cell line(s): <fewi:super value="${genotype.cellLines}"/>
          </span>
        </c:if>  
      </TD>

      <TD>
        <%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_legend.jsp" %>
      </TD>

      <TD>
      <table class="dataSources"><tr><td style="text-align:center">
	<input value=" Print " onClick="window.print();return false;"
	  type="button" class="buttonLabel">
	  </td></tr><tr><td>
	  <%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_source.jsp" %>
	</td></tr></table>
       </TD>

    </TR>
</table>
</div>
