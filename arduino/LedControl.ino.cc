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

const int LED_PIN = 7;
const int LED_COUNT = 120;

const int PIN_1 = 3;
const int PIN_2 = 4;
const int PIN_3 = 5;

Adafruit_NeoPixel strip(LED_COUNT, LED_PIN, NEO_GRB + NEO_KHZ800);
//   NEO_KHZ800  800 KHz bitstream (most NeoPixel products w/WS2812 LEDs)
//   NEO_KHZ400  400 KHz (classic 'v1' (not v2) FLORA pixels, WS2811 drivers)
//   NEO_GRB     Pixels are wired for GRB bitstream (most NeoPixel products)
//   NEO_RGB     Pixels are wired for RGB bitstream (v1 FLORA pixels, not v2)
//   NEO_RGBW    Pixels are wired for RGBW bitstream (NeoPixel RGBW products)

bool isRedAlliance;

void setup() {
  pinMode(PIN_1, INPUT);
  pinMode(PIN_2, INPUT);
  pinMode(PIN_3, INPUT);
  Serial.begin(9600);
  strip.begin();
  strip.show();            // Turn OFF all pixels ASAP
  strip.setBrightness(200); // Set BRIGHTNESS to about 1/5 (max = 255)
  isRedAlliance = true;
}

void rgb(int r, int g, int b) {
  strip.fill(strip.Color(r, g, b));
  strip.show();
}

void loop() {
  bool pin1 = digitalRead(PIN_1);
  bool pin2 = digitalRead(PIN_2);
  bool pin3 = digitalRead(PIN_3);

  // Nothing
  if (pin1 == LOW && pin2 == LOW && pin3 == LOW) {
    rgb(0, 0, 0);
  // Red Teleop
  } else if (pin1 == LOW && pin2 == LOW && pin3 == HIGH) {
    rgb(255, 0, 0);
    isRedAlliance = true;
  // Blue Teleop
  } else if (pin1 == LOW && pin2 == HIGH && pin3 == LOW) {
    rgb(0, 0, 255);
    isRedAlliance = false;
  // Target Lock, solid green
  } else if (pin1 == LOW && pin2 == HIGH && pin3 == HIGH) {
    rgb(0, 255, 0);
  // Endgame
  } else if (pin1 == HIGH && pin2 == LOW && pin3 == LOW) {
    rainbow(15);
  // Shooting, flashing green
  } else if (pin1 == HIGH && pin2 == LOW && pin3 == HIGH) {
    rgb(0, 255, 0);
    delay(50);
    rgb(0, 0, 0);
  // Red auto, flashing
  } else if (pin1 == HIGH && pin2 == HIGH && pin3 == LOW) {
    rgb(255, 0, 0);   
    delay(50);
    rgb(0, 0, 0);
    // Blue auto, flashing
  } else if  (pin1 == HIGH && pin2 == HIGH && pin3 == HIGH) {
    rgb(0, 0, 255);
    delay(50);
    rgb(0, 0, 0);
  }

  // White is failure condition
  else
    rgb(255, 255, 255);
}

void rainbow(int wait) {
  // Hue of first pixel runs 5 complete loops through the color wheel.
  // Color wheel has a range of 65536 but it's OK if we roll over, so
  // just count from 0 to 5*65536. Adding 256 to firstPixelHue each time
  // means we'll make 5*65536/256 = 1280 passes through this loop:
  for (long firstPixelHue = 0; firstPixelHue < 5*65536; firstPixelHue += 256) {
    // strip.rainbow() can take a single argument (first pixel hue) or
    // optionally a few extras: number of rainbow repetitions (default 1),
    // saturation and value (brightness) (both 0-255, similar to the
    // ColorHSV() function, default 255), and a true/false flag for whether
    // to apply gamma correction to provide 'truer' colors (default true).
    strip.rainbow(firstPixelHue);
    // Above line is equivalent to:
    // strip.rainbow(firstPixelHue, 1, 255, 255, true);
    strip.show(); // Update strip with new contents
    delay(wait);  // Pause for a moment
  }
}