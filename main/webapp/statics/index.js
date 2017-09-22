var log_level = {};

log_level.graceInit = function () {
    angular.module('grace.bootstrap.loglevel', ['grace.bootstrap'], angular.injector(['grace.bootstrap.bootConfigs']).get('bootConfig'));
    angular.module('grace.bootstrap.loglevel').controller('loglevelCtrl', ["$scope", "$http", "$gDataAdapterFactory", "$gDataAdapterModelManagerFactory", function ($scope, $http, dataAdapter, ModelManager) {
        $scope.saveShow = false;

        //默认选中root
        $scope.loglevelForm = {};

        //默认获取root的level
        $scope.initLevel = function () {
            console.log("============initLevel=============" + JSON.stringify($scope.loglevelForm));
            $.ajax({
                method: "post",
                dataType: "json",
                url: "/mobile_sys/loglevel/switchApp.ajax",
                data: $scope.loglevelForm,
                beforeSend: function () {
                },
                success: function (data) {
                    console.log("=============initLevel========result=======" + JSON.stringify(data));

                    if (data.code == 0) {

                        //$scope.loglevelForm.level = data.level;
                        // $scope.loglevelForm.domain = data.domain;
                        //$scope.loglevelForm.package = data.logLevelList[0];

                        $scope.logLevelList = data.logLevelList;
                        $scope.appList = data.appList;
                       // $scope.loglevelForm.domain = $scope.appList[1];

                        $scope.loglevelForm.level = null;
                        $scope.loglevelForm.package = null;
                    } else {
                        console.log(data.msg);
                    }
                    $scope.$apply();
                }
            });
        };

        $scope.reset = function () {
            $.ajax({
                method: "post",
                dataType: "json",
                url: "/mobile_sys/loglevel/reset.ajax",
                data: $scope.loglevelForm,
                beforeSend: function () {
                },
                success: function (data) {
                    console.log(data);

                    if (data.code == 0) {
                        alert("复位日志等级成功!");
                        location.reload();
                    } else {
                        alert(data.msg);
                    }
                }
            });
        };

        $scope.save = function () {
            console.log("============saveLogLevel=============" + JSON.stringify($scope.loglevelForm));

            $.ajax({
                method: "post",
                dataType: "json",
                url: "/mobile_sys/loglevel/setUpLog4jOutputLevel.ajax",
                data: $scope.loglevelForm,
                beforeSend: function () {
                },
                success: function (data) {
                    console.log(data);

                    if (data.code == 0) {
                        alert("修改日志等级成功!");
                    } else {
                        alert(data.msg);
                    }
                }
            });

        };

        $scope.lookfor = function () {
            console.log("============lookfor=============" + JSON.stringify($scope.loglevelForm));

            $.ajax({
                method: "post",
                dataType: "json",
                url: "/mobile_sys/loglevel/lookfor.ajax",
                data: $scope.loglevelForm,
                beforeSend: function () {
                },
                success: function (data) {
                    console.log(data);

                    if (data.code == 0) {
                        $scope.loglevelForm.level = data.level;
                    } else {
                        alert(data.msg);
                    }
                    $scope.$apply();
                }
            });

        };
        $scope.initLevel();
        grace_scope = $scope;

    }]);

    angular.bootstrap(document, ['grace.bootstrap.loglevel']);

};


$(document).ready(function () {
    log_level.graceInit();
    //$("#domainId").select2();
    //$("#single2").select2();
    //log_level.graceInit();
});