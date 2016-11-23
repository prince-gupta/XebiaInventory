angular.module('app')
    .controller('activityController',
    function ($scope, $uibModal, ActivityFactory, $timeout) {

        $scope.usersList = [];
        $scope.actions = [];
        $scope.search = {};
        $scope.userDisplayList = [];
        $scope.actionDisplayList = [];
        $scope.activities = [];

        $scope.dummyUser = {
            userName: 'Select User',
            id: -1
        };
        $scope.dummyAction = "Select Action";

        $scope.activityPage = {};
        $scope.activityPage.maxSize = 30;
        $scope.activityPage.totalItems = 0;
        $scope.activityPage.currentPage = 1;
        $scope.activityPage.itemsPerPage = 20;

        $scope.showResultTable = false;
        $scope.isLoading = false;
        $scope.calFromPopup = {
            opened: false
        };
        $scope.calToPopup = {
            opened: false
        };


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

        init();

        function getOffset() {
            return ((($scope.activityPage.currentPage - 1) * $scope.activityPage.itemsPerPage));
        }

        function getLimit() {
            return ($scope.activityPage.itemsPerPage);
        }

        $scope.pageChanged = function () {
            fetch();
        }

        function init() {
            ActivityFactory.getOptions().success(function (data) {
                $scope.usersList = angular.copy(data.data.users);
                $scope.userDisplayList[0] = ($scope.dummyUser);
                $scope.search.user = ($scope.dummyUser.id);
                for (var index = 0; index < $scope.usersList.length; index++) {
                    $scope.userDisplayList.push($scope.usersList[index]);
                }

                $scope.actions = angular.copy(data.data.actions);
                $scope.actionDisplayList[0] = ($scope.dummyAction);
                $scope.search.action = ($scope.dummyAction);
                for (var index = 0; index < $scope.actions.length; index++) {
                    $scope.actionDisplayList.push($scope.actions[index]);
                }
            })
                .error(function (data, status, headers, config) {
                    if (status == 401) {
                        window.location = ""
                    }
                    else {
                        showMessage(resolveError(status), "DANGER");
                    }
                });
            ;
            fetch();
        }

        $scope.searchActivities = function () {
            fetch();
        }

        $scope.resetSearch = function () {
            $scope.search.user = $scope.dummyUser.id;
            $scope.search.action = ($scope.dummyAction);
            $scope.search.from = null;
            $scope.search.to = null;
            fetch();
        }
        function fetch() {
            $scope.isLoading = true;
            $scope.search.offset = getOffset();
            $scope.search.limit = getLimit();
            ActivityFactory.fetchActivities($scope.search).success(function (data) {
                $scope.activityPage.totalItems = angular.copy(data.data.count);
                $scope.activities = angular.copy(data.data.result);
                if ($scope.activities.length > 0) {
                    $scope.showResultTable = true;
                    showMessage($scope.activityPage.totalItems + " activities has been found.", "SUCCESS");
                }
                else {
                    showMessage("It seems no activity yet registered in system with selected search criteria", "WARNING");
                    $scope.showResultTable = false;
                }
                $scope.isLoading = false;
            });
        }

        $scope.openCalFrom = function () {
            $scope.calFromPopup.opened = true;

        };

        $scope.openCalTo = function () {
            $scope.calToPopup.opened = true;

        };

    })

    .factory("ActivityFactory", function ($http, $cookieStore) {
        return {
            getOptions: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/activities/getOptionsForActivityPage');
            },
            fetchActivities: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/activities', data);
            }
        }
    });
