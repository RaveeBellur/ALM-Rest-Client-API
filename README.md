whitebear
==========

A simple Java wrapper API to connect to HP ALM using REST service

Features
========

Create Defect

Read Defect(s)

Update Defect Fields


Usage
=====

1. Create an instance of Authenticator class to authenticate your session

        Authenticator auth = new Authenticator("alm-host","port","alm_domain","alm_project");

2. Login

        auth.login("alm_username","alm_password");

3. Use DefectUtils class to access defects

        DefectUtils du = new DefectUtils(auth);
        du.updateDefect("defectId","fields_to_update_map");

4. Logout

        auth.logout();
        
===================        
Happy Automation !!
===================



