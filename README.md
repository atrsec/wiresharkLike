# wiresharkLike

Parse pcap file.

## Install:
  > use 'make' or 'make all' in the maze directory.

## Usage:
- java Wiresharklike PCAP_FILE
> Launch interactive mode and print all the pcap file.
You can navigate in the pcap with these commands:  
d NUMBER_PACKET : Print the detail vue of packet number NUMBER_PACKET  
c NUMBER_PACKET : Print the transport protocol conversation since the packet number NUMBER_PACKET  
f PROTOCOL : Print only the packet of protocol (and TCP associated packet)  
q | quit : Quit the program.
- java Wiresharklike PCAP_FILE -o OUTPUT_FILE
> Write PCAP_FILE parsed output in OUTPUT_FILE

## Features:
- Handle IP fragmentation
- Handle TCP segmentation
- Display as wireshark, the reassembled packets are shown in the last IP/TCP packet of fragmentation/segmentation
- Protocole handled : Ethernet, ARP, IPV4, UDP, TCP, ICMP, HTTP, DHCP, DNS

## Limitation:
- The program DOESN'T handle TCP errors
- Performance are ugly, 4 minutes for 6500 packets
