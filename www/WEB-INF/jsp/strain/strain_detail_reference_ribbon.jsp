	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="qtlRibbonLabel">
			References
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<ul>
					<c:if test="${not empty strain.earliestReference}">
						<li>
							<div class="label">Earliest</div>
							<div class="value"><a href="${configBean.FEWI_URL}reference/${strain.earliestReference.jnumID}">${strain.earliestReference.jnumID}</a> ${strain.earliestReference.shortCitation}</div>
						</li>
					</c:if>
					<li>
						<div class="label">All</div>
						<div class="value"><a href="${configBean.FEWI_URL}reference/strain/${strain.primaryID}?typeFilter=Literature" id="allRefs">${strain.referenceCount}</a></div>
					</li>
				</ul>
			</section>
		</div>
	</div>