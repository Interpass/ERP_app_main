package app.erp.com.erp_app.callcenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import app.erp.com.erp_app.ERP_Spring_Controller;
import app.erp.com.erp_app.adapter.My_Error_Adapter;
import app.erp.com.erp_app.R;
import app.erp.com.erp_app.gtv_fragment.Fragement_Gtv_Emp_List;
import app.erp.com.erp_app.vo.Gtv_Report_Vo;
import app.erp.com.erp_app.vo.Trouble_HistoryListVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hsra on 2019-06-21.
 */

public class Fragment_trouble_list extends Fragment {

    Context context;

    My_Error_Adapter adapter;
    ListView listView;

    RadioGroup trouble_history_list;
    RadioButton trouble_bus , trouble_nms, trouble_chager , trouble_bit , trouble_nomal;

    TextView nomal_count , bus_count, nms_count,chager_count , bit_count;

    private Retrofit retrofit;
    SharedPreferences pref,page_check_info;
    SharedPreferences.Editor editor;

    String service_id;
    String msg_check3;
    Trouble_HistoryListVO deleteVo;

    public Fragment_trouble_list(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_trouble_list, container ,false);
        context = getActivity();
        pref = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        ((Call_Center_Activity)getActivity()).switchFragment("care");

        page_check_info = context.getSharedPreferences("page_check_info" ,  Context.MODE_PRIVATE);
        editor = page_check_info.edit();
        editor.putString("page_check","trouble_care");
        editor.commit();

