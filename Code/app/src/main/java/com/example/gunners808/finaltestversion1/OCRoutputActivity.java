package com.example.gunners808.finaltestversion1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OCRoutputActivity extends AppCompatActivity {

    TextView textView;
    Button nlp_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocroutput);

        textView = findViewById(R.id.text_view2);
        nlp_button = findViewById(R.id.nlp_button);

        Intent intent = getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);

        textView.setText(text);

        final String nlp_input = textView.getText().toString();

        nlp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OCRoutputActivity.this, NLP_Activity.class);
                intent.putExtra(Intent.EXTRA_TEXT,nlp_input);
                startActivity(intent);
            }
        });
    }
}
