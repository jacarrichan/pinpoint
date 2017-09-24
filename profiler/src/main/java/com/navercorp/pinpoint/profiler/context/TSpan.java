package com.navercorp.pinpoint.profiler.context;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by jianglei on 2017/9/24.
 */
public class TSpan {

    private String agentId; // required
    private String applicationName; // required
    private long agentStartTime; // required
    private ByteBuffer transactionId; // required
    private long spanId; // required
    private long parentSpanId; // optional
    private long startTime; // required
    private int elapsed; // optional
    private String rpc; // optional
    private short serviceType; // required
    private String endPoint; // optional
    private String remoteAddr; // optional
    private short flag; // optional
    private int err; // optional
    private List<TSpanEvent> spanEventList; // optional
    private String parentApplicationName; // optional
    private short parentApplicationType; // optional
    private String acceptorHost; // optional
    private int apiId; // optional
    private short applicationServiceType; // optional
    private byte loggingTransactionInfo; // optional

    public void setSpanId(long spanId) {
        this.spanId = spanId;
    }

    public void setParentSpanId(long parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    public long getParentSpanId() {
        return parentSpanId;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public short getFlag() {
        return flag;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getSpanId() {
        return spanId;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public long getAgentStartTime() {
        return agentStartTime;
    }

    public void setAgentStartTime(long agentStartTime) {
        this.agentStartTime = agentStartTime;
    }

    public ByteBuffer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(ByteBuffer transactionId) {
        this.transactionId = transactionId;
    }

    public int getElapsed() {
        return elapsed;
    }

    public void setElapsed(int elapsed) {
        this.elapsed = elapsed;
    }

    public String getRpc() {
        return rpc;
    }

    public void setRpc(String rpc) {
        this.rpc = rpc;
    }

    public short getServiceType() {
        return serviceType;
    }

    public void setServiceType(short serviceType) {
        this.serviceType = serviceType;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public List<TSpanEvent> getSpanEventList() {
        return spanEventList;
    }

    public void setSpanEventList(List<TSpanEvent> spanEventList) {
        this.spanEventList = spanEventList;
    }

    public String getParentApplicationName() {
        return parentApplicationName;
    }

    public void setParentApplicationName(String parentApplicationName) {
        this.parentApplicationName = parentApplicationName;
    }

    public short getParentApplicationType() {
        return parentApplicationType;
    }

    public void setParentApplicationType(short parentApplicationType) {
        this.parentApplicationType = parentApplicationType;
    }

    public String getAcceptorHost() {
        return acceptorHost;
    }

    public void setAcceptorHost(String acceptorHost) {
        this.acceptorHost = acceptorHost;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    public short getApplicationServiceType() {
        return applicationServiceType;
    }

    public void setApplicationServiceType(short applicationServiceType) {
        this.applicationServiceType = applicationServiceType;
    }

    public byte getLoggingTransactionInfo() {
        return loggingTransactionInfo;
    }

    public void setLoggingTransactionInfo(byte loggingTransactionInfo) {
        this.loggingTransactionInfo = loggingTransactionInfo;
    }

    public void markBeforeTime() {

    }
}

