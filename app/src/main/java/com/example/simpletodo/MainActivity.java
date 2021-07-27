package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Define KEYS

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    //Create List Array

    List<String> items;

    //Define variables for buttons
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;

    //Make Items adapter accessible to other classes

    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set values to the variables
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);


        //Define list array
        //items = new ArrayList<>();
        loadItems();

        //Define Items/Then no longer need items when you create loadItems method
        /*
        items.add("Bring CDR to Mom");
        items.add("Bring Roomba Controller Back Home");
        items.add("Go to Gym");
        */

        //Construct our adapter
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
        @Override
            public void onItemLongClicked(int position) {
                //Delete Item from model
                items.remove(position);

                //Notify the adapter item removed
                itemsAdapter.notifyItemRemoved(position);
                //Toast to inform user item removed
            Toast.makeText(getApplicationContext(), "Item was Removed", Toast.LENGTH_SHORT).show();
            saveItems(); //Call save items method
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                //Good way to see that you are clicking on position
                Log.d("MainActivity", "Single click at position" +position);
                //Create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                //display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        //Pass in item, items and OnLongClickListener
        //We had this saved as final but then changed it to a normal variable deleting Final ItemsAdapter itemsAdapter
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //Add on click listener to button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                //Add new item to model
                items.add(todoItem);
                //notify Adapter we inserted item
                itemsAdapter.notifyItemInserted(items.size()-1);
                etItem.setText("");
                //Toast to inform user item was added
                Toast.makeText(getApplicationContext(), "Item was Added", Toast.LENGTH_SHORT).show();
                saveItems(); //Call method save items
            }
        });
    }

    //Handles the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //Retrieve updated text
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //Extract original position of edited item from key position
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //Update the model at the right position with the new item text
            items.set(position, itemText);
            //notify the adapter
            itemsAdapter.notifyItemChanged(position);
            //persist the changes
            saveItems();

            //TOAST
            Toast.makeText(getApplicationContext(), "Item Updated Successfully", Toast.LENGTH_SHORT).show();

    } else {
        Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    //This function will load items by reading lines from data.txt file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error Reading Items", e);
            items = new ArrayList<>();
        }
    }

    //This function saves items by writing into data.txt file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}