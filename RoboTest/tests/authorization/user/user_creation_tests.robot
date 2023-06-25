*** Settings ***
Documentation     This test suite contains test cases to verify the Create User API functionality.
Test Tags    v1.0.0  user  user_creation
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


*** Test Cases ***
Test Authorization Create User API - Admin role success
    [Tags]    API
    ${payload}    Create Dictionary    email=${test1}[email]    password=${test1}[password]    role=SUBMITTER
    ${response}=    Create User    ${payload}    ${adminAccessToken}   expected_status=201
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  201
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${test1}[email]
    Should Be Equal    ${result}[role]  SUBMITTER

Test Manager Create User API - Access Denied
    [Tags]    API
    Refresh token with Manager1
    ${response}=    Create User    ${test1}    ${manager1AccessToken}   expected_status=403
    Should Be Access Denied    ${response}

Test Specialist Create User API - Access Denied
    [Tags]    API
    Refresh token with Specialist1
    ${response}=    Create User    ${test1}    ${specialist1AccessToken}   expected_status=403
    Should Be Access Denied    ${response}

Test Submitter Create User API - Access Denied
    [Tags]    API
    Refresh token with Submitter1
    ${response}=    Create User    ${test1}    ${submitter1AccessToken}   expected_status=403
    Should Be Access Denied    ${response}

Test None Create User API - Access Denied
    [Tags]    API
    Refresh token with None1
    ${response}=    Create User    ${test1}    ${none1AccessToken}   expected_status=403
    Should Be Access Denied    ${response}

Test No Token Create User API - Unauthorized
    [Tags]    API
    Refresh token with None1
    ${response}=    Create User    ${test1}    ''    expected_status=401

*** Keywords ***
Suite Create User API Setup
    Login with Admin
    Create User    ${manager1}    ${adminAccessToken}
    Create User    ${specialist1}    ${adminAccessToken}
    Create User    ${submitter1}    ${adminAccessToken}
    Create User    ${none1}    ${adminAccessToken}
    Login with Manager1
    Login with Specialist1
    Login with Submitter1
    Login with None1

Login with Admin
    ${response}    Login    ${admin1}[email]  ${admin1}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]

Login with Manager1
    ${response}    Login    ${manager1}[email]  ${manager1}[password]
    Set Suite Variable    ${manager1RefreshToken}  Bearer ${response.json()}[refreshToken]

Login with Specialist1
    ${response}    Login    ${specialist1}[email]  ${specialist1}[password]
    Set Suite Variable    ${specialist1RefreshToken}  Bearer ${response.json()}[refreshToken]

Login with Submitter1
    ${response}    Login    ${submitter1}[email]  ${submitter1}[password]
    Set Suite Variable    ${submitter1RefreshToken}  Bearer ${response.json()}[refreshToken]

Login with None1
    ${response}    Login    ${none1}[email]  ${none1}[password]
    Set Suite Variable    ${none1RefreshToken}  Bearer ${response.json()}[refreshToken]

Suite Create User API Teardown
    Delete Test User    ${manager1}[email]  ${adminAccessToken}
    Delete Test User    ${specialist1}[email]  ${adminAccessToken}
    Delete Test User    ${submitter1}[email]  ${adminAccessToken}
    Delete Test User    ${none1}[email]  ${adminAccessToken}
    Delete Test User    ${test1}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Create User API Setup
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}

Refresh token with Manager1
    ${response}    Refresh    ${manager1RefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${manager1AccessToken}  ${accessToken}

Refresh token with Specialist1
    ${response}    Refresh    ${specialist1RefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${specialist1AccessToken}  ${accessToken}

Refresh token with Submitter1
    ${response}    Refresh    ${submitter1RefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${submitter1AccessToken}  ${accessToken}

Refresh token with None1
    ${response}    Refresh    ${none1RefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${none1AccessToken}  ${accessToken}