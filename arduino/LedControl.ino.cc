// Copyright (c) 2022 Texas Torque
// 
// Authors: Omar, Justus, Jacob, Jack
//
// This file is part of TorqueLib, and is subject to the 
// same license.
//
// Controls LED lights based off RoboRio DIO signals. Works with
// ./src/main/java/org/texastorque/subsystems/lights.java 
// in TexasTorque2022 master.

#include <Adafruit_NeoPixel.h>

#define LED_PIN 7
#define LED_COUNT 120

// DIO inputs from the RoboRio (pins numbers are on the Arduino)
// Also it may be represented as A,B,C in code, same thing.
#define ROBORIO_1 3
#define ROBORIO_2 4
#define ROBORIO_3 5

bool pin1Read;
bool pin2Read;
bool pin3Read;

// instanstiate the strip (ONE strip on LED_PIN)
Adafruit_NeoPixel strip(LED_COUNT, LED_PIN, NEO_GRB + NEO_KHZ800);

// pulled from fms on the robot
bool isRedAlliance = false;
bool flashThisLoop = true;
// used to manage le rainbow
bool rainbowStarted = false;
uint16_t firstPixelHue = 0;


void setup() {
  pinMode(ROBORIO_1, INPUT);
  pinMode(ROBORIO_2, INPUT);
  pinMode(ROBORIO_3, INPUT);
  Serial.begin(9600);     // uncomment this if you need to print stuff
  strip.begin();            // just some initialization
  strip.show();             // show whatever's ready to show, which is nothing at this time
  strip.setBrightness(200); // Set BRIGHTNESS to about 4/5 (max = 255) too high and u get weirdness
}

void loop() {
  pin1Read = digitalRead(ROBORIO_1);
  pin2Read = digitalRead(ROBORIO_2);
  pin3Read = digitalRead(ROBORIO_3);
  Serial.print(pin1Read);
  Serial.print(pin2Read);
  Serial.println(pin3Read);
  
  // NO_LIGHTS
  if (pin1Read == LOW && pin2Read == LOW && pin3Read == LOW)
    rgb(0, 0, 0, &strip);
  // RED_TELEOP
  else if (pin1Read == LOW && pin2Read == LOW && pin3Read == HIGH) {
    rgb(255, 0, 0, &strip);
    isRedAlliance = true;
  }
  // BLUE_TELEOP
  else if (pin1Read == LOW && pin2Read == HIGH && pin3Read == LOW) {
    rgb(0, 0, 255,  &strip);
    isRedAlliance = false;
  }
  // TARGET_LOCK
  else if (pin1Read == LOW && pin2Read == HIGH && pin3Read== HIGH) {
    rgb(0, 255, 0, &strip);
  }
  // ENDGAME
  else if (pin1Read == HIGH && pin2Read == LOW && pin3Read == LOW) {
    if (firstPixelHue > 65535) firstPixelHue = 0; // don't exceed the 16 bit unsigned integer limit!
    firstPixelHue += 256;
    strip.rainbow(firstPixelHue);
  }
  // SHOOTING
  else if (pin1Read == HIGH && pin2Read == LOW && pin3Read == HIGH) {
    if (flashThisLoop) {
      rgb(0, 255, 0,  &strip);
      delay(150);
    } else {
      rgb(0,0,0,&strip);
      delay(150);
    }
    flashThisLoop = !flashThisLoop;
  }
  // RED_AUTO
  else if (pin1Read == HIGH && pin2Read == HIGH && pin3Read == LOW) {
    if (flashThisLoop) {
      rgb(0, 0, 255,  &strip);
      delay(150);
    } else {
      rgb(0,0,0,&strip);
      delay(150);
    }
    flashThisLoop = !flashThisLoop;
    isRedAlliance = false;
  }
  // BLUE_AUTO
  else if (pin1Read == HIGH && pin2Read == HIGH && pin3Read == HIGH) {
    if (flashThisLoop) {
      rgb(0, 0, 255,  &strip);
      delay(150);
    } else {
      rgb(0,0,0,&strip);
      delay(150);
    }
    flashThisLoop = !flashThisLoop;
    isRedAlliance = false;
  }
  // White is failure condition
  else
    rgb(255, 255, 255, &strip);
  // we get the global strip object here so no need to dereference (*) them
  // btw this shows changes made to the strips
  strip.show();
}


void rainbow(int wait, Adafruit_NeoPixel* strip) {
  for (long firstPixelHue = 0; firstPixelHue < 5*65536; firstPixelHue += 256) {
    (*strip).rainbow(firstPixelHue); 
    (*strip).show(); // Update strip with new contents
    delay(wait);  // Pause for a moment
  }
}

// sets the strip (does not show)
void rgb(int r, int g, int b, Adafruit_NeoPixel* strip) {
  (*strip).fill((*strip).Color(r, g, b));
}


