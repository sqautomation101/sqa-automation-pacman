package stepDefinitions;

import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import managers.PlaywrightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.LandingPage;
import utils.ReportLogger;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LandingPageStep {

    private static final Logger LOG = LogManager.getLogger(LandingPageStep.class);

    private final LandingPage landingPage;

    // =========================
    // CONSTRUCTOR
    // =========================
    public LandingPageStep() {

        LOG.debug("Initializing LandingPageStep");

        Page page = PlaywrightManager.getPage();
        this.landingPage = new LandingPage(page);
    }

    // =========================
    // NAVIGATION
    // =========================
    @Given("that the user navigates on the Business Unit - Login Entry Point")
    public void thatTheUserNavigatesOnTheBusinessUnitLoginEntryPoint() {

        ReportLogger.info("Navigating to Business Unit - Login Entry Point");

        try {
            landingPage.open();
            ReportLogger.pass("Successfully opened Business Unit - Login Entry Point");

        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Failed to open Business Unit - Login Entry Point. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    // =========================
    // VERIFICATION
    // =========================
    @Then("the {string} page is displayed")
    public void thePageIsDisplayed(String pageName) {

        ReportLogger.info("Verifying that the " + pageName + " page is displayed");

        try {
            assertThat(landingPage.getLandingPageLogo()).isVisible();
            ReportLogger.pass("Successfully verified that the " + pageName + " page is displayed");

        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Failed to verify " + pageName + " page. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }
}