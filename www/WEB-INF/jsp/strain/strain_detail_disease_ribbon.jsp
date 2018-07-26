	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="diseaseRibbonLabel">
			Associated<br/>
			<c:if test="${(not empty strain.diseases) and (not empty strain.gridCells)}">Phenotypes<br/>and<br/>Diseases</c:if>
			<c:if test="${(empty strain.diseases) and (not empty strain.gridCells)}">Phenotypes</c:if>
			<c:if test="${(not empty strain.diseases) and (empty strain.gridCells)}">Diseases</c:if>
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<c:if test="${not empty strain.gridCells}">
				<section id="mpSlimgridSection" class="summarySec1mp">
					<div id="mpSlimgridWrapper" class="sgWrapper">
						<div class="label" style="width: 100%; text-align:center;">Phenotype Overview<img id="sgPhenoHelpImage" src="${configBean.FEWI_URL}assets/images/help_icon_16.png" style="margin-bottom: -3px; margin-left: 3px; cursor: pointer;"/></div><br />
						<div id="sgPhenoHelp" style="visibility: hidden;">
							<div class="hd">Phenotype Overview</div>
							<div class="bd" style="text-align: center">
								Blue squares indicate phenotypes directly attributed to mutations/alleles in this strain.
							</div>
						</div>
						<c:set var="sgID" value="mpSlimgrid"/>
						<c:set var="sgCells" value="${strain.gridCells}"/>
						<c:set var="sgShowAbbrev" value="false"/>
						<c:set var="sgTooltipTemplate" value="<count> annotation(s)"/>
						<c:set var="sgUrl" value="${configBean.FEWI_URL}strain/phenotype/${strain.primaryID}?header=<term>"/>

						<%@ include file="../shared_slimgrid.jsp" %>
						<br/>
						<span style="font-size: 90%">Click cells to view annotations.</span>
					</div>
				</section>
			</c:if>

			<c:if test="${not empty strain.diseases}">
			    <section id="diseaseDiv" class="summarySec1" style="margin-left: 15px">
					<div class="label" style="width: 100%; text-align:center;padding-bottom:1.25em;">Human Diseases</div><br />
				    <table id="diseaseSummaryTable" style="margin-top: 5px">
				    <tr>
				    	<th id="diseaseTableColumnHeader1"></th>
					    <c:forEach var="genotype" items="${strain.diseaseGenotypes}" varStatus="gStatus">
					    	<th><a href="${configBean.FEWI_URL}allele/genoview/${genotype.genotypeID}">model ${gStatus.count}</a></th>
					    </c:forEach>
				    </tr>
				    <c:forEach var="row" items="${strain.diseases}">
				    	<tr>
				    		<td><c:if test="${not empty row.diseaseID}">
				    			<a href="${configBean.FEWI_URL}disease/${row.diseaseID}" target="_blank" title="${row.diseaseID}"><fewi:super value="${row.disease}"/></a>
				    			</c:if></td>
				    		<c:forEach var="genotype" items="${strain.diseaseGenotypes}">
					    		<td class='center'>
					    			<c:choose>
					    			<c:when test="${row.getCellFlag(genotype.genotypeID) == 1}">&#8730;</c:when>
					    			<c:when test="${row.getCellFlag(genotype.genotypeID) == -1}"><img src="${configBean.WEBSHARE_URL}images/notSymbol.gif" border="0" valign="bottom" alt="NOT"/></c:when>
					    			<c:otherwise></c:otherwise>
					    			</c:choose>
					    		</td>
				    		</c:forEach>
				    	</tr>
				    </c:forEach> 
					</table>
					<div id="diseaseLegend">
						<table id="diseaseKey">
							<tr>
							<td>Key</td>
							<td>&#8730;</td>
							<td class="nowrap">disease model</td>
							<td></td>
							<td><img src="${configBean.WEBSHARE_URL}images/notSymbol.gif" border="0" valign="bottom"/></td>
							<td class="nowrap">expected model not found</td>
							</tr>
						</table>
					</div>
			    </section>
			</c:if>
		</div>
	</div>
	<script>
	// fix width of DIV containing table and adjust the header color
	$('#diseaseSummaryTable th').css('background-color', $('#diseaseRibbonLabel').css('background-color'));
	
	// adjust the diseaseDiv's width so it won't wrap as quickly
	$('#diseaseDiv').css('width', Math.max($('#diseaseSummaryTable').width(), $('#diseaseKey').width()));
	$('#diseaseDiv').css('min-width', Math.max($('#diseaseSummaryTable').width(), $('#diseaseKey').width()));

	// set up popup for help icon in pheno grid area
	YAHOO.namespace("mp.container");
	YAHOO.mp.container.phenoHelp = new YAHOO.widget.Panel("sgPhenoHelp", { width:"360px", draggable:false, visible:false, constraintoviewport:true } );
	YAHOO.mp.container.phenoHelp.render();
	YAHOO.util.Event.addListener("sgPhenoHelpImage", "click", YAHOO.mp.container.phenoHelp.show, YAHOO.mp.container.phenoHelp, true);
	</script>