        // 키보드 내려가는 부분
        try{
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LinearLayout main_layout = view.findViewById(R.id.main_layout);
                    InputMethodManager mInputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(main_layout.getWindowToken(),0);
                }
            },250);
        }catch (Exception e){
            e.printStackTrace();
        }

        //상단 라디오 버튼
        trouble_bus = (RadioButton) view.findViewById(R.id.trouble_bus);
        trouble_nms = (RadioButton) view.findViewById(R.id.trouble_nms);
        trouble_chager = (RadioButton) view.findViewById(R.id.trouble_chager);
        trouble_bit = (RadioButton) view.findViewById(R.id.trouble_bit);
        trouble_nomal = (RadioButton)view.findViewById(R.id.trouble_nomal);

        // 상단 라디오 버튼 옆 카운트 text
        nomal_count = (TextView)view.findViewById(R.id.nomal_count);
        bus_count = (TextView)view.findViewById(R.id.bus_count);
        nms_count = (TextView)view.findViewById(R.id.nms_count);
        chager_count = (TextView)view.findViewById(R.id.chager_count);
        bit_count = (TextView)view.findViewById(R.id.bit_count);

        trouble_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trouble_bus.setChecked(true);
                trouble_nms.setChecked(false);
                trouble_chager.setChecked(false);
                trouble_bit.setChecked(false);
                trouble_nomal.setChecked(false);

                service_id = "01";
                new Filed_MyErrorList().execute();
            }
        });

        trouble_nms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trouble_bus.setChecked(false);
                trouble_nms.setChecked(true);
                trouble_chager.setChecked(false);
                trouble_bit.setChecked(false);
                trouble_nomal.setChecked(false);

                service_id = "02";
                new Filed_MyErrorList().execute();
            }
        });

        trouble_chager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trouble_bus.setChecked(false);
                trouble_nms.setChecked(false);
                trouble_chager.setChecked(true);
                trouble_bit.setChecked(false);
                trouble_nomal.setChecked(false);

                service_id = "04";
                new Filed_MyErrorList().execute();
            }
        });

        trouble_bit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trouble_bus.setChecked(false);
                trouble_nms.setChecked(false);
                trouble_chager.setChecked(false);
                trouble_bit.setChecked(true);
                trouble_nomal.setChecked(false);

                service_id = "13";
                new Filed_MyErrorList().execute();
            }
        });

        trouble_nomal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trouble_bus.setChecked(false);
                trouble_nms.setChecked(false);
                trouble_chager.setChecked(false);
                trouble_bit.setChecked(false);
                trouble_nomal.setChecked(true);

                service_id = "09";
                new Filed_MyErrorList().execute();
            }
        });

        String emp_id = pref.getString("emp_id","inter");
        adapter = new My_Error_Adapter(emp_id);
        adapter.setDefaultRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int postion = (int) v.getTag();
                Trouble_HistoryListVO thlvo = adapter.resultItem(postion);

                Fragment_trobule_care fragment = new Fragment_trobule_care();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Obj",thlvo);
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frage_change,fragment)
                        .commit();

            }
        });

        adapter.setEqual_bus_btn_Listener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion = (int) v.getTag();
                Trouble_HistoryListVO thlvo = adapter.resultItem(postion);

                Fragment_trobule_equal_infra_insert fragment = new Fragment_trobule_equal_infra_insert();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Obj",thlvo);
                fragment.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frage_change,fragment);
                ft.commit();
            }
        });

        adapter.setCall_text_listener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String tel_num = view.getTag().toString().replaceAll("-","");
                String tel = "tel:"+tel_num;
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
            }
        });

        adapter.setMove_info_btn(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postion = (int) view.getTag();
                final Trouble_HistoryListVO thlvo = adapter.resultItem(postion);
                deleteVo = adapter.resultItem(postion);

                ArrayList<Trouble_HistoryListVO> list = adapter.resultList();
                String emp_id = pref.getString("emp_id","inter");

                final HashMap<String,Object> map = new HashMap<>();
                map.put("move_emp_id",emp_id);
                map.put("reg_date",thlvo.getReg_date());
                map.put("reg_time",thlvo.getReg_time());
                map.put("reg_emp_id",thlvo.getReg_emp_id());
                // N = 이동 시작하는 경우
                String msg_check = "N";
                // 내가 작업중인지 아닌지
                String msg_check2 = "N";
                // 내가 다른곳 지원 나갔는데 또 다른운수사로 그냥 이동하는거 체크
                msg_check3 = "Y";

                // M = 다른 운수사로 이동중인데 선택한 운수사로 이동하는 경우
                for(int i=0; i<list.size();i++){

                    // move_emp_id 가 있고 내 아이디랑 동일하면서 지원중인건
                    if(list.get(i).getMove_emp_id() != null && list.get(i).getMove_emp_id().equals(emp_id) && list.get(i).getFirst_yn().equals("N")){
                        msg_check3 = "N";
                        deleteVo = list.get(i);
                    }

                    // move_emp_id 가 있고 내 아이디랑 동일하면서 최초등록건
                    if(list.get(i).getMove_emp_id() != null && list.get(i).getMove_emp_id().equals(emp_id) && list.get(i).getFirst_yn().equals("Y")){
                        map.put("m_move_emp_id",list.get(i).getMove_emp_id());
                        map.put("m_reg_date",list.get(i).getReg_date());
                        map.put("m_reg_time",list.get(i).getReg_time());
                        map.put("m_reg_emp_id",list.get(i).getReg_emp_id());
                        msg_check = "M";
                        msg_check2 = "Y";
                    }
                };

                // C = 다른 운수사로 이동중에서 선택한 운수사를 이미 다른작업자가 이동중인데 자신이 이동하는경우
                if(msg_check2.equals("Y") && thlvo.getMove_emp_id() !=null && !thlvo.getMove_emp_id().equals(emp_id)){
//                    Log.d("ccccccccccccc","ccccccccccccccc");
                    msg_check = "C";
                }

                // A = 선택한 운수사를 이미 다른작업자가 이동중인데 자신이 이동하는경우
                if(!msg_check2.equals("Y") && thlvo.getMove_emp_id() !=null && !thlvo.getMove_emp_id().equals(emp_id)){
//                    Log.d("aaaaaaaaaaaa","aaaaaaaaaa");
                    msg_check = "A";
                }

                // S = 선택한 운수사를 이미 이동중이여서 이동취소하는경우
                if(thlvo.getMove_emp_id() !=null && thlvo.getMove_emp_id().equals(emp_id)){
                    map.put("move_emp_id","");
                    msg_check = "S";
                }

                map.put("msg_check",msg_check);
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
                alertdialog.setTitle("위치 이동하기");

                String p_btn_text = "지원하기";
                String n_btn_text = "이동하기";

                switch (msg_check){
                    case "N":
                        alertdialog.setMessage("해당 운수사로 이동하시겠습니까?");
                        break;
                    case "M" :
                        alertdialog.setMessage("이미 이동중입니다. 다른곳으로 이동하시겠습니까?");
                        break;
                    case "A":
                        alertdialog.setMessage("다른 작업자가 이미 이동중입니다. 이동하시겠습니까?");
                        break;
                    case "S":
                        alertdialog.setMessage("해당 운수사로 이미 이동중입니다. 이동 취소하시겠습니까?");
                        n_btn_text = "취소하기";
                        break;
                    case "C":
                        alertdialog.setMessage("다른 작업자가 이미 이동중입니다. 이동하시겠습니까?");
                        break;
                }



                alertdialog
                        .setCancelable(false)
                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                if(!msg_check.equals("S") && !msg_check.equals("N") && !msg_check.equals("M") ){
                    alertdialog.setPositiveButton(p_btn_text,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String emp_id = pref.getString("emp_id","inter");
                                    ERP_Spring_Controller erp = ERP_Spring_Controller.retrofit.create(ERP_Spring_Controller.class);
                                    Call<Boolean> call = erp.trouble_move_together_insert(map,thlvo, emp_id );
                                    new Fragment_trouble_list.trouble_move_together().execute(call);
                                    dialog.cancel();
                                }
                            });
                }
                alertdialog.setNegativeButton(n_btn_text,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ERP_Spring_Controller erp = ERP_Spring_Controller.retrofit.create(ERP_Spring_Controller.class);
                                Call<Boolean> call = erp.trouble_move_change(map,deleteVo,msg_check3);
                                new Fragment_trouble_list.Update_Move_Emp_id().execute(call);
                                dialog.cancel();
                            }
                        });
                AlertDialog adialog = alertdialog.create();
                adialog.show();
            }
        });

        listView = (ListView)view.findViewById(R.id.my_error_list);

        trouble_bus.performClick();

        ERP_Spring_Controller erp = ERP_Spring_Controller.retrofit.create(ERP_Spring_Controller.class);
        Call<List<Trouble_HistoryListVO>> call = erp.get_trouble_count(emp_id);
        new Fragment_trouble_list.Get_Trouble_Count().execute(call);

        return view;
    }

    private class Filed_MyErrorList extends AsyncTask<String , Integer , Long>{
        @Override
        protected Long doInBackground(String... strings) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.test_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ERP_Spring_Controller erp = retrofit.create(ERP_Spring_Controller.class);
            String emp_id = pref.getString("emp_id","inter");
            Call<List<Trouble_HistoryListVO>> call = erp.getfield_my_error_list(emp_id , service_id);
            call.enqueue(new Callback<List<Trouble_HistoryListVO>>() {
                @Override
                public void onResponse(Call<List<Trouble_HistoryListVO>> call, Response<List<Trouble_HistoryListVO>> response) {
                    try{
                        List<Trouble_HistoryListVO> list = response.body();
                        MakeMyErrorList(list);
                    }catch (Exception e){
                        Toast.makeText(context,"데이터가 없습니다.",Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<List<Trouble_HistoryListVO>> call, Throwable t) {

                }
            });
            return null;
        }
    }

    private void MakeMyErrorList(List<Trouble_HistoryListVO> list) {
        adapter.clearItem();
        listView.setAdapter(adapter);

        if(null == list || 0 == list.size()){
            Toast.makeText(context,"데이터가 없습니다.",Toast.LENGTH_SHORT).show();
            return;
        }

        for(Trouble_HistoryListVO i : list){
            adapter.addItem(i);
        }
    }

    private class Get_Trouble_Count extends AsyncTask<Call , Void , List<Trouble_HistoryListVO>>{
        @Override
        protected List<Trouble_HistoryListVO> doInBackground(Call... calls) {
            try{
                Call<List<Trouble_HistoryListVO>> call = calls[0];
                Response<List<Trouble_HistoryListVO>> response = call.execute();
                return response.body();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Trouble_HistoryListVO> trouble_historyListVOS) {
            Trouble_HistoryListVO vo = trouble_historyListVOS.get(0);

            bus_count.setText(text_plus(vo.getBus()));
            nms_count.setText(text_plus(vo.getJib()));
            chager_count.setText(text_plus(vo.getCharge()));
            bit_count.setText(text_plus(vo.getBit()));
            nomal_count.setText(text_plus(vo.getIb()));

        }
    }

    private class Update_Move_Emp_id extends AsyncTask<Call, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Call... calls) {
            try{
                Call<Boolean> call = calls[0];
                Response<Boolean> response = call.execute();
                return response.body();
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            Fragment fragment = new Fragment_trouble_list();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frage_change,fragment);
            ft.commit();
        }
    }

    private class trouble_move_together extends AsyncTask<Call , Void , Boolean>{
        @Override
        protected Boolean doInBackground(Call... calls) {
            try{
                Call<Boolean> call = calls[0];
                Response<Boolean> response = call.execute();
                return response.body();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            Fragment fragment = new Fragment_trouble_list();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frage_change,fragment);
            ft.commit();
        }
    }

    String text_plus (String text){
        return "(" + text + ")";
    }

}