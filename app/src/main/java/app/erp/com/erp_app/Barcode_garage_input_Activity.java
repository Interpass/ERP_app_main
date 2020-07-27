package app.erp.com.erp_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import app.erp.com.erp_app.adapter.UnitListAdapter;
import app.erp.com.erp_app.vo.UnitList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hsra on 2019-06-21.
 */

public class Barcode_garage_input_Activity extends AppCompatActivity {

    Button scan_btn , scan_unit, submit_barcode, view_refresh;
    Context context;
    private Retrofit retrofit;
    private Gson mgsom;
    String click_type ,office_barcode,area_code;
    List<String> scan_unit_barcodes;
    List<UnitList> unit_list_all;
    TextView scan_office, local_office;
    SharedPreferences pref , barcode_type_pref;
    SharedPreferences.Editor editor;
    int index_num ;

    ListView listView;
    UnitListAdapter adapter;

    ProgressDialog progressDialog;

    public Barcode_garage_input_Activity(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_barcode_garage_input);
        context = this;
        scan_unit_barcodes = new ArrayList<>();

        ArrayList<UnitList> itme = new ArrayList<UnitList>();

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        adapter = new UnitListAdapter();
        listView = (ListView)findViewById(R.id.add_unit_list);

        scan_office = (TextView)findViewById(R.id.scan_office);
        local_office = (TextView)findViewById(R.id.local_office);
        pref = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);

        barcode_type_pref = context.getSharedPreferences("barcode_type", Context.MODE_PRIVATE);
        editor = barcode_type_pref.edit();

        index_num = 1;
        new Unist_List().execute();

        scan_btn = (Button)findViewById(R.id.scan_barcode);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_type = "scan";
                editor.putString("camera_type" , "office");
                editor.commit();
                new IntentIntegrator(Barcode_garage_input_Activity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
//                IntentIntegrator.forFragment(Barcode_garage_input_Activity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }
        });

        scan_unit = (Button)findViewById(R.id.scan_unit);
        scan_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_type = "unit";
                editor.putString("camera_type" , "unit");
                editor.commit();
                new IntentIntegrator(Barcode_garage_input_Activity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
//                IntentIntegrator.forFragment(Barcode_garage_input_Activity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }
        });

        submit_barcode = (Button)findViewById(R.id.submit_barcode);
        submit_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(scan_office.getText().toString().length() == 0){
                    Toast.makeText(context,"버스번호 바코드를 스캔해주세요.",Toast.LENGTH_SHORT).show();
                }else if(scan_unit_barcodes.size() == 0){
                    Toast.makeText(context,"1개 이상의 바코드를 스캔해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("등록중..");
                    progressDialog.show();
                    new app_barcode_install().execute();
                }
            }
        });

        view_refresh = (Button)findViewById(R.id.view_refresh);
        view_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String barcode = result.getContents();
        if(click_type.equals("stop")){
        }else if(click_type.equals("unit")){
            progressDialog.setMessage("검색중...");
            progressDialog.show();
            make_unit_info(barcode);
        }else if(click_type.equals("scan")){
            progressDialog.setMessage("검색중...");
            progressDialog.show();
            new office_name_info().execute(barcode);
        }
    }
    private class app_barcode_install extends AsyncTask<String, Integer, Long>{
        @Override
        protected Long doInBackground(String... strings) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.test_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ERP_Spring_Controller erp = retrofit.create(ERP_Spring_Controller.class);
            String emp_id = pref.getString("emp_id",null);
            List<UnitList> result_list = new ArrayList<>();
            result_list = adapter.resultItem();

            List<String> scan_unit_barcode_list = new ArrayList<>();
            List<String> scan_eb_barcode_list = new ArrayList<>();

            for(int i = 0 ; i < result_list.size(); i++){
                scan_unit_barcode_list.add(result_list.get(i).getArea_code());
                if(result_list.get(i).getEb_barcode() == null){
                    scan_eb_barcode_list.add("");
                }else{
                    scan_eb_barcode_list.add(result_list.get(i).getEb_barcode());
                }
            }

            Call<String> call = erp.app_office_install(scan_unit_barcode_list,office_barcode,emp_id,scan_eb_barcode_list);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String result = response.body();

                    final AlertDialog.Builder a_builder = new AlertDialog.Builder(context);
                    a_builder.setTitle("처리 완료");
                    a_builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onResume();
                                }
                            });
                    if(result == null){
                        progressDialog.dismiss();
                        a_builder.setMessage("오류 발생 다시 시도 해주세요 .");
                        a_builder.show();
//                        Toast.makeText(context,"오류 발생 다시 시도 해주세요 .",Toast.LENGTH_LONG).show();
//                        fragment_reroad();
                    }
                    if(result.equals("true")){
                        progressDialog.dismiss();
                        a_builder.setMessage("단말기 등록 완료.");
                        a_builder.show();
//                        Toast.makeText(context,"단말기 등록 완료",Toast.LENGTH_LONG).show();
//                        fragment_reroad();
                    }else{
                        progressDialog.dismiss();
                        a_builder.setMessage("단말기 등록 실패");
                        a_builder.show();
//                        Toast.makeText(context,"단말기 등록 실패",Toast.LENGTH_LONG).show();
//                        fragment_reroad();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
            return null;
        }
    }

    private class Unist_List extends AsyncTask<String , Integer, Long>{
        @Override
        protected Long doInBackground(String... strings) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.test_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ERP_Spring_Controller erp = retrofit.create(ERP_Spring_Controller.class);
            final Call<List<UnitList>> call = erp.getUnisList();
            call.enqueue(new Callback<List<UnitList>>() {
                @Override
                public void onResponse(Call<List<UnitList>> call, Response<List<UnitList>> response) {
                    unit_list_all = response.body();
                }
                @Override
                public void onFailure(Call<List<UnitList>> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
            return null;
        }
    }

    void make_unit_info(String unit_barcode) {
        progressDialog.dismiss();
        if(unit_barcode == null){
            Toast.makeText(context,"바코드를 다시 스캔해주세요 . ",Toast.LENGTH_SHORT).show();
            return;
        }
        if(area_code == null){
            Toast.makeText(context,"영업소 바코드를 먼저 스캔 해주세요 . ",Toast.LENGTH_SHORT).show();
            return;
        }
        if(scan_unit_barcodes.contains(unit_barcode)){
            Toast.makeText(context,"이미 스캔하였습니다 . 확인해주세요 .",Toast.LENGTH_SHORT).show();
            return;
        }
        String unit_area_code = unit_barcode.substring(0,2);
        if(!area_code.equals(unit_area_code)){
            Toast.makeText(context,"지역바코드가 틀립니다. 확인해주세요 .",Toast.LENGTH_SHORT).show();
            return;
        }
        String unit_barocde_sbt = unit_barcode.substring(0,6);
        String area_name = "";
        String unit_name = "";
        for(int i = 0 ; i < unit_list_all.size(); i++){
            if(unit_list_all.get(i).getArea_code().equals(unit_barocde_sbt)){
                area_name = unit_list_all.get(i).getArea_name();
                unit_name = unit_list_all.get(i).getUnit_name();
                break;
            }
        }
        if(area_name.equals("") && unit_name.equals("")){
            Toast.makeText(context,"잘못된 바코드 입니다.",Toast.LENGTH_SHORT).show();
            return ;
        }
        scan_unit_barcodes.add(unit_barcode);
        listView.setAdapter(adapter);
        adapter.addItem(area_name,unit_name,unit_barcode,""+index_num);
        index_num++;
    }

    private class office_name_info extends AsyncTask<String , Integer , Long>{

        @Override
        protected Long doInBackground(String... strings) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.test_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ERP_Spring_Controller erp = retrofit.create(ERP_Spring_Controller.class);

            office_barcode = strings[0];

            final Call<List<String>> call = erp.get_office_name(office_barcode);
            call.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    List<String> result = response.body();
                    progressDialog.dismiss();
                    if(result.get(0) == null ){
                        scan_office.setText("");
                        local_office.setText("");
                        Toast.makeText(context,"잘못된 바코드 입니다.",Toast.LENGTH_SHORT).show();
                    }else{
                        scan_office.setText(result.get(0).toString());
                        String full_area_code = office_barcode.substring(0,3);
                        switch (full_area_code){
                            case "011" : local_office.setText("경기시내");
                            break;
                            case "012" : local_office.setText("경기마을");
                            break;
                            case "013" : local_office.setText("경기시외");
                            break;
                            case "021" : local_office.setText("인천시내");
                            break;
                            default : local_office.setText("지역");
                            break;
                        }
                        area_code = office_barcode.substring(0,2);
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
            return null;
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2 , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_btn :
                new AlertDialog.Builder(this)
                        .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent i = new Intent(Barcode_garage_input_Activity.this , LoginActivity.class );
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}