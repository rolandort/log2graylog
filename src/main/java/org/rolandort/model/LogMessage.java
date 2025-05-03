package org.rolandort.model;
import lombok.Data;
import com.google.gson.annotations.SerializedName;

@Data
public class LogMessage {

  @SerializedName("ClientDeviceType")
  private String clientDeviceType;        // "desktop"
  
  @SerializedName("ClientIP")
  private String clientIp;                // "192.168.87.52"
  
  @SerializedName("ClientIPClass")
  private String clientIpClass;           // noRecord
  
  @SerializedName("ClientStatus")
  private Integer clientStatus;           // 403
  
  @SerializedName("ClientRequestBytes")
  private Integer clientRequestBytes;     // 889
  
  @SerializedName("ClientRequestReferer")
  private String clientRequestReferer;    // "graylog.org"
  
  @SerializedName("ClientRequestURI")
  private String clientRequestUri;        // "/search"
  
  @SerializedName("ClientRequestUserAgent")
  private String clientRequestUserAgent;  // "Mozilla/5.0 ..."
  
  @SerializedName("ClientSrcPort")
  private Integer clientSrcPort;          // 122
  
  @SerializedName("EdgeServerIP")
  private String edgeServerIp;            // "10.0.151.71"
  
  @SerializedName("EdgeStartTimestamp")
  private Double edgeStartTimestamp;      // 1576929197
  
  @SerializedName("DestinationIP")
  private String destinationIp;           // "172.16.153.30"
  
  @SerializedName("OriginResponseBytes")
  private Integer originResponseBytes;    // 821
  
  @SerializedName("OriginResponseTime")
  private Integer originResponseTime;     // 337000000
}
