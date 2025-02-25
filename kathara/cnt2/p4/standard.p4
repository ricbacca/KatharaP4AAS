/* -*- P4_16 -*- */
#include <core.p4>
#include <v1model.p4>

#define COLLECTION_TIMEDELTA 55000
#define PAYLOAD_CHUNK_SIZE 2048

const bit<16> TYPE_IPV4 = 0x0800;
const bit<16> TYPE_ARP  = 0x0806;
const bit<8>  TYPE_TCP  = 6;

// ARP RELATED CONST VARS
const bit<16> ARP_HTYPE = 0x0001; //Ethernet Hardware type is 1
const bit<16> ARP_PTYPE = TYPE_IPV4; //Protocol used for ARP is IPV4
const bit<8>  ARP_HLEN  = 6; //Ethernet address size is 6 bytes
const bit<8>  ARP_PLEN  = 4; //IP address size is 4 bytes
const bit<16> ARP_REQ = 1; //Operation 1 is request
const bit<16> ARP_REPLY = 2; //Operation 2 is reply


/*************************************************************************
*********************** H E A D E R S  ***********************************
*************************************************************************/

typedef bit<9>  egressSpec_t;
typedef bit<48> macAddr_t;
typedef bit<32> ip4Addr_t;

header ethernet_t {
    macAddr_t dstAddr;
    macAddr_t srcAddr;
    bit<16>   etherType;
}

header arp_t {
  bit<16>   h_type;
  bit<16>   p_type;
  bit<8>    h_len;
  bit<8>    p_len;
  bit<16>   op_code;
  macAddr_t src_mac;
  ip4Addr_t src_ip;
  macAddr_t dst_mac;
  ip4Addr_t dstAddr;
  }


header ipv4_t {
    bit<4>    version;
    bit<4>    ihl;
    bit<8>    diffserv;
    bit<16>   totalLen;
    bit<16>   identification;
    bit<3>    flags;
    bit<13>   fragOffset;
    bit<8>    ttl;
    bit<8>    protocol;
    bit<16>   hdrChecksum;
    ip4Addr_t srcAddr;
    ip4Addr_t dstAddr;
}

header tcp_t{
    bit<16> srcPort;
    bit<16> dstPort;
    bit<32> seqNo;
    bit<32> ackNo;
    bit<4>  dataOffset;
    bit<4>  res;
    bit<1>  cwr;
    bit<1>  ece;
    bit<1>  urg;
    bit<1>  ack;
    bit<1>  psh;
    bit<1>  rst;
    bit<1>  syn;
    bit<1>  fin;
    bit<16> window;
    bit<16> checksum;
    bit<16> urgentPtr;
}

header udp_t {
    bit<16>  srcPort;
    bit<16>  dstPort;
    bit<16>  udplen;
    bit<16>  udpchk;
}

header icmp_t {
    bit<8> type;
    bit<8> code;
    bit<16> checksum;
}

header payload_t{
    bit<PAYLOAD_CHUNK_SIZE> payload;
}

struct metadata {
    /* empty */
}

struct headers {
    ethernet_t   ethernet;
    arp_t        arp;
    ipv4_t       ipv4;
    tcp_t        tcp;
    udp_t        udp;
    icmp_t       icmp;
    payload_t    payload;
}

/*************************************************************************
*********************** P A R S E R  ***********************************
*************************************************************************/

parser MyParser(packet_in packet,
                out headers hdr,
                inout metadata meta,
                inout standard_metadata_t standard_metadata) {

    state start {
        packet.extract(hdr.ethernet);
        transition select(hdr.ethernet.etherType) {
          TYPE_ARP: parse_arp;
          TYPE_IPV4: parse_ipv4;
          default: accept;
        }
        
    }

    state parse_arp {
      packet.extract(hdr.arp);
        transition select(hdr.arp.op_code) {
          ARP_REQ: accept;
      }
    }


    state parse_ipv4 {
        packet.extract(hdr.ipv4);
        transition select(hdr.ipv4.protocol){
            8w0x01: parse_icmp;
            TYPE_TCP: tcp;
            8w0x11: parse_udp;
            default: accept;
        }
    }

    state parse_icmp {
        packet.extract(hdr.icmp);
        transition accept;
    }

    state tcp {
       packet.extract(hdr.tcp);
       transition parse_tcp_payload;
    }

    state parse_udp {
       packet.extract(hdr.udp);
       transition accept;
    }
    state parse_tcp_payload {
        packet.extract(hdr.payload);
        //Tcp_option_parser.apply(packet, hdr.tcp.dataOffset,
        //            hdr.tcp_options_vec, hdr.tcp_options_padding);
        transition accept;
    }
}


/*************************************************************************
************   C H E C K S U M    V E R I F I C A T I O N   *************
*************************************************************************/

control MyVerifyChecksum(inout headers hdr, inout metadata meta) {   
    apply {  }
}


/*************************************************************************
**************  I N G R E S S   P R O C E S S I N G   *******************
*************************************************************************/

