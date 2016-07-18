var selectedScript = "(None)";

function loadScript() {
    selectedScript =  document.getElementById('scriptSelect').value;
    javaServant.loadScript(selectedScript);
}

function populateScriptList() {
    alert('populateScriptList');
    selectDropDown =  document.getElementById('scriptSelect');

    var scriptList = scriptListArray;//["test.txt", "test2.txt"];
    for (var i = 1; i < scriptList.length; i++) {
        // Start at index 1 as "(None)" option already preloaded
        selectDropDown[selectDropDown.length] = new Option(scriptList[i], scriptList[i]);
    }
    alert('populateScriptList done!');
}

/**
TODO: figure out a more angular-esque approach here.
We're subverting the angular design here, but I need to force this update from java =/
*/
function updateScriptStatus(newStatus) {
    var controllerElement = document.querySelector('body');
    var controllerScope = angular.element(controllerElement).scope();
    controllerScope.updateScriptStatus(newStatus);
}