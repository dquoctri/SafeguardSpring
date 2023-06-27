*** Settings ***
Documentation     This test suite contains test cases to verify the user ... functionality.
Test Tags    v1.0.0  user  user_creation
Suite Setup    Suite Get All Users API Setup
Suite Teardown    Suite Get All Users API Teardown
Test Setup    Test Get All Users API Setup
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
Test Get All Users API - Valid Data
    ${pageCriteria}=    Create Dictionary    pageNumber=0  pageSize=25
    ${response}=    Get Users    ${pageCriteria}  ${adminAccessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    '${result}[number]'  '0'
    Should Be Equal    '${result}[size]'  '25'
    Should Be True    ${result}[totalPages] > 0
    Should Be True    ${result}[totalElements] > 0
    Should Not Be Empty    ${result}[content]

Test Get All Users API - Success
    Get All Users API with ${manager} - Success
    Get All Users API with ${specialist} - Success
    Get All Users API with ${submitter} - Success

Test Get All Users API - Access Denied
    Get All Users API with ${none} - Access Denied

Test Get All Users API without Access Token - Unauthorized
    [Tags]    API
    ${pageCriteria}=    Create Dictionary    pageNumber=0  pageSize=25
    ${response}=    Get Users    ${pageCriteria}    ''    expected_status=401

*** Keywords ***
Suite Get All Users API Setup
    ${response}    Login    ${admin1}[email]  ${admin1}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]

Suite Get All Users API Teardown
    Delete Test User    ${none}[email]  ${adminAccessToken}
    Delete Test User    ${manager}[email]  ${adminAccessToken}
    Delete Test User    ${submitter}[email]  ${adminAccessToken}
    Delete Test User    ${specialist}[email]  ${adminAccessToken}
    Delete Test User    ${test}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Get All Users API Setup
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}

Get All Users API with ${user} - Access Denied
    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${pageCriteria}=    Create Dictionary    pageNumber=0  pageSize=25
    ${response}=    Get Users    ${pageCriteria}  ${accessToken}  expected_status=403
    Should Be Access Denied    ${response}

Get All Users API with ${user} - Success
    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${pageCriteria}=    Create Dictionary    pageNumber=0  pageSize=25
    ${response}=    Get Users    ${pageCriteria}  ${accessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    '${result}[number]'  '0'
    Should Be Equal    '${result}[size]'  '25'
    Should Be True    ${result}[totalPages] > 0
    Should Be True    ${result}[totalElements] > 0
    Should Not Be Empty    ${result}[content]