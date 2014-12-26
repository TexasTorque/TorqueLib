#!/usr/bin/env python

# pyConsole
# Simple python script to read and display console output from your roboRio. This is a
# standalone alternative to RioLog that allows you to use another IDE without needing eclipse
# to see console output.

import socket
import time

PORT = 6666
BUFFER_SIZE = 1024
RETRY_PERIOD = 1.0

def main():
    connected = False
    last_error_time = time.time()
    while 1:
        current_time = time.time()
        if not connected and current_time - RETRY_PERIOD > last_error_time:
            try:
                print("Creating socket.")
                rioSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
                rioSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
                #rioSocket.settimeout(0.1)
                rioSocket.bind(("", PORT))
                print("Socket created.")
                connected = True
            except:
                if current_time - RETRY_PERIOD > last_error_time:
                    print("Could not create socket")
                    last_time = time.time()

        try:
            lines, addr = rioSocket.recvfrom(BUFFER_SIZE)
            print(lines[:len(lines)-1])
        except:
            connected = False
            if current_time - RETRY_PERIOD > last_error_time:
                print("Could not retrieve data")
                last_time = time.time()

if __name__ == "__main__":
    main()
