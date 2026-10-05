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
        String device = resolveDevice();

        if ("mobile".equals(device) && "firefox".equals(browserName)) {
            throw new IllegalArgumentException(
                    "Mobile emulation isn't supported on Firefox. Use chromium or webkit.");
        }

        LOG.info("Launching {} (headless={}, device={})", browserName, headless, device);

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

        BrowserContext context = browser.newContext(contextOptions(device));

        PLAYWRIGHT.set(playwright);
        BROWSER.set(browser);
        CONTEXT.set(context);
        PAGE.set(context.newPage());
    }

    /** -Ddevice=mobile on the command line wins, then the properties file, then desktop. */
    public static String resolveDevice() {
        String device = System.getProperty("device");
        if (device == null || device.isBlank()) {
            try {
                device = ConfigReader.get("device");
            } catch (RuntimeException e) {
                device = null;
            }
        }
        return (device == null || device.isBlank()) ? "desktop" : device.toLowerCase();
    }

    private static Browser.NewContextOptions contextOptions(String device) {
        Browser.NewContextOptions options = new Browser.NewContextOptions();

        if ("mobile".equals(device)) {
            options.setViewportSize(390, 844)
                    .setDeviceScaleFactor(3)
                    .setIsMobile(true)
                    .setHasTouch(true)
                    .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) "
                            + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1");
        }
        return options;
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

/**package managers;

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
}**/