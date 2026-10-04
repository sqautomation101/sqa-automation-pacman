package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.LoginPage;
import utils.ReportLogger;

public class LoginPageStep {

    private static final Logger LOG = LogManager.getLogger(LoginPageStep.class);

    // =========================
    // HELPERS
    // =========================
    private LoginPage loginPage() {
        return Hooks.getPageManager().getLoginPage();
    }

    // =========================
    // ACTIONS
    // =========================
    @When("the user clicks the {string} button")
    public void theUserClicksTheButton(String buttonName) {

        ReportLogger.info("Clicking the " + buttonName + " button");

        try {
            switch (buttonName) {
                case "Login with SMAC" -> loginPage().clickLoginWsmac();
                default -> throw new IllegalArgumentException(
                        "Unsupported button name: '" + buttonName + "'");
            }
            ReportLogger.pass("Successfully clicked the " + buttonName + " button");

        } catch (Exception | AssertionError e) {
            LOG.error("Failed to click the {} button", buttonName, e);
            ReportLogger.fail("Failed to click the " + buttonName + " button. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }
}