angular.module('app')
    .controller('registerController',
    function ($scope, AssetFactory, EmployeeFactory, $uibModal, $cookieStore, $timeout) {
        $scope.animationsEnabled = true;
        $scope.approvalsRequired = false;
        $scope.assets = [];
        $scope.isResultOK = false;
        $scope.showSearch = false;
        $scope.canAssetFetched = true;
        $scope.alerts = [];
        $scope.items = ['item1', 'item2', 'item3'];
        $scope.model = {
            name: 'Tabs'
        };
        $scope.unAssignAsset = {};

        $scope.enableApprover = true;

        $scope.assetTabDisable = true;
        $scope.assetHistoryTabDisable = true;


        $scope.employeeList = [];

        $scope.printAssetDisabled = false;

        $scope.search = {};

        $scope.updateEmp = {};

        $scope.search = {
            approvalsRequired: 'NA'
        }

        $scope.showAssetTable = false;

        $scope.showHistoryTable = false;

        $scope.isValidForm = true;

        $scope.firstNameError = false;
        $scope.lastNameError = false;
        $scope.emailError = false;
        $scope.ecodeError = false;

        //Variables for Pagging.
        $scope.empPage = {};
        $scope.empPage.maxSize = 4;
        $scope.empPage.totalItems = 0;
        $scope.empPage.currentPage = 1;
        $scope.empPage.itemsPerPage = 10;

        $scope.enteredECode = {};

        init();

        function getOffset() {
            return ((($scope.empPage.currentPage - 1) * $scope.empPage.itemsPerPage));
        }

        function getLimit() {
            return ($scope.empPage.itemsPerPage);
        }

        function init() {
            fetchAll(getOffset(), getLimit());
        }

        $scope.pageChanged = function () {
            init();
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

        $scope.clearForm = function () {
            $scope.search = {};
            $scope.search.approvalsRequired = 'NA';
            $scope.assets = [];
            $scope.assetTabDisable = true;
            $scope.assetHistoryTabDisable = true;
            resetMessages();
            $scope.isValidForm = true;

            $scope.firstNameError = false;
            $scope.lastNameError = false;
            $scope.emailError = false;
            $scope.ecodeError = false;

        }

        $scope.ecodeResult = "";
        $scope.showErrECodeResult = false;
        $scope.isECodeAvailable = function(){
            if(!(($scope.search.ecode=== undefined) || ($scope.search.ecode.trim == ""))){
                EmployeeFactory.isECodeAvailable($scope.search).success(function(data){
                   if(data.status == 'FAILURE'){
                       $scope.ecodeResult = "Code is already assigned to other employee.";
                       $scope.showErrECodeResult = true;
                       $scope.search.ecode = "";
                   }
                    else{
                       $scope.ecodeResult = "";
                       $scope.showErrECodeResult = false;
                   }
                });
            }
        }

        function fetchAll(offset, limit) {
            $scope.isResultOK = false;
            EmployeeFactory.getTotalCount().success(function (data) {
                $scope.empPage.totalItems = data;
                EmployeeFactory.getEmployeeList(offset, limit).success(function (data) {
                    $scope.employeeList = angular.copy(data.data.list);
                    console.log($scope.employeeList);
                    $scope.isResultOK = true;
                    if ($scope.employeeList.length == 0) {
                        showMessage(" No Records Found.", "WARNING")
                    } else if ($scope.employeeList.length > 1) {
                        $scope.isResultOK = true;
                        $scope.canAssetFetched = false;
                        $scope.assetTabDisable = true;
                        $scope.assetHistoryTabDisable = true;
                        showMessage($scope.empPage.totalItems + " Employee(s) Found. But no tabs are enabled. To Enable them restrict your search to one result.", "SUCCESS");
                    }
                    else {
                        if ($scope.employeeList.length == 1) {
                            $scope.isResultOK = true;
                            $scope.employeeId = $scope.employeeList[0].id;
                            AssetFactory.getAssetListByEmployee($scope.employeeList[0].id).success(function (data) {
                                $scope.assets = angular.copy(data);
                            });
                            $scope.assetTabDisable = false;
                            $scope.assetHistoryTabDisable = false;
                            showMessage(" Asset & Asset History Tabs is enable now.", "SUCCESS");
                        }

                    }
                })
            }).error(function (data, status, headers, config) {
                if (status == 401) {
                    window.location = ""
                }
                else {
                    showMessage(resolveError(status), "DANGER");
                }
            });
        }

        $scope.refreshAssets = function () {
            if ($scope.assetTabDisable == false) {
                refreshAssetsOfEmployee();
            }
        }

        function refreshAssetsOfEmployee() {
            AssetFactory.getAssetListByEmployee($scope.employeeList[0].id).success(function (data) {
                $scope.assets = angular.copy(data);
                showMessage($scope.assets.length + " Asset(s) Found.", "SUCCESS");
                if (data.length == 0) {
                    $scope.showAssetTable = false;
                    $scope.printAssetDisabled = true;
                }
                else {
                    $scope.showAssetTable = true;
                    $scope.printAssetDisabled = false;
                }
            }).error(function (data, status, headers, config) {
                showMessage(resolveError(status), "DANGER");
            });
        }

        $scope.printEmployeeAssets = function () {
            var req = new XMLHttpRequest();
            var fileName = $scope.employeeList[0].firstName + $scope.employeeList[0].lastName;
            req.open("POST", "/inventory/asset/getEmployeeAssetsFileParam", true);
            req.setRequestHeader("Authorization", $cookieStore.get('token'));
            req.setRequestHeader("Username", $cookieStore.get('Username'));
            req.responseType = "blob";

            req.onload = function (event) {
                var blob = req.response;
                console.log(blob.size);
                var link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = fileName + ".pdf";
                link.click();
            };

            req.send($scope.employeeList[0].id);

        }

        $scope.employeeId = "";

        $scope.fetch = function () {
            /*  var $myModal = $('.custom-modal');
             $myModal.fadeIn();
             */
            $scope.isValidForm = true;

            $scope.firstNameError = false;
            $scope.lastNameError = false;
            $scope.emailError = false;
            $scope.ecodeError = false;
            waitingDialog.show("Please wait while data is loading . . .");
            $scope.assets = [];
            $scope.isResultOK = false;
            //        $scope.parseApprovalCheckBox();
            $scope.empPage.currentPage = 1;
            $scope.search.offset = getOffset();
            $scope.search.limit = getLimit();
            $scope.ecodeResult = "";
            $scope.showErrECodeResult = false;
            EmployeeFactory.fetchEmployee($scope.search).success(function (data) {
                $scope.ecodeResult = "";
                $scope.showErrECodeResult = false;
                $scope.empPage.totalItems = angular.copy(data.data.count);
                $scope.employeeList = angular.copy(data.data.result);
                console.log($scope.employeeList);

                $scope.search = {
                    approvalsRequired: 'NA'
                }
                if ($scope.employeeList.length == 0) {
                    showMessage(" No Records Found.", "WARNING")
                } else if ($scope.employeeList.length > 1) {
                    $scope.isResultOK = true;
                    $scope.canAssetFetched = false;
                    $scope.assetTabDisable = true;
                    $scope.assetHistoryTabDisable = true;
                    showMessage(data.data.count + " Employee(s) Found. But no tabs are enabled. To Enable them restrict your search to one result.", "SUCCESS");
                }
                else {
                    if ($scope.employeeList.length == 1) {
                        $scope.isResultOK = true;
                        $scope.employeeId = $scope.employeeList[0].id;
                        $scope.enableApprover = $scope.employeeList[0].approvalsRequired;
                        AssetFactory.getAssetListByEmployee($scope.employeeList[0].id).success(function (data) {
                            $scope.assets = angular.copy(data);
                        });
                        $scope.assetTabDisable = false;
                        $scope.assetHistoryTabDisable = false;
                        showMessage(" Asset & Asset History Tabs is enable now.", "SUCCESS");
                    }

                }
                //  $myModal.fadeOut();
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

        $scope.returnAsset = function (assetId) {
            $scope.unAssignAsset.assetId = assetId;
            $scope.unAssignAsset.employee = $scope.employeeId;
            AssetFactory.unAssignAsset($scope.unAssignAsset).success(function (data) {
                showMessage("Asset Un-Assigned Successfully.", "SUCCESS");
                refreshAssetsOfEmployee();
            }).error(function (data, status, headers, config) {
                showMessage(resolveError(status), "DANGER");
                waitingDialog.hide();
            });
        }

        $scope.assetsHistory = [];
        $scope.fetchHistory = function () {
            if ($scope.assetHistoryTabDisable == false) {
                AssetFactory.getAssetHistoryForEmployee($scope.employeeId).success(function (data) {
                    $scope.assetsHistory = angular.copy(data);
                    showMessage($scope.assetsHistory.length + " Asset History Found.", "SUCCESS");
                    if (data.length == 0) {
                        $scope.showHistoryTable = false;
                    }
                    else {
                        $scope.showHistoryTable = true;
                    }
                }).error(function (data, status, headers, config) {
                    showMessage(resolveError(status), "DANGER");
                });
            }
        }


        $scope.validateForm = function () {
            $scope.isValidForm = true;
            $scope.firstNameError = false;
            $scope.lastNameError = false;
            $scope.emailError = false;
            $scope.ecodeError = false;
            var fields = "";
            if ($scope.search.firstName == "" || $scope.search.firstName === undefined) {
                fields += "First Name";
                $scope.firstNameError = true;

                $scope.isValidForm = false;
            }
            if ($scope.search.lastName == "" || $scope.search.lastName === undefined) {
                if ($scope.firstNameError == true) {
                    fields += ", ";
                }
                fields += "Last Name";
                $scope.lastNameError = true;

                $scope.isValidForm = false;
            }
            if ($scope.search.email == "" || $scope.search.email === undefined) {
                if ($scope.lastNameError == true) {
                    fields += ", ";
                }
                fields += "Email";
                $scope.emailError = true;

                $scope.isValidForm = false;
            }
            if ($scope.search.ecode == "" || $scope.search.ecode === undefined) {
                if ($scope.emailError == true) {
                    fields += ", ";
                }
                fields += "Employee Code ";
                $scope.ecodeError = true;
                $scope.isValidForm = false;
            }
            if ($scope.isValidForm == false) {
                showMessage("Please Enter " + fields + " as they are required.", "WARNING");
            }
        }

        $scope.parseApprovalCheckBox = function () {
            if ($scope.approvalsRequired == true) {
                $scope.search.approvalsRequired = "Y";
            }
            else {
                $scope.search.approvalsRequired = "N";
            }
        }

        $scope.editEmp = function (index) {
            $("#ecodeTxt" + index).hide();
            $("#firstNameTxt" + index).hide();
            $("#lastNameTxt" + index).hide();
            $("#mobileTxt" + index).hide();
            $("#emailTxt" + index).hide();
            $("#ecodeType" + index).show();
            $("#firstNameType" + index).show();
            $("#lastNameType" + index).show();
            $("#mobileType" + index).show();
            $("#emailType" + index).show();
            $("#pencilEmp" + index).hide();
            $("#saveEmp" + index).show();
            $("#approvalReq" + index).hide();
            $("#approvalReqEdit" + index).show();
        }

        $scope.updateEmployee = function (index, id) {
            $("#ecodeTxt" + index).show();
            $("#firstNameTxt" + index).show();
            $("#lastNameTxt" + index).show();
            $("#mobileTxt" + index).show();
            $("#emailTxt" + index).show();
            $("#ecodeType" + index).hide();
            $("#firstNameType" + index).hide();
            $("#lastNameType" + index).hide();
            $("#mobileType" + index).hide();
            $("#emailType" + index).hide();
            $("#pencilEmp" + index).show();
            $("#saveEmp" + index).hide();
            $("#approvalReq" + index).show();
            $("#approvalReqEdit" + index).hide();
            $scope.updateEmp.id = id;
            EmployeeFactory.update($scope.updateEmp).success(function (data) {
                $scope.search = {
                    approvalsRequired: 'NA'
                }
                if (data.status == 'SUCCESS') {
                    showMessage("Data Saved Successfully.", "SUCCESS")
                }
                else {
                    showMessage(data.error['errorMsg'], "DANGER");
                }
                fetchAll();
            }).error(function (data, status, headers, config) {
                showMessage(resolveError(status), "DANGER");
            });
            $scope.updateEmp = {};
        }

        $scope.save = function () {
            $scope.isResultOK = false;
            // $scope.parseApprovalCheckBox();
            waitingDialog.show("Please wait while data is getting saved . . .");
            $scope.validateForm();
            if ($scope.isValidForm == true) {
                EmployeeFactory.save($scope.search).success(function (data) {
                    console.log(data);
                    $scope.search = {
                        approvalsRequired: 'NA'
                    }
                    if (data.status == 'SUCCESS') {
                        showMessage("Data Saved Successfully.", "SUCCESS");
                        fetchAll();
                    }
                    else {
                        showMessage(data.error['errorMsg'], "DANGER");
                    }
                }).error(function (data, status, headers, config) {
                    showMessage(resolveError(status), "DANGER");
                    waitingDialog.hide();
                });
            }
            waitingDialog.hide();
        }

        $scope.today = function () {
            $scope.dt = new Date();
        };
        $scope.today();

        $scope.clear = function () {
            $scope.dt = null;
        };

        $scope.toggleSearch = function () {
            $scope.showSearch = !$scope.showSearch;
        };
        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };

        $scope.dateOptions = {
            dateDisabled: disabled,
            formatYear: 'yy',
            maxDate: new Date(2020, 5, 22),
            minDate: new Date(),
            startingDay: 1
        };

        // Disable weekend selection
        function disabled(data) {
            var date = data.date,
                mode = data.mode;
            return mode === 'day' && (date.getDay() === 0 || date.getDay() === 6);
        }

        $scope.toggleMin = function () {
            $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
            $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
        };

        $scope.toggleMin();

        $scope.open1 = function () {
            $scope.popup1.opened = true;
        };

        $scope.open2 = function () {
            $scope.popup2.opened = true;
        };

        $scope.setDate = function (year, month, day) {
            $scope.dt = new Date(year, month, day);
        };

        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[0];
        $scope.altInputFormats = ['M!/d!/yyyy'];

        $scope.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        };

        $scope.popup1 = {
            opened: false
        };

        $scope.popup2 = {
            opened: false
        };

        var tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        var afterTomorrow = new Date();
        afterTomorrow.setDate(tomorrow.getDate() + 1);
        $scope.events = [
            {
                date: tomorrow,
                status: 'full'
            },
            {
                date: afterTomorrow,
                status: 'partially'
            }
        ];

        function getDayClass(data) {
            var date = data.date,
                mode = data.mode;
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0, 0, 0, 0);

                for (var i = 0; i < $scope.events.length; i++) {
                    var currentDay = new Date($scope.events[i].date).setHours(0, 0, 0, 0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }

            return '';
        }

        $scope.openAssetWindow = function (size) {

            var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'myModalContent.html',
                controller: 'ModalInstanceCtrl',
                size: size,
                resolve: {
                    id: function () {
                        return $scope.employeeId;
                    },
                    enableApprovers: function () {
                        return $scope.enableApprover;
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {

            });
        }

        $scope.openDeleteEmployeeWindow = function (size, ecode) {

            var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'employeeConfirmation.html',
                controller: 'DeleteEmployeeCtrl',
                size: size,
                resolve: {
                    ecode: function () {
                        return ecode;
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {

            });
        };
    }
);
angular.module('app').controller('DeleteEmployeeCtrl', function ($scope, $timeout, EmployeeFactory, $uibModalInstance, ecode) {

    $scope.ecode = ecode;
    $scope.message = "";
    $scope.showMessage = false;

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.delete = function () {
        $scope.message = "";
        $scope.showMessage = false;
        $scope.disableYes = false;
        EmployeeFactory.delete($scope.ecode).success(function (data) {
            $scope.disableYes = true;
            if (data.status == 'SUCCESS') {
                $scope.message = "Employee has been removed from System Successfully. Please trigger your search again to get latest data.";
            }
            else {
                if (data.data.eMessage == 'NOT_FOUND') {
                    $scope.message = "Employee you are trying to modify is not present in system.";
                }
                if (data.data.eMessage == 'ASSETS_PRESENT') {
                    $scope.message = "There are some assets still assigned to employee. Please try to un-assign them first.";
                }
                if (data.data.eMessage == 'LOGGEDIN_USER_ERROR') {
                    $scope.message = "Logged In User can not delete himself from system . Please try to logged in with other user and then try to delete this user.";
                }
            }
            $scope.showMessage = true;
            $timeout(function () {
                $uibModalInstance.dismiss('cancel');
            }, 5000);
        }).error(function (data, status, headers, config) {
            $scope.disableYes = true;
            if (status == 401) {
                $scope.message = "It seems this user don't have permission to delete an Employee.";
                $scope.showMessage = true;
            }
            else {
                $scope.message = "Snap! An error har occured at server side. Please try after some time.";
                $scope.showMessage = true;
            }
        });
    }

});
angular.module('app').controller('ModalInstanceCtrl', function ($scope, AssetFactory, EmployeeFactory, $uibModalInstance, id, enableApprovers) {

    $scope.id = id;
    $scope.enableApprovers = enableApprovers;
    $scope.showPrint = false;
    $scope.showApprovers = false;

    $scope.assetTypeList = [];

    $scope.assetsFound = false;

    $scope.assetList = [];
    $scope.selectedAssetType = "";
    $scope.selectedManufacturer = "";
    $scope.assignAsset = {};
    $scope.approvers = [];
    $scope.assetTypeDisplayList = [];
    $scope.assetDisplayList = [];
    $scope.approversList = [];
    $scope.approversDisplayList = [];
    $scope.manufacturerList = [];
    $scope.manufacturerDisplayList = [];
    $scope.dummyEmployee = {
        id: -1,
        firstName: 'Please select Approver',
        lastName: ""
    };

    $scope.dummyManufacturer = {
        name: 'Select Manufacturer of Asset',
        id: -1
    };

    $scope.assetTypeDummy = {
        id: -1,
        type: 'Please select Asset Type',
        numberOfAsset: 0
    };

    $scope.assetDummy = {
        id: -1,
        name: 'Please select Asset '
    };
    init();

    function init() {
        AssetFactory.getAssetTypeList().success(function (data) {
            $scope.assetTypeList = angular.copy(data).data.Type;
            $scope.assetTypeDisplayList[0] = $scope.assetTypeDummy;
            for (var index = 0; index < $scope.assetTypeList.length; index++) {
                $scope.assetTypeDisplayList.push($scope.assetTypeList[index]);
            }
            $scope.selectedAssetType = $scope.assetTypeDummy.id;
        });

        AssetFactory.getManufacturerList().success(function (data) {
            $scope.manufacturerList = angular.copy(data);
            $scope.manufacturerDisplayList[0] = ($scope.dummyManufacturer);
            $scope.selectedManufacturer = $scope.dummyManufacturer.id;
            for (var index = 0; index < $scope.manufacturerList.length; index++) {
                $scope.manufacturerDisplayList.push($scope.manufacturerList[index]);
            }
        });

        if ($scope.enableApprovers == "Y") {
            $scope.showApprovers = true;
            EmployeeFactory.getApprovers().success(function (data) {
                $scope.approvers = angular.copy(data);
                $scope.approversDisplayList[0] = $scope.dummyEmployee;
                for (var index = 0; index < $scope.approvers.length; index++) {
                    $scope.approversDisplayList.push($scope.approvers[index]);
                }
                $scope.assignAsset.approvedBy = $scope.dummyEmployee.id;
            });
        }
    }

    $scope.assetFetched = false;
    $scope.showMessage = false;
    $scope.message = "";
    $scope.fetchAvailableAssetsByTypeAndManu = function () {
        if ($scope.selectedAssetType == -1) {
            $scope.showMessage = true;
            $scope.message = "Please select type of Asset, you want to fetch."
            $("#assignAssetModalMessage").css("color", "darkorange");
        }
        else if ($scope.selectedManufacturer == -1) {
            $scope.showMessage = true;
            $scope.message = "Please select Manufacturer of Asset, you want to fetch."
            $("#assignAssetModalMessage").css("color", "darkorange");
        }
        else {
            $scope.showMessage = false;
            $scope.message = "";
            var ids = $scope.selectedAssetType + '-' + $scope.selectedManufacturer;
            AssetFactory.fetchAvailableAssetByTypeAndManu(ids).success(function (data) {
                $scope.assetDisplayList = [];
                $scope.assetList = angular.copy(data);
                $scope.assetDisplayList[0] = $scope.assetDummy;
                $scope.assignAsset.assetId = $scope.assetDummy.id;
                if ($scope.assetList.length > 0) {
                    for (var index = 0; index < $scope.assetList.length; index++) {
                        var tempAsset = $scope.assetList[index];
                        tempAsset.name = $scope.assetList[index].name + "(" + $scope.assetList[index].serialNumber + ")";
                        $scope.assetDisplayList.push(tempAsset);
                    }
                    $scope.assetsFound = true;
                }
                else {
                    $scope.assetsFound = false;
                }
                $scope.assetFetched = true;
            });
        }
    }
    $scope.popup3 = {
        opened: false
    };

    $scope.open3 = function () {
        $scope.popup3.opened = true;
    };

    $scope.popup4 = {
        opened: false
    };

    $scope.open4 = function () {
        $scope.popup4.opened = true;
    };

    $scope.isFormValid = true;
    $scope.ok = function () {
        $scope.isFormValid = true;
        validateForm();
        if ($scope.isFormValid == true) {
            $scope.showMessage = false;
            $scope.assignAsset.employee = id;
            AssetFactory.assignAsset($scope.assignAsset).success(function (data) {
                console.log(data);
            });
            $uibModalInstance.close();
        }

    };

    $scope.print = function () {

    }

    function validateForm() {
        if ($scope.assignAsset.assetId == -1 || $scope.assignAsset.assetId === undefined) {
            $scope.showMessage = true;
            $scope.message = "Please select an Asset to assign."
            $("#assignAssetModalMessage").css("color", "darkorange");
            $scope.isFormValid = false;
        }
        else if ($scope.assignAsset.dateOfIssue == "" || $scope.assignAsset.dateOfIssue === undefined) {
            $scope.showMessage = true;
            $scope.message = "Please select Date of Issue."
            $("#assignAssetModalMessage").css("color", "darkorange");
            $scope.isFormValid = false;

        }
        else if ($scope.assignAsset.dateTillValid == "" || $scope.assignAsset.dateTillValid === undefined) {
            $scope.showMessage = true;
            $scope.message = "Please select Date Till Valid."
            $("#assignAssetModalMessage").css("color", "darkorange");
            $scope.isFormValid = false;
        }
        if ($scope.showApprovers == true &&  ($scope.assignAsset.approvedBy == -1 ||  $scope.assignAsset.approvedBy === undefined)) {
            $scope.showMessage = true;
            $scope.message = "Please select an Approver."
            $("#assignAssetModalMessage").css("color", "darkorange");
            $scope.isFormValid = false;
        }
    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
    $scope.print = function () {
        $uibModalInstance.close();

    }
})

    .factory('EmployeeFactory', function ($http, $cookieStore) {

        return{
            getTotalCount: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/employee/getTotalEmployeeCount');
            },
            getEmployeeList: function (offset, limit) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/employee/fetchAll?offset=' + offset + "&limit=" + limit);
            },
            fetchEmployee: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/employee/fetch', data);
            },
            save: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/employee/create', data);
            },
            getApprovers: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/employee/getApprovers');
            },
            update: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/employee/update', data);
            },
            delete: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/employee/delete', data);
            },
            isECodeAvailable: function(data){
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/employee/isECodeAvailable', data);
            }
        }
    })

;