package managers;

import com.microsoft.playwright.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConfigReader;

public final class PlaywrightManager {
    private static final Logger LOG = LogManager.getLogger(PlaywrightManager.class);

    // Storage for each part. ThreadLocal = each test thread has its own copy.
    // ThreadLocal = each worker (thread) has its own copy, so parallel runs don't mix up browsers.
    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();      // the engine
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();            // the browser itself
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();     // the device
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();                  // the screen
    private static final ThreadLocal<DeviceProfile> DEVICE = new ThreadLocal<>();       // which device

    private PlaywrightManager() {}

    /** Opens the store: launches the browser only. No device or page yet. */
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

        Playwright playwright = Playwright.create(); /// starts the Playwright engine that controls the browsers
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless); // browser launch settings (e.g. headless)

        Browser browser;
        try {
            browser = switch (browserName) {
                case "chromium" -> playwright.chromium().launch(options); // which browser to use - Chrome
                case "firefox"  -> playwright.firefox().launch(options); // which browser to use - Firefox
                case "webkit"   -> playwright.webkit().launch(options); // which browser to use - Safari
                default -> throw new IllegalArgumentException(
                        "Unsupported browser '" + browserName + "'. Use chromium, firefox or webkit.");
            };
        } catch (RuntimeException e) {
            playwright.close(); // to close the Playwright engine if the browser did not launch so it doesn't stay running in the background
            throw e;
        }

        PLAYWRIGHT.set(playwright); // stores the specific Playwright engine
        BROWSER.set(browser); // storing the specific browser
    }

    /** Grabs a fresh device (desktop or mobile) and turns on its screen. */
    public static void startSession(DeviceProfile device) {
        if (BROWSER.get() == null) {
            init(); // open the browser first if it isn't open yet
        }
        closeSession(); // put down the previous device, if any

        LOG.info("Starting {} session", device);
        BrowserContext context = BROWSER.get().newContext(device.contextOptions()); // build the actual device
        CONTEXT.set(context); // stores the device built from DeviceProfile's settings
        PAGE.set(context.newPage()); // open new tab using the device
        DEVICE.set(device); // writes down the device from DeviceProfile?
    }

    /** Returns the current screen. If no device was chosen yet, uses the config default. */
    // Take the tab from PAGE.set(context.newPage());
    public static Page getPage() {
        if (PAGE.get() == null) {
            startSession(DeviceProfile.fromConfig()); // get the default device
        }
        return PAGE.get();
    }

    /** Returns the current device, or null if no session has started. */
    // Take the device from DEVICE.set(device);
    public static DeviceProfile getDevice() {
        return DEVICE.get();
    }

    /** Puts the device down. */
    public static void closeSession() {
        if (CONTEXT.get() != null) {
            CONTEXT.get().close();
        }
        CONTEXT.remove();
        PAGE.remove();
        DEVICE.remove();
    }

    /** Closes everything: device, browser, and engine. */
    public static void quit() {
        closeSession();
        if (BROWSER.get() != null) {
            BROWSER.get().close();
        }
        if (PLAYWRIGHT.get() != null) {
            PLAYWRIGHT.get().close();
        }
        BROWSER.remove();
        PLAYWRIGHT.remove();
    }
}**/