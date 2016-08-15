var app = angular.module('JServant', []);
app.controller('JServantCtrl', ['$scope', '$window', function($scope, $window) {
    // ------ CONSTANTS -----
    //TODO: get this list from backend
    var RUN_STATES = ["NO SCRIPT LOADED", "RUNNING", "SUCCEEDED", "FAILED", "STOPPED", "READY"];
    var NO_SCRIPT =  RUN_STATES[0];
    var RUNNING =    RUN_STATES[1];;
    var SUCCEEDED =  RUN_STATES[2];
    var FAILED =     RUN_STATES[3];
    var STOPPED =    RUN_STATES[4];
    var READY =      RUN_STATES[5];

    var STATE_NOT_RUNNING = "notRunning";
    var STATE_RUNNING = "running";
    var STATE_ERROR_RUNNING = "errorRunning";
    var STATE_SUCCESS_RUNNING = "successRunning";

    $scope.NONE_SELECTED_TEXT = "(None)";
    // ----------------------

    $scope.currentScriptStatus = NO_SCRIPT;
    $scope.currentScriptName = $scope.NONE_SELECTED_TEXT;
    $scope.vars = [];

    $scope.runningState = function() {
        var currState = $scope.currentScriptStatus;
        if (currState == NO_SCRIPT || currState == READY) {
            return STATE_NOT_RUNNING;
        }
        if (currState == RUNNING) {
            return STATE_RUNNING;
        }
        if (currState == FAILED || currState == STOPPED) {
            return STATE_ERROR_RUNNING;
        }
        if (currState == SUCCEEDED) {
            return STATE_SUCCESS_RUNNING;
        }
        return NO_SCRIPT;
    };

    $scope.updateScriptStatus = function(newStatus) {
        $scope.$apply(function() {
            $scope.currentScriptStatus = newStatus;
        });
    };

    $scope.updateScriptVars = function(newScriptVars) {
        $scope.$apply(function() {
            $scope.vars = newScriptVars;
        });
    };

    $scope.runScript = function() {
        /* preset script display to prevent any lag waiting for server response
           server will correct us if we're somehow wrong
        */
        if ($scope.currentScriptStatus != NO_SCRIPT) {
            $scope.currentScriptStatus = RUNNING;
        }

        $scope.vars.forEach(function(item, index) {
            alert(item.name + ":" + item.value);
            $window.javaServant.updateVariableMap(item.name, item.value);
        });

        $window.javaServant.runScript();
    };

    $scope.stopScript = function() {
        /* preset script display to prevent any lag waiting for server response
           server will correct us if we're somehow wrong
        */
        if ($scope.currentScriptStatus == RUNNING) {
            $scope.currentScriptStatus = STOPPED;
        }
        $window.javaServant.stopScript();
    };

}]);