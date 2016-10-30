angular.module("app")
    .controller("statsController",
    function ($scope, $interval, StatsFactory) {

        init();

        function init() {
            StatsFactory.assetStats().success(function (data) {
                var chart = {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false
                };
                var tooltip = {
                    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                };
                var plotOptions = {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: false
                        },
                        showInLegend: true
                    }
                };
                var temp_data = data;
                var series = [
                    {
                        type: 'pie',
                        name: '',
                        data: temp_data
                    }
                ];

                var json = {};
                json.chart = chart;
                json.title = "";
                json.tooltip = tooltip;
                json.series = series;
                json.plotOptions = plotOptions;
                $('#container').highcharts(json);
            });









            barChart();

        }

        function barChart(){
            var chart = {
                type: 'bar'
            };
            var xAxis = {
                categories: ['Q1(Jan-Mar)', 'Q2(Apr-Jun)', 'Q3(Jul-Sep)', 'Q4(Oct-Dec)'],
                title: {
                    text: null
                }
            };
            var yAxis = {
                min: 0,
                title: {
                    text: 'Number of Incidents',
                    align: 'high'
                },
                labels: {
                    overflow: 'justify'
                }
            };
            var tooltip = {
                valueSuffix: ''
            };
            var plotOptions = {
                bar: {
                    dataLabels: {
                        enabled: true
                    }
                }
            };
            var legend = {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -40,
                y: 100,
                floating: true,
                borderWidth: 1,
                backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
                shadow: true
            };
            var credits = {
                enabled: false
            };

            var series= [{
                name: 'Approved',
                data: [107, 31, 635, 203]
            }, {
                name: 'Not Approved',
                data: [133, 156, 947, 408]
            }, {
                name: 'Attention Required',
                data: [973, 914, 4054, 732]
            }, {
                name: 'Pending',
                data: [973, 914, 4054, 732]
            }, {
                name: 'Sent',
                data: [973, 914, 4054, 732]
            }
            ];

            var json = {};
            json.chart = chart;
            json.tooltip = tooltip;
            json.xAxis = xAxis;
            json.yAxis = yAxis;
            json.series = series;
            json.plotOptions = plotOptions;
            json.legend = legend;
            json.credits = credits;
            $('#container2').highcharts(json);
        }
    })
    .factory('StatsFactory', function ($http, $cookieStore) {
        return {
            assetStats: function () {
                $http.defaults.headers.common.Authorization = $cookieStore.get('token');
                $http.defaults.headers.common.Username = $cookieStore.get('Username');
                return $http.get("/inventory/statistics/assetStats");
            }
        }
    });