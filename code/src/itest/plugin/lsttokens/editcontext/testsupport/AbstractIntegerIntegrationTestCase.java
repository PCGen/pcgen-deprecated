package plugin.lsttokens.editcontext.testsupport;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractIntegerIntegrationTestCase<T extends CDOMObject>
		extends AbstractIntegrationTestCase<T>
{

	public abstract boolean isZeroAllowed();

	public abstract boolean isNegativeAllowed();

	public abstract boolean isPositiveAllowed();

//	@Test
//	public void testArchitectire() throws PersistenceLayerException
//	{
//		assertTrue(isPositiveAllowed() || isNegativeAllowed());
//	}
//
//	@Test
//	public void testRoundRobinSimple() throws PersistenceLayerException
//	{
//		if (isPositiveAllowed())
//		{
//			verifyCleanStart();
//			TestContext tc = new TestContext();
//			commit(testCampaign, tc, "1");
//			commit(modCampaign, tc, "2");
//			completeRoundRobin(tc);
//		}
//	}

	@Test
	public void testRoundRobinIdentical() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "1");
			commit(modCampaign, tc, "1");
			completeRoundRobin(tc);
		}
	}

//	@Test
//	public void testRoundRobinZero() throws PersistenceLayerException
//	{
//		if (isZeroAllowed())
//		{
//			verifyCleanStart();
//			TestContext tc = new TestContext();
//			if (isNegativeAllowed())
//			{
//				commit(testCampaign, tc, "-4");
//			}
//			else
//			{
//				commit(testCampaign, tc, "1");
//			}
//			commit(modCampaign, tc, "0");
//			completeRoundRobin(tc);
//		}
//	}
//
//	@Test
//	public void testRoundRobinNegative() throws PersistenceLayerException
//	{
//		if (isNegativeAllowed())
//		{
//			verifyCleanStart();
//			TestContext tc = new TestContext();
//			commit(testCampaign, tc, "-1");
//			commit(modCampaign, tc, "-2");
//			completeRoundRobin(tc);
//		}
//	}
//
//	@Test
//	public void testRoundRobinNoSet() throws PersistenceLayerException
//	{
//		verifyCleanStart();
//		TestContext tc = new TestContext();
//		emptyCommit(testCampaign, tc);
//		if (isPositiveAllowed())
//		{
//			commit(modCampaign, tc, "2");
//		}
//		else if (isNegativeAllowed())
//		{
//			commit(modCampaign, tc, "-3");
//		}
//		completeRoundRobin(tc);
//	}
//
//	@Test
//	public void testRoundRobinNoReset() throws PersistenceLayerException
//	{
//		verifyCleanStart();
//		TestContext tc = new TestContext();
//		if (isPositiveAllowed())
//		{
//			commit(testCampaign, tc, "3");
//		}
//		else if (isNegativeAllowed())
//		{
//			commit(testCampaign, tc, "-2");
//		}
//		emptyCommit(modCampaign, tc);
//		completeRoundRobin(tc);
//	}
}
