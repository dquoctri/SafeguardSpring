*** Settings ***
Documentation     This test suite contains test cases to verify the login functionality.
...               It covers different scenarios related to logging into the system.
Test Tags    v1.0.0  authentication  login
Library     RequestsLibrary
Resource    ../../resources/common.robot
Resource    ../../resources/authentication.robot

*** Variables ***
${SERVER_URL}    %{BASE_API_URL}
${LOGIN_API_URL}    ${SERVER_URL}/auth/login
${admin1_email}      admin1@dqtri.com
${admin1_password}      admin1


*** Test Cases ***
Login with Admin User
    [Documentation]    Tests the successful login functionality with valid credentials for an admin user account.
    ...    Verifies that the system grants access to admin-specific features.
    [Tags]    login-01  smoke-test
    ${response}=    Login    ${admin1_email}    ${admin1_password}
    #VALIDATIONS
    ${status_code}=     Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}    200
    ${result}    Set Variable    ${response.json()}
    Should Not Be Empty   ${result}[accessToken]
    Should Not Be Empty   ${result}[refreshToken]
    Logout    Bearer ${result}[refreshToken]

Login with Empty Body
    [Documentation]    Verifies the system behavior when attempting to log in with an empty request body.
    ...    Checks if the system handles this scenario appropriately and provides an expected response.
    [Tags]    login-02
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary
    ${response}=  POST  url=${LOGIN_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Login without Email
    [Documentation]    Tests the behavior of the login process when the email field is missing from the request body.
    ...    Checks if the system detects the absence of the email field and responds with an appropriate error message or validation.
    [Tags]    login-03
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary  password=${admin1_password}
    ${response}=  POST  url=${LOGIN_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Login without Password
    [Documentation]    Verifies the system's response when attempting to log in without providing a password.
    ...    Tests if the system correctly identifies the missing password and handles it accordingly, displaying
    ...    the expected error message or validation.
    [Tags]    login-04
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary  email=${admin1_email}
    ${response}=  POST  url=${LOGIN_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Login with application/text Content Type
    [Documentation]    Tests the behavior of the login process when the request has an incorrect content type,
    ...    specifically "application/text." Verifies if the system rejects the request with an appropriate response
    ...    and returns the expected error message or validation.
    [Tags]    login-05
    ${headers}=    Create Dictionary    Content-Type=application/text
    ${body}=    Create Dictionary    email=${admin1_email}  password=${admin1_password}
    ${response}=  POST  url=${LOGIN_API_URL}  json=${body}  headers=${headers}  expected_status=415
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  415
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[title]  Unsupported Media Type
    Should Be Equal    ${result}[detail]  Content-Type 'application/text' is not supported.

Login with Invalid Email Format
    [Documentation]    Validates the system's response when attempting to log in with an email address
    ...    that doesn't comply with the expected format. Tests if the system identifies the invalid email format
    ...    and handles it appropriately, displaying the expected error message or validation.
    [Tags]    login-06
    ${too_long_email}    Set Variable    Loremipsumdolorsitamet5consecteturadipiscingelit5seddoeiusmodtemporincididuntutlaboreetdoloremagnaaliqua.Utenimadminimveniam5quisnostrudexercitationullamcolaborisnisiutaliquipexeacommodoconsequat.Duisauteiruredolorinreprehenderitinvoluptatevelitessecillumdoloreeufugiatnullapariatur3Excepteursintoccaecatcupida@dqtri.com
    @{invalid_formar_emails}    Create List    invalidEmailFormat  invalidEmailFormat@  @invalidEmailFormat"  invalid@Email@Format  ${too_long_email}
    FOR    ${email}    IN    @{invalid_formar_emails}
    ${response}    Login    ${email}  ${admin1_email}  expected_status=400
    Should Be Bad Request    ${response}
    END

Login with Invalid Password Format
    [Documentation]    Verifies the system behavior when attempting to log in with a password that doesn't meet
    ...    the specified format requirements. Checks if the system detects the invalid password format and responds
    ...    with the expected error message or validation.
    [Tags]    login-07
    @{invalid_formar_passwords}    Create List    st  ''   '         '  too_long_password_that_more_than_24_characters
    FOR    ${password}    IN    @{invalid_formar_passwords}
        ${response}    Login    ${admin1_email}  ${password}  expected_status=400
        Should Be Bad Request    ${response}
    END