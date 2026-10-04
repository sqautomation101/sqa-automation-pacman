package managers;

import com.microsoft.playwright.Page;
import pages.LandingPage;
import pages.LoginPage;

import java.util.Objects;

public final class PageManager {

    private final Page page;

    private LandingPage landingPage;
    private LoginPage loginPage;

    public PageManager(Page page) {
        this.page = Objects.requireNonNull(page, "Page must not be null");
    }

    public LandingPage getLandingPage() {
        if (landingPage == null) {
            landingPage = new LandingPage(page);
        }
        return landingPage;
    }

    public LoginPage getLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(page);
        }
        return loginPage;
    }
}