package com.todocompany.todocustomviews;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    EditText etSeconds, etDelay;
    TextView tvOnTick;
    Button btnRun;
    ChronometerView chronometerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSeconds = (EditText) findViewById(R.id.etSeconds);
        etDelay = (EditText) findViewById(R.id.etDelay);
        tvOnTick = (TextView) findViewById(R.id.tvOnTick);
        btnRun = (Button) findViewById(R.id.btnRun);
        chronometerView = (ChronometerView) findViewById(R.id.chronometerView);

        chronometerView.setChronometerCallback(new ChronometerView.ChronometerCallback() {
            @Override
            public void onDone() {
                Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                tvOnTick.setText("");
            }

            @Override
            public void onDelayTick(int delay) {
                tvOnTick.setText("" + delay);
            }
        });


        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etSeconds.getText().toString().equals("")){
                    chronometerView.setTimeRange(Integer.parseInt(etSeconds.getText().toString()));
                }
                if (!etDelay.getText().toString().equals("")){
                    chronometerView.setCallbacksDelay(Integer.parseInt(etDelay.getText().toString()));

                }

                chronometerView.start();
            }
        });


    }



}
