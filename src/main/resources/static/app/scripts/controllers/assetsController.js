angular.module('app')
    .controller('assetController',
    function ($scope, AssetFactory, $uibModal) {
        $scope.infoPopover = {
            templateUrl: 'popoverInfoTemplate.html',
            title: 'Configuration',
            cpu: 'N/A',
            ram: 'N/A',
            hdd: 'N/A'
        };
        $scope.typeDeletePopover = {
            content: 'Hello, World!',
            templateUrl: 'typeDeleteConfirmation.html',
            title: 'Confirmation',
            id: 1
        };

        $scope.exportToExcelTooltip = "Export To Excel";
        $scope.animationsEnabled = true;
        $scope.showTypeInput = false;
        $scope.assetTypeList = [];
        $scope.assetList = [];
        $scope.assetType = {};
        $scope.isEditAssetType = true;
        $scope.isDoneAssetType = false;
        $scope.isEditManu = true;
        $scope.isDoneManu = false;
        $scope.updateAssetType = {};
        $scope.updateAsset = {};
        $scope.manufacturerList = [];
        $scope.updateManu = {};
        $scope.updateHW = {};
        $scope.hwConfigList = [];
        $scope.newHW = {};
        $scope.assetHWConfig = [];

        $scope.assetTypeList1 = [];
        $scope.assetTypeDisplayList = [];
        $scope.manufacturerList = [];
        $scope.manufacturerDisplayList = [];
        $scope.search = {};
        $scope.dummyAssetType = {
            type: 'Select Type of Asset',
            id: -1
        };
        $scope.dummyManufacturer = {
            name: 'Select Manufacturer of Asset',
            id: -1
        };

        init();

        function init() {
            fetchAllAsset();


            AssetFactory.getAssetTypeList().success(function (data) {
                $scope.assetTypeList1 = angular.copy(data.data.Type);
                $scope.assetTypeDisplayList[0] = ($scope.dummyAssetType);
                $scope.search.assetType = ($scope.dummyAssetType.id);
                for (var index = 0; index < $scope.assetTypeList1.length; index++) {
                    $scope.assetTypeDisplayList.push($scope.assetTypeList1[index]);
                }
            });
            AssetFactory.getManufacturerList().success(function (data) {
                $scope.manufacturerList = angular.copy(data);
                $scope.manufacturerDisplayList[0] = ($scope.dummyManufacturer);
                $scope.search.assetManufacturer = $scope.dummyManufacturer.id;
                for (var index = 0; index < $scope.manufacturerList.length; index++) {
                    $scope.manufacturerDisplayList.push($scope.manufacturerList[index]);
                }
            });
        }

        $scope.clearForm = function () {
            $scope.search = {};
            init();
        }

        $scope.performSearch = function () {
            AssetFactory.searchAsset($scope.search).success(function (data) {
                $scope.assetList = angular.copy(data);
                for (var index = 0; index < $scope.assetList.length; index++) {
                    var hwObj = {};
                    hwObj.cpu = $scope.assetList[index].hardwareConfiguration != null ? $scope.assetList[index].hardwareConfiguration.cpu : 'NA';
                    hwObj.hdd = $scope.assetList[index].hardwareConfiguration != null ? $scope.assetList[index].hardwareConfiguration.hdd : 'NA';
                    hwObj.ram = $scope.assetList[index].hardwareConfiguration != null ? $scope.assetList[index].hardwareConfiguration.ram : 'NA';
                    $scope.assetHWConfig[$scope.assetList[index].assetId] = hwObj;
                }
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


        $scope.closeTypeInput = function () {
            $scope.showTypeInput = false;
        }

        $scope.addDeletedID = function (id) {
            $scope.typeDeletePopover.id = id;
        }

        $scope.hideAssetTypePopup = function (id) {
            $(".popover").hide();
        }

        $scope.fetchAllAssetType = function () {
            fetchAllAssetType();
        }

        $scope.fetchAllAsset = function () {
            fetchAllAsset();
        }
        $scope.fetchAllManufacturer = function () {
            fetchAllManufacturer();
        }

        $scope.fetchAllHWConfig = function () {
            fetchHardwareConfigurations();
        }

        $scope.populateHwConfig = function (id) {
            var hwInfo = $scope.assetHWConfig[id];
            $scope.infoPopover.cpu = hwInfo.cpu;
            $scope.infoPopover.hdd = hwInfo.hdd;
            $scope.infoPopover.ram = hwInfo.ram;
        }


        $scope.edit = function (index) {
            $("#labelType" + index).hide();
            $("#editType" + index).show();
            $("#pencilType" + index).hide();
            $("#floppyType" + index).show();
            $("#cancleType" + index).show();
            $scope.updateAssetType = {};
        }

        $scope.done = function (index, id) {
            $("#labelType" + index).show();
            $("#editType" + index).hide();
            $("#pencilType" + index).show();
            $("#floppyType" + index).hide();
            $("#cancleType" + index).hide();
            $scope.updateAssetType.id = id;
            AssetFactory.updateAssetType($scope.updateAssetType).success(function (data) {
                console.log(data);
                fetchAllAssetType();
            });
        }

        $scope.doneWithNoActio = function (index, id) {
            $("#labelType" + index).show();
            $("#editType" + index).hide();
            $("#pencilType" + index).show();
            $("#floppyType" + index).hide();
            $("#cancleType" + index).hide();
            $scope.updateAssetType.id = id;
        }


        $scope.editManu = function (index) {
            $("#labelManuName" + index).hide();
            $("#labelManuAdd" + index).hide();
            $("#labelManuMobile" + index).hide();
            $("#labelManuEmail" + index).hide();
            $("#editManuName" + index).show();
            $("#editManuAdd" + index).show();
            $("#editManuMobile" + index).show();
            $("#editManuEmail" + index).show();
            $("#pencilManu" + index).hide();
            $("#floppyManu" + index).show();
            $("#cancleManu" + index).show();
            $scope.updateManu = {};
        }

        $scope.doneManu = function (index, id) {
            $("#labelManuName" + index).show();
            $("#labelManuAdd" + index).show();
            $("#labelManuMobile" + index).show();
            $("#labelManuEmail" + index).show();
            $("#editManuName" + index).hide();
            $("#editManuAdd" + index).hide();
            $("#editManuMobile" + index).hide();
            $("#editManuEmail" + index).hide();
            $("#pencilManu" + index).show();
            $("#floppyManu" + index).hide();
            $("#cancleManu" + index).hide();
            $scope.updateManu.id = id;
            AssetFactory.updateManufacturer($scope.updateManu).success(function (data) {
                console.log(data);
                fetchAllManufacturer();
            });
        }

        $scope.doneWithNoActionManu = function (index, id) {
            $("#labelManuName" + index).show();
            $("#labelManuAdd" + index).show();
            $("#labelManuMobile" + index).show();
            $("#labelManuEmail" + index).show();
            $("#editManuName" + index).hide();
            $("#editManuAdd" + index).hide();
            $("#editManuMobile" + index).hide();
            $("#editManuEmail" + index).hide();
            $("#pencilManu" + index).show();
            $("#floppyManu" + index).hide();
            $("#cancleManu" + index).hide();
            $scope.updateManu = {};
        }


        $scope.editHW = function (index) {
            $("#labelHWName" + index).hide();
            $("#labelHWCPU" + index).hide();
            $("#labelHWHDD" + index).hide();
            $("#labelHWRAM" + index).hide();
            $("#editHWName" + index).show();
            $("#editHWCPU" + index).show();
            $("#editHWHDD" + index).show();
            $("#editHWRAM" + index).show();
            $("#pencilHW" + index).hide();
            $("#floppyHW" + index).show();
            $("#cancleHW" + index).show();
            $scope.updateHW = {};
        }

        $scope.doneHW = function (index, id) {
            $("#labelHWName" + index).show();
            $("#labelHWCPU" + index).show();
            $("#labelHWHDD" + index).show();
            $("#labelHWRAM" + index).show();

            $("#editHWName" + index).hide();
            $("#editHWCPU" + index).hide();
            $("#editHWHDD" + index).hide();
            $("#editHWRAM" + index).hide();

            $("#pencilHW" + index).show();
            $("#floppyHW" + index).hide();
            $("#cancleHW" + index).hide();
            $scope.updateHW.id = id;
            AssetFactory.updateHWConfig($scope.updateHW).success(function (data) {
                console.log(data);
                fetchHardwareConfigurations();
            });
        }

        $scope.doneWithNoActionHW = function (index, id) {
            $("#labelHWName" + index).show();
            $("#labelHWCPU" + index).show();
            $("#labelHWHDD" + index).show();
            $("#labelHWRAM" + index).show();

            $("#editHWName" + index).hide();
            $("#editHWCPU" + index).hide();
            $("#editHWHDD" + index).hide();
            $("#editHWRAM" + index).hide();

            $("#pencilHW" + index).show();
            $("#floppyHW" + index).hide();
            $("#cancleHW" + index).hide();
            $scope.updateHW = {};
        }

        $scope.generateAssetReport = function () {
            window.location.href = "/inventory/asset/generateAssetReport"
        }

        $scope.processHistAssets = function(){
            AssetFactory.processHistAssets().success(function(data){
               var result = angular.copy(data);
            });
        }

        function fetchAllAssetType() {
            AssetFactory.getAssetTypeList().success(function (data) {
                var assetTypes = angular.copy(data);
                $scope.assetTypeList = angular.copy(data.data.Type);
            });
        }

        function fetchAllAsset() {
            AssetFactory.getAssetList().success(function (data) {
                $scope.assetList = angular.copy(data);
                for (var index = 0; index < $scope.assetList.length; index++) {
                    var hwObj = {};
                    hwObj.cpu = $scope.assetList[index].hardwareConfiguration != null ? $scope.assetList[index].hardwareConfiguration.cpu : 'NA';
                    hwObj.hdd = $scope.assetList[index].hardwareConfiguration != null ? $scope.assetList[index].hardwareConfiguration.hdd : 'NA';
                    hwObj.ram = $scope.assetList[index].hardwareConfiguration != null ? $scope.assetList[index].hardwareConfiguration.ram : 'NA';
                    $scope.assetHWConfig[$scope.assetList[index].assetId] = hwObj;
                }
                showMessage($scope.assetList.length + " Assets(s) Found.", "SUCCESS");
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

        function fetchAllManufacturer() {
            AssetFactory.getManufacturerList().success(function (data) {
                $scope.manufacturerList = angular.copy(data);
            });
        }

        function fetchHardwareConfigurations() {
            AssetFactory.fetchHardwareConfigurations().success(function (data) {
                $scope.hwConfigList = angular.copy(data);
            });
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

        function resetMessages() {
            $scope.showDangerMessage = false;
            $scope.dangerMessage = "";

            $scope.showWarningMessage = false;
            $scope.warningMessage = "";

            $scope.showSuccessMessage = false;
            $scope.successMessage = "";

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


        $scope.saveType = function () {
            AssetFactory.saveAssetType($scope.assetType).success(function (data) {
                console.log(data);
                fetchAllAssetType();
            });
        }

        $scope.deleteAssetType = function (id, number) {
            if (number > 0) {
                return false;
            }
            AssetFactory.deleteAssetType(id).success(function (data) {
                console.log(data);
                fetchAllAssetType();
            }).error(function (data, status, headers, config) {
                showMessage(resolveError(status), "DANGER");
            });
        }

        $scope.deleteManu = function (id) {
            AssetFactory.deleteManufacturer(id).success(function (data) {
                var result = angular.copy(data);
                if (result.status == "SUCCESS") {
                    showMessage("Manufacturer Info Deleted.", "SUCCESS");
                    fetchAllAssetType();
                }
                else {
                    showMessage("Unable to delete Manufacturer Info, as it might be possible their associated assests still present in system. Delete them first and try again.", "DANGER");
                }
            }).error(function (data, status, headers, config) {
                showMessage(resolveError(status), "DANGER");
            });
        }

        $scope.deleteHWConfig = function (id) {
            AssetFactory.deleteHWConfig(id).success(function (data) {
                var result = angular.copy(data);
                if (result.status == "SUCCESS") {
                    showMessage("Hardware Configuration Deleted.", "SUCCESS");
                    fetchHardwareConfigurations();
                }
                else {
                    showMessage("Unable to delete Hardware Configuration, as it might be possible their associated assests still present in system. Delete them first and try again.", "DANGER");
                }

            }).error(function (data, status, headers, config) {
                showMessage(resolveError(status), "DANGER");
            });
        }
        $scope.openAddAssetWindow = function (size) {

            var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'myAssetModalContent.html',
                controller: 'AssetModalInstanceCtrl',
                size: size,
                resolve: {
                    items: function () {
                        return $scope.items;
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {

            });
        },
            $scope.openAddTypeWindow = function () {
                $scope.showTypeInput = true;
            },
            $scope.openAddManufacturerWindow = function (size) {

                var modalInstance = $uibModal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'ManufacturerModalContent.html',
                    controller: 'ManufacturerModalInstanceCtrl',
                    size: size,
                    resolve: {
                        items: function () {
                            return $scope.items;
                        }
                    }
                });

                modalInstance.result.then(function (selectedItem) {
                    $scope.selected = selectedItem;
                }, function () {

                });
            },
            $scope.openAddHWConfigWindow = function (size) {

                var modalInstance = $uibModal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'HWConfigModalContent.html',
                    controller: 'HWConfigModalInstanceCtrl',
                    size: size,
                    resolve: {
                        items: function () {
                            return $scope.items;
                        }
                    }
                });

                modalInstance.result.then(function (selectedItem) {
                    $scope.selected = selectedItem;
                }, function () {

                });
            }
        $scope.openDeleteAssetWindow = function (size, assetId) {

            var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'assetConfirmation.html',
                controller: 'DeleteAssetCtrl',
                size: size,
                resolve: {
                    assetId: function () {
                        return assetId;
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {

            });
        };

        $scope.openExcelImportWindow = function (size) {

            var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'excelImport.html',
                controller: 'ExcelImportCtrl',
                size: size
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {

            });
        };
    })
    .factory('AssetFactory', function ($http, $cookieStore) {
        return{
            getAssetTypeList: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/asset/fetchAllAssetType');
            },
            saveAssetType: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/createAssetType', data);
            },
            deleteAssetType: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/deleteAssetType', data);
            },
            updateAssetType: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/updateAssetType', data);
            },
            searchAsset: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/searchAsset', data);
            },
            getAssetList: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/asset/fetchAllAsset');
            },
            saveAsset: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/createAsset', data);
            },
            deleteAsset: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/deleteAsset', data);
            },
            getAssetListByEmployee: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/fetchEmployeeAsset', data);
            },
            getAssetByType: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/fetchAssetByType', data);
            },
            processHistAssets: function(){
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/asset/processHistoricalAssets');
            },
            fetchAvailableAssetByTypeAndManu: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/fetchAvailableAssetByTypeAndManu', data);
            },
            assignAsset: function (data) {
                data.userName = $cookieStore.get('Username');
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/assignAsset', data);
            },
            unAssignAsset: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/unAssignAsset', data);
            },
            getManufacturerList: function () {
                return $http.get('/inventory/asset/fetchAllAssetManufacturer');
            },
            saveManufacturer: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/createAssetManufacturer', data);
            },
            deleteManufacturer: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/deleteAssetManufacturer', data);
            },
            updateManufacturer: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('http://localhost:8080/inventory/asset/updateAssetManufacturer', data);
            },
            fetchHardwareConfigurations: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/asset/fetchAllConfiguration');
            },
            saveHWConfig: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/createHardwareConfigurations', data);
            },
            updateHWConfig: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/updateHardwareConfigurations', data);
            },
            deleteHWConfig: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/deleteHardwareConfiguration', data);
            },
            getAssetHistoryForEmployee: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/getAssetsHistory', data);
            },
            getAssetExpirationReport: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/asset/getAssetExpirationReport');
            },
            generateAssetReport: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/asset/generateAssetReport');
            },
            getEmployeeAssetsFileParam: function (data) {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/getEmployeeAssetsFileParam', data);
            }
        }
    })
