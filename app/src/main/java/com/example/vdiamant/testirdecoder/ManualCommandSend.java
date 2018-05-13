package com.example.vdiamant.testirdecoder;

import android.hardware.ConsumerIrManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManualCommandSend extends AppCompatActivity {

    private ConsumerIrManager irManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        irManager =  (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_command_send);
        findViewById(R.id.btnSendCommand).setOnClickListener(new ClickListener());
    }


    private class ClickListener implements View.OnClickListener {
        //private final ManualCommandSend.IRCommand cmd;

        public ClickListener() {
            //this.cmd = cmd;
        }

        @Override
        public void onClick(final View view) {
            TextView txtView = (TextView)findViewById(R.id.txtCommand);
            String cdmString = txtView.getText().toString();
            ManualCommandSend.IRCommand command = hex2ir(cdmString);
            android.util.Log.d("Remote", "frequency: " + command.freq);
            android.util.Log.d("Remote", "pattern: " + Arrays.toString(command.pattern));
            irManager.transmit(command.freq, command.pattern);
        }
    }

    private ManualCommandSend.IRCommand hex2ir(final String irData) {
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

        return new ManualCommandSend.IRCommand(frequency, pattern);
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
