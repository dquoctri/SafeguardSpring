# Project Structure
## Root Folder

- _requirements.txt_ - Python dependencies or
- _README.md_ - Project description 
- _.gitignore_ - Lists files and folders to be ignored by git

## Test Suites
**Test Suites** are located in the tests/ folder.
Depending on the project, the Test Suites can be organized in multiple .robot files and subfolders.

- tests/ - Test Suites folder
  - **authentication/** - Test Suite for Authentication and authorization on REST application
    - _register_tests.robot_ - Test Suite for Register new user functionality
    - _login_tests.robot_ - Test Suite for Log In functionality
  - **authorization/** - 
    - user_apis_tests.robot - Test Suite for authorization functionality on feature
    - submission_apis_tests.robot - Test Suite for authorization functionality on feature
  - ...

## Resources
Reusable variables are stored in .resource or .py files in the resources folder.
Also Python keywords in .py files can be stored there.

- resources/ - Reusable variables
  - api_url.robot - Variables for REST uri are stored here
  - test_user.robot - Variables for authentication are stored here
  - utils.py - Python helper keywords are stored here
  - ...

## Keywords
Reusable keywords are stored in .robot files in the keywords folder.
Also Python keywords in .py files can be stored there.
- keywords/ - Reusable Keywords
  - common.robot - General Keywords are stored here
  - authentication.robot - Keywords for authentication (e.g. Login/Logout, ...) are stored here
  - user.robot - Keywords for user REST apis
  - submission.robot - Keywords for submission REST apis
  - ...

## Libraries
Custom Python Keyword libraries can be stored in a separate _libraries/_ folder, if needed.
Some projects like to seperate the _libraries/_ from the _resources/_.

### Examples
Find some example project structures below.

### Simple Project with _tests/_ and _resources/_ and _data/_ folders:
A flat project structure for a simple project with a few test cases and keywords.

Test Suites are organized in subfolders in the _tests/_ folder. Keywords, variables and python libraries
are organized in subfolders in the _resources/_ folder. Test Data files - like Python or Yaml Variable 
files - are organized in subfolders in the _data/_ folder.

The project root folder contains the _.gitlab-ci.yml_ file for _CI/CD_, the .gitignore file for git, 
the _README.md_ file for documentation and the _requirements.txt_ file for dependencies.

```
RoboTest
├── tests
│   ├── authentication
│   │   ├── register_tests.robot
│   │   ├── login_tests.robot
│   │   ├── ...
│   │   
│   ├── authorization
│   │   ├── user_apis_tests.robot
│   │   ├── submission_apis_tests.robot
│   │   ├── ...
│   │   
│   ├── features
│   │   ├── featureA_tests.robot
│   │   ├── featureB_tests.robot
│   │   ├── ...
│   │   
│   ├── ...
│   
├── resources
│   ├── common.robot
│   ├── general.resource
│   ├── test_user.resource
│   ├── master-data
│   │   ├── login.resource
│   │   ├── users.resource
│   │   ├── ...
│   │   
│   ├── custom_library.py
│   ├── variables.py
│   ├── utils.py
│   ├── ...
│
├── data
│   ├── master-data
│   │   ├── users.py
│   │   ├── ...
│   │
│   ├── user.yaml
│   ├── submission.yaml
│   ├── ...
│
├── .gitlab-ci.yml
├── .gitignore
├── README.md
├── requirements.txt
```
The tests/submission/submission_searching.robot file looks like this:

```robotframework
*** Settings ***
Resource  resources/common.resource
Resource  resources/some_other.resource
Resource  resources/master-data/customers.resource
Library   resources/utils.py
Variables data/master-data/users.py
...
```

Tests can be run with the following command (assuming you are in the project root folder RoboTest):
```commandline
robot --include API -d ./results --variable BASE_API_URL:http://localhost:8152/safeguard/api --pythonpath . --timestampoutputs tests
```

## How to write configuration file requirements.txt
```text
###### Requirements without Version Specifiers ######
nose
nose-cov
beautifulsoup4

###### Requirements with Version Specifiers ######
docopt == 0.6.1             # Version Matching. Must be version 0.6.1
keyring >= 4.1.1            # Minimum version 4.1.1
coverage != 3.5             # Version Exclusion. Anything except version 3.5
Mopidy-Dirble ~= 1.1        # Compatible release. Same as >= 1.1, == 1.*
```
- Use the command below to install the packages according to the configuration file requirements.txt
```commandline
pip install -r requirements.txt
```
- To auto generate the current installed packages
```commandline
pip freeze -r requirements.txt
```

## Here are some commonly used library document links for testing REST APIs in Robot Framework:

## Robot Framework

[Robot Framework](https://robotframework.org/) is a generic open source automation framework. 
It can be used for test automation and robotic process automation (RPA).


## Robot Framework User Guide
### Version 6.1

Robot Framework is a Python-based, extensible keyword-driven automation framework for acceptance testing,
acceptance test driven development (ATDD), behavior driven development (BDD) and robotic process 
automation (RPA). It can be used in distributed, heterogeneous environments, where automation requires 
using different technologies and interfaces.

## BuiltIn
An always available standard library with often needed keywords.

[BuiltIn](https://robotframework.org/robotframework/latest/libraries/BuiltIn.html) is Robot Framework's standard library
that provides a set of generic keywords needed often. It is imported automatically and thus always available. 
The provided keywords can be used, for example, for verifications (e.g. [Should Be Equal](https://robotframework.org/robotframework/latest/libraries/BuiltIn.html#Should%20Be%20Equal),
[Should Contain](https://robotframework.org/robotframework/latest/libraries/BuiltIn.html#Should%20Contain)), conversions 
(e.g. [Convert To Integer](https://robotframework.org/robotframework/latest/libraries/BuiltIn.html#Convert%20To%20Integer))
and for various other purposes (e.g. [Log](https://robotframework.org/robotframework/latest/libraries/BuiltIn.html#Log),
[Sleep](https://robotframework.org/robotframework/latest/libraries/BuiltIn.html#Sleep),
[Run Keyword If](https://robotframework.org/robotframework/latest/libraries/BuiltIn.html#Run%20Keyword%20If),
[Set Global Variable](https://robotframework.org/robotframework/latest/libraries/BuiltIn.html#Set%20Global%20Variable)).


## RequestsLibrary
[RequestsLibrary](https://docs.robotframework.org/docs/different_libraries/requests) is a Robot Framework library aimed to provide HTTP api testing functionalities by wrapping the well known Python Requests Library.


```text
pip install robotframework-requests
```
### Restful Booker
See examples for [Restful Booker](https://docs.robotframework.org/docs/examples/restfulbooker)