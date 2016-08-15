/**
TODO: figure out a more angular-esque approach here.
We're subverting the angular design here, but I need to force some updates from java =/
*/

var selectedScript = "(None)";

function loadScript() {
    selectedScript =  document.getElementById('scriptSelect').value;

    // TODO: constants plz
    // preset script display to prevent any lag waiting for server response
    if (selectedScript == "(None)") {
        updateScriptStatus("NO SCRIPT LOADED");
    } else {
        updateScriptStatus("READY");
    }

    javaServant.loadScript(selectedScript);
}

function populateScriptList() {
    alert('populateScriptList');
    selectDropDown =  document.getElementById('scriptSelect');

    var scriptList = scriptListArray;
    for (var i = 1; i < scriptList.length; i++) {
        // Start at index 1 as "(None)" option already preloaded
        selectDropDown[selectDropDown.length] = new Option(scriptList[i], scriptList[i]);
    }
    alert('populateScriptList done!');
}

function populateVariableList() {
    alert('populateVarList');
    alert(variableMapList);

    var controllerScope = getAngularControllerScope();
    controllerScope.updateScriptVars(JSON.parse(variableMapList));
    alert('populateVarList done!');
}

function updateScriptStatus(newStatus) {
    var controllerScope = getAngularControllerScope();
    controllerScope.updateScriptStatus(newStatus);
}

function getAngularControllerScope() {
    var controllerElement = document.querySelector('body');
    var controllerScope = angular.element(controllerElement).scope();
    return controllerScope;
}

//poll for script status changes every 850ms
setInterval(function() {
    javaServant.updateScriptRunState();
}, 850);

