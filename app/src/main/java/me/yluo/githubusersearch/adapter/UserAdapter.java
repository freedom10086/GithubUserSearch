package me.yluo.githubusersearch.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yluo.githubusersearch.R;
import me.yluo.githubusersearch.api.ApiClient;
import me.yluo.githubusersearch.api.ApiResponse;
import me.yluo.githubusersearch.model.Repo;
import me.yluo.githubusersearch.model.User;
import me.yluo.githubusersearch.utils.Utils;

/**
 * Created by yang on 2016/9/23.
 * 用户列表adapter
 */

public class UserAdapter extends BaseAdapter{

    private List<User> datas;
    private Context context;

    public UserAdapter(Context context,List<User> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        //观察convertView随ListView滚动情况
        final MyViewHolder holder;
        if (convertView == null||convertView.getTag()==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, null);
            holder = new MyViewHolder();
            holder.user_img = (ImageView) convertView.findViewById(R.id.user_img);
            holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.user_likes = (TextView) convertView.findViewById(R.id.user_likes);
            convertView.setTag(holder);
        }else{
            holder = (MyViewHolder)convertView.getTag();
        }

        holder.setData(i);
        return convertView;
    }

    private class MyViewHolder{
        private ImageView user_img;
        private TextView user_name;
        private TextView user_likes;

        void setData(final int pos){
            final User u = datas.get(pos);
            user_name.setText(u.getUserName());

            Picasso.with(context)
                    .load(u.getUserIamge())
                    .placeholder(R.drawable.image_placeholder)
                    .resize(Utils.dip2px(context,40),Utils.dip2px(context,40))
                    .into(user_img);

            List<String> prolans = u.getProgramLang();
            if(prolans!=null){
                setLikesDatas(u);
            }else{
                //开始获得偏好编程语言
                user_likes.setText("loading...");
                ApiClient.instance().getRepos(u.getReposUrl(), new ApiResponse() {
                    @Override
                    public void onFailer(String s) {
                        Log.e("UserAdapter Repos",s);
                    }
                    @Override
                    public void onSuccess(List o) {
                        Map<String,Integer> lans = new HashMap<>();
                        for(Repo r:(List<Repo>)o){
                            if(r.getLanguage()!=null&&!r.getLanguage().equals("null")){
                                int i = 0;
                                if(lans.containsKey(r.getLanguage())){
                                    i = lans.get(r.getLanguage());
                                }
                                i++;
                                lans.put(r.getLanguage(),i);
                            }
                        }

                        //sort most like programm lang
                        List<Map.Entry<String,Integer>> mappingList = new ArrayList<>(lans.entrySet());
                        Collections.sort(mappingList, new Comparator<Map.Entry<String,Integer>>(){
                            public int compare(Map.Entry<String,Integer> mapping1,Map.Entry<String,Integer> mapping2){
                                return mapping2.getValue()-mapping1.getValue();
                            }
                        });
                        List<String> listlikes = new ArrayList<>();
                        for(Map.Entry<String,Integer> mapping:mappingList){
                            listlikes.add(mapping.getKey());
                        }
                        u.setProgramLang(listlikes);
                        setLikesDatas(u);
                    }
                });
            }
        }

        //设置偏好编程语言
        private void setLikesDatas(User u){
            List<String> prolans = u.getProgramLang();
            String s = "常用语言:";
            if(prolans.size()==0){
                s = "常用语言: 无";
            }
            int size = prolans.size()>3?3:prolans.size();
            s+= TextUtils.join(",",prolans.subList(0,size));
            user_likes.setText(s);
        }
    }
}
