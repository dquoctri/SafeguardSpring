*** Settings ***
Documentation     This test suite contains test cases to verify the User APIs functionality.
Test Tags    API  authorization  user
Suite Setup    Suite User APIs Authorization Setup
Suite Teardown    Suite User APIs Authorization Teardown
Test Setup    Test User APIs Authorization Setup
Library     RequestsLibrary
Resource    ../../keywords/common.robot
Resource    ../../resources/api_url.resource
Resource    ../../resources/test_user.resource
Resource    ../../keywords/authentication.robot
Resource    ../../keywords/clean_up.robot
Resource    ../../keywords/user.robot

*** Variables ***
#User list
${admin}    ${admin1}
${manager}    ${manager1}
${specialist}    ${specialist1}
${submitter}    ${submitter1}
${none}    ${none1}
${existed_user}    ${user1}
${new_user}    ${user2}
${updating_user}    ${user4}
${deleting_user}    ${user3}

#suite tokens
${adminRefreshToken}
${adminAccessToken}
${managerRefreshToken}
${specialistRefreshToken}
${submitterRefreshToken}
${noneRefreshToken}
#payload
${existed_user_id}
${updating_user_id}
${deleting_user_id}
&{pageCriteria}    pageNumber=0  pageSize=25
&{specialistPayload}    role=SPECIALIST

*** Test Cases ***
Test NO Access Token attempts to Create User got UNAUTHORIZED
    [Documentation]    This test verifies that a request to create a user without an access token results in an UNAUTHORIZED response.
    [Tags]    user_author_01  create_user  unauthorized
    Create User    ${new_user}  ''  expected_status=401

Test NO Access Token attempts to Delete User got UNAUTHORIZED
    [Documentation]    This test verifies that a request to delete a user without an access token results in an UNAUTHORIZED response.
    [Tags]    user_author_02  delete_user  unauthorized
    Delete User    ${deleting_user_id}  ''  expected_status=401

Test NO Access Token attempts to Get All Users got UNAUTHORIZED
    [Documentation]    This test verifies that a request to get all users without an access token results in an UNAUTHORIZED response.
    [Tags]    user_author_03  get_users  unauthorized
    Get Users    ${pageCriteria}  ''  expected_status=401

Test NO Access Token attempts to Get User Details got UNAUTHORIZED
    [Documentation]    This test verifies that a request to get user details without an access token results in an UNAUTHORIZED response.
    [Tags]    user_author_04  get_user  unauthorized
    Get User    ${existed_user_id}  ''  expected_status=401

Test NO Access Token attempts to Update User got UNAUTHORIZED
    [Documentation]    This test verifies that a request to update a user without an access token results in an UNAUTHORIZED response.
    [Tags]    user_author_05  update_user  unauthorized
    ${payload}=    Create Dictionary    role=SPECIALIST
    Update User    ${updating_user_id}  ${payload}  ''  expected_status=401

Test ADMIN user can Create User
    [Documentation]    This test verifies that a user with the role ADMIN can successfully create a new user.
    [Tags]    user_author_06  create_user  smoke-test
    ${response}=    Create User    ${new_user}  ${adminAccessToken}  expected_status=201
    Should Users Be Equal    ${response.json()}  ${new_user}

Test ADMIN user can Delete User
    [Documentation]    This test verifies that a user with the role ADMIN can successfully delete an existing user.
    [Tags]    user_author_07  delete_user  smoke-test
    Delete User    ${deleting_user_id}  ${adminAccessToken}  expected_status=204

Test ADMIN user can Get All Users
    [Documentation]    This test verifies that a user with the role ADMIN can successfully retrieve all user details.
    [Tags]    user_author_08  get_users  smoke-test
    ${response}=    Refresh    ${adminRefreshToken}
    ${response}=    Get Users    ${pageCriteria}  Bearer ${response.json()}[accessToken]  expected_status=200
    Should Be Simple Pagination Response    ${response.json()}

Test MANAGER user can Get All Users
    [Documentation]    This test verifies that a user with the role MANAGER can successfully retrieve all user details.
    [Tags]    user_author_09  get_users
    ${response}=    Refresh    ${managerRefreshToken}
    ${response}=    Get Users    ${pageCriteria}  Bearer ${response.json()}[accessToken]  expected_status=200
    Should Be Simple Pagination Response    ${response.json()}

Test SPECIALIST user can Get All Users
    [Documentation]    This test verifies that a user with the role SPECIALIST can successfully retrieve all user details.
    [Tags]    user_author_10  get_users
    ${response}=    Refresh    ${specialistRefreshToken}
    ${response}=    Get Users    ${pageCriteria}  Bearer ${response.json()}[accessToken]  expected_status=200
    Should Be Simple Pagination Response    ${response.json()}

