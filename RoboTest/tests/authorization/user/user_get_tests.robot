*** Settings ***
Documentation     This test suite contains test cases to verify the user ... functionality.
Test Tags    v1.0.0  user  user_creation
Suite Setup    Suite Get User API Setup
Suite Teardown    Suite Get User API Teardown
Test Setup    Test Get User API Setup
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
Test Get User API - Valid Data
    ${response}=    Get User    ${test_user_id}  ${adminAccessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${test}[email]
    Should Be Equal    ${result}[role]  SUBMITTER

Test Get User API - Success
    Get User API with ${manager} - Success
    Get User API with ${specialist} - Success
    Get User API with ${submitter} - Success

Test Get User API - Access Denied
    Get User API with ${none} - Access Denied

Test Get User API without Access Token - Unauthorized
    [Tags]    API
    ${response}=    Get User    ${test_user_id}    ''    expected_status=401

*** Keywords ***
Suite Get User API Setup
    ${response}    Login    ${admin1}[email]  ${admin1}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]
    ${payload}    Create Dictionary    email=${test1}[email]    password=${test1}[password]    role=SUBMITTER
    ${response}=    Create User    ${payload}    ${adminAccessToken}   expected_status=201
    ${result}    Set Variable    ${response.json()}
    Set Suite Variable    ${test_user_id}  ${result}[id]

Suite Get User API Teardown
    Delete Test User    ${none}[email]  ${adminAccessToken}
    Delete Test User    ${manager}[email]  ${adminAccessToken}
    Delete Test User    ${submitter}[email]  ${adminAccessToken}
    Delete Test User    ${specialist}[email]  ${adminAccessToken}
    Delete Test User    ${test}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Get User API Setup
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}

Get User API with ${user} - Access Denied
    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Get User    ${test_user_id}  ${accessToken}  expected_status=403
    Should Be Access Denied    ${response}

Get User API with ${user} - Success
    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Get User    ${test_user_id}  ${accessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${test}[email]
    Should Be Equal    ${result}[role]  SUBMITTER