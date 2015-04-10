#!/bin/bash 
 
MMDDHHmmSS=`/bin/date +%m%d%H%M%S` 
 
REQUEST='<?xml version="1.0" encoding="UTF-8"?>
<GetUserRequest>
   <UserId>asthakhatri</UserId>
</GetUserRequest>'
 
URL=http://localhost:8080/RequestProcessingServer-0.0.1/v1/user/getUser
 
echo curl -v -H "Content-Type: application/xml" -k -d "$REQUEST" $URL 
curl -v -H "Content-Type: application/xml" -k -d "$REQUEST" $URL -L 

echo
