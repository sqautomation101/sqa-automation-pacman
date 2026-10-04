package hooks;

import com.aventstack.extentreports.ExtentTest;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import managers.PageManager;
import managers.PlaywrightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConfigReader;
import utils.ExtentManager;
import utils.ExtentTestManager;
import utils.ReportLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class Hooks {

    private static final Logger LOG = LogManager.getLogger(Hooks.class);

    private static final ThreadLocal<PageManager> PAGE_MANAGER = new ThreadLocal<>();

    /** Runs before every scenario. */
    @Before
    public void setUp(Scenario scenario) {
        LOG.info("========================================");
        LOG.info("Starting Scenario: {}", scenario.getName());
        LOG.info("========================================");

        ReportLogger.setScenario(scenario);

        ExtentTest test = ExtentManager.getInstance().createTest(scenario.getName());
        ExtentTestManager.setTest(test);

        PlaywrightManager.init();   // throws if the browser can't start
        PlaywrightManager.getPage().navigate(ConfigReader.get("baseUrl"));
        PAGE_MANAGER.set(new PageManager(PlaywrightManager.getPage()));

        LOG.info("Browser opened and navigated to {}", ConfigReader.get("baseUrl"));
    }

    /** Runs after every scenario. */
    @After
    public void tearDown(Scenario scenario) {
        String status = scenario.getStatus().name();   // PASSED, FAILED, SKIPPED...

        try {
            byte[] shot = captureScreenshot();
            logResult(scenario, status, shot);

            if (shot != null) {
                scenario.attach(shot, "image/png", status + " - final state");
                saveToDisk(scenario, status, shot);
            }
        } catch (Exception e) {
            LOG.error("Failed to save screenshot.", e);

        } finally {
            PlaywrightManager.quit();
            LOG.info("Browser closed.");

            PAGE_MANAGER.remove();
            ExtentTestManager.unload();
            ReportLogger.clear();
        }
    }

    /** Runs once after all scenarios. */
    @AfterAll
    public static void generateReport() {
        ExtentManager.flush();

        LOG.info("========================================");
        LOG.info("Extent Report generated successfully.");
        LOG.info("Location: {}", ExtentManager.getReportPath());
        LOG.info("========================================");
    }

    /** Returns null (and logs) if the screenshot can't be taken, so reporting still happens. */
    private byte[] captureScreenshot() {
        try {
            return PlaywrightManager.getPage().screenshot();
        } catch (Exception e) {
            LOG.error("Could not capture screenshot.", e);
            return null;
        }
    }

    private void logResult(Scenario scenario, String status, byte[] shot) {
        if (scenario.isFailed()) {
            LOG.error("Scenario FAILED: {}", scenario.getName());
        } else {
            LOG.info("Scenario {}: {}", status, scenario.getName());
        }

        ExtentTest test = ExtentTestManager.getTest();
        if (test == null) return;

        String base64 = (shot == null) ? null : Base64.getEncoder().encodeToString(shot);

        switch (scenario.getStatus()) {
            case FAILED -> {
                ExtentTest result = test.fail("Scenario Failed");
                if (base64 != null) result.addScreenCaptureFromBase64String(base64, "Failure Screenshot");
            }
            case PASSED -> {
                ExtentTest result = test.pass("Scenario Passed");
                if (base64 != null) result.addScreenCaptureFromBase64String(base64, "End State Screenshot");
            }
            default -> {
                ExtentTest result = test.skip("Scenario " + status);
                if (base64 != null) result.addScreenCaptureFromBase64String(base64, "End State Screenshot");
            }
        }
    }

    private void saveToDisk(Scenario scenario, String status, byte[] shot) throws Exception {
        Path dir = Paths.get("target", "screenshots");
        Files.createDirectories(dir);
        String name = status + "_" + scenario.getName().replaceAll("[^a-zA-Z0-9]", "_")
                + "_" + System.currentTimeMillis() + ".png";
        Files.write(dir.resolve(name), shot);
    }

    public static PageManager getPageManager() {
        return PAGE_MANAGER.get();
    }
}