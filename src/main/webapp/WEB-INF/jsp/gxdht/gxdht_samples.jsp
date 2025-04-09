<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>
<title>Samples for Experiment ${experimentID}</title>
<meta http-equiv="X-UA-Compatible" content="chrome=1">
</head><body>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<style>
div.experimentWrapper { border: 1px solid gray; border-collapse: collapse; margin-bottom: 8px; }
div.headerShade2 { background-color: #E2AC00; }
.dataShade1 { background-color: #FFFFFF; }
.dataShade2 { background-color: #F0F0F0; }
.dataShade1m { background-color: #FDF0F0; }
.dataShade2m { background-color: #FCE0E0; }
div.idWrapper { border: 1px solid gray; width: 100%; overflow: auto; }
div.idLabels { width: 100px; text-align: right; float: left; font-weight: bold; }
div.ids { width: 125px; padding-left: 4px; float: left; text-align: left; }
div.title { margin-left: 235px; text-align: left; font-weight: bold; border-right: 1px solid gray; }
#sampleTitle { border: 1px solid gray; background-color: #F2D000; text-align: center; }
table { width: 100%; border-spacing: 0px; border-collapse: collapse; }
th { background-color: #E2AC00; text-align: left; vertical-align: top; border: 1px solid gray; padding: 2px; }
td { background-color: #F0F0F0; text-align: left; vertical-align: top; border: 1px solid gray; padding: 2px; }
td.center { text-align: center; }
a { text-decoration: none; }

.idtable {
    float: left;
    max-width: 200px;
}
.idtable * {
    border: none !important;
    background-color: transparent !important;
}
</style>

<c:set var="aeLink" value="" />
<c:if test="${not empty experiment.arrayExpressID}">
    <c:set var="aeLink" value="${fn:replace(externalUrls.ArrayExpressExperiment, '@@@@', experiment.arrayExpressID)}" />
</c:if>
<c:set var="geoLink" value="" />
<c:if test="${not empty experiment.geoID}">
	<c:set var="geoLink" value="${fn:replace(externalUrls.GEOSeries, '@@@@', experiment.geoID)}" />
</c:if>
<c:set var="atlasLink" value="" />
<c:if test="${experiment.isInAtlas == 1}">
	<c:set var="atlasLink" value="${fn:replace(externalUrls.ExpressionAtlas, '@@@@', experiment.arrayExpressID)}" />
</c:if>
<c:set var="gxdLink" value="" />
<c:if test="${experiment.isLoaded == 1}">
	<c:set var="gxdLink" value="${configBean.FEWI_URL}gxd/experiment/${experiment.arrayExpressID}" />
</c:if>

<c:if test="${empty error}">
  <div id="pageWrapper" class="experimentWrapper dataShade1">
    <div id="experimentWrapper" class="idWrapper headerShade2">
      <c:if test="${not empty experiment}">
            <table class="idtable">
            <tbody>
            <c:if test="${not empty gxdLink}"><tr><td>GXD:</td><td><a href="${gxdLink}">${experiment.arrayExpressID}</a></td></tr> </c:if>
            <c:if test="${not empty atlasLink}"><tr><td>Expression Atlas:</td><td><a href="${atlasLink}" target="_blank" class="extUrl">${experiment.arrayExpressID}</a></td></tr> <br/></c:if>
            <c:if test="${not empty aeLink}"><tr><td>ArrayExpress:</td><td><a href="${aeLink}" target="_blank" class="extUrl">${experiment.arrayExpressID}</a></td></tr> </c:if>
            <c:if test="${not empty geoLink}"><tr><td>GEO:</td><td><a href="${geoLink}" target="_blank" class="extUrl">${experiment.geoID}</a></td></tr> </c:if>
            </tbody>
            </table>
	    <div id="title" class="title">${experiment.title}</div>
      </c:if>
      <c:if test="${empty experiment}">
        Cannot find experiment for ID ${experimentID}.
      </c:if>
    </div>
    <c:if test="${not empty samples}">
      <div id="sampleWrapper">
  	    <div id="sampleTitle">Sample information</div>
  	    <table id="sampleTable">
	      <c:set var="cSpan" value="5"/>
  	      <tr>
  	  	    <th>Name</th>
  	  	    <c:if test="${not empty showOrganism}">
		        <th>Organism</th>
		    </c:if>
  	  	    <th>Age</th>
  	  	    <th>Structure</th>
                    <c:if test="${not empty showCellTypes}">
		        <c:set var="cSpan" value="${cSpan + 1}"/>
  	  	        <th>Cell Type</th>
                    </c:if>
  	  	    <th>Genetic Background</th>
  	  	    <th>Mutant Allele(s)</th>
  	  	    <th>Sex</th>
		    <c:if test="${experiment.method == 'RNA-Seq'}">
		        <c:set var="cSpan" value="${cSpan + 1}"/>
			<th>RNA-Seq Type</th>
		    </c:if>
  	  	    <th>Note</th>
  	      </tr>
  	      <c:forEach var="sample" items="${samples}" varStatus="status">
  	      	<c:set var="match" value=""/>
  	      	<c:if test="${highlightSamples and sample.matchesSearch}">
  	      		<c:set var="match" value="match"/>
  	      	</c:if>
  	        <tr class="${match}">
  	          <td>${sample.name}</td>
  	  	      <c:if test="${not empty showOrganism}"><td>${sample.organism}</td></c:if>
  	  	      <c:if test="${sample.relevancy == 'Yes'}">
  	          	<td>${sample.age}</td>
  	          	<td><c:if test="${not empty sample.theilerStage}">TS${sample.theilerStage}:</c:if> ${sample.structureTerm}</td>
                        <c:if test="${not empty showCellTypes}">
                            <td>${sample.celltypeTerm}</td>
                        </c:if>
  	          	<td><fewi:super value="${sample.geneticBackground}" /></td>
  	          	<td><fewi:allelePairs value="${sample.mutantAlleles}" newWindow="true" /></td>
  	          	<td>${sample.sex}</td>
			<c:if test="${experiment.method == 'RNA-Seq'}">
			    <td>${sample.rnaseqType}</td>
			</c:if>
		      </c:if>
  	  	      <c:if test="${sample.relevancy != 'Yes'}">
				<td colspan="${cSpan}" class="center">${sample.relevancy}</td>
  	          </c:if>
  	          <td><fewi:super value="${sample.note}" /></td>
  	        </tr>
  	      </c:forEach>
  	    </table>
      </div>
    </c:if>
    <c:if test="${empty samples}">
      Cannot find samples for experiment with ID ${experimentID}.
    </c:if>
  </div>
</c:if>
<c:if test="${not empty error}">
  Error: ${error}
</c:if>
<script>
$('table tr:odd td').addClass('dataShade1');
$('.match:odd td').addClass('dataShade2m').removeClass('dataShade1');
$('.match:even td').addClass('dataShade1m').removeClass('dataShade1');
</script>
</body></html>
