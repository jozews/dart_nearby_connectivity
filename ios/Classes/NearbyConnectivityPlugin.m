#import "NearbyConnectivityPlugin.h"
#import <nearby_connectivity/nearby_connectivity-Swift.h>

@implementation NearbyConnectivityPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftNearbyConnectivityPlugin registerWithRegistrar:registrar];
}
@end
