package com.betfair.aping.entities;

import com.betfair.aping.enums.ExecutionReportErrorCode;
import com.betfair.aping.enums.ExecutionReportStatus;

import java.util.List;

/**
 * Created by Eugene on 09.01.2016.
 */
public class CancelExecutionReport {
    String customerRef; // Echo of the customerRef if passed.

    ExecutionReportStatus status;

    ExecutionReportErrorCode errorCode;

    @Override
    public String toString() {
        return "CancelExecutionReport{" +
                "customerRef='" + customerRef + '\'' +
                ", status=" + status +
                ", errorCode=" + errorCode +
                ", marketId='" + marketId + '\'' +
                ", instructionReports=" + instructionReports +
                '}';
    }

    String marketId; //    Echo of marketId passed

    List< CancelInstructionReport > instructionReports;

    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public ExecutionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ExecutionReportErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public List<CancelInstructionReport> getInstructionReports() {
        return instructionReports;
    }

    public void setInstructionReports(List<CancelInstructionReport> instructionReports) {
        this.instructionReports = instructionReports;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public ExecutionReportStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionReportStatus status) {
        this.status = status;
    }

}
