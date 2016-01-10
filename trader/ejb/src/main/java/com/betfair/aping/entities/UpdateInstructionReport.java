package com.betfair.aping.entities;

import com.betfair.aping.enums.InstructionReportErrorCode;
import com.betfair.aping.enums.InstructionReportStatus;

/**
 * Created by Eugene on 10.01.2016.
 */
public class UpdateInstructionReport {

    InstructionReportStatus status;         //    whether the command succeeded or failed

    InstructionReportErrorCode errorCode;  //    cause of failure, or null if command succeeds

    UpdateInstruction instruction;          //    The instruction that was requested

    public InstructionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(InstructionReportErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public UpdateInstruction getInstruction() {
        return instruction;
    }

    public void setInstruction(UpdateInstruction instruction) {
        this.instruction = instruction;
    }

    public InstructionReportStatus getStatus() {
        return status;
    }

    public void setStatus(InstructionReportStatus status) {
        this.status = status;
    }
}
