package org.molgenis.selenide.model.dataexplorer.annotators;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Condition.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.molgenis.selenide.model.AbstractModel;
import org.molgenis.selenide.model.dataexplorer.DataExplorerModel;
import org.molgenis.selenide.model.dataexplorer.data.DataModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.SelenideElement;

public class AnnotatorModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorModel.class);

//	@FindBy(css = "#enabled-annotator-selection-container input")
//	private List<WebElement> availableCheckboxes;
//
//	@FindBy(css = "#enabled-annotator-selection-container input:checked")
//	private List<WebElement> selectedCheckboxes;
//
//	@FindBy(id = "enabled-annotator-selection-container")
//	private WebElement enabledAnnotatorSelectionContainer;
//
//	@FindBy(id = "annotate-dataset-button")
//	private WebElement annotateButton;
//
//	@FindBy(css = "a.select-all-btn")
//	private WebElement selectAll;
//
//	@FindBy(css = "a.deselect-all-btn")
//	private WebElement deselectAll;
//
//	@FindBy(linkText = "Data")
//	private WebElement dataTab;

	public AnnotatorModel()
	{
		super();
	}

	public DataExplorerModel selectDataTab()
	{
		LOG.info("Select Data tab...");
		$(By.linkText("Data")).click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return new DataExplorerModel();
	}

	private void waitForAnnotators()
	{
		$("#enabled-annotator-selection-container").waitUntil(visible, 10000);
	}

	public AnnotatorModel select(String annotator)
	{
		LOG.info("Select {}...", annotator);
		SelenideElement checkbox = findAnnotatorCheckbox(annotator);

		if (!checkbox.isSelected())
		{
			checkbox.click();
		}

		return this;
	}

	public AnnotatorModel selectAll()
	{
		LOG.info("Select All...");
		waitForAnnotators();
		$("a.select-all-btn").click();
		return this;
	}

	public AnnotatorModel deselectAll()
	{
		LOG.info("Deselect All...");
		waitForAnnotators();
		$("a.deselect-all-btn").click();
		return this;
	}

	public AnnotatorModel deselect(String annotator)
	{
		LOG.info("deselect {}...", annotator);
		WebElement checkbox = findAnnotatorCheckbox(annotator);
		if (checkbox.isSelected())
		{
			checkbox.click();
		}
		return this;
	}

	private SelenideElement findAnnotatorCheckbox(String annotator)
	{
		return $(By.cssSelector("#annotator-select-container input[value=" + annotator + "]"));
	}

	public List<String> getSelectedAnnotators()
	{
		LOG.info("getSelectedAnnotators()...");
		List<String> result = $$("#enabled-annotator-selection-container input:checked").stream().map(e -> e.getAttribute("value"))
				.collect(Collectors.toList());
		LOG.debug("result = {}", result);
		return result;
	}

	public List<String> getAvailableAnnotators()
	{
		return $$("#enabled-annotator-selection-container input").stream().map(e -> e.getAttribute("name")).collect(Collectors.toList());
	}

	public DataExplorerModel clickAnnotateButtonAndWait(int timeout)
	{
		$("#annotate-dataset-button").click();

		DataModel dataModel = new DataModel();
		dataModel.waitUntilReady(timeout);

		return new DataExplorerModel();
	}

	public AnnotatorModel clickCopy(String entityName, String newEntityName)
	{
		LOG.info("Copy [" + entityName + "] and create [" + newEntityName + "]");
		selectDataTab().selectEntity(entityName);
		DataExplorerModel dataExplorerModel = new DataExplorerModel();
		dataExplorerModel.copyEntity(newEntityName);
		return this;
	}
}
