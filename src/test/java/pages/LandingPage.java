package pages;

import base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.ConfigReader;

import java.util.Map;

public class LandingPage extends BasePage {

    // =========================
    // ELEMENT MAP (feature-file name -> selector)
    // =========================
    private static final Map<String, String> ELEMENTS = Map.ofEntries(
            Map.entry("Login with SMAC",                          "//button[@class='btn btn-secondary']"),
            Map.entry("Create account with SMAC",                 "//button[@class='btn btn-primary']"), //Change it to Register with SMAC after debugging
            Map.entry("New to SMAC? Sign-up!",                    "//span[@class='v-btn__content' and normalize-space(.)='New to SMAC? Sign-up!']"),
            Map.entry("Create a SMAC account in just 3 minutes!", "[data-testid='register-start-button']"),
            Map.entry("Have one account across SMAC and partner stores!",
                    "p:has-text('Have one account across SMAC and partner stores!'):visible"),
            Map.entry("Login with your SMAC&SHOP Account",
                    "text=Login with your SMAC&SHOP Account")
    );

    // =========================
    // CONSTRUCTOR
    // =========================
    public LandingPage(Page page) {
        super(page);
    }

    // =========================`
    // LOOKUPS
    // =========================
    public Locator locatorFor(String elementName) {
        String selector = ELEMENTS.get(elementName);
        if (selector == null) {
            throw new IllegalArgumentException("No locator defined for: '" + elementName
                    + "'. Available elements: " + ELEMENTS.keySet());
        }
        return page.locator(selector);
    }

    // =========================
    // ACTIONS
    // =========================
    public void openBU() {
        page.navigate(ConfigReader.get("baseUrl"));
    }

    public void clickElement(String elementName) {
        locatorFor(elementName).click();
    }

    // =========================
    // HELPERS
    // =========================
    /** Escapes regex special characters in a way both Java and JavaScript understand. */
    public static String escapeForRegex(String text) {
        return text.replaceAll("[\\\\^$.*+?()\\[\\]{}|]", "\\\\$0");
    }

    // =========================
    // GETTERS
    // =========================
    public Page getPage() {
        return page;
    }
}