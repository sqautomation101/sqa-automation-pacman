package hooks;

import com.aventstack.extentreports.ExtentTest;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import managers.PlaywrightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ExtentManager;
import utils.ExtentTestManager;
import utils.ReportLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class Hooks {

    private static final Logger LOG = LogManager.getLogger(Hooks.class);

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

        LOG.info("Browser opened successfully.");
    }

    /** Runs after every scenario. */
    @After
    public void tearDown(Scenario scenario) {
        String status = scenario.getStatus().name();   // PASSED, FAILED, SKIPPED...

        try {
            byte[] shot = PlaywrightManager.getPage().screenshot();
            String base64 = Base64.getEncoder().encodeToString(shot);
            ExtentTest test = ExtentTestManager.getTest();

            if (scenario.isFailed()) {
                LOG.error("Scenario FAILED: {}", scenario.getName());
                if (test != null) {
                    test.fail("Scenario Failed")
                            .addScreenCaptureFromBase64String(base64, "Failure Screenshot");
                }
            } else {
                LOG.info("Scenario {}: {}", status, scenario.getName());
                if (test != null) {
                    test.pass("Scenario Passed")
                            .addScreenCaptureFromBase64String(base64, "End State Screenshot");
                }
            }

            // Also keep the Cucumber report attachment and a file on disk
            scenario.attach(shot, "image/png", status + " - final state");
            saveToDisk(scenario, status, shot);

        } catch (Exception e) {
            LOG.error("Failed to capture screenshot.", e);

        } finally {
            PlaywrightManager.quit();
            LOG.info("Browser closed.");

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

    private void saveToDisk(Scenario scenario, String status, byte[] shot) throws Exception {
        Path dir = Paths.get("target", "screenshots");
        Files.createDirectories(dir);
        String name = status + "_" + scenario.getName().replaceAll("[^a-zA-Z0-9]", "_") + ".png";
        Files.write(dir.resolve(name), shot);
    }
}