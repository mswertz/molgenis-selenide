package org.molgenis.selenide.model.component;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Selenide.$;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.SelenideElement;
import com.google.common.base.Stopwatch;

public class SpinnerModel
{
	private static final int SPINNER_APPEAR_TIMEOUT = 1;

	private static final Logger LOG = LoggerFactory.getLogger(SpinnerModel.class);

	public SelenideElement getSpinner()
	{
		return $("#spinner");
	}

	public SpinnerModel()
	{
	}

	/**
	 * Waits for a certain amount of seconds for a period of one full second without spinner.
	 */
	public SpinnerModel waitTillDone(long timeout, TimeUnit unit)
	{

		LOG.info("Wait for spinners to stop showing...");
		Stopwatch sw = Stopwatch.createStarted();
		internalWaitTillDone(unit.toSeconds(timeout), sw);
		return this;
	}

	/**
	 * Waits for the spinner to have disappeared for at least one second within a certain timespan
	 * 
	 * @param timeout
	 *            the timespan to wait for
	 * @param sw
	 *            {@link Stopwatch} that keeps track of time since we started waiting
	 */
	private void internalWaitTillDone(long timeout, Stopwatch sw)
	{
		while (waitForSpinnerToAppear(SPINNER_APPEAR_TIMEOUT))
		{
			waitForSpinnerToHide(checkTimeLeft(timeout, sw));
		}
	}

	/**
	 * Checks now much time is left until timeout.
	 * 
	 * @param timeout
	 *            timeout in seconds
	 * @param sw
	 *            {@link Stopwatch} that keeps track of time since we started waiting
	 * @return seconds left to wait
	 */
	private long checkTimeLeft(long timeout, Stopwatch sw)
	{
		long timeOutInSeconds = timeout - sw.elapsed(TimeUnit.SECONDS);
		if (timeOutInSeconds <= 0)
		{
			throw new TimeoutException("Spinner did not stop showing for " + timeout + " seconds.");
		}
		return timeOutInSeconds;
	}

	/**
	 * Waits for the spinner to hide. Throws exception if timeout is exceeded.
	 * 
	 * @param timeOutInSeconds
	 *            timeout to wait for
	 */
	private void waitForSpinnerToHide(long timeOutInSeconds)
	{
		if (getSpinner().isDisplayed())
		{
			LOG.info("Spinner showing. Wait {} more seconds for spinner to hide...", timeOutInSeconds);
			getSpinner().waitWhile(hidden, timeOutInSeconds * 1000);
		}
	}

	/**
	 * Waits for the spinner to appear.
	 * 
	 * @param secondWait
	 * @return
	 */
	private boolean waitForSpinnerToAppear(int seconds)
	{
		if (!getSpinner().isDisplayed())
		{
			LOG.info("Wait one second for the spinner to (re-)appear...");
			getSpinner().waitWhile(visible, seconds * 1000);
		}
		return getSpinner().isDisplayed();
	}

}
