	<tr class="stripe1">
		<td class="cat1">Associated Genes</TD>
		<td>
			<div class="left">
				<span class="example">Find SNPs Within or Near Specified Genes</span><br/>
				<div style="width:300px;text-align:left;margin-top:3px;">
					<nobr>
						<span class="label"><a onclick="javascript:openUserhelpWindow('${userhelppage}#marker_name'); return false;" href="${userhelpurl}#marker_name">Gene Symbol/Name:</a></span>
						<form:input id="nomen" path="nomen" class="formWidth" /> Search <fewi:select name="searchGeneBy" id="searchGeneByList" size="1" items="${searchByOptions}" value="${e:forHtml(snpQueryForm.searchGeneBy)}" />
					</nobr>
					<form:errors path="nomen" cssClass="qfError"/>
				</div>
				<div style="margin-left: 120px;margin-bottom:10px;">
					<span class="example">Examples:&nbsp;&nbsp;&nbsp;Fmr1&nbsp;&nbsp;&nbsp;Pax*&nbsp;&nbsp;&nbsp;Dnah5,Dnah6,Dnah7*</span>
				</div>
			</div>
			<br/>
			<div id="rangeDiv" class="left" style="margin-left: 70px;">
				For the gene(s) specified above return all SNPs:<br/><br/>
				<fewi:radio name="withinRange" divider="<br/>" idPrefix="rangeDropList" items="${withinRanges}" value="${e:forHtml(snpQueryForm.withinRange)}" />
			</div>
			<br/>
			<div id="outOfSyncGeneMessage" class="left" style="margin-left: 70px; display:none; color: red; padding-top: 10px;">
				<br>
				--This section is temporarily limited due to a discrepancy between the genome coordinates of RefSNPs and MGI genes.--
			</div>
		</td>
	</tr>
<c:if test="${configBean.snpsOutOfSync == 'true'}">
<script>
$('[name=withinRange]').attr('disabled', true);				// disable all three range radio options
$('[name=withinRange]').first().attr('disabled', false);	// re-enable the first option
$('[name=withinRange]').click();							// click the first option
$('#outOfSyncGeneMessage').css({'display' : 'inline'});		// show the message
$('#rangeDiv span').css({'color' : 'lightgray'});			// gray out the text of the options
$('#rangeDiv span').first().css({'color' : 'black'});		// and re-color the allowed option
</script>
</c:if>
