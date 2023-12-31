import socket
import time
import random

# Set up variables
serverhost = "172.29.208.1"
port = 3310
udp_num_min = 5
udp_num_max = 10

# randomNumber = random.randint(5,7)
# Create TCP socket and connect to robot
print("Connecting...")
s1 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s1.connect((serverhost, port))
print("Connected.")

# Send BlazerID to robot
blazerID = "SSRINIVA"
s1.sendall(blazerID.encode())
print("BlazerID sent successfully.")

# Receive random 5-char string from robot and create TCP socket s2
initalPort = s1.recv(5).decode()
s_2_port = int(initalPort)
print("TCP port %d received!!" % s_2_port)
s2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s2.bind((socket.gethostname(), s_2_port))
s2.listen(3)
print("Creating new socket s2...")

# Wait for robot to initiate new connection to s2
time.sleep(3)
s3, _ = s2.accept()
print("Connected to robot on s2.")

# Receive UDP port numbers from robot and create UDP socket s3
stringportone, stringporttwo = s3.recv(12).decode().split(',')
stringportone_port = int(stringportone)
stringporttwo_port = int(stringporttwo)
print("UDP ports %d, %d received!!" % (stringportone_port, stringporttwo_port))
s3 = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s3.bind((socket.gethostname(), stringporttwo_port))

# Send variable num to robot on stringportone_port
num = str(udp_num_min + (udp_num_max-udp_num_min)//2) # choose a number between udp_num_min and udp_num_max
s3.sendto(num.encode(), (serverhost, stringportone_port))
print("Sending %s using port %d" % (num, stringportone_port))

# Receive string from robot on s3
time.sleep(3)
data, _ = s3.recvfrom(int(num)*10)
data = data.decode()
print("Received %s using port %d" % (data, stringporttwo_port))

# Send same string back to robot on stringportone_port five times
for i in range(5):
    s3.sendto(data.encode(), (serverhost
, stringportone_port))
    time.sleep(1)
    print("Packet %d sent!!" % (i+1))

print("Sent!")
# Close sockets
s1.close()
s2.close()
s3.close()