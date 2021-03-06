package org.jax.mgi.fewi.test.concordion;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.internal.listener.AssertResultRenderer;
import org.concordion.internal.listener.VerifyRowsResultRenderer;

public class FewiConcordionExtension implements ConcordionExtension {

	public static String NAMESPACE = "http://fewi.custom.commands.FewiExtensions";
	
	public void addTo(ConcordionExtender concordionExtender) {
		//========== Add Custom Commands ===========
		//Add verifyRowsUnorderedCommand
		VerifyRowsUnorderedCommand verifyRowsUnordered = new VerifyRowsUnorderedCommand();
		verifyRowsUnordered.addVerifyRowsListener(new VerifyRowsResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"verifyRowsUnordered", verifyRowsUnordered);
		
		//Add verifySubsetOfCommand
		VerifySubsetOfCommand verifySubsetOf = new VerifySubsetOfCommand();
		verifySubsetOf.addVerifyRowsListener(new VerifyRowsResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"verifySubsetOf", verifySubsetOf);
		
		//Add verifySupersetOfCommand
		VerifySupersetOfCommand verifySupersetOf = new VerifySupersetOfCommand();
		verifySupersetOf.addVerifyRowsListener(new VerifyRowsResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"verifySupersetOf", verifySupersetOf);
		
		//Add verifyNotRowsCommand
		VerifyNotRowsCommand verifyNotRows = new VerifyNotRowsCommand();
		verifyNotRows.addVerifyRowsListener(new VerifyRowsResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"verifyNotRows", verifyNotRows);

		//Add assertEmptyCommand
				AssertEmptyCommand assertEmpty = new AssertEmptyCommand();
				assertEmpty.addAssertEqualsListener(new AssertResultRenderer());
				concordionExtender.withCommand(NAMESPACE, 
						"assertEmpty", assertEmpty);
				
		//Add assertAllRowsEqualCommand
		AssertAllRowsEqualCommand assertAllRowsEqual = new AssertAllRowsEqualCommand();
		assertAllRowsEqual.addAssertEqualsListener(new AssertResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"assertAllRowsEqual", assertAllRowsEqual);

		//Add assertNoResultsContainCommand
		AssertNoResultsContainCommand assertNoResultsContain = new AssertNoResultsContainCommand();
		assertNoResultsContain.addAssertEqualsListener(new AssertResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"assertNoResultsContain", assertNoResultsContain);
		
		//Add assertValueAppearsOnlyOnceCommand
		AssertValueAppearsOnlyOnceCommand assertValueAppearsOnlyOnce = new AssertValueAppearsOnlyOnceCommand();
		assertValueAppearsOnlyOnce.addAssertEqualsListener(new AssertResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"assertValueAppearsOnlyOnce", assertValueAppearsOnlyOnce);
				
		//Add assertAllResultsContainTextCommand
		AssertAllResultsContainTextCommand assertAllResultsContainText = new AssertAllResultsContainTextCommand();
		assertAllResultsContainText.addAssertEqualsListener(new AssertResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"assertAllResultsContainText", assertAllResultsContainText);
		
		//Add assertResultsContainCommand
		AssertResultsContainCommand assertResultsContain = new AssertResultsContainCommand();
		assertResultsContain.addAssertEqualsListener(new AssertResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"assertResultsContain", assertResultsContain);
				
		//Add custom executeCommand
		ExecuteCommand execute = new ExecuteCommand();
		// Do we need the executeListener?
		concordionExtender.withCommand(NAMESPACE, 
				"execute", execute);
				
		//Add custom assertEqualsCommand
		AssertEqualsCommand assertEquals = new AssertEqualsCommand();
		assertEquals.addAssertEqualsListener(new AssertResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"assertEquals", assertEquals);
		
		//Add assertNotEqualsCommand
		AssertNotEqualsCommand assertNotEquals = new AssertNotEqualsCommand();
		assertNotEquals.addAssertEqualsListener(new AssertResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"assertNotEquals", assertNotEquals);
		
		//Add assertEqualsCICommand
		AssertEqualsCICommand assertEqualsCI = new AssertEqualsCICommand();
		assertEqualsCI.addAssertEqualsListener(new AssertResultRenderer());
		concordionExtender.withCommand(NAMESPACE, 
				"assertEqualsCI", assertEqualsCI);
		
		//Add dynamicDataCommand
		DynamicDataCommand dynamicData = new DynamicDataCommand();
		concordionExtender.withCommand(NAMESPACE, 
				"dynamicData", dynamicData);
		
		//Add dynamicDataTableCommand
				DynamicDataTableCommand dynamicDataTable = new DynamicDataTableCommand();
				concordionExtender.withCommand(NAMESPACE, 
						"dynamicDataTable", dynamicDataTable);
		
		//Add echoCommand
		EchoCommand echo = new EchoCommand();
		concordionExtender.withCommand(NAMESPACE, 
				"echo", echo);
		
		concordionExtender.withLinkedCSS("/concordion_master.css", new Resource("/concordion_master.css"));
		
	}
}
