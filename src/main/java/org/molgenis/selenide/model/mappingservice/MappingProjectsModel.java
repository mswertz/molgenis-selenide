package org.molgenis.selenide.model.mappingservice;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.molgenis.selenide.model.AbstractModel;
import org.molgenis.selenide.model.component.Select2Model;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Condition.*;

public class MappingProjectsModel extends AbstractModel
{
	private static final By DELETE_BUTTON_SELECTOR = By.xpath("../..//button[contains(@class,'btn-danger')]");
	private static final By CLONE_BUTTON_SELECTOR = By.xpath("../..//button[contains(@class,'clone-btn')]");

//	@FindBy(name = "mapping-project-name")
//	private WebElement mappingProjectTextFieldName;
//
//	@FindBy(id = "create-new-mapping-project-modal")
//	private WebElement createNewMappingProjectModal;
//
//	@FindBy(id = "add-mapping-project-btn")
//	private WebElement addMappingProjectButton;
//
//	@FindBy(id = "submit-new-mapping-project-btn")
//	private WebElement submitNewMappingProjectButton;
//
//	@FindBy(css = "#mapping-projects-tbl tbody tr")
//	private List<WebElement> mappingProjectTableRows;
//
//	@FindBy(xpath = "//div[@class='modal-footer']/button[text()='OK']")
//	private WebElement okButton;

	private Select2Model targetEntitySelect;

	public MappingProjectsModel()
	{
		super();
		targetEntitySelect = new Select2Model("target-entity-select", false);
	}

	public MappingProjectDetailsModel addNewMappingProject(String name, String targetEntity)
	{
		$("#add-mapping-project-btn").click();
		$(By.name("mapping-project-name")).sendKeys(name);
		targetEntitySelect.select(targetEntity);
		$("#submit-new-mapping-project-btn").click();
		return new MappingProjectDetailsModel();
	}

	public List<List<String>> getMappingProjectsTable()
	{
		return getTableData($$("#mapping-projects-tbl tbody tr"));
	}

	public MappingProjectsModel copyMappingProject(String projectName)
	{
		SelenideElement toMappingProjectDetailsLink = $(By.linkText(projectName));
		SelenideElement cloneButton = toMappingProjectDetailsLink.find(CLONE_BUTTON_SELECTOR);
		cloneButton.click();
		return this;
	}

	public MappingProjectsModel deleteMappingProject(String projectName)
	{
		WebElement toMappingProjectDetailsLink = $(By.linkText(projectName));
		WebElement deleteButton = toMappingProjectDetailsLink.findElement(DELETE_BUTTON_SELECTOR);
		deleteButton.click();
		$(By.xpath("//div[@class='modal-footer']/button[text()='OK']")).click();
		spinner().waitTillDone(30, TimeUnit.SECONDS);
		return this;
	}

}
