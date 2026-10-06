@login
Feature: Login page
  As a user
  I want to see a clear login screen
  So that I can start logging in

  Rule: On desktop, the login page shows store navigation and a promo panel

    Background:
      Given the user is using a desktop browser
      And the user has never logged in on this device

    @layout @desktop
    Scenario: User views the SMAC SSO page content on desktop
      Given the user is on the "Login with your SMAC&SHOP Account" page
      Then the following elements should be displayed:
        | element          | type   | text                              | placeholder                  |
        | Back to Store    | button | Back to Store                     |                              |
        | Login header     | header | Login with your SMAC&SHOP Account |                              |
        | Login input      | input  |                                   | Enter Mobile Number or Email |
        | Proceed          | button | Proceed                           |                              |
        | Or continue with | text   | Or continue with                  |                              |
        | Google           | button | Google                            |                              |
        | Apple            | button | Apple                             |                              |
        | Sign-up          | button | New to SMAC? Sign-up!             |                              |
        | Promo panel      | panel  |                                   |                              |
      And the following elements should not be displayed:
        | element |
        | Home    |

  Rule: In the mobile in-app browser, the login page shows a Home button instead of store navigation

    Background:
      Given the user is using a mobile in-app browser
      And the user has never logged in on this device

    @mobile
    Scenario: User views the SMAC SSO page content on mobile (in-app browser)

      Given the user is on the "Login with your SMAC&SHOP Account" page
      Then the following elements should be displayed:
        | element          | type   | text                              | placeholder                  |
        | Home             | button |                                   |                              |
        | Login header     | header | Login with your SMAC&SHOP Account |                              |
        | Login input      | input  |                                   | Enter Mobile Number or Email |
        | Proceed          | button | Proceed                           |                              |
        | Or continue with | text   | Or continue with                  |                              |
        | Google           | button | Google                            |                              |
        | Apple            | button | Apple                             |                              |
        | Sign-up          | button | New to SMAC? Sign-up!             |                              |

      And the following elements should not be displayed:
        | element       |
        | Back to Store |
        | Promo panel   |