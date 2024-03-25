package com.example.myapplication3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoutineSelectAdapter extends RecyclerView.Adapter<RoutineSelectAdapter.ViewHolder>{

    private ArrayList<String> localDataSet2;

    interface OnItemClickListener{
        void onChoiceClick(View v, int positon);//삭제
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }



    //===== 뷰홀더 클래스 =====================================================
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewRSelectRe;
        private Button buttonRSelectRe;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRSelectRe = itemView.findViewById(R.id.textViewRSelectRe);
            buttonRSelectRe = itemView.findViewById(R.id.buttonRSelectRe);

            buttonRSelectRe.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onChoiceClick(view,position);
                        }
                    }
                }
            });

        }
        public TextView getTextView() {
            return textViewRSelectRe;
        }


    }
    //========================================================================

    //----- 생성자 --------------------------------------
    // 생성자를 통해서 데이터를 전달받도록 함
    public RoutineSelectAdapter(ArrayList<String> dataSet) {
        localDataSet2 = dataSet;
    }
    //--------------------------------------------------

    @NonNull
    @Override   // ViewHolder 객체를 생성하여 리턴한다.
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_routineselect, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override   // ViewHolder안의 내용을 position에 해당되는 데이터로 교체한다.
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = localDataSet2.get(position);
        holder.textViewRSelectRe.setText(text);
    }

    @Override   // 전체 데이터의 갯수를 리턴한다.
    public int getItemCount() {
        return localDataSet2.size();
    }
}