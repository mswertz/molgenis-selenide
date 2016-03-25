package org.molgenis.selenide.model;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

import org.molgenis.selenide.model.dataexplorer.DataExplorerModel;
import org.molgenis.selenide.model.importer.ImporterModel;
import org.molgenis.selenide.model.mappingservice.MappingProjectsModel;
import org.molgenis.selenide.model.mappingservice.TagWizardModel;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a util for the use of the Molgenis Menu
 */
public class MenuModel
{
	private static final Logger LOG = LoggerFactory.getLogger(MenuModel.class);

//	@FindBy(id = "spinner")
//	WebElement spinner;
//
//	@FindBy(id = "open-button")
//	WebElement signinButton;
//
//	@FindBy(id = "signout-button")
//	WebElement signoutButton;
//
//	@FindBy(linkText = "Upload")
//	WebElement uploadMenuItem;
//
//	@FindBy(linkText = "Data Explorer")
//	private WebElement dataExplorerMenuItem;
//
//	@FindBy(linkText = "Data Integration")
//	private WebElement dataIntegrationMenuItem;
//
//	@FindBy(linkText = "Mapping Service")
//	private WebElement mappingServiceMenuItem;
//
//	@FindBy(linkText = "Tag Wizard")
//	private WebElement tagWizardMenuItem;

	public MenuModel()
	{
	}

	public SignInModel openSignInDialog()
	{
		LOG.info("openSignInDialog");
		$("#open-button").click();
		return new SignInModel();
	}

	public boolean isLoggedIn()
	{
		return $("#signout-button").isDisplayed();
	}

	public HomepageModel signOut()
	{
		LOG.info("signOut");
		$("#signout-button").click();
		return new HomepageModel();
	}

	public boolean isSignedOut()
	{
		return $("#open-button").isDisplayed();
	}

	public ImporterModel selectImporter()
	{
		LOG.info("Select Importer...");
		$(By.linkText("Upload")).click();
		return new ImporterModel();
	}

	public DataExplorerModel selectDataExplorer()
	{
		LOG.info("Select Data explorer...");
		$(By.linkText("Data Explorer")).click();
		return new DataExplorerModel();
	}

	public MappingProjectsModel selectMappingService()
	{
		LOG.info("Select Mapping Service...");
		$(By.linkText("Data Integration")).click();
		$(By.linkText("Mapping Service")).click();
		return new MappingProjectsModel();
	}

	public TagWizardModel selectTagWizard()
	{
		LOG.info("Select Tag wizard...");
		$(By.linkText("Data Integration")).click();
		$(By.linkText("Tag Wizard")).click();
		return new TagWizardModel();
	}

}
