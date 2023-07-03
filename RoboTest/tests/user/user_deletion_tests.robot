*** Settings ***
Documentation     This test suite contains test cases to verify the user ... functionality.
Test Tags    v1.0.0  user  user_deletion
Suite Setup    Suite Delete User API Setup
Suite Teardown    Suite Delete User API Teardown
Test Setup    Test Delete User API Setup
Library     RequestsLibrary
Resource    ../../resources/common.robot
Resource    ../../resources/api_url.resource
Resource    ../../resources/test_user.resource
Resource    ../../keywords/authentication.robot
Resource    ../../keywords/clean_up.robot
Resource    ../../keywords/user.robot

*** Variables ***
#your variables

*** Test Cases ***
Test Delete User API - Valid Data
    ${response}=    Delete User    ${test_user_id}  ${adminAccessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  204

Test Delete User API - Not existed Id
    ${response}=    Delete User    0  ${adminAccessToken}  expected_status=404
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  404
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[message]    User is not found with id: 0
    Should Be True    '${result}[status]'    'NOT_FOUND'

*** Keywords ***
Suite Delete User API Setup
    ${response}    Login    ${admin1}[email]  ${admin1}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]
    ${payload}    Create Dictionary    email=${user1}[email]    password=${user1}[password]    role=SUBMITTER
    ${response}=    Create User    ${payload}    ${adminAccessToken}   expected_status=201
    ${result}    Set Variable    ${response.json()}
    Set Suite Variable    ${test_user_id}  ${result}[id]

Suite Delete User API Teardown
    Delete Test User    ${user1}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Delete User API Setup
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}
