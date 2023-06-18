# Project Structure
## Root Folder

- _requirements.txt_ - Python dependencies or
- _pyproject.toml_ - Python dependencies
- _Readme.md_ - Project description 
- _.gitignore_ - Lists files and folders to be ignored by git

## Test Suites
**Test Suites** are located in the tests/ folder.
Depending on the project, the Test Suites can be organized in multiple .robot files and subfolders.

- tests/ - Test Suites folder
  - **authentication/** - Test Suite for Authentication and authorization on REST application
    - _register.robot_ - Test Suite for Register new user functionality
    - _login.robot_ - Test Suite for Log In functionality
  - **submission/** - 
    - create_submission.robot - Test Suite for create feature functionality
  - ...

## Resources
Reusable keywords are stored in .resource files in the resources folder.
Also Python keywords in .py files can be stored there.

- resources/ - Reusable keywords
  - common.robot - General Keywords (e.g. Login/Logout, Navigation, ...) are stored here
  - authentication.robot - Keywords for authentication are stored here
  - utils.py - Python helper keywords are stored here
  - ...

## Libraries
Custom Python Keyword libraries can be stored in a separate _libraries/_ folder, if needed.
Some projects like to seperate the _libraries/_ from the _resources/_.

Examples
Find some example project structures below.

### Simple Project with _tests/_ and _resources/_ and _data/_ folders:
A flat project structure for a simple project with a few test cases and keywords.

Test Suites are organized in subfolders in the _tests/_ folder. Keywords, variables and python libraries are organized in subfolders in the _resources/_ folder. Test Data files - like Python or Yaml Variable files - are organized in subfolders in the _data/_ folder.

The project root folder contains the _.gitlab-ci.yml_ file for _CI/CD_, the .gitignore file for git, the _README.md_ file for documentation and the _requirements.txt_ file for dependencies.

```
RoboTest
├── tests
│   ├── authentication
│   │   ├── register.robot
│   │   ├── login.robot
│   │   
│   ├── submission
│   │   ├── submission_searching.robot
│   │   ├── submission_creating.robot
│   │   ├── submission_updating.robot
│   │   
│   ├── ...
│   
├── resources
│   ├── common.resource
│   ├── general.resource
│   ├── some_other.resource
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
│   ├── submission
│   │   ├── submission_creation.yaml
│   │   ├── submission_processing.yaml
│   │   ├── ...
│   │   
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
robot --pythonpath . tests
```

To run the tests, we need to tell Robot Framework where to search for the libraries, resource and variable files. If we run it without any arguments, we will get an error:
```commandline
$ robot tests/authentication/login.robot
[Error] Error in file 'tests/suiteA.robot': ...
Resource file 'resources/general.resource' does not exist.
...
```
We can add the project root folder my_project/ folder to the --pythonpath command line argument:
```commandline
robot --pythonpath . tests/authentication/login.robot
```

**TIP** 

You can also add the resources/ , lib/ or keyword/ folders to the --pythonpath setting:
```commandline
robot --pythonpath .:./lib:./resources:./keywords tests/authentication/login.robot
```