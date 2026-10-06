package stepDefinitions;

import com.microsoft.playwright.Locator;
import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import utils.ReportLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CommonSteps {

    // =========================
    // HELPERS
    // =========================
    private Locator locatorFor(String elementName) {
        return Hooks.getPageManager().locatorFor(elementName);
    }

    // =========================
    // VERIFICATION - ELEMENT TABLES
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
    // PRIVATE HELPERS
    // =========================

    /**
     * Checks the element's HTML tag (or role) matches the type in the table.
     */
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

    /**
     * Passes if no failures; otherwise reports all of them at once.
     */
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