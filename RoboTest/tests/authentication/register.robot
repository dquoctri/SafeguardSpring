*** Settings ***
Documentation     This test suite contains test cases to verify the registration functionality.
...               It covers different scenarios related to user registration.
Test Tags  v1.0.0  authentication  register
Library    RequestsLibrary
Resource    ../../resources/common.robot
Resource    ../../resources/authentication.robot
Suite Setup    Authenticate as Admin
Suite Teardown    Clean up and logout

*** Variables ***
${SERVER_URL}    http://localhost:8152/safeguard/api
#${SERVER_URL}    %{BASE_API_URL}
${REGISTER_API_URL}    ${SERVER_URL}/auth/register
${DELETE_USER_API_URL}    ${SERVER_URL}/cleanup/users
${admin_email}    admin1@dqtri.com
${admin_password}    admin1
${register_email}    test1@dqtri.com
${register_password}    test1

*** Test Cases ***
Register New User with Basic
    [Documentation]    Tests the successful registration of a new user with basic valid information.
    [Tags]    register-01  smoke-test
    No Operation
    ${response}    Register new user    ${register_email}  ${register_password}
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  200
    ${result}    Set Variable    ${response.json()}
    Should Be True    ${result}[id] > 0
    Should Be Equal    ${result}[email]  ${register_email}
    Should Be Equal    ${result}[role]  SUBMITTER

Register with Empty Body
    [Documentation]    Verifies the system behavior when attempting to register with an empty request body.
    ...    Checks if the system handles this scenario appropriately.
    [Tags]    register-02
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Register without Email
    [Documentation]    Tests the behavior of the registration process when the email field is missing from the request body.
    ...    Checks if the system detects the absence of the email field and provides an appropriate response.
    [Tags]    register-03
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary  password=${register_password}
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Register without Password
    [Documentation]    Verifies the system's response when attempting to register without providing a password.
    ...    Tests if the system correctly identifies the missing password and handles it accordingly.
    [Tags]    register-04
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary  email=${register_email}
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=400
    Should Be Bad Request    ${response}

Register with application/text Content Type
    [Documentation]    Tests the behavior of the registration process when the request has an incorrect content type,
    ...    specifically "application/text." Verifies if the system rejects the request with an appropriate response.
    [Tags]    register-05
    ${headers}=    Create Dictionary    Content-Type=application/text
    ${body}=    Create Dictionary    email=${register_email}  password=${register_password}
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=415
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  415
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[title]  Unsupported Media Type
    Should Be Equal    ${result}[detail]  Content-Type 'application/text' is not supported.

Register with Invalid Email Format
    [Documentation]    Validates the system's response when attempting to register with an email address that doesn't
    ...    comply with the expected format. Tests if the system identifies the invalid format and handles it appropriately.
    [Tags]    register-06
    ${too_long_email}    Set Variable    Loremipsumdolorsitamet5consecteturadipiscingelit5seddoeiusmodtemporincididuntutlaboreetdoloremagnaaliqua.Utenimadminimveniam5quisnostrudexercitationullamcolaborisnisiutaliquipexeacommodoconsequat.Duisauteiruredolorinreprehenderitinvoluptatevelitessecillumdoloreeufugiatnullapariatur3Excepteursintoccaecatcupida@dqtri.com
    @{invalid_formar_emails}    Create List    invalidEmailFormat  invalidEmailFormat@  @invalidEmailFormat"  invalid@Email@Format  ${too_long_email}
    FOR    ${email}    IN    @{invalid_formar_emails}
    ${response}    Register new user    ${email}  ${register_password}  expected_status=400
    Should Be Bad Request    ${response}
    END

Register with Invalid Password Format
    [Documentation]    Verifies the system behavior when registering with a password that doesn't meet the specified
    ...    format requirements. Checks if the system detects the invalid password format and responds accordingly.
    [Tags]    register-07
    @{invalid_formar_passwords}    Create List    st  ''   '         '  too_long_password_that_more_than_24_characters
    FOR    ${password}    IN    @{invalid_formar_passwords}
        ${response}    Register new user    ${register_email}  ${password}  expected_status=400
        Should Be Bad Request    ${response}
    END

Register with Existed User Email
    [Documentation]    Tests the registration process when attempting to register with an email that is already associated
    ...    with an existing user account. Verifies if the system correctly detects the duplicate email and handles it appropriately.
    [Tags]    register-08
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary    email=${admin_email}  password=${register_password}
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=409
    #VALIDATIONS
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  409
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[status]  CONFLICT
    Should Be Equal    ${result}[message]  ${admin_email} is already in use

*** Keywords ***
Authenticate as Admin
    ${response}    Login    ${admin_email}  ${admin_password}
    Set Suite Variable    ${adminRefreshToken}  Bearer ${response.json()}[refreshToken]

Clean up and logout
    #Refresh an access token for a suite that spent times more than 5 minutes
    ${response}    Refresh    ${adminRefreshToken}
    Delete User    ${register_email}  Bearer ${response.json()}[accessToken]
    Logout  ${adminRefreshToken}

Register new user
    [Arguments]    ${email}  ${password}  ${expected_status}=200
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary    email=${email}    password=${password}
    ${response}=  POST  url=${REGISTER_API_URL}  json=${body}  headers=${headers}  expected_status=${expected_status}
    RETURN    ${response}

Delete User
    [Arguments]  ${email}    ${adminToken}
    ${headers}    Create Dictionary    Authorization=${adminToken}
    DELETE    url=${DELETE_USER_API_URL}/${email}  headers=${headers}