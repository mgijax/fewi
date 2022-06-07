<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<c:choose>
  <c:when test="${empty alleleSystems}">
    <span>MGI has not yet included tissue activity data for this allele in any anatomical systems.</span>
  </c:when>
  <c:otherwise>

<%
NotesTagConverter ntc = new NotesTagConverter(); 
%>

<!-- Pull in the Pretty Good Grid -->
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/pgg.css">
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/pgg.js"></script>

<!-- A place to draw the table -->
<div id="recombinaseTableWrapper" style="padding-top: 40px;"></div>

<!-- Legend contents.
     Define as a template, then instantiate during popup initialization. -->
<template id="recombinaseTableLegendPopup">
    <h3>Number of results annotated as:</h3>

    <div class='section'>
            <h4>present in structure and/or substructures</h4>
            <div class='swatch' >
                    <div class='pgg-cell b3'><div class="decorator" /></div>
                    <span class='caption'> &gt; 50 </span>
            </div>
            <div class='swatch m1'>
                    <div class='pgg-cell b2'><div class="decorator" /></div>
                    <span class='caption'> 5-50 </span>
            </div>
            <div class='swatch m2' >
                    <div class='pgg-cell b1'><div class="decorator" /></div>
                    <span class='caption'> 1-4 </span>
            </div>
    </div>

    <div class='section'>
            <h4>absent in structure</h4>
            <div class='swatch' >
                    <div class='pgg-cell r3'><div class="decorator" /></div>
                    <span class='caption'> &gt; 20 </span>
            </div>
            <div class='swatch m1'>
                    <div class='pgg-cell r2'><div class="decorator" /></div>
                    <span class='caption'> 5-20 </span>
            </div>
            <div class='swatch m2'>
                    <div class='pgg-cell r1'><div class="decorator" /></div>
                    <span class='caption'> 1-4 </span>
            </div>
    </div>


    <div class="section">
        <h3>Other Symbols</h3>

        <div class="swatch">
                <div class='pgg-cell b2r3'><div class="decorator" /></div>
                <span class='caption'> structure has both present and absent results</span> <br/>
        </div>

        <div class="swatch">
                <div class='pgg-cell b2g'><div class="decorator" /></div>
                <span class='caption'> structure has both present and absent and/or ambiguous results in substructures</span> <br/>
        </div>

        <div class='swatch'>
                <div class='pgg-cell gold-corner'><div class="decorator" /></div>
                <span class='caption'> only absent or ambiguous results in substructures</span> <br/>
        </div>

        <div class='swatch'>
                <div class='pgg-cell gray-sash'><div class="decorator" /></div>
                <span class='caption'> ambiguous in structure </span>
        </div>

        <div class='swatch' >
                <div class='pgg-cell empty-circle'><div class="decorator" /></div>
                <span class='caption'> tissue exists in this age range but has no annotations
                </span>
        </div>
        
        <div class='swatch' >
                <div class='pgg-cell empty'><div class="decorator" /></div>
                <span class='caption'> tissue does not exist in this age range</span>
        </div>
    </div>

    <div class="section">
        <h3>Note</h3>
        <div>Click on a grid cell to access annotation details and images.</div>
    </div>
</template>

<!-- Define cell popup contents as a template, then instantiate during popup initialization.
     When cell is clicked, the context string and the table tbody are filled in by the click handler.
-->
<template id="recombinaseTableCellPopup">
  <div>
    <div style="text-align:center; font-size: 110%; line-height: 2; font-weight: bold; margin-botton:5px;">
        Recombinase activity in <br /><span class="context-string"></span>
    </div>
    <table class="cell-data-table">
        <thead>
            <tr><th>Detected?</th><th># of Results</th></tr>
        </thead>
        <tbody>
        </tbody>
    </table>
    <a  class="view-results-button"
        href="${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=">
        <button>View All Result Details and Images</button>
    </a>
  </div>
</template>

