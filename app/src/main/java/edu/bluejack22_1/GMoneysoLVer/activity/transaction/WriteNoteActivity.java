package edu.bluejack22_1.GMoneysoLVer.activity.transaction;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class WriteNoteActivity extends AppCompatActivity {

    EditText et_transaction_note;
    Button btn_save_transaction_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.note));

        et_transaction_note = findViewById(R.id.et_transaction_note);
        btn_save_transaction_note = findViewById(R.id.btn_save_transaction_note);

        Transaction transaction = (Transaction) getIntent().getSerializableExtra("currTransaction");

        btn_save_transaction_note.setOnClickListener(x -> {
            String note = et_transaction_note.getText().toString().trim();

            Intent intent = new Intent(WriteNoteActivity.this, AddTransactionActivity.class);
            intent.putExtra("transactionNote", note);
            intent.putExtra("currTransaction", transaction);
            startActivity(intent);
        });
    }
}