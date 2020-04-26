package com.gomawa.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gomawa.R;

import java.util.List;

public class SettingRecyclerViewAdapter extends RecyclerView.Adapter<SettingRecyclerViewAdapter.SettingRecyclerViewHolder> {
    List<View.OnClickListener> onClickListenerList;

    // Constructor - On Click Listener 설정
    public SettingRecyclerViewAdapter(List<View.OnClickListener> onClickListenerList) {
        this.onClickListenerList = onClickListenerList;
    }

    // View Holder
    public class SettingRecyclerViewHolder extends RecyclerView.ViewHolder {
        // 레이아웃
        ConstraintLayout layout;

        // 아이콘
        ImageView imageView;
        TextView textView;

        public SettingRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.recyclerView_setting_layout);

            imageView = itemView.findViewById(R.id.recyclerView_setting_imageView);
            textView = itemView.findViewById(R.id.recyclerView_setting_textView);

            Log.d("itemView's position ", String.valueOf(getAdapterPosition()));

            // Item 에 OnClickListener 설정
            //itemView.setOnClickListener(onClickListenerList.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public SettingRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_setting, parent, false);

        SettingRecyclerViewHolder viewHolder = new SettingRecyclerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SettingRecyclerViewHolder holder, int position) {
        /**
         * 항목을 추가하려면
         * 1. FragmentSetting.class initView() 에 Listener 만들어서 onClickListenerList 에 add 하기
         * 2. 이 위치에서 drawables 와 texts 에 값 추가하기
         */

        int[] drawables = {
                R.drawable.user,        // pos 0: 프로필 사진 변경
                R.drawable.pen,         // pos 1: 닉네임 변경
                R.drawable.error,       // pos 2: 공지사항
                R.drawable.star,        // pos 3: 앱 평점 주기
                R.drawable.mail,        // pos 4: 문의하기
                R.drawable.logout,      // pos 5: 로그아웃
                R.drawable.logout       // pos 6: (임시) 회원 탈퇴
        };

        String[] texts = {
                "프로필 사진 변경",
                "닉네임 변경",
                "공지사항",
                "앱 평점 주기",
                "문의하기",
                "로그아웃",
                "회원 탈퇴"
        };

        holder.layout.setOnClickListener(onClickListenerList.get(position));

        holder.imageView.setImageResource(drawables[position]);
        holder.textView.setText(texts[position]);
    }

    @Override
    public int getItemCount() {
        return onClickListenerList.size();
    }
}
