<c:if test="${probe.expressionResultCount > 0}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Expression
			<div id="gxdLogo">
				<a href="${configBean.MGIHOME_URL}homepages/expression.shtml" style="background-color: transparent"> <img id="gxdLogoImage" src="${configBean.WEBSHARE_URL}images/gxd_logo.png"></a>
			</div>
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> expressionRibbon">
			<section class="summarySec1" style="padding-top: 31px">
				<ul>
					<li>
						<div class="label">Assay Results</div>
						<div class="value">
							<a href="${configBean.FEWI_URL}gxd/summary?probeKey=${probe.probeKey}">${probe.expressionResultCount}</a>
						</div>
					</li>
			</section>
		</div>
	</div>
</c:if>