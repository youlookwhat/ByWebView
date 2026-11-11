# ByWebView agent guide

For agents integrating this library into Android View-based apps or working in this repository. An agent operating in another repository does not automatically load this file: fetch and read it explicitly. The guidance below is based on the `1.2.2` tag; examples have not been built or run for this documentation update.

## Scope and version

- `me.jingbin.web.ByWebView` is a wrapper that creates/configures an Android `WebView` and attaches it to a supplied Activity container; it is not a WebView subclass or a native Compose component.
- Latest release: `1.2.2`, dated 2023-01-28. The default branch has later changes; don't assume unreleased APIs and fixes exist in the binary. In particular, `ByWebTools.syncCookie` exists on `master` but not in `1.2.2`.
- AndroidX library with declared `minSdk` 14. The app's effective minimum depends on its dependencies. Material is `compileOnly`; the app must provide a compatible version.
- Features include loading progress, error view, callbacks, optional JavaScript bridge, image selection and full-screen video. DeepLink, browser-intent and Tencent X5 examples are in the demo; ByWebView itself uses Android's standard WebView.

## Integration workflow

1. Inspect the consuming project's repository configuration, Android/Gradle versions, Activity/Fragment lifecycle, existing WebView and URL policy. Decide whether this library fits; do not silently replace an existing WebView client or security policy.
2. Add JitPack in the project's actual repository configuration. Example for `settings.gradle`: `dependencyResolutionManagement { repositories { google(); mavenCentral(); maven { url 'https://jitpack.io' } } }`. Kotlin DSL: `maven { url = uri("https://jitpack.io") }`. For older builds use the existing root `allprojects.repositories`.
3. Add `implementation 'com.github.youlookwhat:ByWebView:1.2.2'` to the consuming module, and a compatible `com.google.android.material:material` dependency if it is not already provided. The library manifest declares `INTERNET` and `ACCESS_NETWORK_STATE`.
4. Create with `ByWebView.with(activity).setWebParent(container, layoutParams)`. A parent and matching `ViewGroup.LayoutParams` are required. Use `.get()` to obtain the wrapper without loading, configure its `getWebView().getSettings()`, then call `loadUrl(url)`. Alternatively, `.loadUrl(url)` builds and loads immediately; do not use that form if settings must be changed before loading.
5. Forward lifecycle appropriately: `onResume()`, `onPause()`, and `onDestroy()` when the owning view is destroyed. `onPause()` and `onResume()` call WebView's process-wide `pauseTimers()` and `resumeTimers()`, so consider other WebViews in the app. In a Fragment, use view lifecycle and do not retain the Activity or WebView after `onDestroyView`.
6. Route back navigation through `handleKeyEvent(keyCode, event)` or `isBack()` before the Activity/Fragment's normal back behavior. It exits full-screen video first, then goes back in WebView history. Verify behavior with the app's existing back dispatcher.
7. If image upload is required, forward Activity `onActivityResult(requestCode, resultCode, data)` to `handleFileChooser(...)`. The bundled chooser uses legacy `startActivityForResult`, selects `image/*` and handles the returned URI; it is not a general file chooser or a camera implementation. Test success and cancellation on supported devices.

