package com.example.vdiamant.testirdecoder;

import android.hardware.ConsumerIrManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class dvdController extends AppCompatActivity {

    private final static String onCMD =
            "0000 006D 0000 0022 00ac 00ac 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0015 0016 0015 0016 0040 0016 0040 0016 0015 0016 0015 0016 0040 0016 0015 0016 0040 0016 0040 0016 0015 0016 0015 0016 0040 0016 0040 0016 0015 0016 071c";
    private final static String offCMD =
            "0000 006D 0022 0000 00ad 00ad 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 0728";
    private ConsumerIrManager irManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        irManager =  (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
        setContentView(R.layout.activity_dvd_controller);
        findViewById(R.id.btnOn).setOnClickListener(new ClickListener(hex2ir(onCMD)));
        findViewById(R.id.btnOff).setOnClickListener(new ClickListener(hex2ir(offCMD)));
    }

    private class ClickListener implements View.OnClickListener {
        private final dvdController.IRCommand cmd;

        public ClickListener(final dvdController.IRCommand cmd) {
                    this.cmd = cmd;
        }

               @Override
       public void onClick(final View view) {
                        android.util.Log.d("Remote", "frequency: " + cmd.freq);
                        android.util.Log.d("Remote", "pattern: " + Arrays.toString(cmd.pattern));
                        irManager.transmit(cmd.freq, cmd.pattern);
                    }
        }

        private dvdController.IRCommand hex2ir(final String irData) {
            List<String> list = new ArrayList<String>(Arrays.asList(irData.split(" ")));
            list.remove(0); // dummy
            int frequency = Integer.parseInt(list.remove(0), 16); // frequency
            list.remove(0); // seq1
            list.remove(0); // seq2

            frequency = (int) (1000000 / (frequency * 0.241246));
            int pulses = 1000000 / frequency;
            int count;
            int[] pattern = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                count = Integer.parseInt(list.get(i), 16);
                pattern[i] = count * pulses;
            }

            return new dvdController.IRCommand(frequency, pattern);
            }

    private class IRCommand {
        private final int freq;
        private final int[] pattern;

        private IRCommand(int freq, int[] pattern) {
            this.freq = freq;
            this.pattern = pattern;
        }
    }
}
