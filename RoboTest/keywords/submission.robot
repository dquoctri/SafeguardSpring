*** Settings ***
Documentation    Common submission resources for the project
Resource    ../keywords/common.robot
Resource    ../resources/api_url.resource
Library    RequestsLibrary

*** Variables ***
#your variables

*** Keywords ***
Get Submissions
    [Arguments]    ${params}  ${accessToken}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorization=${accessToken}
    ${response}=    GET    url=${SUBMISSION_API_URL}  headers=${headers}  params=${params}  expected_status=${expected_status}
    RETURN    ${response}

Get Submission
    [Arguments]    ${id}  ${accessToken}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorization=${accessToken}
    ${response}=    GET    url=${SUBMISSION_API_URL}/${id}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Create Submission
    [Arguments]    ${body}  ${accessToken}  ${expected_status}=201
    ${headers}    Create Dictionary    Content-Type=application/json  Authorization=${accessToken}
    ${response}=    POST    url=${SUBMISSION_API_URL}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Update Submission
    [Arguments]    ${id}  ${body}  ${accessToken}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorization=${accessToken}
    ${response}=    PUT    url=${SUBMISSION_API_URL}/${id}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Delete Submission
    [Arguments]    ${id}  ${accessToken}  ${expected_status}=204
    ${headers}    Create Dictionary    Content-Type=application/json  Authorization=${accessToken}
    ${response}=    DELETE    url=${SUBMISSION_API_URL}/${id}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}