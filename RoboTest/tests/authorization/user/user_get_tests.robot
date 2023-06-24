*** Settings ***
Documentation     This test suite contains test cases to verify the user ... functionality.
Test Tags    v1.0.0  user  user_creation
Library     RequestsLibrary
Resource    ../../../resources/common.robot
Resource    ../../../resources/api_url.resource
Resource    ../../../resources/test_user.resource
Resource    ../../../keywords/authentication.robot
Resource    ../../../keywords/user.robot

*** Variables ***
${SERVER_URL}    http://localhost:8152/safeguard/api
#${SERVER_URL}    %{BASE_API_URL}

*** Test Cases ***


*** Keywords ***