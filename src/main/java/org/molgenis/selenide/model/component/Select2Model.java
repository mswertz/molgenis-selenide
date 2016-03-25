package org.molgenis.selenide.model.component;

import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.Condition.*;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

/**
 * Model for a select2 selection box. This class works for both multi selects and single selects.
 */
public class Select2Model
{
	private static final Logger LOG = LoggerFactory.getLogger(Select2Model.class);
	private String id;
	// Selector for the close buttons for the selected choices.
	private final By closeButtonSelector;
	// Selector for the selected choices
	private final By choiceSelector;
	private final By selectedOptionSelector;
	private final By selectSelector;
	private boolean multi;

	/**
	 * Creates a new Select2Model
	 * 
	 * @param driver
	 *            the {@link WebDriver} to use
	 * @param selector
	 *            the id of the Select2, without the s2id_ prefix
	 */
	public Select2Model(String id, boolean multi)
	{
		this.id = id.replaceFirst("s2id_", "");
		this.multi = multi;
		closeButtonSelector = By
				.xpath("//div[@id='s2id_" + this.id + "']//a[contains(@class, 'select2-search-choice-close')]");
		choiceSelector = By.xpath("//div[@id='s2id_" + this.id + "']//.[contains(@class, 'select2-choice')]");
		selectedOptionSelector = By
				.cssSelector("#s2id_" + this.id + (multi ? " .select2-choices" : " .select2-choice"));
		selectSelector = By.id("s2id_" + this.id);
	}

	/**
	 * Retrieves the labels for the selected items.
	 * 
	 * @return List containing the selected item texts
	 */
	public List<String> getSelectedLabels()
	{
		LOG.debug("Get selected labels in Select2 with id {}...", id);
		
		return $$(choiceSelector).stream().map(SelenideElement::getText).map(String::trim)
				.collect(toList());
	}

	/**
	 * Removes all selected items.
	 */
	public void clearSelection()
	{
		LOG.info("Clear selection in Select2 with id {}...", id);
		while (!StringUtils.isEmpty($(selectSelector).getText().trim()))
		{
			// FIXME It is not working for non multi select2.
			$(closeButtonSelector).click();
		}
		LOG.debug("Selection is empty.");
	}

	/**
	 * Types terms into the select box, waits for a matching result to appear and adds them to the selection.
	 * 
	 * @param term
	 *            the term to type
	 * @throws InterruptedException
	 */
	public void select(String... terms)
	{
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (String term : terms)
		{
			map.put(term, term);
		}
		select(map);
	}

	/**
	 * Types terms into the select box, waits for a matching result to appear and adds them to the selection.
	 * 
	 * @param idsAndLabels
	 *            Map mapping the ID of the term to the label of the term
	 * @throws InterruptedException
	 */
	public void select(Map<String, String> idsAndLabels)
	{
		LOG.info("Selecting terms with ids and labels {} in Select2 with id {}...", idsAndLabels, id);
		for (Map.Entry<String, String> entry : idsAndLabels.entrySet())
		{
			if (entry.getKey().isEmpty() || entry.getValue().isEmpty()) continue;

			LOG.debug("Click select.");
			SelenideElement select2Option = $(selectedOptionSelector);
			select2Option.click();

			LOG.debug("Wait for text input box...");
			SelenideElement select2InputText = $(
					By.cssSelector(multi ? "#s2id_" + id + " input" : "#select2-drop input"));

			LOG.debug("Empty text input box..");
			select2InputText.clear();

			LOG.debug("Text input box empty. Entering term...");
			select2InputText.sendKeys(entry.getKey());

			SelenideElement match = $(By.xpath(
					"//div[contains(@class,'select2-result-label')][normalize-space(.)='" + entry.getKey() + "']"));
			LOG.debug("Click match..");
			match.click();

			LOG.debug("Waiting for selection to appear in the list of search choices...");
			
			if(multi)
			{
				$(By.xpath("//div[@id='s2id_" + id + "']")).waitUntil(text(entry.getValue()), 6000000);
			}
			else
			{
				$(selectedOptionSelector).waitUntil(text(entry.getValue()), 6000000);
			}
		
			LOG.debug("Selected '{}'.", entry);
		}
		LOG.debug("Selected terms with ids and labels {}. Selected labels are: {}.", idsAndLabels, getSelectedLabels());
	}

	/**
	 * Types terms into the select box, waits for a matching result to appear and adds them to the selection.
	 * 
	 * @param idsAndLabels
	 *            Map mapping the ID of the term to the label of the term
	 * @throws InterruptedException
	 */
	public void selectReactForms(Map<String, String> idsAndLabels)
	{
		LOG.info("Selecting terms with ids and labels {} in Select2 with id {}...", idsAndLabels, id);
		for (Map.Entry<String, String> entry : idsAndLabels.entrySet())
		{
			if (entry.getKey().isEmpty() || entry.getValue().isEmpty()) continue;

			LOG.debug("Click select.");
			$(selectedOptionSelector).click();

			By select2InputTextBy = By.cssSelector(multi ? "#s2id_" + id + " input" : "#select2-drop input");

			LOG.info("Type value in the search box..");
			addValueToInputField(select2InputTextBy, entry.getKey());

			LOG.info("Waiting for match..");
			By by = By.cssSelector(
					"#select2-drop:not([style*='display: none']) .select2-results li.select2-result-selectable");
			retryingToFindClick(by);
			LOG.info("Click match..");

			LOG.info("Selected '{}'.", entry);
		}
		LOG.debug("Selected terms with ids and labels {}. Selected labels are: {}.", idsAndLabels, getSelectedLabels());
	}

	private boolean retryingToFindClick(By by)
	{
		boolean result = false;
		int count = 0;
		while (count < 3)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e1)
			{
				LOG.warn("InterruptedException id thrown for Thread.sleep(500) in Select2Model");
			}

			if ($(by).exists())
			{
				$(by).click();
				LOG.info("Click on first match..");
				result = true;
				break;
			}

			count++;
		}
		return result;
	}

	private boolean addValueToInputField(By by, String value)
	{
		boolean result = false;

		LOG.debug("Wait for text input box...");
		SelenideElement select2InputText = $(by);

		LOG.debug("Empty text input box..");
		select2InputText.clear();

		LOG.debug("Text input box empty. Entering term...");
		select2InputText.sendKeys(value);

		$(by).waitUntil(value(value), 30000);
		result = true;

		return result;
	}
}
