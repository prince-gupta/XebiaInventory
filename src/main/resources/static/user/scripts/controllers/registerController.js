angular.module('userApp')
    .controller('registerController',
    function ($scope, Factory, $uibModal, $cookieStore, $timeout) {
        $scope.animationsEnabled = true;
        $scope.approvalsRequired = false;
        $scope.assets = [];
        $scope.isResultOK = false;
        $scope.showSearch = false;
        $scope.canAssetFetched = true;
        $scope.alerts = [];
        $scope.assetsHistory = [];
        $scope.items = ['item1', 'item2', 'item3'];
        $scope.model = {
            name: 'Tabs'
        };
        $scope.showAssetTable = false;

        $scope.showHistoryTable = false;

        $scope.noResult = false;

        init();

        function init() {
            fetchAll();
        }

        $scope.refreshAssets = function () {
            fetchAll();
        }

        function resetMessages() {
            $scope.showDangerMessage = false;
            $scope.dangerMessage = "";

            $scope.showWarningMessage = false;
            $scope.warningMessage = "";

            $scope.showSuccessMessage = false;
            $scope.successMessage = "";

        }

        function showMessage(msg, type) {
            resetMessages();
            if (type == "SUCCESS") {
                $scope.showSuccessMessage = true;
                $scope.successMessage = msg;
            }

            if (type == "WARNING") {
                $scope.showWarningMessage = true;
                $scope.warningMessage = msg;
            }

            if (type == "DANGER") {
                $scope.showDangerMessage = true;
                $scope.dangerMessage = msg;
            }

            $timeout(function () {
                $scope.showDangerMessage = false;
                $scope.showWarningMessage = false;
                $scope.showSuccessMessage = false;
            }, 5000);
        }


        $scope.closeDangerMsg = function () {
            $scope.showDangerMessage = false;
            $scope.dangerMessage = "";
        }

        $scope.closeWarningMsg = function () {
            $scope.showWarningMessage = false;
            $scope.warningMessage = "";
        }

        $scope.closeSuccessMsg = function () {
            $scope.showSuccessMessage = false;
            $scope.SuccessMessage = "";
        }

        function fetchAll() {
            waitingDialog.show("Please wait while data is loading ...");
            Factory.getAssets().success(function (data) {
                $scope.assets = angular.copy(data);
                if (data === undefined || data == "" || data.length == 0) {
                    $scope.showAssetTable = false;
                }
                else {
                    $scope.showAssetTable = true;
                }
                waitingDialog.hide();
            }).error(function (data, status, headers, config) {
                if (status == 401) {
                    window.location = ""
                }
                else {
                    showMessage(resolveError(status), "DANGER");
                }
                waitingDialog.hide();
            });
        }

        $scope.fetchHistory = function () {
            waitingDialog.show("Please wait while data is loading ...");
            Factory.getAssetHistory().success(function (data) {
                $scope.assetsHistory = angular.copy(data);
                if (data === undefined || data == "" || data.length == 0)
                    $scope.showHistoryTable = true;
                else
                    $scope.showHistoryTable = true;
                waitingDialog.hide();
            })
                .error(function (data, status, headers, config) {
                    if (status == 401) {
                        window.location = ""
                    }
                    else {
                        showMessage(resolveError(status), "DANGER");
                    }
                    waitingDialog.hide();
                });
        }
    }
).factory('Factory', function ($http, $cookieStore) {

        return{
            getEmployee: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/employee/fetchEmployeeDetails');
            },
            getAssets: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/asset/getAssets');
            },
            getAssetHistory: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/asset/getAssetsHistoryLoggedIn');
            }
        }
    })

;