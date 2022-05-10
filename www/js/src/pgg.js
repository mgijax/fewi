 /*
  * pgg.js
  *
  * Overview:
  *
  * 'PGG' stands for 'Pretty Good Grid' (in contrast to the SuperGrid).
  * PGG is lightweight, consisting of this Javascript file and its companion style sheet, pgg.css
  * It has no outside dependencies.
  * 
  * PGG knows how to take data in a defined format (see below) and draw a table with rows and cells
  * in the style of the SuperGrid. It knowis that cells have data but it doesn't know/care what that
  * data is. It knows about openy/closey behavior of rows, but it doesn't know anything about fetching data.
  * It knows how to create (movable, closable) popups, but it doesn't know what goes in them.
  * (You get the idea. Lightweight means you do more of the work!)
  *
  * Unlike SuperGrid, PGG does not know how to get data. You have to give it (all of) the data to render up front.
  * PGG does not yet support incremental updates, which will be necessary for reimplementing the GXD result matrices using PGG.
  * (PGG still won't know how to get the data, but it will allow you to add data (rows) in increments, and it will allow you to
  * catch open/close events so you know when to get more data.)
  *
  * Drawing mnatrix cells.
  *
  * Drawing matrix cells is almost exclusively done with CSS.
  * In the DOM, a matrix cell has a simple structure.
  *
  *     <td class="pgg-cell CELLSTYLE"><div class="decorator" /></td>
  *
  * where CELLSTYLE is one of the classes defined in pgg.css. For example,
  *
  *   <!-- light blue square with a dark red corner -->
  *   <td class="pgg-cell b1r3"><div class="decorator" /></td>
  *
  *   <!-- empty square with open circle -->
  *   <td class="pgg-cell empty-circle"><div class="decorator" /></td>
  *
  * So, the key thing needed to draw a cell is a function that takes the data object for
  * the cell and returns the class name to use.
  *
  * Popups.
  *
  * PGG provides support for creating moveable, closable popups for a grid legend and for cell popups.
  * It takes care of creating the containers, givening them the required behaviors, and connecting them
  * with buttons/click events.
  * However, the *content* of these popups are the caller's responsibiltiy: callbacks must be provided that return 
  * the title and main body content.
  *
  * How to use:
  *
  * Provide a place to draw the grid:
  *     <div id="myGridWrapper"></div>
  *
  * Include pgg.js and pgg.css 
  *     <link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/pgg.css">
  *     <script type="text/javascript" src="${configBean.FEWI_URL}assets/js/pgg.js"></script>
  *
  * Get the data for the grid:
  *     1. a list of rows
  *     2. each row is an object with fields:
  *        - name : label to show in lefthand column
  *        - cellData : list of data objects for each cell in this row
  *        - children : list of rows (recursive)
  *
  * To draw a grid, call the main entry point:
  *     pgg.renderGrid (targetId, data, columns, legendConfig, cellPopupConfig)
  *
  * where:
  *
  *     targetId is the id of an empty div element where the grid will be injected
  *
  *     data is a list of row data objects. Row data includes the name to show in
  *             the left column and a list of cell data objects. All rows must have
  *             the same number of cell data objets. A cell data object can be anything.
  *             Typical use case: cell data contains cup ounts of annotations.
  *     columns is a list of column names. 
  *
  *
  *
  */

 (function () {
     function clickHandler (ev, tbl, gridControls) {
         // A single click handler for the table.
         //const tbl = ev.target.closest(".pgg-table")
         if (!tbl) return

         const cellPopup = gridControls ? gridControls.cellPopup : null

         // Check if click was on one of the open/close all buttons
         if (ev.target.tagName === 'A') {
             const name = ev.target.getAttribute("name")
             if (name === "open-all") {
                 openAllRows(tbl)
                 ev.stopPropagation()
                 return
             } else if (name === "close-all") {
                 closeAllRows(tbl)
                 ev.stopPropagation()
                 return
             }
         }

         // See if a cell was clicked
         const cell = ev.target.closest('.pgg-cell')
         if (cell) {
             // if clicked on an empty cell, nothing happens
             if (cell.classList.contains('empty') || cell.classList.contains('empty-circle')) {
                 return
             }
             // Get the data for this cell
             let cellData
             if (cell.getAttribute('_data')) {
                 cellData = JSON.parse(decodeURIComponent(cell.getAttribute('_data')))
             }
             // open the popup near the clicked cell
             if (cellPopup) {
                 cellPopup.controls.open(cell, cellData)
                 const cr = cell.getBoundingClientRect()
                 cellPopup.controls.moveTo(cr.right + 20, cr.top - 20)
                 ev.stopPropagation()
             }
             return
         }

         // See if an openy/closey row was clicked
         const tgtRow = ev.target.closest('.has-children')
         if (tgtRow) {
             // toggle the row's state
             tgtRow.classList.toggle('closed')
             // now show/hide descendant rows.
             const isClosed = tgtRow.classList.contains('closed')
             const tname = tgtRow.getAttribute("name")
             const tdepth = tname.split("-").length
             const cssPattern = `tr[name|="${tname}"]`
             const rows = tbl.querySelectorAll(cssPattern)
             rows.forEach(r => {
                // css pattern returns all desecndants. Only want to open/close
                // immediate children of the tgtRow
                const rname = r.getAttribute('name')
                const rdepth = rname.split('-').length
                if (rdepth === tdepth + 1) r.style.display = isClosed ? "none" : ""
             })
             //
             cellPopup && cellPopup.controls.close()
             //
             ev.stopPropagation()
             return
         }
     }

     function closeAllRows (tbl) {
         tbl.querySelectorAll('tbody > tr[name]').forEach(tr => {
             if (tr.getAttribute("name").split("-").length === 1) {
                 tr.style.display = ''
                 tr.classList.add("closed")
             } else {
                 tr.style.display = 'none'
             }
         })
     }

     function openAllRows (tbl) {
         tbl.querySelectorAll('tbody > tr[name]').forEach(tr => {
             tr.style.display = ''
             tr.classList.remove("closed")
         })
     }

     function renderCell (cellData, cellRenderer) {
         const cellClass = cellRenderer(cellData)
         const _data = encodeURIComponent(JSON.stringify(cellData))
         return `<td class="pgg-cell ${cellClass}" _data="${_data}"><div class="decorator"></div></td>`
     }

     function renderNameCell (rowData) {
         const hasChildren = Array.isArray(rowData.children)
         const turnstile = hasChildren ? '<div class="turnstile">▼</div>' : ''
         return `<td class="row-label"><span>${rowData.name}</span>${turnstile}</td>`
     }

     function renderRow (rowData, level, cellRenderer) {
         const hasChildren = Array.isArray(rowData.children)
         const rowNameHtml = renderNameCell (rowData)
         const cellsHtml = rowData.cellData.map(cd => renderCell(cd, cellRenderer)).join('')
         // zebra striping - each top-level group alternates color. All structures within the group have same background color.
         const stripeIndex = parseInt((level || "0").split("-")[0])
         const stripeClass = 'stripe' + (stripeIndex % 2)
         //
         const rowHtml = `<tr name="${level}" class="pgg-row ${hasChildren ? 'has-children' : ''} ${stripeClass}">${rowNameHtml}${cellsHtml}</tr>`
         // recursive call to render descendant rows
         const childrenHtml = hasChildren ? renderRows(rowData.children, level, cellRenderer) : ''
         //
         return rowHtml + childrenHtml
     }

     function renderRows (rowsData, level, cellRenderer) {
         const joiner = level ? '-' : ''
         return rowsData.map((rowData, rowNum) => renderRow(rowData, level+joiner+rowNum, cellRenderer)).join('')
     }

     function renderColumnHeaders (tbl, columnData, legendFcns) {
         const thead = tbl.querySelector('thead')
         // Caller supplies column names.
         // To first column, we add:
         // - open-all/close-all buttons.
         // - legend popup button
         const openCloseButtons = `
             <div style="padding: 4px; font-size: 10px;">
                 <a name="open-all" style="cursor: pointer; color:blue;" >show</a> or
                 <a name="close-all" style="cursor: pointer; color:blue;" >hide</a> all structures
             </div>
             `
         const legendButton = legendFcns ? `
             <div><button name="showLegend">Legend</button></div>
             ` : ''
         thead.innerHTML = '<tr>' + columnData.map((c,i) => {
             return `<th><div>${c}</div>${i === 0 ? openCloseButtons+legendButton : ''}</th>`
         }).join('') + '</tr>'

         if (legendFcns) {
             const lbutton = thead.querySelector('button[name="showLegend"]')
             lbutton.addEventListener('click', () => legendFcns.open(tbl))
         }
     }

     // The main entry point. Renders (injects) a grid into the specified target using the given data
     // and popup configs. Wires up click handlers, etc.
     //
     // Returns an object containing the grid element, and a list of popups, each one containing a name,
     // a ref to the popup element, and control functions for opening/closing/moving popups.
     // 
     // Args:
     //     wrapperId (string) id of div node to inject the grid and its friends
     //     config (object) config object for the grid (see description at top of file)
     //
     function renderGrid (wrapperId, config) {
         console.log("pgg.renderGrid wrapperId=" + wrapperId)
         // find the wrapper element
         if (typeof(wrapperId) !== 'string') throw "wrapperId not specified"
         const wrapper = document.getElementById(wrapperId)
         if (!wrapper) throw "Cannot find wrapper element: #" + wrapperId
         //
         const rowData = config.rowData
         if (!Array.isArray(rowData)) throw "No row data in config."
         //
         const columnData = config.columnData || []
         //
         const cellRenderer = config.cellRenderer
         if (typeof(cellRenderer) != 'function') throw "Cell renderer is not a function."
         //
         const legendPopupConfig = config.legendPopupConfig
         //
         const cellPopupConfig = config.cellPopupConfig

         // initialize empty table
         wrapper.innerHTML = `<table class="pgg-table"><thead></thead><tbody></tbody></table>`
         const tbl = wrapper.querySelector('table')
         const thead = tbl.childNodes[0]
         const tbody = tbl.childNodes[1]
         //
         const popups = []
         // create the legend popup
         let legendFcns
         if (legendPopupConfig) {
             legendPopupConfig.id = wrapperId + '-legend-popup'
             legendPopupConfig.extraClass = "pgg-legend-wrapper"
             legendFcns = initPopup(wrapper, tbl, legendPopupConfig)
             popups.push({ name: 'legend', popup: legendFcns.popup, controls: legendFcns })
         }
         // create the cell popup
         let cellPopupFcns
         console.log('renderGrid: cellPopupConfig=', cellPopupConfig)
         if (cellPopupConfig) {
             cellPopupConfig.id = wrapperId + '-cell-popup'
             cellPopupConfig.extraClass = 'pgg-cell-popup-wrapper'
             cellPopupFcns = initPopup(wrapper, tbl, cellPopupConfig)
             popups.push({ name: 'cellPopup', popup: cellPopupFcns.popup, controls: cellPopupFcns })
         }
         // Draw column headers
         if (columnData) renderColumnHeaders(tbl, columnData, legendFcns)
         // Draw rows 
         const rowsHtml = renderRows(rowData, '', cellRenderer)
         tbody.innerHTML = rowsHtml
         // Initially close everything
         closeAllRows(tbl)
         // Get coordinates/dimensions of rendered table. Then set initial position of popups just to the table's right.
         const bcr = tbl.getBoundingClientRect()
         legendFcns.moveTo( bcr.right + 20, bcr.top )
         cellPopupFcns.moveTo( bcr.left + 200, bcr.top + 100)
         //
         const gridControls = {
             grid: tbl,
             resetView: function() {
                 legendFcns.open(tbl)
                 cellPopupFcns.close()
             },
             legendPopup: { name: 'legend', popup: legendFcns.popup, controls: legendFcns },
             cellPopup: { name: 'cellPopup', popup: cellPopupFcns.popup, controls: cellPopupFcns },
         }
         // Add click handler for the table
         tbl.addEventListener('click', ev => clickHandler(ev, tbl, gridControls))
         //
         return gridControls
     }

     // Initialize a new popup.
     // A popup is a movable (draggable), open/close-able container of customizable content.
     //
     // Arguments:
     //     wrapperElt - (DOM node) where to inject the popup
     //     config - (object) Configuration for the popup. Contains these fields:
     //         id - (string) an id to assign to the popup. Required.
     //         extraClass - (string) Extra CSS class(es) to assign to the popup. Optional.
     //         title - (function or string) Text to show in the header bar of the popup.
     //             If a function, it will be called each time the popup opens, and the results injected.
     //             Optional. Defaults to no title text.
     //         content - (string or function) Content to inject into the popup's body. If a function,
     //             the function will be called each time the popup opens and the results injected as the body.
     //             For a popup whose content never changes, this argument can also be a string.
     //         initiallyOpen - (boolean) If true, popup is initially in the open state. (Rendering functions if any
     //             will be called without arguments and should be ready for that.)
     // Returns:
     //    An object containing controller functions { open, close, toggle,  moveTo, moveBy }
     //
     function initPopup (wrapperElt, tbl, config) {
         const popup = document.createElement('div')
         const phead = document.createElement('div')
         const pbody = document.createElement('div')
         const ptitle = document.createElement('span')
         const closeButton = document.createElement('button')
         wrapperElt.appendChild(popup)
         popup.appendChild(phead)
         phead.appendChild(ptitle)
         phead.append(closeButton)
         popup.appendChild(pbody)
         //
         popup.setAttribute('id', config.id)
         popup.classList.add('pgg-popup')
         if (config.extraClass) {
             popup.classList.add(config.extraClass)
         }
         phead.classList.add('pgg-popup-head')
         pbody.classList.add('pgg-popup-body')
         closeButton.classList.add('close-button')
         closeButton.innerText = 'X'

         // title rendering function
         //
         let titleFcn
         if (!config.title) {
             titleFcn = () => 'No title specified.'
         } else if (typeof(config.title) === 'string') {
             const cc = config.title
             titleFcn = () => cc
         } else if (typeof(config.title) === 'function') {
             titleFcn = config.title
         } else {
             throw "Title is neither a function nor a string."
         }

         // content rendering function
         //
         let contentFcn
         if (! config.content) {
             contentFcn = () => 'No content specified.'
         } else if (typeof(config.content) === 'string') {
             const cc = config.content
             contentFcn = () => cc
         } else if (typeof(config.content) === 'function') {
             contentFcn = config.content
         } else {
             throw "Content is neither a function nor a string."
         }

         // controller functions --------------------

         // Move popup's upper left corner to x,y
         const moveToFunction = (x, y) => {
             // x,y are client coords (relative to upper left corner of browser visible area)
             const bbox = document.body.getBoundingClientRect()
             popup.style.left = (x - bbox.left) + 'px'
             popup.style.top = (y - bbox.top) + 'px'
         }

         // Move popup by dx, dy pixels
         const moveByFunction  = (dx, dy) => {
             const r = popup.getBoundingClientRect()
             const bbox = document.body.getBoundingClientRect()
             popup.style.left = (r.left - bbox.left + dx) + 'px'
             popup.style.top = (r.top - bbox.top + dy) + 'px'
         }
         
         // Open the popup near the specified element
         const openFunction = function (near) {
             popup.classList.remove('hidden')
             // invoke the rendering functions. Pass along any/all arguments
             ptitle.innerText = titleFcn.apply(null, arguments)
             pbody.innerHTML = contentFcn.apply(null, arguments)
             if (near) {
                 const nbb = near.getBoundingClientRect()
                 moveToFunction(nbb.right + 20, nbb.top - 10)
             }
         }

         // Close the popup
         const closeFunction = () => {
             popup.classList.add('hidden')
         }

         // Open the popup if it's closed, and close the popup if it's open.
         const toggleFunction = (elt) => {
             if (popup.classList.contains('hidden')) {
                 openFunction(elt)
             } else {
                 closeFunction()
             }
         }

         // package up the controllers. This is what we'll return.
         const controllers = {
             popup: popup, 
             open: openFunction,
             close: closeFunction,
             toggle: toggleFunction,
             moveTo: moveToFunction,
             moveBy: moveByFunction
         }

         // wire the close button
         closeButton.addEventListener('click', () => closeFunction())
  
         // Set initial open/closed state
         if (config.initiallyOpen) {
             window.setTimeout( () => openFunction(tbl), 500)
         } else {
             closeFunction()
         }

         // Make the popup draggable
         dragify(popup, phead)

         // done!
         return controllers
     }

    // Lifted and adapted from https://www.w3schools.com/howto/howto_js_draggable.asp
    function dragify (eltToMove, dragHandle) {
      var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;

      dragHandle.onmousedown = dragMouseDown
      function dragMouseDown(e) {
        e = e || window.event;
        e.preventDefault();
        // get the mouse cursor position at startup:
        pos3 = e.clientX;
        pos4 = e.clientY;
        document.onmouseup = closeDragElement;
        // call a function whenever the cursor moves:
        document.onmousemove = elementDrag;
      }

      function elementDrag(e) {
        e = e || window.event;
        e.preventDefault();
        // calculate the new cursor position:
        pos1 = pos3 - e.clientX;
        pos2 = pos4 - e.clientY;
        pos3 = e.clientX;
        pos4 = e.clientY;
        // set the element's new position:
        eltToMove.style.top = (eltToMove.offsetTop - pos2) + "px";
        eltToMove.style.left = (eltToMove.offsetLeft - pos1) + "px";
      }

      function closeDragElement() {
        // stop moving when mouse button is released:
        document.onmouseup = null;
        document.onmousemove = null;
      }
    }

     // Make selected functions available 
     window.pgg = {
         renderGrid: renderGrid
     }

 })()
