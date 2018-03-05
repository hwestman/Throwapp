# ThrowApp

Throwapp is an app that measures how much risk you are willing to expose your phone to by throwing it.
To calculate risk we first measure how much power you use right before the phone goes weightless, and then we measure for how long your device actually is weightless. Based on these two parameters, we calculate a total score which gives you an indicator of how far you threw your device.

## How To Build and run using Netbeans IDE

`git clone https://github.com/hwestman/Throwapp.git`

Open the project you pulled as existing project with Netbeans
In command line update Throwapp with Android sdk (download latest version from android)
```
cd <your Android SDK>/tools
./android update project --Throwapp --target<target> --path <path to Throwapp>
```
In Netbeans you have to add the facebook android sdk like so:
`git clone https://github.com/facebook/facebook-android-sdk.git`

Update the facebook app
`./android update project --Throwapp --target<target> --path <path to facebook sdk>`

Add the facebook-android-sdk/facebook app to your library in Netbeans (in properties of Throwapp project)

Build Throwapp

### Tips:
- Find your available targets by running ./android list in same dir
- To avoid Netbeans showing errors on package import from library, you can just moved the package from the facebook app to the src of Throwapp
(remember to updatet the facebook project before doing this)
