package org.molgenis.selenide.model;

import static com.codeborne.selenide.Selenide.$;

/**
 * This is a model of the MOLGENIS login user interface.}
 */
public class SignInModel
{
//	@FindBy(id = "username-field")
//	WebElement usernameField;
//
//	@FindBy(id = "password-field")
//	WebElement passwordField;
//
//	@FindBy(id = "signin-button")
//	WebElement signinButton;
//
//	@FindBy(css = "p.text-error")
//	WebElement errorText;
//
//	@FindBy(css = "button.close")
//	WebElement closeButton;

	public SignInModel()
	{
	}

	public HomepageModel signIn(String user, String password)
	{
		trySignIn(user, password);
		return new HomepageModel();
	}

	public SignInModel signInFails(String user, String password)
	{
		trySignIn(user, password);
		return this;
	}

	public HomepageModel close()
	{
		$("button.close").click();
		return new HomepageModel();
	}
	
	private void trySignIn(String user, String password)
	{
		// fill in the user name
		$("#username-field").clear();
		$("#username-field").sendKeys(user);

		// fill in the password
		$("#password-field").clear();
		$("#password-field").sendKeys(password);

		// click the sign in button on login page
		$("#signin-button").click();
	}

	public boolean showsErrorText(String s)
	{
		return $("p.text-error").getText().contains(s);
	}
}