Test SUBMITTER user can Get All Users
    [Documentation]    This test verifies that a user with the role SUBMITTER can successfully retrieve all user details.
    [Tags]    user_author_11  get_users  smoke-test
    ${response}=    Refresh    ${submitterRefreshToken}
    ${response}=    Get Users    ${pageCriteria}  Bearer ${response.json()}[accessToken]  expected_status=200
    Should Be Simple Pagination Response    ${response.json()}

Test ADMIN user can Get User details
    [Documentation]    This test verifies that a user with the role ADMIN can retrieve the details of a specific user.
    [Tags]    user_author_12  get_user  smoke-test
    ${response}=    Refresh    ${adminRefreshToken}
    ${response}=    Get User    ${existed_user_id}  Bearer ${response.json()}[accessToken]  expected_status=200
    Should Users Be Equal    ${response.json()}  ${existed_user}

Test MANAGER user can Get User details
    [Documentation]    This test verifies that a user with the role MANAGER can retrieve the details of a specific user.
    [Tags]    user_author_13  get_user
    ${response}=    Refresh    ${managerRefreshToken}
    ${response}=    Get User    ${existed_user_id}  Bearer ${response.json()}[accessToken]  expected_status=200
    Should Users Be Equal    ${response.json()}  ${existed_user}

Test SPECIALIST user can Get User details
    [Documentation]    This test verifies that a user with the role SPECIALIST can retrieve the details of a specific user.
    [Tags]    user_author_14  get_user
    ${response}=    Refresh    ${specialistRefreshToken}
    ${response}=    Get User    ${existed_user_id}  Bearer ${response.json()}[accessToken]  expected_status=200
    Should Users Be Equal    ${response.json()}  ${existed_user}

Test SUBMITTER user can Get User details
    [Documentation]    This test verifies that a user with the role SUBMITTER can retrieve the details of a specific user.
    [Tags]    user_author_15  get_user  smoke-test
    ${response}=    Refresh    ${submitterRefreshToken}
    ${response}=    Get User    ${existed_user_id}  Bearer ${response.json()}[accessToken]  expected_status=200
    Should Users Be Equal    ${response.json()}  ${existed_user}

Test ADMIN user can Update User
    [Documentation]    This test verifies that a user with the role ADMIN can update the details of a specific user with the provided payload.
    [Tags]    user_author_16  update_user  smoke-test
    ${response}=    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Update User    ${updating_user_id}  ${specialistPayload}  ${accessToken}  expected_status=200
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${updating_user}[email]
    Should Be Equal    ${result}[role]  ${specialistPayload}[role]

Test MANAGER user attempt to Create User got Access Denied
    [Documentation]    This test verifies that a user with the role MANAGER is denied access when attempting to create a new user.
    [Tags]    user_author_17  create_user  forbidden
    ${response}=    Refresh    ${managerRefreshToken}
    ${response}=    Create User    ${new_user}  Bearer ${response.json()}[accessToken]  expected_status=403
    Should Be Access Denied    ${response}

Test SPECIALIST user attempt to Create User got Access Denied
    [Documentation]    This test verifies that a user with the role SPECIALIST is denied access when attempting to create a new user.
    [Tags]    user_author_18  create_user  forbidden
    ${response}=    Refresh    ${submitterRefreshToken}
    ${response}=    Create User    ${new_user}  Bearer ${response.json()}[accessToken]  expected_status=403
    Should Be Access Denied    ${response}

Test SUBMITTER user attempt to Create User got Access Denied
    [Documentation]    This test verifies that a user with the role SUBMITTER is denied access when attempting to create a new user.
    [Tags]    user_author_19  create_user  forbidden  smoke-test
    ${response}=    Refresh    ${submitterRefreshToken}
    ${response}=    Create User    ${new_user}  Bearer ${response.json()}[accessToken]  expected_status=403
    Should Be Access Denied    ${response}

Test NONE user attempt to Create User got Access Denied
    [Documentation]    This test verifies that a user with the role NONE is denied access when attempting to create a new user.
    [Tags]    user_author_20  create_user  forbidden
    ${response}=    Refresh    ${noneRefreshToken}
    ${response}=    Create User    ${new_user}  Bearer ${response.json()}[accessToken]  expected_status=403
    Should Be Access Denied    ${response}

Test MANAGER user attempt to Delete User got Access Denied
    [Documentation]    This test verifies that a user with the role MANAGER is denied access when attempting to delete a user.
    [Tags]    user_author_21  delete_user  forbidden
    ${response}=    Refresh    ${managerRefreshToken}
    ${response}=    Delete User    ${deleting_user_id}  Bearer ${response.json()}[accessToken]  expected_status=403
    Should Be Access Denied    ${response}

Test SPECIALIST user attempt to Delete User got Access Denied
    [Documentation]    This test verifies that a user with the role SPECIALIST is denied access when attempting to delete a user.
    [Tags]    user_author_22  delete_user  forbidden
    ${response}=    Refresh    ${submitterRefreshToken}
    ${response}=    Delete User    ${deleting_user_id}  Bearer ${response.json()}[accessToken]  expected_status=403
    Should Be Access Denied    ${response}

