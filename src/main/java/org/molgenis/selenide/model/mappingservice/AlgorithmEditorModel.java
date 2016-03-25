package org.molgenis.selenide.model.mappingservice;

import static com.codeborne.selenide.Selenide.$;

import org.molgenis.selenide.model.AbstractModel;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgorithmEditorModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmEditorModel.class);

//	@FindBy(linkText = "Cancel and go back")
//	WebElement cancelAndGoBackButton;
//
//	@FindBy(id = "ace-editor-text-area")
//	WebElement aceEditorTextArea;

	public AlgorithmEditorModel()
	{
		super();
	}

	public String getAlgorithmValue()
	{
		LOG.info("getAlgorithmValue()...");
		String result = $("#ace-editor-text-area").getAttribute("textContent");
		LOG.debug("algorithm={}", result);
		return result;
	}

	public MappingProjectDetailsModel cancelAndGoBack()
	{
		$(By.linkText("Cancel and go back")).click();
		return new MappingProjectDetailsModel();
	}
}
