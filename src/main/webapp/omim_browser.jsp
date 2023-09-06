<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.IDLinker" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Human Disease Vocabulary Browser</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  
    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
    IDLinker idLinker = (IDLinker) request.getAttribute("idLinker");
    VocabTerm term;
%>

<style type="text/css">
#wrapper
{
	padding: 0px 20px;
}
#vocabBrowserRow td
{
	width: 30px;
}
#vocabBrowser
{
	margin-bottom:10px;
}
#termsList th,
#termsList td
{
	padding:1px 30px 1px 0px;
}
.bold
{
	font-weight: bold;
}
.small
{
	font-size:80%;
}
</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="VOCAB_omim_browser_help.shtml">
    <div name="centeredTitle">
	  <span class="titleBarMainTitle">Human Disease Vocabulary Browser</span>
	</div>
</div>
<div id="wrapper">
<div id="vocabBrowser">
      <table width="100%"  border="0" cellpadding="0" cellspacing="0" bgcolor="#eeeeee">
		<tr bgcolor="#ffffff"><td colspan="2">The current vocabulary contains human disease, syndrome, and condition terms from Online Mendelian Inheritance in Man 
			<a href="http://www.omim.org/" target="_blank">(OMIM database)</a>.
			<br><hr></td>
		</tr>
        <tr>
          <td colspan="2"><span class="bold">Browse vocabulary terms by beginning character</span></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td colspan="2"><table width="100%"  border="0" cellspacing="0" cellpadding="0">
              <tr id="vocabBrowserRow">
                <td><a href="${configBean.FEWI_URL}vocab/omim/A">A</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/B">B</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/C">C</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/D">D</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/E">E</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/F">F</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/G">G</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/H">H</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/I">I</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/J">J</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/K">K</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/L">L</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/M">M</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/N">N</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/O">O</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/P">P</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/Q">Q</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/R">R</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/S">S</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/T">T</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/U">U</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/V">V</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/W">W</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/X">X</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/Y">Y</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/Z">Z</a></td>
                <td><a href="${configBean.FEWI_URL}vocab/omim/0-9">0-9</a></td>
              </tr>
          </table></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
      </table>
      <hr>
</div>

<span class="bold">Human Diseases/Syndromes Beginning with "${subsetLetter}"</span>
<br/><br/>
<span style="font-style:italic;">To see all annotations for a disease, click the disease name.</span>
<br/><br/>

<div id="termsList">
<table>
	<tr><th>OMIM ID</th><th>Human Disease</th></tr>
	<c:forEach var="term" items="${terms}">
	<% term = (VocabTerm) pageContext.getAttribute("term"); %>
	<tr><td><%= idLinker.getLink("OMIM", term.getPrimaryId()) %></td>
	<td><a href="${configBean.FEWI_URL}disease/${term.primaryId}">
		${term.term}</a>
		<c:if test="${term.diseaseModelCount>0}"><span class="small">(${term.diseaseModelCount} mouse models)</span></c:if>
	</td>
	</tr>
</c:forEach>
</table>
</div>
</div>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
