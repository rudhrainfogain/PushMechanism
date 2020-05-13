package com.fedex.peripherals.controller;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fedex.common.cxs.dto.CXSEnvelope;
import com.fedex.peripherals.model.ConnectedPeripheralsResponse;
import com.fedex.peripherals.model.PeripheralInformationReceievedStatusDTO;
import com.fedex.peripherals.model.PeripheralHealthResponse;
import com.fedex.peripherals.model.PeripheralInformation;
import com.fedex.peripherals.model.PeripheralServerDetailsDTO;
import com.fedex.peripherals.model.SubscriptionStatusResponse;
import com.fedex.peripherals.model.SubscriptionStatusResponseDTO;
import com.fedex.peripherals.service.PeripheralSubscriptionService;

@Controller
@RequestMapping("/peripherals/fedexoffice/v1")
public class PeripheralSubscriptionController {

    private static final String RECEIVED_EMPTY = "Received empty";

    private static final String RECEIVED_SUCCESSFULLY = "Received successfully at ";
    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    private static Logger logger = LogManager.getLogger(PeripheralSubscriptionController.class);

    @Autowired
    PeripheralSubscriptionService peripheralSubscriptionService;

    PeripheralInformation deviceInfo = new PeripheralInformation();

    @PostMapping(value = "/subscribe", consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CXSEnvelope<SubscriptionStatusResponseDTO>> subscribeToPeripheralServerForHealthOrConnectedDevices(
                    @RequestBody PeripheralServerDetailsDTO peripheralServerDetails) {
        ResponseEntity<CXSEnvelope<SubscriptionStatusResponseDTO>> subscriptionstatusResponse =
                        peripheralSubscriptionService.hitPeripheralSeverUsingRestTemplate(peripheralServerDetails,
                                        "subscribe");
        return ResponseEntity.status(subscriptionstatusResponse.getStatusCode())
                        .body(CXSEnvelope.success(subscriptionstatusResponse.getBody().getOutput()));
    }

    @PostMapping(value = "/unsubscribe", consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CXSEnvelope<SubscriptionStatusResponseDTO>> unsubscribeFromPeripheralServerForHealthOrConnectedDevices(
                    @RequestBody PeripheralServerDetailsDTO peripheralServerDetails) {
        if (peripheralServerDetails.getSubscriptionType().equals("PERIPHERALS_HEALTH")) {
            deviceInfo.setPeripheralHealthInfo(null);
        } else {
            deviceInfo.setConnectedPeripheralsInfo(null);
        }
        ResponseEntity<CXSEnvelope<SubscriptionStatusResponseDTO>> subscriptionstatusResponse =
                        peripheralSubscriptionService.hitPeripheralSeverUsingRestTemplate(peripheralServerDetails,
                                        "Unsubscribe");
        if (subscriptionstatusResponse.getStatusCode().equals(HttpStatus.NO_CONTENT)
                        || subscriptionstatusResponse.getBody() == null) {
            return ResponseEntity.status(subscriptionstatusResponse.getStatusCode()).body(CXSEnvelope.success(
                            new SubscriptionStatusResponseDTO(new SubscriptionStatusResponse("Can not Unsubscribe"))));
        } else {
            return ResponseEntity.status(subscriptionstatusResponse.getStatusCode())
                            .body(CXSEnvelope.success(subscriptionstatusResponse.getBody().getOutput()));
        }
    }

    @PostMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CXSEnvelope<PeripheralInformationReceievedStatusDTO>> getPeripheralHealthInfo(
                    @RequestBody PeripheralHealthResponse peripheralHealthResponse) {
        PeripheralInformationReceievedStatusDTO peripheralServiceHealthReceievedDTO =
                        new PeripheralInformationReceievedStatusDTO();

        if (null != peripheralHealthResponse) {
            deviceInfo.setPeripheralHealthInfo(peripheralHealthResponse);
            logger.info("recieved Health Information: {}", peripheralHealthResponse);

            peripheralServiceHealthReceievedDTO.setRecievedStatus(RECEIVED_SUCCESSFULLY);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                            CXSEnvelope.success(new PeripheralInformationReceievedStatusDTO(RECEIVED_SUCCESSFULLY)));

        } else {
            peripheralServiceHealthReceievedDTO.setRecievedStatus(RECEIVED_EMPTY);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(CXSEnvelope.success(new PeripheralInformationReceievedStatusDTO(RECEIVED_EMPTY)));
        }
    }

    @PostMapping(value = "/connectedperipherals", produces = MediaType.APPLICATION_JSON_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CXSEnvelope<PeripheralInformationReceievedStatusDTO>> getConnectedPeripheralInfo(
                    @RequestBody ConnectedPeripheralsResponse connectedPeripheralsResponse) {
        try {
            int nextInt = new Random().nextInt(10) + 10;
            logger.info("waiting for " + nextInt + " seconds.");
            TimeUnit.SECONDS.sleep(nextInt);
        } catch (InterruptedException e) {
            logger.info("waiting failed.");
        }
        if (null != connectedPeripheralsResponse && connectedPeripheralsResponse.getConnectedPeripherals() != null) {
            deviceInfo.setConnectedPeripheralsInfo(connectedPeripheralsResponse);
            logger.info("recieved Connected Peripherals Information : {}", connectedPeripheralsResponse);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                            .body(CXSEnvelope.success(new PeripheralInformationReceievedStatusDTO(
                                            RECEIVED_SUCCESSFULLY + df.format(new Date()))));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(CXSEnvelope.success(new PeripheralInformationReceievedStatusDTO(RECEIVED_EMPTY)));
        }
    }

    @PostMapping(value = "/displayinfo", consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CXSEnvelope<PeripheralInformation>> displayDeviceInfo() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(CXSEnvelope.success(deviceInfo));
    }
}
