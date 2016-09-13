angular.module('userApp')
    .controller('registerController',
    function ($scope, Factory, $uibModal, $cookieStore, $timeout, FileUpload) {
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

        $scope.approvals = [];

        init();

        $scope.uploadFile = function () {
            console.log($scope);
            var file = $scope.myFile;

            console.log('file is ');
            console.dir(file);

            var uploadUrl = "/fileUpload";
            FileUpload.uploadFileToUrl(file, uploadUrl);
        }

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

        $scope.fetchApprovals = function(){
            Factory.getAssetApprovals().success(function(data){
                $scope.approvals = angular.copy(data);
            })
        }
        $scope.openRequestAssetWindow = function (size) {

            var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'ModalContent.html',
                controller: 'ModalInstanceCtrl',
                size: size
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {

            });
        }

    }
)
    .directive('fileModel', function ($parse) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var model = $parse(attrs.fileModel);
                var modelSetter = model.assign;
                console.log(scope);

                element.bind('change', function () {
                    scope.$apply(function () {
                        modelSetter(scope.$parent.$parent, element[0].files[0]);
                    });
                });
            }
        };
    })
    .factory('Factory', function ($http, $cookieStore) {

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
            },
            getAssetTypeList: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/asset/fetchAllAssetType');
            },
            sendAssetApproval: function(data){
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/asset/assetApproval', data);
            },
            getAssetApprovals : function(){
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get('/inventory/asset/getApprovals');
            },
            updateEmployee : function(data){
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.post('/inventory/employee/updateEmployee', data);
            }
        }
    });
angular.module('userApp').controller('ModalInstanceCtrl', function ($scope, $timeout, $uibModalInstance, Factory) {

    $scope.assetTypeList = [];
    $scope.assetTypeDisplayList = [];

    $scope.showMessage = false;
    $scope.message = "";
    $scope.requestAsset = {};

    $scope.disableReq = false;

    $scope.assetTypeDummy = {
        id: -1,
        type: 'Please select Asset Type',
        numberOfAsset: 0
    };

    init();

    function init() {
        Factory.getAssetTypeList().success(function (data) {
            $scope.assetTypeList = angular.copy(data).data.Type;
            $scope.assetTypeDisplayList[0] = $scope.assetTypeDummy;
            for (var index = 0; index < $scope.assetTypeList.length; index++) {
                $scope.assetTypeDisplayList.push($scope.assetTypeList[index]);
            }
            $scope.selectedAssetType = $scope.assetTypeDummy.id;
        });
    }
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.popup4 = {
        opened: false
    };

    $scope.open4 = function () {
        $scope.popup4.opened = true;
    };

    $scope.ok = function(){
        Factory.sendAssetApproval($scope.requestAsset).success(function(data){
            if(data.status == "SUCCESS") {
                $scope.showMessage = true;
                $scope.message = "Request has been raised to IT. For smooth approval of request please send approval mail approved by your manager to IT. You can check status in Approvals tab.";
                $("#messageId").css("color", "green");
                $scope.disableReq = true;
                $timeout(function(){
                    $uibModalInstance.dismiss('cancel');
                }, 5000);
            }
        });
    }
});