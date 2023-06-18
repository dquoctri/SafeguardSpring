*** Settings ***
Documentation       *RequestsLibrary* is a Robot Framework library aimed to provide HTTP api testing functionalities
...     by wrapping the well known *Python Requests Library*.
Metadata        Version            2.0
Metadata        Robot Framework    http://robotframework.org
Metadata        Requests Library    https://docs.robotframework.org/docs/different_libraries/requests
Metadata        Platform           ${PLATFORM}
Library               RequestsLibrary

*** Test Cases ***

Quick Get Request Test
    ${response}=    GET  https://www.google.com

Quick Get Request With Parameters Test
    ${response}=    GET  https://www.google.com/search  params=query=ciao  expected_status=200

Quick Get A JSON Body Test
    ${response}=    GET  https://jsonplaceholder.typicode.com/posts/1
    Should Be Equal As Strings    1  ${response.json()}[id]