<c:set var="sequences" value="${probe.sequences}"/>
<c:if test="${not empty sequences}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Sequences
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> sequenceRibbon">
			<section class="summarySec1 ">
				<c:forEach var="seq" items="${sequences}">
					<div class="sequence">
						${seq.sequenceID}
						(${idLinker.getLinks(seq.sequence.preferredGenBankID)} |
						<a href="${configBean.FEWI_URL}sequence/${seq.primaryID}">MGI Sequence Detail</a>)
					</div>
				</c:forEach>
			</section>
		</div>
	</div>
</c:if>