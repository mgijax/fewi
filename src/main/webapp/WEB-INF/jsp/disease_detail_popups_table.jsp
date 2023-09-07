<%
	if (dmIt.hasNext()) {
		// the "filter:alpha(opacity=100)" style on this table is to
		// compensate for an IE bug where table borders were showing
		// even once the <div> had been hidden
%>
		  <table id="table<%= dr.getDiseaseRowKey() %>" style="filter:alpha(opacity=100);">
			 <tr><td class="headerStripe allBorders" style="max-width='80%'"><font class="label">Allelic Composition</font></td>
		       <td class="headerStripe allBorders"><font class="label">Genetic Background</font></td>
		       <td class="headerStripe allBorders"><font class="label">Reference</font></td>
		       <td class="headerStripe allBorders"><font class="label">Phenotypes</font></td></tr>
<% 
	    while (dmIt.hasNext()) {
	       dm = dmIt.next();
	       rowCount++;

	       if (dm.getBackgroundStrain().length() > 40) {
	          needsWidthLimit.put ("" + dr.getDiseaseRowKey(), ""); 
	       }
		// style="white-space: nowrap"
%>
<tr><td class="allBorders leftAlign"><%= ntc.useNewWindows(ntc.convertNotes(dm.getAllelePairs(), '|').replace("\n", "<br/>").replace("</sup>", "</span>").replace("<sup>", "<span class='superscript'>")) %></td>
	<td class="allBorders leftAlign"><%= FormatHelper.superscript(dm.getBackgroundStrain()).replace("</SUP>", "</span>").replace("<SUP>", "<span class='superscript'>") %></td>
		  <td class="allBorders leftAlign">
<%
		     refIt = dm.getReferences().iterator();
		     while (refIt.hasNext()) {
		       ref = refIt.next();
%>
		       <a href="<%= fewiUrl %>reference/<%= ref.getJnumID() %>" target="_blank"><%= ref.getJnumID() %></a><%
		       if (refIt.hasNext()) { %>, <%
		       } // if we need a comma
		     } // while more references
%>
		  </td>
		  <td class="allBorders leftAlign"><a href="<%= fewiUrl %>allele/genoview/<%= dm.getGenotypeID() %>" target="_blank">View</a></td></tr>
<% 
	    } // while more disease models
%>
		  </table><!-- models -->
<%
	} // if we have any models in dmIt (may be models or NOT models)
%>
