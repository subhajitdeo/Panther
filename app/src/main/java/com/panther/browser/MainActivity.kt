
package com.panther.browser

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

class MainActivity : AppCompatActivity() {

    private lateinit var geckoView: GeckoView
    private lateinit var session: GeckoSession
    private val app: PrivacyBrowserApp get() = application as PrivacyBrowserApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        geckoView = findViewById(R.id.geckoView)
        val searchBar = findViewById<android.widget.EditText>(R.id.searchBar)

        // Get the session from our Application class
        session = app.session
        
        // Attach the session to the GeckoView UI
        session.setSessionAttachedState(GeckoSession.SessionAttachedState.ATTACHED)
        session.setActive(true) // Make it active
        geckoView.setSession(session)

        // Handle search bar "Go" button
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val input = searchBar.text.toString().trim()
                if (input.isNotEmpty()) {
                    // If it doesn't look like a URL, search DuckDuckGo
                    val url = if (input.startsWith("http://") || input.startsWith("https://")) {
                        input
                    } else {
                        "https://duckduckgo.com/?q=${java.net.URLEncoder.encode(input, "UTF-8")}"
                    }
                    session.loadUri(url)
                    searchBar.clearFocus()
                }
                true
            } else {
                false
            }
        }
    }

    override fun onBackPressed() {
        // If you press back, wipe the data completely
        app.clearEverything()
        
        // Get the new fresh session and attach it
        session = app.session
        session.setSessionAttachedState(GeckoSession.SessionAttachedState.ATTACHED)
        session.setActive(true)
        geckoView.setSession(session)
        
        // Reset the search bar
        findViewById<android.widget.EditText>(R.id.searchBar).text.clear()
    }
}
