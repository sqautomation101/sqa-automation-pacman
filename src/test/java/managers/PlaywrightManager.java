package managers;

import com.microsoft.playwright.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConfigReader;

public final class PlaywrightManager {
    private static final Logger LOG = LogManager.getLogger(PlaywrightManager.class);

    // ThreadLocal = each worker (thread) has its own copy, so parallel runs don't mix up browsers.
    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();      // the engine
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();            // the browser itself
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();     // the device
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();                  // the screen
    private static final ThreadLocal<DeviceProfile> DEVICE = new ThreadLocal<>();       // which device

    private PlaywrightManager() {}

    /** Launches the browser only. No device or page yet. */
    public static void init() {
        String browserName = ConfigReader.get("browser").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");
        LOG.info("Launching {} (headless={})", browserName, headless);

        Playwright playwright = Playwright.create(); // starts the Playwright engine that controls the browsers
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
            playwright.close(); // don't leave the engine running if the browser didn't launch
            throw e;
        }

        PLAYWRIGHT.set(playwright);
        BROWSER.set(browser);
    }

    /** Grabs a fresh device (desktop or mobile) and opens a page on it. */
    public static void startSession(DeviceProfile device) {
        if (BROWSER.get() == null) {
            init(); // open the browser first if it isn't open yet
        }

        // From teammate's version: Firefox can't emulate mobile devices
        if (device == DeviceProfile.MOBILE_IN_APP
                && "firefox".equals(ConfigReader.get("browser").toLowerCase())) {
            throw new IllegalArgumentException(
                    "Mobile emulation isn't supported on Firefox. Use chromium or webkit.");
        }

        closeSession(); // put down the previous device, if any

        LOG.info("Starting {} session", device);
        BrowserContext context = BROWSER.get().newContext(device.contextOptions());
        CONTEXT.set(context);
        PAGE.set(context.newPage());
        DEVICE.set(device);
    }

    /** Returns the current page. If no device was chosen yet, uses the default device. */
    public static Page getPage() {
        if (PAGE.get() == null) {
            startSession(DeviceProfile.fromConfig());
        }
        return PAGE.get();
    }

    /** Returns the current device, or null if no session has started. */
    public static DeviceProfile getDevice() {
        return DEVICE.get();
    }

    /** Closes the current device and page. */
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
        LOG.info("Closing browser");
        try {
            closeSession();
            if (BROWSER.get() != null) BROWSER.get().close();
        } finally {
            if (PLAYWRIGHT.get() != null) PLAYWRIGHT.get().close();
            BROWSER.remove();
            PLAYWRIGHT.remove();
        }
    }
}