;
angular.module('app').controller('DeleteAssetCtrl', function ($scope, $timeout, AssetFactory, $uibModalInstance, assetId) {

    $scope.assetId = assetId;
    $scope.message = "";
    $scope.showMessage = false;

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.delete = function () {
        $scope.message = "";
        $scope.showMessage = false;
        $scope.disableYes = false;
        AssetFactory.deleteAsset($scope.assetId).success(function (data) {
            $scope.disableYes = true;
            if (data.status == 'SUCCESS') {
                $scope.message = "Asset has been removed from System Successfully. Please trigger your search again to get latest data.";
            }
            else {
                if (data.data.eCode == 'ASSET_ASSINGMENT_ERROR ') {
                    $scope.message = "Asset you are trying to delete is assigned to " + data.data.empName + ". Please un-assign it before removing it from system.";
                }
            }
            $scope.showMessage = true;
            $timeout(function () {
                $uibModalInstance.dismiss('cancel');
            }, 5000);
        })
    }

});

angular.module('app')
    .directive("fileModel", function () {
        return {
            restrict: 'EA',
            scope: {
                setFileData: "&"
            },
            link: function (scope, ele, attrs) {
                ele.on('change', function () {
                    scope.$apply(function () {
                        var val = ele[0].files[0];
                        scope.setFileData({ value: val });
                    });
                });
            }
        }
    })
    .service('fileUpload', ['$http','$cookieStore',
        function ($http,$cookieStore) {

            this.uploadFileToUrl = function (data) {
                var fd = new FormData();
                fd.append('file', data);

                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                $http.post("/inventory/common/uploadExcelFile", fd, {
                    withCredentials: false,
                    headers: {
                        'Content-Type': undefined
                    },
                    transformRequest: angular.identity
                })
                    .success(function (response, status, headers, config) {
                        console.log(response);

                        if (status == 200 || status == 202){

                        } //do whatever in success
                        else{

                        } // handle error in  else if needed
                    })
                    .error(function (error, status, headers, config) {
                        console.log(error);

                        // handle else calls
                    });
            }
        }
    ])
    .controller('ExcelImportCtrl', function ($scope, fileUpload, $uibModalInstance) {

        $scope.navigate = function () {
            $uibModalInstance.dismiss('cancel');
            window.location = "#excelMapping";
        }
        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $scope.fileToUpload={};
        $scope.process = function () {
           /* var formData = new FormData();
            angular.forEach($scope.files, function (value, key) {
                formData.append(key, value);
            });*/
            /*ExcelFactory.uploadExcelFile(formData).success(function (data) {
                alert(data);
            })*/

            fileUpload.uploadFileToUrl($scope.fileToUpload);
        }

    });

