<c:if test="${not empty probe.references}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			References
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> referenceRibbon">
			<section class="summarySec1 ">
				<c:forEach var="reference" items="${probe.uniqueReferences}">
					<div class="reference">
					<a href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a> 
					${reference.shortCitation}
					</div>
				</c:forEach>
			</section>
		</div>
	</div>
</c:if>