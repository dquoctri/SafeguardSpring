*** Settings ***
Documentation    Common user resources for the project
Library    RequestsLibrary

*** Variables ***
${BASE_API_URL}    %{BASE_API_URL}
${SUBMISSION_API_URL}    ${BASE_API_URL}/users

*** Keywords ***
Create Submission
    [Arguments]    ${accessToken}  ${body}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
#    ${body}    Create Dictionary    email=${email}  password=${password}
    ${response}=    POST    url=${SUBMISSION_API_URL}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}