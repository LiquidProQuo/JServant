var app = angular.module('JServant', []);
app.controller('JServantCtrl', function($scope) {

    $scope.RUN_STATES = ["Not Running", "Running", "Succeeded", "Failed", "Stopped", "Ready"];

    $scope.currentScriptStatus = $scope.RUN_STATES[0];
    $scope.currentScriptName = "(None)";

    $scope.runningState = function() {
        var currState = $scope.currentScriptStatus;
        if (currState == $scope.RUN_STATES[0] || currState == $scope.RUN_STATES[5]) {
            return "notRunning";
        }
        if (currState == $scope.RUN_STATES[1]) {
            return "running";
        }
        if (currState == $scope.RUN_STATES[2] || currState == $scope.RUN_STATES[4]) {
            return "errorRunning";
        }
        if (currState == $scope.RUN_STATES[3]) {
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