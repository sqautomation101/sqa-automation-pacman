package managers;

import com.microsoft.playwright.Page;
import pages.LandingPage;

public class PageManager {

    private final Page page;

    private LandingPage LandingPage;

    public PageManager(Page page) {
        this.page = page;
    }

    public LandingPage getLoginPage() {

        if (LandingPage == null) {
            LandingPage = new LandingPage(page);
        }

        return LandingPage;
    }
}