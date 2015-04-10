(function(angular) {
'use strict';
angular.module('punjaab', [])
.controller('PunjaabController', ['$scope', '$http',
function($scope, $http) {
    $scope.home = true;
    $scope.menu = false;
    $scope.reservation = false;
    $scope.contact = false;
    $scope.reserveUrl = 'http://76.126.191.77:8080/punjaab_server-1.0.0/v1/reservation/enterReservation';
    //$scope.reserveUrl = 'http://192.168.0.10:8080/punjaab_server-1.0.0/v1/reservation/enterReservation';
    $scope.planReservation = false;
    $scope.lunchTimes = false;
    $scope.dinnerTimes = false;
    $scope.showSummary = false;
    $scope.reservationInputFlow=false;

    $scope.method='POST';
    $scope.reservationType = null;
    $scope.reservationHour = null;
    $scope.reservationMinute = null;
    $scope.reservationSuccess=false;
    $scope.disableDinner = false;
    $scope.disableLunch = false;

    $scope.menuMap = new Map();
    $scope.menuItemsToDisplay=null;

    $scope.setMenu = function(str){
        $scope.menuItemsToDisplay = $scope.menuMap.get(str);
    }

    $scope.next = function(){
        if($scope.checkEmailAndPhone() == false){
                $scope.setSecondAlert=true;
                $scope.alertMessage = "Please enter email and phone to continue."
                return;
        }
        $scope.setSecondAlert=false;
        $scope.reservationInputFlow=false;
        $scope.showSummary = true;

        $scope.reservationSummaryLine1 = "Hello " +$scope.userName + ". Creating reservation for " + $scope.reservationType + " at " +$scope.reservationHour + ":" + $scope.reservationMinute + ". Party size: " +$scope.numberOfGuests;
        $scope.reservationSummaryLine2 = "A  confirmation email will be sent to the following email address: " + $scope.userEmail;
        $scope.reservationInputFlow=false;
        $scope.showSummary = true;
    }

    $scope.modify = function(){
        $scope.showSummary = false;
        $scope.reservationView();
    }

    $scope.submit = function(){
       $scope.generateRequest();
       $scope.sendRequest();
    }

    $scope.checkEmailAndPhone = function(){
        if($scope.userPhone == null){
            return false;
        }
        if($scope.userEmail == null){
            return false;
        }
    }

    $scope.sendRequest=function(){
        var req = {
            method: $scope.method,
            url: $scope.reserveUrl,
            headers: {
                "Content-Type": "application/xml"
            },
            data: $scope.reserveRequest,
            timeout:'100000'
        };

        $http(req).
        success(function(data, status) {
            $scope.gotResponse=true;
            $scope.status = status;
            $scope.data = data;
            $scope.handleSuccessReserve();
        }).
        error(function(data, status) {
            $scope.gotResponse=true;
            $scope.data = data;
            $scope.status = status;
            $scope.handleFailureReserve();
        });
    }

    $scope.handleSuccessReserve=function(){
        $scope.showSummary = false;
        $scope.reservationSuccess=true;
        $scope.reservationFailure=false;
        $scope.successMessageCustom='Yipee! you are all set. We have sent a confirmation to ' + $scope.userEmail +'.';
        $scope.successMessageDefault = 'You are about to be pampered. We look forward to serving you.';
    }

    $scope.handleFailureReserve=function(){
        //$scope.data.message = "Could not find availability";
        $scope.showSummary = false;
        $scope.reservationSuccess=false;
        $scope.reservationFailure=true;
        $scope.failureMessageCustom='Alas! we were unable to book you today. ' +$scope.data.Message +'.';
        $scope.failureMessageDefault='Sorry for the inconvenience. For further assistance, call our number.'
    }

    $scope.generateRequest=function(){
        $scope.reserveRequest =
            '<?xml version="1.0" encoding="UTF-8"?>'
            +'<ReservationRequest>'
                +'<UserName>'+ $scope.userName + '</UserName>'
                +'<UserPhone>' + $scope.userPhone + '</UserPhone>'
                +'<ReservationType>' + $scope.reservationType + '</ReservationType>'
                +'<ReservationTime>' + $scope.reservationHour+$scope.reservationMinute + '</ReservationTime>'
                +'<UserEmail>' + $scope.userEmail + '</UserEmail>'
                +'<NumberOfPeople>' + $scope.numberOfGuests + '</NumberOfPeople>'
            +'</ReservationRequest>';
    }

    $scope.homeView=function(){
        $scope.home = true;
        $scope.menu = false;
        $scope.reservation = false;
        $scope.contact = false;
        $scope.gotResponse=false;
    };

    $scope.menuView=function(){
        $scope.home = false;
        $scope.menu = true;
        $scope.reservation = false;
        $scope.contact = false;
        $scope.gotResponse=false;
        $scope.setMenuCache();
        $scope.setMenu("soups");
    };

    $scope.reservationView=function(){
        $scope.reservationInputFlow=true;

        $scope.home = false;
        $scope.menu = false;
        $scope.reservation = true;
        $scope.contact = false;
        $scope.planReservation = true;
        $scope.planReservation2 = false;

        $scope.userEmail = null;
        $scope.numberOfGuests = null;
        $scope.userName = null;
        $scope.userPhone = null;

        $scope.reservationSuccess=false;
        $scope.reservationFailure=false;

        $scope.showSummary = false;
        $scope.disableLunch = false;
        $scope.disableDinner = false;
        $scope.lunchTimes = false;
        $scope.dinnerTimes = false;
        $scope.gotResponse=false;
    };

    $scope.contactView=function(){
        $scope.home = false;
        $scope.menu = false;
        $scope.reservation = false;
        $scope.gotResponse=false;
        $scope.contact = true;
    };

    $scope.setLunch=function(){

        if($scope.checkInput() == false){
            $scope.setAlert=true;
            $scope.alertMessage = "Please enter you name and party size.";
            return;
        }
        $scope.setAlert=false;
        $scope.reservationType = "LUNCH";
        $scope.enableTimes('0000');
        $scope.lunchTimes = true;
        $scope.dinnerTimes = false;
        $scope.disableLunch = true;
        $scope.disableDinner = false;
        $scope.showSummary = false;
    };

    $scope.setDinner=function(){
        if($scope.checkInput() == false){
            $scope.setAlert=true;
            $scope.alertMessage = "Please enter you name and party size.";
            return;
        }
        $scope.setAlert=false;
        $scope.reservationType = "DINNER";
        $scope.enableTimes('0000');
        $scope.lunchTimes = false;
        $scope.dinnerTimes = true;
        $scope.disableLunch = false;
        $scope.disableDinner = true;
        $scope.showSummary = false;
    };

    $scope.checkInput= function(){
        if($scope.userName == null){
            return false;
        }
        if($scope.numberOfGuests == null){
            return false;
        }
    }

    $scope.enableTimes=function (str){
        var times = [1145,1200,1215,1230,1245,1300,1315,1330,1345,1400,1700,1715,1730,1745,1800,1815,1830,1845,1900,2015,2030,2045];

        for (var i = 0; i < times.length; i++) {
            if(times[i]!=str){
                angular.element(document.getElementById(times[i])).prop('disabled',false);
            }else{
                angular.element(document.getElementById(times[i])).prop('disabled',true);
            }
        }
    }

    $scope.setReservationTime = function(str){
        $scope.reservationHour = str.substring(0,2);
        $scope.reservationMinute = str.substring(2,4);
        $scope.enableTimes(str);
        $scope.planReservation2 = true;
    }

    $scope.reset = function(){
       $scope.reservationSummary = null;
       $scope.reservationView();
    }

    $scope.setMenuCache = function(){
        $scope.setSoups();
        $scope.setStarters();
        $scope.setEntree();
        $scope.setBreads();
        $scope.setRice();
        $scope.setSides();
        $scope.setDessert();

    }

    $scope.setSoups = function (){
        var menuItem;
        //soups
        var soupsI = 0;
        var soups = [];
        menuItem = $scope.createMenuItem("Tomato soup", "Coriander flavored tangy blend of fresh tomato with garlic.", "6")
        soups[soupsI++] = menuItem;
        menuItem = $scope.createMenuItem('Sweet corn soup', 'Corn and carrots simmered in mild sweet creamy sauce.', '6');
        soups[soupsI++] = menuItem;
        menuItem = $scope.createMenuItem('Chicken Manchow soup', 'Indo-chinese flavoured Chicken soup with tangy rich flavour.', '6');
        soups[soupsI++] = menuItem;

        $scope.menuMap.set("soups", soups);
    }

    $scope.setStarters = function (){
        var menuItem;
        //soups
        var startersI = 0;
        var starters = [];
        menuItem = $scope.createMenuItem("Vegetable Samosa", "Triangle flaky pastry stuffed with peas and potatoes deep fried.", "6")
        starters[startersI++] = menuItem;
        menuItem = $scope.createMenuItem('Bharma Kumbh(Fried Mushrooms)', 'Mushrooms stuffed with dry fruits, and crispy fried.', '6');
        starters[startersI++] = menuItem;
        menuItem = $scope.createMenuItem('Bread platter', 'Small garlic, kashmiri and cheese naan platter.', '6');
        starters[startersI++] = menuItem;
        menuItem = $scope.createMenuItem("Tandoori Murg", "Chicken marinated in traditional spices and roasted.", "6")
        starters[startersI++] = menuItem;
        menuItem = $scope.createMenuItem('Murg Malai Kebab', 'Tender boneless chicken pieces marinated in cream and spices.', '6');
        starters[startersI++] = menuItem;
        menuItem = $scope.createMenuItem('Masaledaar Chaamp (rack chops)', 'Succulent tendered lamb chops cooked in clay oven.', '6');
        starters[startersI++] = menuItem;
        menuItem = $scope.createMenuItem('Ajwain aur Lahsun ki Machli (Fish tikka)', 'Tender boneless fish pieces peppered with carom seeds.', '6');
        starters[startersI++] = menuItem;
        menuItem = $scope.createMenuItem('Chick pea fritters (chane ki tikki 4 pcs)', 'Chick peas, potatoes mixed with spices for perfection.', '6');
        starters[startersI++] = menuItem;

        $scope.menuMap.set("starters", starters);
    }

    $scope.setEntree = function (){
        var menuItem;
        //soups
        var entreeI = 0;
        var entree = [];
        menuItem = $scope.createMenuItem("Makkhan wala Kukkad (Butter chicken)", "Chicken simmered in a rich and spicy butter tomato gravy.", "6")
        entree[entreeI++] = menuItem;
        menuItem = $scope.createMenuItem('Gosht Rogan Josh', 'Slow cooked tender Lamb in tomato onion gravy with aromatic spices.', '6');
        entree[entreeI++] = menuItem;
        menuItem = $scope.createMenuItem('Murg Tikka Masala', 'Tender boneless chicken pieces cooked in a rich & creamy gravy.', '6');
        entree[entreeI++] = menuItem;
        menuItem = $scope.createMenuItem("Lamb Madras", "Lamb cooked in a coconut milk gravy with ground spices.", "6")
        entree[entreeI++] = menuItem;
        menuItem = $scope.createMenuItem('Beef  Vindaloo', 'Beef and potatoes cooked in a hot red chilly gravy with herbs.', '6');
        entree[entreeI++] = menuItem;
        menuItem = $scope.createMenuItem('Beef Korma', 'Beef and potatoes cooked in a hot red chilly gravy with herbs.', '6');
        entree[entreeI++] = menuItem;
        menuItem = $scope.createMenuItem("Chatpata Jheenga Kadhai ka (Prawn masala)", "Fresh prawn cooked in whole spices and capsicum gravy.", "6")
        entree[entreeI++] = menuItem;
        menuItem = $scope.createMenuItem('Madrasi Machli (Fish curry)', 'Barramundi done in creamy coconut curry.', '6');
        entree[entreeI++] = menuItem;
        menuItem = $scope.createMenuItem('Kadhai Paneer', 'Paneer tossed in an Indian wok with onion, capsicum & tomato.', '6');
        entree[entreeI++] = menuItem;
        menuItem = $scope.createMenuItem('Palak Paneer', 'Paneer cooked in a spinach with onion tomato sauce.', '6');
        entree[entreeI++] = menuItem;
        $scope.menuMap.set("entree", entree);
    }

    $scope.setBreads = function (){
        var menuItem;
        //soups
        var breadI = 0;
        var breads = [];
        menuItem = $scope.createMenuItem("Naans", "Garlic, Plain, Butter.", "6")
        breads[breadI++] = menuItem;
        menuItem = $scope.createMenuItem('Stuffed  Naans', 'Potato kulcha, Kashmiri naan, Cheese naan.', '6');
        breads[breadI++] = menuItem;
        menuItem = $scope.createMenuItem('Wholemeal  breads', 'Tandoori roti, Parantha(Flaky bread).', '6');
        breads[breadI++] = menuItem;

        $scope.menuMap.set("breads", breads);
    }

    $scope.setRice = function (){
        var menuItem;
        //soups
        var riceI = 0;
        var rice = [];
        menuItem = $scope.createMenuItem("Steamed Rice", "Steamed plain basmati rice.", "6")
        rice[riceI++] = menuItem;
        menuItem = $scope.createMenuItem('Pea pulao', 'Rice tossed with cumin seeds, peas and touch of butter.', '6');
        rice[riceI++] = menuItem;
        menuItem = $scope.createMenuItem('Biryani ( Veg/Mutton/Chicken/Prawn)', 'Basmati rice slow cooked with spices and served with raita.', '6');
        rice[riceI++] = menuItem;

        $scope.menuMap.set("rice", rice);
    }

    $scope.setSides = function (){
        var menuItem;
        //soups
        var sidesI = 0;
        var sides = [];
        menuItem = $scope.createMenuItem("Raita", "Curd mixed with salad.", "6")
        sides[sidesI++] = menuItem;
        menuItem = $scope.createMenuItem('Fresh vegetable salad', 'Fresh vegetable salad.', '6');
        sides[sidesI++] = menuItem;
        menuItem = $scope.createMenuItem('Papadoms', 'Crispy crunchy spicy wafer.', '6');
        sides[sidesI++] = menuItem;
        menuItem = $scope.createMenuItem('Assortment of sides', 'Raita, kchumber, papadoms(2pcs) and pickle.', '6');
        sides[sidesI++] = menuItem;
        $scope.menuMap.set("sides", sides);
    }

    $scope.setDessert = function (){
        var menuItem;
        //soups
        var dessertI = 0;
        var dessert = [];
        menuItem = $scope.createMenuItem("Gulaab Jaamun", "Sweetened Khoya balls flavored with cardamom.", "6")
        dessert[dessertI++] = menuItem;
        menuItem = $scope.createMenuItem('Chocolate Kulfi', 'Home made indian ice‐cream  with nuts inside.', '6');
        dessert[dessertI++] = menuItem;
        menuItem = $scope.createMenuItem('Mango kulfi', 'Home made style mango ice‐cream.', '6');
        dessert[dessertI++] = menuItem;
        menuItem = $scope.createMenuItem('Choice of Ice Cream', 'Vanilla / strawberry.', '6');
        dessert[dessertI++] = menuItem;

        $scope.menuMap.set("desserts", dessert);
    }

    $scope.createMenuItem = function(menuName, menuFooter, menuPrice){
        var menuItem = {
            name: menuName,
            footer: menuFooter,
            price: menuPrice
        }

        return menuItem;
    }

}]);
})(window.angular);
