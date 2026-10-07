package stepDefinitions;

import hooks.Hooks;
import pages.LoginPage;

public class LoginPageStep {

    // =========================
    // HELPERS
    // =========================
    private LoginPage loginPage() {
        return Hooks.getPageManager().getLoginPage();
    }

    // =========================
    // ACTIONS
    // =========================

}