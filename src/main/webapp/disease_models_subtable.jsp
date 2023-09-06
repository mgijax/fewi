	  <c:set var="borders" value="allBorders"/>

<%
dmIt = models.iterator();
while (dmIt.hasNext()) {
  model = dmIt.next();
  pageContext.setAttribute("model", model);
  if (!dmIt.hasNext()) {
%>
    <c:set var="borders" value="${sectionBorder}"/>
<%
  } // end of section; need darker bottom border
%>
<c:set var="rowCount" value="${rowCount + 1}"/>
<c:set var="stripe" value="stripe1"/>
<c:if test="${(rowCount % 2) == 0}">
	<c:set var="stripe" value="stripe2"/>
</c:if>

<tr>${prefix}

	<td class="${borders} ${stripe} leftAlign">
<%= model.getDisease() %></td>

	<td class="${borders} ${stripe} leftAlign">
<%= ntc.convertNotes(model.getAllelePairs(), '|').trim().replace("\n", "<br/>").replace("</sup>", "</span>").replace("<sup>", "<span class='superscript'>") %></td>

	<td class="${borders} ${stripe} leftAlign">
	<c:choose>
		<c:when test="${not empty model.genotype.strainID}">
			<a href="${configBean.FEWI_URL}strain/${model.genotype.strainID}" target="_blank"><%= FormatHelper.superscript(model.getBackgroundStrain()).replace("</SUP>", "</span>").replace("<SUP>", "<span class='superscript'>") %></a>
		</c:when>
		<c:otherwise>
			<%= FormatHelper.superscript(model.getBackgroundStrain()).replace("</SUP>", "</span>").replace("<SUP>", "<span class='superscript'>") %>
		</c:otherwise>
	</c:choose>
	</td>

	<td class="${borders} ${stripe} leftAlign">
<%
                     refIt = model.getReferences().iterator();
                     while (refIt.hasNext()) {
                       ref = refIt.next();
%>
                       <a href="<%= fewiUrl %>reference/<%= ref.getJnumID() %>"
><%= ref.getJnumID() %></a><%
                       if (refIt.hasNext()) { %>, <%
                       } // if we need a comma
                     } // while more references
%>
	</td>

	<td class="${borders} ${stripe} leftAlign">
		<a href="<%= fewiUrl %>allele/genoview/<%= model.getGenotypeID() %>" target="_blank">View</a>
	</td>
</tr>

<c:set var="prefix" value=""/>
<%
} // while more models
%>
