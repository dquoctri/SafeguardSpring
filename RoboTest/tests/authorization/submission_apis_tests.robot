*** Settings ***
Documentation     This test suite contains test cases to verify the submission ... functionality.
Test Tags    API  authorization  submission
Suite Setup    Suite Authorization Submission APIs Setup
Suite Teardown    Suite Authorization Submission APIs Teardown
Test Setup    Test Authorization Submission APIs Setup
Test Teardown    Test Authorization Submission APIs Teardown
Library     RequestsLibrary
Resource    ../../resources/common.robot
Resource    ../../resources/api_url.resource
Resource    ../../resources/test_user.resource
Resource    ../../keywords/authentication.robot
Resource    ../../keywords/clean_up.robot
Resource    ../../keywords/user.robot
Resource    ../../keywords/submission.robot

*** Variables ***
#User list
${admin}    ${admin1}
${manager}    ${manager1}
${specialist}    ${specialist1}
${submitter}    ${submitter1}
${none}    ${none1}
#suite var
${adminRefreshToken}
${adminAccessToken}
${managerRefreshToken}
${specialistRefreshToken}
${submitterRefreshToken}
${noneRefreshToken}
#payload
${existed_id}    0
${new_id}    0
&{submissionPayload}    status=DRAFT  content=Example content
&{pageCriteria}    pageNumber=0  pageSize=25

*** Test Cases ***
Test NO Access Token attempts to Create Submission got UNAUTHORIZED
    [Documentation]    This test verifies that a request to submit a submission without an access token results in an UNAUTHORIZED response.
    [Tags]    submission_author_01  create_submission  unauthorized
    Create Submission    ${submissionPayload}  ''  expected_status=401

Test NO Access Token attempts to Delete Submission got UNAUTHORIZED
    [Documentation]    This test verifies that a request to delete a submission without an access token results in an UNAUTHORIZED response.
    [Tags]    submission_author_02  delete_submission  unauthorized
    Delete Submission    ${existed_id}  ''  expected_status=401

Test NO Access Token attempts to Get All Submissions got UNAUTHORIZED
    [Documentation]    This test verifies that a request to get all submissions without an access token results in an UNAUTHORIZED response.
    [Tags]    submission_author_03  get_submissions  unauthorized
    Get Submissions    ${pageCriteria}  ''  expected_status=401

Test NO Access Token attempts to Get Submission Details got UNAUTHORIZED
    [Documentation]    This test verifies that a request to get submission details without an access token results in an UNAUTHORIZED response.
    [Tags]    submission_author_04  get_submission  unauthorized
    Get Submission    ${existed_id}  ''  expected_status=401

Test NO Access Token attempts to Update Submission got UNAUTHORIZED
    [Documentation]    This test verifies that a request to update a submission without an access token results in an UNAUTHORIZED response.
    [Tags]    submission_author_05  update_submission  unauthorized
    ${payload}=    Create Dictionary    role=SPECIALIST
    Update Submission    ${existed_id}  ${payload}  ''  expected_status=401

Test ADMIN user can Create Submission
    [Documentation]    This test verifies that a user with the role ADMIN can successfully create a new submission.
    [Tags]    submission_author_06  create_submission
    ${response}=    Create Submission    ${submissionPayload}  ${adminAccessToken}  expected_status=201
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Set Suite Variable    ${new_id}    ${result}[id]
    Should Be Equal    ${result}[content]  ${submissionPayload}[content]
    Should Be Equal    ${result}[status]  DRAFT

*** Keywords ***
Suite Authorization Submission APIs Setup
    Login with Admin
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

Suite Authorization Submission APIs Teardown
    Logout    ${managerRefreshToken}
    Logout    ${specialistRefreshToken}
    Logout    ${submitterRefreshToken}
    Logout    ${noneRefreshToken}
    Delete Test User    ${manager}[email]  ${adminAccessToken}
    Delete Test User    ${specialist}[email]  ${adminAccessToken}
    Delete Test User    ${submitter}[email]  ${adminAccessToken}
    Delete Test User    ${none}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Authorization Submission APIs Setup
    ${response}    Refresh    ${adminRefreshToken}
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]

Test Authorization Submission APIs Teardown
    ${response}    Refresh    ${adminRefreshToken}
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]
    Delete Test Submission    ${new_id}  Bearer ${response.json()}[accessToken]

Should Be Simple Pagination Response
    [Arguments]    ${result}
    Should Be Equal    '${result}[number]'  '0'
    Should Be Equal    '${result}[size]'  '25'
    Should Be True    ${result}[totalPages] > 0
    Should Be True    ${result}[totalElements] > 0
    Should Not Be Empty    ${result}[content]