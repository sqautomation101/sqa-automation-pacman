package managers;

import com.microsoft.playwright.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConfigReader;

public final class PlaywrightManager {

    private static final Logger LOG = LogManager.getLogger(PlaywrightManager.class);

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightManager() {}

    public static void init() {
        String browserName = ConfigReader.get("browser").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");
        LOG.info("Launching {} (headless={})", browserName, headless);

        Playwright playwright = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless);

        Browser browser;
        try {
            browser = switch (browserName) {
                case "chromium" -> playwright.chromium().launch(options);
                case "firefox"  -> playwright.firefox().launch(options);
                case "webkit"   -> playwright.webkit().launch(options);
                default -> throw new IllegalArgumentException(
                        "Unsupported browser '" + browserName + "'. Use chromium, firefox or webkit.");
            };
        } catch (RuntimeException e) {
            playwright.close(); // don't leak the Playwright process if launch fails
            throw e;
        }

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
        try {
            if (CONTEXT.get() != null) CONTEXT.get().close();
            if (BROWSER.get() != null) BROWSER.get().close();
        } finally {
            if (PLAYWRIGHT.get() != null) PLAYWRIGHT.get().close();
            PAGE.remove();
            CONTEXT.remove();
            BROWSER.remove();
            PLAYWRIGHT.remove();
        }
    }
}