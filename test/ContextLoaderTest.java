/*
 * This is here to wrap the concordion index file with concordion:run commands
 */


import org.jax.mgi.fewi.config.ContextLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-ci.xml"})
@TransactionConfiguration
@Transactional
public class ContextLoaderTest {
	
	@Test
	public void testCommonFewiProperties()
	{
		Assert.assertNotEquals(null,ContextLoader.getConfigBean().getProperty("SNP_BUILD"));
		Assert.assertNotEquals("",ContextLoader.getConfigBean().getProperty("SNP_BUILD"));
	}
}
