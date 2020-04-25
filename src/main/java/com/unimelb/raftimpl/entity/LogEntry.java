package com.unimelb.raftimpl.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class LogEntry implements Serializable,Comparable {

    private long index;
    private int term;
    private String text;

    @Override
    public int compareTo(Object o) {
        if(o == null) return 1;
        if(this.getIndex() > ((LogEntry) o).getIndex()){
            return 1;
        }else{
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEntry logEntry = (LogEntry) o;
        return index == logEntry.index &&
                term == logEntry.term &&
                Objects.equals(text, logEntry.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, term, text);
    }

}
