package com.aadamsdev.communities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aadamsdev.communities.R;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private View view;

    private String email, password;

    private EditText usernameField,passwordField;

    private Button loginButton;

    private MainFragment chatFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.login_fragment, container, false);

        loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(LoginFragment.this);
//
//        usernameField = (EditText) view.findViewById(R.id.emailTextInputLayout);
//        passwordField = (EditText) view.findViewById(R.id.passwordTextInputLayout);
//
//        usernameField.setText("TestAccount");
//        passwordField.setText("1234!");
//
//        loginButton.setOnClickListener(this);

        return view;
    }



    //    private class LoginCaller extends AsyncTask<String, Void, Boolean> {
//
//        String email, password;
//        private ProgressDialog dialog;
//
//
//        public LoginCaller(String email, String password){
//
//            this.email = email;
//            this.password = password;
//        }
//
//        @Override
//        protected void onPreExecute(){
//
//            super.onPreExecute();
//            dialog = new ProgressDialog(getContext());
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setMessage("Logging in...");
//            dialog.show();
//        }
//
//        @Override
//        public Boolean doInBackground(String... params){
//
//            service = new ExchangeService();
//
//            service.setCredentials(new WebCredentials(email, password));
//
//            try {
//
//                service.setUrl(new URI("https://outlook.office365.com/EWS/Exchange.asmx"));
//
//                Folder inboxFolder = Folder.bind(service, WellKnownFolderName.Inbox);
//
//            } catch (HttpErrorException ex) {
//
//                return false;
//            } catch (Exception ex) {
//
//                Log.i("exchangeTaskFragment", ex.toString());
//                return false;
//            }
//
//            return true;
//        }
//
//        @Override
//        public void onPostExecute(Boolean result){
//
//            super.onPostExecute(result);
//
//            if (result && getActivity().getFragmentManager() != null){
//
//                getFragmentManager().popBackStack();
//
//                FragmentTransaction mainFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();;
//
//                MainFragment mainFragment = new MainFragment();
//                mainFragment.setService(service);
//
//                mainFragmentTransaction.add(R.id.mainFrameLayout, mainFragment);
//                mainFragmentTransaction.addToBackStack(null);
//                mainFragmentTransaction.commit();
//
//                Toast toast = Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT);
//                toast.show();
//
//                if (preferences.getString("email", null) == null && preferences.getString("password", null) == null){
//
//                    editor = preferences.edit();
//                    editor.putString("email", email);
//                    editor.putString("password", password);
//                    editor.commit();
//
//                    System.out.println("login saved in cache");
//                }
//
//            }
//
//            else if (!result){
//
//                Toast toast = Toast.makeText(getContext(), "Incorrect email address or password", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
//        }
//    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case (R.id.login_button):

                chatFragment = new MainFragment();

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.activity_main, chatFragment);
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                Toast.makeText(getContext(), "MainFragment", Toast.LENGTH_SHORT).show();

                break;
        }
    }

}