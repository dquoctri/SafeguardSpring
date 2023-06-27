*** Settings ***
Documentation    Common api urls resourse of server

*** Variables ***
${admin1_email}    admin1@dqtri.com
${admin1_password}    admin1
&{admin1}    email=admin1@dqtri.com  password=admin1  role=ADMIN
&{manager1}    email=manager1@dqtri.com  password=manager1  role=MANAGER
&{manager2}    email=manager2@dqtri.com  password=manager2  role=MANAGER
&{specialist1}    email=specialist1@dqtri.com  password=specialist1  role=SPECIALIST
&{specialist2}    email=specialist2@dqtri.com  password=specialist2  role=SPECIALIST
&{submitter1}    email=submitter1@dqtri.com  password=submitter1  role=SUBMITTER
&{submitter2}    email=submitter2@dqtri.com  password=submitter2  role=SUBMITTER
&{none1}    email=none1@dqtri.com  password=none1  role=NONE
&{user1}    email=test1@dqtri.com  password=test1  role=SUBMITTER
&{user2}    email=test2@dqtri.com  password=test2  role=SUBMITTER