angular.module('app')
    .controller('pageRoleController',
    function ($scope, UserFactory, $uibModal) {


        $scope.pageRoles = [];

        $scope.page = {};
        $scope.page.maxSize = 4;
        $scope.page.totalItems = 0;
        $scope.page.currentPage = 1;
        $scope.page.itemsPerPage = 10;

        init();
        function getOffset() {
            return ((($scope.page.currentPage - 1) * $scope.page.itemsPerPage));
        }

        function getLimit() {
            return ($scope.page.itemsPerPage);
        }

        function init() {
            fetchAllPageRoles(getOffset(), getLimit());
        }

        function fetchAllPageRoles(offset, limit) {
            UserFactory.getPageRolesCount().success(function (data) {
                $scope.page.totalItems = data;
                UserFactory.getPageRoles(offset, limit).success(function (data) {
                    $scope.pageRoles = angular.copy(data.data.list);
                })
                    .error(function (data, status, headers, config) {
                        if (status == 401) {
                            window.location = ""
                        }
                        else {
                        }
                        waitingDialog.hide();
                    });
            });

        }

        $scope.pageChanged = function () {
            init();
        }

        $scope.hidePopulateBtn = false;
        $scope.populateRoles = function () {
            $scope.hidePopulateBtn = false;
            waitingDialog.show("Please wait while application is parsing xml and populating data . . .")
            UserFactory.populatePageRoles().success(function (data) {
                if (data.status != "SUCCESS") {
                    showMessage(data.error.eMessage, "DANGER")
                }
                else {
                    showMessage("Data has been populated successfully.", "SUCCESS");
                    init();
                }
                waitingDialog.hide();
            }).error(function (data, status, headers, config) {
                if (status == 401) {
                    showMessage("It seems this user don't have permission to update Page Roles.", "WARNING");
                    $scope.hidePopulateBtn = true;
                }
                else {
                    showMessage(resolveError(status), "DANGER");
                }
                waitingDialog.hide();
            });
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

    });