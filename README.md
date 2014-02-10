Sky Camera
=======

by Mohit Meena

Read more at: http://publiclab.org/wiki/sky-camera

Sky Camera is a prototype Android app (updated now) for attaching to a balloon or kite for taking pictures at a set interval. It was developed by Mohit Meena as part of the 2013 Google Summer of Code program with Public Lab.

It has the following features:

* Android 2.3.3 is supported now.
* Android 3 is required at minimum.
* custom intervalometer to take pictures every X seconds (from 7 seconds up)
* image stability detection to take pictures only when the camera is not moving too much
* auto-emailing to send low-res versions of the photos to the given email address while in the air, over the cell data connection
* auto-emailing of latitude/longitude
* some camera settings adjustments

Planned features include:

* "beep" pulse on interval to synchronize with hi-res cameras
* more compatibility with more phones
* more links to MapKnitter and other Public Lab resources
* low power mode which disables the screen
* more features listed here: https://github.com/publiclab/sky-camera/issues/

For using this application:

1. Please turn on Your GPS and WiFi for location and Email updates respectively.
2. Set time period to greater than 7 Seconds.(Default is 7s where s stands for seconds; m for minutes and h for hour)
3. Set Acceleration Threshold to around 0.5 (You can set it as according to your need)
4. For email updates, type in your mail id.


