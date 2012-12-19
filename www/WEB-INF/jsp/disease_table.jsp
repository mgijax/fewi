<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<style type="text/css">
  .stripe1 {background-color:#FFF;}
  .stripe2 {background-color:#EFEFEF;}
  .arrowRight,.arrowDown {float:right;}
  .leftBorder {border-left: solid 1px #ccc;}
  .genoHeader { text-align:center; padding: 0 2px 2px 2px; }
  .provider { text-align:center; padding: 0px; font-size:9px;}
  #phenoSystemTH {text-align:left; font-size:120%; font-weight:bold;}
  .noDisplay{display:none;}
  .noWrap {white-space: nowrap;}
  th.genoBorder, td.genoBorder{border-left: solid 2px #AAA;}
  th.rightGenoBorder,td.rightGenoBorder{border-right: solid 2px #AAA;}
  th.borderUnder{border-bottom:solid 1px #ccc;}
  td.borderUnder{border-bottom:solid 1px #F8F8F8;}
  th.sexBorder,td.sexBorder{border-left: solid 1px #ccc;}
  .provider img {   display: block;   margin-left: auto;   margin-right: auto; }
#diseasetable_id { border-spacing:0px; border-collapse:collapse; border: 2px solid #AAA;}
#diseasetable_id td { width: 18px; padding: 4px 2px;}
#diseasetable_id .hmGenoButton {
  border-right: 2px solid rgb(209, 109, 0); 
  border-bottom: 2px solid rgb(249, 149, 0);
}
#diseasetable_id .htGenoButton {
  border-right: 2px solid rgb(0, 166, 202); 
  border-bottom: 2px solid rgb(0, 166, 202); 
}
#diseasetable_id .cxGenoButton {
  border-right: 2px solid rgb(118, 88, 175);
  border-bottom: 2px solid rgb(118, 88, 175);
}
#diseasetable_id .cnGenoButton {
  border-right: 2px solid rgb(126, 168, 88); 
  border-bottom: 2px solid rgb(126, 168, 88); 
}
#diseasetable_id .tgGenoButton {
  border-right: 2px solid rgb(215, 73, 158); 
  border-bottom: 2px solid rgb(215, 73, 158); 
}
#diseasetable_id .otGenoButton {
  border-right: 2px solid #99755A; 
  border-bottom: 2px solid #AC8B72; 
}
.blankGeno
{
	border-left: thin solid #DDD;
	border-top: thin solid #DDD;
	border-right: 2px solid #AAA;
  	border-bottom: 2px solid #AAA;
}
.genoButton{padding: 2px;}
</style>
<%@ include file="/WEB-INF/jsp/phenotype_table_geno_imports.jsp" %>

<!-- container table -->
<table class="diseasetable" id="diseasetable_id">

<tr class="stripe1"><th id="phenoSystemTH">&nbsp;</th>
<c:forEach var="diseaseGenotype" items="${genotypes}" varStatus="gStatus">
  <th class="genoHeader genoBorder <c:if test="${gStatus.last}">rightGenoBorder</c:if>">
  <c:set var="genotype" value="${diseaseGenotype.genotype}" scope="request"/>
  <div class="${genotype.genotypeType}Geno ${genotype.genotypeType}GenoButton genoButton">
  <a href='${configBean.FEWI_URL}allele/genoview/${diseaseGenotype.genotype.primaryID}' target="new" 
  class='genoLink small' title='phenotype details'
  onClick="javascript:popupGenotype ('${configBean.FEWI_URL}allele/genoview/${genotype.primaryID}?counter=${diseaseGenotype.genotypeSeq}', '${diseaseGenotype.genotypeSeq}'); return false;">
  ${genotype.genotypeType}${diseaseGenotype.genotypeSeq}</a></div>

  </th>
</c:forEach>
</tr>
<c:forEach var="disease" items="${diseases}" varStatus="dStatus">
    <tr class="${dStatus.index % 2==0 ? ' stripe2' : ' stripe1'}">
      <td class="noWrap borderUnder" style="min-width:250px" >
        <div style="text-align:left;">
           <a class="MP" href='${configBean.JAVAWI_URL}WIFetch?page=humanDisease&id=${disease.omimID}'>
                   ${disease.disease}</a>&nbsp;&nbsp;
           <span style="font-size:80%">OMIM: <a class="MP" href="http://www.omim.org/entry/${disease.omimID}">${disease.omimID }</a></span>
        </div>
      </td>
      <!-- TDs for grid system row will go here -->
      <c:set var="genoID" value="" />
      <c:forEach var="cell" items="${disease.cells}" varStatus="cStatus">
		<td class="<c:if test="${genoID!=cell.genotypeID}">genoBorder </c:if> borderUnder <c:if test="${cStatus.last}">rightGenoBorder</c:if>" style="text-align:center;">
         <c:if test="${cell.hasCall}">
		    	<c:choose>
		    	<c:when test="${cell.callString=='N'}">
		    		<img src="http://www.informatics.jax.org/webshare/images/notSymbol.gif" border="0"/>
		    	</c:when>
		    	<c:otherwise>
  					<c:out value="${cell.callString}" escapeXml="false"/></a>
		    	</c:otherwise>
		    	</c:choose>
  			</c:if>
        </td>
        <c:set var="genoID" value="${cell.genotypeID }"/>
      </c:forEach>
    </tr>
  </c:forEach>
</table>

<!--
<br/>
<a href='${configBean.FEWI_URL}allele/genoview/MGI:2166662' target="new" 
  class='genoLink' title='genotype details'
  onClick="javascript:popupGenotype ('${configBean.FEWI_URL}allele/genoview/MGI:2166662?counter=1', '1'); return false;" >
  EXAMPLE GENO POPUP -- ${configBean.FEWI_URL}/genoview/MGI:2166662
</a>
-->

<script type="text/javascript">
  /* --- specific to genotype popup windows ----------------------------- */
  var popupNextX = 0;	// x position of top-left corner of next popup
  var popupNextY = 0;	// y position of top-left corner of next popup

  // pop up a new window for displaying details from the given 'url' for the
  // given genotype key.
  function popupGenotype (url, counter)
  {
    // new window will be named using the genotype key with a prefix
    var windowName;
    windowName = "genoPopup" + counter;

    // open the window small but scrollable and resizable
    var child = window.open (url, windowName,
	'width=800,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes');

    // move the new window and bring it to the front
    child.moveTo (popupNextX, popupNextY);
    child.focus();

    // set the position for the next new window (at position 400,400 we will
    // start over at 0,0)

    if (popupNextX >= 400) {
	popupNextX = 0;
	popupNextY = 0;
    }
    else {
	popupNextX = popupNextX + 20;
	popupNextY = popupNextY + 20;
    }
    return;
  }
</script>


