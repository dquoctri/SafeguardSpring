*** Settings ***
Documentation    Common authentication resources for the project
Library    RequestsLibrary

*** Variables ***
${SERVER_URL}    http://localhost:8152/safeguard/api
#${SERVER_URL}    %{BASE_API_URL}
${LOGIN_API_URL}    ${SERVER_URL}/auth/login
${REFRESH_API_URL}    ${SERVER_URL}/auth/refresh
${LOGOUT_API_URL}    ${SERVER_URL}/auth/logout

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