*** Settings ***
Documentation     This test suite contains test cases to verify the Create User API functionality.
Test Tags    v1.0.0  user  user_creation
Library     RequestsLibrary
Resource    ../../../resources/common.robot
Resource    ../../../resources/authentication.robot

*** Variables ***
${BASE_API_URL}    %{BASE_API_URL}
${USER_API_URL}    ${BASE_API_URL}/users


*** Test Cases ***
#Test Create User API - Valid Data
#    [Tags]    API
#    Create User    validuser@example.com    password123    admin
#
#Test Create User API - Empty Email
#    [Tags]    API
#    Create User    ""    password123    admin
#
#Test Create User API - Empty Password
#    [Tags]    API
#    Create User    testuser@example.com    ""    admin
#
#Test Create User API - Invalid Role
#    [Tags]    API
#    Create User    testuser@example.com    password123    invalidrole
#

*** Keywords ***
Create User
    [Arguments]    ${body}  ${accessToken}  ${expected_status}=201
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
#    ${body}    Create Dictionary    email=${email}  password=${password}
    ${response}=    POST    url=${USER_API_URL}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Create User
    [Arguments]    ${accessToken}    ${email}    ${password}    ${role}
    ${headers}    Create Dictionary    Content-Type=application/json  Authorizaion=${accessToken}
    ${payload}    Create Dictionary    email=${email}    password=${password}    role=${role}
    ${response}    POST    url=${USER_API_URL}    json=${payload}    headers=${headers}
    Run Keyword If    '${response.status_code}' == '201'    Log    User created successfully
    Run Keyword If    '${response.status_code}' == '400'    Log    Invalid data: ${response.text}
#    Should Be Equal As Strings    ${response.status_code}    201
#    Should Contain    ${response.text}    User created successfully
