*** Settings ***
Documentation    Common keywords for the project

*** Keywords ***
Should Be Bad Request
    [Arguments]  ${response}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  400
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[title]  Bad Request
    Should Be Equal    ${result}[detail]  Invalid request content.

Should Be Access Denied
    [Arguments]  ${response}
    ${status_code}    Convert To String    ${response.status_code}
    Should Be Equal    ${status_code}  403
    ${result}    Set Variable    ${response.json()}
    Should Be Equal    ${result}[status]  FORBIDDEN
    Should Be Equal    ${result}[message]  Access Denied