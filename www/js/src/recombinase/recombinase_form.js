/*
 * Javascript wiring for the recombianse form
 */

var structureUrl = fewiurl+"autocomplete/structure?query=";
var driverUrl = fewiurl+"autocomplete/driver?query=";
var disableColor = "#CCC";

// wire the form interactions
$(function(){

	var $creForm = $("#creForm");

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
            const ndHeader = creForm.querySelector('thead th.nd-header')
            const ane = creForm.querySelector('#nowhereElse')
            const aneLabel = creForm.querySelector('label.nowhereElseLabel')
            //
            let ndChecked = false
            ndRadios.forEach(r => ndChecked = ndChecked || r.checked)
            if (ndChecked) {
                ane.disabled = true
                aneLabel.classList.add('disabled')
            } else if (ane.checked) {
                ndRadios.forEach(r => r.disabled = true)
                ndHeader.classList.add('disabled')
            } else {
                ane.disabled = false
                aneLabel.classList.remove('disabled')
                ndRadios.forEach(r => r.disabled = false)
                ndHeader.classList.remove('disabled')
            }
        }

        /* add another structure input with detected/not detected radios */
        var addStructureRow = function () {
            const stbody = $creForm.find("table.structure-table tbody")[0]
            const nrows = stbody.childNodes.length
            const tr = document.createElement('tr')
            tr.innerHTML =
                `<td>
                    <input type="text" size="40"
                        name="structure_${nrows}"
                        class="ui-autocomplete-input structure-input"
                        style="margin-bottom:8px"
                        name="creStructureAC"
                        placeholder="any anatomical structure" value="" />
                </td><td>
                    <input type="radio" name="detected_${nrows}" value="true" checked />
                </td><td>
                    <input type="radio" name="detected_${nrows}" value="false" />
                </td>`
            stbody.appendChild(tr)
            // wire in autocomplete behavior
            const structureInput = tr.querySelector('input[type="text"]')
            attachAutocomplete(structureInput)
            // wire in NotDetected/NowhereElse incompatibility
            const ane = $creForm.find("#nowhereElse")[0]
            const radios = tr.querySelectorAll('input[type="radio"]')
            radios.forEach(r => {
                r.addEventListener('click', check_nd_nwe_incompatibility)
                if (r.value === "false" && ane.checked) {
                   r.disabled = true
                }
            })
        }

        /* add  the first row */
        addStructureRow()

        const ane = $creForm.find("#nowhereElse")[0]
        ane.addEventListener('click', check_nd_nwe_incompatibility)

	/* Clicking ADD button adds a row to the structure table */
	$creForm.find(".addButton").click(function(e){
            addStructureRow()
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
        /* validate form inputs */
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
                    if (snames.has(sname) && retVal) {
                        const msg = "Query error: Duplicate structure name detected: " +
                                    sname + ". Please modify your query and try again."
                        alert(msg)
                        retVal = false // cancel form submission
                    }
                    snames.add(sname)
                    structs.push((nd?"-":"") + sname)
                }
            })
            hinput.value = structs.join('|')
            return retVal // proceed with form submission
        }

        // attach the validation routing as the onsubmit handler
        $creForm.submit(validate_form_inputs)
});