<script type="text/javascript">

 // -------------------------------------------------------------------
 // Build up the data structure
 var recombinaseTableRowData = [] 
 var alleleSystem = null

 <c:forEach var="alleleSystem" items="${alleleSystems}" >
  alleleSystem = {
      name: "${alleleSystem.system}",
      cellData: JSON.parse('${alleleSystem.cellData}'),
      children: []
  }
  <c:forEach var="structure" items="${alleleSystem.recombinaseSystemStructures}">
      alleleSystem.children.push({
          name: "${structure.structure}",
          cellData: JSON.parse('${structure.cellData}')
      })
  </c:forEach>
  recombinaseTableRowData.push(alleleSystem)
 </c:forEach>
 console.log("Recombinase table data:", recombinaseTableRowData)

 // -------------------------------------------------------------------
 // Define the cell renderer function that turns a cell's data object into
 // a class name (defined in pgg.css)
 function cellData2class (cd) {
     const blueLevel = cd.d == 0 ? 0 : (cd.d <= 4 ? 1 : (cd.d<= 50 ? 2 : 3)) 
     const redLevel  = cd.nd == 0 ? 0 : (cd.nd <= 4 ? 1 : (cd.nd<= 20 ? 2 : 3)) 
     if (blueLevel) {
         if (redLevel) {
             return 'b' + blueLevel + 'r' + redLevel
         } else if (cd.ndd) {
             return 'b' + blueLevel + 'g'
         } else {
             return 'b' + blueLevel
         }   
     } else if (redLevel) {
         return 'r' + redLevel
     } else if (cd.ndd) {
         return 'gold-corner'
     } else if (cd.amb) {
         return 'gray-sash'
     } else if (cd.sv) {
         return 'empty-circle'
     } else {
         return 'empty'
     }   
 }   

 // -------------------------------------------------------------------
 // Define config for the legend popup
 const legendTemplate = document.getElementById('recombinaseTableLegendPopup')
 const legendPopupConfig = {
    initiallyOpen: true,
    title: 'Matrix Legend',
    content: legendTemplate.innerHTML // pass the HTML as a string
 }


 // -------------------------------------------------------------------
 // Define config for the cell popup. This one's a lot more complicated because
 // the contents are dynamic
 const cellPopupConfig = {
     initiallyOpen: false,
     title: (cell) => {
         return 'Cell counts'
     },
     content: (cell, data) => {
         const template = document.getElementById('recombinaseTableCellPopup')
         const clone = template.content.cloneNode(true)
         const div = clone.children[0]
         // Get references to my row and the enclosing tbody
         const row = cell.closest('.pgg-row')
         const tbody = row.closest('tbody')
         // Figure out the name of my parent row
         const rowName = row.getAttribute('name')
         const i = rowName.lastIndexOf('-')
         const parentRowName = i == -1 ? rowName : rowName.substring(0, i)
         // Get a ref to my parent row
         const parentRow = tbody.querySelector('tr[name="'+parentRowName+'"]')
         // Get the structure name and system name
         const structureName = row.querySelector('td:first-child span').innerText
         const systemName = parentRow.querySelector('td:first-child span').innerText
         // Get the age bin
         const tbl = row.closest('.pgg-table')
         const colIndex = cell.cellIndex
         const col = tbl.querySelectorAll('thead th')[colIndex]
         const ageBin = col.innerText
         // inject context string
         const contextString = structureName + " (" + ageBin + ")"
         const contextNode = div.querySelector('span.context-string')
         contextNode.innerText = contextString
         // append system name to button's URL
         const btn = div.querySelector('a.view-results-button')
         const url = btn.getAttribute('href') + encodeURIComponent(systemName)
         btn.setAttribute('href', url)
         //
         const rows = [
             ['Yes', data.d],
             ['No', data.nd],
             ['Ambiguous', data.amb],
             ['No/Ambiguous<br/>in descendants', data.ndd]
         ].filter(p => p[1] > 0)
         const rowHtml = rows.map(r => '<tr><td>' + r[0] + '</td><td>' + r[1] + '</td></tr>').join('')
         const tbody2 = div.querySelector('table.cell-data-table > tbody')
         tbody2.innerHTML = rowHtml
         //
         return div.innerHTML
     }
 }

 // -------------------------------------------------------------------
 // Define the column labels
 var columnData = [
     '<span>Activity in Systems/Structures</span>',
     '<span>Embryonic</span><span> (E0-8.9)</span>',
     '<span>Embryonic</span><span> (E9-13.9)</span>',
     '<span>Embryonic</span><span> (E14-21)</span>',
     '<span>Newborn</span><span> (P0-3.9)</span>',
     '<span>Pre-weaning</span><span> (P4-21.9)</span>',
     '<span>Post-weaning</span><span> (P22-42.9)</span>',
     '<span>Adult</span><span> (P>43)</span>',
     '<span>Postnatal</span><span> (age unspecified)</span>'
 ]

 // -------------------------------------------------------------------
 // Put it all together into a config 
 const gridConfig = {
     rowData: recombinaseTableRowData,
     columnData: columnData,
     cellRenderer: cellData2class,
     legendPopupConfig: legendPopupConfig,
     cellPopupConfig: cellPopupConfig
 }

 // -------------------------------------------------------------------
 // And finally
 window.recombinaseGrid = pgg.renderGrid ("recombinaseTableWrapper", gridConfig)

</script>


  </c:otherwise>
</c:choose>


