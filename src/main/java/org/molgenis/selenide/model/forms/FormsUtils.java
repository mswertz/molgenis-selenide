package org.molgenis.selenide.model.forms;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.selenide.model.AbstractModel;
import org.molgenis.selenide.model.component.Select2Model;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.google.common.base.Predicate;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.Condition.*;

public class FormsUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(FormsUtils.class);
	private static final String NONCOMPOUND_CONTAINER = "div";
	private static final String COMPOUND_CONTAINER = "fieldset";

	public FormsUtils()
	{
	}

	public static void changeValueNoncompoundAttributeUnsafe(By context, String simpleName, String value)
	{
		SelenideElement input = $(context).find(findAttributeInputBy(simpleName, false, true));
		try
		{
			typeTextIntoInput(value, input);
		}
		catch (Exception ex)
		{
			LOG.warn("Failed to enter text {} into input {}", value, simpleName);
		}
	}

	public static void changeValueNoncompoundAttribute(By context, String simpleName, String value)
	{
		SelenideElement input = $(context).find(findAttributeInputBy(simpleName, false, true));
		// type the text
		typeTextIntoInput(value, input);
		// check value
		input.shouldBe(value(value));
		LOG.info("Typed value {} into input {}...", value, simpleName);
	}

	public static void sendKeysNoncompoundAttributeUnsafe(By context, String simpleName, String value)
	{
		$(context).findElement(findAttributeInputBy(simpleName, false, true)).sendKeys(value);
	}

	public static void changeValueNoncompoundAttributeRadio(By context, String simpleName, String value)
	{
		String xpathContainer = createXPathAttributeContainerWebElement(simpleName, false, false);
		LOG.info("Click on a radio element of attribute {} with value: '{}'...", simpleName, value);
		$(By.xpath(xpathContainer + "//input[@name='" + simpleName + "'][@type='radio'][@value='" + value + "']"))
				.click();
		getValueNoncompoundAttributeRadio(context, simpleName).waitUntil(value(value),
				AbstractModel.IMPLICIT_WAIT_SECONDS * 1000);
	}

	public static void typeValueNoncompoundAttributeAceEditor(By context, String simpleName, String value)
	{
		By textareaBy = By.xpath(".//textarea[@class='ace_text-input']");
		SelenideElement attributeContainer = findAttributeContainerWebElement(context, simpleName, false);
		SelenideElement textarea = attributeContainer.find(textareaBy);
		textarea.sendKeys(value);
	}

	public static void changeValueNoncompoundAttributeTextarea(By context, String simpleName, String value)
	{
		By textareaBy = By.cssSelector("textarea");
		SelenideElement attributeContainer = findAttributeContainerWebElement(context, simpleName, false);
		SelenideElement textarea = attributeContainer.find(textareaBy);
		textarea.clear();
		textarea.sendKeys(value);
	}

	public static String getValueNoncompoundAttribute(By context, String simpleName)
	{
		return $(findAttributeInputBy(simpleName, false, false)).getAttribute("value");
	}

	public static SelenideElement getValueNoncompoundAttributeRadio(By context, String simpleName)
	{
		SelenideElement attributeContainer = findAttributeContainerWebElement(context, simpleName, false);
		return attributeContainer.find(By.cssSelector("input[name='" + simpleName + "'][type='radio']:checked"));
	}

	public static void changeValueCompoundAttribute(By context, String simpleName, String simpleNamePartOf,
			String value)
	{
		SelenideElement attributeContainer = findAttributeContainerWebElement(context, simpleName, true);
		SelenideElement inputElement = attributeContainer.find(By.xpath(".//input[@name='" + simpleNamePartOf + "']"));
		typeTextIntoInput(value, inputElement);
	}

	private static void typeTextIntoInput(String value, SelenideElement inputElement)
	{
		LOG.info("Type {} into input...", value);
		inputElement.waitUntil(enabled, 1000);
		inputElement.setValue(value);
		Selenide.sleep(1000); //for validation to catch up
	}

	/**
	 * Focus on element
	 */
	public static void focusOnElement(By context, String simpleName)
	{
		SelenideElement attributeContainer = findAttributeContainerWebElement(context, simpleName, false);
		SelenideElement inputElement = attributeContainer.find(By.xpath(".//input[@name='" + simpleName + "']"));
		Selenide.actions().moveToElement(inputElement).perform();
	}

	/**
	 * Change the value of a checkbox attribute
	 * 
	 * @param driver
	 * @param context
	 * @param simpleName
	 * @param values
	 */
	public static void changeValueNoncompoundAttributeCheckbox(By context, String simpleName, String... values)
	{
		SelenideElement container = findAttributeContainerWebElement(context, simpleName, false);
		container.findElements(By.cssSelector("input[name='" + simpleName + "']")).forEach(e -> {
			if (e.isSelected())
			{
				e.click();
			}
		});
		Arrays.asList(values).stream().filter(e -> !"".equals(e)).forEach(e -> container
				.findElement(By.xpath(".//input[@name='" + simpleName + "'][@value='" + e + "']")).click());
	}

	public static void clickDeselectAll(By context, String simpleName)
	{
		SelenideElement container = findAttributeContainerWebElement(context, simpleName, false);
		SelenideElement link = container.find(By.xpath(".//span[contains(text(), 'Deselect all')]/.."));
		link.click();
		Selenide.sleep(1000); //give it time to complete
	}

	public static void clickSelectAll(By context, String simpleName)
	{
		SelenideElement container = findAttributeContainerWebElement(context, simpleName, false);
		SelenideElement link = container.find(By.xpath(".//span[contains(text(), 'Select all')]/.."));
		link.click();
	}

	/**
	 * Use this method to empty and add new values to the select2 attribute
	 * 
	 * @param simpleName
	 * @param idAndLabel
	 */
	public static void changeValueAttributeSelect2Multi(By context, String simpleName, Map<String, String> idAndLabel,
			boolean clearOriginalValues)
	{
		SelenideElement container = findAttributeContainerWebElement(context, simpleName, false);
		Select2Model s2model = new Select2Model(container.find(By.cssSelector(".select2-container")).getAttribute("id"),
				true);

		if (clearOriginalValues)
		{
			s2model.clearSelection();
		}
		s2model.selectReactForms(idAndLabel);
	}

	/**
	 * Use this method to change selection of non multi select2 attribute
	 * 
	 * @param simpleName
	 * @param idAndLabel
	 */
	public static void changeValueAttributeSelect2NonMulti(By context, String simpleName,
			Map<String, String> idAndLabel)
	{
		SelenideElement container = findAttributeContainerWebElement(context, simpleName, false);
		Select2Model s2model = new Select2Model(container.find(By.cssSelector(".select2-container")).getAttribute("id"),
				false);
		s2model.selectReactForms(idAndLabel);
	}

	public static Map<String, SelenideElement> findAttributesContainerWebElement(By context, List<String> simpleNames,
			boolean isCompoundAttribute)
	{
		Map<String, SelenideElement> result = new HashMap<String, SelenideElement>();
		simpleNames.stream().forEachOrdered(simpleName -> result.put(simpleName,
				FormsUtils.findAttributeContainerWebElement(context, simpleName, isCompoundAttribute)));
		return result;
	}

	public static SelenideElement findAttributeContainerWebElement(By context, String simpleName,
			boolean isCompoundAttribute)
	{
		return $(context)
				.find(By.xpath(createXPathAttributeContainerWebElement(simpleName, isCompoundAttribute, true)));
	}

	public static String createXPathAttributeContainerWebElement(String simpleName, boolean isCompoundAttribute,
			boolean relative)
	{
		String xpath = getXpathSlashSlashPrefix(relative)
				+ (isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER)
				+ "[substring(@data-reactid, string-length(@data-reactid) - " + simpleName.length() + ") = '$"
				+ simpleName + "']";
		LOG.info("xpath: {}", xpath);
		return xpath;
	}

	private static String getXpathSlashSlashPrefix(boolean relative)
	{
		return (relative ? "." : "") + "//";
	}

	public static By findAttributeInputBy(String simpleName, boolean isCompoundAttribute, boolean relative)
	{
		return By.xpath(
				getXpathSlashSlashPrefix(relative) + (isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER)
						+ "[substring(@data-reactid, string-length(@data-reactid) - " + simpleName.length() + ") = '$"
						+ simpleName + "']//input[@name='" + simpleName + "']");
	}

	public static void waitForErrorMessage(String simpleName, String simpleNamePartOf, String errorMessage)
	{
		waitForErrorMessageInternal(errorMessage,
				By.xpath(FormsUtils.createXPathAttributeContainerWebElement(simpleName, true, false)));
	}

	public static void waitForErrorMessage(String simpleName, String errorMessage)
	{
		waitForErrorMessageInternal(errorMessage,
				By.xpath(FormsUtils.createXPathAttributeContainerWebElement(simpleName, false, false)));
	}

	private static void waitForErrorMessageInternal(String errorMessage, By container)
	{

		$(container).waitUntil(matchesText(errorMessage), AbstractModel.IMPLICIT_WAIT_SECONDS * 1000);
	}

	public static By getAttributeContainerWebElementBy(String simpleName, boolean isCompoundAttribute)
	{
		By by = By.xpath(".//" + (isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER)
				+ "[substring(@data-reactid, string-length(@data-reactid) - " + simpleName.length() + ") = '$"
				+ simpleName + "']");
		return by;
	}

	/**
	 * an answer for the question: This form contains errors?
	 * 
	 * @param webDriver
	 *            WebDriver
	 * @param context
	 *            WebElement the context in which an element with class "has-error" can be found.
	 * @return an answer for the question: This form contains errors?
	 */
	public static boolean formHasErrors(By context)
	{
		return $(By.cssSelector(".has-error")).isDisplayed();
	}
}
