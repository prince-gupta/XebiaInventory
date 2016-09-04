angular.module('app')
    .controller('userController',
    function ($scope, UserFactory, $uibModal) {


        $scope.userList = [];
        $scope.roleTabDisabled = false;
        $scope.selectedUsers = [];
        $scope.user = {};
        $scope.roles = [];
        $scope.rolesSelected = {};
        $scope.userRoles = [];
        $scope.allRoles = {};
        $scope.selectedUser = {};
        initUsers();
        function initUsers() {
            fetchAllUser();
        }

        $scope.initialize = function () {
            $('#undo_redo').multiselect();
            var allRoles = [];
            $scope.roles = [];
            $scope.userRoles = [];
            UserFactory.getRoles().success(function (data) {
                allRoles = angular.copy(data.data.list);
                UserFactory.getUserRoles($scope.selectedUser.id).success(function (data) {
                    $scope.userRoles = angular.copy(data.data.list);
                    if ($scope.userRoles.length > 0) {
                        for (var index = 0; index < allRoles.length; index++) {
                            var matched = false;
                            for (var inner_index = 0; inner_index < $scope.userRoles.length; inner_index++) {
                                if (allRoles[index].id == $scope.userRoles[inner_index].id) {
                                    matched = true;
                                }
                            }
                            if(matched == false){
                                $scope.roles.push(allRoles[index]);
                            }
                        }
                    }
                    else {
                        $scope.roles = angular.copy(allRoles);
                    }
                });

            });

        }

        $scope.refreshUsers = function(){
            fetchAllUser();
        }
        function fetchAllUser() {
            waitingDialog.show("Please wait while sending data . . .");
            UserFactory.getUsers().success(function (data) {
                $scope.userList = angular.copy(data.data.list);
                if($scope.userList === undefined || $scope.userList == "" || $scope.userList.length == 0){
                      $scope.roleTabDisabled = true;
                }
                else {
                    $scope.selectedUser.id = $scope.userList[0].id;
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
            waitingDialog.hide();
        }

        $scope.moveToRight = function () {
            var selectedRoles = angular.copy($scope.allRoles);
            if(selectedRoles === undefined || selectedRoles.role === undefined || selectedRoles == "" || selectedRoles.length ==0)
                return true;
            var tempRole = angular.copy($scope.roles);
            for (var index = 0; index < tempRole.length; index++) {
                for (var inner_index = 0; inner_index < selectedRoles.role.length; inner_index++) {
                    if (tempRole[index].id == selectedRoles.role[inner_index]) {
                        $scope.userRoles.push($scope.roles[index]);
                    }
                }
            }

            for(var index = 0 ; index < $scope.userRoles.length ; index ++){
                for(var inner_index = 0 ; inner_index < $scope.roles.length; inner_index ++){
                    if($scope.userRoles[index].id == $scope.roles[inner_index].id){
                        $scope.roles.splice(inner_index,1);
                    }
                }
            }
        }

        $scope.moveToLeft = function () {
            var selectedRoles = angular.copy($scope.rolesSelected);
            if(selectedRoles === undefined  || selectedRoles.role === undefined || selectedRoles == "" || selectedRoles.length ==0)
                return true;
            var tempUserRoles = angular.copy($scope.userRoles);
            for (var index = 0; index < tempUserRoles.length; index++) {
                for (var inner_index = 0; inner_index < selectedRoles.role.length; inner_index++) {
                    if (tempUserRoles[index].id == selectedRoles.role[inner_index]) {
                        $scope.roles.push($scope.userRoles[index]);
                    }
                }
            }

            for(var index = 0 ; index < $scope.roles.length ; index ++){
                for(var inner_index = 0 ; inner_index < $scope.userRoles.length; inner_index ++){
                    if($scope.roles[index].id == $scope.userRoles[inner_index].id){
                        $scope.userRoles.splice(inner_index,1);
                    }
                }
            }
        }

        $scope.moveAllToLeft = function () {
            var tempUserRoles = angular.copy($scope.userRoles);

            for (var index = 0; index < tempUserRoles.length; index++) {
                $scope.roles.push($scope.userRoles[0]);
                $scope.userRoles.splice(0, 1);
            }
        }

        $scope.moveAllToRight = function () {
            var tempRoles = angular.copy($scope.roles);
            for (var index = 0; index < tempRoles.length; index++) {
                $scope.userRoles.push($scope.roles[0]);
                $scope.roles.splice(0, 1);
            }
        }

        $scope.updateUserRoles = function(){
            var user = {};
            user.id = $scope.selectedUser.id;
            var roleIds = [];
            for(var index = 0 ; index < $scope.userRoles.length; index++){
                roleIds.push($scope.userRoles[index].id);
            }

            user.roleIds = roleIds;
            $scope.closeSuccessMsg();
            UserFactory.updateUserRole(user).success(function(data){
                showMessage("Roles has been updated for selected User.","SUCCESS");
            })
        }

        $scope.fetchUsers = function () {
            fetchAllUser();
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
                if (status == 401) {
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
                for (var temp_index = 0; temp_index < $scope.selectedUsers.length; temp_index++) {
                    if ($scope.selectedUsers[temp_index] == "SELECTED") {
                        count++;
                    }
                }
                if (count == 1) {
                    $scope.roleTabDisabled = false;
                }
                else {
                    $scope.roleTabDisabled = true;
                }
            }
            if ($("#selectedUser" + index).prop("checked") == false) {
                var tempArr = [];
                for (var temp_index_1 = 0; temp_index_1 < $scope.selectedUsers.length; temp_index_1++) {
                    if (temp_index_1 != id)
                        tempArr[temp_index_1] = $scope.selectedUsers[temp_index_1];
                }
                var count = 0;
                $scope.selectedUsers = tempArr;
                for (var temp_index_2 = 0; temp_index_2 < $scope.selectedUsers.length; temp_index_2++) {
                    if ($scope.selectedUsers[temp_index_2] == "SELECTED") {
                        count++;
                    }
                }
                if (count == 1) {
                    $scope.roleTabDisabled = false;
                }
                else {
                    $scope.roleTabDisabled = true;
                }
            }
        }

        $scope.resetPassword = function (username) {
            var user = {};
            user.username = username;
            UserFactory.resetPassword(user).success(function (data) {
                showMessage("Password Reset Successfully. User need to login again.", "SUCCESS");
            }).error(function (data, status, headers, config) {
                if (status == 401) {
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
            resetPassword: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/user/resetPassword', data);
            },
            getRoles: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/user/getRoles');
            },
            getUserRoles: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/user/getUserRoles', data);
            },
            updateUserRole: function(data){
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/user/updateUserRoles',data);
            },
            getPageRoles: function(){
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/user/getPageRoles');
            },
            populatePageRoles: function(){
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/user/populatePageRoles');
            }
        }
    });