angular.module('app').controller('AssetModalInstanceCtrl', function ($scope, AssetFactory, $uibModalInstance, items) {

    $scope.assetTypeList1 = [];
    $scope.assetTypeDisplayList = [];
    $scope.manufacturerList = [];
    $scope.manufacturerDisplayList = [];
    $scope.hwConfigList = [];
    $scope.hwConfigDisplayList = [];
    $scope.asset = {};
    $scope.popup5 = {
        opened: false
    };
    $scope.dummyAssetType = {
        type: 'Select Type of Asset',
        id: -1
    };
    $scope.dummyManufacturer = {
        name: 'Select Manufacturer of Asset',
        id: -1
    };
    $scope.dummyHardwareConfig = {
        id: -1,
        label: 'Select Hardware Configuration of Asset'
    };
    init();

    function init() {
        AssetFactory.getAssetTypeList().success(function (data) {
            $scope.assetTypeList1 = angular.copy(data.data.Type);
            $scope.assetTypeDisplayList[0] = ($scope.dummyAssetType);
            $scope.asset.assetType = ($scope.dummyAssetType.id);
            for (var index = 0; index < $scope.assetTypeList1.length; index++) {
                $scope.assetTypeDisplayList.push($scope.assetTypeList1[index]);
            }
        });
        AssetFactory.getManufacturerList().success(function (data) {
            $scope.manufacturerList = angular.copy(data);
            $scope.manufacturerDisplayList[0] = ($scope.dummyManufacturer);
            $scope.asset.assetManufacturer = $scope.dummyManufacturer.id;
            for (var index = 0; index < $scope.manufacturerList.length; index++) {
                $scope.manufacturerDisplayList.push($scope.manufacturerList[index]);
            }
        });
        AssetFactory.fetchHardwareConfigurations().success(function (data) {
            $scope.hwConfigList = angular.copy(data);
            $scope.hwConfigDisplayList[0] = $scope.dummyHardwareConfig;
            $scope.asset.hardwareConfiguration = $scope.dummyHardwareConfig.id;
            for (var index = 0; index < $scope.hwConfigList.length; index++) {
                var attr = {};
                attr.label = $scope.hwConfigList[index].cpu + " " + $scope.hwConfigList[index].hdd + " " + $scope.hwConfigList[index].ram;
                attr.id = $scope.hwConfigList[index].id;
                $scope.hwConfigDisplayList.push(attr);
            }
        });
    }

    $scope.open5 = function () {
        $scope.popup5.opened = true;

    };

    $scope.ok = function () {
        AssetFactory.saveAsset($scope.asset).success(function (data) {
            console.log(data);
            $uibModalInstance.close();
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});

angular.module('app').controller('ManufacturerModalInstanceCtrl', function ($scope, AssetFactory, $uibModalInstance, items) {

    $scope.newManu = {};

    $scope.popup6 = {
        opened: false
    };

    $scope.open6 = function () {
        $scope.popup6.opened = true;
    };

    $scope.ok = function () {
        AssetFactory.saveManufacturer($scope.newManu).success(function (data) {
            console.log(data);
            $uibModalInstance.close();
        });

    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});

angular.module('app').controller('HWConfigModalInstanceCtrl', function ($scope, AssetFactory, $controller, $uibModalInstance, items) {

    var cntrl2 = $scope.$new();
    $controller('assetController', {$scope: cntrl2 });

    $scope.newHW = {};

    $scope.popup7 = {
        opened: false
    };

    $scope.open7 = function () {
        $scope.popup7.opened = true;
    };

    $scope.ok = function () {
        AssetFactory.saveHWConfig($scope.newHW).success(function (data) {
            console.log(data);
            cntrl2.fetchAllHWConfig();
            $uibModalInstance.close();
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});


