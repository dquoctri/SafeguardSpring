*** Settings ***
Documentation     This test suite contains test cases to verify the login functionality.
...               It covers different scenarios related to logging into the system.
Test Tags    API  authentication  login
Suite Setup    Suite Login API Setup
Suite Teardown    Suite Login API Teardown
Test Setup    Test Login API Setup
Library     RequestsLibrary
Resource    ../../keywords/common.robot
Resource    ../../resources/api_url.resource
Resource    ../../resources/test_user.resource
Resource    ../../keywords/authentication.robot
Resource    ../../keywords/clean_up.robot

*** Variables ***
${invalid_email}    invalid_email@dqtri.com

*** Test Cases ***
Test Login API - Admin User
    [Documentation]    Tests the successful login functionality with valid credentials for an admin user account.
    ...    Verifies that the system grants access to admin-specific features.
    [Tags]    login-01  smoke-test
    ${response}=    Login    ${admin1}[email]    ${admin1}[password]
    #VALIDATIONS
    ${status_code}=     Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}    200
    ${result}    Set Variable    ${response.json()}
    Should Not Be Empty   ${result}[accessToken]
    Should Not Be Empty   ${result}[refreshToken]

Test Login API - Empty Body
    [Documentation]    Verifies the system behavior when attempting to log in with an empty request body.
    ...    Checks if the system handles this scenario appropriately and provides an expected response.
    [Tags]    login-02
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary
    ${response}=  POST  url=${LOGIN_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Test Login API - without Email
    [Documentation]    Tests the behavior of the login process when the email field is missing from the request body.
    ...    Checks if the system detects the absence of the email field and responds with an appropriate error message or validation.
    [Tags]    login-03
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary  password=${admin1}[password]
    ${response}=  POST  url=${LOGIN_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Test Login API - without Password
    [Documentation]    Verifies the system's response when attempting to log in without providing a password.
    ...    Tests if the system correctly identifies the missing password and handles it accordingly, displaying
    ...    the expected error message or validation.
    [Tags]    login-04
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary  email=${admin1}[email]
    ${response}=  POST  url=${LOGIN_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Test Login API - Content Type application/text
    [Documentation]    Tests the behavior of the login process when the request has an incorrect content type,
    ...    specifically "application/text." Verifies if the system rejects the request with an appropriate response
    ...    and returns the expected error message or validation.
    [Tags]    login-05
    ${headers}=    Create Dictionary    Content-Type=application/text
    ${body}=    Create Dictionary    email=${admin1}[email]  password=${admin1}[password]
    ${response}=  POST  url=${LOGIN_API_URL}  json=${body}  headers=${headers}  expected_status=415
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  415
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[title]  Unsupported Media Type
    Should Be Equal    ${result}[detail]  Content-Type 'application/text' is not supported.

Test Login API - Invalid Email Format
    [Documentation]    Validates the system's response when attempting to log in with an email address
    ...    that doesn't comply with the expected format. Tests if the system identifies the invalid email format
    ...    and handles it appropriately, displaying the expected error message or validation.
    [Tags]    login-06
    ${too_long_email}    Set Variable    Loremipsumdolorsitamet5consecteturadipiscingelit5seddoeiusmodtemporincididuntutlaboreetdoloremagnaaliqua.Utenimadminimveniam5quisnostrudexercitationullamcolaborisnisiutaliquipexeacommodoconsequat.Duisauteiruredolorinreprehenderitinvoluptatevelitessecillumdoloreeufugiatnullapariatur3Excepteursintoccaecatcupida@dqtri.com
    @{invalid_formar_emails}    Create List    invalidEmailFormat  invalidEmailFormat@  @invalidEmailFormat  invalid@Email@Format  ${too_long_email}
    FOR    ${email}    IN    @{invalid_formar_emails}
    ${response}    Login    ${email}  ${admin1}[email]  expected_status=400
    Should Be Bad Request    ${response}
    END

Test Login API - Invalid Password Format
    [Documentation]    Verifies the system behavior when attempting to log in with a password that doesn't meet
    ...    the specified format requirements. Checks if the system detects the invalid password format and responds
    ...    with the expected error message or validation.
    [Tags]    login-07
    @{invalid_format_passwords}    Create List    st  ''   '         '  too_long_password_that_more_than_24_characters
    FOR    ${password}    IN    @{invalid_format_passwords}
        ${response}    Login    ${admin1}[email]  ${password}  expected_status=400
        Should Be Bad Request    ${response}
    END

Test Login API - Invalid Email and Password
    [Documentation]    Tests the login functionality with invalid email and password credentials.
    ...    Checks if the system handles this scenario appropriately and provides an expected response.
    [Tags]    login-08
    ${response}=    Login    ${invalid_email}.vn    invalid_password   expected_status=401
    #VALIDATIONS
    ${status_code}=     Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}    401
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[status]  UNAUTHORIZED
    Should Be Equal    ${result}[message]  Bad credentials

Test Login API - Invalid Email and Password For 5 Times
    [Documentation]    Tests the try login functionality with invalid email and password credentials multibles times.
    ...    Checks if the system handles this scenario appropriately and provides an expected response.
    [Tags]    login-09
    Repeat Keyword    5 times    Login    ${invalid_email}    invalid_password1   expected_status=401
    Repeat Keyword    20 times    Login    ${invalid_email}    invalid_password5   expected_status=422
    ${response}=    Login    ${invalid_email}    invalid_password5   expected_status=422
    #VALIDATIONS
    ${status_code}=     Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}    422
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[status]  UNPROCESSABLE_ENTITY
    Should Be Equal    ${result}[message]  ${invalid_email} has been locked due to multiple failed login attempts

*** Keywords ***
Suite Login API Setup
    ${response}    Login    ${admin1}[email]  ${admin1}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]

Suite Login API Teardown
    Delete Login Attempt    ${invalid_email}.vn  ${adminAccessToken}
    Delete Login Attempt    ${invalid_email}  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Login API Setup
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}