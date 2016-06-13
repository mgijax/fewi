<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ include file="header.jsp" %>

<script>
	// change window title on page load
    document.title = '${pageTitle}';
</script>

</head>

<body style="margin: 8px; min-width: 1px;">

<div id="title">${pageTitle}</div>

<style>
td.tableLabel {
	font-weight: bold;
	font-size: 110%;
	text-align:left;
	vertical-align:middle;
}
table tr td {
	padding: 3px;
	text-align: left;
	vertical-align: middle;
}
td.headerTitle {
	background-color: #dddddd;
	height: 50px;
	white-space: nowrap;
}
td.header {
	height: 150px;
	padding-bottom: 0px;
	text-align: left;
	vertical-align: bottom;
	white-space: nowrap;
	font-weight: normal;
	max-width: 25px;
	min-width: 25px;
	width: 25px;
}
div.header {
	color: black;
    text-indent: 2px;
    width: 27px;
    min-width: 27px;
    max-width: 27px;
    position: relative;
    height: 100%;
    transform: skew(-45deg,0deg);
    -webkit-transform: skew(-45deg,0deg);
    left: 70px;
    overflow: hidden;
    top: 0px;
    border-left: 1px solid #dddddd;
    border-right: 1px solid #dddddd;
    border-top: 1px solid #dddddd;
}
span.header {
	transform: skew(45deg, 0deg) rotate(315deg);
    display: inline-block;
    width: 90px;
    position: absolute;
    text-align: left;
    left: -33px;
    bottom: 25px;
    font-size: 10pt;
    font-weight: bold;
    font-family: Arial, Helvetica;
    color: #666666;
}
td.border {
	border: thin solid black;
}

td.human100 { background-color: #F7861D; }
td.human6 { background-color: #F4A041; }
td.human2 { background-color: #F2BF79; }
td.human1 { background-color: #FBDBB4; }

td.mouse100 { background-color: #0C2255; }
td.mouse6 { background-color: #49648B; }
td.mouse2 { background-color: #879EBA; }
td.mouse1 { background-color: #C6D6E8; }

tr.highlight:hover { background-color: #FFFFCC; cursor: pointer }
</style>

<c:if test="${not empty isPhenotype}">
	<div id="legend">
	  <table id="hdpSystemPopupLegend">
	    <tr>
		  <td>*</td><td>Aspects of the system are reported to show a normal phenotype.</td></tr><tr>
		  <td class="bgsensitive">!</td><td>Indicates phenotype varies with strain background.</td></tr><tr>
		  <td></td><td><span class="highlight">Highlighted Columns</span> contain at least one phenotype or disease result matching your search term(s).</td>
	    </tr>
	  </table>
	</div>
</c:if>

<%@ include file="/WEB-INF/jsp/hmdc/popup_hpo.jsp" %>
<p/>
<%@ include file="/WEB-INF/jsp/hmdc/popup_mp.jsp" %>
<div style="margin-top: 50px">
Debugging Info:<p/>

gridClusterKey: ${gridClusterKey}<br/>
header: ${headerTerm}<br/>
gridKey count: ${gridKeyCount}<br/>
annotation count: ${annotationCount}<br/>
terms: <c:forEach var="term" items="${highlightTerms}">${term}, </c:forEach><br/>
termIds: <c:forEach var="termId" items="${highlightTermIds}">${termId}, </c:forEach><br/>
isPhenotype: ${isPhenotype}<br/>
isDisease: ${isDisease}<br/>
<p/>
omimGroup: ${omimGroup}<br/>
mpGroup: ${mpGroup}<br/>
hpoGroup: ${hpoGroup}<br/>
</div>

<!-- Table and Wrapping div -->

<div id="markerList">
<c:forEach var="marker" items="${markers2}" varStatus="status">
  Find Mice: IMSR strains or lines carrying any ${marker.symbol} Mutation
  <a href='${configBean.IMSRURL}summary?gaccid=${marker.primaryID}&states=ES+Cell&states=embryo&states=live&states=ovaries&states=sperm' target='_blank'>
    (${marker.countForImsr} available)
  </a><br/>
</c:forEach>
</div>

</body>
</html>
