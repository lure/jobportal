package ru.shubert.jobportal.dao;
import java.io.Serializable;

/**
 * Holder for additional query parameters:
 * <ul>
 * <li><code>java.lang.String</code> field for "Order by" clause
 * <li><code>boolean</code> is order ascending (true|false)
 * <li><code>int</code> first result row
 * <li><code>int</code> total row count
 * </ul>
 */
public class QueryParams implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderField;
    private boolean orderAsc;
    private int first;
    private int count;

    public QueryParams(int first, int count) {
        this.first = first;
        this.count = count;
    }

    public String getOrderField() {
        return orderField;
    }

    public QueryParams setOrderField(String orderField) {
        this.orderField = orderField;
        return this;
    }

    public boolean getOrderAsc() {
        return orderAsc;
    }

    public QueryParams setOrderAsc(boolean orderAsc) {
        this.orderAsc = orderAsc;
        return this;
    }

    public int getFirst() {
        return first;
    }

    public QueryParams setFirst(int first) {
        this.first = first;
        return this;
    }

    public int getCount() {
        return count;
    }

    public QueryParams setCount(int count) {
        this.count = count;
        return this;
    }
}