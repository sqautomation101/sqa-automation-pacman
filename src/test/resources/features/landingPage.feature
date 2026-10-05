Feature: Landing page
Background:
    Given that the user navigates on the Business Unit - Login Entry Point

    Scenario: User navigates to the Business Unit - Login Entry Point
    Then the "Login with SMAC" button is displayed
    And the "Create account with SMAC" button is displayed

    Scenario: User opens the SMAC  Login page
      When the user clicks the "Login with SMAC" button
      Then the "New to SMAC? Sign-up!" button is displayed
      And the page URL should contain "smacloginsit.smadvantage.com/auth/login"
      And the channel id should contain "ace-hardware"
      And the browser tab title should be "Login"

    Scenario: User opens the SMAC SSO Registration page
      When the user clicks the "Create account with SMAC" button
      Then the "Create a SMAC account in just 3 minutes!" button is displayed
      And the page URL should contain "smacloginsit.smadvantage.com/auth/register"
      And the page URL should contain "channel_id=ace-hardware"
      And the browser tab title should be "Create Account"








