package base;

import com.microsoft.playwright.Page;

public class BasePage {

    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public void click(String locator) {
        page.locator(locator).click();
    }

    public void type(String locator, String text) {
        page.locator(locator).fill(text);
    }

    public String getText(String locator) {
        return page.locator(locator).textContent();
    }

    public boolean isVisible(String locator) {
        return page.locator(locator).isVisible();
    }

    public void scrollTo(String locator) {
        page.locator(locator).scrollIntoViewIfNeeded();
    }
}