*** Settings ***
Documentation    Common user resources for the project
Library    RequestsLibrary

*** Variables ***
${BASE_API_URL}    %{BASE_API_URL}
${USER_API_URL}    ${BASE_API_URL}/users

*** Keywords ***
Create User
    [Arguments]    ${accessToken}  ${body}  ${expected_status}=201
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
#    ${body}    Create Dictionary    email=${email}  password=${password}
    ${response}=    POST    url=${USER_API_URL}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Create User
    [Arguments]    ${accessToken}  ${body}   ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
#    ${body}    Create Dictionary    email=${email}  password=${password}
    ${response}=    POST    url=${USER_API_URL}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}