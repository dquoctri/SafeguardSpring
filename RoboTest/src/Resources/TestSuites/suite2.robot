*** Settings ***
Documentation    Example suite
Suite Setup      Set Test Suite Name    ${MESSAGE}
Suite Teardown    Log    Suite A Teardown
Test Setup    Log    Test Setup
Test Teardown    Log    Test Teardown
Test Tags        example

*** Variables ***
${MESSAGE}       Hello, world!

*** Test Cases ***
Example Test Case
    Log    Running Test Case in Suite: ${TEST SUITE NAME}    INFO
    ...

*** Keywords ***
Set Test Suite Name
    [Arguments]    ${name}
    Set Suite Variable    ${TEST SUITE NAME}    ${name}