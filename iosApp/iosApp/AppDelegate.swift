import UIKit
import UserNotifications
// import FirebaseCore       // Uncomment after adding Firebase via SPM/CocoaPods
// import FirebaseAnalytics  // Uncomment after adding Firebase via SPM/CocoaPods

class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {

        // Initialize Firebase (uncomment after adding Firebase SDK)
        // FirebaseApp.configure()

        // Request notification permissions
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("Notification permission error: \(error)")
            } else {
                print("Notification permission granted: \(granted)")
            }
        }

        // Observe analytics events posted from Kotlin via NSNotificationCenter
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleAnalyticsEvent(_:)),
            name: NSNotification.Name("LumiAnalyticsEvent"),
            object: nil
        )

        return true
    }

    @objc private func handleAnalyticsEvent(_ notification: Notification) {
        guard let userInfo = notification.userInfo,
              let eventName = userInfo["event_name"] as? String else {
            return
        }

        let parameters = userInfo["parameters"] as? [String: Any]

        // Forward to Firebase Analytics (uncomment after adding Firebase SDK)
        // Analytics.logEvent(eventName, parameters: parameters)

        // Debug logging (can be removed in production)
        if let parameters = parameters {
            print("[Firebase Bridge] \(eventName) | \(parameters)")
        } else {
            print("[Firebase Bridge] \(eventName)")
        }
    }
}
