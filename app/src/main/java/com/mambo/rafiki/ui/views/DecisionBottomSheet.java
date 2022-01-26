package com.mambo.rafiki.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.databinding.LayoutBottomSheetDecisionBinding;

public class DecisionBottomSheet extends BottomSheetDialogFragment {

    private LayoutBottomSheetDecisionBinding binding;
    private DecisionListener listener;

    public DecisionBottomSheet(DecisionListener listener) {
        //empty
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener((DialogInterface.OnShowListener) dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_bottom_sheet_decision, null, false);

        binding.edtDecision.requestFocus();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.edtDecision.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveDecision();
                    return true;
                }

                return false;
            }
        });

        binding.edtDecision.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSaveButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.checkBoxAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showDescriptionEditText();
            }
        });

        binding.btnSave.setOnClickListener(v -> saveDecision());

        showDescriptionEditText();
        setSaveButtonEnable();
        showKeyboard();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setSaveButtonEnable() {

        String decision = binding.edtDecision.getText().toString();

        if (!TextUtils.isEmpty(decision)) {
            binding.btnSave.setEnabled(true);
        } else {
            binding.btnSave.setEnabled(false);
        }

    }

    private void showDescriptionEditText() {

        boolean isPublic = binding.checkBoxAdd.isChecked();

        if (isPublic) {
            binding.inputLayoutDescription.setVisibility(View.VISIBLE);
        } else {
            binding.inputLayoutDescription.setVisibility(View.GONE);

            //checks if description is not null and clears
            if (binding.edtDescription.getText() != null)
                binding.edtDescription.getText().clear();
        }

    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void saveDecision() {

        String content = binding.edtDecision.getText().toString();
        String description = binding.edtDescription.getText().toString();

        boolean isPublic = binding.checkBoxAdd.isChecked();

        Decision decision = new Decision();

        if (!TextUtils.isEmpty(content)) {

            decision.setContent(content);

            if (isPublic) {
                decision.setPublic(binding.checkBoxAdd.isChecked());
                decision.setDescription(description);
            }

            listener.onDecisionSaved(decision);
            dismiss();

        } else {

            setSaveButtonEnable();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        closeKeyboard();
        if (binding != null)
            binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    public interface DecisionListener {
        void onDecisionSaved(Decision decision);
    }

}
