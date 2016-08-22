angular.module('app')
    .controller('dashboardController', function ($scope, $interval, AssetFactory) {
        $scope.typeList = [];
        $scope.manuMap = [];
        $scope.statMap = [];
        $scope.manuName = [];
        $scope.typeIdMap = [];
        $scope.manuMap = [];
        $scope.manuNameMap = [];
        $scope.assetExpirationReport = [];

        init();

        function fetchAssetExpirationReport() {
            AssetFactory.getAssetExpirationReport().success(function (data) {
                $scope.assetExpirationReport = angular.copy(data);
            }).error(function (data, status, headers, config) {
                if (status == 401) {
                    window.location = ""
                }
            });
        }

        function fetchAssetAvailability() {
            AssetFactory.getAssetTypeList().success(function (data) {
                $scope.typeList = angular.copy(data.data.Type);
                var tempManuMap = angular.copy(data.data.ManuMap);
                $scope.typeIdMap = Object.keys(tempManuMap);
                for (var i = 0; i < $scope.typeIdMap.length; i++) {
                    $scope.manuName[$scope.typeIdMap[i]] = Object.keys(tempManuMap[$scope.typeIdMap[i]]);
                    $scope.manuNameMap[$scope.typeIdMap[i]] = tempManuMap[$scope.typeIdMap[i]];
                }
                for (var index = 0; index < $scope.typeList.length; index++) {
                    var available = $scope.typeList[index].availableAssets;
                    var total = $scope.typeList[index].numberOfAsset;
                    if (total != 0) {
                        $scope.typeList[index].availableWidth = (available / total) * 100;
                        $scope.typeList[index].totalWidth = 100 - ((available / total) * 100);
                    }
                    else {
                        $scope.typeList[index].availableWidth = 0;
                        $scope.typeList[index].totalWidth = 100;
                    }
                }
            })
                .error(function (data, status, headers, config) {
                    if (status == 401) {
                        window.location = ""
                    }
                });
        }

        $scope.availabilityInterval ;
        $scope.reportInterval ;
        function init() {
            fetchAssetAvailability();
            fetchAssetExpirationReport();
            $scope.reportInterval = $interval(fetchAssetExpirationReport, 10000);
            $scope.availabilityInterval = $interval(fetchAssetAvailability, 10000);

            $scope.$on('$locationChangeStart', function(){
                $interval.cancel($scope.availabilityInterval);
                $interval.cancel($scope.reportInterval);
            });
        }
    })
;
