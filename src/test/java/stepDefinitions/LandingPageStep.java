package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import hooks.Hooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.LandingPage;
import utils.ReportLogger;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LandingPageStep {

    private static final Logger LOG = LogManager.getLogger(LandingPageStep.class);

    // =========================
    // HELPERS
    // =========================
    private LandingPage landingPage() {
        return Hooks.getPageManager().getLandingPage();
    }

    // =========================
    // NAVIGATION
    // =========================
    @Given("that the user navigates on the Business Unit - Login Entry Point")
    public void thatTheUserNavigatesOnTheBusinessUnitLoginEntryPoint() {

        ReportLogger.info("Navigating to Business Unit - Login Entry Point");

        try {
            landingPage().openBU();
            ReportLogger.pass("Successfully opened Business Unit - Login Entry Point");

        } catch (Exception | AssertionError e) {
            LOG.error("Navigation to Business Unit - Login Entry Point failed", e);
            ReportLogger.fail("Failed to open Business Unit - Login Entry Point. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    // =========================
    // VERIFICATION
    // =========================
    @Then("the {string} button is displayed")
    public void theButtonIsDisplayed(String buttonName) {

        ReportLogger.info("Verifying that the " + buttonName + " button is displayed");

        try {
            assertThat(landingPage().getLoginWsmacButton()).isVisible();
            ReportLogger.pass("Successfully verified that the " + buttonName + " button is displayed");

        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Failed to verify the " + buttonName + " button. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }
}