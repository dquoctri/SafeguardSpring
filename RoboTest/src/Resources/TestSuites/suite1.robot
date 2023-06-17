*** Settings ***
Suite Setup    Set Test Suite Name  Hello Wourld

*** Test Cases ***
Example Test Case
    Log    Running Test Case in Suite: ${TEST SUITE NAME}    INFO
    ...

Example Test Case2
    Log    Running Test Case2 in Suite: ${TEST SUITE NAME}    INFO
    ...

*** Keywords ***
Set Test Suite Name
    [Arguments]    ${name}
    Set Suite Variable    ${TEST SUITE NAME}    ${name}