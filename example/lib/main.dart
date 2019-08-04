
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:nearby_connectivity/nearby_connectivity.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {


  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Nearby connectivity'),
        ),
        body: Center(),
      ),
    );
  }

  startAdvertising() {
    NearbyConnectivity.startAdvertising(name: "name", idService: "id").listen((advertise) {
      switch (advertise.type) {
        case TypeLifecycle.initiated:
          acceptConnection(advertise);
          // you are now connected
          break;
        case TypeLifecycle.result:
          break;
        case TypeLifecycle.disconnected:
        // you are now disconnected
          break;
      }
    });
  }

  startDiscovering() {
    NearbyConnectivity.startDiscovering(idService: "id").listen((discovery) {
      switch (discovery.type) {
        case TypeDiscovery.found:
          requestConnection(discovery);
          break;
        case TypeDiscovery.lost:
          break;
      }
    });
  }

  requestConnection(Discovery discovery) {
    NearbyConnectivity.requestConnection(idEndpoint: discovery.idEndpoint).listen((lifecycle) {
      switch (lifecycle.type) {
        case TypeLifecycle.initiated:
          acceptConnection(lifecycle);
          // you are now connected
          break;
        case TypeLifecycle.result:
          break;
        case TypeLifecycle.disconnected:
        // you are now disconnected
          break;
      }
    });
  }

  acceptConnection(Lifecycle advertise) {
    NearbyConnectivity.acceptConnection(idEndpoint: advertise.idEndpoint).listen((payload) {
      switch (payload.type) {
        case TypePayload.received:
        // payload received
          break;
        case TypePayload.transferred:
        // payload being transferred
          break;
      }
    });
  }
}
