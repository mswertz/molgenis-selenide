package org.molgenis.selenide.model.forms;

import java.util.concurrent.TimeUnit;

import org.molgenis.selenide.model.AbstractModel;
import org.molgenis.selenide.model.dataexplorer.data.DataModel;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Condition.*;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.google.common.base.Predicate;

public class FormsModalModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(FormsModalModel.class);

	// @FindBy(xpath = "//button[@title=\"Hide optional fields\"]")
	// private WebElement eyeButton;
	//
	// @FindBy(css = "div.modal-body")
	// private WebElement modal;
	//
	// @FindBy(xpath = "//button[@name=\"save-changes\"]")
	// private WebElement saveChangesButton;
	//
	// @FindBy(xpath = "//button[@name=\"create\"]")
	// private WebElement createButton;
	//
	// @FindBy(xpath = "//button[@name=\"cancel\"]")
	// private WebElement cancelButton;

	// Modal body
	private By modalBy = By.cssSelector("div.modal.in");

	public FormsModalModel()
	{
		super();
	}

	public FormsModalModel clickEyeButton()
	{
		LOG.info("click on the modal eye button...");
		$(By.xpath("//button[@title=\"Hide optional fields\"]")).click();
		spinner().waitTillDone(AbstractModel.IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
		LOG.info("clicked on the modal eye button");
		return new FormsModalModel();
	}

	public DataModel clickOnSaveChangesButton()
	{
		LOG.info("click on save changes button...");
		if ($(By.cssSelector(".has-error")).isDisplayed())
		{
			$(By.cssSelector(".has-error")).waitUntil(hidden, 1000);
		}
		$(By.xpath("//button[@name=\"save-changes\"]")).click();
		waitUntilModalFormClosed();
		return new DataModel();
	}

	public DataModel clickOnCreateButton()
	{
		LOG.info("click on create button...");
		Selenide.sleep(5000); // waiting for validation to complete
		$(By.name("create")).waitUntil(visible, 1000);
		$(By.name("create")).click();
		return new DataModel();
	}

	public DataModel clickOnCancelButton()
	{
		LOG.info("click on cancel button...");
		$(By.name("cancel")).click();
		waitUntilModalFormClosed();
		return new DataModel();
	}

	private void waitUntilModalFormClosed()
	{
		LOG.info("Waiting for Model to be closed ...");
		if ($(getModalBy()).exists())
		{
			$(getModalBy()).waitUntil(hidden, 10000);
		}
	}

	/**
	 * @return the modal
	 */
	public SelenideElement getModal()
	{
		return $("div.modal-body");
	}

	/**
	 * @return the modalBy
	 */
	public By getModalBy()
	{
		return modalBy;
	}

	public FormsModalModel waitForModal()
	{
		LOG.info("Wait for modal...");
		Selenide.sleep(1000); // loading of elements
		return this;
	}

	public By getValidationBy()
	{
		return By.cssSelector(".has-error");
	}
}
