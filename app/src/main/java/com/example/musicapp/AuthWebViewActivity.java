package com.example.musicapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError; // For API Level 23+ generic errors
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse; // For API Level 21+ HTTP errors
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AuthWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    private String clerkFrontendHost;
    private String redirectUrlAfterLogin;
    private String nativeAppCallbackScheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_webview);

        // Initialize UI elements
        webView = findViewById(R.id.auth_webview);
        progressBar = findViewById(R.id.webview_progress_bar);

        // Fetch URLs from strings.xml
        clerkFrontendHost = getString(R.string.clerk_frontend_host);
        redirectUrlAfterLogin = getString(R.string.clerk_redirect_url_after_login);
        nativeAppCallbackScheme = getString(R.string.native_app_callback_scheme);

        setupWebView();

        // Load Clerk's sign-in page.
        // You can use "/sign-in", "/sign-up", or the root URL which often presents both.
        webView.loadUrl(clerkFrontendHost + "/sign-in");
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Essential for Clerk's UI to function
        webSettings.setDomStorageEnabled(true); // Recommended for modern web applications
        webSettings.setDatabaseEnabled(true);   // Recommended

        // Security best practices: Disallow file access
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowContentAccess(false);

        // Enable cookies for WebView, as Clerk heavily relies on them for session management.
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Needed for third-party cookies in newer Android versions if Clerk uses them across subdomains
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new AuthWebViewClient());

        // Set a WebChromeClient to handle progress updates and potentially JavaScript dialogs
        webView.setWebChromeClient(new android.webkit.WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
    }

    private class AuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            String url = uri.toString();

            Log.d("AuthWebView", "Intercepting URL: " + url);

            // Step 1: Intercept your custom native app callback URL
            // This is the ideal way to get control back to your native app after auth.
            if (url.startsWith(nativeAppCallbackScheme)) {
                handleAuthCallback(uri);
                return true; // Indicate that we've handled the URL and prevent WebView from loading it
            }

            // Step 2: Fallback - Intercept the Clerk post-login redirect URL.
            // This is useful if Clerk cannot directly redirect to your custom scheme.
            // We then explicitly try to get the session cookie.
            if (url.startsWith(redirectUrlAfterLogin)) {
                Log.d("AuthWebView", "Redirect URL after login detected: " + url);
                handleClerkSessionRedirect(uri); // Try to get the session from cookies
                return true; // Indicate that we've handled the URL
            }

            // For all other URLs, let the WebView handle them normally
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            Log.d("AuthWebView", "Page started loading: " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            Log.d("AuthWebView", "Page finished loading: " + url);
        }

        // Handles generic resource loading errors (e.g., network down, resource not found for non-HTTP)
        // For API Level 23 (Marshmallow) and above
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error); // Call the correct super method

            // Log and show generic loading errors
            String errorMessage = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                errorMessage = "WebView loading error for: " + request.getUrl().toString() +
                        "\nCode: " + error.getErrorCode() +
                        "\nDescription: " + error.getDescription();
            }
            Log.e("AuthWebView", errorMessage);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Toast.makeText(AuthWebViewActivity.this, "WebView Error: " + error.getDescription(), Toast.LENGTH_LONG).show();
            }
        }

        // Handles HTTP errors (e.g., 404 Not Found, 500 Internal Server Error)
        // For API Level 21 (Lollipop) and above
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse); // Call the correct super method

            // Log and show HTTP errors
            String errorMessage = "WebView HTTP Error for: " + request.getUrl().toString() +
                    "\nStatus: " + errorResponse.getStatusCode() +
                    "\nReason: " + errorResponse.getReasonPhrase();
            Log.e("AuthWebView", errorMessage);
            Toast.makeText(AuthWebViewActivity.this, "WebView HTTP Error: " + errorResponse.getReasonPhrase() + " (Code: " + errorResponse.getStatusCode() + ")", Toast.LENGTH_LONG).show();
        }

        // Deprecated onReceivedError for older Android versions (API < 23) if you need to support them
        @Override
        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // Only call super if the modern onReceivedError is not called
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                String errorMessage = "WebView old error: " + failingUrl + "\nCode: " + errorCode + "\nDescription: " + description;
                Log.e("AuthWebView", errorMessage);
                Toast.makeText(AuthWebViewActivity.this, "WebView Error (Old API): " + description, Toast.LENGTH_LONG).show();
            }
        }
    }

    // This method is called when the WebView navigates to your custom scheme URL
    // or to the configured redirectUrlAfterLogin.
    private void handleAuthCallback(Uri uri) {
        // For Clerk, after a successful login in the WebView, the crucial step is to get the session token.
        // Clerk usually sets a __session cookie (and others like __client) on its own domain.
        // You need to extract this cookie.
        String cookies = CookieManager.getInstance().getCookie(clerkFrontendHost);
        String clerkSessionToken = null;

        Log.d("AuthWebView", "Attempting to retrieve cookies for: " + clerkFrontendHost + "\nAll cookies: " + cookies);

        if (cookies != null) {
            // Parse the cookies string to find the __session cookie
            String[] cookieParts = cookies.split(";");
            for (String part : cookieParts) {
                part = part.trim();
                if (part.startsWith("__session=")) {
                    clerkSessionToken = part.substring("__session=".length());
                    break;
                }
            }
        }

        if (clerkSessionToken != null && !clerkSessionToken.isEmpty()) {
            Toast.makeText(this, "Login successful via WebView!", Toast.LENGTH_LONG).show();
            // TODO: IMPORTANT! Store this token securely.
            // Use Android's EncryptedSharedPreferences for production apps.
            // For now, logging it for debugging:
            Log.d("AuthWebView", "Clerk Session Token Retrieved: " + clerkSessionToken);

            // Navigate to your main app activity
            Intent intent = new Intent(AuthWebViewActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the AuthWebViewActivity
        } else {
            Toast.makeText(this, "Login successful, but could not retrieve session token.", Toast.LENGTH_LONG).show();
            Log.e("AuthWebView", "Clerk session token (__session) not found in cookies after redirect.");
            // You might want to display an error or prompt the user to try again
        }
    }

    // Helper method for when Clerk redirects to its own domain after login
    // This will trigger the same logic as the custom scheme callback to extract the cookie.
    private void handleClerkSessionRedirect(Uri uri) {
        // The session cookie should already be available in the CookieManager for CLERK_FRONTEND_HOST.
        // Re-use the logic to get the cookie.
        handleAuthCallback(uri);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}