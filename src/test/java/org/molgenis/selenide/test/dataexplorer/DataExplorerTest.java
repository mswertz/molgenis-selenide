package org.molgenis.selenide.test.dataexplorer;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.molgenis.selenide.model.importer.ImporterModel.EntitiesOptions.ADD;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.molgenis.JenkinsConfig;
import org.molgenis.selenide.model.HomepageModel;
import org.molgenis.selenide.model.dataexplorer.DataExplorerModel;
import org.molgenis.selenide.model.importer.ImporterModel;
import org.molgenis.selenide.test.AbstractSeleniumTest;
import org.molgenis.selenide.test.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class DataExplorerTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(DataExplorerTest.class);
	private DataExplorerModel model;

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Person", "Location");
		restClient.logout(token);
		File emxAllDatatypes = ImporterModel.getFile("org/molgenis/selenide/emx/xlsx/emx_all_datatypes.xlsx");
		open(baseURL);
		HomepageModel homePage = new HomepageModel();
		homePage.menu().openSignInDialog().signIn(uid, pwd).menu().selectImporter().importFile(emxAllDatatypes, ADD)
				.finish().menu().signOut();
	}

	@AfterClass
	public void afterClass()
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Person", "Location");
		restClient.logout(token);
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		model = homepage.menu().selectDataExplorer();
	}

	@Test
	public void test1() throws InterruptedException
	{
		LOG.info("Test data explorer, select TypeTest in entity select...");
		model.selectEntity("TypeTest");
		assertEquals(model.getSelectedEntityTitle().getText(), "TypeTest");

		model.next().previous();

		assertTrue(url().endsWith("dataexplorer?entity=org_molgenis_test_TypeTest#"));
	}

	@Test
	public void test2() throws InterruptedException
	{
		LOG.info("Test data explorer, select TypeTest through URL...");

		open(baseURL + "/menu/main/dataexplorer?entity=org_molgenis_test_TypeTest");		
		model.getSelectedEntityTitle().shouldHave(text("TypeTest"));		
		model.next();
		url().endsWith("dataexplorer?entity=org_molgenis_test_TypeTest#");
	}
}