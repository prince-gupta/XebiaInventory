package com.xebia.dto;

import com.xebia.entities.Employee;

/**
 * Created by Pgupta on 19-09-2016.
 */
public class EmployeeSearchDTO extends Employee {
    private int offset;
    private int limit;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
