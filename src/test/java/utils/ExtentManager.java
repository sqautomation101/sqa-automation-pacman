package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class ExtentManager {

    private static final String REPORT_PATH = "target/extent-reports/ExtentReport.html";
    private static ExtentReports extent;

    private ExtentManager() {}

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
            spark.config().setReportName("SQA Automation - Pacman");
            spark.config().setDocumentTitle("Test Execution Report");
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setEncoding("UTF-8");
            spark.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Environment", System.getProperty("env", "sit"));
            extent.setSystemInfo("Browser", valueOrDefault(ConfigReader.get("browser")));
            extent.setSystemInfo("Headless", valueOrDefault(ConfigReader.get("headless")));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("Device", managers.DeviceProfile.fromConfig().name());
        }
        return extent;
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }

    /** Absolute path, so the log line is clickable and unambiguous in CI. */
    public static String getReportPath() {
        Path absolute = Paths.get(REPORT_PATH).toAbsolutePath().normalize();
        return absolute.toString();
    }

    private static String valueOrDefault(String value) {
        return value == null ? "not set" : value;
    }
}