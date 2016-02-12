<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "java.util.Map" %>
<%@ page import = "java.util.LinkedHashMap" %>

<%@ page trimDirectiveWhitespaces="true" %>
<fewi:count count="${count}" />
<div id="hideStrainsDiv" style="display: inline-block; padding-left: 435px; padding-bottom: 3px">
<%
    Map<Object,String> strainOptions = new LinkedHashMap<Object, String>();
    strainOptions.put(Boolean.TRUE, "Show only selected strains with alleles");
    strainOptions.put(Boolean.FALSE, "Show all selected strains");
%> 
<fewi:radio name="hideStrains" items="<%= strainOptions %>" value="${snpQueryForm.hideStrains}" cssClass="label" divider="&nbsp;&nbsp;&nbsp;&nbsp;"/><br/>
<I>Drag columns to re-order.</i>&nbsp;&nbsp;<img src="${configBean.WEBSHARE_URL}images/warning2.gif" style="height: 15px"> <i>Column order is reset with Filter or Show option change.</i>
</div>
<div id="exportBar" class="blueBG bottomlessBox">
	<div id="exportButtons" class="padded inlineblock" style="width: 200px">
		<span class="label">Export:</span>
		<a id="snpsToTextFile" class="filterButton" href="${configBean.FEWI_URL}snp/report.txt?${querystring}"><img width="10" height="10" src="${configBean.WEBSHARE_URL}images/text.png"> SNPs to Text File </a> 
	</div>
	<c:if test="${not empty jbrowseLink}">
	<div id="jbrowseDiv" class="padded inlineblock">
		For this genome region: View <a href="${jbrowseLink}" target="_blank">Mouse Genome Browser</a>.
	</div>
	</c:if>
</div>
<table id="snpSummaryTable">
<tr>
	<th class="blueBG" id="snp_id">SNP ID<br/>(${buildNumber})</th>
	<th class="blueBG" id="map_position">Map Position<br/>(${assemblyVersion})</th>
	<th class="blueBG" id="genes">Gene &amp; Category</th>
	<th class="blueBG" id="variation_type">Variation<br/>Type</th>
	<th class="blueBG" id="alleles" style="width: 80px">Allele<br/>Summary<br/>(all strains)</th>
	<c:forEach var="strain" items="${strains}" varStatus="status">
		<c:set var="bgstyle" value="blueBG"/>
		<c:if test="${strain == referenceStrain}">
			<c:set var="bgstyle" value="refBG"/>
		</c:if>
		<th id="${strainHeaders[strain]}" class="snpStrainVerticalHeader ${bgstyle}" style="font-weight: normal; vertical-align: bottom">
			<div class="snpStrainDiv" <fewi:tooltip text="${strain}" superscript="true"/>>
				${fn:replace(fn:replace(strain, "<", "&lt;"), ">", "&gt;")}
			</div>
		</th>
	</c:forEach>
</tr>

<c:forEach var="snp" items="${snps}" varStatus="status">

<% /* after each 25th row, we need to insert a header row (use <td> tags so
    * not draggable)
    */
%>
    <c:if test="${!status.first && (status.index % 25 == 0)}">
	<tr>
	<td class="blueBG bold snpStrainHeader">SNP ID<br/>(${buildNumber})</td>
	<td class="blueBG bold snpStrainHeader">Map Position<br/>(${assemblyVersion})</td>
	<td class="blueBG bold snpStrainHeader">Gene &amp; Category</td>
	<td class="blueBG bold snpStrainHeader">Variation<br/>Type</td>
	<td class="blueBG bold snpStrainHeader" style="width: 80px">Allele<br/>Summary<br/>(all strains)</td>
	<c:forEach var="strain" items="${strains}">
		<c:set var="bgstyle" value="blueBG"/>
		<c:if test="${strain == referenceStrain}">
			<c:set var="bgstyle" value="refBG"/>
		</c:if>
		<td class="snpStrainVerticalHeader ${bgstyle}" style="font-weight: normal; vertical-align: bottom">
			<div class="snpStrainDiv" <fewi:tooltip text="${strain}" superscript="true"/>>
				${fn:replace(fn:replace(strain, "<", "&lt;"), ">", "&gt;")}
			</div>
		</td>
	</c:forEach>
	</tr>
    </c:if>

    <c:set var="shaded" value=""/>
    <c:if test="${status.index % 2 == 1}">
	<c:set var="shaded" value=" class='shaded'"/>
    </c:if>
<tr>
	<td${shaded}>${snp.accid}</td>
	<td${shaded}>${snp.location}</td>
	<td${shaded}>${snp.functionClass}</td>
	<td${shaded}>${snp.variationClass}</td>
	<td${shaded}>${snp.alleleSummary}</td>
	<c:forEach var="strain" items="${strains}">
	    <c:set var="call" value="${snp.alleles[strain]}"/>
	    <c:if test="${not empty call}">
	    <td class="snpCentered allele${fn:replace(fn:replace(call, '-', 'Dash'), '?', 'Q')}${snp.conflicts[strain]}">${call}</td>
	    </c:if>
	    <c:if test="${empty call}">
	    <td${shaded}></td>
	    </c:if>
	</c:forEach>
</tr>
</c:forEach>

<c:if test="${empty snps}">
<tr><td colspan='5'>No SNPs returned</td></tr>
</c:if>

</table>
<script>
$("input[name=hideStrains]").on("change", function() {
	setHideStrains($(this).val());
	resetTableOrder();
	handleNavigation(generateRequest());
});

// line up the hide/show choices with leftmost strain
$("#hideStrainsDiv").css({"padding-left" : $("#alleles").position().left + $("#alleles").width() - $("#dragInstructions").width() })

// adjust the width of the export bar to match the table below
$('#exportBar').attr("style", "width:" + ($('#snpSummaryTable').width() - 2) + "px");

// list of strains as a comma-delimited string (the last is a replace-all)
var displayStrains = '${strains}'.replace('[','').replace(']','').replace(/ /g, '');

// fix the text button to be aware of filter selections
$('#snpsToTextFile').attr('href', $('#snpsToTextFile').attr('href') + getQuerystring() + '&displayStrains=' + displayStrains)
</script>