control MyIngress(inout headers hdr,
                  inout metadata meta,
                  inout standard_metadata_t standard_metadata) {
    
    
    counter(64, CounterType.packets) c;

    action drop() {
        c.count((bit<32>) standard_metadata.ingress_port);
        mark_to_drop(standard_metadata);
    }

    action arp_reply(macAddr_t src_mac) {
      //update operation code from request to reply
      hdr.arp.op_code = ARP_REPLY;
      
      //reply's dst_mac is the request's src mac
      hdr.arp.dst_mac = hdr.arp.src_mac;
      
      //reply's dst_ip is the request's src ip
      hdr.arp.src_mac = src_mac;

      //reply's src ip is the request's dst ip
      hdr.arp.src_ip = hdr.arp.dstAddr;

      //update ethernet header
      hdr.ethernet.dstAddr = hdr.ethernet.srcAddr;
      hdr.ethernet.srcAddr = src_mac;

      //send it back to the same port
      standard_metadata.egress_spec = standard_metadata.ingress_port;
      
    }
    
    action l2_forward(egressSpec_t port) {
        standard_metadata.egress_spec = port;
    }

    action ipv4_forward(macAddr_t dstAddr, egressSpec_t port) {
        c.count((bit<32>) standard_metadata.ingress_port);
        standard_metadata.egress_spec = port;
        hdr.ethernet.srcAddr = hdr.ethernet.dstAddr;
        hdr.ethernet.dstAddr = dstAddr;
        hdr.ipv4.ttl = hdr.ipv4.ttl - 1;
    }

    table arp_exact {
        key = {
            hdr.arp.dstAddr: exact;
        }
        actions = {
            arp_reply;
            drop;
        }
        size = 1024;
        default_action = drop;
    }

    table ipv4_exact {
        key = {
            hdr.ipv4.dstAddr: exact;
        }
        actions = {
            ipv4_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = NoAction();
    }

    table ipv4_exact_src {
        key = {
            hdr.ipv4.srcAddr: exact;
        }
        actions = {
            drop;
            ipv4_forward;
            NoAction;
        }
        size = 1024;
        default_action = NoAction();
    }

    table ipv4_lpm {
        key = {
            hdr.ipv4.dstAddr: lpm;
        }
        actions = {
            ipv4_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = NoAction();
    }

    apply {
        if (hdr.ipv4.isValid()) {
            if (!ipv4_exact_src.apply().hit){
                if (!ipv4_exact.apply().hit) {
                    ipv4_lpm.apply();
                }
            }
        } else if (hdr.ethernet.etherType == TYPE_ARP) {
          //cannot validate ARP header, dunno why :S
          arp_exact.apply();
        } else {
          mark_to_drop(standard_metadata);        
        }
    }
}

/*************************************************************************
****************  E G R E S S   P R O C E S S I N G   *******************
*************************************************************************/

control MyEgress(inout headers hdr,
                 inout metadata meta,
                 inout standard_metadata_t standard_metadata) {
    register<bit<48>>(1000) packet_processing_time_array; //egress timestamp - ingress timestamp
    register<bit<32>>(1000) packet_enqueuing_time_array; //enq_timestamp
    register<bit<19>>(1000) packet_enqueuing_depth_array; //enq_qdepth
    register<bit<32>>(1000) packet_dequeuing_timedelta_array; //deq_timedelta
    register<bit<19>>(1000) packet_dequeuing_depth_array; //deq_qdepth
    
    register<bit<48>>(1) timestamp_last_seen_packet;
    register<bit<32>>(1) last_saved_index;
    bit<48> diff_time;
    bit<48> last_time;
    bit<32> current_index;


    apply {  
        timestamp_last_seen_packet.read(last_time,     0);

        diff_time = standard_metadata.ingress_global_timestamp - last_time;
        if (diff_time > (bit<48>)COLLECTION_TIMEDELTA) { //grab info everytime the window is hit
            //retrieve index
            last_saved_index.read(current_index,     0);
            
            //retrieve packet processing time
            packet_processing_time_array.write(current_index,     
                standard_metadata.egress_global_timestamp-standard_metadata.ingress_global_timestamp);
            
            //retrieve enqueuing timestamp
            packet_enqueuing_time_array.write(current_index,     
                standard_metadata.enq_timestamp);

            //retrieve enqueue depth 
            packet_enqueuing_depth_array.write(current_index,     
                standard_metadata.enq_qdepth);

            //retrieve dequeue timedelta 
            packet_dequeuing_timedelta_array.write(current_index,     
                standard_metadata.deq_timedelta);

            //retrieve dequeuing timestamp
            packet_dequeuing_depth_array.write(current_index,     
                standard_metadata.deq_qdepth);

            //update index
            last_saved_index.write(0,     current_index + 1);
            if(current_index + 1 > 999){
                last_saved_index.write(0,     0);
            }
            
            //reset time window
            timestamp_last_seen_packet.write(0,     standard_metadata.ingress_global_timestamp);  
        }
    }
}

/*************************************************************************
*************   C H E C K S U M    C O M P U T A T I O N   **************
*************************************************************************/

control MyComputeChecksum(inout headers hdr, inout metadata meta) {
     apply {
	    update_checksum(
	    hdr.ipv4.isValid(),
            { hdr.ipv4.version,
	          hdr.ipv4.ihl,
              hdr.ipv4.diffserv,
              hdr.ipv4.totalLen,
              hdr.ipv4.identification,
              hdr.ipv4.flags,
              hdr.ipv4.fragOffset,
              hdr.ipv4.ttl,
              hdr.ipv4.protocol,
              hdr.ipv4.srcAddr,
              hdr.ipv4.dstAddr },
            hdr.ipv4.hdrChecksum,
            HashAlgorithm.csum16);
    }
}


/*************************************************************************
***********************  D E P A R S E R  *******************************
*************************************************************************/

control MyDeparser(packet_out packet, in headers hdr) {
    apply {
        /* TODO: add deparser logic */
        packet.emit(hdr.ethernet);
        packet.emit(hdr.arp);
        packet.emit(hdr.ipv4);
        packet.emit(hdr.icmp);
        packet.emit(hdr.udp);
        packet.emit(hdr.tcp);
        packet.emit(hdr.payload);
    }
}

/*************************************************************************
***********************  S W I T C H  *******************************
*************************************************************************/

V1Switch(
MyParser(),
MyVerifyChecksum(),
MyIngress(),
MyEgress(),
MyComputeChecksum(),
MyDeparser()
) main;
