package com.example.myapplication3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChestActivity extends AppCompatActivity {

    SQLiteHelper sqLiteHelper;

    private int imageCounter = 0;  // 이미지 번호를 관리하기 위한 변수

    // 동적 생성 이미지에 대한 번호 저장 어레이
    private ArrayList<Integer> selectedChestImageIds = new ArrayList<>();
    // 최종 운동설정 어레이 (2차원)
    private ArrayList<ArrayList<String>> chestDetailsList = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest);

        ArrayList<ChestModel> chestModels = new ArrayList();
        RecyclerView recyclerView;
        Button chest_setting_Btn = findViewById(R.id.setting_chest);
        Button chest_back_Btn = findViewById(R.id.back_chest);

        sqLiteHelper = new SQLiteHelper(this);

        //데이터 모델리스트

        chestModels.add(new ChestModel("푸쉬업", "c_01"));
        chestModels.add(new ChestModel("벤치프레스", "c_02"));
        chestModels.add(new ChestModel("체스트프레스", "c_03"));
        chestModels.add(new ChestModel("덤벨체스트프레스", "c_04"));


        recyclerView = findViewById(R.id.chest_recyclerview);
        ChestAdapter adapter = new ChestAdapter(this, chestModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // 열생성
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapter.setOnItemClickListener(new ChestAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                ChestModel clickedItem = chestModels.get(position);
                String imagePath = clickedItem.getImage_path();

                // "X"를 표시할 TextView를 동적으로 생성
                TextView xTextView = new TextView(ChestActivity.this);
                xTextView.setText("X");
                // X색
                xTextView.setTextColor(Color.RED);
                // X의 크기 설정
                float textSizeInSp = 25;
                xTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp);


                // 동적으로 ImageView 생성
                ImageView imageView = new ImageView(ChestActivity.this);
                // 이미지에 고유한 번호를 부여
                int imageNumber = ++imageCounter;

                // ImageView의 크기를 설정
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.width = 120; // 원하는 가로 크기를 픽셀 단위로 설정
                layoutParams.height = 120; // 원하는 세로 크기를 픽셀 단위로 설정
                imageView.setLayoutParams(layoutParams);

                // 선택한 항목의 이미지 경로에 따라 이미지 리소스 설정
                int resId = getResources().getIdentifier(imagePath, "drawable", getPackageName());
                imageView.setImageResource(resId);

                // ImageView를 레이아웃에 추가
                LinearLayout imageLayout = findViewById(R.id.chest_image_layout); // 실제 레이아웃 ID로 대체
                imageLayout.addView(imageView);


                // TextView에 터치 이벤트 추가
                xTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TextView를 클릭했을 때의 동작을 추가
                        imageLayout.removeView(imageView);
                        imageLayout.removeView(xTextView);

                        // 이미지 넘버에 해당하는 어레이를 찾기
                        int index = selectedChestImageIds.indexOf(imageNumber);
                        if (index != -1) {
                            // 이미지 넘버에 해당하는 어레이 삭제
                            selectedChestImageIds.remove(index);
                            chestDetailsList.remove(index);
                        }

                    }
                });

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 이미지 클릭했을 때 동작
                        showExerciseDialog(clickedItem, imageNumber);


                    }
                });
                // TextView를 레이아웃에 추가
                imageLayout.addView(xTextView, layoutParams);


            }
        });


        //하단 버튼 누를시 동작 설정
        chest_setting_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sqLiteHelper.UpdateRoutineTemp(chestDetailsList);

                // Intent 객체 생성
                Intent intent = new Intent(ChestActivity.this, ExerciseActivity.class);

                intent.putExtra("chestDetailsList", chestDetailsList);

                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                // 인텐트를 사용하여 다른 클래스 시작
                startActivity(intent);

                finish();
            }
        });
        //취소 버튼 누를시 동작 설정
        chest_back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
    }
    private void showExerciseDialog(ChestModel chestModel, int imageNumber) {
        AlertDialog.Builder Chest_dlg = new AlertDialog.Builder(ChestActivity.this);
        Chest_dlg.setTitle("운동 설정");
        View dialogLayout = getLayoutInflater().inflate(R.layout.setting_dlg, null);
        Chest_dlg.setView(dialogLayout);

        // 다이얼로그 UI
        EditText weightEditText = dialogLayout.findViewById(R.id.dlg_set_weight);
        EditText countEditText = dialogLayout.findViewById(R.id.dlg_set_count);
        EditText workEditText = dialogLayout.findViewById(R.id.dlg_set_work);
        EditText breakEditText = dialogLayout.findViewById(R.id.dlg_set_break);
        EditText repeatEditText = dialogLayout.findViewById(R.id.dlg_set_repeat);

        // 0으로 디폴트값 설정
        weightEditText.setText("0");
        countEditText.setText("0");
        workEditText.setText("0");
        breakEditText.setText("0");
        repeatEditText.setText("0");

        //중량 +,- 버튼
        Button weightSub5 = dialogLayout.findViewById(R.id.weight_sub_5);
        Button weightSub2_5 = dialogLayout.findViewById(R.id.weight_sub_2_5);
        Button weightAdd2_5 = dialogLayout.findViewById(R.id.weight_add_2_5);
        Button weightAdd5 = dialogLayout.findViewById(R.id.weight_add_5);

        //세트수 +,-
        Button countSub_1 = dialogLayout.findViewById(R.id.count_sub_1);
        Button countAdd_1 = dialogLayout.findViewById(R.id.count_add_1);

        // 운동시간 +,-
        Button workSub30 = dialogLayout.findViewById(R.id.work_sub_30);
        Button workSub15 = dialogLayout.findViewById(R.id.work_sub_15);
        Button workAdd15 = dialogLayout.findViewById(R.id.work_add_15);
        Button workAdd30 = dialogLayout.findViewById(R.id.work_add_30);

        // 휴식시간 +,-
        Button breakSub30 = dialogLayout.findViewById(R.id.break_sub_30);
        Button breakSub15 = dialogLayout.findViewById(R.id.break_sub_15);
        Button breakAdd15 = dialogLayout.findViewById(R.id.break_add_15);
        Button breakAdd30 = dialogLayout.findViewById(R.id.break_add_30);

        // 반복횟수 +,-
        Button repeatSub_5 = dialogLayout.findViewById(R.id.repeat_sub_5);
        Button repeatSub_1 = dialogLayout.findViewById(R.id.repeat_sub_1);
        Button repeatAdd_1 = dialogLayout.findViewById(R.id.repeat_add_1);
        Button repeatAdd_5 = dialogLayout.findViewById(R.id.repeat_add_5);

        // 초기 상태에서 -버튼 비활성화
        weightSub2_5.setEnabled(Double.parseDouble(weightEditText.getText().toString()) > 0);
        weightSub5.setEnabled(Double.parseDouble(weightEditText.getText().toString()) > 0);
        countSub_1.setEnabled(Double.parseDouble(weightEditText.getText().toString()) > 0);
        workSub30.setEnabled(Double.parseDouble(weightEditText.getText().toString()) > 0);
        workSub15.setEnabled(Double.parseDouble(weightEditText.getText().toString()) > 0);
        breakSub30.setEnabled(Double.parseDouble(weightEditText.getText().toString()) > 0);
        breakSub15.setEnabled(Double.parseDouble(weightEditText.getText().toString()) > 0);
        repeatSub_5.setEnabled(Double.parseDouble(weightEditText.getText().toString()) > 0);
        repeatSub_1.setEnabled(Double.parseDouble(weightEditText.getText().toString()) > 0);


        // weightEditText의 값이 변경될 때마다 호출
        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // weightEditText의 값이 0이 아니면 버튼 활성화, 0이면 비활성화
                double currentWeight = Double.parseDouble(weightEditText.getText().toString());
                weightSub2_5.setEnabled(currentWeight > 0);
                weightSub5.setEnabled(currentWeight > 0);
            }
        });

        // countEditText의 값이 변경될 때마다 호출
        countEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // EditText의 값이 0이 아니면 버튼 활성화, 0이면 비활성화
                double currentCount = Double.parseDouble(countEditText.getText().toString());
                countSub_1.setEnabled(currentCount > 0);
            }
        });

        // workEditText의 값이 변경될 때마다 호출
        workEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // EditText의 값이 0이 아니면 버튼 활성화, 0이면 비활성화
                double currentWork = Double.parseDouble(workEditText.getText().toString());
                workSub30.setEnabled(currentWork > 0);
                workSub15.setEnabled(currentWork > 0);
            }
        });
        // breakEditText의 값이 변경될 때마다 호출
        breakEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // EditText의 값이 0이 아니면 버튼 활성화, 0이면 비활성화
                double currentBreak = Double.parseDouble(breakEditText.getText().toString());
                breakSub30.setEnabled(currentBreak > 0);
                breakSub15.setEnabled(currentBreak > 0);
            }
        });
        // repeatEditText의 값이 변경될 때마다 호출
        repeatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // EditText의 값이 0이 아니면 버튼 활성화, 0이면 비활성화
                double currentRepeat = Double.parseDouble(repeatEditText.getText().toString());
                repeatSub_1.setEnabled(currentRepeat > 0);
                repeatSub_5.setEnabled(currentRepeat > 0);
            }
        });


        // 중량 2.5감소
        weightSub2_5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                double currentWeight = 0.0;

                if (!weightEditText.getText().toString().isEmpty()) {
                    // Weight EditText의 값이 비어 있지 않으면
                    currentWeight = Double.parseDouble(weightEditText.getText().toString());
                }
                // 중량이 2.5 이상이면 2.5 감소
                if (currentWeight >= 2.5) {
                    double newWeight = currentWeight - 2.5;
                    weightEditText.setText(String.valueOf(newWeight));
                } else {
                    // 2.5 미만으로 내려가지 않도록 처리
                    weightEditText.setText("0");
                }

            }
        });


        // 중량 5감소
        weightSub5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double currentWeight = 0.0;

                if (!weightEditText.getText().toString().isEmpty()) {
                    // Weight EditText의 값이 비어 있지 않으면
                    currentWeight = Double.parseDouble(weightEditText.getText().toString());
                }
                // 중량이 5 이상이면 5 감소
                if (currentWeight >= 5) {
                    double newWeight = currentWeight - 5;
                    weightEditText.setText(String.valueOf(newWeight));
                } else {
                    // 5 미만으로 내려가지 않도록 처리
                    weightEditText.setText("0");
                }
            }
        });
        // 중량 2.5 증가가
        weightAdd2_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double currentWeight = 0.0;

                if (!weightEditText.getText().toString().isEmpty()) {
                    // Weight EditText의 값이 비어 있지 않으면
                    currentWeight = Double.parseDouble(weightEditText.getText().toString());
                }

                double newWeight = currentWeight + 2.5;
                weightEditText.setText(String.valueOf(newWeight));
            }
        });
        // 중량 5 증가
        weightAdd5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double currentWeight = 0.0;

                if (!weightEditText.getText().toString().isEmpty()) {
                    // Weight EditText의 값이 비어 있지 않으면
                    currentWeight = Double.parseDouble(weightEditText.getText().toString());
                }

                double newWeight = currentWeight + 5;
                weightEditText.setText(String.valueOf(newWeight));
            }
        });

        // 세트 수 1회 감소
        countSub_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentCount = 0;

                if (!countEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentCount = Integer.parseInt(countEditText.getText().toString());
                }

                if (currentCount >= 1) {
                    int newCount = currentCount - 1;
                    countEditText.setText(String.valueOf(newCount));
                } else {
                    countEditText.setText("0");
                }
            }
        });
        // 세트 수 1회 증가
        countAdd_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentCount = 0;

                if (!countEditText.getText().toString().isEmpty()) {
                    // Count EditText의 값이 비어 있지 않으면
                    currentCount = Integer.parseInt(countEditText.getText().toString());
                }

                int newCount = currentCount + 1;
                countEditText.setText(String.valueOf(newCount));
            }
        });

        // 운동시간 15초 감소
        workSub15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               int currentWork = 0;

                if (!workEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentWork = Integer.parseInt(workEditText.getText().toString());
                }
                if (currentWork >= 15) {
                    int newWork = currentWork - 15;
                    workEditText.setText(String.valueOf(newWork));
                } else {
                    workEditText.setText("0");
                }
            }
        });
        // 운동시간 30초 감소
        workSub30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentWork = 0;

                if (!workEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentWork = Integer.parseInt(workEditText.getText().toString());
                }

                if (currentWork >= 30) {
                    int newWork = currentWork - 30;
                    workEditText.setText(String.valueOf(newWork));
                } else {
                    workEditText.setText("0");
                }
            }
        });
        // 운동시간 15초 증가가
        workAdd15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentWork = 0;

                if (!workEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentWork = Integer.parseInt(workEditText.getText().toString());
                }

                int newWork = currentWork + 15;
                workEditText.setText(String.valueOf(newWork));
            }
        });
        // 운동시간 30초 증가
        workAdd30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentWork = 0;

                if (!workEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentWork = Integer.parseInt(workEditText.getText().toString());
                }

                int newWork = currentWork + 30;
                workEditText.setText(String.valueOf(newWork));
            }
        });

        // 휴식시간 15초 감소
        breakSub15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentBreak = 0;

                if (!breakEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentBreak = Integer.parseInt(breakEditText.getText().toString());
                }
                if (currentBreak >= 15) {
                    int newBreak = currentBreak - 15;
                    breakEditText.setText(String.valueOf(newBreak));
                } else {
                    breakEditText.setText("0");
                }
            }
        });
        // 휴식시간 30초 감소
        breakSub30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentBreak = 0;

                if (!breakEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentBreak = Integer.parseInt(breakEditText.getText().toString());
                }

                if (currentBreak >= 30) {
                    int newBreak = currentBreak - 30;
                    breakEditText.setText(String.valueOf(newBreak));
                } else {
                    breakEditText.setText("0");
                }
            }
        });
        // 휴식시작 15초 증가
        breakAdd15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentBreak = 0;

                if (!breakEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentBreak = Integer.parseInt(breakEditText.getText().toString());
                }

                int newBreak = currentBreak + 15;
                breakEditText.setText(String.valueOf(newBreak));
            }
        });
        // 휴식시작 30초 증가
        breakAdd30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentBreak = 0;

                if (!breakEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentBreak = Integer.parseInt(breakEditText.getText().toString());
                }

                int newBreak = currentBreak + 30;
                breakEditText.setText(String.valueOf(newBreak));
            }
        });

        // 반복 횟수 5회 감소
        repeatSub_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentRepeat = 0;

                if (!repeatEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentRepeat = Integer.parseInt(repeatEditText.getText().toString());
                }

                if (currentRepeat >= 5) {
                    int newRepeat = currentRepeat - 5;
                    repeatEditText.setText(String.valueOf(newRepeat));
                } else {
                    repeatEditText.setText("0");
                }
            }
        });
        // 반복 횟수 1회 감소
        repeatSub_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentRepeat = 0;

                if (!repeatEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentRepeat = Integer.parseInt(repeatEditText.getText().toString());
                }

                if (currentRepeat >= 1) {
                    int newRepeat = currentRepeat - 1;
                    repeatEditText.setText(String.valueOf(newRepeat));
                } else {
                    repeatEditText.setText("0");
                }
            }
        });
        // 반복 횟수 5회 증가
        repeatAdd_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentRepeat = 0;

                if (!repeatEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentRepeat = Integer.parseInt(repeatEditText.getText().toString());
                }

                int newCount = currentRepeat + 5;
                repeatEditText.setText(String.valueOf(newCount));
            }
        });
        // 반복 횟수 1회 증가
        repeatAdd_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentRepeat = 0;

                if (!repeatEditText.getText().toString().isEmpty()) {
                    // EditText의 값이 비어 있지 않으면
                    currentRepeat = Integer.parseInt(repeatEditText.getText().toString());
                }

                int newCount = currentRepeat + 1;
                repeatEditText.setText(String.valueOf(newCount));
            }
        });

        int index = selectedChestImageIds.indexOf(imageNumber);

        // 이미 입력된 값이 있으면 해당 값을 다이얼로그에 표시
        if (index != -1) {
            ArrayList<String> setChestList = chestDetailsList.get(index);
            weightEditText.setText(setChestList.get(2));  // 첫 번째 값(세트 수) 설정
            countEditText.setText(setChestList.get(3));   // 두 번째 값(작업 시간) 설정
            workEditText.setText(setChestList.get(4));  // 세 번째 값(휴식 시간) 설정
            breakEditText.setText(setChestList.get(5)); // 네 번째 값(반복 횟수) 설정
            repeatEditText.setText(setChestList.get(6)); // 다섯 번째 값(중량) 설정
        }


        Chest_dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = chestModel.getTitle();
                // chestModel 및 imageNumber를 활용하여 현재 클릭된 이미지에 대한 정보에 접근
                // 이미지 번호로 해당 이미지의 어레이를 찾기
                int index = selectedChestImageIds.indexOf(imageNumber);
                if (index != -1) {
                    // 기존 이미지의 어레이가 존재하면 수정
                    ArrayList<String> setChestList = chestDetailsList.get(index);

                    // 다이얼로그에서 입력된 값을 얻어와서 어레이를 수정
                    setChestList.set(2, weightEditText.getText().toString());    // 중량
                    setChestList.set(3, countEditText.getText().toString());     // 세트수
                    setChestList.set(4, workEditText.getText().toString());      // 운동 시간
                    setChestList.set(5, breakEditText.getText().toString());     // 휴식 시간
                    setChestList.set(6, repeatEditText.getText().toString());    // 반복 횟수
                } else {
                    // 이미지의 어레이가 존재하지 않으면 새로운 어레이를 추가
                    ArrayList<String> newSetChestList = new ArrayList<>();
                    newSetChestList.add("가슴");  // 부위
                    newSetChestList.add(title); // 운동이름
                    newSetChestList.add(weightEditText.getText().toString());    // 중량
                    newSetChestList.add(countEditText.getText().toString());     // 세트수
                    newSetChestList.add(workEditText.getText().toString());      // 운동 시간
                    newSetChestList.add(breakEditText.getText().toString());     // 휴식 시간
                    newSetChestList.add(repeatEditText.getText().toString());    // 반복 횟수


                    // 이미지 넘버에 해당하는 어레이를 추가
                    selectedChestImageIds.add(imageNumber);
                    chestDetailsList.add(newSetChestList);
                }

            }
        });

        Chest_dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Chest_dlg.show();
    }

}