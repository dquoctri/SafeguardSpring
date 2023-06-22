*** Settings ***
Documentation    Common user resources for the project
Library    RequestsLibrary

*** Variables ***
${BASE_API_URL}    %{BASE_API_URL}
${USER_API_URL}    ${BASE_API_URL}/users

*** Keywords ***
Get Users
    [Arguments]    ${accessToken}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${response}=    GET    url=${USER_API_URL}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Get User
    [Arguments]    ${id}  ${body}  ${accessToken}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${response}=    GET    url=${USER_API_URL}/${id}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Create User
    [Arguments]    ${body}  ${accessToken}  ${expected_status}=201
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${response}=    POST    url=${USER_API_URL}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Update User
    [Arguments]    ${id}  ${body}  ${accessToken}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${response}=    PUT    url=${USER_API_URL}/${id}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Delete User
    [Arguments]    ${id}  ${body}  ${accessToken}  ${expected_status}=204
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${response}=    GET    url=${USER_API_URL}/${id}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}