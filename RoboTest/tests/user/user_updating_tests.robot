*** Settings ***
Documentation     This test suite contains test cases to verify the user ... functionality.
Test Tags    v1.0.0  user  user_creation
Library     RequestsLibrary
Resource    ../../resources/common.robot
Resource    ../../resources/api_url.resource
Resource    ../../resources/test_user.resource
Resource    ../../keywords/authentication.robot
Resource    ../../keywords/clean_up.robot
Resource    ../../keywords/user.robot

*** Variables ***

*** Test Cases ***


*** Keywords ***
Suite Create User API Setup
    ${response}    Login    ${admin1}[email]  ${admin1}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]

Suite Create User API Teardown
    Delete Test User    ${user1}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Create User API Setup
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}