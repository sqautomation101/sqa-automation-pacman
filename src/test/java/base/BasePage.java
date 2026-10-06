package base;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.Map;

public class BasePage {

    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    // =========================
    // ELEMENT LOOKUP (new)
    // =========================

    /** Each page overrides this with its own element map. Empty by default. */
    protected Map<String, String> elements() {
        return Map.of();
    }

    /** Verify if this page has element with this name */
    public boolean hasElement(String elementName) {
        return elements().containsKey(elementName);
    }

    /** Finds the element by its feature-file name, e.g. "Login with SMAC". */
    public Locator locatorFor(String elementName) {
        String selector = elements().get(elementName);
        if (selector == null) {
            throw new IllegalArgumentException("No locator defined for: '" + elementName
                    + "'. Available elements: " + elements().keySet());
        }
        return page.locator(selector);
    }


    /** Clicks an element by its feature-file name. */
    public void clickElement(String elementName) {
        locatorFor(elementName).click();
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