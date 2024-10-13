/*
 * This file is generated by jOOQ.
 */
package db.tables.records;


import db.tables.Messages;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MessagesRecord extends UpdatableRecordImpl<MessagesRecord> implements Record5<Integer, String, Integer, Integer, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>cs.messages.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>cs.messages.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>cs.messages.content</code>.
     */
    public void setContent(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>cs.messages.content</code>.
     */
    public String getContent() {
        return (String) get(1);
    }

    /**
     * Setter for <code>cs.messages.sender_id</code>.
     */
    public void setSenderId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>cs.messages.sender_id</code>.
     */
    public Integer getSenderId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>cs.messages.receiver_id</code>.
     */
    public void setReceiverId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>cs.messages.receiver_id</code>.
     */
    public Integer getReceiverId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>cs.messages.created_date</code>.
     */
    public void setCreatedDate(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>cs.messages.created_date</code>.
     */
    public LocalDateTime getCreatedDate() {
        return (LocalDateTime) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Integer, String, Integer, Integer, LocalDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Integer, String, Integer, Integer, LocalDateTime> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Messages.MESSAGES.ID;
    }

    @Override
    public Field<String> field2() {
        return Messages.MESSAGES.CONTENT;
    }

    @Override
    public Field<Integer> field3() {
        return Messages.MESSAGES.SENDER_ID;
    }

    @Override
    public Field<Integer> field4() {
        return Messages.MESSAGES.RECEIVER_ID;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return Messages.MESSAGES.CREATED_DATE;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getContent();
    }

    @Override
    public Integer component3() {
        return getSenderId();
    }

    @Override
    public Integer component4() {
        return getReceiverId();
    }

    @Override
    public LocalDateTime component5() {
        return getCreatedDate();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getContent();
    }

    @Override
    public Integer value3() {
        return getSenderId();
    }

    @Override
    public Integer value4() {
        return getReceiverId();
    }

    @Override
    public LocalDateTime value5() {
        return getCreatedDate();
    }

    @Override
    public MessagesRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public MessagesRecord value2(String value) {
        setContent(value);
        return this;
    }

    @Override
    public MessagesRecord value3(Integer value) {
        setSenderId(value);
        return this;
    }

    @Override
    public MessagesRecord value4(Integer value) {
        setReceiverId(value);
        return this;
    }

    @Override
    public MessagesRecord value5(LocalDateTime value) {
        setCreatedDate(value);
        return this;
    }

    @Override
    public MessagesRecord values(Integer value1, String value2, Integer value3, Integer value4, LocalDateTime value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MessagesRecord
     */
    public MessagesRecord() {
        super(Messages.MESSAGES);
    }

    /**
     * Create a detached, initialised MessagesRecord
     */
    public MessagesRecord(Integer id, String content, Integer senderId, Integer receiverId, LocalDateTime createdDate) {
        super(Messages.MESSAGES);

        setId(id);
        setContent(content);
        setSenderId(senderId);
        setReceiverId(receiverId);
        setCreatedDate(createdDate);
    }
}