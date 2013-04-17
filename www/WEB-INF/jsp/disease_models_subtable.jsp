	  <c:set var="borders" value="allBorders"/>

<%
	  dmIt = models.iterator();
	  while (dmIt.hasNext()) {
	    model = dmIt.next();

	    if (!dmIt.hasNext()) {
%>
	      <c:set var="borders" value="${sectionBorder}"/>
<%
	    } // end of section; need darker bottom border
%>

	    <tr>${prefix}
	      <td class="${borders} ${stripe} leftAlign">
<%= ntc.convertNotes(model.getAllelePairs(), '|').trim().replace("\n", "<br/>").replace("</sup>", "</span>").replace("<sup>", "<span class='superscript'>") %></td>
	      <td class="${borders} ${stripe} leftAlign">
<%= FormatHelper.superscript(model.getBackgroundStrain()).replace("</SUP>", "</span>").replace("<SUP>", "<span class='superscript'>") %></td>
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
