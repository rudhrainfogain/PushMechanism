$(document).ready(function() {
    var protocol = "http://";
    var peripheralInformation = null;
    var myInterval;
    var subscribedInfo = {
    		"PERIPHERALS_HEALTH":false,
    		"CONNECTED_PERIPHERALS":false
    };
    var ajaxCall = function(urlToHit, payload, success, failure) {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: urlToHit,
            data: JSON.stringify(payload),
            dataType: 'json',
            success: success,
            error: failure ? failure : failureCallback
        });
    };
    
    var failureCallback = function(response) {
        var errorMsg = "Error in fetching data";
        if (typeof response == "object" && response.responseText && response.responseText != "error") {
            var errorMsgArray,
                responseText = JSON.parse(response.responseText);

            if (responseText.errors && responseText.errors.length) {
                for (var i = 0; i < responseText.errors.length; i++) {
                    errorMsgArray = "code: " + responseText.errors[i].code + " message: " + responseText.errors[i].message;
                }
                errorMsg = errorMsg + " with error[ " + errorMsgArray + "]";
            }
        } else if (response) {
            errorMsg = errorMsg + " with error: " + response;
        }
        alertModal(errorMsg);
    };
    
    var alertModal = function(message) {
        alert(message);
    };

    var showData = function(message) {
        $("#peripheral-health-info").html(message);
        if (!message) {
        	peripheralInformation = null;
            $("#peripheral-health-outer-div").css("display", "block");
        } else {
            $("#peripheral-health-outer-div").css("display", "none");
        }
    };

    var displaySuccessMessage = function(message) {
        $("#successMessage").css("color", "green");
        $("#successMessage").html(message + "!");
        hideMessage();
    };
	var checkClearInterval = function() {
		var toClear = true;
		for(let check of Object.values(subscribedInfo)){
			  if(check){
				  toClear = false;
				  break;
			  }
		}
		if(toClear){
			clearInterval(myInterval); 
			myInterval = null;
            showData("");
		}
	};
    var convertToTable = function(data) {
    	if(peripheralInformation && data.connectedPeripheralsInfo && JSON.stringify(peripheralInformation.connectedPeripheralsInfo) !== JSON.stringify(data.connectedPeripheralsInfo)){
        	displaySuccessMessage("Connected Peripheral's Information is received");
    	}
    	else if(peripheralInformation && data.peripheralHealthInfo && JSON.stringify(peripheralInformation.peripheralHealthInfo) !== JSON.stringify(data.peripheralHealthInfo)){
        	displaySuccessMessage("Peripheral's Health Information is received");
    	}
        var dataToAppend = "<table><tr><th>Peripherals</th>";
    	if(data.connectedPeripheralsInfo){
    		dataToAppend += "<th>Connected Peripheral's Information</th>";
    	}
    	if(data.peripheralHealthInfo){
    		dataToAppend += "<th>Peripheral's Health Information</th>";
    	}
        dataToAppend += "</tr>";
        var healthData;
        var peripheralsLength = Object.values(data.connectedPeripheralsInfo || data.peripheralHealthInfo)[0].length;
        if(data.peripheralHealthInfo && data.peripheralHealthInfo.peripheralHealths && data.peripheralHealthInfo.peripheralHealths.length){
        	healthData = [...data.peripheralHealthInfo.peripheralHealths];
        }
        else{
        	healthData = [];
        }
        for(var i = 0; i < peripheralsLength; i++){
        	var imageName;
        	if(data.connectedPeripheralsInfo && data.connectedPeripheralsInfo.connectedPeripherals.length > i){
            	imageName = data.connectedPeripheralsInfo.connectedPeripherals[i].type != "LABEL_PRINTER" ? data.connectedPeripheralsInfo.connectedPeripherals[i].type : data.connectedPeripheralsInfo.connectedPeripherals[i].name;
        	}
        	else if(data.peripheralHealthInfo && data.peripheralHealthInfo.peripheralHealths.length > i){
            	imageName = data.peripheralHealthInfo.peripheralHealths[i].peripheralType != "LABEL_PRINTER" ? data.peripheralHealthInfo.peripheralHealths[i].peripheralType : data.peripheralHealthInfo.peripheralHealths[i].peripheralName;
        	}
            dataToAppend += "<tr><td><img alt = "+ imageName +" src='images/" + imageName + ".jpg'></td>";
            if(data.connectedPeripheralsInfo && data.connectedPeripheralsInfo.connectedPeripherals.length && data.peripheralHealthInfo && data.peripheralHealthInfo.peripheralHealths.length){
            	var peripheralName = data.connectedPeripheralsInfo.connectedPeripherals[i].name;
        		dataToAppend += appendObjectData(data.connectedPeripheralsInfo.connectedPeripherals[i]);
	            for(var [index, health] of healthData.entries()){
	            	if(health.peripheralName && health.peripheralName == peripheralName){
	            		dataToAppend += appendObjectData(health);
		            	healthData.splice(index, 1); 
		            	break;
	            	}
	            }
            }
            else{
	            if(data.connectedPeripheralsInfo && data.connectedPeripheralsInfo.connectedPeripherals.length > i) {
	            	dataToAppend += appendObjectData(data.connectedPeripheralsInfo.connectedPeripherals[i]);
	            }
	            if(data.peripheralHealthInfo && data.peripheralHealthInfo.peripheralHealths.length > i) {
	            	dataToAppend += appendObjectData(data.peripheralHealthInfo.peripheralHealths[i]);
	        	}
            }
            dataToAppend += "</tr>";
        }
        if(peripheralsLength == 0){
        	dataToAppend += "<tr><td colspan='100%' style='text-align: center; color:red;'>No Peripherals are connected!</td></tr>";
        }
        dataToAppend += "</table>";
    	peripheralInformation = data;
        return dataToAppend;
    };
	var appendObjectData = function(objectData) {
    	var dataToAppend = "<td>";
		for (var [key, value] of Object.entries(objectData)) {
			if(key && value) {
				dataToAppend = dataToAppend + "<b>" + key + "</b>: " + value + "<br/>";
			}
		}
        dataToAppend += "</td>";
        return dataToAppend;
	};
    var displayErrorMessage = function(message) {
        $("#successMessage").css("color", "red");
        $("#successMessage").html(message);
        hideMessage();
    };

    var hideMessage = function() {
        setTimeout(function() {
            $("#successMessage").html("");
        }, 3000);
    };
    
    var checkIpAndPortAndMakePayload = function() {
        var ip = $('#ip').val();
        var portNumber = $('#portNumber').val();
        if (ip && portNumber) {
            return {
                "ipAndPort": protocol.concat(ip, ":", portNumber),
                "subscriptionType": $('#drop-down').val()
            };
        } else {
            return null;
        }
    };
	
    var getPeripheralHealth = function(seconds) {
    	if(!myInterval){
	    	myInterval = setInterval(function() {
	            var urlToHit = "peripherals/fedexoffice/v1/displayinfo";
	
	            var success = function(response, textStatus, jqXHR) {
	                if (jqXHR.status == 202 && response) {
	                    if (response.output !== null && (response.output.peripheralHealthInfo || response.output.connectedPeripheralsInfo)) {
	                        if(JSON.stringify(peripheralInformation) !== JSON.stringify(response.output)){
	                    		showData(convertToTable(response.output));
	                        }
	                    } else {
	                        showData("");
	                    }
	                } else {
	                    alertModal("response failure");
	                }
	            };
	            
	            ajaxCall(urlToHit, {}, success);
	        }, seconds);
    	}
    };

    $("#subscribe").click(function() {
        var payload = checkIpAndPortAndMakePayload();
        if (payload) {

            var urlToHit = "peripherals/fedexoffice/v1/subscribe";

            var success = function(response,
                textStatus, jqXHR) {
                if (jqXHR.status == 202 &&
                    response) {
                    if (response.output &&
                        response.output.subscription &&
                        response.output.subscription.status) {
                        subscribedInfo[$('#drop-down').val()] = true;
                        displaySuccessMessage(response.output.subscription.status);
                        getPeripheralHealth(1000);
                    } else {
                        alertModal(response);
                    }
                } else {
                    alertModal("response failure");
                }
            };
            
            ajaxCall(urlToHit, payload, success);

        } else {
            alertModal("Please enter IP Address and Port Number");
        }
    });

    $("#unSubscribe").click(function() {
        var payload = checkIpAndPortAndMakePayload();
        if (payload) {
            var urlToHit = "peripherals/fedexoffice/v1/unsubscribe";

            var success = function(response, textStatus, jqXHR) {
                if (jqXHR.status == 204) {
                	subscribedInfo[$('#drop-down').val()] = false;
                	checkClearInterval();
                    displaySuccessMessage("Unsubscribed Successfully");
                } else {
                    alertModal("Can not unsubscribe");
                }
            };

            ajaxCall(urlToHit, payload, success);
        } else {
            alertModal("Please enter IP Address and Port Number");
        }
    });

});