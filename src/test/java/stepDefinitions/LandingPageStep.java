package stepDefinitions;

import com.microsoft.playwright.Locator;
import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import managers.PlaywrightManager;
import pages.LandingPage;
import utils.ConfigReader;
import utils.ReportLogger;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static pages.LandingPage.escapeForRegex;

public class LandingPageStep {

    // =========================
    // HELPERS
    // =========================
    private LandingPage landingPage() {
        return Hooks.getPageManager().getLandingPage();
    }

    /** Finds the element on whichever page has it. */
    private Locator locatorFor(String elementName) {
        return Hooks.getPageManager().locatorFor(elementName);
    }

    private void verifyElementDisplayed(String elementName) {

        ReportLogger.info("Verifying that the " + elementName + " is displayed");

        try {
            assertThat(locatorFor(elementName)).isVisible();
            ReportLogger.pass("Successfully verified that the " + elementName + " is displayed");

        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Failed to verify the " + elementName + ". Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    private void verifyUrlContains(String expectedPart) {

        ReportLogger.info("Verifying that the page URL contains '" + expectedPart + "'");

        try {
            assertThat(landingPage().getPage())
                    .hasURL(Pattern.compile(".*" + escapeForRegex(expectedPart) + ".*"));
            ReportLogger.pass("Page URL contains '" + expectedPart + "'");

        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Page URL does not contain '" + expectedPart + "'. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    // =========================
    // NAVIGATION
    // =========================
    @Given("the user navigates on the Business Unit - Login Entry Point")
    public void theUserNavigatesOnTheBusinessUnitLoginEntryPoint() {

        ReportLogger.info("Navigating to Business Unit - Login Entry Point");

        try {
            PlaywrightManager.getPage().navigate(ConfigReader.get("baseUrl"));
            ReportLogger.pass("Successfully opened Business Unit - Login Entry Point");

        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Failed to open Business Unit - Login Entry Point. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    // =========================
    // ACTIONS
    // =========================
    @When("the user clicks the {string} button")
    public void theUserClicksTheButton(String buttonName) {

        ReportLogger.info("Clicking the " + buttonName + " button");

        try {
            locatorFor(buttonName).click();
            ReportLogger.pass("Successfully clicked the " + buttonName + " button");

        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Failed to click the " + buttonName + " button. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    // =========================
    // VERIFICATION - ELEMENTS
    // =========================
    @Then("the {string} button is displayed")
    public void theButtonIsDisplayed(String buttonName) {
        verifyElementDisplayed(buttonName);
    }

    @Then("the {string} page should be displayed")
    public void thePageShouldBeDisplayed(String pageName) {
        verifyElementDisplayed(pageName);
    }

    // =========================
    // VERIFICATION - URL
    // =========================
    @Then("the page URL should contain {string}")
    public void thePageUrlShouldContain(String expectedPart) {
        verifyUrlContains(expectedPart);
    }

    @Then("the channel id should contain {string}")
    public void theChannelIdShouldContain(String expectedPart) {
        verifyUrlContains(expectedPart);
    }

    // =========================
    // VERIFICATION - TITLE
    // =========================
    @Then("the browser tab title should be {string}")
    public void theBrowserTabTitleShouldBe(String expectedTitle) {

        ReportLogger.info("Verifying that the browser tab title is '" + expectedTitle + "'");

        try {
            assertThat(landingPage().getPage()).hasTitle(expectedTitle);
            ReportLogger.pass("Browser tab title is '" + expectedTitle + "'");

        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Browser tab title is not '" + expectedTitle + "'. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }
}