# UberClone

## Setup Guide

### Setup firebase
- Open your [Firebase console](https://console.firebase.google.com/)
- Add a new project
- Add a new Android application, you have to do add 2 applicatitons, driver and rider one.
- Then go to the project settings > Cloud Messaging. On this tab you will copy the Server Key and you will put it in [IFCMService.java](applications/DriverApp/app/src/main/java/com/iramml/uberclone/driverapp/Interfaces/IFCMService.java) file you will put the Server Key on the line 14, after Authorization:key=
- Do also the last step to the RiderApp

### Setup Google APIs
- Go to your [Google cloud platform](https://console.cloud.google.com/) and select your project or create a new project.
- Search and active:
    - Places SDK for Android
    - Maps SDK for Android
    - Directions API
- Go to your Credentials and copy the API Key
- You will replace the API key on the String.xml of the both applications

## Screenshots
|Driver Application|Rider Application|
|---|---|
|![ScreenshotDriverApp00](Images/Screenshot_DriverApp_00.png)|![ScreenshotRiderApp00](Images/Screenshot_RiderApp_00.png)|
