/*
 * Javascript wiring for the recombianse form
 */

var structureUrl = fewiurl+"autocomplete/structure?query=";
var driverUrl = fewiurl+"autocomplete/driver?query=";
var disableColor = "#CCC";

// wire the form interactions
$(function(){

	var $creForm = $("#creForm");
        var rowCount = 0;

        //--------------------------------------
        // wire in the structure autocomplete.
        // argument is an input element for a structure
        // call this function as each input is added
        var attachAutocomplete = function (elt) {
            $(elt).autocomplete({
                source: function( request, response ) {
                        $.ajax({
                                url: structureUrl+request.term,
                                dataType: "json",
                                success: function( data ) {
                                        response($.map(data["resultObjects"], function( item ) {
                                                return {label: item.synonym, hasCre: item.hasCre,
                                                        isStrictSynonym: item.isStrictSynonym,
                                                        original: item.structure};
                                        }));
                                }
                        });
                },
                minLength: 1
            }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
                var value = item.label;
                if (item.isStrictSynonym)
                {
                        var synonymColor = item.hasCre ? "#222" : disableColor;
                        value += "<span style=\"color:"+synonymColor+"; font-size:0.9em; font-style:normal;\">[<span style=\"font-size:0.8em;\">syn. of</span> "+item.original+"]</span> ";
                }
                if (item.hasCre)
                {
                        return $('<li></li>')
                                .data("item.autocomplete",item)
                                .append("<a>" + value + "</a>")
                                .appendTo(ul);
                }
                // adding the item this way makes it disabled
                return $('<li class="ui-menu-item disabled" style="color:#CCC;"></li>')
                        .data("item.autocomplete", item)
                        .append('<span>'+value+'</span>')
                        .appendTo(ul);
            };
        }

        /* function implementing NotDetected/NowhereElse incompatibility. */
        var check_nd_nwe_incompatibility = function () {
            const creForm = document.getElementById('creForm')
            const ndRadios = creForm.querySelectorAll('input[type="radio"][value="false"]')
            const ndHeader = creForm.querySelector('table.structure-table thead th.nd-header')
            const ane = creForm.querySelector('#nowhereElse')
            const aneLabel = creForm.querySelector('label.nowhereElseLabel')
            //
            let ndChecked = false
            ndRadios.forEach(r => ndChecked = ndChecked || r.checked)
            if (ndChecked) {
                ane.disabled = true
                aneLabel.classList.add('disabled')
                ndRadios.forEach(r => r.disabled = false)
                ndHeader.classList.remove('disabled')
            } else if (ane.checked) {
                ane.disabled = false
                aneLabel.classList.remove('disabled')
                ndRadios.forEach(r => r.disabled = true)
                ndHeader.classList.add('disabled')
            } else {
                ane.disabled = false
                aneLabel.classList.remove('disabled')
                ndRadios.forEach(r => r.disabled = false)
                ndHeader.classList.remove('disabled')
            }
            //
            const displayValue = ndRadios.length > 1 ? 'initial' : 'none'
            const removeButtons = creForm.querySelectorAll('button.removeButton')
            removeButtons.forEach(b => b.style.display = displayValue)
        }

        /* add another structure input with detected/not detected radios */
        var addStructureRow = function () {
            const stbody = $creForm.find("table.structure-table tbody")[0]
            const nrows = $(stbody).find('tr').length
            const tr = document.createElement('tr')
            rowCount += 1
            tr.innerHTML =
                `<td>
                    <button type="button" class="removeButton" title="Remove this structure.">X</button>
                    <input type="text" size="40"
                        name="structure_${rowCount}"
                        class="ui-autocomplete-input structure-input"
                        style="margin-bottom:8px"
                        name="creStructureAC"
                        placeholder="any anatomical structure" value="" />
                </td><td>
                    <input type="radio" name="detected_${rowCount}" value="true" checked />
                </td><td>
                    <input type="radio" name="detected_${rowCount}" value="false" />
                </td>`
            stbody.appendChild(tr)
            // click handler for delete-row button
            $(tr).find("button.removeButton").click(function(e){
                removeStructureRow(tr)
            });
            // wire in autocomplete behavior
            const structureInput = tr.querySelector('input[type="text"]')
            attachAutocomplete(structureInput)
            // wire in NotDetected/NowhereElse incompatibility
            const ane = $creForm.find("#nowhereElse")[0]
            const radios = tr.querySelectorAll('input[type="radio"]')
            radios.forEach(r => {
                r.addEventListener('click', check_nd_nwe_incompatibility)
            })
            check_nd_nwe_incompatibility()
        }

        /* remove another last structure row (unless there's only one row) */
        var removeStructureRow = function (tr) {
            // hang on to the parent, then remove the row
            const pnode = tr.parentNode
            tr.remove()
            // don't allow zero rows
            const nremaining = pnode.querySelectorAll('tr').length
            if (nremaining === 0) addStructureRow()
            //
            check_nd_nwe_incompatibility()
        }

        /* clears the form to the initial state: 
         * - one structuure row, nothing in text box, and detected is checked
         * - the "and nowhere else box is unchecked.
         * - the driver field is blank
         */
        var resetForm = function () {
            const creForm = document.getElementById('creForm')
            const trs = creForm.querySelectorAll('table.structure-table tbody > tr')
            const ane = creForm.querySelector('#nowhereElse')
            const driver = creForm.querySelector('#creDriverAC')

            ane.checked = false
            driver.value = ''
            trs.forEach(tr => removeStructureRow(tr))
        }

        /* add  the first row */
        addStructureRow()

        const ane = $creForm.find("#nowhereElse")[0]
        ane.addEventListener('click', check_nd_nwe_incompatibility)

	/* Clicking Add button adds a row to the structure table */
	$creForm.find("button.addButton").click(function(e){
            addStructureRow()
	});

	/* Clicking Reset button clears the form */
	$creForm.find("button.resetButton").click(function(e){
            resetForm()
	});

        //--------------------------------------
	/* Changing input values detects if "AND" text should appear */
	$creForm.find("input").keyup(function(e){
		displayAndDivider();
	});

	/* if both structure and driver are filled in, then display AND */
	var displayAndDivider = function() {

                return

		if ($creForm[0].structure.value
				&& $creForm[0].driver.value) {
			// show
			$creForm.find("#creAndDivider").css("opacity","1");
		}
		else {
			// hide
			$creForm.find("#creAndDivider").css("opacity","0");
		}
	};
	displayAndDivider();
        //--------------------------------------
        //
        // wire in the driver autocomplete
        var creDriverAC = $( "#creDriverAC" ).autocomplete({
            source: function( request, response ) {
                    $.ajax({
                            url: driverUrl+request.term,
                            dataType: "json",
                            success: function( data ) {
                                    response($.map(data["resultObjects"], function( item ) {
                                            //return {label: item.driverDisplay, driver: item.driver};
                                            return {label: item.driverDisplay, value: item.driver};
                                    }));
                            }
                    });
            },
            minLength: 1
        }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
                    var value = item.label;
                    return $('<li></li>')
                            .data("item.autocomplete", item)
                            .append("<a>" + value + "</a>")
                            .appendTo(ul);
        };
        //--------------------------------------
        /* (1) Validates form inputs and (2) encodes the multiple structures and 
         * detected/not-detected into a single string field ("structures")
         *
         * Current validation rules:
         * - no two structures can be the same
         */
        var validate_form_inputs = function () {
            const creForm = document.getElementById('creForm')
            const hinput = creForm.querySelector('input[name="structures"]')
            const sinputs = creForm.querySelectorAll('input.structure-input')
            const dRadios = creForm.querySelectorAll('input[type="radio"][value="true"]')
            const ndRadios = creForm.querySelectorAll('input[type="radio"][value="false"]')
            const snames = new Set()
            let retVal = true
            let structs = []
            sinputs.forEach((si,i) => {
                const sname = si.value.trim()
                const nd = ndRadios[i].checked
                if (sname) {
                    if (snames.has(sname) && retVal /* avoids multiple alerts */) {
                        const msg = "Query error: Duplicate structure name detected: " +
                                    sname + ". Please modify your query and try again."
                        alert(msg)
                        retVal = false // cancel form submission
                    }
                    snames.add(sname)
                    structs.push((nd?"-":"+") + sname)
                }
            })
            // sets the hidden "structures" field
            hinput.value = structs.join('|')
            return retVal
        }

        // attach the validation routing as the onsubmit handler
        $creForm.submit(validate_form_inputs)
});
