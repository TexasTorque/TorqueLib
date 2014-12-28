#!/usr/bin/env python

#
#  --- pyConsole ---
#
# Author: Texas Torque
#
# Simple python script to read and display console output from your roboRio. This is a
# standalone alternative to RioLog that allows you to use another IDE without needing eclipse
# to see console output.

import socket
import time

PORT = 6666
BUFFER_SIZE = 1024

def main():
        try:
            print("Creating socket.")
            rioSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            rioSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            rioSocket.bind(("", PORT))
            print("Socket created.")

            print("Robot output:\n\n")
            while 1:
                lines, address = rioSocket.recvfrom(BUFFER_SIZE)
                print(lineslines)
            
        except:
            print("Could not create socket")
            last_error_time = time.time()

if __name__ == "__main__":
    main()
