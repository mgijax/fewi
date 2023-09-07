<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Glossary Index</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
h3 
{
	display: block;
	font-size: 1.17em;
	margin:1em;
	font-weight: bold;
}
li
{
	margin:0.5em;
}
</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper">
    <div name="centeredTitle">
	  <span class="titleBarMainTitle">MGI Glossary</span>
	</div>
</div>

<div id="glossayInfo">
<hr>
<P>This glossary provides definitions for some terms found in our help
documentation and other terms that are useful in understanding mouse genetics
and bioinformatics. Terms are defined in a general sense as they apply to
eukaryotic genetics, particularly mouse genetics; some terms are also defined
as they are specifically used in MGI (<I>e.g.</I>,
<a href="${configBean.FEWI_URL}glossary/gene">gene</a>).
This list will grow as time goes on. If there is a term you think should be
included in this list, please contact
<a href="${configBean.MGIHOME_URL}support/mgi_inbox.shtml">MGI User Support</a>.
There are a number of useful online glossaries in addition to this one:

<UL>
  <li>
The
<a href="${configBean.FEWI_URL}vocab/gxd/anatomy">
Mouse Developmental Anatomy Browser</a>
displays anatomical terms of the mouse in a hierarchical manner for a series
of developmental stages, using standard anatomical nomenclature.
  </li>
  <li>
The
<a href="${configBean.FEWI_URL}vocab/gxd/ma_ontology">
Adult Mouse Anatomy Browser</a> provides standardized
nomenclature for anatomical structures in the postnatal mouse
(Theiler stage 28).
  </li>
  <li>
The
<a href="http://www.geneontology.org">Gene Ontology
definitions</a> page provides current definitions of all terms used
in the Gene Ontology Project.
  </li>
  <li>
The
<a href="https://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=MeSH">MeSH Browser</a>
at <a href="${configBean.FEWI_URL}glossary/ncbi">NCBI</a>
contains useful definitions of many terms, organized as a hierarchical
<a href="${configBean.FEWI_URL}glossary/controlled_vocabulary">controlled vocabulary</a>.
  </li>
  <li>
The
<a href="https://www.ncbi.nlm.nih.gov/Education/BLASTinfo/glossary2.html">
BLAST Glossary</a> at NCBI defines terms used in searching sequence databases.
  </li>
  <li>
The
<a href="http://www.weihenstephan.de/~schlind/genglos.html">
Hypermedia Glossary of Genetic Terms</a> by Birgid Schlindwein contains over
600 genetic terms.
  </li>
</UL>

If you find an online glossary that you think should be included here, please contact
<a href="${configBean.MGIHOME_URL}support/mgi_inbox.shtml">MGI User Support</a>.
<hr>
</div>
<div id="glossaryBrowser">
<h3 align=center>
<a href="#A">A</a>&nbsp;&nbsp;<a href="#B">B</a>&nbsp;&nbsp;
<a href="#C">C</a>&nbsp;&nbsp;<a href="#D">D</a>&nbsp;&nbsp;
<a href="#E">E</a>&nbsp;&nbsp;<a href="#F">F</a>&nbsp;&nbsp;
<a href="#G">G</a>&nbsp;&nbsp;<a href="#H">H</a>&nbsp;&nbsp;
<a href="#I">I</a>&nbsp;&nbsp;<a href="#J">J</a>&nbsp;&nbsp;
<a href="#K">K</a>&nbsp;&nbsp;<a href="#L">L</a>&nbsp;&nbsp;
<a href="#M">M</a>&nbsp;&nbsp;<a href="#N">N</a>&nbsp;&nbsp;
<a href="#O">O</a>&nbsp;&nbsp;<a href="#O">P</a>&nbsp;&nbsp;
<a href="#Q">Q</a>&nbsp;&nbsp;<a href="#R">R</a>&nbsp;&nbsp;
<a href="#S">S</a>&nbsp;&nbsp;<a href="#T">T</a>&nbsp;&nbsp;
<a href="#U">U</a>&nbsp;&nbsp;<a href="#V">V</a>&nbsp;&nbsp;
<a href="#W">W</a>&nbsp;&nbsp;<a href="#X">X</a>&nbsp;&nbsp;
<a href="#Y">Y</a>&nbsp;&nbsp;<a href="#Z">Z</a>&nbsp;&nbsp;
</h3>
<p align=center>Click on a letter above to jump to that section of the MGI Glossary.</p>
<hr>
</div>

<div id="glossaryTermsList">
<ul>
<c:set var="letter" value="" />
<c:forEach var="glossaryTerm" items="${glossaryTerms}" varStatus="status">
<c:if test="${letter!=glossaryTerm.firstLetter}">
	<c:set var="letter" value="${glossaryTerm.firstLetter}" />
	<h3 id="${letter}">${letter}</h3>
</c:if>
<li><a href="${configBean.FEWI_URL}glossary/${glossaryTerm.glossaryKey}">${glossaryTerm.displayName}</a></li>
</c:forEach>
</ul>
</div>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
