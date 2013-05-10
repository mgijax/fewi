package org.jax.mgi.fewi.test;

import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration({"classpath:applicationContext-ci.xml"})
public class NTCTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		Thread.sleep(10000);
		String s = "blah \\Allele(MGI:12345||) \\Ref(MGI!12345678||) \\Acc(MGI:123456||) blah & blah \\Link(http://compbio.dfci.harvard.edu/tgi/cgi-bin/tgi/tc_report.pl?tc=TC1619255&species=mouse%()|Link Name|)";
		String s2 = "";
		for(int i=0;i<10000;i++)
		{
			NotesTagConverter ntc = new NotesTagConverter();
			String sc = ntc.convertNotes(FormatHelper.formatVerbatim(s), '|');
		}
		System.out.println("Done");
	}

}
