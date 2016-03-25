package org.molgenis.selenide.model.importer;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.molgenis.selenide.model.AbstractModel;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImporterModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(ImporterModel.class);

	public static enum EntitiesOptions
	{
		// Add entities
		// Importer adds new entities or fails if entity exists
		ADD,

		// Add entities / update existing
		// Importer adds new entities or updates existing entities
		ADD_UPDATE,

		// Update entities
		// Importer updates existing entities or fails if entity does not exist
		UPDATE;
	}

	// @FindBy(id = "wizard-next-button")
	// private WebElement nextButton;
	//
	// @FindBy(id = "wizard-finish-button")
	// private WebElement finishButton;
	//
	// @FindBy(name = "upload")
	// private WebElement upload;
	//
	// @FindBy(css = "ol.bwizard-steps li:nth-child(1)")
	// private WebElement fileNameSelection;
	//
	// @FindBy(css = "#message-panel .panel-heading")
	// private WebElement messagePanel;
	//
	// @FindBy(xpath = "//input[@name='name']")
	// private WebElement entityNameInput;
	//
	// @FindBy(xpath = "//input[@value='add']")
	// private WebElement addRadioButton;
	//
	// @FindBy(xpath = "//input[@value='add_update']")
	// private WebElement addUpdateRadioButton;
	//
	// @FindBy(xpath = "//input[@value='update']")
	// private WebElement updateRadioButton;
	//
	// @FindBy(xpath = "//input[@value='base']")
	// private WebElement basePackageRadioButton;
	//
	// @FindBy(id = "message")
	// private WebElement message;
	//
	// @FindBy(css = "ol.bwizard-steps li:nth-child(1).active")
	// private WebElement stepOne;
	//
	// @FindBy(css = "ol.bwizard-steps li:nth-child(2).active")
	// private WebElement stepTwo;
	//
	// @FindBy(css = "ol.bwizard-steps li:nth-child(3).active")
	// private WebElement stepThree;
	//
	// @FindBy(css = "ol.bwizard-steps li:nth-child(4).active")
	// private WebElement stepFour;
	//
	// @FindBy(css = "ol.bwizard-steps li:nth-child(5).active")
	// private WebElement stepFive;

	// private final Wait<WebDriver> oneMinuteWait;
	// private final Wait<WebDriver> fiveMinuteWait;

	public ImporterModel()
	{
		// oneMinuteWait = new WebDriverWait(driver, 60);
		// fiveMinuteWait = new WebDriverWait(driver, TimeUnit.MINUTES.toSeconds(5));
	}

	public static File getFile(String relativePath)
	{
		// http://stackoverflow.com/questions/5610256/file-upload-using-selenium-webdriver-and-java
		File file;
		try
		{
			file = new File(ImporterModel.class.getClassLoader().getResource(relativePath).toURI());
		}
		catch (Exception ex)
		{
			file = new File("test-classes/" + relativePath);
		}
		return file;
	}

	public ImporterModel importFile(File file, EntitiesOptions options)
	{
		LOG.info("importFile {}. Options={} ...", file, options);
		uploadFile(file);
		selectOptions(options);
		selectBasePackage();
		validate();
		waitForResult();
		LOG.info("done");
		return this;
	}

	public ImporterModel importVcf(File file, String entityName)
	{
		LOG.info("importFile {}. entityName={} ...", file, entityName);
		uploadFile(file);
		selectEntityName(entityName);
		selectBasePackage();
		validate();
		waitForResult();
		LOG.info("done");
		return this;
	}

	public String getMessageHeader()
	{
		return $("#message-panel .panel-heading").getText();
	}

	public String getMessage()
	{
		return $("#message").getText();
	}

	public ImporterModel uploadFile(File file)
	{
		LOG.info("uploadFile {}...", file);
		assertTrue(file.exists());
		$("ol.bwizard-steps li:nth-child(1).active").click();
		$(By.name("upload")).sendKeys(file.getAbsolutePath());
		$("#wizard-next-button").click();
		return this;
	}

	public ImporterModel finish()
	{
		LOG.info("finish()");
		$("#wizard-finish-button").click();
		$("ol.bwizard-steps li:nth-child(1).active").waitUntil(appears, 60000);
		return this;
	}

	public ImporterModel selectEntityName(String entityName)
	{
		waitForStepTwo();
		$(By.xpath("//input[@name='name']")).clear();
		$(By.xpath("//input[@name='name']")).sendKeys(entityName);
		clickNext();
		return this;
	}

	public void clickNext()
	{
		$("#wizard-next-button").click();
	}

	public void waitForStepTwo()
	{
		$("ol.bwizard-steps li:nth-child(2).active").waitUntil(appears, 60000);
	}

	public ImporterModel selectOptions(EntitiesOptions options)
	{
		LOG.info("selectOptions {}...", options);
		waitForStepTwo();
		switch (options)
		{
			case ADD:
				$(By.xpath("//input[@value='add']")).click();
				break;
			case ADD_UPDATE:
				$(By.xpath("//input[@value='add_update']")).click();
				break;
			case UPDATE:
				$(By.xpath("//input[@value='update']")).click();
				break;
		}
		clickNext();
		return this;
	}

	public ImporterModel selectBasePackage()
	{
		LOG.info("selectBasePackage...");
		$("ol.bwizard-steps li:nth-child(3).active").waitUntil(appears, 60000);
		$(By.xpath("//input[@value='base']")).click();
		clickNext();
		return this;
	}

	public ImporterModel validate()
	{
		LOG.info("validate...");
		$("ol.bwizard-steps li:nth-child(4).active").waitUntil(appears, 60000);
		clickNext();
		return this;
	}

	public ImporterModel waitForResult()
	{
		LOG.info("waitForResult...");
		$("ol.bwizard-steps li:nth-child(5).active").waitUntil(appears, 60000);

		$(By.xpath("//*[contains(.,'Importing...')]")).waitUntil(disappears, 3000000);
		return this;
	}
}
