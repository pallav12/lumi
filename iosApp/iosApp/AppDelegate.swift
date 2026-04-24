import UIKit
import UserNotifications
import StoreKit
import ComposeApp
import FirebaseCore
import FirebaseAnalytics

class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {

        FirebaseApp.configure()

        // Initialize RevenueCat via Kotlin bridge (reads key from Info.plist)
        let rcKey = Bundle.main.object(forInfoDictionaryKey: "REVENUECAT_IOS_KEY") as? String ?? ""
        IOSInitializerKt.initializeLumi(revenueCatApiKey: rcKey)

        // Register StoreKit review handler — Kotlin triggers this after Void burn / Orbit completion
        IOSReviewManager.shared.setHandler {
            DispatchQueue.main.async {
                guard let scene = UIApplication.shared.connectedScenes
                    .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene else {
                    return
                }
                SKStoreReviewController.requestReview(in: scene)
            }
        }

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

        Analytics.logEvent(eventName, parameters: parameters)

        if let parameters = parameters {
            print("[Firebase] \(eventName) | \(parameters)")
        } else {
            print("[Firebase] \(eventName)")
        }
    }
}
