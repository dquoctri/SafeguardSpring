*** Settings ***
Documentation     This test suite contains test cases to verify the Create User API functionality.
Test Tags    API  user  user_creation
Suite Setup    Suite Create User API Setup
Suite Teardown    Suite Create User API Teardown
Test Setup    Test Create User API Setup
Library     RequestsLibrary
Resource    ../../../resources/common.robot
Resource    ../../../resources/api_url.resource
Resource    ../../../resources/test_user.resource
Resource    ../../../keywords/authentication.robot
Resource    ../../../keywords/clean_up.robot
Resource    ../../../keywords/user.robot

*** Variables ***
${manager}   ${manager1}
${specialist}    ${specialist1}
${submitter}    ${submitter1}
${none}    ${none1}
${test}    ${test1}

*** Test Cases ***
Test Create User API with Admin - Created
    [Tags]    API
    ${payload}    Create Dictionary    email=${test}[email]    password=${test}[password]    role=SUBMITTER
    ${response}=    Create User    ${payload}    ${adminAccessToken}   expected_status=201
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  201
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${test}[email]
    Should Be Equal    ${result}[role]  SUBMITTER

Test Create User API - Access Denied
    [Tags]    API
    Create User API with ${manager} - Access Denied
    Create User API with ${specialist} - Access Denied
    Create User API with ${submitter} - Access Denied
    Create User API with ${none} - Access Denied

Test Create User API without Access Token - Unauthorized
    [Tags]    API
    ${response}=    Create User    ${test}    ''    expected_status=401

*** Keywords ***
Suite Create User API Setup
    Login with Admin

Login with Admin
    ${response}    Login    ${admin1}[email]  ${admin1}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]

Suite Create User API Teardown
    Delete Test User    ${manager}[email]  ${adminAccessToken}
    Delete Test User    ${specialist}[email]  ${adminAccessToken}
    Delete Test User    ${submitter}[email]  ${adminAccessToken}
    Delete Test User    ${none}[email]  ${adminAccessToken}
    Delete Test User    ${test}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Create User API Setup
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}

Create User API with ${user} - Access Denied
    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Create User    ${test}    ${accessToken}   expected_status=403
    Should Be Access Denied    ${response}
