package org.molgenis.selenide.model.mappingservice;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.molgenis.selenide.model.AbstractModel;
import org.molgenis.selenide.model.component.Select2Model;
import org.molgenis.selenide.model.dataexplorer.DataExplorerModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class MappingProjectDetailsModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(MappingProjectDetailsModel.class);

//	@FindBy(partialLinkText = "Back to mapping project overview")
//	private WebElement backToMappingProjectOverviewButton;
//
//	@FindBy(id = "add-new-attr-mapping-btn")
//	private WebElement addSourceButton;
//
//	@FindBy(id = "submit-new-source-column-btn")
//	private WebElement submitNewSourceColumnButton;
//
//	@FindBy(xpath = "//div[@id='create-new-source-column-modal']//button[contains(text(), 'Cancel')]")
//	private WebElement cancelNewSourceButton;
//
//	@FindBy(css = "#attribute-mapping-table tbody tr")
//	private List<WebElement> mappingProjectTableRows;
//
//	@FindBy(xpath = "//a[@data-target='#create-integrated-entity-modal' and contains(@Class, btn)]")
//	private WebElement createIntegratedDatasetModalButton;
//
//	@FindBy(xpath = "//div[@class='modal-footer']/button[text()='OK']")
//	private WebElement okButton;

	private Select2Model sourceEntitySelect;

	public MappingProjectDetailsModel()
	{
		super();
		sourceEntitySelect = new Select2Model("source-entity-select", false);
	}

	public MappingProjectsModel backToMappingProjectsOverview()
	{
		$(By.partialLinkText("Back to mapping project overview")).click();
		return new MappingProjectsModel();
	}

	public MappingProjectDetailsModel addSource(String sourceEntityName)
	{
		$("#add-new-attr-mapping-btn").click();
		sourceEntitySelect.select(sourceEntityName);
		$("#submit-new-source-column-btn").click();
		spinner().waitTillDone(30, TimeUnit.SECONDS);
		return this;
	}

	public DataExplorerModel createIntegratedDataset(String entityName)
	{
		LOG.info("Create integrated dataset. Entity name = {}", entityName);
		$(By.xpath("//a[@data-target='#create-integrated-entity-modal' and contains(@Class, btn)]")).click();
		return new CreateIntegratedDatasetModalModel().setEntityName(entityName)
				.createIntegratedDataset();
	}

	public AlgorithmEditorModel editAlgorithm(String sourceEntityName, String attributeName)
	{
		By editButtonSelector = By.xpath("//form[@action='/menu/dataintegration/mappingservice/attributeMapping']"
				+ "[input[@name='targetAttribute'][@value='" + attributeName + "']]" + "[input[@name='source'][@value='"
				+ sourceEntityName + "']]/button");
		$(editButtonSelector).click();
		return new AlgorithmEditorModel();
	}

	public MappingProjectDetailsModel removeAttributeMapping(String sourceEntityName, String attributeName)
	{
		By removeButtonSelector = By
				.xpath("//form[@action='/menu/dataintegration/mappingservice/removeAttributeMapping']"
						+ "[input[@name='attribute'][@value='" + attributeName + "']]"
						+ "[input[@name='source'][@value='" + sourceEntityName + "']]/button");
		$(removeButtonSelector).click();
		$(By.xpath("//div[@class='modal-footer']/button[text()='OK']")).click();
		return this;
	}

	public List<List<String>> getMappingProjectTableData()
	{
		return getTableData($$("#attribute-mapping-table tbody tr"));
	}
}
