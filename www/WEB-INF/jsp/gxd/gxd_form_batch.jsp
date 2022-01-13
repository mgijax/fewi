<style>
div.fixed { height: 128px }
div.top { vertical-align: top }
div.middle { vertical-align: middle }
#uploadMessage { font-size: 90%; font-weight: bold; font-style: italic; }
</style>
<form:form commandName="gxdBatchQueryForm" class="gxdQf" method="POST" enctype="multipart/form-data" id="gxdBatchQueryForm1">
<!-- query form table -->
<form:hidden path="batchSubmission" onSubmit="return false;" />
<table class="pad5 borderedTable" width="100%">
	<tr>
		<td colspan="2" align="left">
			<input class="buttonLabel" value="Search" type="submit" id="submit6" onClick="return checkBatchInput()">
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
				&nbsp;&nbsp;<form:textarea id="ids" path="ids" class="formWidth" value="${e:forHtml(gxdBatchQueryForm.ids)}" style="margin-bottom: 0px"></form:textarea>
				<br/>
				&nbsp;&nbsp;<span class="example">* tab, space, and newline separated ids</span>
				<br/>
				&nbsp;&nbsp;<span class="example">* limit input to 5000 ids/symbols</span>
				<div id="uploadMessage" style="display: none">
				</div>
			</div>
			<div id="batchMiddle" class="fixed middle" style="display: inline-block; text-align: center; font-weight: bold; font-size: 120%; padding-left: 30px; padding-right: 30px">
				<div style="padding-top: 35px">|<br/>OR<br/>|</div>
			</div>
			<div id="batchRight" class="fixed top" style="display: inline-block; padding-top: 4px;">
				<div id="typeWrapper">
				<span style="font-size: 120%">Use IDs from a File</span>
				<br/>
					<div style='display: inline-block; padding-top: 9px'>
					<span class="label">File Type:</span><br/>
					&nbsp;&nbsp;<span><form:radiobutton path="fileType" value="tab"/>&nbsp;tab-delimited</span><br/>
					&nbsp;&nbsp;<span><form:radiobutton path="fileType" value="csv"/>&nbsp;comma separated</span><br/>
					</div>
					<div style="display: inline-block; vertical-align: top; padding-left: 15px; padding-top: 9px">
					<span class="label">ID/Symbols column:</span><br/>
					&nbsp;&nbsp;<form:input path="idColumn" size="3"></form:input><br/>
					<span class="example">* ID/Symbols parsed from a single column</span>
					</div>
				</div>
				<p style='padding: 3px 0'/>
				<span class="label">ID/Symbols File:</span>
				<br/>
					&nbsp;&nbsp;<input type="file" name="idFile" onChange="readFile(event)">
			</div>
		    </div>
		</div>

		</td>
	</tr>

	<tr>
		<td colspan="3" align="left">
			<input class="buttonLabel" value="Search" type="submit"  id="submit6" onClick="return checkBatchInput()">
			&nbsp;&nbsp;
			<input type="reset" id="reset6">
		</td>
	</tr>
</table>
<c:if test="${not empty structureIDs}">
	<c:forEach var="headerID" items="${structureIDs}">
	<input type="hidden" name="structureIDFilter" value="${headerID}"/>
	</c:forEach>
</c:if>
</form:form>
<script>
$("#ids").scrollTop(0);		// scroll ID text box up to top of its list
</script>
