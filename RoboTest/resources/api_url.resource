*** Settings ***
Documentation    Common api urls resourse of server

*** Variables ***
${LOCAL_API_URL}    http://localhost:8152/safeguard/api
${BASE_API_URL}=    %{BASE_API_URL=${LOCAL_API_URL}}

#Authentication urls
${LOGIN_API_URL}    ${BASE_API_URL}/auth/login
${REFRESH_API_URL}    ${BASE_API_URL}/auth/refresh
${LOGOUT_API_URL}    ${BASE_API_URL}/auth/logout
${REGISTER_API_URL}    ${BASE_API_URL}/auth/register

#Clean up urls
${DELETE_USER_API_URL}    ${BASE_API_URL}/cleanup/users
${LOGIN_ATTEMPT_API_URL}    ${BASE_API_URL}/cleanup/login-attempt
${DELETE_SUBMISSION_API_URL}    ${BASE_API_URL}/cleanup/submissions

#feature urls
${USER_API_URL}    ${BASE_API_URL}/users
${SUBMISSION_API_URL}    ${BASE_API_URL}/submissions
