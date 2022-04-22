 /*
  * pgg.js
  *
  * 'PGG' stands for 'Pretty Good Grid', both an homage to and a contrast with the SuperGrid.
  * PGG is lightweight, consisting of this Javascript file and its companion style sheet, pgg.css
  */

 (function () {
     function pgg_clickHandler (ev) {
         // A single click handler for the table.
         // Check the target to see what to do
         const tbl = ev.target.closest(".pgg-table")
         if (!tbl) return

         if (ev.target.tagName === 'A') {
             const name = ev.target.getAttribute("name")
             if (name === "open-all") {
                 pgg_openAllRows(tbl)
                 return
             } else if (name === "close-all") {
                 pgg_closeAllRows(tbl)
                 return
             }
         }

         const tgt = ev.target.closest('.has-children .row-label')
         if (!tgt) return
         const tgtRow = tgt.parentNode
         tgtRow.classList.toggle('closed')
         const isClosed = tgtRow.classList.contains('closed')
         const tname = tgtRow.getAttribute("name")
         const cssPattern = `tr[name|="${tname}"]`
         const rows = tbl.querySelectorAll(cssPattern)
         rows.forEach(r => {
            if (r !== tgtRow) r.style.display = isClosed ? "none" : ""
         })
     }

     function pgg_closeAllRows (tbl) {
         tbl.querySelectorAll('tbody > tr[name]').forEach(tr => {
             if (tr.getAttribute("name").split("-").length === 1) {
                 tr.style.display = ''
                 tr.classList.add("closed")
             } else {
                 tr.style.display = 'none'
             }
         })
     }

     function pgg_openAllRows (tbl) {
         tbl.querySelectorAll('tbody > tr[name]').forEach(tr => {
             tr.style.display = ''
             tr.classList.remove("closed")
         })
     }

     function pgg_cellData2class (cd) {
         const blueLevel = cd.d == 0 ? 0 : (cd.d <= 4 ? 1 : (cd.d<= 50 ? 2 : 3))
         const redLevel  = cd.nd == 0 ? 0 : (cd.nd <= 4 ? 1 : (cd.nd<= 20 ? 2 : 3))
         if (blueLevel) {
             if (redLevel) {
                 return 'b' + blueLevel + 'r' + redLevel
             } else {
                 return 'b' + blueLevel
             }
         } else if (redLevel) {
             return 'r' + redLevel
         } else if (cd.ndd) {
             return 'gold-corner'
         } else if (cd.amb) {
             return 'gray-sash'
         } else if (cd.validStructure) {
             return 'empty-circle'
         } else {
             return 'empty'
         }
     }

     function pgg_renderCell (cellData) {
         const cellClass = pgg_cellData2class(cellData)
         const cellText = `d:${cellData.d}, nd:${cellData.nd}, ndd:${cellData.ndd}, amb:${cellData.amb}`
         return `<td class="cell ${cellClass}" title="${cellText}"><div class="decorator"></div></td>`
     }

     function pgg_renderNameCell (rowData) {
         const hasChildren = Array.isArray(rowData.children)
         const turnstile = hasChildren ? '<div class="turnstile">▼</div>' : ''
         return `<td class="row-label">${rowData.name}${turnstile}</td>`
     }

     function pgg_renderRow (rowData, level) {
         const hasChildren = Array.isArray(rowData.children)
         const rowNameHtml = pgg_renderNameCell (rowData)
         const cellsHtml = rowData.cellData.map(cd => pgg_renderCell(cd)).join('')
         // zebra striping - each top-level group alternates color. All structures within the group have same background color.
         const stripeIndex = parseInt((level || "0").split("-")[0])
         const stripeClass = 'stripe' + (stripeIndex % 2)
         //
         const rowHtml = `<tr name="${level}" class="${hasChildren ? 'has-children' : ''} ${stripeClass}">${rowNameHtml}${cellsHtml}</tr>`
         // recursive call to render descendant rows
         const childrenHtml = hasChildren ? pgg_renderRows(rowData.children, level) : ''
         //
         return rowHtml + childrenHtml
     }

     function pgg_renderRows (rowsData, level) {
         const joiner = level ? '-' : ''
         return rowsData.map((rowData, rowNum) => pgg_renderRow(rowData, level+joiner+rowNum)).join('')
     }

     function pgg_renderColumnHeaders (tbl, columnData) {
         const thead = tbl.querySelector('thead')
         // Caller supplies column names.
         // To first column, we add:
         // - open-all/close-all buttons.
         // - legend popup button
         const openCloseButtons = `
             <div class="small" style="padding: 4px;">
                 <a name="open-all" style="cursor: pointer; color:blue;" >show</a> or
                 <a name="close-all" style="cursor: pointer; color:blue;" >hide</a> all structures
             </div>
             `
         const legendButton = `
             <div><button name="showLegend">Legend</button></div>
             `
         thead.innerHTML = '<tr>' + columnData.map((c,i) => {
             return `<th><div>${c}</div>${i === 0 ? openCloseButtons+legendButton : ''}</th>`
         }).join('') + '</tr>'
     }

     function pgg_renderTable (wrapperId, rowData, columnData) {
         const wrapper = document.getElementById(wrapperId)
         wrapper.innerHTML = '<table></table>'
         const tbl = wrapper.querySelector('table')
         tbl.classList.add('pgg-table')
         tbl.innerHTML = "<thead></thead><tbody></tbody>"
         if (columnData) pgg_renderColumnHeaders(tbl, columnData)
         const tbody = tbl.querySelector('tbody')
         const rowsHtml = pgg_renderRows(rowData, '')
         tbody.innerHTML = rowsHtml
         tbl.addEventListener('click', pgg_clickHandler)
         pgg_closeAllRows(tbl)
     }

     window.pgg = { renderTable: pgg_renderTable }

 })()
