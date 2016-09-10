/**
 * Created by Pgupta on 02-08-2016.
 */
angular.module('app')
    .controller('loginController',
    function ($scope, $cookieStore, LoginFactory, $uibModal, MainFactory) {

        $scope.data = {};
        $scope.loginFailed = false;
        $scope.loginMessage = "Invalid Credentials";
        $scope.user = {};
        $scope.emptyUserName = false;
        $scope.emptyPwd = false;
        init();


        function init() {
            var userName = $cookieStore.get("Username");
            if (userName != "" && !(userName === undefined)) {
                $scope.user.username = $cookieStore.get("Username");
                LoginFactory.logout($scope.user).success(function (data) {
                    if (data.status == "SUCCESS") {
                        $scope.loginFailed = true;
                        $cookieStore.remove("Username");
                        $cookieStore.remove("token");
                        $scope.loggedInObj.isLoggedIn = false;
                    }
                });
            }

        }

        $scope.reset = function () {
            $scope.emptyUserName = false;
            $scope.emptyPwd = false;
        }
        $scope.login = function () {
            $scope.loginFailed = false;
            if ($scope.data.username == "" || $scope.data.username === undefined) {
                $scope.emptyUserName = true;
            }

            else if ($scope.data.password == "" || $scope.data.password === undefined) {
                $scope.emptyPwd = true;
            }
            else {
                LoginFactory.login($scope.data).success(function (data) {
                    if (data.message == 'SUCCESS') {
                        $cookieStore.put('token', data.token);
                        $cookieStore.put('Username', data.userName);
                        if (data.changePasswordRequired == true) {
                            window.location = "/app/views/main.html#/changePassword";
                        }
                        else {
                            window.location = "/app/views/main.html#/dashboard";
                        }
                    }
                    else if (data.token == "INVALID-ROLE") {
                        $scope.loginFailed = true;
                        $scope.loginMessage = "You don't have permissions to access admin portal. Try to login to employee portal."
                    }
                    else {
                        $scope.loginFailed = true;
                        $scope.loginMessage = "Invalid Username or Password !"
                    }
                }).error(function (data, status, headers, config) {
                    alert("Login Failed");
                });
            }

        }

    }).factory('LoginFactory', function ($http, $cookieStore) {
        return{
            login: function (data) {
                return $http.post('/inventory/logon/generateToken', data);
            },
            changePassword: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/logon/changePassword', data);
            },
            logout: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/logon/logout', data);
            }
        }
    });