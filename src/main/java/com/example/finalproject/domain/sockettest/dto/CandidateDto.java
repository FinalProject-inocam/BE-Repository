package com.example.finalproject.domain.sockettest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CandidateDto {
    private String address;
    private String candidate;
    private String component;
    private String foundation;
    private String port;
    private String priority;
    private String protocol;
    private String relatedAddress;
    private String relatedPort;
    private Long sdpMLineIndex;
    private String sdpMid;
    private String tcpType;
    private String type;
    private String usernameFragment;


    @Builder
    public CandidateDto(String address, String candidate, String component, String foundation, String port, String priority, String protocol, String relatedAddress, String relatedPort, Long sdpMLineIndex, String sdpMid, String tcpType, String type, String usernameFragment) {
        this.address = address;
        this.candidate = candidate;
        this.component = component;
        this.foundation = foundation;
        this.port = port;
        this.priority = priority;
        this.protocol = protocol;
        this.relatedAddress = relatedAddress;
        this.relatedPort = relatedPort;
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdpMid = sdpMid;
        this.tcpType = tcpType;
        this.type = type;
        this.usernameFragment = usernameFragment;
    }
}