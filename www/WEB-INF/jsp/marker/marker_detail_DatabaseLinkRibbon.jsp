	<c:if test="${not empty logicalDBs}">
		<div class="row" id="otherIdsRibbon">
			<div class="header <%=leftTdStyles.getNext() %>">
				Other&nbsp;Database<br/>Links
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<section class="summarySec1">
					<div title="Show Less" class="toggleImage hdCollapse">less</div>
					<ul class="extra open">
						<c:forEach var="item" items="${logicalDBs}">
							<li>
								<div class="label">${item}</div>
								<div class="value">${otherIDs[item]}</div>
							</li>
						</c:forEach>
					</ul>
				</section>
			</div>
		</div>
	</c:if>
