*** Settings ***
Documentation    Common user resources for the project
Resource    ../resources/common.robot
Resource    ../resources/api_url.resource
Library    RequestsLibrary

*** Variables ***
#your variables

*** Keywords ***
Delete Test User
    [Arguments]  ${email}  ${accessToken}  ${expected_status}=204
    ${headers}    Create Dictionary    Authorization=${accessToken}
    DELETE    url=${DELETE_USER_API_URL}/${email}  headers=${headers}  expected_status=${expected_status}

Delete Login Attempt
    [Arguments]  ${email}  ${accessToken}  ${expected_status}=204
    ${headers}    Create Dictionary    Authorization=${accessToken}
    DELETE    url=${LOGIN_ATTEMPT_API_URL}/${email}  headers=${headers}  expected_status=${expected_status}

Delete Test Submission
    [Arguments]    ${id}  ${accessToken}  ${expected_status}=204
    ${headers}    Create Dictionary    Content-Type=application/json  Authorization=${accessToken}
    DELETE    url=${DELETE_SUBMISSION_API_URL}/${id}  headers=${headers}  expected_status=${expected_status}
