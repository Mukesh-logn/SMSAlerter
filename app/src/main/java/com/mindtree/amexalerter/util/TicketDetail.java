package com.mindtree.amexalerter.util;

/**
 * Created by M1030452 on 4/18/2018.
 */

public class TicketDetail {
    private String inc, severity, queueName, ticketDesc, ticketAcceptanceTime,
            ticketReceiveTime, acceptanceMessageTime,ticketAcceptedBy;

    public TicketDetail(String inc, String severity, String queueName, String ticketDesc, String ticketAcceptanceTime, String ticketReceiveTime, String acceptanceMessageTime) {
        this.inc = inc;
        this.severity = severity;
        this.queueName = queueName;
        this.ticketDesc = ticketDesc;
        this.ticketAcceptanceTime = ticketAcceptanceTime;
        this.ticketReceiveTime = ticketReceiveTime;
        this.acceptanceMessageTime = acceptanceMessageTime;

    }

    public TicketDetail() {


    }

    public String getInc() {
        return inc;
    }

    public void setInc(String inc) {
        this.inc = inc;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getTicketDesc() {
        return ticketDesc;
    }

    public void setTicketDesc(String ticketDesc) {
        this.ticketDesc = ticketDesc;
    }

    public String getTicketAcceptanceTime() {
        return ticketAcceptanceTime;
    }

    public void setTicketAcceptanceTime(String ticketAcceptanceTime) {
        this.ticketAcceptanceTime = ticketAcceptanceTime;
    }

    public String getTicketReceiveTime() {
        return ticketReceiveTime;
    }

    public void setTicketReceiveTime(String ticketReceiveTime) {
        this.ticketReceiveTime = ticketReceiveTime;
    }

    public String getAcceptanceMessageTime() {
        return acceptanceMessageTime;
    }

    public void setAcceptanceMessageTime(String acceptanceMessageTime) {
        this.acceptanceMessageTime = acceptanceMessageTime;
    }

    public String getTicketAcceptedBy() {
        return ticketAcceptedBy;
    }

    public void setTicketAcceptedBy(String ticketAcceptedBy) {
        this.ticketAcceptedBy = ticketAcceptedBy;
    }
}
