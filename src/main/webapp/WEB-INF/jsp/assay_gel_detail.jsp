<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.ImageUtils" %>
<%@ page import = "org.jax.mgi.fewi.util.NotesTagConverter" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%
  NotesTagConverter ntc = new NotesTagConverter();
%>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/assay_detail_shared_content.jsp" %>

<%
  // iterate to avoid incorrect right-style
  String tmp = rightTdStyles.getNext();
%>


<!-- --------------- Result Details --------------- -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Results
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
    <!-- --------------- Image Pane ------------------- -->
	<c:if test="${assay.hasGelImage}">
		<c:set scope="page" var="imagepane" value="${assay.gelImagePane}" />
        <% ImagePane imagepane = (ImagePane) pageContext.getAttribute("imagepane"); %>
		<c:choose>
		<c:when test="${not empty imagepane.image.pixeldbNumericID}">
		<div id="imagePaneDiv${paneCount.index}" class="imagePaneWrapper">
			<c:set var="imgurl" scope="page" value="${configBean.FEWI_URL}image/${imagepane.image.mgiID}"/>
			<a href="${imgurl}">
			    <%= ImageUtils.createImagePaneHTML(imagepane,740,300) %></a>
			    <b>Image:</b> 
			    <a href="${imgurl}">
			     <%= FormatHelper.superscript(imagepane.getCombinedLabel()) %></a>
		        <a href="${imgurl}#copyright" class="copySymbol">&copy;</a>
		</div>
	    </c:when>
	    <c:otherwise>
	      	<b>Image:</b> <%= FormatHelper.superscript(imagepane.getCombinedLabel()) %>
	    </c:otherwise>
	    </c:choose>
	      
	</c:if>
	<br/>
	<!-- display Gel Lanes -->
      <table id="assayResultTable">
      <tr class="header">
      	<!-- super headers -->
      	<th>&nbsp;</th><th colspan="2">Sample Information</th>
      	<th colspan="${assay.bandsColSpan}">Bands</th>
      	<th colspan="${assay.otherInfoColSpan}">Other Sample Information</th></tr>
      <tr class="header">
      	<!-- sub headers -->
      	<th>Lane</th>
      	<!-- sample information subheader -->
      	<th>Age</th><th>Structure</th>
      	<!-- bands sub header -->
      	<c:forEach var="gelRow" items="${assay.gelRowCache}">
      		<th>${gelRow.rowSize} 
      		<c:if test="${not empty gelRow.rowNoteLetter}">
	      			<a href="#footnoteID${gelRow.rowNoteLetter}">(${gelRow.rowNoteLetter})</a>
	      		</c:if></th>
      	</c:forEach>
      	<!-- other information sub header -->
      	<c:if test="${assay.hasSampleAmount}"><th>Amount</th></c:if>
      	<th>Genetic Background</th><th>Mutant Allele(s)</th><th>Sex</th>
      	<c:if test="${assay.hasLaneNotes}"><th>Note</th></c:if>
      </tr>
      <c:forEach var="gelLane" items="${assay.gelLanes}" varStatus="gStatus">
      <tr>
      	  <!-- Lane label data -->
	      <td class="header">
	      	<% GelLane gelLane = (GelLane) pageContext.getAttribute("gelLane"); %>
	      	<%= FormatHelper.superscript(gelLane.getLaneLabel()) %> 
	      </td>
      <c:choose>
      	<c:when test="${gelLane.isControl}">
      		<td style="text-align:center;" colspan="${assay.controlLaneColSpan}">${gelLane.controlText}</td>
      	</c:when>
      	<c:otherwise>
      		<c:set var="genotype" value="${gelLane.genotype}" scope="request"/>
		      <% 
			    // unhappily resorting to scriptlet for building genotype string
			    String allCompNoBR = new String();
				Genotype genotype = (Genotype)request.getAttribute("genotype"); 
			    if ( (genotype != null) && (genotype.getCombination3() != null) ) {
		          String allComp = genotype.getCombination3().trim();
		          allComp = ntc.convertNotes(allComp, '|');
		          allCompNoBR = FormatHelper.replaceNewline(allComp.replace("\"", "'"),", ");
		        }
			  %>
	      <!-- sample information data -->
	      <td>${gelLane.age} 
	      <c:if test="${not empty gelLane.ageNoteLetter}">
	      			<a href="#footnoteID${gelLane.ageNoteLetter}">(${gelLane.ageNoteLetter})</a>
	      		</c:if></td>
	      <td>
	      	<c:forEach var="structure" items="${gelLane.structures}" varStatus="strStatus">
	      		<c:if test="${strStatus.index>0}"><br/></c:if>
	      		<a href="${configBean.FEWI_URL}vocab/gxd/anatomy/${structure.primaryId}">${structure.printname}</a>
	      	</c:forEach>
		  </td>
	      <!--  bands data -->
	      <c:forEach var="gelBand" items="${gelLane.bands}" varStatus="bStatus">
	      	<td>${gelBand.strength} 
	      		<c:if test="${not empty gelBand.bandNoteLetter}">
	      			<a href="#footnoteID${gelBand.bandNoteLetter}">(${gelBand.bandNoteLetter})</a>
	      		</c:if></td>
	      </c:forEach>
	      <!-- other sample information data -->
	      <c:if test="${assay.hasSampleAmount}"><td>${gelLane.sampleAmount}</td></c:if>
	      <td> <%=FormatHelper.superscript(genotype.getBackgroundStrain())%></td>
	      <td><%= allCompNoBR %></td><td>${gelLane.sex}</td>
	      <c:if test="${assay.hasLaneNotes}">
    	    <c:set var="lanenoteString" value="${gelLane.laneNote}" scope="request"/>
	    <% String lanenoteString = (String)request.getAttribute("lanenoteString");
	       // quick fix to avoid printing "null"
	       if (lanenoteString == null) { lanenoteString = ""; }
	    %>
	        <td>
	      	<%= FormatHelper.formatVerbatim(lanenoteString) %> 
	        </td>
	      </c:if>
	    </c:otherwise>
      </c:choose>
      </tr>
      </c:forEach>
      <tr>
      </tr>
      </table>

      <!-- print out footnotes -->
      <c:forEach var="footnote" items="${assay.blotNotes}" varStatus="bnStatus">

      	<c:if test="${bnStatus.index==0}"><b>Notes:</b></c:if>
      	<br/>
      	<span id="footnoteID${footnote.footnoteLetter}">(${footnote.footnoteLetter}) 
    	  <c:set var="footnoteString" value="${footnote.note}" scope="request"/>
	      <%= FormatHelper.formatVerbatim((String)request.getAttribute("footnoteString")) %> 
      	</span>
      </c:forEach>

    </td>
  </tr>

<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
