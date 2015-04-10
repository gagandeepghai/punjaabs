#!/bin/bash 
 
MMDDHHmmSS=`/bin/date +%m%d%H%M%S` 
 
REQUEST='<?xml version="1.0" encoding="UTF-8"?>
<AddUserRequest>
   <UserId>gagandeep</UserId>
   <password>password</password>
   <FirstName>gagandeep</FirstName>
   <LastName>Ghai</LastName>
</AddUserRequest>'
 
URL=http://localhost:8080/RequestProcessingServer-0.0.1/v1/user/addUser
 
echo curl -v -H "Content-Type: application/xml" -k -d "$REQUEST" $URL 
curl -v -H "Content-Type: application/xml" -k -d "$REQUEST" $URL -L 

echo
