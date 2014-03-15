white-bear
==========

This a simple Java wrapper API to connect to HP ALM using REST service

Features
========

Create Defect
Read Defect(s)
Update Defect Fields


Usage
=====

Create an instance of Authenticator class to authenticate your session

Authenticator auth = new Authenticator("http://alm-url","port","alm_domain","alm_project");

login

auth.login("alm_username","alm_password");

Use DefectUtils class to access defects

DefectUtils du = new DefectUtils(auth);
du.updateDefect("defectId","fields_to_update_map");

logout

auth.logout();



