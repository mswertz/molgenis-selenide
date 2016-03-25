package org.molgenis.selenide.model;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.selenide.model.component.SpinnerModel;
import org.openqa.selenium.By;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

public abstract class AbstractModel
{
	protected final MenuModel menuModel;
	protected final SpinnerModel spinnerModel;
	public static final int IMPLICIT_WAIT_SECONDS = 30;

	public AbstractModel()
	{
		this.menuModel = new MenuModel();
		this.spinnerModel = new SpinnerModel();
	}

	public SpinnerModel spinner()
	{
		return spinnerModel;
	}

	public MenuModel menu()
	{
		return menuModel;
	}

	protected List<List<String>> getTableData(ElementsCollection table)
	{
		List<List<String>> result = new ArrayList<List<String>>();
		for (SelenideElement row : table)
		{
			List<String> rowResult = new ArrayList<String>();
			for (SelenideElement cell : row.findAll(By.cssSelector("td")))
			{
				rowResult.add(cell.getText());
			}
			result.add(rowResult);
		}
		return result;

	}

	/**
	 * Tests the absence of an element right now, without waiting if it perhaps will appear within the implicit timeout.
	 * Resets the web driver's timeout to {@link #IMPLICIT_WAIT_SECONDS} when done.
	 * 
	 * @param webDriver
	 *            WebDriver
	 * @param context
	 *            WebElement: can be null than the webDriver is used to search.
	 * @param by
	 *            By: by is used to fined the WebElement and define if exist
	 * @return
	 */
	public static boolean noElementFound(By context, By by)
	{
		return (null == context ? !$(by).exists() : !$(context).find(by).exists());
	}
}
