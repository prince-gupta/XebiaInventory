angular.module("app")
    .controller('xlsMappingController',
    function ($scope, ExcelFactory) {
        $scope.mappings =[];

        ExcelFactory.getExcelMappings().success(function(data){
            $scope.mappings = angular.copy(data);
        });

        $scope.save = function(){
            ExcelFactory.updateExcelMappings($scope.mappings).success(function(data){
                $scope.mappings = angular.copy(data);
            })
        }
    })

;