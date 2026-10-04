package pages;

import base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.ConfigReader;

public class LandingPage extends BasePage {

    // =========================
    // LOCATORS
    // =========================
    private final Locator loginWsmacButton = page.locator("button.btn.btn-secondary");

    // =========================
    // CONSTRUCTOR
    // =========================
    public LandingPage(Page page) {
        super(page);
    }

    // =========================
    // ACTIONS
    // =========================
    public void openBU() {
        page.navigate(ConfigReader.get("baseUrl"));
    }

    public void clickLoginWsmac() {
        loginWsmacButton.click();
    }

    // =========================
    // VERIFICATIONS
    // =========================
    public boolean isLoginWsmacButtonVisible() {
        return loginWsmacButton.isVisible();
    }

    // =========================
    // GETTERS
    // =========================
    public Locator getLoginWsmacButton() {
        return loginWsmacButton;
    }
}