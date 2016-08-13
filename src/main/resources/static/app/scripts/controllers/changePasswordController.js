angular.module("app")
    .controller('passwordController',
    function ($scope, $cookieStore, LoginFactory) {

        $scope.pwdJson = {};
        $scope.resultMessage = "";
        $scope.error = false;
        $scope.confirmPassword="";

        init();

        function init(){
            var token = $cookieStore.get('token');
            var uName = $cookieStore.get('Username');
            if((token == null || (token === undefined)) || (uName == null || (uName === undefined))){
                window.location = "";
            }
        }

        $scope.changePassword = function() {
            $scope.resultMessage = "";
            $scope.error = false;
            if ($scope.confirmPassword != $scope.pwdJson.password) {
                $scope.error = true;
                $scope.resultMessage = "Both Provided Passwords are not same";
            }
            else {
                $scope.pwdJson.username = $cookieStore.get("Username");
                $scope.pwdJson.token = $cookieStore.get("token");
                LoginFactory.changePassword($scope.pwdJson).success(function (data) {
                    if (data.status == "SUCCESS") {
                        $scope.resultMessage = "Password Has Been Changed Successfully.Please login again.";
                    }
                    else if (data.status == "FAILURE") {
                        $scope.resultMessage = "Authentication Exception.If you have changes you password recently. Try to login with changed password.";
                    }
                });
            }
        }

    });