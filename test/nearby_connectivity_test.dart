
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:nearby_connectivity/nearby_connectivity.dart';

void main() {

  const MethodChannel channel = MethodChannel('nearby_connectivity');

  setUp(() {

  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

}
