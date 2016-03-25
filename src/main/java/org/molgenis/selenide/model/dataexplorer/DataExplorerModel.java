package org.molgenis.selenide.model.dataexplorer;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.WebDriverRunner.*;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.molgenis.selenide.model.AbstractModel;
import org.molgenis.selenide.model.component.Select2Model;
import org.molgenis.selenide.model.dataexplorer.annotators.AnnotatorModel;
import org.molgenis.selenide.model.dataexplorer.data.DataModel;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.SelenideElement;

import autovalue.shaded.com.google.common.common.collect.Lists;

/**
 * This is a model of the MOLGENIS Data Explorer user interface
 */
public class DataExplorerModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(DataExplorerModel.class);

	public static enum DeleteOption
	{
		DATA, DATA_AND_METADATA;
	}

	private final Select2Model entityModel;

//	@FindBy(css = ".page-next a")
//	private WebElement next;
//
//	@FindBy(css = ".page-prev a")
//	private WebElement previous;
//
//	@FindBy(id = "entity-class-name")
//	private WebElement entityClassName;
//
//	@FindBy(id = "dropdownMenu1")
//	private WebElement deleteDropdownMenu;
//
//	@FindBy(id = "delete-data-btn")
//	private WebElement deleteDataButton;
//
//	@FindBy(id = "delete-data-metadata-btn")
//	private WebElement deleteDataMetadataBtn;
//
//	@FindBy(css = "[data-bb-handler=confirm]")
//	private WebElement confirmButton;
//
//	@FindBy(linkText = "Annotators")
//	private WebElement annotatorTab;
//
//	@FindBy(linkText = "Data")
//	private WebElement dataTab;
//
//	@FindBy(css = "a.tree-deselect-all-btn")
//	private WebElement deselectAllButton;
//
//	@FindBy(css = "div.molgenis-tree span.fancytree-has-children span.fancytree-checkbox")
//	private List<WebElement> treeFolders;
//
//	@FindBy(css = ".molgenis-table-container tbody tr")
//	private List<WebElement> tableRows;
//
//	@FindBy(css = "#copy-data-btn")
//	private WebElement copyCheckBtn;

	public DataExplorerModel()
	{
		entityModel = new Select2Model( "dataset-select", false);
	}

	public void deleteEntity(DeleteOption deleteOption)
	{
		String selectedEntity = getSelectedEntityTitle().getText();
		LOG.info("deleteEntity {}, mode={} ...", selectedEntity, deleteOption);
		$("#dropdownMenu1").click();

		switch (deleteOption)
		{
			case DATA:
				$("#delete-data-btn").click();
				break;
			case DATA_AND_METADATA:
				$("#delete-data-metadata-btn").click();
				break;
			default:
				break;
		}
		$("[data-bb-handler=confirm]").click();
		spinner().waitTillDone(20, TimeUnit.SECONDS);
	}

	public DataExplorerModel selectEntity(String entityLabel)
	{
		LOG.info("selectEntity", entityLabel);
		entityModel.select(entityLabel);
		return this;
	}

	public SelenideElement getSelectedEntityTitle()
	{
		return $("#entity-class-name");
	}

	public DataExplorerModel next()
	{
		$(".page-next a").click();
		return this;
	}

	public DataExplorerModel previous()
	{
		$(".page-prev a").click();
		return this;
	}

	public AnnotatorModel selectAnnotatorTab()
	{
		LOG.info("Select annotator tab...");
		$(By.linkText("Annotators")).click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return new AnnotatorModel();
	}

	public DataModel selectDataTab()
	{
		LOG.info("Select data tab...");
		$(By.linkText("Data")).click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return new DataModel();
	}

	public DataExplorerModel deselectAll()
	{
		$("a.tree-deselect-all-btn").click();
		return this;
	}

	/**
	 * Clicks on an attribute's checkbox in the attribute tree.
	 * 
	 * @param attributeName
	 * @return
	 */
	public DataExplorerModel clickAttribute(String attributeName)
	{
		LOG.info("Click on attribute: " + attributeName);
		$(By.xpath("//div[@class='molgenis-tree']//li[span/span/text()='" + attributeName
						+ "']/span/span[@class='fancytree-checkbox']")).click();
		return this;
	}

	/**
	 * Retrieves the currently displayed table data, row by row. The first three columns are skipped.
	 */
	public List<List<String>> getTableData()
	{
		LOG.info("getTableData...");
		LOG.info("Contents: "+$(".molgenis-table-container tbody").innerHtml());
		List<List<String>> temp = getTableData($$(".molgenis-table-container tbody tr"));
		LOG.info("getTableData temp={}", temp);
		List<List<String>> result = getTableData($$(".molgenis-table-container tbody tr")).stream().map(row -> row.subList(3, row.size())).collect(toList());
		LOG.info("getTableData result={}", result);
		return result;
	}

	/**
	 * Returns the fully qualified name of the currently displayed entity, based on the driver's URL.
	 */
	public Optional<String> getEntityNameFromURL()
	{
		try
		{
			List<NameValuePair> params = URLEncodedUtils.parse(new URI(url()), "UTF-8");
			return params.stream().filter(nvp -> "entity".equals(nvp.getName())).findFirst()
					.map(NameValuePair::getValue);
		}
		catch (URISyntaxException e)
		{
			return empty();
		}
	}

	public DataExplorerModel copyEntity(String newEntityName)
	{
		LOG.info("Start copy");

		$("#copy-data-btn").waitUntil(appears, 60000);
		$("#copy-data-btn").click();

		SelenideElement input = $("input.bootbox-input");
		input.clear();
		input.sendKeys(newEntityName);

		SelenideElement okBtn = $("div.bootbox .modal-footer button.btn-primary");
		okBtn.click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);

		LOG.info("Finished copy");
		return this;
	}
}
