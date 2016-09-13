angular
    .module('userApp', [
        'ngRoute',
        'ngAnimate',
        'ui.bootstrap',
        'ngCookies'
    ])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '../views/login.html',
                controller: 'loginController'
            })
            .when('/changePassword', {
                templateUrl: '../views/changePassword.html',
                controller: 'passwordController'
            })
            .when('/register', {
                templateUrl: '../views/register.html'
            })
            .when('/approvals', {
                templateUrl: '../views/underConstruction.html'
            })
            .when('/about', {
                templateUrl: '../views/underConstruction.html'
            })
            .when('/contact', {
                templateUrl: '../views/underConstruction.html'
            })
            .otherwise({
                templateUrl: '../views/404.html'
            });
    });


angular.module('userApp')
    .controller('mainController',
    function ($scope, $interval, MainFactory, LoginFactory, Factory, $timeout, $cookieStore, $uibModal) {
        $scope.pingErrMessage = "";
        $scope.showPingErrMessage = false;
        $scope.pingMessage = "";
        $scope.showPingMessage = false;
        $scope.loggedInObj = {};
        $scope.loggedInObj.isLoggedIn = false;
        $scope.loggedInEmp = {};
        $scope.userName = "";
        $scope.loggedInUser = {};

        init();

        function init() {
            var username = $cookieStore.get("Username");
            var token = $cookieStore.get("token");
            $scope.loggedInObj.username = $cookieStore.get('Username');
            if (username === undefined || username == "" || token === undefined || token == "") {
                $scope.loggedInObj = {};
                $scope.loggedInObj.isLoggedIn = false;
            }
            else {
                var username = $cookieStore.get("Username");
                $scope.userName = username;
                $scope.loggedInObj = {};
                $scope.loggedInObj.username = $cookieStore.get('Username');
                $scope.loggedInObj.isLoggedIn = true;
            }
        }

        function ping() {
            MainFactory.ping().success(function (data) {
                $scope.showPingMessage = false;
                if ($scope.showPingErrMessage == true) {
                    $scope.showPingErrMessage = false;
                    $scope.pingMessage = "We are back again. Please proceed with your operations.";
                    $scope.showPingMessage = true;
                }

            }).error(function (data, status, headers, config) {
                $scope.showPingMessage = false;
                $scope.pingErrMessage = "Server or Network is down. Please hold on while we are trying to contact Server again.";
                $scope.showPingErrMessage = true;
            });
        }

        $scope.populateUser = function () {
            Factory.getEmployee().success(function (data) {
                $scope.loggedInUser = angular.copy(data);
            });
        }

        $scope.logout = function () {
            var userName = $cookieStore.get("Username");
            var user = {};
            user.username = userName;
            LoginFactory.logout(user).success(function (data) {
                if (data.status == "SUCCESS") {
                    $scope.loginFailed = true;
                    $scope.loginMessage = "You are logged out !"
                    $cookieStore.remove("Username");
                    $cookieStore.remove("token");
                    window.location = "/";
                    $scope.loggedInObj.isLoggedIn = false;
                }
            });
        }

        $interval(ping, 10000);

        $scope.openEmployeeWindow = function (size) {

            var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'employeeprofile.html',
                controller: 'EmployeeProfileCtrl',
                size: size
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {

            });
        };

    })
    .factory('MainFactory', function ($http, $cookieStore) {
        return{
            ping: function () {
                return $http.get('/inventory/logon/dummy');
            }
        }
    })
;

angular.module('userApp').controller('EmployeeProfileCtrl', function ($scope, $uibModalInstance, Factory) {

    $scope.message = "";
    $scope.showMessage = false;
    $scope.showEdit = true;
    $scope.showDone = false;
    $scope.user = {};
    $scope.employee = {};
    $scope.mobileErr = false;

    init();

    function init() {
        Factory.getEmployee().success(function (data) {
            $scope.user = angular.copy(data);

        });
    }

    $scope.editEmployee = function () {
        $scope.showEdit = false;
        $scope.showDone = true;
    }

    $scope.doneEmployee = function () {
        if($scope.employee.mobile.length != 10){
            $scope.mobileErr = true;
            $scope.employee = {};
            return;
        }
        Factory.updateEmployee($scope.employee).success(function(data){
            $scope.showEdit = true;
            $scope.showDone = false;
            init();
        });
    }

    $scope.cancleEdit = function () {
        $scope.showEdit = true;
        $scope.showDone = false;
    }
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});

function resolveError(status) {
    if (status == -1) {
        return "Our Serve is not working right now. Please try again after some time."
    }
    if (status == 500) {
        return "Something went wrong at Server Side."
    }
}

angular.module('userApp')
    .service('FileUpload', ['$http', function ($http) {
        this.uploadFileToUrl = function (file, uploadUrl) {
            var fd = new FormData();
            fd.append('file', file);
            var url = "/inventory/common"+uploadUrl;
            $http.post(url, fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })

                .success(function () {
                })

                .error(function () {
                });
        }
    }]);