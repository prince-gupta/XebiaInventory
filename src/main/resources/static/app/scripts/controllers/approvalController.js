angular.module('app')
    .controller('approvalController',
    function ($scope, AssetFactory, $uibModal) {

        $scope.approvals = [];
        $scope.status = [
            {
                id: "APPROVED",
                name: "Approved"
            },
            {
                id: "NOT_APPROVED",
                name: "Not Approved"
            },
            {
                id: "ATTENTION",
                name: "Attention"
            }
        ];
        $scope.dummyStatus = {
            id: "NA",
            name: "Status"
        }
        $scope.uapproval = {};
        $scope.remarks = "";
        $scope.statusArray = [];
        $scope.searchBoxErr = false;
        $scope.search = {};
        $scope.animationsEnabled = true;
        $scope.isApprovedOnly = {};
        $scope.disableAllButtons = false;

        init();

        function fetchApprovals() {
            $scope.isApprovedOnly.status = false;
            AssetFactory.getAllApprovals().success(function (data) {
                $scope.approvals = angular.copy(data);
                for (var index = 0; index < $scope.approvals.length; index++) {
                    $scope.statusArray.push($scope.status);
                }
                $scope.totalItems = $scope.approvals.length;
            })
                .error(function (data, status, headers, config) {
                    if (status == 401) {
                        window.location = ""
                    }
                    else {
                    }
                    waitingDialog.hide();
                });
        }

        function init() {
            fetchApprovals();
        }

        $scope.toggleSearchErr = function () {
            $scope.searchBoxErr = false;
        }

        $scope.performSearch = function () {
            $scope.searchBoxErr = false;
            $scope.isApprovedOnly.status = false;
            $scope.search.showApproved = false;
            if ($scope.search.incidentId === undefined || $scope.search.incidentId.trim() == "") {
                $scope.searchBoxErr = true;
                return true;
            }
            AssetFactory.searchApprovals($scope.search).success(function (data) {
                $scope.approvals = angular.copy(data);
            });

        }
        $scope.refresh = function () {
            fetchApprovals();
            resetFields();
        }

        function resetFields() {
            for (var i = 0; i < $scope.approvals.length; i++) {
                $("#editRemark" + i).show();
                $("#textArea" + i).hide();
            }
        }

        $scope.edit = function (index) {
            resetFields();
            $("#editRemark" + index).hide();
            $("#textArea" + index).show();
        }

        $scope.fetchApprovedOnly = function () {
            $scope.search.incidentId = "";
            $scope.search.showApproved = $scope.isApprovedOnly.status;
            if ($scope.isApprovedOnly.status == false) {
                fetchApprovals();
            }
            else {
                AssetFactory.searchApprovals($scope.search).success(function (data) {
                    $scope.approvals = angular.copy(data);
                });
            }
        }

        $scope.update = function (index, id) {
            $scope.disableAllButtons = true;
            if (isRemarksSuggested(index)) {
                openSuggestionWindow();
                $scope.disableAllButtons = false;
            }
            else {
                $scope.uapproval.incidentId = id;
                $scope.uapproval.status = $scope.statusArray[index];
                AssetFactory.updateApproval($scope.uapproval).success(function (data) {
                    fetchApprovals();
                    $scope.disableAllButtons = false;
                });
            }
        }

        function openSuggestionWindow() {

            var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'Suggestion.html',
                size: 'sm'

            });
        }

        function isRemarksSuggested(index) {
            if ($scope.statusArray[index] == "NOT_APPROVED" || $scope.statusArray[index] == "ATTENTION") {
                if ($scope.uapproval.remark === undefined || $scope.uapproval.remark == "")
                    return true;
            }
            return false;
        }
    });
