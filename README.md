<img src="https://github.com/gargk747/AutoMatify/blob/main/assets/banner.png">

#  About
### Translate Photo and instantly recognize and translate texts and hear texts in more than 20+ languages. Just snap a photo of some text with your mobile and get the text from the image and translate it to another language in a second!

***

#  Technologies used
- Android Studio ( Java )
- Firebase ML kit API
- XML

***

#  Installation
- Clone the repository either by Git CLI or in Terminal by  ``` Git Clone https://github.com/gargk747/AutoMatify.git ```
- Open the project in Android Studio and build the project
- Then generate the APK file or run it on the emulator or your physical device
- Or can simply download the application from this link [Click Here](https://github.com/gargk747/AutoMatify/blob/main/AutoMatify.apk)

***

#  Screenshots

<table align="center"> 
  <tr>
   <td align="center">↓ Splash Screen ↓</td>
   <td align="center">↓ Home Screen ↓</td>
  </tr>
  <tr>
    <td><img src="https://github.com/gargk747/AutoMatify/blob/main/assets/1.png" width=250 height=480></td>
    <td><img src="https://github.com/gargk747/AutoMatify/blob/main/assets/2.png" width=250 height=480></td>
  </tr>
 </table>

***

#  Key Features
## ✔ Image Scan
   ``` Used Invoke Camera method to scan/capture the image ```
 <table align="center">
  <tr>
    <td><img src="https://github.com/gargk747/AutoMatify/blob/main/assets/3.png" width=250 height=480></td>
  </tr>
 </table>

## ✔ Image to Text & text to Speech

    implementation 'com.google.android.gms:play-services-mlkit-text-recognition:16.2.0'
   ``` Used ML Text Recognition API to extract texts from the image ```
   
On Clicking the speaker Button, the text is spoken out.   ``` Used Google TextToSpeech API ```
 <table align="center">
  <tr>
    <td><img src="https://github.com/gargk747/AutoMatify/blob/main/assets/4.png" width=250 height=480></td>
  </tr>
 </table>


## ✔ Text Translator

    implementation 'com.google.mlkit:translate:16.1.2'
    implementation 'com.google.mlkit:language-id:16.1.1'
   ``` Used ML Text Translation API to translate texts to many languages ```
 <table align="center">
  <tr>
    <td><img src="https://github.com/gargk747/AutoMatify/blob/main/assets/5.png" width=250 height=440></td>
    <td><img src="https://github.com/gargk747/AutoMatify/blob/main/assets/6.png" width=250 height=440></td>
    <td><img src="https://github.com/gargk747/AutoMatify/blob/main/assets/7.png" width=250 height=440></td>
    <td><img src="https://github.com/gargk747/AutoMatify/blob/main/assets/9.png" width=250 height=440></td>
  </tr>
 </table>

