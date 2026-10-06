package managers;

import com.microsoft.playwright.Browser;
import utils.ConfigReader;

public enum DeviceProfile {
    DESKTOP,
    MOBILE_IN_APP;

    public Browser.NewContextOptions contextOptions() {
        return switch (this) {
            case DESKTOP -> new Browser.NewContextOptions()
                    .setViewportSize(1440,900); // screen size

            case MOBILE_IN_APP -> {
                Browser.NewContextOptions options = new Browser.NewContextOptions()
                        .setViewportSize(390, 844) // screen size
                        .setDeviceScaleFactor(3) // sharpness (pixel density) to behave like a real phone
                        .setIsMobile(true) // behave like a phone
                        .setHasTouch(true); // touch screen

                // Only used once we know how the real in-app browser identifies itself
                String userAgent = ConfigReader.get("mobile.inapp.userAgent"); // to read the .properties file
                if (userAgent != null && !userAgent.isBlank()) {
                    options.setUserAgent(userAgent);
                }
                yield options;
            }
        };
    }

    public static DeviceProfile fromConfig() {
        return valueOf(ConfigReader.get("device").toUpperCase()); // default device is desktop
    }
}

