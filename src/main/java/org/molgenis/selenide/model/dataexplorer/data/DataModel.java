package org.molgenis.selenide.model.dataexplorer.data;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.util.concurrent.TimeUnit;

import org.molgenis.selenide.model.AbstractModel;
import org.molgenis.selenide.model.forms.FormsModalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

public class DataModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(DataModel.class);

//	@FindBy(css = "#data-table-container button[title='Add row']")
//	private WebElement addRowButton;
//
//	@FindBy(css = "#data-table-container button[title='Edit row']")
//	private List<WebElement> editRowButton;
//
//	@FindBy(css = "div.alerts")
//	private WebElement alertsContainer;

	public DataModel()
	{
		super();
	}

	public FormsModalModel clickOnAddRowButton()
	{
		LOG.info("click on add row button for entity TypeTest...");
		$("#data-table-container button[title='Add row']").click();
		Selenide.sleep(5000); //wait for model to load
		return new FormsModalModel().waitForModal();
	}

	public FormsModalModel clickOnEditFirstRowButton()
	{
		LOG.info("click on edit first row button for entity TypeTest...");
		$$("#data-table-container button[title='Edit row']").get(0).click();
		spinner().waitTillDone(IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
		return new FormsModalModel().waitForModal();
	}

	/**
	 * @return the alertsContainer
	 */
	public SelenideElement getAlertsContainer()
	{
		return $("div.alerts");
	}

	public DataModel waitUntilReady(int timeout)
	{
		$("#data-table-container button[title='Edit row']").waitUntil(visible,timeout*1000);
		return this;
	}

}
