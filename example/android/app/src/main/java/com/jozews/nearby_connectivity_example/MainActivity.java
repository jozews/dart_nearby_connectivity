
package com.jozews.chessumbrella;

import android.os.Bundle;

import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.*;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.*;

import java.util.ArrayList;
import java.util.HashMap;


class LifecycleManager extends ConnectionLifecycleCallback implements EventChannel.StreamHandler {

  private MainActivity  activity;

  LifecycleManager(MainActivity activity) {
    this.activity = activity;
  }

  private EventChannel.EventSink eventSink;

  @Override
  public void onConnectionInitiated(String idEndpoint, ConnectionInfo connectionInfo) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", 0);
    map.put("id_endpoint", idEndpoint);
    map.put("name_endpoint", connectionInfo.getEndpointName());
    eventSink.success(map);
  }

  @Override
  public void onConnectionResult(String idEndpoint, ConnectionResolution connectionResolution) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", 1);
    map.put("id_endpoint", idEndpoint);
    map.put("accepted", connectionResolution.getStatus().getStatusCode() == 0); // * NEEDS DEBUGGING
    eventSink.success(map);
  }

  @Override
  public void onDisconnected(String idEndpoint) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", 2);
    map.put("id_endpoint", idEndpoint);
    eventSink.success(map);
  }

  @Override
  public void onListen(Object o, EventChannel.EventSink eventSink) {
    this.eventSink = eventSink;
    activity.onListen(this, o);
  }

  @Override
  public void onCancel(Object o) {
    this.eventSink = null;
    activity.onCancel(this, o);
  }
}


class DiscoveryManager extends EndpointDiscoveryCallback implements EventChannel.StreamHandler {

  private MainActivity  activity;

  DiscoveryManager(MainActivity activity) {
    this.activity = activity;
  }

  private EventChannel.EventSink eventSink;

  @Override
  public void onEndpointFound(String idEndpoint, DiscoveredEndpointInfo discoveredEndpointInfo) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", 0);
    map.put("id_endpoint", idEndpoint);
    map.put("name_endpoint", discoveredEndpointInfo.getEndpointName());
    eventSink.success(map);
  }

  @Override
  public void onEndpointLost(String idEndpoint) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", 1);
    eventSink.success(map);
  }

  @Override
  public void onListen(Object o, EventChannel.EventSink eventSink) {
    this.eventSink = eventSink;
    activity.onListen(this, o);
  }

  @Override
  public void onCancel(Object o) {
    this.eventSink = null;
    activity.onCancel(this, o);
  }
}


class PayloadManager extends PayloadCallback implements EventChannel.StreamHandler {

  private MainActivity  activity;

  PayloadManager(MainActivity activity) {
    this.activity = activity;
  }

  private EventChannel.EventSink eventSink;

  @Override
  public void onPayloadReceived(String idEndpoint, Payload payload) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", 0);
    map.put("bytes", payload.asBytes());
    eventSink.success(map);
  }

  @Override
  public void onPayloadTransferUpdate(String idEndpoint, PayloadTransferUpdate payloadTransferUpdate) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", 1);
    map.put("total_bytes", payloadTransferUpdate.getTotalBytes());
    map.put("total_bytes_transferred", payloadTransferUpdate.getBytesTransferred());
    map.put("status", payloadTransferUpdate.getStatus());
    eventSink.success(map);
  }

  @Override
  public void onListen(Object o, EventChannel.EventSink eventSink) {
    this.eventSink = eventSink;
    activity.onListen(this, o);
  }

  @Override
  public void onCancel(Object o) {
    this.eventSink = null;
    activity.onCancel(this, o);
  }
}



public class MainActivity extends FlutterActivity {


  static String nameChannelStartAdvertising = "nearby-start-advertising";
  static String nameChannelStartDiscovering = "nearby-start-discovering";
  static String nameChannelRequestConnection = "nearby-request-connection";
  static String nameChannelAcceptConnection = "nearby-accept-connection";
  static String nameChannelMethod = "nearby-method";

  static LifecycleManager handlerStartAdvertising;
  static DiscoveryManager handlerStartDiscovery;
  static LifecycleManager handlerRequestConnection;
  static PayloadManager handlerAcceptConnection;

  static String name;
  static String idService;


  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    GeneratedPluginRegistrant.registerWith(this);

    handlerStartAdvertising = new LifecycleManager(this);
    new EventChannel(getFlutterView(), nameChannelStartAdvertising).setStreamHandler(handlerStartAdvertising);

    handlerStartDiscovery = new DiscoveryManager(this);
    new EventChannel(getFlutterView(), nameChannelStartDiscovering).setStreamHandler(handlerStartDiscovery);

    handlerRequestConnection = new LifecycleManager(this);
    new EventChannel(getFlutterView(), nameChannelRequestConnection).setStreamHandler(handlerRequestConnection);

    handlerAcceptConnection = new PayloadManager(this);
    new EventChannel(getFlutterView(), nameChannelAcceptConnection).setStreamHandler(handlerAcceptConnection);

    MainActivity activity = this;

    new MethodChannel(getFlutterView(), nameChannelMethod).setMethodCallHandler(
            new MethodCallHandler() {
              @Override
              public void onMethodCall(MethodCall call, Result result) {
                activity.onMethodCall(call, result);
              }
            });
  }

  void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("sendPayload")) {
      sendPayload(call.arguments);
      result.success(true);
    }
    else {
      result.error("Method not implemented", null, null);
    }
  }

  void onListen(EventChannel.StreamHandler handler, Object objArgs) {
    if (handler.equals(handlerStartAdvertising)) {
      startAdvertising(objArgs);
    }
    if (handler.equals(handlerStartDiscovery)) {
      startDiscovering(objArgs);
    }
    if (handler.equals(handlerRequestConnection)) {
      requestConnection(objArgs);
    }
    if (handler.equals(handlerAcceptConnection)) {
      acceptConnection(objArgs);
    }
  }

  void onCancel(EventChannel.StreamHandler handler, Object o) {
    // nothing to see here
  }

  void startAdvertising(Object objArgs) {
    ArrayList<Object> args = (ArrayList<Object>)objArgs;
    name = (String)args.get(0);
    idService = (String)args.get(1);
    AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
    Nearby.getConnectionsClient(this).startAdvertising(name, idService, handlerStartAdvertising, advertisingOptions);
  }

  void startDiscovering(Object objArgs) {
    ArrayList<Object> args = (ArrayList<Object>)objArgs;
    idService = (String)args.get(0);
    DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
    Nearby.getConnectionsClient(this).startDiscovery(idService, handlerStartDiscovery, discoveryOptions);
  }

  void requestConnection(Object objArgs) {
    ArrayList<Object> args = (ArrayList<Object>)objArgs;
    String idEndpoint = (String)args.get(0);
    Nearby.getConnectionsClient(this).requestConnection(name, idEndpoint, handlerRequestConnection);
  }

  void acceptConnection(Object objArgs) {
    ArrayList<Object> args = (ArrayList<Object>)objArgs;
    String idEndpoint = (String)args.get(0);
    Nearby.getConnectionsClient(this).acceptConnection(idEndpoint, handlerAcceptConnection);
  }

  void sendPayload(Object objArgs) {
    ArrayList<Object> array = (ArrayList<Object>)objArgs;
    String idEndpoint = (String)array.get(0);
    byte[] bytes = (byte[])array.get(1);
    Payload payloadBytes = Payload.fromBytes(bytes);
    Nearby.getConnectionsClient(this).sendPayload(idEndpoint, payloadBytes);
  }
}













