package stepDefinitions;

import io.cucumber.java.en.Given;
import managers.PlaywrightManager;
import utils.ConfigReader;
import utils.ReportLogger;

public class LandingPageStep {

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
}