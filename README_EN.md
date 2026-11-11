# ByWebView

[简体中文](README.md) | English

**ByWebView is an Android WebView wrapper** for View-based apps. It provides a configurable loading progress bar, page and title callbacks, an error view, image selection, full-screen video, and optional JavaScript interaction. The repository also contains separate DeepLink, external-browser and Tencent X5 demo code; X5 is not the engine used by the ByWebView library.

## Status

The latest published release is [1.2.2](https://github.com/youlookwhat/ByWebView/releases/tag/1.2.2), dated 2023-01-28. The default branch has later changes (including a 2026-05-12 update); these are **not automatically included in 1.2.2**. See [releases](https://github.com/youlookwhat/ByWebView/releases) and report problems via [issues](https://github.com/youlookwhat/ByWebView/issues). Dependency resolution and Android builds have not been verified for this documentation update.

## Using an AI coding assistant

The [AI integration guide](AGENTS.md) contains API details and caveats. Agents working in a different repository will not necessarily load it automatically. Copy and adapt this prompt:

```text
Use https://github.com/youlookwhat/ByWebView to add a web page to this Android View-based project. First read the repository's README and AGENTS.md and check the release version. Adapt the integration to this project's Gradle configuration and lifecycle; handle page errors, back navigation and certificate errors. Enable JavaScript, image selection or full-screen video only if needed, review URL and WebView security settings, and build and verify the result. Report anything you could not verify.
```

ByWebView is not a native Jetpack Compose component. Choose an approach appropriate for your app.

## Installation

Add JitPack alongside `google()` and `mavenCentral()` in the location where the project manages repositories. For modern Gradle projects this is usually `dependencyResolutionManagement.repositories` in `settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Or in `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Older projects may instead use `allprojects.repositories` in the root build file; use the location appropriate to the existing project rather than adding duplicate configuration.

In the app module:

```groovy
dependencies {
    implementation 'com.github.youlookwhat:ByWebView:1.2.2'
    implementation 'com.google.android.material:material:1.0.0'
}
```

Kotlin DSL:

```kotlin
dependencies {
    implementation("com.github.youlookwhat:ByWebView:1.2.2")
    implementation("com.google.android.material:material:1.0.0")
}
```

Material is `compileOnly` in the library; use a compatible version already provided by your app if present. The library declares `minSdk` 14, but the effective minimum depends on the selected app dependencies. Its manifest declares Internet and network-state permissions.

**Compatibility:** 1.2.2 uses an older build configuration and APIs; it may require changes for current Android/Gradle environments. The newer default-branch changes are not part of this release. Verify dependency resolution, compilation and runtime behavior in the target project instead of assuming compatibility.

## Quick start

This Java Activity creates a container, cancels SSL errors, and disables JavaScript and mixed content before loading a trusted HTTPS URL. Add it to the app manifest and replace the example URL with an appropriate trusted URL. The example is illustrative and has not been compiled for this documentation update.

```java
import android.app.Activity;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import me.jingbin.web.ByWebView;
import me.jingbin.web.OnByWebClientCallback;

public class WebActivity extends Activity {
    private ByWebView byWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout container = new FrameLayout(this);
        setContentView(container);

        byWebView = ByWebView.with(this)
                .setWebParent(container, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .setOnByWebClientCallback(new OnByWebClientCallback() {
                    @Override
                    public boolean onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        handler.cancel();
                        return true;
                    }
                })
                .get();
        WebSettings settings = byWebView.getWebView().getSettings();
        settings.setJavaScriptEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }
        byWebView.loadUrl("https://example.com");
    }

    @Override protected void onResume() {
        super.onResume();
        if (byWebView != null) byWebView.onResume();
    }

    @Override protected void onPause() {
        if (byWebView != null) byWebView.onPause();
        super.onPause();
    }

    @Override protected void onDestroy() {
        if (byWebView != null) byWebView.onDestroy();
        super.onDestroy();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (byWebView != null && byWebView.handleKeyEvent(keyCode, event)) return true;
        return super.onKeyDown(keyCode, event);
    }
}
```

If image selection is required, forward `onActivityResult` to `byWebView.handleFileChooser(requestCode, resultCode, data)` and test it on target devices. The 1.2.2 implementation uses the legacy Activity result flow and chooses `image/*`; it is not a general-purpose file picker.

## Important WebView defaults

- In 1.2.2, JavaScript and DOM storage are enabled and mixed content is allowed by default. Change settings **before** loading a page, according to the app's requirements.
- Without an overriding callback, SSL errors show a dialog that permits the user to continue. Override `onReceivedSslError`, cancel the handler and return `true`; do not bypass certificate validation.
- `addJavascriptInterface` exposes native methods to pages. Use it only with trusted content and navigation, expose the minimum necessary methods and protect inputs. `quickCallJs` does not fully escape arbitrary string arguments: do not pass untrusted input directly.
- The default URL handler can open external apps. When setting `OnByWebClientCallback`, its `isOpenThirdApp` method takes over URL handling; define and test your own navigation policy if needed. Validate initial URLs and redirects, including hosts and schemes, before enabling a bridge.
- Full-screen video can change Activity orientation. `OnTitleProgressCallback.onHandleScreenOrientation` can take over orientation handling by returning `true`.

For progress, custom error views, callbacks and further examples see [the Chinese README](README.md), [AGENTS.md](AGENTS.md), and the [demo app](app/src/main/java/com/example/jingbin/webviewstudy/).

## License

[Apache License 2.0](LICENSE).
