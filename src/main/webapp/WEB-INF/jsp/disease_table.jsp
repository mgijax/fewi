<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fe.datamodel.Genotype" %>
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

td.border { border-bottom:thin solid grey; border-top:thin solid grey; border-left:thin solid grey; border-right:thin solid grey }
td.padLR { padding-left:4px; padding-right:4px }
td.padTop { padding-top:4px }
td.padSmall { padding: 2px }
.small {font-size: 80%}
</style>

<%@ include file="/WEB-INF/jsp/phenotype_table_geno_imports.jsp" %>

<table>

	<!-- Key -->
	<tr>
		<td class="rightBorderThinGray padLR" ALIGN="right" WIDTH="1%" style="vertical-align:top;"><span class="label">Key:</span></td><td class="padLR" style="vertical-align:top;">
			<table WIDTH="!" BORDER="0" CELLPADDING="1" CELLSPACING="1" BGCOLOR="#888888">
				<tr>
					<td class="border padSmall" ALIGN="center" BGCOLOR="#FFFFFF"><span class='small'>&#8730;</span></td>
					<td class="border padSmall" BGCOLOR="#FFFFFF" NOWRAP="nowrap"><span class='small'>disease model</span></td>
					<td style="padding-left:1px;padding-right:1px;" border="0" ><span class='small'> &nbsp; </span></td>
					<td class="border padSmall" ALIGN="center" BGCOLOR="#FFFFFF"><img src="${configBean.WEBSHARE_URL}images/notSymbol.gif" border="0" valign="bottom"/></td>
					<td class="border padSmall" BGCOLOR="#FFFFFF" NOWRAP="nowrap"><span class='small'>expected model not found</span></td>
				</tr>
			</table>
		</td>
	</tr>

	<!-- Diseases -->
	<tr>
		<td class="rightBorderThinGray padLR padTop" ALIGN="right" WIDTH="1%" NOWRAP="nowrap" style="vertical-align:top;">
			<font class="label">Models:</font>
		</td>
		<td class="padLR padTop">

			<!-- diseasetable container -->
			<table class="diseasetable" id="diseasetable_id">

				<!-- create genotype headers -->
				<tr class="stripe1"><th id="phenoSystemTH">Human Diseases</th>
					<c:forEach var="diseaseGenotype" items="${genotypes}" varStatus="gStatus">
						<th class="genoHeader genoBorder <c:if test="${gStatus.last}">rightGenoBorder</c:if>">
							<c:set var="genotype" value="${diseaseGenotype.genotype}" scope="request"/>
							<div class="${genotype.genotypeType}Geno ${genotype.genotypeType}GenoButton genoButton">
								<a href='${configBean.FEWI_URL}allele/genoview/${diseaseGenotype.genotype.primaryID}?counter=${diseaseGenotype.genotypeSeq}' target="_blank" 
									class='genoLink small' title='phenotype details'>${genotype.genotypeType}${diseaseGenotype.genotypeSeq}</a>
							</div>
						</th>
					</c:forEach>
				</tr>

				<!-- create disease rows -->
				<c:forEach var="disease" items="${diseases}" varStatus="dStatus">
					<tr class="${dStatus.index % 2==0 ? ' stripe2' : ' stripe1'}">
						<!-- disease -->
						<td class="noWrap borderUnder" style="min-width:300px">
							<div style="text-align:left;">
								<div style="float: left;"><a class="MP" href='${configBean.FEWI_URL}disease/${disease.diseaseID}' target="_blank">${disease.disease}</a></div>
								<div style="float: right;"><span id="show_${fn:replace(disease.vocabTerm.primaryId, ':', '_')}_dialog" class="link">IDs</span></div>
								<%@ include file="disease_table_popup.jsp" %>
							</div>
						</td>
						<c:set var="genoID" value="" />
						<!-- disease/geno table cell-->
						<c:forEach var="cell" items="${disease.cells}" varStatus="cStatus">
							<td class="<c:if test="${genoID!=cell.genotypeID}">genoBorder </c:if> borderUnder <c:if test="${cStatus.last}">rightGenoBorder</c:if>" style="text-align:center;">
								<c:if test="${cell.hasCall}">
									<c:choose>
										<c:when test="${cell.callString=='N'}">
											<img src="${configBean.WEBSHARE_URL}images/notSymbol.gif" border="0"/>
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
	</tr>
</table>

<!-- JavaScript for geno popup -->
<script type="text/javascript">

	/* --- specific to genotype popup windows ----------------------------- */
	var popupNextX = 0;	// x position of top-left corner of next popup
	var popupNextY = 0;	// y position of top-left corner of next popup

	// pop up a new window for displaying details from the given 'url' for the
	// given genotype key.
	function popupGenotype (url, counter, id) {
		// new window will be named using the genotype key with a prefix
		var windowName;
		windowName = "genoPopup_" + id + "_" + counter;

		// open the window small but scrollable and resizable
		var child = window.open (url, windowName, 'width=800,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes');

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
