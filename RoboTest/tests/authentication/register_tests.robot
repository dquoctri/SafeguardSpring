*** Settings ***
Documentation     This test suite contains test cases to verify the registration functionality.
...               It covers different scenarios related to user registration.
Test Tags    API authentication  register
Suite Setup    Suite Register API Setup
Suite Teardown    Suite Register API Teardown
Test Setup    Test Register API Setup
Library    RequestsLibrary
Resource    ../../keywords/common.robot
Resource    ../../resources/api_url.resource
Resource    ../../resources/test_user.resource
Resource    ../../keywords/authentication.robot
Resource    ../../keywords/clean_up.robot


*** Variables ***
#your variables

*** Test Cases ***
Test Register API - New User with Basic
    [Documentation]    Tests the successful registration of a new user with basic valid information.
    [Tags]    register-01  smoke-test
    No Operation
    ${response}    Register    ${user1}[email]  ${user1}[password]
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${user1}[email]
    Should Be Equal    ${result}[role]  SUBMITTER

Test Register API - Empty Body
    [Documentation]    Verifies the system behavior when attempting to register with an empty request body.
    ...    Checks if the system handles this scenario appropriately.
    [Tags]    register-02
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Test Register API - without Email
    [Documentation]    Tests the behavior of the registration process when the email field is missing from the request body.
    ...    Checks if the system detects the absence of the email field and provides an appropriate response.
    [Tags]    register-03
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary  password=${user1}[password]
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Test Register API - without Password
    [Documentation]    Verifies the system's response when attempting to register without providing a password.
    ...    Tests if the system correctly identifies the missing password and handles it accordingly.
    [Tags]    register-04
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary  email=${user1}[email]
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Test Register API - Content Type application/text
    [Documentation]    Tests the behavior of the registration process when the request has an incorrect content type,
    ...    specifically "application/text." Verifies if the system rejects the request with an appropriate response.
    [Tags]    register-05
    ${headers}=    Create Dictionary    Content-Type=application/text
    ${body}=    Create Dictionary    email=${user1}[email]  password=${user1}[password]
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=415
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  415
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[title]  Unsupported Media Type
    Should Be Equal    ${result}[detail]  Content-Type 'application/text' is not supported.

Test Register API - Invalid Email Format
    [Documentation]    Validates the system's response when attempting to register with an email address that doesn't
    ...    comply with the expected format. Tests if the system identifies the invalid format and handles it appropriately.
    [Tags]    register-06
    ${too_long_email}    Set Variable    Loremipsumdolorsitamet5consecteturadipiscingelit5seddoeiusmodtemporincididuntutlaboreetdoloremagnaaliqua.Utenimadminimveniam5quisnostrudexercitationullamcolaborisnisiutaliquipexeacommodoconsequat.Duisauteiruredolorinreprehenderitinvoluptatevelitessecillumdoloreeufugiatnullapariatur3Excepteursintoccaecatcupida@dqtri.com
    @{invalid_formar_emails}    Create List    invalidEmailFormat  invalidEmailFormat@  @invalidEmailFormat"  invalid@Email@Format  ${too_long_email}
    FOR    ${email}    IN    @{invalid_formar_emails}
    ${response}    Register    ${email}  ${user1}[password]  expected_status=400
    Should Be Bad Request    ${response}
    END

Test Register API - Invalid Password Format
    [Documentation]    Verifies the system behavior when registering with a password that doesn't meet the specified
    ...    format requirements. Checks if the system detects the invalid password format and responds accordingly.
    [Tags]    register-07
    @{invalid_format_passwords}    Create List    st  ''   '         '  too_long_password_that_more_than_24_characters
    FOR    ${password}    IN    @{invalid_format_passwords}
        ${response}    Register    ${user1}[email]  ${password}  expected_status=400
        Should Be Bad Request    ${response}
    END

Test Register API - Existed User Email
    [Documentation]    Tests the registration process when attempting to register with an email that is already associated
    ...    with an existing user account. Verifies if the system correctly detects the duplicate email and handles it appropriately.
    [Tags]    register-08
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary    email=${admin1}[email]  password=${user1}[password]
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=409
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  409
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[status]  CONFLICT
    Should Be Equal    ${result}[message]  ${admin1}[email] is already in use

*** Keywords ***
Suite Register API Setup
    ${response}    Login    ${admin1}[email]  ${admin1}[password]
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]
    Set Suite Variable    ${adminAccessToken}  Bearer ${response.json()}[accessToken]

Suite Register API Teardown
    Delete Test User    ${user1}[email]  ${adminAccessToken}
    Logout    ${adminRefreshToken}

Test Register API Setup
    ${response}    Refresh    ${adminRefreshToken}
    ${accessToken}    Set Variable    Bearer ${response.json()}[accessToken]
    Set Suite Variable    ${adminAccessToken}  ${accessToken}
