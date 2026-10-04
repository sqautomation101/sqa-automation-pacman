Feature: SMAC SSO Page
Background:
  Given that the user navigates on the Business Unit - Login Entry Point
  Then the "Login with SMAC" button is displayed

  Scenario: User opens the SMAC SSO page
    When the user clicks the "Login with SMAC" button
    Then the SMAC SSO page should be displayed
    And the page URL should contain "smacloginsit.smadvantage.com/auth/login"
    And the page URL should contain "channel_id=ace-hardware"
    And the browser tab title should be "Login"
    And the "Login with your SMAC&SHOP Account" page should be displayed