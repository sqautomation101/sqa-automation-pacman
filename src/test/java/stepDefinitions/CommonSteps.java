package stepDefinitions;

import com.microsoft.playwright.Locator;
import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import managers.PlaywrightManager;
import utils.ReportLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static pages.LandingPage.escapeForRegex;

public class CommonSteps {

    // =========================
    // ACTIONS
    // =========================
    @When("the user clicks the {string} button")
    public void theUserClicksTheButton(String buttonName) {
        ReportLogger.info("Clicking the " + buttonName + " button");
        try {
            locatorFor(buttonName).click();
            ReportLogger.pass("Successfully clicked the " + buttonName + " button");
        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Failed to click the " + buttonName + " button. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    // =========================
    // VERIFICATION - SINGLE ELEMENT (fail-fast)
    // =========================
    @Then("the {string} button is displayed")
    public void theButtonIsDisplayed(String buttonName) {
        verifyElementDisplayed(buttonName);
    }

    @Then("the {string} page should be displayed")
    public void thePageShouldBeDisplayed(String pageName) {
        verifyElementDisplayed(pageName);
    }

    // =========================
    // VERIFICATION - ELEMENT TABLES (collects all failures)
    // =========================
    @Then("the following elements should be displayed:")
    public void theFollowingElementsShouldBeDisplayed(DataTable table) {
        List<String> failures = new ArrayList<>();

        for (Map<String, String> row : table.asMaps()) {
            String name = row.get("element");
            ReportLogger.info("Checking that '" + name + "' is displayed");

            try {
                Locator element = locatorFor(name);

                // 1. Visible?
                assertThat(element).isVisible();

                // 2. Right type? (button, header, input...)
                String type = row.get("type");
                if (type != null && !matchesType(element, type)) {
                    failures.add(name + ": expected type '" + type + "'");
                }

                // 3. Right text?
                String text = row.get("text");
                if (text != null) {
                    String actual = element.innerText().replaceAll("\\s+", " ").trim();
                    if (!actual.equals(text)) {
                        failures.add(name + ": expected text '" + text + "' but was '" + actual + "'");
                    }
                }

                // 4. Right placeholder?
                String placeholder = row.get("placeholder");
                if (placeholder != null) {
                    String actual = element.getAttribute("placeholder");
                    if (!placeholder.equals(actual)) {
                        failures.add(name + ": expected placeholder '" + placeholder + "' but was '" + actual + "'");
                    }
                }

            } catch (Exception | AssertionError e) {
                failures.add(name + ": " + e.getMessage());
            }
        }

        reportResult(failures, "All elements are displayed correctly");
    }

    @Then("the following elements should not be displayed:")
    public void theFollowingElementsShouldNotBeDisplayed(DataTable table) {
        List<String> failures = new ArrayList<>();

        for (Map<String, String> row : table.asMaps()) {
            String name = row.get("element");
            ReportLogger.info("Checking that '" + name + "' is not displayed");

            try {
                assertThat(locatorFor(name)).isHidden();   // passes if hidden OR not on the page
            } catch (Exception | AssertionError e) {
                failures.add(name + ": should not be displayed");
            }
        }

        reportResult(failures, "None of the elements are displayed");
    }

    // =========================
    // VERIFICATION - URL
    // =========================
    @Then("the page URL should contain {string}")
    public void thePageUrlShouldContain(String expectedPart) {
        verifyUrlContains(expectedPart);
    }

    @Then("the channel id should contain {string}")
    public void theChannelIdShouldContain(String expectedPart) {
        verifyUrlContains(expectedPart);
    }

    // =========================
    // VERIFICATION - TAB TITLE
    // =========================
    @Then("the browser tab title should be {string}")
    public void theBrowserTabTitleShouldBe(String expectedTitle) {
        ReportLogger.info("Verifying that the browser tab title is '" + expectedTitle + "'");
        try {
            assertThat(PlaywrightManager.getPage()).hasTitle(expectedTitle);
            ReportLogger.pass("Browser tab title is '" + expectedTitle + "'");
        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Browser tab title is not '" + expectedTitle + "'. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    // =========================
    // PRIVATE HELPERS
    // =========================

    /** Finds the element on whichever page has it. */
    private Locator locatorFor(String elementName) {
        return Hooks.getPageManager().locatorFor(elementName);
    }

    /** Checks one element is visible; stops the scenario immediately if not. */
    private void verifyElementDisplayed(String elementName) {
        ReportLogger.info("Verifying that the " + elementName + " is displayed");
        try {
            assertThat(locatorFor(elementName)).isVisible();
            ReportLogger.pass("Successfully verified that the " + elementName + " is displayed");
        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Failed to verify the " + elementName + ". Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    /** Checks the current page URL contains the given text. */
    private void verifyUrlContains(String expectedPart) {
        ReportLogger.info("Verifying that the page URL contains '" + expectedPart + "'");
        try {
            // escapeForRegex (not Pattern.quote) because Playwright sends this to the browser as a JS regex
            assertThat(PlaywrightManager.getPage())
                    .hasURL(Pattern.compile(".*" + escapeForRegex(expectedPart) + ".*"));
            ReportLogger.pass("Page URL contains '" + expectedPart + "'");
        } catch (Exception | AssertionError e) {
            ReportLogger.fail("Page URL does not contain '" + expectedPart + "'. Error: "
                    + e.getMessage(), e);
            throw e;
        }
    }

    /** Checks the element's HTML tag (or role) matches the type in the table. */
    private boolean matchesType(Locator element, String type) {
        String tag = (String) element.evaluate("el => el.tagName.toLowerCase()");
        String role = element.getAttribute("role");

        return switch (type) {
            case "button" -> tag.equals("button") || "button".equals(role);
            case "header" -> tag.matches("h[1-6]") || "heading".equals(role);
            case "input" -> tag.equals("input") || tag.equals("textarea");
            case "panel" -> tag.matches("div|section|aside") || "region".equals(role);
            case "image" -> tag.equals("img") || "img".equals(role);
            case "link" -> tag.equals("a");
            case "text" -> true;   // any element with text
            default -> throw new IllegalArgumentException("Unknown type in table: '" + type + "'");
        };
    }

    /** Passes if no failures; otherwise reports all of them at once. */
    private void reportResult(List<String> failures, String successMessage) {
        if (failures.isEmpty()) {
            ReportLogger.pass(successMessage);
            return;
        }
        AssertionError error = new AssertionError(
                failures.size() + " element check(s) failed:\n- " + String.join("\n- ", failures));
        ReportLogger.fail(error.getMessage(), error);
        throw error;
    }
}