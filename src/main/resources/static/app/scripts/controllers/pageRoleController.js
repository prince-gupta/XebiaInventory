angular.module('app')
    .controller('pageRoleController',
    function ($scope, UserFactory, $uibModal) {


        $scope.pageRoles = [];

        init();
        function init() {
            fetchAllPageRoles();
        }

        function fetchAllPageRoles(){
            UserFactory.getPageRoles().success(function(data){
                $scope.pageRoles = angular.copy(data);
            });
        }

        $scope.populateRoles = function(){
            waitingDialog.show("Please wait while application is parsing xml and populating data . . .")
            UserFactory.populatePageRoles().success(function(data){
               if(data.status != "SUCCESS"){
                   showMessage(data.error.eMessage,"DANGER")
               }
                else{
                   showMessage("Data has been populated successfully.","SUCCESS");
                   fetchAllPageRoles();
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