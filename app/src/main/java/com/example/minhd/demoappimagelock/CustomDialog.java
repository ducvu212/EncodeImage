package com.example.minhd.demoappimagelock;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by minhd on 17/08/23.
 */

public class CustomDialog extends Dialog implements View.OnClickListener {

    private Button ok, cancel;
    public static String name ;
    private EditText edtName ;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        ok = (Button) findViewById(R.id.btn_ok);
        cancel = (Button) findViewById(R.id.btn_cancel);
        edtName = findViewById(R.id.edt_name) ;
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                name = edtName.getText().toString() ;
                Log.d("Name", name);
                if (name != null) {
                    Folder folder = new Folder("0", name, 10);
                    Database database = new Database(getContext());
                    database.insertFolder(folder);
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

}
