package managers;

import com.microsoft.playwright.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PlaywrightManager {

    private static final Logger LOG = LogManager.getLogger(PlaywrightManager.class);

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightManager() {}

    public static void init() {
        LOG.info("Launching Chromium");
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
        BrowserContext context = browser.newContext();

        PLAYWRIGHT.set(playwright);
        BROWSER.set(browser);
        CONTEXT.set(context);
        PAGE.set(context.newPage());
    }

    public static Page getPage() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException(
                    "Page not initialized. Did PlaywrightManager.init() run in @Before?");
        }
        return page;
    }

    public static void quit() {
        LOG.info("Closing browser");
        if (CONTEXT.get() != null) CONTEXT.get().close();
        if (BROWSER.get() != null) BROWSER.get().close();
        if (PLAYWRIGHT.get() != null) PLAYWRIGHT.get().close();
        PAGE.remove();
        CONTEXT.remove();
        BROWSER.remove();
        PLAYWRIGHT.remove();
    }
}