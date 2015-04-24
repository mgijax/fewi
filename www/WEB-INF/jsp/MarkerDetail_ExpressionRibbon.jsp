
	<!-- expression row -->
	<c:set var="allenID" value="${marker.allenBrainAtlasID.accID}"/>
	<c:set var="gensatID" value="${marker.gensatID.accID}"/>
	<c:set var="geoID" value="${marker.geoID.accID}"/>
	<c:set var="arrayExpressID" value="${marker.arrayExpressID.accID}"/>
	<c:set var="zfinLinks" value="${marker.expressedInZfinLinks}"/>
	<c:set var="geishaLinks" value="${marker.expressedInChickenLinks}"/>
	<c:set var="xenbaseLinks" value="${marker.expressedInXenBaseLinks}"/>

	<c:if test="${not (empty marker.gxdAssayCountsByType and (marker.countOfGxdLiterature < 1) and (marker.countOfCdnaSources < 1) and empty allenID and empty gensatID and empty geoID and empty arrayExpressID and empty zfinLinks and empty geishaLinks and empty xenbaseLinks)}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				<div id="gxdHeading">
					Expression
				</div>
				<div id="gxdLogo" style="margin-top: auto; text-align: center;">
					<a href="${configBean.HOMEPAGES_URL}expression.shtml"> <img id="gxdLogoImage" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" style='width: 90%;'> </a>
				</div>
			</div>
			<div id="gxd" class="detail <%=rightTdStyles.getNext() %>">
				<c:if test="${marker.countOfGxdLiterature > 0}">
					<div id="gxdLit" style="vertical-align:top">
						Literature Summary: (<a href="${configBean.FEWI_URL}gxdlit/marker/${marker.primaryID}">${marker.countOfGxdLiterature}</a> records)<br/>
					</div>
				</c:if>
				<div id="gxdOther" style="margin-top: 5px;">
					<c:if test="${not empty gxdAssayTypes}">
						Data Summary:
						<c:if test="${marker.countOfGxdResults > 0}">
							Results (<a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}">${marker.countOfGxdResults}</a>)&nbsp;&nbsp;&nbsp;
						</c:if>
						<c:if test="${marker.countOfGxdTissues > 0}">
							Tissues (<a href="${configBean.FEWI_URL}tissue/marker/${marker.primaryID}">${marker.countOfGxdTissues}</a>)&nbsp;&nbsp;&nbsp;
						</c:if>
						<c:if test="${marker.countOfGxdImages > 0}">
							Images (<a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}?tab=imagestab">${marker.countOfGxdImages}</a>)&nbsp;&nbsp;&nbsp;
						</c:if>
						<c:if test="${marker.countOfGxdResults > 0}">
							Tissue x Stage Matrix (<a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}?tab=stagegridtab">view</a>)
						</c:if>
						<br/>
					</c:if>

					<c:if test="${not empty gxdAssayTypes}">
						<c:set var="gxdResultUrl" value="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}?assayType="/>
						<table>
							<tr><td style="padding-left:2em;">Assay Type</td><td>Results</td></tr>
							<c:forEach var="assayType" items="${gxdAssayTypes}">
								<tr><td style="padding-left:4em;padding-right:1em;">${assayType}</td>
									<td align="right"><a href="${gxdResultUrl}${assayType}">${gxdResultCounts[assayType]}</a></td>
								</tr>
							</c:forEach>
						</table>
					</c:if>

					<c:if test="${marker.countOfCdnaSources > 0}">cDNA source data(<a href="${configBean.WI_URL}searches/estclone_report.cgi?_Marker_key=${marker.markerKey}&sort=Tissue">${marker.countOfCdnaSources}</a>)<br/></c:if>

					<c:if test="${not (empty allenID and empty gensatID and empty geoID and empty arrayExpressID and empty zfinLinks and empty geishaLinks and empty xenbaseLinks)}">
		
						<c:if test="${not (empty allenID and empty gensatID and empty geoID and empty arrayExpressID)}">Other mouse links: </c:if>
						<c:if test="${not empty allenID}">
							<a href="${fn:replace (externalUrls.Allen_Brain_Atlas, '@@@@', allenID)}" target="_new">Allen Institute</a>&nbsp;&nbsp;
						</c:if>
						<c:if test="${not empty gensatID}">
							<a href="${fn:replace (externalUrls.GENSAT, '@@@@', gensatID)}" target="_new">GENSAT</a>&nbsp;&nbsp;
						</c:if>
						<c:if test="${not empty geoID}">
							<a href="${fn:replace (externalUrls.GEO, '@@@@', geoID)}" target="_new">GEO</a>&nbsp;&nbsp;
						</c:if>
						<c:if test="${not empty arrayExpressID}">
							<a href="${fn:replace (externalUrls.ArrayExpress, '@@@@', arrayExpressID)}" target="_new">Expression Atlas</a>
						</c:if>
						<br/>
						<c:if test="${not (empty zfinLinks and empty geishaLinks and empty xenbaseLinks)}">
							Other vertebrate links: 
							<c:if test="${not empty geishaLinks}">
								GEISHA 
								<c:forEach var="zf" items="${geishaLinks}" varStatus="status">
									<a href="${zf.url}" target="_new">${zf.displayText}</a><c:if test="${!status.last}">, </c:if>
								</c:forEach>
								<c:if test="${not empty xenbaseLinks}">; </c:if>
							</c:if>
							<c:if test="${not empty xenbaseLinks}">
								Xenbase
								<c:forEach var="zf" items="${xenbaseLinks}" varStatus="status">
									<a href="${zf.url}" target="_new">${zf.displayText}</a><c:if test="${!status.last}">, </c:if>
								</c:forEach>
								<c:if test="${not empty zfinLinks}">; </c:if>
							</c:if>
							<c:if test="${not empty zfinLinks}">
								ZFIN 
								<c:forEach var="zf" items="${zfinLinks}" varStatus="status">
									<a href="${zf.url}" target="_new">${zf.displayText}</a><c:if test="${!status.last}">, </c:if>
								</c:forEach>
							</c:if>
							&nbsp;
							<span style="position: relative; background-color: yellow; border: 1px dashed black; font-weight: bold">&nbsp;<I>NEW</I>&nbsp;</span>
						</c:if>
					</c:if>
				</div>
			</div>
		</div>
	</c:if>
