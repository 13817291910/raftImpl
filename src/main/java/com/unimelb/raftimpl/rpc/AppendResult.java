/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.unimelb.raftimpl.rpc;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-04-25")
public class AppendResult implements org.apache.thrift.TBase<AppendResult, AppendResult._Fields>, java.io.Serializable, Cloneable, Comparable<AppendResult> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("AppendResult");

  private static final org.apache.thrift.protocol.TField TERM_FIELD_DESC = new org.apache.thrift.protocol.TField("term", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.BOOL, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new AppendResultStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new AppendResultTupleSchemeFactory();

  public int term; // required
  public boolean success; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    TERM((short)1, "term"),
    SUCCESS((short)2, "success");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // TERM
          return TERM;
        case 2: // SUCCESS
          return SUCCESS;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __TERM_ISSET_ID = 0;
  private static final int __SUCCESS_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.TERM, new org.apache.thrift.meta_data.FieldMetaData("term", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(AppendResult.class, metaDataMap);
  }

  public AppendResult() {
  }

  public AppendResult(
    int term,
    boolean success)
  {
    this();
    this.term = term;
    setTermIsSet(true);
    this.success = success;
    setSuccessIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public AppendResult(AppendResult other) {
    __isset_bitfield = other.__isset_bitfield;
    this.term = other.term;
    this.success = other.success;
  }

  public AppendResult deepCopy() {
    return new AppendResult(this);
  }

  @Override
  public void clear() {
    setTermIsSet(false);
    this.term = 0;
    setSuccessIsSet(false);
    this.success = false;
  }

  public int getTerm() {
    return this.term;
  }

  public AppendResult setTerm(int term) {
    this.term = term;
    setTermIsSet(true);
    return this;
  }

  public void unsetTerm() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __TERM_ISSET_ID);
  }

  /** Returns true if field term is set (has been assigned a value) and false otherwise */
  public boolean isSetTerm() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __TERM_ISSET_ID);
  }

  public void setTermIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __TERM_ISSET_ID, value);
  }

  public boolean isSuccess() {
    return this.success;
  }

  public AppendResult setSuccess(boolean success) {
    this.success = success;
    setSuccessIsSet(true);
    return this;
  }

  public void unsetSuccess() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __SUCCESS_ISSET_ID);
  }

  /** Returns true if field success is set (has been assigned a value) and false otherwise */
  public boolean isSetSuccess() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __SUCCESS_ISSET_ID);
  }

  public void setSuccessIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __SUCCESS_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case TERM:
      if (value == null) {
        unsetTerm();
      } else {
        setTerm((java.lang.Integer)value);
      }
      break;

    case SUCCESS:
      if (value == null) {
        unsetSuccess();
      } else {
        setSuccess((java.lang.Boolean)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case TERM:
      return getTerm();

    case SUCCESS:
      return isSuccess();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case TERM:
      return isSetTerm();
    case SUCCESS:
      return isSetSuccess();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof AppendResult)
      return this.equals((AppendResult)that);
    return false;
  }

  public boolean equals(AppendResult that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_term = true;
    boolean that_present_term = true;
    if (this_present_term || that_present_term) {
      if (!(this_present_term && that_present_term))
        return false;
      if (this.term != that.term)
        return false;
    }

    boolean this_present_success = true;
    boolean that_present_success = true;
    if (this_present_success || that_present_success) {
      if (!(this_present_success && that_present_success))
        return false;
      if (this.success != that.success)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + term;

    hashCode = hashCode * 8191 + ((success) ? 131071 : 524287);

    return hashCode;
  }

  @Override
  public int compareTo(AppendResult other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetTerm()).compareTo(other.isSetTerm());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTerm()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.term, other.term);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetSuccess()).compareTo(other.isSetSuccess());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSuccess()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, other.success);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("AppendResult(");
    boolean first = true;

    sb.append("term:");
    sb.append(this.term);
    first = false;
    if (!first) sb.append(", ");
    sb.append("success:");
    sb.append(this.success);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class AppendResultStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public AppendResultStandardScheme getScheme() {
      return new AppendResultStandardScheme();
    }
  }

  private static class AppendResultStandardScheme extends org.apache.thrift.scheme.StandardScheme<AppendResult> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, AppendResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // TERM
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.term = iprot.readI32();
              struct.setTermIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SUCCESS
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.success = iprot.readBool();
              struct.setSuccessIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, AppendResult struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(TERM_FIELD_DESC);
      oprot.writeI32(struct.term);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
      oprot.writeBool(struct.success);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class AppendResultTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public AppendResultTupleScheme getScheme() {
      return new AppendResultTupleScheme();
    }
  }

  private static class AppendResultTupleScheme extends org.apache.thrift.scheme.TupleScheme<AppendResult> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, AppendResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetTerm()) {
        optionals.set(0);
      }
      if (struct.isSetSuccess()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetTerm()) {
        oprot.writeI32(struct.term);
      }
      if (struct.isSetSuccess()) {
        oprot.writeBool(struct.success);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, AppendResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.term = iprot.readI32();
        struct.setTermIsSet(true);
      }
      if (incoming.get(1)) {
        struct.success = iprot.readBool();
        struct.setSuccessIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

