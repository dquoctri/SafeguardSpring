*** Settings ***
Documentation     This test suite contains test cases to verify the submission ... functionality.
Test Tags    v1.0.0  user  user_creation
Library     RequestsLibrary
Resource    ../../resources/common.robot
Resource    ../../keywords/authentication.robot

*** Variables ***
${SERVER_URL}    %{BASE_API_URL}

*** Test Cases ***


*** Keywords ***