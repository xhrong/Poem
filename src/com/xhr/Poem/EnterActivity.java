package com.xhr.Poem;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import com.xhr.Poem.util.Player;

import java.io.File;

/**
 * Created by xhrong on 2015/5/11.
 */
public class EnterActivity extends Activity {

    private Button btnPause, btnPlayUrl, btnStop;
    private SeekBar skbProgress;
    private Player player;
    private boolean isPaused = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.enter);

        this.setTitle("在线音乐播放---hellogv编写");

        btnPlayUrl = (Button) this.findViewById(R.id.btnPlayUrl);
        btnPlayUrl.setOnClickListener(new ClickEvent());

        btnPause = (Button) this.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new ClickEvent());

        btnStop = (Button) this.findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new ClickEvent());

        skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        player = new Player();

    }

    class ClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            if (arg0 == btnPause) {
                if (!isPaused) {
                    player.pause();
                    isPaused = true;
                    btnPause.setText("继续");
                }else{
                    player.play();
                    isPaused=false;
                    btnPause.setText("暂停");
                }

            } else if (arg0 == btnPlayUrl) {

                String fileName= Environment.getExternalStorageDirectory()+"/w201008120542248.mp3";
                File file=new File(fileName);
                if(file.exists()){
                    player.playUrl(fileName);
                }else{


                }


                //在百度MP3里随便搜索到的,大家可以试试别的链接
//                String url = "http://files.cnblogs.com/files/GrateSea/w201008120542248.rar";
//                player.playUrl(url);
            } else if (arg0 == btnStop) {
                player.stop();
            }
        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);
        }
    }
}
