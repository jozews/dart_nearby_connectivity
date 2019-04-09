
import 'dart:typed_data';
import 'dart:async';

import 'package:flutter/services.dart';


enum TypeLifecycle {
  initiated, result, disconnected
}

class Lifecycle {

  TypeLifecycle type;
  String idEndpoint;
  String nameEndpoint;
  String idService;

  Lifecycle(this.type, this.idEndpoint, this.nameEndpoint, this.idService);

  Lifecycle.fromMap(Map map) {
    this.type = TypeLifecycle.values[map["type"]];
    this.idEndpoint = map["id_endpoint"];
    this.nameEndpoint = map["name_endpoint"];
    this.idService = map["id_service"];
  }
}


enum TypeDiscovery {
  found, lost
}

class Discovery {

  TypeDiscovery type;
  String idEndpoint;
  String nameEndpoint;
  bool accepted;

  Discovery(this.type, this.idEndpoint, this.nameEndpoint, this.accepted);

  Discovery.fromMap(Map map) {
    this.type = TypeDiscovery.values[map["type"]];
    this.idEndpoint = map["id_endpoint"];
    this.nameEndpoint = map["name_endpoint"];
    this.accepted = map["accepted"];
  }
}


enum TypePayload {
  received, transferred
}

class Payload {

  TypePayload type;
  Uint8List bytes;
  int countTotalBytes;
  int countBytesTransferred;
  int status;

  Payload(this.type, this.bytes, this.countTotalBytes, this.countBytesTransferred, this.status);

  Payload.fromMap(Map map) {
    this.type = TypePayload.values[map["type"]];
    if (map["bytes"] != null) {
      this.bytes = Uint8List.fromList(map["bytes"]);
    }
    this.countTotalBytes = map["count_total_bytes"];
    this.countBytesTransferred = map["count_bytes_transferred"];
    this.status = map["status"];
  }
}


class NearbyConnectivity {

  static var channelStartAdvertising = EventChannel("nearby-start-advertising");
  static var channelStartDiscovering = EventChannel("nearby-start-discovering");
  static var channelRequestConnection = EventChannel("nearby-request-connection");
  static var channelAcceptConnection = EventChannel("nearby-accept-connection");
  static var channelMethod = MethodChannel("nearby-method");

  static Stream<Lifecycle> startAdvertising({String name, String idService}) {
    var controller = StreamController<Lifecycle>();
    try {
      channelStartAdvertising.receiveBroadcastStream([name, idService]).listen((event) {
        var advertise = Lifecycle.fromMap(event);
        controller.add(advertise);
      });
    } on PlatformException catch (e) {
      print(e);
    }
    return controller.stream;
  }

  static Stream<Discovery> startDiscovering({String idService}) {
    var controller = StreamController<Discovery>();
    try {
      channelStartDiscovering.receiveBroadcastStream([idService]).listen((event) {
        var discovery = Discovery.fromMap(event);
        controller.add(discovery);
      });
    } on PlatformException catch (e) {
      print(e);
    }
    return controller.stream;
  }

  static Stream<Lifecycle> requestConnection({String idEndpoint}) {
    var controller = StreamController<Lifecycle>();
    try {
      channelRequestConnection.receiveBroadcastStream([idEndpoint]).listen((event) {
        var connection = Lifecycle.fromMap(event);
        controller.add(connection);
      });
    } on PlatformException catch (e) {
      print(e);
    }
    return controller.stream;
  }

  static Stream<Payload> acceptConnection({String idEndpoint}) {
    var controller = StreamController<Payload>();
    try {
      channelAcceptConnection.receiveBroadcastStream([idEndpoint]).listen((event) {
        var payload = Payload.fromMap(event);
        controller.add(payload);
      });
    } on PlatformException catch (e) {
      print(e);
    }
    return controller.stream;
  }

  static Future sendPayloadBytes({String idEndpoint, Uint8List bytes}) {
    var completer = new Completer();
    try {
      channelMethod.invokeMethod("sendPayload", [idEndpoint, bytes]).then((event) {
        completer.complete();
      });
    } on PlatformException catch (e) {
      print(e);
    }
    return completer.future;
  }
}