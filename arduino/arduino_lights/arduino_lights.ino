#include<Wire.h>
byte lightState;

void setup() {
  Wire.begin(84);//register address
  Wire.onReceive(receiveEvent);
  lightState = 0;
  Serial.begin(9600);
}

void loop() {
  delay(10);
  Serial.println(lightState);
  //TODO put in the actual lights.
}

void receiveEvent(int value) {
  while (Wire.available()) {
    lightState = Wire.read(); 
  }
}

