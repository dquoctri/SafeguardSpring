*** Settings ***
Documentation     This test suite contains test cases to verify the user ... functionality.
Test Tags    v1.0.0  user  user_deletion
Suite Setup    Suite Delete User API Setup
Suite Teardown    Suite Delete User API Teardown
Test Setup    Test Delete User API Setup
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
Test Delete User API - Valid Data
    ${response}=    Delete User    ${test_user_id}  ${adminAccessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  204

Test Delete User API - Access Denied
    Delete User API with ${manager} - Access Denied
    Delete User API with ${specialist} - Access Denied
    Delete User API with ${submitter} - Access Denied
    Delete User API with ${none} - Access Denied

Test Delete User API without Access Token - Unauthorized
    [Tags]    API
    ${response}=    Delete User    ${test_user_id}    ''    expected_status=401

*** Keywords ***
Suite Delete User API Setup
    ${response}    Login    ${admin1}[email]  ${admin1}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]
    ${payload}    Create Dictionary    email=${test1}[email]    password=${test1}[password]    role=SUBMITTER
    ${response}=    Create User    ${payload}    ${adminAccessToken}   expected_status=201
    ${result}    Set Variable    ${response.json()}
    Set Suite Variable    ${test_user_id}  ${result}[id]

Suite Delete User API Teardown
    Delete Test User    ${test}[email]  ${adminAccessToken}
    Delete Test User    ${none}[email]  ${adminAccessToken}
    Delete Test User    ${manager}[email]  ${adminAccessToken}
    Delete Test User    ${submitter}[email]  ${adminAccessToken}
    Delete Test User    ${specialist}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Delete User API Setup
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}

Delete User API with ${user} - Access Denied
    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Delete User    ${test_user_id}    ${accessToken}   expected_status=403
    Should Be Access Denied    ${response}
