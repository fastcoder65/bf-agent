package com.betfair.aping.entities;

import com.betfair.aping.enums.InstructionReportErrorCode;
import com.betfair.aping.enums.InstructionReportStatus;

import java.util.Date;

/**
 * Created by Eugene on 09.01.2016.
 */
public class CancelInstructionReport {

    InstructionReportStatus status;         // whether the command succeeded or failed

    InstructionReportErrorCode errorCode;   // cause of failure, or null if command succeeds

    CancelInstruction instruction;          // The instruction that was requested

    double sizeCancelled;

    Date cancelledDate;

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public InstructionReportStatus getStatus() {
        return status;
    }

    public void setStatus(InstructionReportStatus status) {
        this.status = status;
    }

    public InstructionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(InstructionReportErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CancelInstruction getInstruction() {
        return instruction;
    }

    public void setInstruction(CancelInstruction instruction) {
        this.instruction = instruction;
    }

    public double getSizeCancelled() {
        return sizeCancelled;
    }

    public void setSizeCancelled(double sizeCancelled) {
        this.sizeCancelled = sizeCancelled;
    }

    @Override
    public String toString() {
        return "CancelInstructionReport{" +
                "cancelledDate=" + cancelledDate +
                ", status=" + status +
                ", errorCode=" + errorCode +
                ", instruction=" + instruction +
                ", sizeCancelled=" + sizeCancelled +
                '}';
    }
}
