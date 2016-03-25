package org.molgenis.selenide.model.mappingservice;

import static com.codeborne.selenide.Selenide.$;

import org.molgenis.selenide.model.AbstractModel;
import org.molgenis.selenide.model.dataexplorer.DataExplorerModel;
import org.openqa.selenium.By;

/**
 * Model for the "Create integrated dataset" popup in the MappingProjectDetail view.
 */
public class CreateIntegratedDatasetModalModel extends AbstractModel
{
//	@FindBy(name = "newEntityName")
//	private WebElement newEntityName;
//
//	@FindBy(id = "create-integrated-entity-btn")
//	private WebElement createIntegratedEntityButton;

	public CreateIntegratedDatasetModalModel()
	{
		super();
	}

	public CreateIntegratedDatasetModalModel setEntityName(String name)
	{
		$(By.name("newEntityName")).clear();
		$(By.name("newEntityName")).sendKeys(name);
		return this;
	}

	public DataExplorerModel createIntegratedDataset()
	{
		$("#create-integrated-entity-btn").click();
		return new DataExplorerModel();
	}

}
