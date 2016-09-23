package me.yluo.githubusersearch;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yluo.githubusersearch.adapter.UserAdapter;
import me.yluo.githubusersearch.api.ApiClient;
import me.yluo.githubusersearch.api.ApiResponse;
import me.yluo.githubusersearch.model.User;
import me.yluo.githubusersearch.utils.Utils;

/**
 * 说明：本demo用于搜索github相关用户，根据用户的查询关键字，能够实时显示出结果列表(最多只显示第一页30条)，以及编程语言喜好
 * 编程语言喜好来自统计user的repos各语言repos的数量
 *
 * 为了满足要求 ‘代码尽量整洁，不包含无用代码’，本demo尽量没有使用第三方库，只使用了图片异步加载库 ‘picasso’
 * 如果是功能相对完整的App，本应用的应用场景可以使用 gson retrofit okhttp 等相对成熟的库
 */
public class MainActivity extends Activity implements View.OnClickListener,
        EditText.OnEditorActionListener{

    private EditText input;
    private UserAdapter adapter;
    private List<User> datas = new ArrayList<>();
    private ProgressBar progressBar;
    private ImageView searchbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchbtn = (ImageView) findViewById(R.id.search_btn);
        searchbtn.setOnClickListener(this);
        ListView userlist = (ListView) findViewById(R.id.list_res);
        input = (EditText) findViewById(R.id.search_input);
        input.setOnEditorActionListener(this);
        adapter = new UserAdapter(this,datas);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        userlist.setAdapter(adapter);
    }


    //请求api获得数据
    private void searchBtnClick(){
        //check input
        String inputstr = input.getText().toString().trim();
        if(TextUtils.isEmpty(inputstr)){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        searchbtn.setVisibility(View.GONE);
        Utils.hide_ime(this);

        Map<String,String> params = new HashMap<>();
        params.put("q",inputstr);
        ApiClient.instance().getUsers(params, new ApiResponse() {
            @Override
            public void onFailer(String s) {
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
                finishGetData(null);
            }

            @Override
            public void onSuccess(List o) {
                finishGetData(o);
            }
        });
    }

    //完成获得数据
    private void finishGetData(List<User> datass){
        datas.clear();
        if(datass!=null)
            if(datass.size()==0){
                Toast.makeText(MainActivity.this,"无搜索结果",Toast.LENGTH_SHORT).show();
            }else{
                datas.addAll(datass);
            }

        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        searchbtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.search_btn){
            searchBtnClick();
        }
    }

    //软键盘 搜索按钮被点击事件
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchBtnClick();
            handled = true;
        }
        return handled;
    }
}
