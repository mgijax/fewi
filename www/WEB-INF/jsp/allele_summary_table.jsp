<%@ page import = "org.owasp.encoder.Encode" %>

<div id="toolbar" class="bluebar" style="">
	<div id="downloadDiv">
		<span class="label">Export:</span>
		<a id="textDownload"  class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
        <a id="excelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a>
	</div>
</div>

<div id="dynamicdata"></div>

<div id="paginationWrap" style="width: 468px; float:right;">
	<div id="paginationBottom">&nbsp;</div>
</div>

<!-- including this file will start the data injection -->
<% String queryString = (String) request.getAttribute("queryString"); %>
<script type="text/javascript">
    window.querystring="<%= Encode.forJavaScript(queryString) %>";
    window.fewiurl="${configBean.FEWI_URL}";
</script>
