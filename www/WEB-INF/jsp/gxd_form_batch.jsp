<style>
div.fixed { height: 120px }
div.top { vertical-align: top }
div.middle { vertical-align: middle }
</style>
<form:form commandName="gxdBatchQueryForm" class="gxdQf" method="POST" enctype="multipart/form-data" id="gxdBatchQueryForm1">
<!-- query form table -->
<form:hidden path="batchSubmission" value="true"/>
<table class="pad5 borderedTable" width="100%">
	<tr>
		<td colspan="2" align="left">
			<input class="buttonLabel" value="Search" type="submit" id="submit6">
			&nbsp;&nbsp;
			<input type="reset" id="reset6">
		</td>
	</tr>

	<tr class="stripe1">
		<td class="cat1Gxd">Genes</td>
		<td>
		<div id="genesWrapper" class="top" style="padding: 3px;">
			<div style="border-bottom: solid thin gray; width: 100%; clear:both; padding-bottom: 4px;">
				Search by:
				<form:select path="idType" items="${gxdBatchQueryForm.idTypes}">
				<form:options items="${idTypes}"/>
				</form:select>
				<br/>
			</div>
		    <div id="entryWrapper">
			<div id="batchLeft" class="fixed top" style="display: inline-block; padding-top: 4px;">
				<span style="font-size: 120%">Enter Text</span>
				<p/>
				<span class="label">ID/Symbols List:</span>
				<br/>
				&nbsp;&nbsp;<form:textarea id="ids" path="ids" class="formWidth" value="${gxdBatchQueryForm.ids}"></form:textarea>
				<br/>
				&nbsp;&nbsp;<span class="example">* tab, space, and newline separated ids.</span>
			</div>
			<div id="batchMiddle" class="fixed middle" style="display: inline-block; text-align: center; font-weight: bold; font-size: 120%; padding-left: 30px; padding-right: 30px">
				<div style="padding-top: 35px">|<br/>OR<br/>|</div>
			</div>
			<div id="batchRight" class="fixed top" style="display: inline-block; padding-top: 4px;">
				<span style="font-size: 120%">Upload File</span>
				<p/>
				<span class="label">ID/Symbols File:</span>
				<br/>
					&nbsp;&nbsp;<input type="file" name="idFile">
				<p style='padding: 3px 0'/>
				<div id="typeWrapper">
					<div style='display: inline-block'>
					<span class="label">File Type:</span><br/>
					&nbsp;&nbsp;<span><form:radiobutton path="fileType" value="tab"/>&nbsp;tab-delimited</span><br/>
					&nbsp;&nbsp;<span><form:radiobutton path="fileType" value="csv"/>&nbsp;comma separated</span><br/>
					</div>
					<div style="display: inline-block; vertical-align: top; padding-left: 15px">
					<span class="label">ID/Symbols column:</span><br/>
					&nbsp;&nbsp;<form:input path="idColumn" size="3"></form:input><br/>
					<span class="example">* ID/Symbols parsed from a single column</span>
					</div>
				</div>
			</div>
		    </div>
		</div>

		</td>
	</tr>

	<tr>
		<td colspan="3" align="left">
			<input class="buttonLabel" value="Search" type="submit"  id="submit6">
			&nbsp;&nbsp;
			<input type="reset" id="reset6">
		</td>
	</tr>
</table>
</form:form>
