package com.mambo.rafiki.ui.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.User;
import com.mambo.rafiki.databinding.FragmentAuthenticationBinding;
import com.mambo.rafiki.ui.views.LoadingDialog;
import com.mambo.rafiki.utils.ConstantsUtil;
import com.mambo.rafiki.utils.NetworkUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;

public class AuthenticationFragment extends Fragment {

    private static final int RC_SIGN_IN = 200;

    private FragmentAuthenticationBinding binding;
    private NavController navController;

    private GoogleSignInClient mGoogleSignInClient;
    private LoadingDialog loadingDialog;
    private FirebaseAuth mAuth;
    private GoogleSignInAccount account;
    private SharedPrefsUtil prefsUtil;

    public AuthenticationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_authentication, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        loadingDialog = LoadingDialog.getInstance();
        prefsUtil = SharedPrefsUtil.getInstance(requireActivity().getApplication());

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        binding.signInButton.setSize(SignInButton.SIZE_WIDE);
        binding.signInButton.setOnClickListener(v -> checkInternetConnectivity());
    }

    private void checkInternetConnectivity() {
        if (NetworkUtils.isConnected(requireContext())) {
            signIn();
        } else {
            openNetworkDialog();
        }
    }

    private void openNetworkDialog() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        builder.setCancelable(false)
                .setTitle("No Internet connection")
                .setMessage("Please check your connection again, or connect  to WiFi to continue with the authentication process")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("ok", (dialog, which) -> {
                    checkInternetConnectivity();
                })
                .show();

    }

    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase

                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Snackbar.make(binding.layoutAuthenticate, "Failed to sign in with Google.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        loadingDialog.showDialog(requireContext());

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        setUpUser(createUserDetails(firebaseUser.getUid()));

                        loadingDialog.hideDialog();

                    } else {
                        // If sign in fails, display a message to the user.
                        loadingDialog.hideDialog();
                        Snackbar.make(binding.layoutAuthenticate, "Authentication Failed. Please try again!", Snackbar.LENGTH_LONG).show();

                    }
                });
    }

    private void setUpUser(User user) {

        String json = new Gson().toJson(user);

        prefsUtil.storeUserData(user);
        prefsUtil.setUserIsLoggedIn();

        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtil.EXTRA, json);

        navController.navigate(R.id.action_authenticationFragment_to_setupFragment, bundle);
        navController.getGraph().setStartDestination(R.id.setupFragment);

    }

    private User createUserDetails(String uid) {

        User user = new User();

        if (account.getEmail().equals("mambobryan@gmail.com") || this.account.getEmail().equals("fourty400@gmail.com"))
            user.setAdmin(true);
        else
            user.setAdmin(false);

        user.setId(uid);
        user.setProfileUrl(this.account.getPhotoUrl().toString());
        user.setUsername(this.account.getDisplayName());

        return user;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (binding != null)
            binding = null;
    }

}