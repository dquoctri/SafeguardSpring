*** Settings ***
Documentation    Common authentication resources for the project
Resource    ../keywords/common.robot
Resource    ../resources/api_url.resource
Library    RequestsLibrary

*** Variables ***
#your variables

*** Keywords ***
Login
    [Arguments]    ${email}  ${password}  ${expected_status}=200
    ${headers}    Create Dictionary    Content-Type=application/json
    ${body}    Create Dictionary    email=${email}  password=${password}
    ${response}=    POST    url=${LOGIN_API_URL}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Refresh
    [Arguments]    ${refreshToken}  ${expected_status}=200
    ${headers}    Create Dictionary    Authorization=${refreshToken}
    ${response}=    GET    url=${REFRESH_API_URL}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Logout
    [Arguments]  ${refreshToken}  ${expected_status}=204
    ${headers}=    Create Dictionary    Authorization=${refreshToken}  expected_status=${expected_status}
    DELETE    url=${LOGOUT_API_URL}  headers=${headers}

Register
    [Arguments]    ${email}  ${password}  ${expected_status}=200
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary    email=${email}    password=${password}
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}