package pages;

import base.BasePage;
import com.microsoft.playwright.Page;

import java.util.Map;

public class LoginPage extends BasePage {

    // =========================
    // ELEMENT MAP (feature-file name -> selector)
    // =========================
    private static final Map<String, String> ELEMENTS = Map.ofEntries(
            Map.entry("Back to Store",    "#back-button__desktop"),
            Map.entry("Home",             "#back-button__mobile"),
            Map.entry("Login header",     "#login__identifier-header h1"),
            Map.entry("Login input",      "[data-testid='login-username']"),
            Map.entry("Proceed",          "[data-testid='login-proceed-button']"),
            Map.entry("Or continue with", "#login__social-login > p"),
            Map.entry("Google",           "[data-testid='login-google-button']"),
            Map.entry("Apple",            "[data-testid='login-apple-button']"),
            Map.entry("Sign-up",          "[data-testid='login-register-button']"),
            Map.entry("Promo panel",      "#auth-landing-layout__promotional-container")
    );

    // =========================
    // CONSTRUCTOR
    // =========================
    public LoginPage(Page page) {
        super(page);
    }

    // =========================
    // ELEMENTS
    // =========================
    @Override
    protected Map<String, String> elements() {
        return ELEMENTS;
    }
}