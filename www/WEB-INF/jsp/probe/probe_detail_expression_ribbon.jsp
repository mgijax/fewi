<c:if test="${probe.expressionResultCount > 0}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Expression
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> expressionRibbon">
			<section class="summarySec1 ">
				Gene Expression Data
				(<a href="${configBean.FEWI_URL}gxd/summary?probeKey=${probe.probeKey}">${probe.expressionResultCount}</a> results)
			</section>
		</div>
	</div>
</c:if>