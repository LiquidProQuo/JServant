var app = angular.module('JServant', []);
app.controller('JServantCtrl', function($scope) { //TODO: try using $window for DOM vars

    //TODO: get this list from backend
    $scope.RUN_STATES = ["NO SCRIPT LOADED", "RUNNING", "SUCCEEDED", "FAILED", "STOPPED", "READY"];

    $scope.currentScriptStatus = $scope.RUN_STATES[0];
    $scope.currentScriptName = "(None)";
    $scope.vars = [/*
        {
            name : "server",
            value: "starfish3"
        },
        {
            name : "server2",
            value: "starfish4"
        },
        {
            name : "server899",
            value: "starfish4rewyry"
        },
        {
            name : "serve99",
            value: "starfish4wrwe"
        }
    */];

    $scope.runningState = function() {
        var currState = $scope.currentScriptStatus;
        if (currState == $scope.RUN_STATES[0] || currState == $scope.RUN_STATES[5]) {
            return "notRunning";
        }
        if (currState == $scope.RUN_STATES[1]) {
            return "running";
        }
        if (currState == $scope.RUN_STATES[3] || currState == $scope.RUN_STATES[4]) {
            return "errorRunning";
        }
        if (currState == $scope.RUN_STATES[2]) {
            return "successRunning";
        }
        return $scope.RUN_STATES[0];
    };

    $scope.updateScriptStatus = function(newStatus) {
        $scope.$apply(function() {
            $scope.currentScriptStatus = newStatus;
        });
    };

});