Test SUBMITTER user attempt to Delete User got Access Denied
    [Documentation]    This test verifies that a user with the role SUBMITTER is denied access when attempting to delete a user.
    [Tags]    user_author_23  delete_user  forbidden  smoke-test
    ${response}=    Refresh    ${submitterRefreshToken}
    ${response}=    Delete User    ${deleting_user_id}  Bearer ${response.json()}[accessToken]  expected_status=403
    Should Be Access Denied    ${response}

Test NONE user attempt to Delete User got Access Denied
    [Documentation]    This test verifies that a user with the role NONE is denied access when attempting to delete a user.
    [Tags]    user_author_24  delete_user  forbidden
    ${response}=    Refresh    ${noneRefreshToken}
    ${response}=    Delete User    ${deleting_user_id}  Bearer ${response.json()}[accessToken]  expected_status=403
    Should Be Access Denied    ${response}

Test NONE user attempt to Get All Users got Access Denied
    [Documentation]    This test verifies that a user with the role NONE is denied access when attempting to get all user details.
    [Tags]    user_author_25  get_users  forbidden
    ${response}=    Refresh    ${noneRefreshToken}
    ${response}=    Get Users    ${pageCriteria}  Bearer ${response.json()}[accessToken]  expected_status=403
    Should Be Access Denied    ${response}

Test NONE user attempt to Get User details got Access Denied
    [Documentation]    This test verifies that a user with the role NONE is denied access when attempting to get the details of a specific user.
    [Tags]    user_author_26  get_user  forbidden
    ${response}=    Refresh    ${noneRefreshToken}
    ${response}=    Get User    ${existed_user_id}  Bearer ${response.json()}[accessToken]  expected_status=403
    Should Be Access Denied    ${response}

Test MANAGER user attempt to Update User got Access Denied
    [Documentation]    This test verifies that a user with the role MANAGER is denied access when attempting to update the details of a specific user.
    [Tags]    user_author_27  update_user  forbidden
    ${response}=    Refresh    ${managerRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Update User    ${updating_user_id}  ${specialistPayload}  ${accessToken}  expected_status=403
    Should Be Access Denied    ${response}

Test SPECIALIST user attempt to Update User got Access Denied
    [Documentation]    This test verifies that a user with the role SPECIALIST is denied access when attempting to update the details of a specific user.
    [Tags]    user_author_28  update_user  forbidden
    ${response}=    Refresh    ${submitterRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Update User    ${updating_user_id}  ${specialistPayload}  ${accessToken}  expected_status=403
    Should Be Access Denied    ${response}

Test SUBMITTER user attempt to Update User got Access Denied
    [Documentation]    This test verifies that a user with the role SUBMITTER is denied access when attempting to update the details of a specific user.
    [Tags]    user_author_29  update_user  forbidden  smoke-test
    ${response}=    Refresh    ${submitterRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Update User    ${updating_user_id}  ${specialistPayload}  ${accessToken}  expected_status=403
    Should Be Access Denied    ${response}

Test NONE user attempt to Update User got Access Denied
    [Documentation]    This test verifies that a user with the role NONE is denied access when attempting to update the details of a specific user.
    [Tags]    user_author_30  update_user  forbidden
    ${response}=    Refresh    ${noneRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    ${response}=    Update User    ${updating_user_id}  ${specialistPayload}  ${accessToken}  expected_status=403
    Should Be Access Denied    ${response}

*** Keywords ***
Suite User APIs Authorization Setup
    Login with Admin
    ${response}=    Create User    ${existed_user}    ${adminAccessToken}   expected_status=201
    Set Suite Variable    ${existed_user_id}  ${response.json()}[id]
    ${response}=    Create User    ${updating_user}    ${adminAccessToken}   expected_status=201
    Set Suite Variable    ${updating_user_id}  ${response.json()}[id]
    ${response}=    Create User    ${deleting_user}    ${adminAccessToken}   expected_status=201
    Set Suite Variable    ${deleting_user_id}  ${response.json()}[id]
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
    Delete Test User    ${updating_user}[email]  ${adminAccessToken}
    Delete Test User    ${deleting_user}[email]  ${adminAccessToken}
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

Should Be Simple Pagination Response
    [Arguments]    ${result}
    Should Be Equal    '${result}[number]'  '0'
    Should Be Equal    '${result}[size]'  '25'
    Should Be True    ${result}[totalPages] > 0
    Should Be True    ${result}[totalElements] > 0
    Should Not Be Empty    ${result}[content]

Should Users Be Equal
    [Arguments]    ${actual_user}  ${expected_user}
    Should Be True    ${actual_user}[id] > 0
    Should Be Equal    ${actual_user}[email]  ${expected_user}[email]
    Should Be Equal    ${actual_user}[role]  ${expected_user}[role]