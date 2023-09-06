<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<style type="text/css">
</style>


<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>BLAST Sequences</title>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="recombinase_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">BLAST Sequences</span>
</div>
<!-- end header bar -->

<form method="POST" name="mgiSequences" action="${configBean.NCBI_BLAST_URL}">

Forwarding ${fn:length(sequences)} sequence(s) to NCBI BLAST...
<UL>
<c:forEach var="seq" items="${sequences}">
<LI>${seq.description}</LI>
</c:forEach>
</UL>

If this page does not redirect automatically in 10 seconds, please click
<a href="javascript:mgiSequences.submit()">here</a>.

<input type="hidden" name="PAGE_TYPE" value="${pageType}"/>
<input type="hidden" name="LINK_LOC" value="${linkLoc}"/>
<c:if test="${not empty program}">
<input type="hidden" name="PROGRAM" value="${program}"/>
</c:if><c:if test="${not empty filter}">
<input type="hidden" name="FILTER" value="${filter}"/>
</c:if><c:if test="${not empty repeats}">
<input type="hidden" name="REPEATS" value="${repeats}"/>
</c:if><c:if test="${not empty query}">
<input type="hidden" name="QUERY" value="${query}"/>
</c:if><c:if test="${not empty blastSpec}">
<input type="hidden" name="BLAST_SPEC" value="${blastSpec}"/>
</c:if>
</form>

<script>
window.onload = function() {
    mgiSequences.submit();
}
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
