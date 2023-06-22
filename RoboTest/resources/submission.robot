*** Settings ***
Documentation    Common submission resources for the project
Library    RequestsLibrary

*** Variables ***
${BASE_API_URL}    %{BASE_API_URL}
${SUBMISSION_API_URL}    ${BASE_API_URL}/submissions

*** Keywords ***
Get Submissions
    [Arguments]    ${accessToken}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${response}=    GET    url=${SUBMISSION_API_URL}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Get Submission
    [Arguments]    ${id}  ${body}  ${accessToken}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${response}=    GET    url=${SUBMISSION_API_URL}/${id}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Create Submission
    [Arguments]    ${body}  ${accessToken}  ${expected_status}=201
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${response}=    POST    url=${SUBMISSION_API_URL}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Update Submission
    [Arguments]    ${id}  ${body}  ${accessToken}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${response}=    PUT    url=${SUBMISSION_API_URL}/${id}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Delete Submission
    [Arguments]    ${id}  ${body}  ${accessToken}  ${expected_status}=204
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${response}=    GET    url=${SUBMISSION_API_URL}/${id}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}