package pages;

import base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.ConfigReader;

public class LandingPage extends BasePage {

    // =========================
    // CONSTRUCTOR
    // =========================
    public LandingPage(Page page) {
        super(page);
    }

    // =========================
    // LOCATORS
    // =========================
    private final Locator landingPageLogo =
            page.locator("#__nuxt header a img");

    // =========================
    // ACTIONS
    // =========================
    public void open() {
        page.navigate(ConfigReader.get("baseUrl"));
    }

    // =========================
    // GETTERS
    // =========================
    public Locator getLandingPageLogo() {
        return landingPageLogo;
    }
}