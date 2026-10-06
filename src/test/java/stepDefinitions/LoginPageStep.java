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

<<<<<<< Updated upstream
}
=======
    }
>>>>>>> Stashed changes
