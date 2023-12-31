import socket
import time
import random


##My Name: Shreyas Srinivasa, My Partner: Ahamed Alisha Sayed 
#When running on different computers, set the server host here to the IP of the computer the robot is running in. 

serverhost = "172.24.18.96"
port = 3310
udp_num_min = 5
udp_num_max = 10


print("Connecting...")
s1 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s1.connect((serverhost, port))
print("Connected.")

blazerID = "SSRINIVA" 
s1.sendall(blazerID.encode())
print("BlazerID sent successfully.")

initalPort = s1.recv(5).decode()
s_2_port = int(initalPort)
print("TCP port %d received!!" % s_2_port)
s2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s2.bind((socket.gethostname(), s_2_port))
s2.listen(3)
print("Creating new socket s2...")

time.sleep(3)
s3, _ = s2.accept()
print("Connected to robot on s2.")

stringportone, stringporttwo = s3.recv(12).decode().split(',')
stringportone_port = int(stringportone)
stringporttwo_port = int(stringporttwo)
print("UDP ports %d, %d received!!" % (stringportone_port, stringporttwo_port))
s3 = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s3.bind((socket.gethostname(), stringporttwo_port))

num = str(udp_num_min + (udp_num_max-udp_num_min)//2) 
s3.sendto(num.encode(), (serverhost, stringportone_port))
print("Sending %s using port %d" % (num, stringportone_port))

time.sleep(3)
data, _ = s3.recvfrom(int(num)*10)
data = data.decode()
print("Received %s using port %d" % (data, stringporttwo_port))

for i in range(5):
    s3.sendto(data.encode(), (serverhost
, stringportone_port))
    time.sleep(1)
    print("Packet %d sent!!" % (i+1))

print("Sent!")

s1.close()
s2.close()
s3.close()