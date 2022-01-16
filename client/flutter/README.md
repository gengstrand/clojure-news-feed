# feed

The news feed GUI implemented in Flutter.

## Getting Started

I am deving on an Ubuntu laptop with no android SDK installed.

```bash
export CHROME_EXECUTABLE=$(whereis chromium | awk '{ print $2 }')
flutter test --no-sound-null-safety
flutter run -d chrome --release --no-sound-null-safety
```

The app never loads when chrome launches. Right now, I have to manually open a new tab with the same address in order to load the application in the browser.

## More Resources

A few resources to get you started if this is your first Flutter project:

- [Lab: Write your first Flutter app](https://flutter.dev/docs/get-started/codelab)
- [Cookbook: Useful Flutter samples](https://flutter.dev/docs/cookbook)

For help getting started with Flutter, view our
[online documentation](https://flutter.dev/docs), which offers tutorials,
samples, guidance on mobile development, and a full API reference.
