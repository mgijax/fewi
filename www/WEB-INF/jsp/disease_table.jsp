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
.noDisplay{display:none;}
.noWrap {white-space: nowrap;}
th.genoBorder, td.genoBorder{border-left: solid 2px #AAA;}
th.rightGenoBorder,td.rightGenoBorder{border-right: solid 2px #AAA;}
th.borderUnder{border-bottom:solid 1px #ccc;}
td.borderUnder{border-bottom:solid 1px #F8F8F8;}
th.sexBorder,td.sexBorder{border-left: solid 1px #ccc;}
.provider img {   display: block;   margin-left: auto;   margin-right: auto; }
#phenoSystemTH {text-align:left; font-size:120%; font-weight:bold;}
#diseasetable_id { border-spacing:0px; border-collapse:collapse; border: 2px solid #AAA;}
#diseasetable_id td { width: 18px; padding: 4px 2px;}
.genoButton{padding: 2px;}
.yui-skin-sam tr.yui-dt-even { background-color:#FFF; } /* white */
.yui-skin-sam tr.yui-dt-odd { background-color:#f1f1f1; } /* light grey */
</style>

<%@ include file="/WEB-INF/jsp/phenotype_table_geno_imports.jsp" %>

<table>

<!-- Key -->
<tr>
  <td class="rightBorderThinGray" ALIGN="right" WIDTH="1%" style="vertical-align:top;"><span class="label">Key:</span></td><td style="vertical-align:top;"><table WIDTH="!" BORDER="0" CELLPADDING="1" CELLSPACING="1" BGCOLOR="#888888">
    <tr>
      <td style="padding-left:4px;padding-right:4px;" ALIGN="center" BGCOLOR="#FFFFFF" CLASS="small">&#8730;</td>
      <td style="padding-left:4px;padding-right:4px;" BGCOLOR="#FFFFFF" CLASS="small" NOWRAP="nowrap">disease model</td>
      <td style="padding-left:1px;padding-right:1px;" border="0" > &nbsp; </td>
      <td style="padding-left:4px;padding-right:4px;" ALIGN="center" BGCOLOR="#FFFFFF" CLASS="small"><img src="http://www.informatics.jax.org/webshare/images/notSymbol.gif" border="0" valign="bottom"/></td>
      <td style="padding-left:4px;padding-right:4px;" BGCOLOR="#FFFFFF" CLASS="small" NOWRAP="nowrap">expected model not found</td>
    </tr>
    </table>
  </td>
</tr>

<!-- Diseases -->
<tr>
  <td class="rightBorderThinGray" ALIGN="right" WIDTH="1%" NOWRAP="nowrap" style="vertical-align:top;">
    <font class="label">Models:</font>
  </td>
  <td >

  <!-- diseasetable container -->
  <table class="diseasetable" id="diseasetable_id">

  <!-- create genotype headers -->
  <tr class="stripe1"><th id="phenoSystemTH">Human Diseases</th>
  <c:forEach var="diseaseGenotype" items="${genotypes}" varStatus="gStatus">
    <th class="genoHeader genoBorder <c:if test="${gStatus.last}">rightGenoBorder</c:if>">
    <c:set var="genotype" value="${diseaseGenotype.genotype}" scope="request"/>
    <div class="${genotype.genotypeType}Geno ${genotype.genotypeType}GenoButton genoButton">
    <a href='${configBean.FEWI_URL}allele/genoview/${diseaseGenotype.genotype.primaryID}' target="new" 
    class='genoLink small' title='phenotype details'
    onClick="javascript:popupGenotype ('${configBean.FEWI_URL}allele/genoview/${genotype.primaryID}?counter=${diseaseGenotype.genotypeSeq}', '${diseaseGenotype.genotypeSeq}', '${genotype.primaryID}'); return false;">
    ${genotype.genotypeType}${diseaseGenotype.genotypeSeq}</a></div>
    </th>
  </c:forEach>
  </tr>

  <!-- create disease rows -->
  <c:forEach var="disease" items="${diseases}" varStatus="dStatus">
    <tr class="${dStatus.index % 2==0 ? ' stripe2' : ' stripe1'}">
      <!-- disease -->
      <td class="noWrap borderUnder" style="min-width:250px" >
        <div style="text-align:left;">
           <a class="MP" href='${configBean.JAVAWI_URL}WIFetch?page=humanDisease&id=${disease.omimID}'>
                   ${disease.disease}</a>&nbsp;&nbsp;
           <span style="font-size:80%">OMIM: <a class="MP" href="http://www.omim.org/entry/${disease.omimID}">${disease.omimID }</a></span>
        </div>
      </td>
      <c:set var="genoID" value="" />
      <!-- disease/geno table cell-->
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

  </td>
</TR>
</table>





<!-- JavaScript for geno popup -->
<script type="text/javascript">

  /* --- specific to genotype popup windows ----------------------------- */
  var popupNextX = 0;	// x position of top-left corner of next popup
  var popupNextY = 0;	// y position of top-left corner of next popup

  // pop up a new window for displaying details from the given 'url' for the
  // given genotype key.
  function popupGenotype (url, counter, id)
  {
    // new window will be named using the genotype key with a prefix
    var windowName;
    windowName = "genoPopup_" + id + "_" + counter;

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
