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
#your variables

*** Test Cases ***
Test Delete User API - Valid Data
    ${response}=    Delete User    ${test_user_id}  ${adminAccessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  204

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
    Delete Test User    ${test1}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Delete User API Setup
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}
