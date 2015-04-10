#!/bin/bash

MMDDHHmmSS=`/bin/date +%m%d%H%M%S`

REQUEST='<?xml version="1.0" encoding="UTF-8"?><ReservationRequest><UserName>Astha</UserName><UserPhone>3522261826</UserPhone><ReservationType>DINNER</ReservationType><ReservationTime>1730</ReservationTime><UserEmail>acb@gmail.com</UserEmail><NumberOfPeople>4</NumberOfPeople></ReservationRequest>'
#REQUEST='<?xml version="1.0" encoding="UTF-8"?><ReservationRequest><UserName>Astha</UserName><UserPhone>76767567676</UserPhone><ReservationType>DINNER</ReservationType><ReservationTime>8787</ReservationTime><UserEmail>ghlnbufchv</UserEmail><NumberOfPeople>8</NumberOfPeople></ReservationRequest>'

URL=http://localhost:8080/punjaab_server-1.0.0/v1/reservation/enterReservation
#URL=http://76.126.191.77:8080/punjaab_server-1.0.0/v1/reservation/enterReservation

echo curl -v -H "Content-Type: application/xml" -k -d "$REQUEST" $URL
curl -v -H "Content-Type: application/xml" -k -d "$REQUEST" $URL -L

echo
