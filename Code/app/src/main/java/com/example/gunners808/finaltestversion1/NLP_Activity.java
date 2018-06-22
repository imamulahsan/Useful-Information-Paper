package com.example.gunners808.finaltestversion1;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class NLP_Activity extends AppCompatActivity {


    TextView test,text_name,name_output,text_location,location_output,text_organization,organization_output;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nlp_);

        text_name = findViewById(R.id.text_name);
        name_output = findViewById(R.id.name_output);
        text_location = findViewById(R.id.text_location);
        location_output = findViewById(R.id.location_output);
        text_organization = findViewById(R.id.text_organization);
        organization_output = findViewById(R.id.organization_output);
        test=findViewById(R.id.test_text);


        Intent intent = getIntent();
        String nlp_text = intent.getStringExtra(Intent.EXTRA_TEXT);

        nlp_text = nlp_text.replace(".","");
        nlp_text = nlp_text.replace(",","");
        nlp_text = nlp_text.replace("\n"," ");


        //Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
        //String tokens[] = tokenizer.tokenize(nlp_text);

        AssetManager assetManager = getAssets();
        InputStream in_name = null;
        InputStream in_loc = null;
        InputStream in_org = null;
        InputStream in_token = null;


        try
        {
            in_token = assetManager.open("en-token.bin");

            TokenizerModel tokenModel = new TokenizerModel(in_token);
            TokenizerME tokenizer = new TokenizerME(tokenModel);

            String tokens[] = tokenizer.tokenize(nlp_text);


            in_name = assetManager.open("en-ner-person.bin");
            in_loc = assetManager.open("en-ner-location.bin");
            in_org = assetManager.open("en-ner-organization.bin");

            if(in_name!=null && in_loc!=null && in_org!=null)
            {
                TokenNameFinderModel model_name = new TokenNameFinderModel(in_name);
                in_name.close();

                TokenNameFinderModel model_loc = new TokenNameFinderModel(in_loc);
                in_loc.close();

                TokenNameFinderModel model_org = new TokenNameFinderModel(in_org);
                in_org.close();

                NameFinderME nameFinder_name = new NameFinderME(model_name);
                NameFinderME nameFinder_loc = new NameFinderME(model_loc);
                NameFinderME nameFinder_org = new NameFinderME(model_org);

                Span nameSpans1[] = nameFinder_name.find(tokens);
                Span nameSpans2[] = nameFinder_loc.find(tokens);
                Span nameSpans3[] = nameFinder_org.find(tokens);

                for(Span s: nameSpans1){

                    // s.getStart() : contains the start index of possible name in the input string array
                    // s.getEnd() : contains the end index of the possible name in the input string array
                    for(int index=s.getStart();index<s.getEnd();index++){

                        name_output.setText(tokens[index]+" ");
                    }
                }

                for(Span s: nameSpans2){

                    // s.getStart() : contains the start index of possible name in the input string array
                    // s.getEnd() : contains the end index of the possible name in the input string array
                    for(int index=s.getStart();index<s.getEnd();index++){

                        location_output.setText(tokens[index]+" ");
                    }
                }

                for(Span s: nameSpans3){

                    // s.getStart() : contains the start index of possible name in the input string array
                    // s.getEnd() : contains the end index of the possible name in the input string array
                    for(int index=s.getStart();index<s.getEnd();index++){

                        organization_output.setText(tokens[index]+" ");
                    }
                }
            }
            else
            {
                Log.w("NLP", "ParserModel could not initialized.");

            }
        }
        catch (Exception ex)
        {
            Log.e("NLP", "message: " + ex.getMessage(), ex);
        }

        test.setText(nlp_text);


    }




}
