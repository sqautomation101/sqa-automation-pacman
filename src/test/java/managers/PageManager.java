package managers;

import base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import pages.LandingPage;
import pages.LoginPage;

import java.util.List;
import java.util.Objects;

public final class PageManager {

    private Page currentPage;   // page the page objects below were built on

    private LandingPage landingPage;
    private LoginPage loginPage;

    /** If the device changed, forget the old page objects so fresh ones are built. */
    private void refreshIfDeviceChanged() {
        Page latest = PlaywrightManager.getPage();
        if (latest != currentPage) {
            currentPage = latest;
            landingPage = null;
            loginPage = null;
        }
    }

    public LandingPage getLandingPage() {
        refreshIfDeviceChanged();
        if (landingPage == null) {
            landingPage = new LandingPage(currentPage);
        }
        return landingPage;
    }

    public LoginPage getLoginPage() {
        refreshIfDeviceChanged();
        if (loginPage == null) {
            loginPage = new LoginPage(currentPage);
        }
        return loginPage;
    }

    /** Finds which page has this element and returns its locator. */
    public Locator locatorFor(String elementName) {
        for (BasePage page : List.of(getLandingPage(), getLoginPage())) {
            if (page.hasElement(elementName)) {
                return page.locatorFor(elementName);
            }
        }
        throw new IllegalArgumentException("No page has a locator for: '" + elementName + "'");
    }
}