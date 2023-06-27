*** Settings ***
Documentation     This test suite contains test cases to verify the User APIs functionality.
Test Tags    API  user  user_creation
Suite Setup    Suite User APIs Authorization Setup
Suite Teardown    Suite User APIs Authorization Teardown
Test Setup    Test User APIs Authorization Setup
Library     RequestsLibrary
Resource    ../../resources/common.robot
Resource    ../../resources/api_url.resource
Resource    ../../resources/test_user.resource
Resource    ../../keywords/authentication.robot
Resource    ../../keywords/clean_up.robot
Resource    ../../keywords/user.robot

*** Variables ***
${admin}   ${admin1}
${manager}   ${manager1}
${specialist}    ${specialist1}
${submitter}    ${submitter1}
${none}    ${none1}
${existed_user}    ${user1}
${new_user}    ${user2}

*** Test Cases ***
Test Create User API without Access Token - Unauthorized
    [Tags]    API
    ${response}=    Create User    ${new_user}    ''    expected_status=401

Test Delete User API without Access Token - Unauthorized
    [Tags]    API
    ${response}=    Delete User    ${existed_user_id}    ''    expected_status=401

Test Get All Users API without Access Token - Unauthorized
    [Tags]    API
    ${pageCriteria}=    Create Dictionary    pageNumber=0  pageSize=25
    ${response}=    Get Users    ${pageCriteria}    ''    expected_status=401

Test Get User API without Access Token - Unauthorized
    [Tags]    API
    ${response}=    Get User    ${existed_user_id}    ''    expected_status=401

Test Update User API without Access Token - Unauthorized
    [Tags]    API
    ${payload}=    Create Dictionary    role=SPECIALIST
    ${response}=    Update User    ${existed_user_id}  ${payload}  ''  expected_status=401

Test Create User API with Admin - Created
    [Tags]    API
    ${response}=    Create User    ${new_user}  ${adminAccessToken}  expected_status=201
    #VALIDATIONS
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${new_user}[email]
    Should Be Equal    ${result}[role]  ${new_user}[role]

Test Get All Users API with Admin - Pagination Result
    ${pageCriteria}=    Create Dictionary    pageNumber=0  pageSize=25
    ${response}=    Get Users    ${pageCriteria}  ${adminAccessToken}  expected_status=200
    ${result}    Set Variable    ${response.json()}
    Should Be Simple Pagination result    ${result}

Test Get All Users API - Success
    Get All Users API with ${manager} - Success
    Get All Users API with ${specialist} - Success
    Get All Users API with ${submitter} - Success

