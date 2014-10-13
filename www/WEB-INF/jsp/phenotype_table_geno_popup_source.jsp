<c:set var="showDataSources" value=""/>
<c:forEach var="mpSystem" items="${mpSystems}">
    <c:forEach var="term" items="${mpSystem.terms}">
        <c:forEach var="annot" items="${term.annots}">
	    <c:forEach var="ref" items="${annot.references}">
	   	<c:if test="${ref.hasNonMgiSource}">
		    <c:set var="showDataSources" value="1"/>
		</c:if>
	    </c:forEach>
	</c:forEach>
    </c:forEach>
</c:forEach>
<c:if test="${not empty showDataSources}">
<div style="text-align:center; margin-left:5px; font-size: 80%" onmouseout="nd();" onmouseover="return overlib('Mouse over source labels to view Data Interpretation and Phenotyping Centers for high throughput phenotype annotations.<br><br><b>Data Interpretation Center:</b> The source of Mammalian Phenotype calls made from primary phenotyping data.<br><br><b>Phenotyping Center:</b> The source of primary phenotyping data (where phenotyping tests were performed for annotations shown).', LEFT, WIDTH, 400);">
Data Sources
<br>
<img width="16" height="15" src="${configBean.WEBSHARE_URL}images/help_small_transp.gif">
</div>
</c:if>
