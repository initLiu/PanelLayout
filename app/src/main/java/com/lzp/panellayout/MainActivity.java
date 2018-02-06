package com.lzp.panellayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PanelLayout panelLayout = (PanelLayout) findViewById(R.id.panel);

        SparseArray<PanelLayout.PanelInfo> panelInfos = new SparseArray<>();
        panelInfos.append(0, new PanelLayout.PanelInfo(1, R.drawable.panel_camera, R.layout.panel_camera_layout, "camera"));
        panelInfos.append(1, new PanelLayout.PanelInfo(2, R.drawable.panel_emotion, R.layout.panel_emoj_layout, "emotion"));
        panelInfos.append(2, new PanelLayout.PanelInfo(3, R.drawable.panel_file, R.layout.panel_file_layout, "file"));
        panelInfos.append(3, new PanelLayout.PanelInfo(4, R.drawable.panel_image, R.layout.panel_photo_layout, "image"));
        panelInfos.append(4, new PanelLayout.PanelInfo(5, R.drawable.panel_voice, R.layout.panel_voice_layout, "voice"));
        panelInfos.append(5, new PanelLayout.PanelInfo(6, R.drawable.panel_video, R.layout.panel_voice_layout, "video"));
        panelInfos.append(6, new PanelLayout.PanelInfo(7, R.drawable.panel_plus, R.layout.panel_plus_layout, "plus"));
        panelLayout.addPanel(panelInfos);
    }
}
