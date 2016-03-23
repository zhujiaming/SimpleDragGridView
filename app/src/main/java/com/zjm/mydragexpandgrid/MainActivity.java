package com.zjm.mydragexpandgrid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.zjm.mydragexpandgrid.bean.DragChildData;
import com.zjm.mydragexpandgrid.bean.DragData;
import com.zjm.mydragexpandgrid.view.CustomContainerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomContainerView ccv = (CustomContainerView) findViewById(R.id.ccv);
        ccv.setDatas(getdatas());
        ccv.setOnCustomItemClickListener(new CustomContainerView.OnCustomItemClickListener() {
            @Override
            public void onAboveItemClick(int position) {
                Toast.makeText(MainActivity.this, getdatas().get(position).name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBottomItemClick(int abPosition, int position) {
                Toast.makeText(MainActivity.this, getdatas().get(abPosition).childDatas.get(position).name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<DragData> getdatas() {
        ArrayList<DragData> datas = new ArrayList<>();
        DragData dd = null;
        for (int i = 0; i < 13; i++) {
            dd = new DragData();
            dd.name = i + "" + i + "" + i;
            dd.imgId = i;
            datas.add(dd);
            if (i == 3) {
                dd.childDatas.addAll(getChildDatas("aa", 10));
                dd.name = "" + i + " more..";
            }
            if (i == 4) {
                dd.childDatas.addAll(getChildDatas("bb", 13));
                dd.name = "" + i + " more..";

            }
            if (i == 5) {
                dd.childDatas.addAll(getChildDatas("ii", 8));
                dd.name = "" + i + " more..";

            }
            if (i == 1) {
                dd.childDatas.addAll(getChildDatas("cc", 12));
                dd.name = "" + i + " more..";

            }
            if (i == 8) {
                dd.childDatas.addAll(getChildDatas("ee", 19));
                dd.name = "" + i + " more..";

            }
            if (i == 12) {
                dd.childDatas.addAll(getChildDatas("ff", 16));
                dd.name = "" + i + " more..";

            }
        }
        return datas;
    }

    public List<DragChildData> getChildDatas(String str, int ii) {
        ArrayList<DragChildData> datas = new ArrayList<>();
        DragChildData dd;
        for (int i = 0; i < ii; i++) {
            dd = new DragChildData();
            dd.name = str + "" + i + "" + i;
            dd.id = i + "";
            datas.add(dd);
        }
        return datas;
    }
}
