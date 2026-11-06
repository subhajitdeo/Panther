package com.panther.browser

import android.app.Application
import android.util.Log
import org.mozilla.geckoview.ContentBlocking
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.StorageController

class PrivacyBrowserApp : Application() {

    lateinit var runtime: GeckoRuntime
    lateinit var session: GeckoSession

    override fun onCreate() {
        super.onCreate()

        // 1. Forced Dark Mode + Strict Anti-Fingerprinting
        val settings = GeckoRuntimeSettings.Builder()
            .contentBlocking(
                ContentBlocking.Settings.Builder()
                    .antiTracking(ContentBlocking.AntiTracking.FINGERPRINTING)
                    .enhancedTrackingProtectionLevel(ContentBlocking.EtpLevel.STRICT)
                    .build()
            )
            .preferredColorScheme(GeckoRuntimeSettings.COLOR_SCHEME_DARK)
            .build()

        // 2. Create GeckoRuntime
        runtime = GeckoRuntime.create(this, settings)

        // 3. Load uBlock Origin
        runtime.webExtensionController
            .ensureBuiltIn("resource://android/assets/ublock/", "uBlock0@raymondhill.net")
            .accept(
                { ext -> Log.i("PantherUBlock", "Installed: ${ext?.id}") },
                { err -> Log.e("PantherUBlock", "Failed: ${err?.message}") }
            )

        // 4. Create the initial session
        session = GeckoSession()
        session.open(runtime)
    }

    // 5. Clear everything function
    fun clearEverything() {
        // Close old session
        session.close()
        
        // Clear ALL cookies, storage, cache
        runtime.storageController.clearData(StorageController.ClearFlags.ALL)
        
        // Open a fresh session for next time
        session = GeckoSession()
        session.open(runtime)
    }
}
