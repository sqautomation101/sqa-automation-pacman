package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import managers.PlaywrightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConfigReader;
import utils.ReportLogger;

import java.util.regex.Pattern;

public class NavigationSteps {

    private static final Logger LOG = LogManager.getLogger(NavigationSteps.class);   // ← was missing

    @Given("the user is on the {string} page")
    public void theUserIsOnThePage(String pageName) {

        ReportLogger.info("Navigating to the " + pageName + " page");

        try {
            switch (pageName) {
                case "Login with your SMAC&SHOP Account" -> goToLoginPage();

                default -> throw new IllegalArgumentException(
                        "No navigation defined for page: '" + pageName + "'");
            }
            ReportLogger.pass("Successfully opened the " + pageName + " page");

        } catch (Exception | AssertionError e) {
            LOG.error("Failed to navigate to the {} page", pageName, e);
            ReportLogger.fail("Failed to open the " + pageName + " page. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    // =========================
    // HOW TO REACH EACH PAGE
    // =========================

    /** BU entry point → click "Login with SMAC" → wait for the login URL. */
    private void goToLoginPage() {
        PlaywrightManager.getPage().navigate(ConfigReader.get("baseUrl"));
        Hooks.getPageManager().getLandingPage().clickElement("Login with SMAC");
        PlaywrightManager.getPage().waitForURL(Pattern.compile(".*/auth/login.*"));
    }
}