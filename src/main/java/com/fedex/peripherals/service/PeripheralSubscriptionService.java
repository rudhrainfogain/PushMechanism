package com.fedex.peripherals.service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fedex.common.cxs.dto.CXSEnvelope;
import com.fedex.peripherals.model.PeripheralServerDetailsDTO;
import com.fedex.peripherals.model.PushMechanismSubscriptionType;
import com.fedex.peripherals.model.SubscriptionRequest;
import com.fedex.peripherals.model.SubscriptionRequestDTO;
import com.fedex.peripherals.model.SubscriptionStatusResponseDTO;

@Service
public class PeripheralSubscriptionService {

    public String getperipheralServiceInformationURL(String subscriptionType) {
        URI subscribeURL = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
        String peripheralServiceHealthURL = subscribeURL.toString()
                        .substring(0, subscribeURL.toString().lastIndexOf('/'));
        peripheralServiceHealthURL = subscriptionType.equalsIgnoreCase("PERIPHERALS_HEALTH")
                        ? peripheralServiceHealthURL.concat("/health")
                        : peripheralServiceHealthURL.concat("/connectedperipherals");
        InetAddress peripheralServiceAddresses[] = null;
        try {
            peripheralServiceAddresses = InetAddress.getAllByName(InetAddress.getLocalHost().getCanonicalHostName());
            peripheralServiceAddresses = Arrays.stream(peripheralServiceAddresses)
                            .filter(peripheralServiceAddress -> peripheralServiceAddress instanceof Inet4Address)
                            .toArray(size -> new InetAddress[size]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (peripheralServiceAddresses != null) {
            peripheralServiceHealthURL = peripheralServiceHealthURL.replace("localhost",
                            peripheralServiceAddresses[peripheralServiceAddresses.length - 1].getHostAddress());
        }
        return peripheralServiceHealthURL;
    }

    public SubscriptionRequestDTO generateSubscriptionPayload(PeripheralServerDetailsDTO peripheralServerDetails) {
        String peripheralServiceHealthURL = getperipheralServiceInformationURL(peripheralServerDetails.getSubscriptionType());

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setSubscriptionType(
                        PushMechanismSubscriptionType.valueOf(peripheralServerDetails.getSubscriptionType()));
        subscriptionRequest.setSubscriptionURL(peripheralServiceHealthURL);

        SubscriptionRequestDTO peripheralSubscriptionInfo = new SubscriptionRequestDTO();
        peripheralSubscriptionInfo.setSubscriptionRequest(subscriptionRequest);

        return peripheralSubscriptionInfo;
    }

    public String generateServerSubscriptionURL(String ipAndPort) {
        return ipAndPort.concat("/peripherals/fedexoffice/v1/subscriptions");
    }

    public ResponseEntity<CXSEnvelope<SubscriptionStatusResponseDTO>> hitPeripheralSeverUsingRestTemplate(
                    PeripheralServerDetailsDTO peripheralServerDetails, String subscribeOrUnsubscribe) {

        ResponseEntity<CXSEnvelope<SubscriptionStatusResponseDTO>> subscriptionstatusResponseEnvelope;
        SubscriptionRequestDTO peripheralSubscriptionInfo = generateSubscriptionPayload(peripheralServerDetails);
        String peripheralServerUrl = generateServerSubscriptionURL(peripheralServerDetails.getIpAndPort());
        ParameterizedTypeReference<CXSEnvelope<SubscriptionStatusResponseDTO>> responseType =
                        new ParameterizedTypeReference<CXSEnvelope<SubscriptionStatusResponseDTO>>() {};
        HttpEntity<SubscriptionRequestDTO> request = new HttpEntity<SubscriptionRequestDTO>(peripheralSubscriptionInfo);
        RestTemplate subscriberToSever = new RestTemplate();
        if (subscribeOrUnsubscribe.equals("subscribe")) {
            subscriptionstatusResponseEnvelope =
                            subscriberToSever.exchange(peripheralServerUrl, HttpMethod.POST, request, responseType);
        } else {
            subscriptionstatusResponseEnvelope =
                            subscriberToSever.exchange(peripheralServerUrl, HttpMethod.DELETE, request, responseType);
        }
        return subscriptionstatusResponseEnvelope;
    }
}