See [README_EN.md](README_EN.md#quick-start) for a complete Java Activity example. In the target project use real resources and data, compile and exercise the flow; do not treat documentation examples as proof of compatibility.

## Security and navigation

- Version 1.2.2 enables JavaScript and DOM storage and sets `MIXED_CONTENT_ALWAYS_ALLOW` on Android 5+ by default. For pages that do not need JS, call `getWebView().getSettings().setJavaScriptEnabled(false)` before loading. On Android 5+, use `WebSettings.MIXED_CONTENT_NEVER_ALLOW` unless there is a reviewed reason otherwise.
- The default SSL error handler presents a dialog allowing `handler.proceed()`. Override `OnByWebClientCallback.onReceivedSslError`, call `handler.cancel()` and return `true` to prevent the default dialog. Never blindly bypass certificate errors.
- Validate the initial URL, scheme and host, and restrict subsequent navigation and redirects according to the app's trust policy. With a custom `OnByWebClientCallback`, `isOpenThirdApp(String)` takes over navigation decisions; returning `true` consumes the navigation, `false` leaves it to the WebView. The default callback consumes non-HTTP(S) URLs rather than opening them. Without a callback, the library calls `ByWebTools.handleThirdApp`, which can launch external applications. Apply an explicit allowlist and test external intent behavior; do not open arbitrary schemes.
- Only add a JavaScript interface for trusted content. Methods exposed to JavaScript require `@JavascriptInterface`; do not expose sensitive operations to untrusted content or frames. Account for redirects and navigation away from the trusted origin. Avoid bridge exposure on older Android versions when it cannot be secured.
- `quickCallJs(method, params...)` concatenates non-JSON parameters into JavaScript without fully escaping strings. Do not pass untrusted or arbitrary strings directly; encode data safely and test the actual bridge. `loadJs` uses `evaluateJavascript` on API 19+ and `loadUrl` below it, where a result callback is not provided.
- `setOnByWebClientCallback` and `setOnTitleProgressCallback` accept callback objects, not arbitrary replacements for the underlying clients. If an app replaces the WebView clients, built-in progress, chooser, SSL, error or navigation behavior may be lost.

## API reference

- Builder: `with(Activity)`, `setWebParent(ViewGroup, LayoutParams)` or `setWebParent(ViewGroup, int, LayoutParams)`, `setCustomWebView(WebView)`, `useWebProgress(boolean|int|String)`, `useWebProgress(startColor, endColor, heightDp)`, `setErrorLayout(layoutId[, title])`, `setOnTitleProgressCallback(...)`, `setOnByWebClientCallback(...)`, `addJavascriptInterface(name, object)`, `get()`, `loadUrl(url)`.
- Wrapper: `loadUrl`, `reload`, `getWebView`, `getProgressBar`, `showErrorView`, `hideErrorView`, `getErrorView`, `setTextZoom`, `onResume`, `onPause`, `onDestroy`, `handleFileChooser`, `isBack`, `handleKeyEvent`.
- `OnTitleProgressCallback`: `onReceivedTitle`, `onProgressChanged`, `onHandleScreenOrientation`, `onJsAlert`, `onJsConfirm`, `onJsPrompt`. Returning `true` for a JS dialog means the app handles the result and must complete it via `confirm` or `cancel`. For orientation, `true` means the app handles orientation; otherwise the library defaults to landscape in full-screen and portrait on exit.
- `OnByWebClientCallback`: `onPageStarted`, `onPageFinished`, `isOpenThirdApp`, `onReceivedSslError`. A `true` SSL return means the app has handled the handler.
- Built-in error view handles main-frame network errors on supported API paths; the 1.2.2 HTTP-error handling is disabled, so do not claim all HTTP 4xx/5xx errors automatically show an error page.
- `getLoadJsHolder().quickCallJs(...)` and `loadJs(...)` provide Java-to-JavaScript calls. The README includes ProGuard/R8 guidance for JavaScript interface methods when minification is enabled.

## Validation and release limitations

Version 1.2.2 uses an older build configuration and contains `WebSettings.setAppCacheEnabled`; the newer default branch comments this call out. The current Android SDK/Gradle and JitPack resolution may not work with the old release. Build the actual consuming project and report resolution or compilation failures; do not imply that an unreleased fix is present in 1.2.2.

Validate page loading and main-frame failures, SSL cancellation, allowed and rejected URLs, history/back navigation, and Activity/Fragment cleanup. If used, also test image chooser selection/cancellation, JS calls and bridge restrictions, full-screen entry/exit and orientation, and behavior on supported Android versions. Do not report unperformed checks as passing.

## Repository

`:ByWebView` contains the library; `:app` contains examples. Useful references: `app/src/main/java/com/example/jingbin/webviewstudy/ui/ByWebViewActivity.java`, library source under `ByWebView/src/main/java/me/jingbin/web/`, and [release notes](https://github.com/youlookwhat/ByWebView/releases). Read the tag being integrated before using an API from the default branch. The Gradle wrapper is `./gradlew`; Android SDK and compatible tooling are required.
