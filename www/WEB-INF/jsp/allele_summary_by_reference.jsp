<%@ include file="/WEB-INF/jsp/allele_summary_header.jsp" %>

<!-- header table -->
<table class="summaryHeader">
<tr >
  <td class="summaryHeaderCat1">
       <b>Reference</b>
  </td>
  <td class="summaryHeaderData1">

    <a style="font-size:x-large;  font-weight: bold; padding-bottom:10px;"
      href="${configBean.FEWI_URL}reference/${reference.jnumID}">
      ${reference.jnumID}
    </a>

    <div style="padding:4px;"> </div>

    ${reference.shortCitation}
  </td>
</tr>
</table>

<div id="summary">
	<div id="rightcolumn" style="float: right;">
			<div class="innertube">
				<div id="paginationTop">&nbsp;</div>
			</div>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/allele_summary_table.jsp" %>

<%@ include file="/WEB-INF/jsp/allele_summary_footer.jsp" %>
