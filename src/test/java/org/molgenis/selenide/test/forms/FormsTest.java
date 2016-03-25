package org.molgenis.selenide.test.forms;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.molgenis.rest.model.SettingsModel;
import org.molgenis.selenide.model.AbstractModel;
import org.molgenis.selenide.model.dataexplorer.data.DataModel;
import org.molgenis.selenide.model.forms.FormsModalModel;
import org.molgenis.selenide.model.forms.FormsUtils;
import org.molgenis.selenide.test.AbstractSeleniumTest;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.codeborne.selenide.SelenideElement;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class FormsTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(FormsTest.class);

	/**
	 * A list of all non compound attributes. Removed from this list: "xcompound", xcompound_int", "xcompound_string"
	 */
	private static final List<String> TESTTYPE_NONCOMPOUND_ATTRIBUTES = Lists.<String> newArrayList("id", "xbool",
			"xboolnillable", "xcategorical_value", "xcategoricalnillable_value", "xcategoricalmref_value",
			"xcatmrefnillable_value", "xdate", "xdatenillable", "xdatetime", "xdatetimenillable", "xdecimal",
			"xdecimalnillable", "xemail", "xemailnillable", "xenum", "xenumnillable", "xhtml", "xhtmlnillable",
			"xhyperlink", "xhyperlinknillable", "xint", "xintnillable", "xintrange", "xintrangenillable", "xlong",
			"xlongnillable", "xlongrange", "xlongrangenillable", "xmref_value", "xmrefnillable_value", "xstring",
			"xstringnillable", "xtext", "xtextnillable", "xxref_value", "xxrefnillable_value", "xstring_hidden",
			"xstringnillable_hidden", "xstring_unique", "xint_unique", "xxref_unique", "xcomputedxref", "xcomputedint");

	/**
	 * A list of all <b>nillable</b> noncompound attributes. Removed from this list: "xcompound"
	 */
	private static final List<String> TESTTYPE_NONCOMPOUND_NILLABLE_ATTRIBUTES = Lists.<String> newArrayList("id",
			"xbool", "xcategorical_value", "xcategoricalmref_value", "xdate", "xdatetime", "xdecimal", "xemail",
			"xenum", "xhtml", "xhyperlink", "xint", "xintrange", "xlong", "xlongrange", "xmref_value", "xstring",
			"xtext", "xxref_value", "xstring_hidden", "xstring_unique", "xint_unique");

	/**
	 * A list of all nonnillable attributes
	 * 
	 * Removed from this list: "xcompound_int", "xcompound_string"
	 */
	private static final List<String> TESTTYPE_NONCOMPOUND_NONNILLABLE_ATTRIBUTES = Lists.<String> newArrayList(
			"xboolnillable", "xcategoricalnillable_value", "xcatmrefnillable_value", "xdatenillable",
			"xdatetimenillable", "xdecimalnillable", "xemailnillable", "xenumnillable", "xhtmlnillable",
			"xhyperlinknillable", "xintnillable", "xintrangenillable", "xlongnillable", "xlongrangenillable",
			"xmrefnillable_value", "xstringnillable", "xtextnillable", "xxrefnillable_value", "xstringnillable_hidden",
			"xxref_unique", "xcomputedxref", "xcomputedint");

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		super.token = super.restClient.login(uid, pwd).getToken();	
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Location", "Person");
		new SettingsModel(super.restClient, super.token).updateDataExplorerSettings("mod_data", true);
		super.restClient.logout(token);
		super.importEMXFiles("org/molgenis/selenide/emx/xlsx/emx_all_datatypes.xlsx");
	}

	/**
	 * Action: Click on 'edit' icon of first row. Result: Form should be visible.
	 */
	@Test
	public void testVisibilityFirstRow()
	{
		LOG.info("testVisibilityFirstRow...");
		final DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		final FormsModalModel model = dataModel.clickOnEditFirstRowButton();

		Map<String, SelenideElement> noncompoundAttrbutes = FormsUtils
				.findAttributesContainerWebElement(model.getModalBy(), TESTTYPE_NONCOMPOUND_ATTRIBUTES, false);
		noncompoundAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that noncompound attributes are found: {}", TESTTYPE_NONCOMPOUND_ATTRIBUTES);

		Map<String, SelenideElement> compoundAttrbutes = FormsUtils
				.findAttributesContainerWebElement(model.getModalBy(), Arrays.asList("xcompound"), true);
		compoundAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that compound attributes are found: {}", "xcompound");

		model.clickOnCancelButton();
	}

	/**
	 * Action: Click on 'eye' icon. Result: Nillable fields should be hidden.
	 */
	@Test
	public void testNillableFieldsShouldBeHidden()
	{
		LOG.info("testNillableFieldsShouldBeHidden...");
		final DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		model = model.clickEyeButton();

		Map<String, SelenideElement> noncompoundNillableAttrbutes = FormsUtils
				.findAttributesContainerWebElement(model.getModalBy(), TESTTYPE_NONCOMPOUND_NILLABLE_ATTRIBUTES, false);
		noncompoundNillableAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that noncompound and nonnillable attributes are displayed: {}",
				TESTTYPE_NONCOMPOUND_NILLABLE_ATTRIBUTES);

		assertTrue(AbstractModel.noElementFound(model.getModalBy(),
				FormsUtils.getAttributeContainerWebElementBy("xcompound", true)));
		LOG.info("Test that xcompound is not displayed");

		Map<String, SelenideElement> noncompoundNonnillableAttributes = FormsUtils.findAttributesContainerWebElement(
				model.getModalBy(), TESTTYPE_NONCOMPOUND_NONNILLABLE_ATTRIBUTES, false);
		noncompoundNonnillableAttributes.values().forEach(e -> assertFalse(e.isDisplayed()));
		LOG.info("Test that noncompound and nillable attributes are hidden: {}",
				TESTTYPE_NONCOMPOUND_NONNILLABLE_ATTRIBUTES);

		model.clickOnCancelButton();
	}

	/**
	 * Action: Click 'Save changes'. Result: Form should be saved without errors and give a 'saved' message.
	 * 
	 * This test will fail because of this issue: "Save changes message in forms is not shown #4273"
	 */
	@Test
	public void testSaveChanges()
	{
		LOG.info("testSaveChanges...");
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		final FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		dataModel = model.clickOnSaveChangesButton();
		LOG.info("Tested save changes button");
	}

	/**
	 * Create new TypeTestRef Fill in all attributes (Use new TypeTestRef) and click 'Create'
	 */
	@Test
	public void testCreateNewTypeTestRefAndTypeTest()
	{
		LOG.info("testCreateNewTypeTestRefAndTypeTest...");
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTestRef").selectDataTab();
		FormsModalModel model = dataModel.clickOnAddRowButton();
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "value", "ref6");
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "label", "label6");
		dataModel = model.clickOnCreateButton();
		LOG.info("Added a new row with values: {value:ref6, label:label6} [TypeTestRef]");

		dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		model = dataModel.clickOnAddRowButton();
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "id", "55");
		populateAllNonUniqueTestTypeAttributeValues(model.getModalBy());

		// Unique
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "xstring_unique", "Unique");
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "xint_unique", "42");
		FormsUtils.changeValueAttributeSelect2NonMulti(model.getModalBy(), "xxref_unique",
				ImmutableMap.<String, String> of("ref6", "label6"));

		// "These values are computed automatically": xcomputedint, xcomputedxref

		dataModel = model.clickOnCreateButton();
		LOG.info("Added a new row with values: {...} [TypeTest]");
	}

	/**
	 * Action: Edit some values and save changes. Result: Values should be updated
	 */
	@Test
	public void testEditValuesAndSaveChanges()
	{
		LOG.info("testEditValuesAndSaveChanges...");
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		populateAllNonUniqueTestTypeAttributeValues(model.getModalBy());
		model.clickOnSaveChangesButton();
		LOG.info("Tested editing some values and pushing the save changes button");
	}

	private static void populateAllNonUniqueTestTypeAttributeValues(By modalBy)
	{
		FormsUtils.changeValueNoncompoundAttributeRadio(modalBy, "xbool", "true");
		FormsUtils.changeValueNoncompoundAttributeRadio(modalBy, "xboolnillable", "");
		FormsUtils.changeValueCompoundAttribute(modalBy, "xcompound", "xcompound_int", "55");
		FormsUtils.changeValueCompoundAttribute(modalBy, "xcompound", "xcompound_string", "selenium test");
		FormsUtils.changeValueNoncompoundAttributeRadio(modalBy, "xcategorical_value", "ref1");
		FormsUtils.changeValueNoncompoundAttributeRadio(modalBy, "xcategoricalnillable_value", "");
		FormsUtils.changeValueNoncompoundAttributeCheckbox(modalBy, "xcategoricalmref_value", "ref1", "ref2");
		FormsUtils.clickDeselectAll(modalBy, "xcatmrefnillable_value");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xdate", "2015-12-31");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xdatenillable", "");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xdatetime", "2015-12-31T23:59:59+0100");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xdatetimenillable", "");

		// FIXMEE: #4283 Remove value xdecimal, try to add x.xx (example: 1.00) will not be possible
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xdecimal", "555");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xdecimalnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xemail", "molgenis@gmail.com");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xemailnillable", "");
		FormsUtils.changeValueNoncompoundAttributeRadio(modalBy, "xenum", "enum3");
		FormsUtils.changeValueNoncompoundAttributeRadio(modalBy, "xenumnillable", "");
		FormsUtils.typeValueNoncompoundAttributeAceEditor(modalBy, "xhtml", "<h2>hello selenium test");
		FormsUtils.typeValueNoncompoundAttributeAceEditor(modalBy, "xhtmlnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xhyperlink", "http://www.seleniumhq.org/");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xhyperlinknillable", "");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xint", "5");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xintnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xintrange", "5");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xintrangenillable", "");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xlong", "5");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xlongnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xlongrange", "5");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xlongrangenillable", "");
		FormsUtils.changeValueAttributeSelect2Multi(modalBy, "xmref_value",
				ImmutableMap.<String, String> of("ref4", "label4", "ref5", "label5"), true);
		FormsUtils.changeValueAttributeSelect2Multi(modalBy, "xmrefnillable_value",
				ImmutableMap.<String, String> of("", ""), true);
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xstring", "xstring");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xstringnillable", "");
		FormsUtils.changeValueNoncompoundAttributeTextarea(modalBy, "xtext", "xtext");
		FormsUtils.changeValueNoncompoundAttributeTextarea(modalBy, "xtextnillable", "");
		FormsUtils.changeValueAttributeSelect2NonMulti(modalBy, "xxref_value",
				ImmutableMap.<String, String> of("ref4", "label4"));
		FormsUtils.changeValueAttributeSelect2NonMulti(modalBy, "xxrefnillable_value",
				ImmutableMap.<String, String> of("ref4", "label4"));
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xstring_hidden", "hidden");
		FormsUtils.changeValueNoncompoundAttribute(modalBy, "xstringnillable_hidden", "");
	}

	/**
	 * Action: Test error messages for invalid values
	 */
	@Test
	public void testErrorMessagesInvalidValues()
	{
		LOG.info("testErrorMessagesInvalidValues");
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();

		// xcompound_int
		FormsUtils.changeValueCompoundAttribute(model.getModalBy(), "xcompound", "xcompound_int", "9999999999999");
		FormsUtils.waitForErrorMessage("xcompound", "xcompound_int",
				"Please enter a value between -2147483648 and 2147483647.");
		FormsUtils.changeValueCompoundAttribute(model.getModalBy(), "xcompound", "xcompound_int", "100");

		// FIXME Issue #4315: Forms occasionally eat up some of the letters that you've typed.
		// xdate
		// FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xdate", "");
		// FormsUtils.waitForErrorMessage(driver, "xdate", "Please enter a value.");
		// FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xdate", "2015-12-31");
		// String actualXDate = FormsUtils.getValueNoncompoundAttribute(driver, model.getModalBy(), "xdate");
		// assertEquals(actualXDate, "2015-12-31");

		// Test onblur xdecimal
		// FormsUtils.changeValueNoncompoundAttributeUnsafe(model.getModalBy(), "xdecimal", "1-1-1-1-1");
		// FormsUtils.focusOnElement(model.getModalBy(), "xdecimal");
		// String actual1 = FormsUtils.getValueNoncompoundAttribute(model.getModalBy(), "xdecimal");
		// assertEquals(actual1, "11111");

		// xemail
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "xemail", "molgenisgmail.com");
		FormsUtils.waitForErrorMessage("xemail", "Please enter a valid email address.");
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "xemail", "molgenis@gmail.com");

		// xhyperlink
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "xhyperlink", " www.molgenis.org");
		FormsUtils.waitForErrorMessage("xhyperlink", "Please enter a valid URL.");
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "xhyperlink", "http://www.molgenis.org");

		// xmref_value
		FormsUtils.changeValueAttributeSelect2Multi(model.getModalBy(), "xmref_value",
				ImmutableMap.<String, String> of("", ""), true);
		FormsUtils.waitForErrorMessage("xmref_value", "Please enter a value.");
		FormsUtils.changeValueAttributeSelect2Multi(model.getModalBy(), "xmref_value",
				ImmutableMap.<String, String> of("ref1", "label1"), true);

		// xstring_unique
		String oXstringUnique = FormsUtils.getValueNoncompoundAttribute(model.getModalBy(), "xstring_unique");
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "xstring_unique",
				(oXstringUnique.equals("str4") ? "str3" : "str4"));
		FormsUtils.waitForErrorMessage("xstring_unique", "This xstring_unique already exists. It must be unique.");
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "xstring_unique", oXstringUnique);

		// xint_unique: Change into different, but already used, value
		String oXintUnique = FormsUtils.getValueNoncompoundAttribute(model.getModalBy(), "xint_unique");
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "xint_unique",
				(oXintUnique.equals("2") ? "1" : "2"));
		FormsUtils.waitForErrorMessage("xint_unique", "This xint_unique already exists. It must be unique.");
		FormsUtils.changeValueNoncompoundAttribute(model.getModalBy(), "xint_unique", oXintUnique);

		// xxref_unique
		String xXrefUnique = FormsUtils.getValueNoncompoundAttribute(model.getModalBy(), "xxref_unique");
		if (!xXrefUnique.isEmpty())
		{
			Map<String, String> xXrefUniqueError = (xXrefUnique.equals("ref2")
					? ImmutableMap.<String, String> of("ref1", "label1")
					: ImmutableMap.<String, String> of("ref2", "label2"));
			FormsUtils.changeValueAttributeSelect2NonMulti(model.getModalBy(), "xxref_unique", xXrefUniqueError);
			FormsUtils.waitForErrorMessage("xxref_unique", "This xxref_unique already exists. It must be unique.");
			FormsUtils.changeValueAttributeSelect2NonMulti(model.getModalBy(), "xxref_unique",
					ImmutableMap.<String, String> of(xXrefUnique, "label" + xXrefUnique.replaceAll("ref", "")));
		}

		model.clickOnSaveChangesButton();
		LOG.info("Tested editing some values and pushing the save changes button");
	}

	/**
	 * Action: Click link 'Deselect all' link of xcategoricalmref_value. Result: All checkboxes should be unchecked.
	 */
	@Test
	public void testDeselectAll()
	{
		LOG.info("testDeselectAll");
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		FormsUtils.clickDeselectAll(model.getModalBy(), "xcategoricalmref_value");
		FormsUtils.waitForErrorMessage("xcategoricalmref_value", "Please enter a value.");
		model.clickOnCancelButton();
		LOG.info("Test deselect all checkboxes xcategoricalmref_value");
	}

	/**
	 * Action: Click 'select all' link of xcategoricalmref_value. Result: All checkboxes should be checked.
	 */
	@Test
	public void testSelectAll()
	{
		LOG.info("testSelectAll");
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		FormsUtils.clickSelectAll(model.getModalBy(), "xcategoricalmref_value");
		model.clickOnSaveChangesButton();
		LOG.info("Test select all checkboxes xcategoricalmref_value");
	}

	@AfterClass
	public void afterClass() throws InterruptedException
	{
		super.token = super.restClient.login(uid, pwd).getToken();
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Location", "Person");
		super.restClient.logout(token);
	}
}
