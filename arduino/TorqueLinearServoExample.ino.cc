// Copyright (c) 2022 Texas Torque
// 
// Authors: Omar
//
// This file is part of TorqueLib, and is subject to the 
// same license.

#include <Servo.h>

Servo myservo;  // create servo object to control a servo
int pos = 0;    // variable to store the servo position

void setup() {
  myservo.attach(9);  // attaches the servo on pin 9 to the servo object
}

void loop() {

  // goes from 0 degrees to 180 degrees in steps of 1 degree
  for (pos = 0; pos <= 180; pos += 1) { 

    myservo.write(pos);              // tell servo to go to position in variable 'pos'

    delay(15);                       // waits 15ms for the servo to reach the position

  }

  // goes from 180 degrees to 0 degrees
  for (pos = 180; pos >= 0; pos -= 1) { 

    myservo.write(pos);              // tell servo to go to position in variable 'pos'

    delay(15);                       // waits 15ms for the servo to reach the position

  }

}
