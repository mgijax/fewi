<%
  /* code for generating the "View 1 model" style popups for the disease
   * detail page.  This gets included in the disease_detail.jsp page.  It
   * uses a lot of Java scriptlet rather than JSTL, because the
   * interactions with the NotesTagConverter class was difficult to work out.
   */

  /* at this point, we have access to 'disease' (a Disease) which is defined
   * at the top of disease_detail.jsp.
   */

   /*-------------------- variable definitions -------------------- */

   // used to convert markup in allele pairs to be HTML links
   NotesTagConverter ntc = new NotesTagConverter();

   // iterates over DiseaseGroup objects
   Iterator<DiseaseGroup> dgIt = disease.getDiseaseGroups().iterator();

   // one DiseaseGroup from 'dgIt'
   DiseaseGroup dg;

   // iterates over DiseaseRow objects within 'dg'
   Iterator<DiseaseRow> drIt;

   // one DiseaseRow from 'drIt'
   DiseaseRow dr;

   // List of DiseaseModel objects for 'dr'
   List<DiseaseModel> dmList;

   // List of DiseaseModel objects for 'dr' (which are NOT models)
   List<DiseaseModel> notList;

   // iterates over DiseaseModel objects within 'dmList'
   Iterator<DiseaseModel> dmIt;

   // one DiseaseModel object from 'dmIt'
   DiseaseModel dm;

   // iterates over each Reference for 'dm'
   Iterator<Reference> refIt;

   // one Reference object from 'refIt'
   Reference ref;

   // base URL to the fewi instance
   String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

   ArrayList<String> popups = new ArrayList<String>();
   HashMap<String,String> needsHeightLimit = new HashMap<String,String>();
   HashMap<String,String> needsWidthLimit = new HashMap<String,String>();

   /*-------------------- compose the popups -------------------- */

   // walk through disease groups (mouse/human, mouse only, human only, etc.)

   while (dgIt.hasNext()) {
      dg = dgIt.next();

      drIt = dg.getDiseaseRows().iterator();

      // walk through disease rows (one per homology class or per gene that is
      // not part of a homology class)
 
      while (drIt.hasNext()) {
         dr = drIt.next();
	 dmList = dr.getMouseModels();
	 notList = dr.getNotModels();

	 // if this disease row has one or more models, we need a popup

	 if ( ((dmList != null) && (dmList.size() > 0)) ||
		((notList != null) && (notList.size() > 0)) ) {

	   // remember the disease row key for this popup, as we'll need it to
	   // build the Javascript later on for hooking the link up to the
	   // popup

	   popups.add ("" + dr.getDiseaseRowKey());
%>
	   <div id="models<%= dr.getDiseaseRowKey() %>" class="facetFilter; bottomBorder" style="display:none"/>
	      <div class="hd">Mouse Models</div>
	      <div class="bd" style="overflow:auto">
		 <a></a><!-- this empty 'a' tag is to keep Chrome and Safari from putting a selection box aroudn the first link displayed -->
	         <font class="label">Human Disease Modeled:</font> <%= disease.getDisease() %><br/>
<%
		 Iterator<Marker> geneIt = dr.getCausativeMouseMarkers().iterator();
		 if (geneIt.hasNext()) {
		    boolean firstMarker = true;
		    Marker gene;

		    while (geneIt.hasNext()) {
		       gene = geneIt.next();
		       if (firstMarker) {
%>
			  <font class="label">Associated Mouse <%= gene.getMarkerType() %>:</font>
<%
		          firstMarker = false;
		       }
%>
<a href="<%= fewiUrl %>marker/<%= gene.getPrimaryID() %>" target="_blank" class="link"><%= FormatHelper.superscript(gene.getSymbol()).replace("<SUP>", "<span class='superscript'>").replace("</SUP>", "</span>") %></a><%
		       if (geneIt.hasNext()) {
%>, <%
		       } // if we need a comma
		    } // while there are more genes
		 } // if we had any causative genes
	    dmIt = dmList.iterator();

	    int rowCount = 0;
%>
		 <p/>
<%@ include file="/WEB-INF/jsp/disease_detail_popups_table.jsp" %>
<%
	    dmIt = notList.iterator();
	    if (dmIt.hasNext()) {
%>
		 <p/>
	         <font class="label">NOT Models</font><br/>
		 No similarity to the expected human disease phenotype was found.
		 <br/>
<%
	    }
%>

<%@ include file="/WEB-INF/jsp/disease_detail_popups_table.jsp" %>

	      </div><!-- bd -->
	   </div><!-- models<%= dr.getDiseaseRowKey() %> popup -->
<%
	    if (rowCount > 8) {
	        needsHeightLimit.put ("" + dr.getDiseaseRowKey(), "");
	    }
	 } // if there are any disease models
      } // while more disease rows
   } // while more disease groups

   /*-------------------- compose Javascript -------------------- */

   // Now we need to add the necessary Javascript to pull the popup DIV
   // elements into YUI Panels and hook up their corresponding 'view' links.
%>
   <script type="text/javascript">
      YAHOO.namespace("diseaseDetail.container");
      YAHOO.util.Event.onDOMReady(function() {
<%
      Iterator<String> popupIt = popups.iterator();
      String drKey;
      String heightLimit;
      String widthLimit;

      while (popupIt.hasNext()) {
         drKey = popupIt.next();

	 heightLimit = "";
	 widthLimit = "";
	 if (needsHeightLimit.containsKey(drKey)) {
	     heightLimit = "height: \"150px\", ";
	 }
	 if (needsWidthLimit.containsKey(drKey)) {
	     widthLimit = "width: \"750px\", ";
	 }

	 // Note that we assume the 'showX' link (in disease_detail_subtable)
	 // and the 'modelsX' DIV element (above) are implicitly joined by a
	 // common disease row key as 'X'.  We now must make that linkage
	 // explicit in Javascript.
%>
	YAHOO.diseaseDetail.container.panel<%= drKey %> = new YAHOO.widget.Panel ("models<%= drKey %>", { <%= heightLimit %><%= widthLimit %>visible:false, constraintoviewport:true, context:['show<%= drKey %>', 'tl', 'br', ['beforeShow', 'windowResize'] ] } );
	 YAHOO.diseaseDetail.container.panel<%= drKey %>.render();
	 YAHOO.util.Event.addListener("show<%= drKey %>", "click", YAHOO.diseaseDetail.container.panel<%= drKey %>.show, YAHOO.diseaseDetail.container.panel<%= drKey %>, true);
	 YAHOO.util.Event.addListener("YAHOO.diseaseDetail.container.panel<%= drKey %>", "move", YAHOO.diseaseDetail.container.panel<%= drKey %>.forceContainerRedraw);
	 YAHOO.util.Event.addListener("YAHOO.diseaseDetail.container.panel<%= drKey %>", "mouseover", YAHOO.diseaseDetail.container.panel<%= drKey %>.forceContainerRedraw);
	 var elem = document.getElementById("models<%= drKey %>");
	 if (elem != null) {
	    elem.style.display = '';	// make the div visible
	 }
<%
      }
%>
      });
   </script>
