/**
 * Created by Pgupta on 02-08-2016.
 */
angular.module('userApp')
    .controller('loginController',
    function ($scope, $cookieStore, LoginFactory, $uibModal) {

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
                        $cookieStore.remove("Username");
                        $cookieStore.remove("token");
                        $scope.loggedInObj.isLoggedIn = true;
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
                        $scope.loggedInObj.isLoggedIn = true;
                        $scope.loggedInObj.username = $cookieStore.get('Username');
                        if (data.changePasswordRequired == true) {
                            window.location = "/user/views/main.html#/changePassword";
                        }
                        else {
                            window.location = "/user/views/main.html#/register";
                        }
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
                return $http.post('/inventory/logon/generateEmployeeToken', data);
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