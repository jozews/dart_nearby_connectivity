A Flutter plugin built on top of [Nearby Connections](https://developers.google.com/nearby/connections/overview) and [Multipeer Connectivity](https://developer.apple.com/documentation/multipeerconnectivity).
**iOS side under development**
**Currently only P2P_CLUSTER strategy is supported**

Following example automatically connects two devices.

Start advertising

    NearbyConnectivity.startAdvertising(name: name, idService: idService).listen((FFadvertise) {
      switch (advertise.type) {
        case TypeLifecycle.initiated:
          // accept connection here
          break;
        case TypeLifecycle.result:
          break;
        case TypeLifecycle.disconnected:
            // you are now disconnected
          break;
      }
    });
    
Start discovering

    NearbyConnectivity.startDiscovering(idService: idService).listen((discovery) {
      switch (discovery.type) {
        case TypeDiscovery.found:
          // request connection here
          break;
        case TypeDiscovery.lost:
          break;
      }
    });
    
    
Request connection using the discovery object

    NearbyConnectivity.requestConnection(idEndpoint: discovery.idEndpoint).listen((lifecycle) {
      switch (lifecycle.type) {
        case TypeLifecycle.initiated:
          // accept connection here
          break;
        case TypeLifecycle.result:
          break;
        case TypeLifecycle.disconnected:
          // you are now disconnected
          break;
      }
    });
    
Accept connection using the advertise object

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
    
Send payload
   
    Nearby.sendPayloadBytes(idEndpoint: connection.idEndpoint, bytes: bytesPayload);

