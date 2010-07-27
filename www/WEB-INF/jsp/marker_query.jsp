<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	
	<link href="/proto/css/mgi_qf.css" rel="stylesheet" type="text/css"/>
	
	<title>Marker Query</title>
</head>
<body>

<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="marker_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">Genes and Markers Query Form</span>
</div>
<!-- end header bar -->

<form:form method="GET" commandName="markerQuery" action="/fewi/mgi/marker/summary">

<!-- query form table -->
<TABLE WIDTH="100%">
	<TR>
		<TD COLSPAN="3" align="left">
			<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
			&nbsp;&nbsp;
			<INPUT TYPE="reset">&nbsp;&nbsp;<em>Search for genes and markers by 
			name, location, GO terms, protein domains, etc.</em>
		</td>
	</tr>

	<!-- gene symbol/name section -->
	<TR CLASS="stripe1">
		<TD CLASS="cat1">Gene/Marker</TD>
		<TD CLASS="data1" COLSPAN="2">
			<div style="float:left;">
				<DL>
					<DT CLASS="qfLabel">
						<A HREF="#">Gene/Marker Symbol, Name, or Keywords</A>:
	      			</DT>
	      			<DD>
	      				<form:input path="query" maxlength="25" cssClass="grayBackground"/>
	      				&nbsp;Search for matching text in: 
	     			</DD>	
	     		</DL>
	     	</div>
			<div class="small">
				<form:checkbox path="vocabularies" value="Nomenclature" />Nomenclature&nbsp;&nbsp;
				<form:checkbox path="vocabularies" value="GO" />Gene Function&nbsp;(GO)&nbsp;&nbsp;
				<form:checkbox path="vocabularies" value="MP" />Abnormal&nbsp;Phenotypes&nbsp;(MP)&nbsp;&nbsp;<br />
				<form:checkbox path="vocabularies" value="GXD" />Expression&nbsp;(AD)&nbsp;&nbsp;
				<form:checkbox path="vocabularies" value="OMIM" />Disease Models&nbsp;(OMIM)&nbsp;&nbsp;
				<form:checkbox path="vocabularies" value="IP" />Protein Domains&nbsp;(InterPro)&nbsp;&nbsp;
			</div>
		</TD>
	</TR>

    <!-- map position section -->
	<TR CLASS="stripe2">
		<TD CLASS="cat2">Map position</TD>
		<TD CLASS="data2" COLSPAN="2">
			<div>

				<DL>
					<DT CLASS="qfLabel">
						<A HREF="#">Chromosome(s)</A>:	    
					</DT>
					<DD>
						<div>
							<form:select path="chromosomes" items="${markerQuery.chrOptions}" size="5"/>
							<span class="example">
	  							To select multiple chromosomes, hold down the Ctrl 
	  							or command (Apple) key and click the desired chromosomes.
	  						</span>
						</div>
    				</DD>
  				</DL>
			</div>
			<div>
				<div style="float:left;">
					<DL>
						<DT CLASS="qfLabel">
							<A HREF="#">Genome Coordinates</A>:
							<SPAN CLASS="example">from NCBI Build 37</SPAN>
						</DT>
    					<DD>
							<form:input path="genomeCoordinates" maxlength="23" />
							<form:select path="coordinateUnits" items="${markerQuery.coordOptions}" />
						</DD>
						<DD>
    						<SPAN CLASS="example">e.g., "125.618-125.622" Mbp<br />
							or "0-5000" bp or "246.2-99999" Mbp</SPAN>
						</DD>
					</DL>
				</div>
				<div style="float:left;vertical-align:middle; margin:25px 10px 0px 15px;">
					<strong>OR</strong>
				</div>
				<div style="float:left;">
					<DL>
						<DT CLASS="qfLabel">
							<A HREF="#">cM Position</A>:
						</DT>
						<DD>
							<form:input path="cmPosition" maxlength="13" />
						</DD>
						<DD>
							<SPAN CLASS="example">e.g., "10.0-40.0"<br />
							or "0-15" or "45.3-100"</SPAN>
						</DD>
  					</DL>
				</div>
			</div>
		</TD>
	</TR>
    <TR>
		<TD COLSPAN="3" align="left">
			<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
			&nbsp;&nbsp;
			<INPUT TYPE="reset">
		</TD>
    </TR>
</TABLE>
</form:form>


</body>
</html>

