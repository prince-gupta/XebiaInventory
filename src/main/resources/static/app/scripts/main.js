angular
    .module('app', [
        'ngRoute',
        'ngAnimate',
        'ui.bootstrap',
        'ngCookies',
        'toggle-switch'
    ])

   /* .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('securityInterceptor');
    }])
    .factory('securityInterceptor', function ($q) {
        return {
            response: function (response) {
                // do something on success
                if (response.status === 403 || response.status === 401) {
                    $location.path('');
                    return $q.reject(response);
                }
                return response;

            },
            responseError: function (response) {
                // do something on error
                return $q.reject(response);
            }
        };
    })*/.config(function ($routeProvider) {
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
                templateUrl: '../views/register.html',
                controller: 'registerController',
            })
            .when('/assets', {
                templateUrl: '../views/assets.html',
                controller: 'assetController'
            })
            .when('/approvals', {
                templateUrl: '../views/approvals.html',
                controller: 'approvalController'
            })
            .when('/about', {
                templateUrl: '../views/underConstruction.html'
            })
            .when('/contact', {
                templateUrl: '../views/underConstruction.html'
            })
            .when('/dashboard', {
                templateUrl: '../views/dashboard.html',
                controller: 'dashboardController'
            })
            .when('/user', {
                templateUrl: '../views/user.html',
                controller: 'userController'
            })
            .when('/monitor', {
                templateUrl: '../views/monitor.html',
                controller: 'monitorController'
            })
            .when('/excelMapping', {
                templateUrl: '../views/excelMapping.html',
                controller: 'xlsMappingController'
            })
            .when('/pageMapping', {
                templateUrl: '../views/pageMapping.html',
                controller: 'pageRoleController'
            })
            .when('/stats', {
                templateUrl: '../views/stats.html',
                controller: 'statsController'
            })
            .when('/activity', {
                templateUrl: '../views/activity.html',
                controller: 'activityController'
            })
            .otherwise({
                templateUrl: '../views/404.html'
            });
    });

function openSideBar() {
    $("#menu-toggle").hide();
    $("#menu-close").show();
    $("#sidebar-wrapper").toggleClass("active");
}
function closeSideBar() {
    $("#menu-toggle").show();
    $("#menu-close").hide();
    $("#sidebar-wrapper").toggleClass("active");
}
function resolveError(status) {
    if (status == -1) {
        return "Our Serve is not working right now. Please try again after some time."
    }
    if (status == 500) {
        return "Something went wrong at Server Side."
    }
}

angular.module('app')
    .controller('mainController',
    function ($scope, $interval, $location, MainFactory, ExcelFactory, LoginFactory, $timeout, $cookieStore, AssetFactory) {
        $scope.pingErrMessage = "";
        $scope.showPingErrMessage = false;
        $scope.pingMessage = "";
        $scope.showPingMessage = false;
        $scope.approvalsBadgeCount = "";

        init();

        function init(){
            badgeCount();
        }
        function badgeCount(){
            AssetFactory.getApprovalsBadgeCount().success(function(data){
                $scope.approvalsBadgeCount = data;
            });
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
                }
            });
        }

        $interval(ping, 10000);
        $interval(badgeCount, 20000);

        $scope.exportToExcel = function (tableId, sheetName, fileName) { // ex: '#my-table'
            $scope.exportHref = Excel.tableToExcel("#" + tableId, sheetName);
            $timeout(function () {
                var link = document.createElement('a');
                link.download = fileName + ".xls";
                link.href = $scope.exportHref;
                link.click();
            }, 100); // trigger download
        }


    })
    .factory('MainFactory', function ($http, $cookieStore) {
        return{
            ping: function () {
                return $http.get('/inventory/logon/dummy');
            },
            getUserRoles : function(){
            $http.defaults.headers.common.Username = $cookieStore.get('Username');
            return $http.get('/inventory/common/getUserRoles');
        }
        }
    })
    .factory('ExcelFactory', function ($window, $http, $cookieStore) {
        var uri = 'data:application/vnd.ms-excel;base64,',
            template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>',
            base64 = function (s) {
                return $window.btoa(unescape(encodeURIComponent(s)));
            },
            format = function (s, c) {
                return s.replace(/{(\w+)}/g, function (m, p) {
                    return c[p];
                })
            };
        return {
            tableToExcel: function (tableId, worksheetName) {
                var table = $(tableId),
                    ctx = {worksheet: worksheetName, table: table.html()},
                    href = uri + base64(format(template, ctx));
                return href;
            },
            getExcelMappings: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/common/getExcelMappings');
            },
            updateExcelMappings: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/common/updateExcelMappings', data);
            }
        };
    })
;