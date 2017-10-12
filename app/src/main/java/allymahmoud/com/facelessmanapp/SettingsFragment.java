package allymahmoud.com.facelessmanapp;

import java.io.*;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;



/**
 * Created by Allymahmoud on 10/11/17.
 */

public class SettingsFragment extends Fragment{
    private static final String TAG = "Settings";

    //declaring constant/non constant variables
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri ImageCaptureUri;
    private Uri SavedimageUri;
    private static final String PROFILE_IMAGE_URI_INSTANCE_KEY = "saved_image_uri";

    //declaring views on the page
    private Button buttonAccount;
    private ImageView profileImageView;
    private EditText etHandle;
    private EditText etFullName;
    private EditText etPassword;
    private  Button buttonSave;
    private Bitmap bitmap;
    public static String SHARED_PREF = "my_sharedpref";
    private String publicConfirmPassword = "";
    private View view;
    private Intent takePic;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment,container,false);

        super.onCreate(savedInstanceState);
        checkPermissions();

        //--->setContentView(R.layout.activity_main);

        //set up the EditTextFields, Buttons, and the image view
        etHandle = (EditText) view.findViewById(R.id.textHandle);
        etFullName = (EditText) view.findViewById(R.id.textFullName);
        etPassword = (EditText) view.findViewById(R.id.textPassword);
        buttonSave = (Button) view.findViewById(R.id.save);
        buttonAccount   = (Button) view.findViewById(R.id.account);
        profileImageView = (ImageView) view.findViewById(R.id.imageView);


        if (savedInstanceState != null) {
//            if (bitmap != null){
//
//                Log.d("bitmap in null", "yes");
            //ImageCaptureUri = savedInstanceState.getParcelable(PROFILE_IMAGE_URI_INSTANCE_KEY);
            Toast.makeText(view.getContext(), "loading image from internal storage", Toast.LENGTH_SHORT).show();
            SavedimageUri = savedInstanceState.getParcelable(PROFILE_IMAGE_URI_INSTANCE_KEY);
            profileImageView.setImageURI(SavedimageUri);
            loadImage();
        }


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(view.getContext(), "You clicked on ImageView", Toast.LENGTH_LONG);
                toast.show();

                //create an intent and send to camera
                takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                /*if (takePic.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePic, REQUEST_IMAGE_CAPTURE);
                }*/

                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                ImageCaptureUri = getActivity().getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                takePic.putExtra(MediaStore.EXTRA_OUTPUT, ImageCaptureUri);
                takePic.putExtra("return-data", true);

                try {
                    // Start a camera capturing activity
                    startActivityForResult(takePic, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


        //listen for clicks from already have an account button
        buttonAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonAccount.getText() == "I already have an account"){
                    Log.d("account", "I alredy have an account clicked");

                }
                else{

                    //clear all textfields
                    etHandle.setText("");
                    etFullName.setText("");
                    etPassword.setText("");
                    profileImageView.findViewById(R.id.imageView);
                    buttonAccount.setText("I already have an account");
                }
            }
        });

        //check if text has changed in any of the Edit Text fields
        etHandle.addTextChangedListener(filterTextWatcher);
        etFullName.addTextChangedListener(filterTextWatcher);
        etPassword.addTextChangedListener(filterTextWatcher);

        //check if the user has done entering the password
        etPassword.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId ==  EditorInfo.IME_ACTION_DONE) {
                    Log.d("passwordButton", "donetyping");
                    AlertDialog.Builder mbuilder = new AlertDialog.Builder(view.getContext());
                    View mview = getLayoutInflater().inflate(R.layout.dialog_confirm_password,null);
                    final EditText confirmPassword = (EditText) mview.findViewById(R.id.confirmPassword);
                    final Button Submit = (Button) mview.findViewById(R.id.submit);
                    final TextView matchIndicator = (TextView) mview.findViewById(R.id.passworMatchIndicator);
                    mbuilder.setView(mview);
                    final AlertDialog dialog = mbuilder.create();
                    dialog.show();

                    Submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //dismiss pop up window
                            if (Submit.getText() == "submit"){
                                Log.d("Alertdialog","clicked submit");

                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                            else{
                                Log.d("Alertdialog", "clicked cancel");
                                dialog.dismiss();
                            }
                        }

                    });
                    confirmPassword.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                            if (confirmPassword.getText().toString().equals(etPassword.getText().toString())){
                                matchIndicator.setText("✓ MATCH ");
                                matchIndicator.setBackgroundColor(Color.BLUE);
                                Submit.setText("submit");
                                publicConfirmPassword = confirmPassword.getText().toString();

                            }
                            else {
                                matchIndicator.setText("✖ DOESN'T MATCH");
                                matchIndicator.setBackgroundColor(Color.RED);
                                Submit.setText("cancel");
                            }
                        }
                    });
                    return true;
                }
                return false; // pass on to other listeners.
            }
        });


        //set a listener for the save button, if the user clicks this button, save the strings in the textedits
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(view.getContext(), "Saving", Toast.LENGTH_SHORT);
                toast.show();

                //check if the text fields have text
                if (etHandle.getText().toString().equals("") || etPassword.getText().toString().equals("") || etFullName.getText().toString().equals("")){
                    alertView("No text field can be empty");
                }
                else if (! etPassword.getText().toString().equals(publicConfirmPassword)){
                    alertView("Password field must macth");

                }
                else {


                    //setting up shared preferences for the textviews
                    SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF, 0);

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("charname", etHandle.getText().toString());
                    editor.commit();

                    SharedPreferences.Editor editor1 = sp.edit();
                    editor1.putString("fullname", etFullName.getText().toString());
                    editor1.commit();

                    SharedPreferences.Editor editor2 = sp.edit();
                    editor2.putString("password", etPassword.getText().toString());
                    editor2.commit();
                }

            }
        });



        return view;

    }


    /**
     * Code to check for runtime permissions.
     */
    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;

        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }




    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            buttonAccount.setText("clear");
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    };


    /*
   returns the result intent from a take photo intent and then calls thr cropping methods
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK ){

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (ImageCaptureUri != null){
                    Log.d("imageUri", "not null");
                    // Send image taken from camera for cropping
                    beginCrop(ImageCaptureUri);
                }
                else{
                    Toast.makeText(view.getContext(), "imageURi is empty", Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == Crop.REQUEST_CROP){
                Toast.makeText(view.getContext(), "Handle crop being called", Toast.LENGTH_SHORT).show();
                // Update image view after image crop
                handleCrop(resultCode, data);
            }
        }
    }
    /*
    helper functions to crop the image
     */
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity());
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == getActivity().RESULT_OK) {
            profileImageView.setImageURI(null);
            profileImageView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(view.getContext(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /*
    loadImage- loads a profile photo file name from internal storage, if no image exist it just has the default view
     */
    private void loadImage() {
        // Load profile photo from internal storage
        try {

            FileInputStream fis = getActivity().openFileInput(PROFILE_IMAGE_URI_INSTANCE_KEY);
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            profileImageView.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            if (SavedimageUri != null){
                Toast.makeText(view.getContext(), "image in internal storage", Toast.LENGTH_SHORT).show();
                profileImageView.setImageURI(SavedimageUri);

            }else{
                Toast.makeText(view.getContext(), "found image uri null", Toast.LENGTH_SHORT).show();
                // Default profile photo if no photo saved before.
                profileImageView.findViewById(R.id.imageView);
            }
        }
    }
    private void alertView( String message ) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());

        dialog.setTitle( "Error!" )
                .setIcon(R.drawable.defaultphoto)
                .setMessage(message)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                    }})
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }




}
