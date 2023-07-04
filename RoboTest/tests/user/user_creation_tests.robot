*** Settings ***
Documentation     This test suite contains test cases to verify the Create User API functionality.
Test Tags    API  user  user_creation
Suite Setup    Suite Create User API Setup
Suite Teardown    Suite Create User API Teardown
Test Setup    Test Create User API Setup
Library     RequestsLibrary
Resource    ../../keywords/common.robot
Resource    ../../resources/api_url.resource
Resource    ../../resources/test_user.resource
Resource    ../../keywords/authentication.robot
Resource    ../../keywords/clean_up.robot
Resource    ../../keywords/user.robot

*** Variables ***


*** Test Cases ***
Test Create User API - Valid Data
    [Tags]    API
    ${payload}    Create Dictionary    email=${user1}[email]    password=${user1}[password]    role=SUBMITTER
    ${response}=    Create User    ${payload}    ${adminAccessToken}   expected_status=201
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  201
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${user1}[email]
    Should Be Equal    ${result}[role]  SUBMITTER

Test Create User API - Empty Body
    [Tags]    API
    ${payload}    Create Dictionary
    ${response}=    Create User    ${payload}    ${adminAccessToken}    expected_status=400
    Should Be Bad Request    ${response}

Test Create User API - Empty Email
    [Tags]    API
    ${payload}    Create Dictionary   password=${user1}[password]    role=SUBMITTER
    ${response}=    Create User    ${payload}    ${adminAccessToken}    expected_status=400
    Should Be Bad Request    ${response}

Test Create User API - Empty Password
    [Tags]    API
    ${payload}    Create Dictionary   email=${user1}[email]    role=SUBMITTER
    ${response}=    Create User    ${payload}    ${adminAccessToken}    expected_status=400
    Should Be Bad Request    ${response}

Test Create User API - Invalid Role
    [Tags]    API
    ${payload}    Create Dictionary   email=${user1}[email]  password=${user1}[password]  role=HELLO
    ${response}=    Create User    ${payload}    ${adminAccessToken}    expected_status=400
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  400
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[title]  Bad Request
    Should Be Equal    ${result}[detail]  Failed to read request

#todo

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


