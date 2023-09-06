	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="referenceRibbonLabel">
			References
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
				<ul>
					<c:if test="${not empty strain.earliestReference}">
						<li>
							<div class="label narrow">Earliest</div>
							<div class="valueNarrow"><a href="${configBean.FEWI_URL}reference/${strain.earliestReference.jnumID}">${strain.earliestReference.jnumID}</a> ${strain.earliestReference.shortCitation}</div>
						</li>
					</c:if>
					<li>
						<div class="label narrow">All</div>
						<div class="valueNarrow"><a href="${configBean.FEWI_URL}reference/strain/${strain.primaryID}?typeFilter=Literature" id="allRefs">${strain.referenceCount}</a></div>
					</li>
				</ul>
		</div>
	</div>