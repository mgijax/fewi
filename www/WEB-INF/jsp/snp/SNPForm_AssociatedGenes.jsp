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
			<div class="left" style="margin-left: 70px;">
				For the gene(s) specified above return all SNPs:<br/><br/>
				<fewi:radio name="withinRange" divider="<br/>" idPrefix="rangeDropList" items="${withinRanges}" value="${e:forHtml(snpQueryForm.withinRange)}" />
			</div>
			<br/>
			<div class="left" style="margin-left: 70px;">
				All SNP function classes will be returned.<br />
				You can filter SNPs by function class in the search results.
			</div>
		</td>
	</tr>
