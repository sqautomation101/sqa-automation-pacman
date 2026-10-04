package utils;

import com.aventstack.extentreports.ExtentTest;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ReportLogger {

    private static final Logger LOG = LogManager.getLogger(ReportLogger.class);
    private static final ThreadLocal<Scenario> SCENARIO = new ThreadLocal<>();

    /** Set to true to also write every log line into the Cucumber report. */
    private static final boolean ATTACH_TO_CUCUMBER = false;

    private ReportLogger() {}

    public static void setScenario(Scenario scenario) {
        SCENARIO.set(scenario);
    }

    public static void clear() {
        SCENARIO.remove();
    }

    public static void info(String message) {
        LOG.info("⚙️ INFO: {}", message);
        attach("⚙️ INFO: " + message);
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) test.info(message);
    }

    public static void pass(String message) {
        LOG.info("✅ PASS: {}", message);
        attach("✅ PASS: " + message);
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) test.pass(message);
    }

    public static void fail(String message) {
        LOG.error("❌ FAIL: {}", message);
        attach("❌ FAIL: " + message);
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) test.fail(message);
    }

    public static void fail(String message, Throwable t) {
        LOG.error("❌ FAIL: {}", message, t);
        attach("❌ FAIL: " + message);
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.fail(message);
            test.fail(t);   // adds the exception and stack trace to the Extent report
        }
    }

    private static void attach(String message) {
        if (!ATTACH_TO_CUCUMBER) return;
        Scenario scenario = SCENARIO.get();
        if (scenario != null) {
            scenario.log(message);
        }
    }
}