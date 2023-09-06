
	<form:form commandName="batchQueryForm" enctype="multipart/form-data" method="post" action="${configBean.FEWI_URL}batch/summary">
		<table class="borderedTable pad0">
			<tr class="queryControls">
				<td colspan="2" align="left" style="padding:5px;">
					<input class="buttonLabel" value="Search" type="submit">
					<input type="reset">
					<span class="example">&nbsp;Download gene/marker data for a batch of IDs or symbols.</span>
				</td>
			</tr>
			<tr>
				<td class="resultsHeader" style="padding:5px;">Input</td>
				<td class="resultsHeader" style="padding:5px;">Output</td>
			</tr>
			<tr class="stripe2" style="text-align:left;">
				<td>
					<table class="noBorder pad0">
						<tr>
							<td style="padding:5px;"><span class="label">Type:</span>
								&nbsp;&nbsp;<form:select path="idType" items="${batchQueryForm.idTypes}">
	                        		<form:options items="${idTypes}" />
	                        	</form:select>
							</td>
						</tr>
						<tr>
							<td style="padding:5px;"><span class="label">Source:</span></td>
						</tr>
						<tr>
							<td>
								<div id="batchSource" class="yui-navset batch">
									<ul class="yui-nav">
										<li id="enterTextButton" class="selected"><a href="#tab1"><em>Enter Text</em></a></li>
										<li><a href="#tab2"><em>Upload File</em></a></li>
									</ul>
									<div class="yui-content" style="height:10em; width:300px; padding:5px;">
										<div>
											<div>
												<span class="label">ID/Symbols List:</span><br/>
												&nbsp;&nbsp;<form:textarea id="ids" path="ids" class="formWidth"></form:textarea><br/>
												&nbsp;&nbsp;<span class="example">*tab, space, and newline separated ids.</span>
											</div>
											<div id="uploadMessage" style="display: none">
											</div>
										</div>
										<div>
											<div style="float:left;">
												<span class="label"> File Type:</span><br/>
												<span>&nbsp;&nbsp;<form:radiobutton path="fileType" value="tab"/>tab-delimited</span><br>
												<span>&nbsp;&nbsp;<form:radiobutton path="fileType" value="csv"/>comma separated</span>
											</div>
											<div>
												<span class="label nowrap"> ID/Symbols column:</span><br>
												<span>&nbsp;&nbsp;<form:input path="idColumn" size="3"></form:input></span><br>
												<span class="example">*ID/Symbols parsed from<br/>a single column</span>
											</div>
											<div>
												<span class="label">ID/Symbols File:</span><br>
												&nbsp;&nbsp;<input type="file" name="idFile" onChange="readFile(event)"><br/>
											</div>
										</div>
									</div>
								</div>
							</td>
						</tr>
					</table>
				</td>
				<td valign="top" style="padding:5px;">
					<div class="batch" style="white-space:normal;">
						<span class="label">Gene Attributes:</span><br/>
						<div class="options">
							<form:checkboxes path="attributes" items="${batchQueryForm.attributeList}" />
						</div>
					</div>

					<div class="batch" style="white-space:normal;clear:both;">
						<span class="label">Additional Information:</span><br/>
						<div class="options">
							<form:radiobuttons path="association" items="${batchQueryForm.associationList}" />
						</div>
					</div>
				</td>
			</tr>
			<tr class="queryControls">
				<td colspan="2" align="left" style="padding:5px;">
					<input class="buttonLabel" value="Search" type="submit">
					<INPUT type="reset">
				</td>
			</tr>
		</table>
		<form:hidden path="viaQF" value="true" />
	</form:form>
