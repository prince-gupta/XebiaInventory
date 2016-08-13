angular.module('app')
    .controller('userController',
    function ($scope, UserFactory, $uibModal) {


        $scope.userList = [];
        $scope.roleTabDisabled = true;
        $scope.selectedUsers = [];
        $scope.user = {};
        initUsers();
        function initUsers() {
            fetchAllUser();
        }

        $scope.initialize = function () {
            $('#undo_redo').multiselect();
        }
        function fetchAllUser() {
            waitingDialog.show("Please wait while sending data . . .");
            UserFactory.getUsers().success(function (data) {
                $scope.userList = angular.copy(data);
            })
                .error(function (data, status, headers, config) {
                    if(status == 401){
                        window.location = ""
                    }
                    else {
                        showMessage(resolveError(status), "DANGER");
                    }
                });
            waitingDialog.hide();
        }

        $scope.fetchUsers = function () {
            fetchAllUser();
        }

        $scope.selectedUser;

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
            4
            $scope.warningMessage = "";
        }

        $scope.closeSuccessMsg = function () {
            $scope.showSuccessMessage = false;
            $scope.SuccessMessage = "";
        }

        $scope.createUser = function () {
            waitingDialog.show("Please wait while sending data . . .");
            UserFactory.createUser($scope.user).success(function (data) {
                var result = angular.copy(data);
                if (result.status == "FAILURE") {
                    if (result.data.eMessage == "UN_Error") {
                        showMessage("Username is already taken by some other employee.Try something else.", "WARNING");
                    }
                    else if (result.data.eMessage == "EC_Error") {
                        showMessage("Employee with mentioned Employee Code is not present in System. Try to save Employee first.", "WARNING");
                    }
                    else if (result.data.eMessage == "EC_PRESENT_ERROR") {
                        showMessage("Username for mentioned Employee Code is already present in System as " + result.data.eAddInfo, "WARNING");
                    }
                }
                else if (result.status == "SUCCESS") {
                    showMessage("User created with username : " + data.data.user.username + " with default password : " + data.data.user.password, "SUCCESS");
                    fetchAllUser();
                }
            }).error(function (data, status, headers, config) {
                if(status == 401){
                    window.location = ""
                }
                else {
                    showMessage(resolveError(status), "DANGER");
                }
            });
            waitingDialog.hide();
        }

        $scope.selectUser = function (id, index) {
            $scope.selectedUser = "";
            if ($("#selectedUser" + index).prop("checked") == true) {
                $scope.selectedUser = id;
                $scope.selectedUsers[id] = "SELECTED";
                var count = 0;
                for(var index = 0; index < $scope.selectedUsers.length; index ++){
                    if($scope.selectedUsers[index] == "SELECTED"){
                        count++;
                    }
                }
                if (count == 1) {
                    $scope.roleTabDisabled = false;
                }
                else{
                    $scope.roleTabDisabled = true;
                }
            }
            if ($("#selectedUser" + index).prop("checked") == false) {
                var tempArr = [];
                for (var index = 0; index < $scope.selectedUsers.length; index++) {
                    if(index != id)
                        tempArr[index] = $scope.selectedUsers[index];
                }
                var count = 0;
                $scope.selectedUsers = tempArr;
                for(var index = 0; index < $scope.selectedUsers.length; index ++){
                    if($scope.selectedUsers[index] == "SELECTED"){
                        count++;
                    }
                }
                if (count == 1) {
                    $scope.roleTabDisabled = false;
                }
                else{
                    $scope.roleTabDisabled = true;
                }
            }
        }

        $scope.resetPassword = function(username){
            var user={};
            user.username = username;
            UserFactory.resetPassword(user).success(function(data){
                showMessage("Password Reset Successfully. User need to login again.", "SUCCESS");
            }).error(function (data, status, headers, config) {
                if(status == 401){
                    window.location = ""
                }
                else {
                    showMessage(resolveError(status), "DANGER");
                }
            });
        }

    }).factory('UserFactory', function ($http, $cookieStore) {
        return{
            getUsers: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/user/fetchAllUsers');
            },
            createUser: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/user/createUser', data);
            },
            resetPassword: function(data){
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/user/resetPassword', data);
            }
        }
    });