        <style>
        #otherIdsRibbon table.extra {
            margin-left: 65px;
        }
        #otherIdsRibbon table.extra td {
            padding-right: 8px;
        }
        </style>
	<c:if test="${not empty logicalDBs}">
		<div class="row" id="otherIdsRibbon">
			<div class="header <%=leftTdStyles.getNext() %>">
				Other&nbsp;Database<br/>Links
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<section class="summarySec1">
					<div id="odbToggle" title="Show Less" class="toggleImage hdCollapse">less</div>
                                        <table class = "extra" >
                                        <tbody>
                                            <c:forEach var="item" items="${logicalDBs}">
                                                <tr>
                                                    <td class="label">${item}</td>
                                                    <td class="value">${otherIDs[item]}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                        </table>
				</section>
			</div>
		</div>
	</c:if>
