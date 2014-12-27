#include<Wire.h>
#define min_pin 22
#define max_pin 53

void setup() {
  Wire.begin(84);//register address
  Wire.onReceive(receiveEvent);
  Wire.onRequest(requestEvent);
  
  int i;
  for (i = min_pin; i < max_pin + 1; i++) {
    pinMode(i, INPUT);//set each digital pin as INPUT
  }
}

void loop() {
  delay(10);
}

void receiveEvent(int value) {
  
}

void requestEvent() {
  byte vals[4];
  int o, i;
  for (o = 0; o < 4; o++) {
    for (i = 0; i < 8; i++) {
      if ((i + 1) * (o + 1) == max_pin) {
        continue;
      }
      int sensorOn = digitalRead((i + 1) * (o + 1));//gets HIGH or LOW as an int
      if (sensorOn) {
        vals[o] += 1 << i;
      } else {
        vals[o] = 0;
      }
    }
    
  }
  Wire.write(vals, 4);
}