Test Get User API - Valid Data
    ${response}=    Get User    ${existed_user_id}  ${adminAccessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${existed_user}[email]
    Should Be Equal    ${result}[role]  SUBMITTER

Test Get User API - Success
    Get User API with ${manager} - Success
    Get User API with ${specialist} - Success
    Get User API with ${submitter} - Success

Test Update User API - Valid Data
    ${payload}=    Create Dictionary    role=SPECIALIST
    ${response}=    Update User    ${existed_user_id}  ${payload}  ${adminAccessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${existed_user}[email]
    Should Be Equal    ${result}[role]  SPECIALIST

Test Create User API - Access Denied
    [Tags]    API
    Create User API with ${manager} - Access Denied
    Create User API with ${specialist} - Access Denied
    Create User API with ${submitter} - Access Denied
    Create User API with ${none} - Access Denied

Test Delete User API - Valid Data
    ${response}=    Delete User    ${existed_user_id}  ${adminAccessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  204

Test Delete User API - Access Denied
    Delete User API with ${manager} - Access Denied
    Delete User API with ${specialist} - Access Denied
    Delete User API with ${submitter} - Access Denied
    Delete User API with ${none} - Access Denied

Test Get All Users API - Access Denied
    Get All Users API with ${none} - Access Denied

Test Get User API - Access Denied
    Get User API with ${none} - Access Denied

Test Update User API - Access Denied
    Update User API with ${manager} - Access Denied
    Update User API with ${specialist} - Access Denied
    Update User API with ${submitter} - Access Denied
    Update User API with ${none} - Access Denied

*** Keywords ***
Suite User APIs Authorization Setup
    Login with Admin
    ${response}=    Create User    ${existed_user}    ${adminAccessToken}   expected_status=201
    Set Suite Variable    ${existed_user_id}  ${response.json()}[id]
    Create Manager and Login with Manager
    Create Specilist and Login with Specilist
    Create Submitter and Login with Submitter
    Create None and Login with None

Login with Admin
    ${response}    Login    ${admin}[email]  ${admin}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]

Create Manager and Login with Manager
    Create User    ${manager}    ${adminAccessToken}
    ${response}=    Login    ${manager}[email]  ${manager}[password]
    Set Suite Variable    ${managerRefreshToken}  Bearer ${response.json()}[refreshToken]

Create Specilist and Login with Specilist
    Create User    ${specialist}    ${adminAccessToken}
    ${response}=    Login    ${specialist}[email]  ${specialist}[password]
    Set Suite Variable    ${specialistRefreshToken}  Bearer ${response.json()}[refreshToken]

Create Submitter and Login with Submitter
    Create User    ${submitter}    ${adminAccessToken}
    ${response}=    Login    ${submitter}[email]  ${submitter}[password]
    Set Suite Variable    ${submitterRefreshToken}  Bearer ${response.json()}[refreshToken]

Create None and Login with None
    Create User    ${none}    ${adminAccessToken}
    ${response}=    Login    ${none}[email]  ${none}[password]
    Set Suite Variable    ${noneRefreshToken}  Bearer ${response.json()}[refreshToken]

Suite User APIs Authorization Teardown
    Delete Test User    ${existed_user}[email]  ${adminAccessToken}
    Delete Test User    ${new_user}[email]  ${adminAccessToken}
    Logout    ${managerRefreshToken}
    Logout    ${specialistRefreshToken}
    Logout    ${submitterRefreshToken}
    Logout    ${noneRefreshToken}
    Delete Test User    ${manager}[email]  ${adminAccessToken}
    Delete Test User    ${specialist}[email]  ${adminAccessToken}
    Delete Test User    ${submitter}[email]  ${adminAccessToken}
    Delete Test User    ${none}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test User APIs Authorization Setup
    ${response}    Refresh    ${adminRefreshToken}
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]

Create User API with ${user} - Access Denied
#    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Create User    ${user}    ${accessToken}   expected_status=403
    Should Be Access Denied    ${response}

Delete User API with ${user} - Access Denied
#    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Delete User    ${existed_user_id}    ${accessToken}   expected_status=403
    Should Be Access Denied    ${response}

Get All Users API with ${user} - Access Denied
#    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${pageCriteria}=    Create Dictionary    pageNumber=0  pageSize=25
    ${response}=    Get Users    ${pageCriteria}  ${accessToken}  expected_status=403
    Should Be Access Denied    ${response}

Get All Users API with ${user} - Success
#    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${pageCriteria}=    Create Dictionary    pageNumber=0  pageSize=25
    ${response}=    Get Users    ${pageCriteria}  ${accessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    Should Be Simple Pagination result    ${response.json()}
#    ${result}    Set Variable    ${response.json()}
#    Should Be Equal    '${result}[number]'  '0'
#    Should Be Equal    '${result}[size]'  '25'
#    Should Be True    ${result}[totalPages] > 0
#    Should Be True    ${result}[totalElements] > 0
#    Should Not Be Empty    ${result}[content]

Should Be Simple Pagination result
    [Arguments]    ${result}
    Should Be Equal    '${result}[number]'  '0'
    Should Be Equal    '${result}[size]'  '25'
    Should Be True    ${result}[totalPages] > 0
    Should Be True    ${result}[totalElements] > 0
    Should Not Be Empty    ${result}[content]

Get User API with ${user} - Access Denied
#    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Get User    ${existed_user_id}  ${accessToken}  expected_status=403
    Should Be Access Denied    ${response}

Get User API with ${user} - Success
#    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Get User    ${existed_user_id}  ${accessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${existed_user}[email]
    Should Be Equal    ${result}[role]  SUBMITTER

Update User API with ${user} - Access Denied
#    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${payload}=    Create Dictionary    role=SPECIALIST
    ${response}=    Update User    ${existed_user_id}  ${payload}  ${accessToken}  expected_status=403
    Should Be Access Denied    ${response}

Update User API with ${user} - Success
#    Create User    ${user}    ${adminAccessToken}
    ${response}=    Login    ${user}[email]  ${user}[password]
    ${response}=    Refresh    Bearer ${response.json()}[refreshToken]
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${payload}=    Create Dictionary    role=SPECIALIST
    ${response}=    Update User    ${existed_user_id}  ${payload}  ${accessToken}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${existed_user}[email]
    Should Be Equal    ${result}[role]  SUBMITTER