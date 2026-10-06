package stepDefinitions;

import com.aventstack.extentreports.model.Report;
import io.cucumber.java.en.Given;
import managers.DeviceProfile;
import managers.PlaywrightManager;
import utils.ReportLogger;

public class PlatformSteps {

    @Given("the user is using a desktop browser")
    public void theUserIsUsingADesktopBrowser() {
        PlaywrightManager.startSession(DeviceProfile.DESKTOP);
        ReportLogger.info("Using a desktop browser");
    }

    @Given("the user is using a mobile in-app browser")
    public void theUserIsUsingAMobileInAppBrowser() {
        PlaywrightManager.startSession(DeviceProfile.MOBILE_IN_APP);
        ReportLogger.info("Using a mobile in-app browser");
    }

    @Given("the user has never logged in on this device")
    public void theUserHasNeverLoggedInOnThisDevice() {
        // Every scenario starts with a fresh session so no further action needed startSession()
        ReportLogger.info("Fresh device session: no previous logins on this device");
    }
}
