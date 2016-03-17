<table name="snp_seq" style="border: 1px solid black;">
	<tbody>
		<tr>
			<td bgcolor="#FFFFFF" style="padding: 5px;">
<pre>
${snp.flank5PrimeFormatted}<b>${snp.iupaccode}</b>
${snp.flank3PrimeFormatted}
<b>${snp.iupaccode}</b> = ${snp.alleleSummary}
</pre>
<br/>
				<font class="small">
					<i>Note: Sequence in lower case indicates low-complexity or repetitive sequence</i>
				</font>
				<hr>
				<form name="seqPullDownForm" method="GET" action="http://www.informatics.jax.org/sequence/blast" target="_blank">
					<input type="hidden" name="blastSpec" value="OGP__10090__9559">
					<input type="hidden" name="snpID" value="${snp.accid}">
					<input type="hidden" name="snpFlank" value="${snp.flank5Prime}${snp.iupaccode}${snp.flank3Prime}">
					<font class="small">
						<i>BLAST SNP flanking sequence against the mouse Genome</i>
						<br>
						<select name="seqPullDown">
								<option value="">send flank to NCBI BLAST</option>
						</select>
						<input type="button" value="Go" onclick="submit()">
					</font>
				</form>
			</td>
		</tr>
	</tbody>
</table>

