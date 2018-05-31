create or replace procedure FORALL_STATIC_BASE

(

 base_array in STATIC_BASE_array,

 rowcount out number,

 msg out varchar2

 ) as

  baseObj RISKRESULT_STATIC_BASE_OBJ;
  --≈˙¡ø≤Â»Î

begin

  for i in base_array.First() .. base_array.Last() loop

    baseObj := base_array(i);

    insert into riskresult_static
      (VC_RESULT_ID,
       i_riks_id,
       vc_risk_description,
       i_spring_date,
       i_spring_time,
       c_declare_type,
       vc_index_unit,
       i_compare_bs,
       i_fund,
       vc_stock_code,
       VC_OFFEND_TYPE,
       EN_VALVE_VALUE,
       EN_RESULT_VALUE,
       VC_FZ_VALUE,
       VC_FM_VALUE,
       VC_SERIAL_NUMBER
       )

    values
      (baseObj.vc_result_id,
       baseObj.i_riks_id,
       baseObj.vc_risk_description,
       baseObj.i_spring_date,
       baseObj.i_spring_time,
       baseObj.c_declare_type,
       baseObj.vc_index_unit,
       baseObj.i_compare_bs,
       baseObj.i_fund,
       baseObj.vc_stock_code,
       baseObj.offendType,
       baseObj.enValue,
       baseObj.enResultValue,
       baseObj.VC_FZ_VALUE,
       baseObj.VC_FM_VALUE,
       baseObj.VC_SERIAL_NUMBER
       );

  end loop;

  rowcount := sql%rowcount;

  commit;

exception

  when others then

    rowcount := 0;

    msg := sqlerrm;

    rollback;

end FORALL_STATIC_BASE;
/
