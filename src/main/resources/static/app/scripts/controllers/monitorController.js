angular.module("app")
    .controller("monitorController",
    function ($scope, $interval, MonitorFactory) {

        init();

        $scope.diskInterval;
        $scope.cpuInterval;
        $scope.memoryInterval;
        function init() {
            console.log("Init()")
            google.charts.setOnLoadCallback(drawChart);
           // google.charts.setOnLoadCallback(drawGauge);
            function drawChart() {
                var result ={} ;
                var gData = google.visualization.arrayToDataTable([
                    ['Stats', '%'],
                    ['Free', 100],
                    ['Used', 0]
                ]);
                var chart = new google.visualization.PieChart(document.getElementById('piechart_3d'));
                var options = {
                    is3D: false,
                    height : 250,
                    width : 250,
                    chartArea :{left:0,top:0,bottom:0,right:0},
                    legend : {position: 'bottom', textStyle: {color: 'blue', fontSize: 16}}
                };
                MonitorFactory.metrics().success(function(data){
                    result = angular.copy(data);
                    gData.setValue(0, 1, result['disk.c.free']);
                    gData.setValue(1, 1, result['disk.c.used']);
                    chart.draw(gData, options);
                });

                $scope.diskInterval = $interval(function () {
                    var tempResult;
                    MonitorFactory.metrics().success(function(data){
                        tempResult = angular.copy(data);
                        gData.setValue(0, 1,tempResult['disk.c.free']);
                        gData.setValue(1, 1,tempResult['disk.c.used']);
                        chart.draw(gData, options);
                    });

                }, 5000);
            }

            /*function drawGauge() {

                var data = google.visualization.arrayToDataTable([
                    ['Label', 'Value'],
                    ['CPU', 55],
                ]);

                var options = {
                    width: 250, height: 250,
                    redFrom: 90, redTo: 100,
                    yellowFrom:75, yellowTo: 90,
                    minorTicks: 5,
                    chartArea :{left:0,top:0,bottom:0,right:0}
                };

                var chart = new google.visualization.Gauge(document.getElementById('chart_div'));

                chart.draw(data, options);

                $interval(function() {
                    data.setValue(0, 1, 40 + Math.round(60 * Math.random()));
                    chart.draw(data, options);
                }, 13000);
            }*/

            var g1 = new JustGage({
                id: 'cpu_gauge',
                value: 0,
                min: 0,
                max: 100,
                symbol: '%',
                pointer: true,
                pointerOptions: {
                    toplength: -15,
                    bottomlength: 10,
                    bottomwidth: 12,
                    color: '#8e8e93',
                    stroke: '#ffffff',
                    stroke_width: 3,
                    stroke_linecap: 'round'
                },
                width: 300,
                height : 300,
                gaugeWidthScale: 0.6,
                counter: true
            });
            $scope.cpuInterval = $interval(function(){
                MonitorFactory.metrics().success(function(data){
                    g1.refresh(data['cpu.usage']);
                })
            }, 2000);

            var g2 = new JustGage({
                id: 'ram_gauge',
                value: 0,
                min: 0,
                max: 100,
                symbol: '%',
                pointer: true,
                pointerOptions: {
                    toplength: -15,
                    bottomlength: 10,
                    bottomwidth: 12,
                    color: '#8e8e93',
                    stroke: '#ffffff',
                    stroke_width: 3,
                    stroke_linecap: 'round'
                },
                width: 300,
                height : 300,
                gaugeWidthScale: 0.6,
                counter: true
            });
            $scope.memoryInterval = $interval(function(){
                MonitorFactory.metrics().success(function(data){
                    g2.refresh(data['ram.usage']);
                })
            }, 2000);
        }

        $scope.$on('$locationChangeStart', function(){
            $interval.cancel($scope.memoryInterval);
            $interval.cancel($scope.cpuInterval);
            $interval.cancel($scope.diskInterval);
        });
    })
    .factory('MonitorFactory', function($http){
        return {
            metrics : function(){
                return $http.get("/metrics");
            }
        }
    });