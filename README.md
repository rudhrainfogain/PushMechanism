# RT- Peripheral Server

This server is exposing the retail application peripherals as a service. Following are the applications are in-scope for development:

 - Receipt Printer - EPSON Receipt Printer (H6000II, H6000III, H6000IV and TM88V)
 - Label Printer - Zebra Label Printer (USB and Network), DYMO Label Printer
 - Barcode Scanner - Honeywell XENON 1900 Barcode Scanner, Zebra Barcode Scanner (MT 2070 with JPOS as an interface)
 
Future devices which will be part of development:
 - Cash Drawer
 - Scale
 - Dimensioner

Purpose of exposing the devices as service to allow any application irresepctive written in any platform or any technology, can utilize the peripheral server through REST API's and 
also to allow sharing of devices within multiple applications or services.

Peripheral Server is published at Nexus Repsitory Snapshot as under 

 - Group ID  - eai3530951.com.fedex.peripherals
 - Artifact ID - rtl-peripherals

All peripheral device and JPOS related Jars has been published at Nexus Release Repository as provided below:

 - EPSON Printers - 
    - Group ID - eai3530951.com.fedex.peripherals.epson
    - Artifact ID - epsonlibs

 - JPOS jars - 
    - Group ID - eai3530951.com.fedex.peripherals.jpos
    - Artifact ID - jposlibs

 - Zebra MT2070 Barcode Scanner jars - 
    - Group ID - eai3530951.com.fedex.peripherals.zebra
    - Artifact ID - zebra-scanner-libs

 - Honeywell Barcode Scanner jars - 
    - Group ID - eai3530951.com.fedex.peripherals.honeywell
    - Artifact ID - honeywell-libs

# API - Getting Started

- Make sure you have access to peripehral-service project(https://gitlab.prod.fedex.com/APP3530951/peripheral-service.git)

- Clone the repository locally -- git clone https://gitlab.prod.fedex.com/APP3530951/peripheral-service.git

- Build the JAR file with maven command --  mvn install
