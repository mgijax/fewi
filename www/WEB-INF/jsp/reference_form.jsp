<!-- query form table -->
<form:form method="GET" commandName="referenceQueryForm" action="${configBean.FEWI_URL}reference/summary">
<TABLE WIDTH="100%" class="pad5 borderedTable">
	<TR class="queryControls">
		<TD COLSPAN="2" align="left">
			<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
			&nbsp;&nbsp;
			<INPUT TYPE="reset">
			&nbsp;&nbsp;
			<form:errors cssClass="qfError"/>
		</td>
	</tr>

	<TR CLASS="stripe1">
		<TD CLASS="cat1">Author</TD>
		<TD>
			<div>
				<div style="float:left; width:300px;text-align:left;">
					<div id="authorAutoComplete" style="position:relative; z-index:100;">
						<form:input id="author" path="author"></form:input>
						<div id="authorContainer"></div>
					</div>
					<div id="authHelp" style="display:none;" class="example">Continue typing to add another Author.</div>
				</div>
				<div style="float:left; text-align:left;">
					<form:radiobutton id="authorScope1" path="authorScope" value="any" checked="checked"/> Any Author(s)<br/>
		    		<form:radiobutton id="authorScope2" path="authorScope" value="first"/> First Author<br/>
		    		<form:radiobutton id="authorScope3" path="authorScope" value="last"/> Last Author
				</div>
    		</div>
		</TD>
	</TR>

	<TR CLASS="stripe2">
		<TD CLASS="cat2">Journal</TD>
		<TD>
			<div>
				<div style="float:left;width:300px;height:3em;text-align:left;">
					<div id="journalAutoComplete">
						<form:input id="journal" path="journal"></form:input>
						<div id="journalContainer"></div>
					</div>
					<div id="journalHelp" style="display:none;" class="example">Continue typing to add another Journal.</div>
				</div>
				<div style="height:3em;" class="example">
					<div style="float:left;text-align:left;vertical-align:middle;width:7em;" class="example">
						Examples:<br/>
						(See <a href="https://www.ncbi.nlm.nih.gov/nlmcatalog?term=journalspmc">NLM</a>.)
					</div>
					<div style="text-align:left;" class="example">
						Proc Natl Acad Sci USA<br/>
						J Cell Mol Med 
					</div>
				</div>
    		</div>
		</TD>
	</TR>
	<tr  CLASS="stripe1">
		<td CLASS="cat1">Year</td>
		<td>
			<div style="height:4em;">
				<div style="float:left;width:300px;height:4.5em;text-align:left;">
					<form:input id="year" path="year" class="formWidth"></form:input><br/>
					<form:errors path="year" cssClass="qfError"/>
				</div>
				<div style="height:4em;" class="example">
					<div style="float:left;text-align:left;line-height:4.5em;vertical-align:middle;width:7em;" class="example">
						Examples:
					</div>
					<div style="text-align:left;" class="example">
						2008<br/>
						1990-2004<br/>
						-2007 (from the earliest reference through 2007)<br/>
						2009- (from 2009 through the present)
					</div>
				</div>
    		</div>
		</td>
	</tr>
	<tr  CLASS="stripe2">
		<td CLASS="cat2">Text</td>
		<td>
			<div style="height:5em;">
				<div style="float:left;width:300px;text-align:left;">
					<form:textarea id="text" path="text" class="formWidth"></form:textarea><br/>
					<form:checkbox id="inTitle1" path="inTitle" /> In Title
					<form:checkbox id="inAbstract1" path="inAbstract" /> In Abstract
				</div>
				<div style="height:4em;" class="example">
					<div style="float:left;text-align:left;line-height:4em;vertical-align:middle;width:7em;" class="example">
						Examples:
					</div>
					<div style="text-align:left;" class="example">
						oocyte, spermatocyte<br/>
						"telomeres in meiocytes"<br/>
						5-bromo-2'-deoxyuridine-positive cells<br/>
					spastic paraplegia spinocerabellar ataxia cerebellum
					</div>
				</div>
    		</div>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<div style="width:800px; text-align: center; font-weight: bold;">OR</div>
		</td>
	</tr>
	<tr  CLASS="stripe1">
		<td  CLASS="cat1">
			PubMed ID or<br/>
			MGI Reference ID
		</td>
		<td>
			<div style="height:3em;">
				<div style="float:left; width:300px;text-align:left;">
					<form:input id="id" path="id" class="formWidth" value="" maxlength="256"></form:input><br/>
					<form:errors path="id" cssClass="qfError"/>
				</div>
				<div style="height:4em;" class="example">
					<div style="float:left;text-align:left;line-height:4em;vertical-align:middle;width:7em;" class="example">
						Examples:
					</div>
					<div style="text-align:left;" class="example">
					20339075 (PubMed)<br/>
					J:159210 (MGI reference ID)<br/>
					18989690; 18192873; J:159210 (List)
					</div>
				</div>

    		</div>
		</td>
	</tr>
    <TR class="queryControls">
		<TD COLSPAN="3" align="left">
			<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
			&nbsp;&nbsp;
			<INPUT TYPE="reset">
			&nbsp;&nbsp;
			<form:errors cssClass="qfError"/>
		</TD>
    </TR>
</TABLE>
</form:form>