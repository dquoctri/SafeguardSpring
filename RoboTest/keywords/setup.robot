*** Settings ***
Documentation    Common user resources for the project
Resource    ../keywords/common.robot
Resource    ../resources/api_url.resource
Resource    ../resources/test_user.resource
Resource    ./authentication.robot
Resource    ./clean_up.robot
Library    RequestsLibrary

*** Variables ***
#your variables

*** Keywords ***
Suite API Setup
    [Arguments]    ${suite_setup}
    ${response}    Login    ${admin1}[email]  ${admin1}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]
    Run Keyword And Ignore Error    ${suite_setup}

Suite API Teardown
    [Arguments]    ${suite_teardown}
    Run Keyword And Ignore Error    ${suite_teardown}
    Logout    ${adminRefreshToken}

Test API Setup
    [Arguments]    ${test_setup}
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}
    Run Keyword And Ignore Error    ${test_setup}
