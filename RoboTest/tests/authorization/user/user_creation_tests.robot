*** Settings ***
Documentation     This test suite contains test cases to verify the user ... functionality.
Test Tags    v1.0.0  user  user_creation
Library     RequestsLibrary
Resource    ../../../resources/common.robot
Resource    ../../../resources/authentication.robot

*** Variables ***
${SERVER_URL}    http://localhost:8152/safeguard/api
#${SERVER_URL}    %{BASE_API_URL}

*** Test Cases ***


*** Keywords ***