package pages;

import base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage extends BasePage {

    // =========================
    // CONSTRUCTOR
    // =========================
    public LoginPage(Page page) {
        super(page);
    }

    // =========================
    // LOCATORS
    // =========================
    private final Locator loginWsmacButton = page.locator("button.btn.btn-secondary");

    // =========================
    // ACTIONS
    // =========================
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